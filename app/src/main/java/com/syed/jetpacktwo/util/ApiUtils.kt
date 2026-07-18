package com.syed.jetpacktwo.util

import retrofit2.Response
import org.json.JSONObject

fun <T> Response<T>.getErrorMessage(defaultMessage: String = "Unknown error"): String {
    val errorBodyStr = this.errorBody()?.string()
    var errorMessage = if (this.message().isNullOrBlank()) {
        "Error: ${this.code()}"
    } else {
        "${this.message()} (Code: ${this.code()})"
    }
    
    try {
        if (!errorBodyStr.isNullOrBlank()) {
            val jsonObject = JSONObject(errorBodyStr)
            if (jsonObject.has("errorDescription")) {
                errorMessage = jsonObject.getString("errorDescription")
            } else if (jsonObject.has("message")) {
                errorMessage = jsonObject.getString("message")
            }
        }
    } catch (e: Exception) {
        // Ignore parsing errors, keep the default errorMessage
    }
    return errorMessage
}
