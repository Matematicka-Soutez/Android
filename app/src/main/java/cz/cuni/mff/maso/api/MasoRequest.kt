package cz.cuni.mff.maso.api

import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path

interface MasoRequest {
	@Headers(
		"Content-Type:application/json",
		"User-Agent:MaSo-Android-App"
	)
	@PUT("/api/games/{gameCode}/teams/{teamNumber}/solutions")
	fun sendQrCode(
			@Path("gameCode") gameCode: String,
			@Path("teamNumber") teamNumber: Int,
            @Header("X-Auth-Token") authToken: String,
            @Header("X-User-Id") userId: String,
			@Body body: QrRequestEntity
	): Call<QrResponseEntity>
}