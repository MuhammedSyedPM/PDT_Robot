package com.syed.jetpacktwo.util

import androidx.compose.foundation.clickable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

fun Modifier.debouncedClickable(
    enabled: Boolean = true,
    debounceTime: Long = 500L,
    onClick: () -> Unit
): Modifier = composed {
    val debouncedClick = rememberDebouncedClick(debounceTime, onClick)
    this.clickable(enabled = enabled, onClick = debouncedClick)
}

@Composable
fun rememberDebouncedClick(
    debounceTime: Long = 500L,
    onClick: () -> Unit
): () -> Unit {
    var lastClickTime by remember { mutableLongStateOf(0L) }
    return {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > debounceTime) {
            lastClickTime = currentTime
            onClick()
        }
    }
}
