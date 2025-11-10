package com.example.hybridtraqining.data

/**
 * Represents a training plan with optional completion status for overview display
 */
data class TrainingOverview(
    val trainingPlan: TrainingPlan,
    val completedSets: Set<SetIdentifier> = emptySet()
) {
    /**
     * Identifies a specific set in the training plan
     */
    data class SetIdentifier(
        val blockIndex: Int,
        val setIndex: Int
    )
    
    /**
     * Check if a specific set is completed
     */
    fun isSetCompleted(blockIndex: Int, setIndex: Int): Boolean {
        return completedSets.contains(SetIdentifier(blockIndex, setIndex))
    }
    
    /**
     * Check if a block is fully completed
     */
    fun isBlockCompleted(blockIndex: Int): Boolean {
        val block = trainingPlan.blocks.getOrNull(blockIndex) ?: return false
        return block.sets.indices.all { setIndex ->
            isSetCompleted(blockIndex, setIndex)
        }
    }
    
    /**
     * Check if the entire training is completed
     */
    fun isTrainingCompleted(): Boolean {
        return trainingPlan.blocks.indices.all { blockIndex ->
            isBlockCompleted(blockIndex)
        }
    }
}


