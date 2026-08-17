package com.example.myapplication.data.remote

import com.example.myapplication.data.local.DailyWorldNewsBank
import com.example.myapplication.data.model.WorldNewsArticle

interface WorldNewsRepository {
    fun getTodayNews(): List<WorldNewsArticle>
    fun getNewsById(id: String): WorldNewsArticle?
}

class DefaultWorldNewsRepository(
    private val onDeviceNewsAnalyzer: OnDeviceNewsAnalyzer? = null
) : WorldNewsRepository {
    override fun getTodayNews(): List<WorldNewsArticle> = DailyWorldNewsBank.getNews()
    override fun getNewsById(id: String): WorldNewsArticle? = DailyWorldNewsBank.getNewsById(id)
}
