package com.example.hybridtraqining.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hybridtraqining.data.TimeExercise
import com.example.hybridtraqining.data.TrainingPlan
import com.example.hybridtraqining.data.TrainingSet
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TrainingCoachUiState(
    val currentSetIndex: Int = 0,
    val isResting: Boolean = false,
    val isExerciseTimerRunning: Boolean = false,
    val exerciseTimeRemaining: Int = 0,
    val restTimeRemaining: Int = 0,
    val isCompleted: Boolean = false
)

class TrainingCoachViewModel(
    private val trainingPlan: TrainingPlan
) : ViewModel() {
    
    // Flatten all sets with their block and set indices
    private val allSets: List<Triple<Int, Int, TrainingSet>> = trainingPlan.blocks.flatMapIndexed { blockIndex, block ->
        block.sets.mapIndexed { setIndex, set ->
            Triple(blockIndex, setIndex, set)
        }
    }
    
    private val _uiState = MutableStateFlow(
        TrainingCoachUiState(
            exerciseTimeRemaining = (allSets.firstOrNull()?.third?.exercise as? TimeExercise)?.durationSeconds ?: 0,
            restTimeRemaining = allSets.firstOrNull()?.third?.restTimeSeconds ?: 0
        )
    )
    val uiState: StateFlow<TrainingCoachUiState> = _uiState.asStateFlow()
    
    private var exerciseTimerJob: Job? = null
    private var restTimerJob: Job? = null
    
    // Store original duration for time exercises to allow restart
    private var originalExerciseDuration: Int = 0
    
    init {
        // Store original duration for first exercise if it's time-based
        val firstSet = allSets.firstOrNull()?.third
        if (firstSet?.exercise is TimeExercise) {
            originalExerciseDuration = (firstSet.exercise as TimeExercise).durationSeconds
        }
    }
    
    val currentSetTriple: Triple<Int, Int, TrainingSet>?
        get() = allSets.getOrNull(_uiState.value.currentSetIndex)
    
    val currentSet: TrainingSet?
        get() = currentSetTriple?.third
    
    val currentBlockName: String?
        get() = trainingPlan.blocks.getOrNull(currentSetTriple?.first ?: 0)?.name
    
    val totalSets: Int
        get() = allSets.size
    
    val currentSetNumber: Int
        get() = _uiState.value.currentSetIndex + 1
    
    val currentBlockNumber: Int
        get() = (currentSetTriple?.first ?: 0) + 1
    
    val totalBlocks: Int
        get() = trainingPlan.blocks.size
    
    
    fun startExerciseTimer() {
        val currentState = _uiState.value
        if (currentState.exerciseTimeRemaining > 0 && !currentState.isExerciseTimerRunning && !currentState.isResting) {
            exerciseTimerJob?.cancel()
            exerciseTimerJob = viewModelScope.launch {
                var remaining = currentState.exerciseTimeRemaining
                _uiState.value = _uiState.value.copy(isExerciseTimerRunning = true)
                
                while (remaining > 0 && _uiState.value.isExerciseTimerRunning && !_uiState.value.isResting) {
                    _uiState.value = _uiState.value.copy(exerciseTimeRemaining = remaining)
                    delay(1000)
                    remaining--
                }
                
                if (remaining == 0 && !_uiState.value.isResting) {
                    _uiState.value = _uiState.value.copy(
                        isExerciseTimerRunning = false,
                        exerciseTimeRemaining = 0
                    )
                } else if (_uiState.value.isResting) {
                    // Rest started, stop timer
                    _uiState.value = _uiState.value.copy(isExerciseTimerRunning = false)
                }
            }
        }
    }
    
    fun finishExercise() {
        val currentState = _uiState.value
        val set = currentSet
        
        if (set == null) return
        
        // For time exercises, can only finish after timer completes
        if (set.exercise is TimeExercise && currentState.exerciseTimeRemaining > 0) {
            return
        }
        
        exerciseTimerJob?.cancel()
        
        // Start rest period
        _uiState.value = _uiState.value.copy(
            isResting = true,
            restTimeRemaining = set.restTimeSeconds,
            isExerciseTimerRunning = false
        )
        
        startRestTimer()
    }
    
    fun skipRest() {
        restTimerJob?.cancel()
        moveToNextSet()
    }
    
    fun restartExerciseTimer() {
        val set = currentSet
        if (set?.exercise is TimeExercise) {
            exerciseTimerJob?.cancel()
            val duration = (set.exercise as TimeExercise).durationSeconds
            originalExerciseDuration = duration
            _uiState.value = _uiState.value.copy(
                exerciseTimeRemaining = duration,
                isExerciseTimerRunning = false
            )
            // If timer was running, user needs to start it again
        }
    }
    
    fun skipExercise() {
        val set = currentSet
        if (set == null) return
        
        exerciseTimerJob?.cancel()
        
        // Start rest period immediately (skip the exercise)
        _uiState.value = _uiState.value.copy(
            isResting = true,
            restTimeRemaining = set.restTimeSeconds,
            isExerciseTimerRunning = false,
            exerciseTimeRemaining = 0
        )
        
        startRestTimer()
    }
    
    private fun startRestTimer() {
        val currentState = _uiState.value
        if (currentState.isResting && currentState.restTimeRemaining > 0) {
            restTimerJob?.cancel()
            restTimerJob = viewModelScope.launch {
                var remaining = currentState.restTimeRemaining
                while (remaining > 0 && _uiState.value.isResting) {
                    _uiState.value = _uiState.value.copy(restTimeRemaining = remaining)
                    delay(1000)
                    remaining--
                }
                if (remaining == 0 && _uiState.value.isResting) {
                    _uiState.value = _uiState.value.copy(
                        isResting = false,
                        restTimeRemaining = 0
                    )
                    moveToNextSet()
                }
            }
        }
    }
    
    private fun moveToNextSet() {
        val nextIndex = _uiState.value.currentSetIndex + 1
        
        if (nextIndex < allSets.size) {
            val nextSet = allSets[nextIndex].third
            val nextExerciseDuration = (nextSet.exercise as? TimeExercise)?.durationSeconds ?: 0
            originalExerciseDuration = nextExerciseDuration
            _uiState.value = _uiState.value.copy(
                currentSetIndex = nextIndex,
                exerciseTimeRemaining = nextExerciseDuration,
                restTimeRemaining = nextSet.restTimeSeconds,
                isResting = false
            )
        } else {
            _uiState.value = _uiState.value.copy(
                isCompleted = true,
                isResting = false
            )
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        exerciseTimerJob?.cancel()
        restTimerJob?.cancel()
    }
}

