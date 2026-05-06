package com.syed.jetpacktwo.data.model

import com.google.gson.annotations.SerializedName

data class StockTakeRequest(
    @SerializedName("custID") val custID: String,
    @SerializedName("schedulerID") val schedulerID: String,
    @SerializedName("deviceID") val deviceID: String,
    @SerializedName("StockTakeInfo") val stockTakeInfo: List<StockTakeInfo>
)

data class StockTakeInfo(
    @SerializedName("RFID") val rfid: String,
    @SerializedName("ScanDate") val scanDate: String,
    @SerializedName("Location") val location: Int
)

data class StockTakeResponse(
    @SerializedName("retValue") val retValue: Int,
    @SerializedName("status") val status: Boolean,
    @SerializedName("errorDescription") val errorDescription: String
)
