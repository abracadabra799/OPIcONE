package com.example.myapplication.data.session

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PracticeSessionStoreTest {

    @Test
    fun `normalizes case trim and repeated whitespace`() {
        val store = PracticeSessionStore()

        store.addAskedQuestions(listOf("  Tell   Me About Your HOME. "))

        assertTrue(store.containsQuestion("tell me about your home."))
    }

    @Test
    fun `new store starts with empty process history`() {
        val first = PracticeSessionStore().apply {
            addAskedQuestions(listOf("Question"))
        }
        val second = PracticeSessionStore()

        assertEquals(1, first.askedQuestions().size)
        assertTrue(second.askedQuestions().isEmpty())
    }
}
