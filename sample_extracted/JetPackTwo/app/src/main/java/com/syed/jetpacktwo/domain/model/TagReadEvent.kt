package com.syed.jetpacktwo.domain.model

/**
 * Domain event when an RFID tag is read.
 */
data class TagReadEvent(
    val epc: String,
    val totalInventory: Int
)
