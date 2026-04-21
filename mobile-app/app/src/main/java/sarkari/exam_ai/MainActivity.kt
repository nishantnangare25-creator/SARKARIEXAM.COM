package sarkari.exam_ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import sarkari.exam_ai.ui.navigation.AppNavigation
import sarkari.exam_ai.ui.navigation.MainScaffold
import sarkari.exam_ai.ui.theme.SarkariExamTheme
import sarkari.exam_ai.ui.viewmodels.AuthViewModel

class MainActivity : ComponentActivity() {
    // Shared AuthViewModel — single instance for the whole Activity
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SarkariExamTheme {
                val navController = androidx.navigation.compose.rememberNavController()
                AppNavigation(navController = navController, authViewModel = authViewModel)
            }
        }
    }
}
