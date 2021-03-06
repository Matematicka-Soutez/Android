package cz.cuni.mff.maso.ui.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import cz.cuni.mff.maso.tools.Preferences
import cz.cuni.mff.maso.ui.BaseViewModel

private const val GAME_CODE_MIN_LENGTH = 4
private const val GAME_CODE_MAX_LENGTH = 16

class SettingsViewModel : BaseViewModel() {

	val usePrinter = MutableLiveData<Boolean>()
	val gameCode = MutableLiveData<String>()
	val isGameCodeValid: MutableLiveData<Boolean> = Transformations.map(gameCode) { gameCode.value?.length ?: 0 in GAME_CODE_MIN_LENGTH..GAME_CODE_MAX_LENGTH } as MutableLiveData<Boolean>

	fun updateData(): Boolean {
		Preferences.setUsePrinter(usePrinter.value == true)
		gameCode.value?.let {
			Preferences.setGameCode(it)
		} ?: return false
		return true
	}
}