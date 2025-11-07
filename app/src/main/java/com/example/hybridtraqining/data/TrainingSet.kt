package com.example.hybridtraqining.data

/**
 * Represents a set in a training block
 * @param exercise The exercise to perform
 * @param restTimeSeconds Rest time in seconds between sets
 */
data class TrainingSet(
    val exercise: Exercise,
    val restTimeSeconds: Int
)

