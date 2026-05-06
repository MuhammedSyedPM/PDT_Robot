package com.syed.jetpacktwo.data.remote

import com.syed.jetpacktwo.data.model.DeviceListResponse
import com.syed.jetpacktwo.data.model.LoginRequest
import com.syed.jetpacktwo.data.model.LoginResponse
import com.syed.jetpacktwo.data.model.StockTakeRequest
import com.syed.jetpacktwo.data.model.StockTakeResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST
    suspend fun login(
        @Url url: String,
        @Body request: LoginRequest,
        @Header("accept") accept: String = "text/plain",
        @Header("Content-Type") contentType: String = "application/json-patch+json"
    ): Response<LoginResponse>

    @GET
    suspend fun getDeviceList(
        @Url url: String,
        @Header("accept") accept: String = "text/plain"
    ): Response<DeviceListResponse>

    @POST
    suspend fun uploadStockTake(
        @Url url: String,
        @Body request: StockTakeRequest,
        @Header("accept") accept: String = "text/plain",
        @Header("Content-Type") contentType: String = "application/json-patch+json"
    ): Response<StockTakeResponse>
}
