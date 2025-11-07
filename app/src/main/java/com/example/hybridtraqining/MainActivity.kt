package com.example.hybridtraqining

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.hybridtraqining.data.TrainingPlan
import com.example.hybridtraqining.data.repository.TrainingRepositoryImpl
import com.example.hybridtraqining.ui.screens.HomeScreen
import com.example.hybridtraqining.ui.screens.TrainingCoachScreen
import com.example.hybridtraqining.ui.theme.HybridTraqiningTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HybridTraqiningTheme {
                HybridTraqiningApp()
            }
        }
    }
}

@Composable
fun HybridTraqiningApp() {
    val repository = remember { TrainingRepositoryImpl() }
    
    // Save only the training plan name, not the entire object
    var currentTrainingPlanName by rememberSaveable { mutableStateOf<String?>(null) }
    
    // Look up the training plan by name when restoring
    val currentTrainingPlan = remember(currentTrainingPlanName) {
        currentTrainingPlanName?.let { repository.getTrainingByName(it) }
    }

    if (currentTrainingPlan != null) {
        TrainingCoachScreen(
            trainingPlan = currentTrainingPlan,
            onBack = { currentTrainingPlanName = null }
        )
    } else {
        HomeScreen(
            onStartTraining = { trainingPlan ->
                currentTrainingPlanName = trainingPlan.name
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}