package com.syed.jetpacktwo.domain.model

/**
 * Domain model for RFID reader connection status.
 * UI-agnostic; statusColor is an Android color value for convenience.
 */
data class ReaderStatus(
    val status: String,
    val statusColor: Int,
    val isConnected: Boolean,
    val isConnecting: Boolean = false,
    val batteryLevel: Int? = null
)
