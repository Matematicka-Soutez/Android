package cz.cuni.mff.maso.ui.settings

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import cz.cuni.mff.maso.R
import cz.cuni.mff.maso.databinding.ActivitySettingsBinding
import cz.cuni.mff.maso.tools.Preferences
import cz.cuni.mff.maso.ui.BaseActivity
import cz.cuni.mff.maso.ui.login.LoginActivity
import cz.cuni.mff.maso.ui.qr.QrScanActivity

interface SettingsView {
	fun onNextClicked()
}

private const val ARG_CHANGE_SETTINGS = "arg_change_settings"
private const val REQUEST_ENABLE_BT = 11
private val BA = BluetoothAdapter.getDefaultAdapter()

class SettingsActivity : BaseActivity<ActivitySettingsBinding, SettingsViewModel, SettingsView>() {
	override val layoutResId: Int = R.layout.activity_settings
	override val viewModel by lazy { initViewModel<SettingsViewModel>() }
	override val view = object : SettingsView {
		override fun onNextClicked() {
			if (viewModel.updateData()) {

				if (viewModel.usePrinter.value != null && viewModel.usePrinter.value!!) {
					if (!BA.isEnabled) {
						val turnOn = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
						startActivityForResult(turnOn, REQUEST_ENABLE_BT)
					} else {
						findDevices()
					}

				} else {
					startQrScanActivity()
				}
			}
		}
	}

	override fun displayBackArrow(): Boolean {
		return (intent.extras?.getByte(ARG_CHANGE_SETTINGS, 0.toByte()) ?: 0.toByte()) != 0.toByte()
	}

	companion object {
		fun newIntent(context: Context, changeSettings: Boolean = false) = Intent(context, SettingsActivity::class.java).apply {
			putExtra(ARG_CHANGE_SETTINGS, (if (changeSettings) 1 else 0).toByte())
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		if (displayBackArrow()) {
			viewModel.gameCode.setValue(Preferences.getGameCode())
		}
		if (!displayBackArrow() && !Preferences.getGameCode().isNullOrEmpty()) {
			startActivity(QrScanActivity.newIntent(this))
		}
		val filter = IntentFilter()
		filter.addAction(BluetoothDevice.ACTION_FOUND)
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
		registerReceiver(receiver, filter)
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.menu_privacy_policy, menu)
		menuInflater.inflate(R.menu.menu_logout, menu)
		return super.onCreateOptionsMenu(menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.action_privacy_policy -> {
				startPrivacyPolicyActivity()
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

	private fun startQrScanActivity() {
		startActivity(QrScanActivity.newIntent(this@SettingsActivity))
	}

	private fun findDevices() {
		if (BA.isDiscovering) {
			BA.cancelDiscovery()
		}
		BA.startDiscovery()
		println("A")
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (requestCode == REQUEST_ENABLE_BT) {
			findDevices()
		}
	}

	// Create a BroadcastReceiver for ACTION_FOUND.
	private val receiver = object : BroadcastReceiver() {

		override fun onReceive(context: Context, intent: Intent) {
			val action: String = intent.action.toString()
			println(action)
			when(action) {
				BluetoothDevice.ACTION_FOUND -> {
					// Discovery has found a device. Get the BluetoothDevice
					// object and its info from the Intent.
					val device: BluetoothDevice =
						intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
					val deviceName = device.name
					val deviceHardwareAddress = device.address // MAC address
					if (deviceName.startsWith("Maso ")) {
						//device.setPin(362940)
						device.createBond()
					}
				}
				BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
					println("Started")
				}
				BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
					println("Ended")
				}
			}
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		unregisterReceiver(receiver)
	}
}
