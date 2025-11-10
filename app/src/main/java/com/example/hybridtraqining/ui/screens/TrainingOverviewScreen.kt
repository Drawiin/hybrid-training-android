package com.example.hybridtraqining.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hybridtraqining.data.RepetitionExercise
import com.example.hybridtraqining.data.TimeExercise
import com.example.hybridtraqining.data.TrainingOverview

@Composable
fun TrainingOverviewScreen(
    trainingOverview: TrainingOverview,
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val trainingPlan = trainingOverview.trainingPlan
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Training Name Header
        Text(
            text = trainingPlan.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Training List
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            trainingPlan.blocks.forEachIndexed { blockIndex, block ->
                // Block as top-level TODO item
                BlockItem(
                    blockName = block.name,
                    isCompleted = trainingOverview.isBlockCompleted(blockIndex),
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Sets as nested TODO items
                block.sets.forEachIndexed { setIndex, set ->
                    val exercise = set.exercise
                    val isSetCompleted = trainingOverview.isSetCompleted(blockIndex, setIndex)
                    
                    // Count how many times this exercise appears in the block
                    val exerciseSeriesCount = block.sets.count { it.exercise.name == exercise.name }
                    val currentSeries = block.sets.take(setIndex + 1).count { it.exercise.name == exercise.name }
                    
                    SetItem(
                        exerciseName = exercise.name,
                        isCompleted = isSetCompleted,
                        seriesInfo = if (exerciseSeriesCount > 1) {
                            "Series $currentSeries/$exerciseSeriesCount"
                        } else {
                            null
                        },
                        exerciseDetails = when (exercise) {
                            is RepetitionExercise -> "${exercise.repetitions} reps"
                            is TimeExercise -> formatTime(exercise.durationSeconds)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                // Spacing between blocks
                if (blockIndex < trainingPlan.blocks.size - 1) {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun BlockItem(
    blockName: String,
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Checkbox icon
        if (isCompleted) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Completed",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Block name
        Text(
            text = blockName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (isCompleted) {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

@Composable
private fun SetItem(
    exerciseName: String,
    isCompleted: Boolean,
    seriesInfo: String?,
    exerciseDetails: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(start = 36.dp, top = 6.dp, bottom = 6.dp), // Indented like nested TODO
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Checkbox icon (smaller for nested items)
        if (isCompleted) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Completed",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Exercise name and details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = exerciseName,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isCompleted) {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Series info if applicable
                seriesInfo?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // Exercise details (reps or time)
                Text(
                    text = exerciseDetails,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

