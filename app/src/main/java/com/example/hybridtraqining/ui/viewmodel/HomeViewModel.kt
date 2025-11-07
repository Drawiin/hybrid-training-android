package com.example.hybridtraqining.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hybridtraqining.data.TrainingPlan
import com.example.hybridtraqining.data.repository.TrainingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

data class HomeUiState(
    val trainingPlan: TrainingPlan? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class HomeViewModel(
    private val repository: TrainingRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadTrainingForToday()
    }
    
    fun loadTrainingForToday() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val calendar = Calendar.getInstance()
                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                val trainingPlan = repository.getTrainingForDay(dayOfWeek)
                
                _uiState.value = _uiState.value.copy(
                    trainingPlan = trainingPlan,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }
}

