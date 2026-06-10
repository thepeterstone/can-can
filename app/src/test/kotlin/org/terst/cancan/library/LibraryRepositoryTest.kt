package org.terst.cancan.library

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LibraryRepositoryTest {

    private val repository = LibraryRepository()
    private val documents = repository.documents

    @Test
    fun `documents list is not empty`() {
        assertTrue(documents.isNotEmpty())
    }

    @Test
    fun `every document has non-blank assetPath`() {
        documents.forEach { doc ->
            assertFalse("${doc.id} has blank assetPath", doc.assetPath.isBlank())
        }
    }

    @Test
    fun `categories match expected set`() {
        val expected = setOf("USDA Canning Guide", "NCHFP", "Food Preservation", "Foraging", "Hawaii CTAHR")
        val actual = documents.map { it.category }.toSet()
        assertEquals(expected, actual)
    }

    @Test
    fun `document IDs are unique`() {
        val ids = documents.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun `every document has non-blank title`() {
        documents.forEach { doc ->
            assertFalse("${doc.id} has blank title", doc.title.isBlank())
        }
    }
}
