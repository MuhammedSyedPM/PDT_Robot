package com.technowave.trolley_robo.data.remote

import com.technowave.trolley_robo.data.model.DeviceListResponse
import com.technowave.trolley_robo.data.model.LoginRequest
import com.technowave.trolley_robo.data.model.LoginResponse
import com.technowave.trolley_robo.data.model.StockTakeRequest
import com.technowave.trolley_robo.data.model.StockTakeResponse
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

    @GET
    suspend fun getRackStatus(
        @Url url: String,
        @Query("CustID") custId: String
    ): Response<List<com.technowave.trolley_robo.data.remote.model.ExpectedRackDto>>

    @GET
    suspend fun getStockStatus(
        @Url url: String,
        @Query("CustID") custId: String
    ): Response<List<com.technowave.trolley_robo.data.remote.model.StockStatusDto>>

    @GET
    suspend fun getAllRacks(
        @Url url: String,
        @Query("CustID") custId: String
    ): Response<List<com.technowave.trolley_robo.data.remote.model.ExpectedRackDto>>
}
