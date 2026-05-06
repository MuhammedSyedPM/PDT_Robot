package com.syed.jetpacktwo.util

/**
 * Utility to convert between ASCII and HEX strings, commonly used in RFID tag encoding.
 */
object AsciiUtils {

    /**
     * Converts an ASCII string to its HEX representation.
     * Example: "ABC" -> "414243"
     */
    fun asciiToHex(ascii: String): String {
        val hexChars = CharArray(ascii.length * 2)
        for (i in ascii.indices) {
            val v = ascii[i].toInt() and 0xFF
            hexChars[i * 2] = Character.forDigit(v ushr 4, 16)
            hexChars[i * 2 + 1] = Character.forDigit(v and 0x0F, 16)
        }
        return String(hexChars).uppercase()
    }

    /**
     * Checks if a string is a valid HEX string.
     */
    fun isHex(s: String): Boolean {
        return s.matches(Regex("^[0-9a-fA-F]+$"))
    }
}
