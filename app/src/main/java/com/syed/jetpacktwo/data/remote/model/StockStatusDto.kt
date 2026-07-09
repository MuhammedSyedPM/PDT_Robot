package com.syed.jetpacktwo.data.remote.model

import com.google.gson.annotations.SerializedName

data class StockStatusDto(
    @SerializedName("no") val no: Int,
    @SerializedName("department") val department: String,
    @SerializedName("expected") val expected: Int,
    @SerializedName("scanned") val scanned: Int
) {
    val variance: Int
        get() = scanned - expected
}
