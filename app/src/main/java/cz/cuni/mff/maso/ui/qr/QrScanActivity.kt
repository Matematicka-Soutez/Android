package cz.cuni.mff.maso.ui.qr

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.budiyev.android.codescanner.*
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.BarcodeFormat
import cz.cuni.mff.maso.R
import cz.cuni.mff.maso.api.ErrorType
import cz.cuni.mff.maso.api.RequestTypeEnum
import cz.cuni.mff.maso.api.Status
import cz.cuni.mff.maso.databinding.ActivityQrScanBinding
import cz.cuni.mff.maso.tools.Preferences
import cz.cuni.mff.maso.ui.BaseActivity
import cz.cuni.mff.maso.ui.login.LoginActivity
import cz.cuni.mff.maso.ui.settings.SettingsActivity

private const val PERMISSION_CAMERA_CODE = 69

interface QrScanView {
	fun cancelOverlay()
	fun actionFail()
	fun cameraPermissionClicked()
	fun enterManually()
}

class QrScanActivity : BaseActivity<ActivityQrScanBinding, QrScanViewModel, QrScanView>(), ManualFillInDialogListener {
	private var snackBar: Snackbar? = null

	override val layoutResId = R.layout.activity_qr_scan

	override val viewModel by lazy { initViewModel<QrScanViewModel>() }

	override val view = object : QrScanView {
		override fun enterManually() {
			stopScanning()
			binding.spinnerSelector.visibility = View.GONE
			ManualFillInDialogFragment.newInstance().apply {
				(object : DialogInterface {
					override fun dismiss() {
						startScanning()
					}

					override fun cancel() {
						startScanning()
					}
				})
			}.show(supportFragmentManager, ManualFillInDialogFragment.TAG)
		}

		override fun cameraPermissionClicked() {
			requestCameraPermission()
		}

		override fun cancelOverlay() {
			viewModel.state.value = QrScreenState.SCANNING
		}

		override fun actionFail() {
			viewModel.state.value = QrScreenState.SCANNING
			if (viewModel.request.value?.errorType == ErrorType.UNAUTHORIZED) {
				Preferences.clearPreferences()
				startLoginActivity()
			} else {
				viewModel.retry()
			}
		}
	}

	companion object {
		fun newIntent(context: Context) = Intent(context, QrScanActivity::class.java).apply {
			flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
		}
	}

