package com.example.hybridtraqining.data.repository

import com.example.hybridtraqining.data.RepetitionExercise
import com.example.hybridtraqining.data.TimeExercise
import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar

class TrainingRepositoryImplTest {
    
    private val repository = TrainingRepositoryImpl()
    
    @Test
    fun `getTrainingForDay returns Legs Training on Monday`() {
        val result = repository.getTrainingForDay(Calendar.MONDAY)
        
        assertNotNull(result)
        assertEquals("Legs Training", result?.name)
        assertEquals(3, result?.blocks?.size)
        
        // Verify block order: Physical Therapy, Legs Training, Core Training
        assertEquals("Physical Therapy", result?.blocks?.get(0)?.name)
        assertEquals("Legs Training", result?.blocks?.get(1)?.name)
        assertEquals("Core Training", result?.blocks?.get(2)?.name)
    }
    
    @Test
    fun `getTrainingForDay returns Pull Training on Wednesday`() {
        val result = repository.getTrainingForDay(Calendar.WEDNESDAY)
        
        assertNotNull(result)
        assertEquals("Pull Training", result?.name)
        assertEquals(3, result?.blocks?.size)
        
        // Verify block order: Physical Therapy, Pull Training, Core Training
        assertEquals("Physical Therapy", result?.blocks?.get(0)?.name)
        assertEquals("Pull Training", result?.blocks?.get(1)?.name)
        assertEquals("Core Training", result?.blocks?.get(2)?.name)
    }
    
    @Test
    fun `getTrainingForDay returns Push Training on Friday`() {
        val result = repository.getTrainingForDay(Calendar.FRIDAY)
        
        assertNotNull(result)
        assertEquals("Push Training", result?.name)
        assertEquals(3, result?.blocks?.size)
        
        // Verify block order: Physical Therapy, Push Training, Core Training
        assertEquals("Physical Therapy", result?.blocks?.get(0)?.name)
        assertEquals("Push Training", result?.blocks?.get(1)?.name)
        assertEquals("Core Training", result?.blocks?.get(2)?.name)
    }
    
    @Test
    fun `getTrainingForDay returns null for non-training days`() {
        val tuesday = repository.getTrainingForDay(Calendar.TUESDAY)
        val thursday = repository.getTrainingForDay(Calendar.THURSDAY)
        val saturday = repository.getTrainingForDay(Calendar.SATURDAY)
        val sunday = repository.getTrainingForDay(Calendar.SUNDAY)
        
        assertNull(tuesday)
        assertNull(thursday)
        assertNull(saturday)
        assertNull(sunday)
    }
    
    @Test
    fun `Physical Therapy block contains expected exercises`() {
        val mondayPlan = repository.getTrainingForDay(Calendar.MONDAY)
        val physicalTherapyBlock = mondayPlan?.blocks?.get(0)
        
        assertNotNull(physicalTherapyBlock)
        assertEquals("Physical Therapy", physicalTherapyBlock?.name)
        assertTrue(physicalTherapyBlock?.sets?.isNotEmpty() == true)
        
        // Check first exercise is Ankle Circles
        val firstExercise = physicalTherapyBlock?.sets?.get(0)?.exercise
        assertTrue(firstExercise is RepetitionExercise)
        assertEquals("Ankle Circles", firstExercise?.name)
        
        // Check there's a time-based exercise (Towel Stretch)
        val hasTimeExercise = physicalTherapyBlock?.sets?.any { it.exercise is TimeExercise } == true
        assertTrue(hasTimeExercise)
    }
    
    @Test
    fun `Core Training block is consistent across all days`() {
        val mondayPlan = repository.getTrainingForDay(Calendar.MONDAY)
        val wednesdayPlan = repository.getTrainingForDay(Calendar.WEDNESDAY)
        val fridayPlan = repository.getTrainingForDay(Calendar.FRIDAY)
        
        val mondayCore = mondayPlan?.blocks?.get(2)
        val wednesdayCore = wednesdayPlan?.blocks?.get(2)
        val fridayCore = fridayPlan?.blocks?.get(2)
        
        assertEquals("Core Training", mondayCore?.name)
        assertEquals("Core Training", wednesdayCore?.name)
        assertEquals("Core Training", fridayCore?.name)
        
        // Core training should have same number of sets
        assertEquals(mondayCore?.sets?.size, wednesdayCore?.sets?.size)
        assertEquals(mondayCore?.sets?.size, fridayCore?.sets?.size)
    }
    
    @Test
    fun `All training plans have sets with rest times`() {
        val mondayPlan = repository.getTrainingForDay(Calendar.MONDAY)
        
        mondayPlan?.blocks?.forEach { block ->
            block.sets.forEach { set ->
                assertTrue("Rest time should be positive", set.restTimeSeconds > 0)
                assertNotNull("Exercise should not be null", set.exercise)
            }
        }
    }
}

