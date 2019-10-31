package cz.cuni.mff.maso.ui.login

import android.content.Context
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import cz.cuni.mff.maso.R
import cz.cuni.mff.maso.databinding.ActivityLoginBinding
import cz.cuni.mff.maso.ui.BaseActivity
import cz.cuni.mff.maso.ui.zip.ZipActivity

interface LoginView {
	fun onNextClicked()
}

private const val ARG_CHANGE_PASSWORD = "arg_change_password"

class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel, LoginView>() {
	override val layoutResId: Int = R.layout.activity_login
	override val viewModel by lazy { initViewModel<LoginViewModel>() }
	override val view = object : LoginView {
		override fun onNextClicked() {
			if (viewModel.updateData()) {
				startActivity(ZipActivity.newIntent(this@LoginActivity))
//				startActivity(SettingsActivity.newIntent(this@LoginActivity))
			}
		}
	}

	override fun displayBackArrow(): Boolean {
		return (intent.extras?.getByte(ARG_CHANGE_PASSWORD, 0.toByte()) ?: 0.toByte()) != 0.toByte()
	}

	companion object {
		fun newIntent(context: Context, changePassword: Boolean = false) = Intent(context, LoginActivity::class.java).apply {
			putExtra(ARG_CHANGE_PASSWORD, (if (changePassword) 1 else 0).toByte())
		}
	}

//	override fun onCreate(savedInstanceState: Bundle?) {
//		super.onCreate(savedInstanceState)
//		if (!displayBackArrow() && !Preferences.getUsername().isNullOrEmpty() && !Preferences.getPassword().isNullOrEmpty()) {
//			startActivity(SettingsActivity.newIntent(this))
//		}
//	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.menu_privacy_policy, menu)
		return super.onCreateOptionsMenu(menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.action_privacy_policy -> {
				startPrivacyPolicyActivity()
				return true
			}
		}
		return super.onOptionsItemSelected(item)
	}
}