	private lateinit var codeScanner: CodeScanner
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setupScanning()
		viewModel.request.observe(this, Observer {
			when (it.status) {
				Status.SUCCESS -> if (viewModel.state.value == QrScreenState.PROGRESS) showSuccess()
				Status.ERROR -> if (viewModel.state.value == QrScreenState.PROGRESS) showFail()
				Status.LOADING -> showProgress()
			}
		})
		viewModel.state.observe(this, Observer {
			binding.progressContainer.visibility = if (it == QrScreenState.PROGRESS) View.VISIBLE else View.GONE
			binding.successContainer.visibility = if (it == QrScreenState.SUCCESS) View.VISIBLE else View.GONE
			binding.failContainer.visibility = if (it == QrScreenState.ERROR) View.VISIBLE else View.GONE
			binding.permissionContainer.visibility = if (it == QrScreenState.PERMISSION_REQUIRED) View.VISIBLE else View.GONE
			if (it == QrScreenState.SCANNING) {
				viewModel.cancelDelayTimer()
				startScanning()
			} else {
				stopScanning()
			}
		})
		initSpinner()
	}

	override fun onDataEntered(teamNo: Int, problemNo: Int, requestType: RequestTypeEnum) {
		viewModel.sendRequest(teamNo, problemNo, requestType)
	}

	override fun onDismissed() {
		if (viewModel.state.value == QrScreenState.SCANNING) {
			startScanning()
		}
		binding.spinnerSelector.visibility = View.VISIBLE
	}

	private fun initSpinner() {
		val options = resources.getStringArray(R.array.request_options)
		val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
		binding.spinnerSelector.adapter = adapter
		binding.spinnerSelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View?,
                position: Int,
                l: Long
            ) {
                viewModel.requestType = when (position) {
					0 -> RequestTypeEnum.SOLVE
					1 -> RequestTypeEnum.EXCHANGE
					else -> RequestTypeEnum.CANCEL
				}
			}

			override fun onNothingSelected(adapterView: AdapterView<*>) {}
		}
	}

	private fun showProgress() {
		viewModel.state.value = QrScreenState.PROGRESS
	}

	private fun showFail() {
		viewModel.state.value = QrScreenState.ERROR
	}

	private fun showSuccess() {
		viewModel.state.value = QrScreenState.SUCCESS
		viewModel.runDelayTimer()
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.menu_privacy_policy, menu)
		menuInflater.inflate(R.menu.menu_settings, menu)
		menuInflater.inflate(R.menu.menu_logout, menu)
		return super.onCreateOptionsMenu(menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.action_privacy_policy -> {
				startPrivacyPolicyActivity()
				return true
			}
			R.id.action_settings -> {
				startSettingsActivity()
				return true
			}
			R.id.action_logout -> {
				Preferences.clearPreferences()
				startLoginActivity()
				return true
			}
		}
		return super.onOptionsItemSelected(item)
	}

	private fun startLoginActivity() {
		startActivity(LoginActivity.newIntent(this))
	}

	private fun startSettingsActivity() {
		startActivity(SettingsActivity.newIntent(this, true))
	}

	override fun onStart() {
		super.onStart()
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
			requestCameraPermission()
		} else {
			if (viewModel.state.value == QrScreenState.PERMISSION_REQUIRED) {
				viewModel.state.value = QrScreenState.SCANNING
			} else if (viewModel.state.value == QrScreenState.SCANNING) {
				startScanning()
			}
		}
	}

	private fun requestCameraPermission() {
		viewModel.state.value = QrScreenState.SCANNING
		ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), PERMISSION_CAMERA_CODE)
	}

	override fun onStop() {
		stopScanning()
		super.onStop()
	}

	override fun onRequestPermissionsResult(requestCode: Int,
		permissions: Array<String>, grantResults: IntArray) {
		when (requestCode) {
			PERMISSION_CAMERA_CODE -> {
				if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
					viewModel.state.value = QrScreenState.SCANNING
				} else {
					viewModel.state.value = QrScreenState.PERMISSION_REQUIRED
					snackBar = Snackbar.make(binding.root, R.string.error_camera_permission_denied, Snackbar.LENGTH_INDEFINITE)
						.setAction(R.string.action_settings) {
							openAppSettings()
						}
						.setActionTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
					snackBar?.show()
				}
				return
			}

			// Add other 'when' lines to check for other
			// permissions this app might request.
			else -> {
				// Ignore all other requests.
			}
		}
	}

	private fun startScanning() {
		codeScanner.startPreview()
		snackBar?.dismiss()
	}

	private fun stopScanning() {
		codeScanner.releaseResources()
		codeScanner.stopPreview()
	}

	private fun setupScanning() {
		codeScanner = CodeScanner(this, binding.scannerView)

		// Parameters (default values)
		codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
		codeScanner.formats = listOf(BarcodeFormat.QR_CODE) // list of type BarcodeFormat,

		codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
		codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
		codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
		codeScanner.isFlashEnabled = false // Whether to enable flash or not

		// Callbacks
		codeScanner.decodeCallback = DecodeCallback {
			if (!viewModel.processQrCodeResult(it.text)) {
				runOnUiThread {
					Snackbar.make(binding.root, R.string.error_qr_code_format_not_valid, Snackbar.LENGTH_LONG).show()
					binding.scannerView.postDelayed({
						codeScanner.startPreview()
					}, 350)
				}
			}
		}
		codeScanner.errorCallback = ErrorCallback {
			runOnUiThread {

			}
		}
	}

	private fun openAppSettings() {
		val intent = Intent()
		intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
		val uri = Uri.fromParts("package", packageName, null)
		intent.data = uri
		startActivity(intent)
	}

}