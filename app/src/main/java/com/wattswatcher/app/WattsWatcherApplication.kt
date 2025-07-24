package com.wattswatcher.app

import android.app.Application

class WattsWatcherApplication : Application() {
    
    // Simple singleton repository
    val repository by lazy { 
        com.wattswatcher.app.data.repository.WattsWatcherRepository()
    }
    
    val userPreferences by lazy {
        com.wattswatcher.app.data.preferences.UserPreferences(this)
    }
}