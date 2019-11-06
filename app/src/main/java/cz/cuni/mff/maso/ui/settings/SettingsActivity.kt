package cz.cuni.mff.maso.ui.settings

import android.content.Context
import android.content.Intent
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

class SettingsActivity : BaseActivity<ActivitySettingsBinding, SettingsViewModel, SettingsView>() {
	override val layoutResId: Int = R.layout.activity_settings
	override val viewModel by lazy { initViewModel<SettingsViewModel>() }
	override val view = object : SettingsView {
		override fun onNextClicked() {
			if (viewModel.updateData()) {
				startActivity(QrScanActivity.newIntent(this@SettingsActivity))
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
}
