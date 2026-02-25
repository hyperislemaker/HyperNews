package com.hypernews.app.presentation.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hypernews.app.domain.model.ReactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReactionBottomSheet(
    isVisible: Boolean,
    selectedReaction: ReactionType?,
    reactionCounts: Map<ReactionType, Int>,
    onReactionSelected: (ReactionType) -> Unit,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Tepki Ver",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ReactionType.entries.forEach { type ->
                        ReactionButton(
                            type = type,
                            count = reactionCounts[type] ?: 0,
                            isSelected = selectedReaction == type,
                            onClick = { onReactionSelected(type) }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ReactionButton(
    type: ReactionType,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(if (isSelected) 1.2f else 1f, label = "scale")
    val backgroundColor by animateColorAsState(
        if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
        label = "bg"
    )
    
    val emoji = when (type) {
        ReactionType.LIKE -> "👍"
        ReactionType.LOVE -> "❤️"
        ReactionType.WOW -> "😮"
        ReactionType.SAD -> "😢"
        ReactionType.ANGRY -> "😠"
        ReactionType.THINKING -> "🤔"
    }
    
    Column(
        modifier = Modifier
            .scale(scale)
            .clickable(onClick = onClick)
            .background(backgroundColor, CircleShape)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = emoji,
            fontSize = 28.sp
        )
        if (count > 0) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
