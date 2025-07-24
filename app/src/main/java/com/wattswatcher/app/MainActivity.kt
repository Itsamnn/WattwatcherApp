package com.wattswatcher.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.wattswatcher.app.navigation.WattsWatcherNavigation
import com.wattswatcher.app.ui.theme.WattsWatcherTheme

class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val app = application as WattsWatcherApplication
        
        setContent {
            val themeMode by app.userPreferences.themeMode.collectAsState(initial = "system")
            
            WattsWatcherTheme(themeMode = themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WattsWatcherNavigation()
                }
            }
        }
    }
}