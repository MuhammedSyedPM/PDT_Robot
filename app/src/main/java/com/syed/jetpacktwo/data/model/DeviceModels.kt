package com.syed.jetpacktwo.data.model

import com.google.gson.annotations.SerializedName

data class DeviceListResponse(
    @SerializedName("dataSet") val dataSet: DeviceDataSet,
    @SerializedName("status") val status: Boolean,
    @SerializedName("errorDescription") val errorDescription: String
)

data class DeviceDataSet(
    @SerializedName("data") val data: List<Device>
)

data class Device(
    @SerializedName("id") val id: Int,
    @SerializedName("deviceName") val deviceName: String,
    @SerializedName("location") val location: String,
    @SerializedName("status") val status: String
)
