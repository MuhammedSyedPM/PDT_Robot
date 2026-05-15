package com.syed.jetpacktwo.domain.model

/**
 * Domain event when a barcode is scanned.
 */
data class BarcodeEvent(
    val barcode: String
)
