package com.example.listenote.ui.util

import android.annotation.SuppressLint

@SuppressLint("DefaultLocale")
fun formatDuration(ms: Long): String {
    if (ms < 0) return "00:00"
    val minutes = (ms / 1000) / 60
    val seconds = (ms / 1000) % 60
    return String.format("%02d:%02d", minutes, seconds)
}