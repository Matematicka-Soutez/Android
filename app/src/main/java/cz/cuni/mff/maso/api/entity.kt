package cz.cuni.mff.maso.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

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

@JsonClass(generateAdapter = true)
data class LoginRequestEntity(
        @Json(name = "username") val username: String,
        @Json(name = "password") val password: String
)

data class QrRequestEntityWrapper(
        val requestEntity: QrRequestEntity,
        val gameCode: String,
        val teamNumber: Int,
        val authToken: String,
        val userId: String
)

data class LoginRequestEntityWrapper(
        val requestEntity: LoginRequestEntity
)

@JsonClass(generateAdapter = true)
data class QrResponseEntity(
        @Json(name = "teamNumber") val teamNumber: Int,
        @Json(name = "teamName") val teamName: String,
        @Json(name = "taskNumber") val taskNumber: Int,
        @Json(name = "taskStatusId") val taskStatusId: Int,
        @Json(name = "print") val print: Boolean,
        @Json(name = "printNumber") val printNumber: Int?
)

@JsonClass(generateAdapter = true)
data class LoginResponseEntity(
        @Json(name = "status") val status: RequestStatusEnum,
        @Json(name = "data") val data: LoginDataEntity?,
        @Json(name = "message") val message: String?
)

@JsonClass(generateAdapter = true)
data class LoginDataEntity(
        @Json(name = "authToken") val authToken: String,
        @Json(name = "userId") val userId: String
)

enum class RequestTypeEnum(val value: String) {
    SOLVE("solve"), CANCEL("cancel"), EXCHANGE("exchange")
}

enum class RequestStatusEnum(val value: String) {
    @Json(name = "success") SUCCESS("success"),
    @Json(name = "error") ERROR("error")
}

enum class TaskStatusEnum(val value: Int) {
    ISSUED(1), SOLVED(2), EXCHANGED(3)
}


