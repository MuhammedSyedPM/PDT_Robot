package com.syed.jetpacktwo.domain.model

/**
 * Domain model for tag trace updates (signal strength while tracing a tag).
 */
data class TracedTagInfo(
    val epcHex: String,
    val scaledRssi: Int,
    val ordinal: String? = null
)
