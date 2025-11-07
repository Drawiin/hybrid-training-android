package com.example.hybridtraqining.data

/**
 * Represents an exercise that can be performed by repetitions or by time
 */
sealed class Exercise {
    abstract val name: String
    abstract val description: String?
}

data class RepetitionExercise(
    override val name: String,
    override val description: String? = null,
    val repetitions: Int
) : Exercise()

data class TimeExercise(
    override val name: String,
    override val description: String? = null,
    val durationSeconds: Int
) : Exercise()

