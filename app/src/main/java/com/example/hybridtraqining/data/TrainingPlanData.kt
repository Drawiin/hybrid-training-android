package com.example.hybridtraqining.data

/**
 * Hardcoded training plan data based on the Hybrid Training Plan
 */
object TrainingPlanData {
    
    /**
     * Get the training plan for a specific day of the week
     * Monday: Legs
     * Wednesday: Pull
     * Friday: Push
     */
    fun getTrainingForDay(dayOfWeek: Int): TrainingPlan? {
        return when (dayOfWeek) {
            java.util.Calendar.MONDAY -> legsTrainingPlan()
            java.util.Calendar.WEDNESDAY -> pullTrainingPlan()
            java.util.Calendar.FRIDAY -> pushTrainingPlan()
            else -> null
        }
    }
    
    private fun legsTrainingPlan(): TrainingPlan {
        return TrainingPlan(
            name = "Legs Training",
            blocks = listOf(
                // Pre-Workout Physiotherapy
                TrainingBlock(
                    name = "Pre-Workout Physiotherapy",
                    sets = listOf(
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Ankle Circles",
                                description = "Lift one foot slightly off the ground, rotate ankle clockwise and counterclockwise",
                                repetitions = 10
                            ),
                            restTimeSeconds = 30
                        ),
                        TrainingSet(
                            exercise = TimeExercise(
                                name = "Towel Stretch (Achilles / Calf Stretch)",
                                description = "Sit with leg extended, loop towel around forefoot, pull toes gently toward shin",
                                durationSeconds = 25
                            ),
                            restTimeSeconds = 30
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Seated Toe Raises (Tibialis Activation)",
                                description = "Sit, heels on floor, lift toes upward slowly",
                                repetitions = 15
                            ),
                            restTimeSeconds = 30
                        ),
                        TrainingSet(
                            exercise = TimeExercise(
                                name = "Standing Calf Stretch Against Wall",
                                description = "One leg back, heel on floor, slight bend in front knee, lean forward",
                                durationSeconds = 25
                            ),
                            restTimeSeconds = 30
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Heel Raises — Partial / Pain-Free",
                                description = "Stand, raise heels slowly, only as far as comfortable",
                                repetitions = 10
                            ),
                            restTimeSeconds = 60
                        )
                    )
                ),
                // Warm-up
                TrainingBlock(
                    name = "Warm-up",
                    sets = listOf(
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Complex Burpees (no-jump version)",
                                repetitions = 8
                            ),
                            restTimeSeconds = 30
                        ),
                        TrainingSet(
                            exercise = TimeExercise(
                                name = "Cycling",
                                durationSeconds = 180
                            ),
                            restTimeSeconds = 30
                        )
                    )
                ),
                // Core Training
                TrainingBlock(
                    name = "Core Training",
                    sets = listOf(
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Abs Crunches",
                                repetitions = 15
                            ),
                            restTimeSeconds = 30
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Leg Raises",
                                repetitions = 15
                            ),
                            restTimeSeconds = 30
                        ),
                        TrainingSet(
                            exercise = TimeExercise(
                                name = "Lateral Plank (each side)",
                                durationSeconds = 30
                            ),
                            restTimeSeconds = 30
                        ),
                        TrainingSet(
                            exercise = TimeExercise(
                                name = "Plank",
                                durationSeconds = 60
                            ),
                            restTimeSeconds = 30
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Glute Bridge",
                                repetitions = 15
                            ),
                            restTimeSeconds = 60
                        )
                    )
                ),
                // Legs Training
                TrainingBlock(
                    name = "Legs Training",
                    sets = listOf(
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Standing Calf Raises",
                                description = "Slow descend",
                                repetitions = 15
                            ),
                            restTimeSeconds = 60
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Toe Raises",
                                repetitions = 10
                            ),
                            restTimeSeconds = 60
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Bent-Knee Calf Raises",
                                repetitions = 10
                            ),
                            restTimeSeconds = 60
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Alternating Lunges (each side)",
                                repetitions = 14
                            ),
                            restTimeSeconds = 60
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Bodyweight Squats",
                                repetitions = 30
                            ),
                            restTimeSeconds = 60
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Tempo Squats",
                                description = "Replace jump squats until recovery",
                                repetitions = 15
                            ),
                            restTimeSeconds = 60
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Bodyweight Squats",
                                repetitions = 25
                            ),
                            restTimeSeconds = 60
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Tempo Squats",
                                repetitions = 10
                            ),
                            restTimeSeconds = 60
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Bodyweight Squats",
                                repetitions = 20
                            ),
                            restTimeSeconds = 60
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Tempo Squats",
                                repetitions = 5
                            ),
                            restTimeSeconds = 60
                        )
                    )
                )
            )
        )
    }
    
    private fun pullTrainingPlan(): TrainingPlan {
        return TrainingPlan(
            name = "Pull Training",
            blocks = listOf(
                // Pre-Workout Physiotherapy
                TrainingBlock(
                    name = "Pre-Workout Physiotherapy",
                    sets = listOf(
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Ankle Circles",
                                description = "Lift one foot slightly off the ground, rotate ankle clockwise and counterclockwise",
                                repetitions = 10
                            ),
                            restTimeSeconds = 30
                        ),
                        TrainingSet(
                            exercise = TimeExercise(
                                name = "Towel Stretch (Achilles / Calf Stretch)",
                                description = "Sit with leg extended, loop towel around forefoot, pull toes gently toward shin",
                                durationSeconds = 25
                            ),
                            restTimeSeconds = 30
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Seated Toe Raises (Tibialis Activation)",
                                description = "Sit, heels on floor, lift toes upward slowly",
                                repetitions = 15
                            ),
                            restTimeSeconds = 30
                        ),
                        TrainingSet(
                            exercise = TimeExercise(
                                name = "Standing Calf Stretch Against Wall",
                                description = "One leg back, heel on floor, slight bend in front knee, lean forward",
                                durationSeconds = 25
                            ),
                            restTimeSeconds = 30
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Heel Raises — Partial / Pain-Free",
                                description = "Stand, raise heels slowly, only as far as comfortable",
                                repetitions = 10
                            ),
                            restTimeSeconds = 60
                        )
                    )
                ),
                // Warm-up
                TrainingBlock(
                    name = "Warm-up",
                    sets = listOf(
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Complex Burpees (no-jump version)",
                                repetitions = 8
                            ),
                            restTimeSeconds = 30
                        ),
                        TrainingSet(
                            exercise = TimeExercise(
                                name = "Cycling",
                                durationSeconds = 180
                            ),
                            restTimeSeconds = 30
                        )
                    )
                ),
                // Core Training
                TrainingBlock(
                    name = "Core Training",
                    sets = listOf(
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Abs Crunches",
                                repetitions = 15
                            ),
                            restTimeSeconds = 30
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Leg Raises",
                                repetitions = 15
                            ),
                            restTimeSeconds = 30
                        ),
                        TrainingSet(
                            exercise = TimeExercise(
                                name = "Lateral Plank (each side)",
                                durationSeconds = 30
                            ),
                            restTimeSeconds = 30
                        ),
                        TrainingSet(
                            exercise = TimeExercise(
                                name = "Plank",
                                durationSeconds = 60
                            ),
                            restTimeSeconds = 30
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Glute Bridge",
                                repetitions = 15
                            ),
                            restTimeSeconds = 60
                        )
                    )
                ),
                // Pull Training
                TrainingBlock(
                    name = "Pull Training",
                    sets = listOf(
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Pull-ups",
                                description = "Proper form",
                                repetitions = 7
                            ),
                            restTimeSeconds = 90
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Pull-ups",
                                repetitions = 7
                            ),
                            restTimeSeconds = 90
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Pull-ups",
                                repetitions = 7
                            ),
                            restTimeSeconds = 90
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Pull-ups",
                                repetitions = 7
                            ),
                            restTimeSeconds = 90
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Australian Rows",
                                repetitions = 13
                            ),
                            restTimeSeconds = 90
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Australian Rows",
                                repetitions = 13
                            ),
                            restTimeSeconds = 90
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Australian Rows",
                                repetitions = 13
                            ),
                            restTimeSeconds = 90
                        ),
                        TrainingSet(
                            exercise = TimeExercise(
                                name = "Isometric Hold (Pull)",
                                durationSeconds = 20
                            ),
                            restTimeSeconds = 90
                        ),
                        TrainingSet(
                            exercise = TimeExercise(
                                name = "Isometric Hold (Pull)",
                                durationSeconds = 20
                            ),
                            restTimeSeconds = 90
                        ),
                        TrainingSet(
                            exercise = TimeExercise(
                                name = "Isometric Hold (Pull)",
                                durationSeconds = 20
                            ),
                            restTimeSeconds = 90
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Improvised Curl",
                                repetitions = 12
                            ),
                            restTimeSeconds = 90
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Improvised Curl",
                                repetitions = 12
                            ),
                            restTimeSeconds = 90
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Improvised Curl",
                                repetitions = 12
                            ),
                            restTimeSeconds = 60
                        )
                    )
                )
            )
        )
    }
    
    private fun pushTrainingPlan(): TrainingPlan {
        return TrainingPlan(
            name = "Push Training",
            blocks = listOf(
                // Pre-Workout Physiotherapy
                TrainingBlock(
                    name = "Pre-Workout Physiotherapy",
                    sets = listOf(
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Ankle Circles",
                                description = "Lift one foot slightly off the ground, rotate ankle clockwise and counterclockwise",
                                repetitions = 10
                            ),
                            restTimeSeconds = 30
                        ),
                        TrainingSet(
                            exercise = TimeExercise(
                                name = "Towel Stretch (Achilles / Calf Stretch)",
                                description = "Sit with leg extended, loop towel around forefoot, pull toes gently toward shin",
                                durationSeconds = 25
                            ),
                            restTimeSeconds = 30
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Seated Toe Raises (Tibialis Activation)",
                                description = "Sit, heels on floor, lift toes upward slowly",
                                repetitions = 15
                            ),
                            restTimeSeconds = 30
                        ),
                        TrainingSet(
                            exercise = TimeExercise(
                                name = "Standing Calf Stretch Against Wall",
                                description = "One leg back, heel on floor, slight bend in front knee, lean forward",
                                durationSeconds = 25
                            ),
                            restTimeSeconds = 30
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Heel Raises — Partial / Pain-Free",
                                description = "Stand, raise heels slowly, only as far as comfortable",
                                repetitions = 10
                            ),
                            restTimeSeconds = 60
                        )
                    )
                ),
                // Warm-up
                TrainingBlock(
                    name = "Warm-up",
                    sets = listOf(
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Complex Burpees (no-jump version)",
                                repetitions = 8
                            ),
                            restTimeSeconds = 30
                        ),
                        TrainingSet(
                            exercise = TimeExercise(
                                name = "Cycling",
                                durationSeconds = 180
                            ),
                            restTimeSeconds = 30
                        )
                    )
                ),
                // Core Training
                TrainingBlock(
                    name = "Core Training",
                    sets = listOf(
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Abs Crunches",
                                repetitions = 15
                            ),
                            restTimeSeconds = 30
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Leg Raises",
                                repetitions = 15
                            ),
                            restTimeSeconds = 30
                        ),
                        TrainingSet(
                            exercise = TimeExercise(
                                name = "Lateral Plank (each side)",
                                durationSeconds = 30
                            ),
                            restTimeSeconds = 30
                        ),
                        TrainingSet(
                            exercise = TimeExercise(
                                name = "Plank",
                                durationSeconds = 60
                            ),
                            restTimeSeconds = 30
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Glute Bridge",
                                repetitions = 15
                            ),
                            restTimeSeconds = 60
                        )
                    )
                ),
                // Push Training
                TrainingBlock(
                    name = "Push Training",
                    sets = listOf(
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Push-ups",
                                repetitions = 14
                            ),
                            restTimeSeconds = 90
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Push-ups",
                                repetitions = 14
                            ),
                            restTimeSeconds = 90
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Push-ups",
                                repetitions = 14
                            ),
                            restTimeSeconds = 90
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Push-ups",
                                repetitions = 14
                            ),
                            restTimeSeconds = 90
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Dips",
                                repetitions = 9
                            ),
                            restTimeSeconds = 90
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Dips",
                                repetitions = 9
                            ),
                            restTimeSeconds = 90
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Dips",
                                repetitions = 9
                            ),
                            restTimeSeconds = 90
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Pike Push-ups",
                                repetitions = 7
                            ),
                            restTimeSeconds = 90
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Pike Push-ups",
                                repetitions = 7
                            ),
                            restTimeSeconds = 90
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Pike Push-ups",
                                repetitions = 7
                            ),
                            restTimeSeconds = 90
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Diamond Push-ups",
                                repetitions = 9
                            ),
                            restTimeSeconds = 90
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Diamond Push-ups",
                                repetitions = 9
                            ),
                            restTimeSeconds = 90
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Diamond Push-ups",
                                repetitions = 9
                            ),
                            restTimeSeconds = 90
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Explosive Push-ups",
                                description = "Low reps to protect joints during recovery",
                                repetitions = 6
                            ),
                            restTimeSeconds = 90
                        ),
                        TrainingSet(
                            exercise = RepetitionExercise(
                                name = "Explosive Push-ups",
                                repetitions = 6
                            ),
                            restTimeSeconds = 60
                        )
                    )
                )
            )
        )
    }
}

