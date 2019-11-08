package cz.cuni.mff.maso.api

import retrofit2.Call
import retrofit2.http.*

interface TaskUpdateRequest {
	@Headers(
			"Content-Type:application/json",
			"User-Agent:MaSo-Android-App"
	)
	@PUT("/api/games/{gameCode}/teams/{teamNumber}/tasks")
	fun sendQrCode(
			@Path("gameCode") gameCode: String,
			@Path("teamNumber") teamNumber: Int,
			@Header("X-Auth-Token") authToken: String,
			@Header("X-User-Id") userId: String,
			@Body body: QrRequestEntity
	): Call<QrResponseEntity>
}

interface LoginRequest {
	@Headers(
			"Content-Type:application/json",
			"User-Agent:MaSo-Android-App"
	)
	@POST("/api/login")
	fun login(
			@Body body: LoginRequestEntity
	): Call<LoginResponseEntity>
}