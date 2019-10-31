package cz.cuni.mff.maso.ui.qr

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import cz.cuni.mff.maso.api.*
import cz.cuni.mff.maso.tools.Preferences
import cz.cuni.mff.maso.ui.BaseViewModel
import java.util.regex.Pattern

private const val HIDE_SUCCESS_DELAY = 2000L

class QrScanViewModel : BaseViewModel() {

	val state = MutableLiveData<QrScreenState>().apply { value = QrScreenState.SCANNING }

	private var delayHandler: Handler? = null
	private var delayRunnable: Runnable? = null

	private val patternAll = Pattern.compile("T\\d+P\\d+")
	private val patternTeam = Pattern.compile("T(\\d+)")
	private val patternTask = Pattern.compile("P(\\d+)")
	private val requestEntity = MutableLiveData<QrRequestEntityWrapper?>()
	var requestType = RequestTypeEnum.ADD
	val request: LiveData<Resource<QrResponseEntity>> = Transformations.switchMap(requestEntity) { it ->
		it?.let {
			RetrofitHelper.createRequest(RetrofitHelper.instance.create(MasoRequest::class.java).sendQrCode(it.gameCode, it.teamNumber, it.authToken, it.userId, it.requestEntity))
		}
	}

	fun processQrCodeResult(text: String?): Boolean {
		val qrCodeEntity = extractDataFromQrCode(text)
		qrCodeEntity?.let {
			sendRequest(it.teamNumber, it.taskNumber, requestType)
			return true
		}
		return false
	}

	fun retry() {
		val tempRequestEntity = requestEntity.value?.copy()
		requestEntity.value = null
		requestEntity.value = tempRequestEntity
	}

	private fun callApiRequest(requestBody: QrRequestEntityWrapper) {
		requestEntity.postValue(requestBody)
	}

	private fun extractDataFromQrCode(text: String?): QrCodeEntity? {
		text?.let {
			//if the whole code doesn't match the required format, return null
			if (!patternAll.matcher(text).find()) {
				return null
			}
			var teamNumber: Int? = null
			var taskNumber: Int? = null
			val teamMatcher = patternTeam.matcher(text)
			val problemMatcher = patternTask.matcher(text)
			if (teamMatcher.find()) {
				teamNumber = teamMatcher.group(1).toIntOrNull()
			}
			if (problemMatcher.find()) {
				taskNumber = problemMatcher.group(1).toIntOrNull()
			}
			if (teamNumber != null && taskNumber != null) {
				return QrCodeEntity(teamNumber, taskNumber)
			}
		}
		return null
	}

	override fun onCleared() {
		super.onCleared()
		cancelDelayTimer()
	}

	fun runDelayTimer() {
		delayHandler = Handler()
		delayRunnable = Runnable {
			state.value = QrScreenState.SCANNING
		}
		delayHandler!!.postDelayed(delayRunnable, HIDE_SUCCESS_DELAY)
	}

	fun cancelDelayTimer() {
		delayRunnable?.run {
			delayHandler?.removeCallbacks(this)
			delayHandler = null
			delayRunnable = null
		}
	}

	fun sendRequest(teamNumber: Int, taskNumber: Int, requestType: RequestTypeEnum) {
		Preferences.getGameCode()?.let {
			callApiRequest(QrRequestEntityWrapper(QrRequestEntity(requestType, taskNumber), it, teamNumber, Preferences.getAuthToken()!!, Preferences.getUserId()!!))
		}
	}
}
