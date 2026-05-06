package com.syed.jetpacktwo.data.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("custID") val custID: String,
    @SerializedName("userID") val userID: String,
    @SerializedName("password") val password: String,
    @SerializedName("regToken") val regToken: String,
    @SerializedName("deviceMACID") val deviceMACID: String,
    @SerializedName("locationID") val locationID: Int,
    @SerializedName("deviceID") val deviceID: String
)

data class LoginResponse(
    @SerializedName("userID") val userID: String?,
    @SerializedName("userName") val userName: String?,
    @SerializedName("roleID") val roleID: Int?,
    @SerializedName("deptID") val deptID: Int?,
    @SerializedName("userType") val userType: Int?,
    @SerializedName("storeID") val storeID: Int?,
    @SerializedName("storeName") val storeName: String?,
    @SerializedName("storeCode") val storeCode: String?,
    @SerializedName("regToken") val regToken: String?,
    @SerializedName("isAdmin") val isAdmin: Int?,
    @SerializedName("isStoreView") val isStoreView: Int?,
    @SerializedName("isApplicationType") val isApplicationType: Int?,
    @SerializedName("maxSchedulerID") val maxSchedulerID: Int?,
    @SerializedName("retValue") val retValue: Int?,
    @SerializedName("status") val status: Boolean?,
    @SerializedName("errorDescription") val errorDescription: String?
)
