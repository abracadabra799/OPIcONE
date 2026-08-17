package com.example.myapplication.ui.navigation

import com.example.myapplication.data.model.PracticeCategory

sealed class Routes(val route: String) {
    data object Home : Routes("home")
    data object Favorites : Routes("favorites")
    data object Settings : Routes("settings")

    data object Practice : Routes("practice/{category}") {
        fun createRoute(category: PracticeCategory) = "practice/${category.name}"
    }

    data object SetComplete : Routes("set_complete")

    data object FavoritePractice : Routes("favorite_practice/{favoriteId}") {
        fun createRoute(favoriteId: Long) = "favorite_practice/$favoriteId"
    }

    data object NewsDetail : Routes("news_detail/{articleId}") {
        fun createRoute(articleId: String) = "news_detail/$articleId"
    }
}
