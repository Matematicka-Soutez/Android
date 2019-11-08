package cz.cuni.mff.maso.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import cz.cuni.mff.maso.R
import cz.cuni.mff.maso.api.*
import cz.cuni.mff.maso.databinding.ActivityLoginBinding
import cz.cuni.mff.maso.tools.Preferences
import cz.cuni.mff.maso.ui.BaseActivity
import cz.cuni.mff.maso.ui.settings.SettingsActivity

interface LoginView {
	fun onNextClicked()
}

class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel, LoginView>() {
	override val layoutResId: Int = R.layout.activity_login
	override val viewModel by lazy { initViewModel<LoginViewModel>() }
	override val view = object : LoginView {
		override fun onNextClicked() {
			if (viewModel.updateData()) {
				performLogin()
			}
		}
	}

	override fun displayBackArrow(): Boolean {
		return false
	}

	companion object {
		fun newIntent(context: Context) = Intent(context, LoginActivity::class.java)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		if (!displayBackArrow() && !Preferences.getUsername().isNullOrEmpty() && !Preferences.getPassword().isNullOrEmpty()) {
			startActivity(SettingsActivity.newIntent(this))
		}
		viewModel.request.observe(this, Observer {
			when (it.status) {
				Status.SUCCESS -> if (viewModel.state.value == LoginScreenState.PROGRESS) showSuccess(
					it.data
				)
				Status.ERROR -> if (viewModel.state.value == LoginScreenState.PROGRESS) showFail(it.message)
				Status.LOADING -> showProgress()
			}
		})
	}

	private fun showProgress() {
		viewModel.state.value = LoginScreenState.PROGRESS
	}

	private fun showFail(errorMessage: String?) {
		viewModel.state.value = LoginScreenState.ERROR
		errorMessage?.run {
			Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
		}
	}

	private fun showSuccess(responseBody: LoginResponseEntity?) {
		if (responseBody?.status?.equals(RequestStatusEnum.SUCCESS) == true) {
			Preferences.setUserId(viewModel.request.value?.data?.data?.userId!!)
			Preferences.setAuthToken(viewModel.request.value?.data?.data?.authToken!!)
			startActivity(SettingsActivity.newIntent(this@LoginActivity))
		} else {
			Snackbar.make(binding.root, R.string.error_generic, Snackbar.LENGTH_SHORT).show()
		}
	}

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

	private fun performLogin() {
		with(Preferences) {
			viewModel.callApiRequest(
				LoginRequestEntityWrapper(
					LoginRequestEntity(
						getUsername()!!,
						getPassword()!!
					)
				)
			)
		}
	}
}
