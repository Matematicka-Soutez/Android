package cz.cuni.mff.maso.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import cz.cuni.mff.maso.tools.Preferences
import cz.cuni.mff.maso.ui.BaseViewModel

private const val PASSWORD_MIN_LENGTH = 8
private const val USERNAME_MIN_LENGTH = 3

class LoginViewModel : BaseViewModel() {

	val username = MutableLiveData<String>()
	val password = MutableLiveData<String>()
	val isPasswordValid: MutableLiveData<Boolean> = Transformations.map(password) { password.value?.length ?: 0 >= PASSWORD_MIN_LENGTH } as MutableLiveData<Boolean>
	val isUsernameValid: MutableLiveData<Boolean> = Transformations.map(username) { username.value?.length ?: 0 >= USERNAME_MIN_LENGTH } as MutableLiveData<Boolean>

	fun updateData(): Boolean {
		username.value?.let {
			Preferences.setUsername(it)
		} ?: return false
		password.value?.let {
			Preferences.setPassword(it)
		} ?: return false
		return true
	}
}