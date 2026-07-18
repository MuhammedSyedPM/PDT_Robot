package com.syed.jetpacktwo.data.remote.model

import com.google.gson.annotations.SerializedName

data class StockStatusDto(
    var no: Int = 0,
    @SerializedName("department") val department: String,
    @SerializedName("expected") val expected: Int,
    @SerializedName("scanned") val scanned: Int,
    @SerializedName("variance") val variance: Int
)
