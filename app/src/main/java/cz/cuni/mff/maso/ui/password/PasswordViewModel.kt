package cz.cuni.mff.maso.ui.password

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import cz.cuni.mff.maso.tools.Preferences
import cz.cuni.mff.maso.ui.BaseViewModel

private const val PASSWORD_MIN_LENGTH = 8
private const val GAME_CODE_MIN_LENGTH = 6

class PasswordViewModel : BaseViewModel() {

	val password = MutableLiveData<String>()
	val gameCode = MutableLiveData<String>()
	val isPasswordValid: MutableLiveData<Boolean> = Transformations.map(password) { password.value?.length ?: 0 >= PASSWORD_MIN_LENGTH } as MutableLiveData<Boolean>
	val isGameCodeValid: MutableLiveData<Boolean> = Transformations.map(gameCode) { gameCode.value?.length ?: 0 >= GAME_CODE_MIN_LENGTH } as MutableLiveData<Boolean>

	fun updateData(): Boolean {
		password.value?.let {
			Preferences.setPassword(it)
		} ?: return false
		gameCode.value?.let {
			Preferences.setGameCode(it)
		} ?: return false
		return true
	}
}