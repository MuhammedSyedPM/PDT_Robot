package com.syed.jetpacktwo.data.remote.model

import com.google.gson.annotations.SerializedName

data class RackStatusDto(
    @SerializedName("no") val no: Int,
    @SerializedName("department") val department: String,
    @SerializedName("description") val description: String,
    @SerializedName("isStatusOk") val isStatusOk: Boolean
)
