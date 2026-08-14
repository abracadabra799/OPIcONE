package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.myapplication.di.AppContainer
import com.example.myapplication.ui.navigation.AppNavGraph
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val appContainer by lazy { AppContainer(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AppNavGraph(appContainer = appContainer)
            }
        }
    }
}
