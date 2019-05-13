package cz.cuni.mff.maso.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path

interface MasoRequest {
	@PUT("/api/games/{gameCode}/teams/{teamNumber}/solutions")
	fun sendQrCode(@Path("gameCode") gameCode: String, @Path("teamNumber") teamNumber: Int, @Body body: QrRequestEntity): Call<QrResponseEntity>
}