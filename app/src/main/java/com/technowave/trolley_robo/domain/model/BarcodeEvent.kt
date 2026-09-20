package com.technowave.trolley_robo.domain.model

/**
 * Domain event when a barcode is scanned.
 */
data class BarcodeEvent(
    val barcode: String
)
