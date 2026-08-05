package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ApiResponseResult

@Composable
fun StatusBadge(
    result: ApiResponseResult,
    modifier: Modifier = Modifier
) {
    val statusColor = when {
        result.statusCode in 200..299 -> Color(0xFF10B981)
        result.statusCode in 300..399 -> Color(0xFF3B82F6)
        result.statusCode in 400..499 -> Color(0xFFF59E0B)
        result.statusCode >= 500 -> Color(0xFFEF4444)
        else -> Color(0xFF64748B)
    }

    Row(
        modifier = modifier.testTag("status_badge_row"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Code Badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(statusColor.copy(alpha = 0.15f))
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Text(
                text = if (result.statusCode > 0) "${result.statusCode} ${result.statusMessage}" else result.statusMessage,
                color = statusColor,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }

        // Time Pill
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 8.dp, vertical = 5.dp)
        ) {
            Text(
                text = "${result.timeMs} ms",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Size Pill
        if (result.sizeBytes > 0) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
                val formattedSize = when {
                    result.sizeBytes < 1024 -> "${result.sizeBytes} B"
                    result.sizeBytes < 1024 * 1024 -> String.format("%.1f KB", result.sizeBytes / 1024.0)
                    else -> String.format("%.2f MB", result.sizeBytes / (1024.0 * 1024.0))
                }
                Text(
                    text = formattedSize,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
