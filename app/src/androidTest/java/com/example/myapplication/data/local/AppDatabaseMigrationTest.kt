package com.example.myapplication.data.local

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseMigrationTest {

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java
    )

    @Test
    fun migration1To2_preservesExistingFavoriteWithNullQuestion() {
        helper.createDatabase("migration-test", 1).apply {
            execSQL(
                "INSERT INTO favorite_sentences " +
                    "(id, category, koreanHint, englishSentence, createdAt) " +
                    "VALUES (1, 'HOUSING', '힌트', 'Answer.', 1)"
            )
            close()
        }

        helper.runMigrationsAndValidate("migration-test", 2, true, MIGRATION_1_2).use { db ->
            db.query(
                "SELECT opicQuestion, englishSentence " +
                    "FROM favorite_sentences WHERE id = 1"
            ).use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertTrue(cursor.isNull(0))
                assertEquals("Answer.", cursor.getString(1))
            }
        }
    }
}
