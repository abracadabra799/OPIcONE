package com.example.myapplication.ui.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

class RoutesTest {

    @Test
    fun `favorite practice route carries persisted favorite id`() {
        assertEquals(
            "favorite_practice/{favoriteId}",
            Routes.FavoritePractice.route
        )
        assertEquals(
            "favorite_practice/42",
            Routes.FavoritePractice.createRoute(42L)
        )
    }
}
