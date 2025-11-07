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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.hybridtraqining.data.TrainingPlan
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
    var currentTrainingPlan by rememberSaveable { mutableStateOf<TrainingPlan?>(null) }

    if (currentTrainingPlan != null) {
        TrainingCoachScreen(
            trainingPlan = currentTrainingPlan!!,
            onBack = { currentTrainingPlan = null }
        )
    } else {
        HomeScreen(
            onStartTraining = { trainingPlan ->
                currentTrainingPlan = trainingPlan
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}