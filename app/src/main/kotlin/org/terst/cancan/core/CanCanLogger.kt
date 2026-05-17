package org.terst.cancan.core

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * In-app debug log. Primary debugging tool when ADB is unavailable.
 * Exposed as a StateFlow so any screen can observe and display the log.
 */
object CanCanLogger {
    private const val MAX_ENTRIES = 500
    private val TAG = "CanCan"

    private val _entries = MutableStateFlow<List<String>>(emptyList())
    val entries: StateFlow<List<String>> = _entries.asStateFlow()

    fun log(message: String) {
        Log.d(TAG, message)
        val updated = (_entries.value + message).takeLast(MAX_ENTRIES)
        _entries.value = updated
    }

    fun clear() {
        _entries.value = emptyList()
    }
}
