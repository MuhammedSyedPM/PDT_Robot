package com.syed.jetpacktwo.data.remote.model

import com.google.gson.annotations.SerializedName

data class ExpectedRackDto(
    @SerializedName("department") val department: String,
    @SerializedName("shelfName") val shelfName: String,
    @SerializedName("shelfTagID") val shelfTagID: String,
    @SerializedName("status") val status: Int
)
