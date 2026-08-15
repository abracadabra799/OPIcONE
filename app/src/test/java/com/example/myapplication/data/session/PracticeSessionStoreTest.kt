package com.example.myapplication.data.session

import com.example.myapplication.data.model.PracticeCategory
import com.example.myapplication.data.model.PracticeQuestion
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

    @Test
    fun `completed snapshot is isolated from caller and reader mutations`() {
        val firstItem = completedItem("Question one")
        val secondItem = completedItem("Question two")
        val callerOwnedItems = mutableListOf(firstItem, secondItem)
        val store = PracticeSessionStore()

        store.saveCompletedSet(CompletedPracticeSet(callerOwnedItems))
        callerOwnedItems.clear()
        val readerOwnedItems = store.lastCompletedSet()!!.items as MutableList
        readerOwnedItems.clear()

        assertEquals(
            listOf(firstItem, secondItem),
            store.lastCompletedSet()!!.items
        )
    }

    private fun completedItem(question: String) = CompletedPracticeItem(
        question = PracticeQuestion(
            opicQuestion = question,
            koreanHint = "힌트",
            englishSentence = "Answer",
            category = PracticeCategory.HOUSING
        ),
        isFavorite = false
    )
}
