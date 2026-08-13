package com.example.myapplication.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.data.model.PracticeCategory
import com.example.myapplication.data.model.PracticeQuestion
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoriteDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: FavoriteRepository

    private val sampleQuestion = PracticeQuestion(
        koreanHint = "나는 산다 / 서울에",
        englishSentence = "I live in Seoul.",
        category = PracticeCategory.HOUSING
    )

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        repository = FavoriteRepository(database.favoriteDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun addFavorite_thenObserveAll_returnsIt() = runBlocking {
        repository.addFavorite(sampleQuestion)

        val favorites = repository.observeFavorites().first()

        assertEquals(1, favorites.size)
        assertEquals("I live in Seoul.", favorites[0].englishSentence)
    }

    @Test
    fun isFavorite_returnsFalseBeforeAddAndTrueAfter() = runBlocking {
        assertFalse(repository.isFavorite(sampleQuestion))

        repository.addFavorite(sampleQuestion)

        assertTrue(repository.isFavorite(sampleQuestion))
    }

    @Test
    fun removeFavorite_deletesIt() = runBlocking {
        repository.addFavorite(sampleQuestion)
        val stored = repository.observeFavorites().first().first()

        repository.removeFavorite(stored)

        assertTrue(repository.observeFavorites().first().isEmpty())
    }

    @Test
    fun removeFavoriteByQuestion_deletesMatchingRow() = runBlocking {
        repository.addFavorite(sampleQuestion)

        repository.removeFavoriteByQuestion(sampleQuestion)

        assertTrue(repository.observeFavorites().first().isEmpty())
    }
}
