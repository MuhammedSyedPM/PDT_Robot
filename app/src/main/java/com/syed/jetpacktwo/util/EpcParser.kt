package com.syed.jetpacktwo.util

import android.util.Log

/**
 * Utility to parse EPC strings and extract the "Ordinal" (Serial Number) as per GS1 standards.
 */
object EpcParser {

    /**
     * Extracts the serial number (ordinal) from a 96-bit EPC hex string.
     * Most common scheme is SGTIN-96 (Header 0x30).
     */
    fun extractOrdinal(epc: String): String {
        if (epc.isBlank()) return ""
        
        try {
            // SGTIN-96 is 24 hex characters
            if (epc.length == 24 && epc.startsWith("30")) {
                // Convert hex to binary
                val binary = hexToBinary(epc)
                
                // GS1 SGTIN-96: Serial number is the last 38 bits (bits 58 to 95)
                // String indices in binary are 0-based, so bits 58-95 are index 58 to 95 inclusive
                if (binary.length >= 96) {
                    val serialBinary = binary.substring(58, 96)
                    val serialLong = java.lang.Long.parseLong(serialBinary, 2)
                    return serialLong.toString()
                }
            }
        } catch (e: Exception) {
            Log.e("EpcParser", "Error parsing EPC $epc: ${e.message}")
        }
        
        // Fallback: If not SGTIN-96 or parsing fails, return the last 8-10 chars of hex
        return if (epc.length > 8) epc.substring(epc.length - 8) else epc
    }

    private fun hexToBinary(hex: String): String {
        val sb = StringBuilder()
        for (i in hex.indices) {
            val hexChar = hex[i]
            val binString = Integer.toBinaryString(Integer.parseInt(hexChar.toString(), 16))
            sb.append(binString.padStart(4, '0'))
        }
        return sb.toString()
    }
}
