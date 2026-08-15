package com.example.myapplication

import android.app.Application
import com.example.myapplication.di.AppContainer

class OpicApplication : Application() {
    private val containerOwner = ProcessAppContainerOwner {
        AppContainer(this)
    }

    val appContainer: AppContainer
        get() = containerOwner.appContainer
}

internal class ProcessAppContainerOwner<T>(factory: () -> T) {
    val appContainer: T by lazy(factory)
}
