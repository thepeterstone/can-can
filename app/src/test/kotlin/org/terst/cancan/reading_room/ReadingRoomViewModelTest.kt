package org.terst.cancan.reading_room

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReadingRoomViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository = ReadingRoomRepository()
    private lateinit var viewModel: ReadingRoomViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ReadingRoomViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has all documents and no active category`() {
        val state = viewModel.uiState.value
        assertEquals(repository.documents.size, state.allDocuments.size)
        assertNull(state.activeCategory)
        assertEquals(repository.documents.size, state.filtered.size)
    }

    @Test
    fun `selecting a category filters documents`() {
        viewModel.onCategorySelected("NCHFP")
        val state = viewModel.uiState.value
        assertEquals("NCHFP", state.activeCategory)
        assertTrue(state.filtered.isNotEmpty())
        assertTrue(state.filtered.all { it.category == "NCHFP" })
    }

    @Test
    fun `clearing category restores all documents`() {
        viewModel.onCategorySelected("NCHFP")
        viewModel.onCategorySelected(null)
        val state = viewModel.uiState.value
        assertNull(state.activeCategory)
        assertEquals(repository.documents.size, state.filtered.size)
    }

    @Test
    fun `selecting unknown category returns empty filtered list`() {
        viewModel.onCategorySelected("Unknown Category")
        val state = viewModel.uiState.value
        assertTrue(state.filtered.isEmpty())
    }
}
