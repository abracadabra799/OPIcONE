package com.example.myapplication

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertSame
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OpicApplicationTest {

    @Test
    fun applicationReturnsSameContainerAndSessionStoreToRecreatedActivities() {
        val application = ApplicationProvider.getApplicationContext<OpicApplication>()

        val firstActivityContainer = application.appContainer
        val recreatedActivityContainer = application.appContainer

        assertSame(firstActivityContainer, recreatedActivityContainer)
        assertSame(
            firstActivityContainer.practiceSessionStore,
            recreatedActivityContainer.practiceSessionStore
        )
    }
}
