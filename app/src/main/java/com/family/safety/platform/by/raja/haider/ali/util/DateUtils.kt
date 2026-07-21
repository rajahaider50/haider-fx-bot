package com.family.safety.platform.by.raja.haider.ali.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private val fullFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    private val shortFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())

    fun formatFull(timestamp: Long): String = fullFormat.format(Date(timestamp))
    fun formatTime(timestamp: Long): String = shortFormat.format(Date(timestamp))
    fun formatDate(timestamp: Long): String = dateFormat.format(Date(timestamp))
    fun formatDay(timestamp: Long): String = dayFormat.format(Date(timestamp))

    fun timeAgo(timestamp: Long): String {
        val diff = System.currentTimeMillis() - timestamp
        return when {
            diff < 60_000 -> "Just now"
            diff < 3600_000 -> "${diff / 60_000}m ago"
            diff < 86400_000 -> "${diff / 3600_000}h ago"
            diff < 604800_000 -> "${diff / 86400_000}d ago"
            else -> formatDate(timestamp)
        }
    }

    fun formatScreenTime(millis: Long): String {
        val hours = millis / 3600000
        val minutes = (millis % 3600000) / 60000
        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m"
            else -> "0m"
        }
    }

    fun todayString(): String = dateFormat.format(Date())
}
