package com.wattswatcher.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wattswatcher.app.data.api.HistoricalDataPoint
import kotlin.math.max

@Composable
fun ConsumptionChart(
    data: List<HistoricalDataPoint>,
    period: String,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    
    if (data.isEmpty()) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No data available for $period",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }
    
    Column(modifier = modifier) {
        // Chart
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .padding(16.dp)
        ) {
            val maxValue = data.maxOfOrNull { it.consumption } ?: 1.0
            val minValue = data.minOfOrNull { it.consumption } ?: 0.0
            val range = max(maxValue - minValue, 1.0)
            
            val stepX = size.width / (data.size - 1).coerceAtLeast(1)
            val stepY = size.height / range.toFloat()
            
            // Draw grid lines
            for (i in 0..4) {
                val y = size.height * i / 4
                drawLine(
                    color = surfaceVariant,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.dp.toPx()
                )
            }
            
            // Draw line chart
            if (data.size > 1) {
                val path = Path()
                data.forEachIndexed { index, point ->
                    val x = index * stepX
                    val y = size.height - ((point.consumption - minValue) * stepY).toFloat()
                    
                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                }
                
                drawPath(
                    path = path,
                    color = primaryColor,
                    style = Stroke(width = 3.dp.toPx())
                )
                
                // Draw data points
                data.forEachIndexed { index, point ->
                    val x = index * stepX
                    val y = size.height - ((point.consumption - minValue) * stepY).toFloat()
                    
                    drawCircle(
                        color = primaryColor,
                        radius = 4.dp.toPx(),
                        center = Offset(x, y)
                    )
                }
            }
        }
        
        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (data.isNotEmpty()) data.first().timestamp else "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = if (data.isNotEmpty()) data.last().timestamp else "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}