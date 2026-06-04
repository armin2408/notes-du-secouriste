package com.notesdusecouriste.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.notesdusecouriste.app.navigation.AppNavHost
import com.notesdusecouriste.app.ui.theme.AppThemeHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppThemeHost {
                AppNavHost()
            }
        }
    }
}
