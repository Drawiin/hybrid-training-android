package com.example.hybridtraqining.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.createSavedStateHandle
import com.example.hybridtraqining.data.Exercise
import com.example.hybridtraqining.data.RepetitionExercise
import com.example.hybridtraqining.data.TimeExercise
import com.example.hybridtraqining.data.TrainingPlan
import com.example.hybridtraqining.ui.viewmodel.TrainingCoachViewModel

@Composable
fun TrainingCoachScreen(
    trainingPlan: TrainingPlan,
    onBack: () -> Unit,
    viewModel: TrainingCoachViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                TrainingCoachViewModel(trainingPlan, createSavedStateHandle())
            }
        }
    ),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as? ComponentActivity
    
    // Keep screen on during active training (when exercise timer is running)
    // Allow screen to turn off during rest periods or when paused
    LaunchedEffect(uiState.isExerciseTimerRunning) {
        activity?.window?.let { window ->
            if (uiState.isExerciseTimerRunning) {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
    }
    
    // Clean up when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
    
    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
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
                
                // Current exercise name - visible even during rest
                if (!uiState.isCompleted) {
                    viewModel.currentSet?.exercise?.let { exercise ->
                        val currentSeries = viewModel.currentExerciseSeriesNumber
                        val totalSeries = viewModel.totalExerciseSeries
                        val seriesText = if (totalSeries > 1) {
                            "Series $currentSeries/$totalSeries"
                        } else {
                            ""
                        }
                        
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = exercise.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (seriesText.isNotEmpty()) {
                                Text(
                                    text = seriesText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
                
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
                
                // Scrollable content area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
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
                                currentBlockName = viewModel.currentBlockName ?: "",
                                onStartExercise = { viewModel.startExerciseTimer() },
                                onFinishExercise = { viewModel.finishExercise() },
                                onRestartExercise = { viewModel.restartExerciseTimer() },
                                onSkipExercise = { viewModel.skipExercise() },
                                onSkipBlock = { viewModel.skipBlock() }
                            )
                        }
                    }
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
    currentBlockName: String,
    onStartExercise: () -> Unit,
    onFinishExercise: () -> Unit,
    onRestartExercise: () -> Unit,
    onSkipExercise: () -> Unit,
    onSkipBlock: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSkipExerciseDialog by remember { mutableStateOf(false) }
    var showSkipBlockDialog by remember { mutableStateOf(false) }
    
    // Show skip exercise confirmation dialog
    if (showSkipExerciseDialog) {
        AlertDialog(
            onDismissRequest = { showSkipExerciseDialog = false },
            title = {
                Text("Skip Exercise?")
            },
            text = {
                Text("Are you sure you want to skip this exercise? You'll move directly to the rest period.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSkipExerciseDialog = false
                        onSkipExercise()
                    }
                ) {
                    Text("Skip")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSkipExerciseDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Show skip block confirmation dialog
    if (showSkipBlockDialog) {
        AlertDialog(
            onDismissRequest = { showSkipBlockDialog = false },
            title = {
                Text("Skip Block?")
            },
            text = {
                Text("Are you sure you want to skip the entire \"$currentBlockName\" block? All remaining exercises in this block will be skipped.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSkipBlockDialog = false
                        onSkipBlock()
                    }
                ) {
                    Text("Skip Block")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSkipBlockDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                            
                            if (isExerciseTimerRunning) {
                                Text(
                                    text = "Exercise in progress...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else if (exerciseTimeRemaining < exercise.durationSeconds && exerciseTimeRemaining > 0) {
                                Text(
                                    text = "Timer paused",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
            
            // Add bottom padding to account for fixed buttons
            Spacer(modifier = Modifier.height(120.dp))
        }
        
        // Fixed action buttons at bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            when (exercise) {
                is RepetitionExercise -> {
                    Button(
                        onClick = onFinishExercise,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Finish Set")
                    }
                }
                
                is TimeExercise -> {
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
                                onClick = { showSkipExerciseDialog = true },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Skip")
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Skip Block button - always visible
            OutlinedButton(
                onClick = { showSkipBlockDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Skip Block")
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
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                }
            }
            
            // Add bottom padding to account for fixed buttons
            Spacer(modifier = Modifier.height(80.dp))
        }
        
        // Fixed action button at bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
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
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                }
            }
            
            // Add bottom padding to account for fixed buttons
            Spacer(modifier = Modifier.height(80.dp))
        }
        
        // Fixed action button at bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
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

