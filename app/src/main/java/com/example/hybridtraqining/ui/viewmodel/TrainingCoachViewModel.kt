package com.example.hybridtraqining.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hybridtraqining.data.TimeExercise
import com.example.hybridtraqining.data.TrainingOverview
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
    private val trainingPlan: TrainingPlan,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    // Flatten all sets with their block and set indices
    private val allSets: List<Triple<Int, Int, TrainingSet>> = trainingPlan.blocks.flatMapIndexed { blockIndex, block ->
        block.sets.mapIndexed { setIndex, set ->
            Triple(blockIndex, setIndex, set)
        }
    }
    
    // Restore state from SavedStateHandle or use defaults
    private val _uiState = MutableStateFlow(
        TrainingCoachUiState(
            currentSetIndex = savedStateHandle.get<Int>("currentSetIndex") ?: 0,
            isResting = savedStateHandle.get<Boolean>("isResting") ?: false,
            isExerciseTimerRunning = false, // Always reset to false on restore (timers are not restored)
            exerciseTimeRemaining = savedStateHandle.get<Int>("exerciseTimeRemaining") 
                ?: (allSets.firstOrNull()?.third?.exercise as? TimeExercise)?.durationSeconds ?: 0,
            restTimeRemaining = savedStateHandle.get<Int>("restTimeRemaining") 
                ?: allSets.firstOrNull()?.third?.restTimeSeconds ?: 0,
            isCompleted = savedStateHandle.get<Boolean>("isCompleted") ?: false
        )
    )
    val uiState: StateFlow<TrainingCoachUiState> = _uiState.asStateFlow()
    
    init {
        // Save state whenever it changes
        viewModelScope.launch {
            _uiState.collect { state ->
                savedStateHandle["currentSetIndex"] = state.currentSetIndex
                savedStateHandle["isResting"] = state.isResting
                savedStateHandle["exerciseTimeRemaining"] = state.exerciseTimeRemaining
                savedStateHandle["restTimeRemaining"] = state.restTimeRemaining
                savedStateHandle["isCompleted"] = state.isCompleted
            }
        }
    }
    
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
    
    /**
     * Get completed sets based on current progress
     * A set is considered completed if we've moved past it (currentSetIndex > setIndex)
     */
    fun getCompletedSets(): Set<TrainingOverview.SetIdentifier> {
        val currentIndex = _uiState.value.currentSetIndex
        val completedSets = mutableSetOf<TrainingOverview.SetIdentifier>()
        
        // All sets before the current one are completed
        for (i in 0 until currentIndex) {
            val triple = allSets.getOrNull(i) ?: continue
            completedSets.add(
                TrainingOverview.SetIdentifier(
                    blockIndex = triple.first,
                    setIndex = triple.second
                )
            )
        }
        
        // If training is completed, mark all sets as completed
        if (_uiState.value.isCompleted) {
            trainingPlan.blocks.forEachIndexed { blockIndex, block ->
                block.sets.indices.forEach { setIndex ->
                    completedSets.add(
                        TrainingOverview.SetIdentifier(blockIndex, setIndex)
                    )
                }
            }
        }
        
        return completedSets
    }
    
    /**
     * Get TrainingOverview with current completion status
     */
    fun getTrainingOverview(): TrainingOverview {
        return TrainingOverview(
            trainingPlan = trainingPlan,
            completedSets = getCompletedSets()
        )
    }
    
    // Current series number for the current exercise within the current block
    val currentExerciseSeriesNumber: Int
        get() {
            val currentTriple = currentSetTriple ?: return 0
            val currentBlockIndex = currentTriple.first
            val currentSetIndex = currentTriple.second
            val currentExerciseName = currentTriple.third.exercise.name
            
            val currentBlock = trainingPlan.blocks.getOrNull(currentBlockIndex) ?: return 0
            
            // Find which occurrence this is (1-based) by counting exercises with same name up to current set index
            var seriesCount = 0
            for (i in 0..currentSetIndex) {
                if (currentBlock.sets.getOrNull(i)?.exercise?.name == currentExerciseName) {
                    seriesCount++
                }
            }
            return seriesCount
        }
    
    // Total number of series for the current exercise within the current block
    val totalExerciseSeries: Int
        get() {
            val currentTriple = currentSetTriple ?: return 0
            val currentBlockIndex = currentTriple.first
            val currentExerciseName = currentTriple.third.exercise.name
            
            val currentBlock = trainingPlan.blocks.getOrNull(currentBlockIndex) ?: return 0
            return currentBlock.sets.count { it.exercise.name == currentExerciseName }
        }
    
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
    
    fun skipBlock() {
        val currentTriple = currentSetTriple ?: return
        val currentBlockIndex = currentTriple.first
        
        // Find the last set index in the current block
        val lastSetInBlockIndex = allSets.indexOfLast { it.first == currentBlockIndex }
        
        if (lastSetInBlockIndex < 0) return
        
        // Cancel any running timers
        exerciseTimerJob?.cancel()
        restTimerJob?.cancel()
        
        // Move to the first set of the next block (or complete if it's the last block)
        val nextBlockIndex = currentBlockIndex + 1
        
        if (nextBlockIndex < trainingPlan.blocks.size) {
            // Find the first set of the next block
            val nextSetIndex = allSets.indexOfFirst { it.first == nextBlockIndex }
            
            if (nextSetIndex >= 0) {
                val nextSetTriple = allSets[nextSetIndex]
                val nextSet = nextSetTriple.third
                val nextExerciseDuration = (nextSet.exercise as? TimeExercise)?.durationSeconds ?: 0
                originalExerciseDuration = nextExerciseDuration
                
                _uiState.value = _uiState.value.copy(
                    currentSetIndex = nextSetIndex,
                    exerciseTimeRemaining = nextExerciseDuration,
                    restTimeRemaining = nextSet.restTimeSeconds,
                    isResting = false,
                    isExerciseTimerRunning = false
                )
            }
        } else {
            // Last block was skipped, complete the training
            _uiState.value = _uiState.value.copy(
                isCompleted = true,
                isResting = false,
                isExerciseTimerRunning = false
            )
        }
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

