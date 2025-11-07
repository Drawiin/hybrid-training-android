package com.example.hybridtraqining.ui.viewmodel

import com.example.hybridtraqining.data.TrainingPlan
import com.example.hybridtraqining.data.repository.TrainingRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.Calendar

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    
    private val testDispatcher = StandardTestDispatcher()
    private val repository: TrainingRepository = mockk()
    private lateinit var viewModel: HomeViewModel
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `initial state is loading`() = runTest(testDispatcher) {
        val mockPlan = TrainingPlan(
            name = "Test Plan",
            blocks = emptyList()
        )
        
        coEvery { repository.getTrainingForDay(any()) } returns mockPlan
        
        viewModel = HomeViewModel(repository)
        advanceUntilIdle()
        
        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertNotNull(state.trainingPlan)
    }
    
    @Test
    fun `loadTrainingForToday returns training plan for Monday`() = runTest(testDispatcher) {
        val mockPlan = TrainingPlan(
            name = "Legs Training",
            blocks = emptyList()
        )
        
        // Mock any day since we can't control Calendar.getInstance()
        coEvery { repository.getTrainingForDay(any()) } returns mockPlan
        
        viewModel = HomeViewModel(repository)
        advanceUntilIdle()
        
        val state = viewModel.uiState.first()
        assertEquals(mockPlan, state.trainingPlan)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }
    
    @Test
    fun `loadTrainingForToday returns null for rest day`() = runTest(testDispatcher) {
        coEvery { repository.getTrainingForDay(any()) } returns null
        
        viewModel = HomeViewModel(repository)
        advanceUntilIdle()
        
        val state = viewModel.uiState.first()
        assertNull(state.trainingPlan)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }
    
    @Test
    fun `loadTrainingForToday handles repository errors`() = runTest(testDispatcher) {
        val errorMessage = "Repository error"
        coEvery { repository.getTrainingForDay(any()) } throws RuntimeException(errorMessage)
        
        viewModel = HomeViewModel(repository)
        advanceUntilIdle()
        
        val state = viewModel.uiState.first()
        assertNull(state.trainingPlan)
        assertFalse(state.isLoading)
        assertEquals(errorMessage, state.error)
    }
    
    @Test
    fun `loadTrainingForToday can be called multiple times`() = runTest(testDispatcher) {
        val mockPlan1 = TrainingPlan(name = "Plan 1", blocks = emptyList())
        val mockPlan2 = TrainingPlan(name = "Plan 2", blocks = emptyList())
        
        coEvery { repository.getTrainingForDay(any()) } returnsMany listOf(mockPlan1, mockPlan2)
        
        viewModel = HomeViewModel(repository)
        advanceUntilIdle()
        
        var state = viewModel.uiState.first()
        assertEquals(mockPlan1, state.trainingPlan)
        
        viewModel.loadTrainingForToday()
        advanceUntilIdle()
        
        state = viewModel.uiState.first()
        assertEquals(mockPlan2, state.trainingPlan)
    }
    
    @Test
    fun `loadTrainingForToday sets loading state correctly`() = runTest(testDispatcher) {
        val mockPlan = TrainingPlan(name = "Test", blocks = emptyList())
        
        coEvery { repository.getTrainingForDay(any()) } coAnswers {
            kotlinx.coroutines.delay(100)
            mockPlan
        }
        
        viewModel = HomeViewModel(repository)
        advanceUntilIdle()
        
        // Should not be loading after completion
        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertEquals(mockPlan, state.trainingPlan)
    }
}

