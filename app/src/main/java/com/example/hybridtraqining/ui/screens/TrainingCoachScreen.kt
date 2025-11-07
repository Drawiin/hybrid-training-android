package com.example.hybridtraqining.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hybridtraqining.data.Exercise
import com.example.hybridtraqining.data.RepetitionExercise
import com.example.hybridtraqining.data.TimeExercise
import com.example.hybridtraqining.data.TrainingPlan
import com.example.hybridtraqining.ui.viewmodel.TrainingCoachViewModel

@Composable
fun TrainingCoachScreen(
    trainingPlan: TrainingPlan,
    onBack: () -> Unit,
    viewModel: TrainingCoachViewModel = viewModel { TrainingCoachViewModel(trainingPlan) },
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Progress indicator
            Text(
                text = "Block ${viewModel.currentBlockNumber} of ${viewModel.totalBlocks}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = viewModel.currentBlockName ?: "",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            LinearProgressIndicator(
                progress = { viewModel.currentSetNumber.toFloat() / viewModel.totalSets.toFloat() },
                modifier = Modifier.fillMaxWidth()
            )
            
            Text(
                text = "Set ${viewModel.currentSetNumber} of ${viewModel.totalSets}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (uiState.isResting) {
                // Rest screen
                RestView(
                    restTimeRemaining = uiState.restTimeRemaining,
                    onSkipRest = { viewModel.skipRest() }
                )
            } else if (uiState.isCompleted) {
                // Completed screen
                CompletedView(onBack = onBack)
            } else {
                // Exercise screen
                viewModel.currentSet?.let { set ->
                    ExerciseView(
                        exercise = set.exercise,
                        isExerciseTimerRunning = uiState.isExerciseTimerRunning,
                        exerciseTimeRemaining = uiState.exerciseTimeRemaining,
                        onStartExercise = { viewModel.startExerciseTimer() },
                        onFinishExercise = { viewModel.finishExercise() },
                        onRestartExercise = { viewModel.restartExerciseTimer() },
                        onSkipExercise = { viewModel.skipExercise() }
                    )
                }
            }
        }
    }
}

@Composable
private fun ExerciseView(
    exercise: Exercise,
    isExerciseTimerRunning: Boolean,
    exerciseTimeRemaining: Int,
    onStartExercise: () -> Unit,
    onFinishExercise: () -> Unit,
    onRestartExercise: () -> Unit,
    onSkipExercise: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSkipDialog by remember { mutableStateOf(false) }
    
    // Show skip confirmation dialog
    if (showSkipDialog) {
        AlertDialog(
            onDismissRequest = { showSkipDialog = false },
            title = {
                Text("Skip Exercise?")
            },
            text = {
                Text("Are you sure you want to skip this exercise? You'll move directly to the rest period.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSkipDialog = false
                        onSkipExercise()
                    }
                ) {
                    Text("Skip")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSkipDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = exercise.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            exercise.description?.let { description ->
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            when (exercise) {
                is RepetitionExercise -> {
                    Text(
                        text = "Repetitions: ${exercise.repetitions}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Button(
                        onClick = onFinishExercise,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Finish Set")
                    }
                }
                
                is TimeExercise -> {
                    val progress = if (exercise.durationSeconds > 0) {
                        1f - (exerciseTimeRemaining.toFloat() / exercise.durationSeconds.toFloat())
                    } else {
                        0f
                    }
                    
                    val animatedProgress by animateFloatAsState(
                        targetValue = progress,
                        animationSpec = tween(300),
                        label = "progress"
                    )
                    
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.height(120.dp)
                    )
                    
                    Text(
                        text = formatTime(exerciseTimeRemaining),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    if (!isExerciseTimerRunning && exerciseTimeRemaining == exercise.durationSeconds) {
                        Button(
                            onClick = onStartExercise,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Start Exercise")
                        }
                    } else if (exerciseTimeRemaining == 0) {
                        Button(
                            onClick = onFinishExercise,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Finish Set")
                        }
                    } else {
                        // Exercise timer is running or paused - show restart and skip options
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (isExerciseTimerRunning) {
                                Text(
                                    text = "Exercise in progress...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                Text(
                                    text = "Timer paused",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = onRestartExercise,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Restart")
                                }
                                
                                OutlinedButton(
                                    onClick = { showSkipDialog = true },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Skip")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RestView(
    restTimeRemaining: Int,
    onSkipRest: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Rest",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            Text(
                text = formatTime(restTimeRemaining),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Button(
                onClick = onSkipRest,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Skip Rest")
            }
        }
    }
}

@Composable
private fun CompletedView(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Training Complete!",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Text(
                text = "Great job!",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back to Home")
            }
        }
    }
}


private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

