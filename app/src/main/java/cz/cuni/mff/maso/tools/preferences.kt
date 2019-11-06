package cz.cuni.mff.maso.tools

import android.preference.PreferenceManager
import cz.cuni.mff.maso.App
import cz.cuni.mff.maso.api.LoginRequestEntity

const val ENCRYPTION_KEY = "5_-mN%HkjCt{C!CS15%Ao\$2_{)GXB\$aqA(BkCUHDyq#Oht[--O,]T}QzS)[y7@H?"
const val ENCRYPTION_SALT = "HUohnASx:N|mX0DS@EL;-fMK>tyjiTpGfOh@X*9X{~b;O9/p%!`eTDGAY-+~rNpL"
val ENCRYPTION_IV = ByteArray(16)
private const val PREF_AUTH_TOKEN = "pref_auth_token"
private const val PREF_USER_ID = "pref_user_id"
private const val PREF_USERNAME = "pref_username"
private const val PREF_PASSWORD = "pref_password"
private const val PREF_GAME_CODE = "pref_game_code"
private const val PREF_USE_PRINTER = "pref_use_printer"

object Preferences {

	private val encryption = Encryption.getDefault(ENCRYPTION_KEY, ENCRYPTION_SALT, ENCRYPTION_IV)
	private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.instance)

	fun clearPreferences() {
		sharedPreferences.edit().clear().apply()
	}

	fun setAuthToken(value: String) {
		sharedPreferences.edit().putString(PREF_AUTH_TOKEN, encryption?.encryptOrNull(value)).apply()
	}

	fun getAuthToken(): String? {
		return encryption?.decryptOrNull(sharedPreferences.getString(PREF_AUTH_TOKEN, null))
	}

	fun setUserId(value: String) {
		sharedPreferences.edit().putString(PREF_USER_ID, encryption?.encryptOrNull(value)).apply()
	}

	fun getUserId(): String? {
		return encryption?.decryptOrNull(sharedPreferences.getString(PREF_USER_ID, null))
	}

	fun setUsername(value: String) {
		sharedPreferences.edit().putString(PREF_USERNAME, encryption?.encryptOrNull(value)).apply()
	}

	fun getUsername(): String? {
		return encryption?.decryptOrNull(sharedPreferences.getString(PREF_USERNAME, null))
	}

	fun setPassword(value: String) {
		sharedPreferences.edit().putString(PREF_PASSWORD, encryption?.encryptOrNull(value)).apply()
	}

	fun getPassword(): String? {
		return encryption?.decryptOrNull(sharedPreferences.getString(PREF_PASSWORD, null))
	}

	fun getLoginRequestEntity(): LoginRequestEntity? {
		return LoginRequestEntity(this.getUsername()!!, this.getPassword()!!)
	}

	fun setGameCode(value: String) {
		sharedPreferences.edit().putString(PREF_GAME_CODE, encryption?.encryptOrNull(value)).apply()
	}

	fun getGameCode(): String? {
		return encryption?.decryptOrNull(sharedPreferences.getString(PREF_GAME_CODE, null))
	}

	fun setUsePrinter(value: Boolean) {
		sharedPreferences.edit().putBoolean(PREF_USE_PRINTER, value).apply()
	}

	fun getUsePrinter(): Boolean? {
		return sharedPreferences.getBoolean(PREF_USE_PRINTER, false)
	}

}