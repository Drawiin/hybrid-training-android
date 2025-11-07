package com.example.hybridtraqining.data.repository

import com.example.hybridtraqining.data.TrainingPlan

interface TrainingRepository {
    fun getTrainingForDay(dayOfWeek: Int): TrainingPlan?
    fun getTrainingByName(name: String): TrainingPlan?
}

