package cz.cuni.mff.maso.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class ErrorEntity(
        @Json(name = "message") val message: String,
        @Json(name = "type") val type: String?,
        @Json(name = "errors") val errors: List<ErrorItem>?
)

@JsonClass(generateAdapter = true)
data class ErrorItem(
        @Json(name = "error") val error: String,
        @Json(name = "rule") val rule: String,
        @Json(name = "field") val field: String
)

data class QrCodeEntity(val teamNumber: Int, val taskNumber: Int)

@JsonClass(generateAdapter = true)
data class QrRequestEntity(
        @Json(name = "action") val action: RequestTypeEnum,
        @Json(name = "taskNumber") val taskNumber: Int
)

data class QrRequestEntityWrapper(
        val requestEntity: QrRequestEntity,
        val gameCode: String,
        val teamNumber: Int,
        val authToken: String,
        val userId: String
)

@JsonClass(generateAdapter = true)
data class QrResponseEntity(
        @Json(name = "teamNumber") val teamNumber: Int,
        @Json(name = "teamName") val teamName: String,
        @Json(name = "taskNumber") val taskNumber: Int,
        @Json(name = "solved") val solved: Boolean, // TODO: change to taskStatusId
        @Json(name = "print") val print: Boolean,
        @Json(name = "printTaskNumber") val printTaskNumber: Int
)

enum class RequestTypeEnum(val value: String) {
    ADD("add"), CANCEL("cancel"), EXCHANGE("exchange")
}

