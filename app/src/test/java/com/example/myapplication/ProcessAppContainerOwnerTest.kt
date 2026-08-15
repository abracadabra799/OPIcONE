package com.example.myapplication

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class ProcessAppContainerOwnerTest {

    @Test
    fun `activity recreation reuses application owned container`() {
        val container = Any()
        var creationCount = 0
        val applicationOwner = ProcessAppContainerOwner {
            creationCount += 1
            container
        }

        val firstActivityContainer = applicationOwner.appContainer
        val recreatedActivityContainer = applicationOwner.appContainer

        assertSame(container, firstActivityContainer)
        assertSame(firstActivityContainer, recreatedActivityContainer)
        assertEquals(1, creationCount)
    }
}
