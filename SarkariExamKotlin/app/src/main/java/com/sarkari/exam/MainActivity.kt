package com.sarkari.exam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.sarkari.exam.ui.BananiAppMain

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            val options = FirebaseOptions.Builder()
                .setApiKey(AppConstants.FIREBASE_API_KEY)
                .setApplicationId(AppConstants.FIREBASE_APP_ID)
                .setProjectId(AppConstants.FIREBASE_PROJECT_ID)
                .setStorageBucket(AppConstants.FIREBASE_STORAGE_BUCKET)
                .setGcmSenderId(AppConstants.FIREBASE_MESSAGING_SENDER_ID)
                .build()

            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this, options)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        setContent {
            Surface(modifier = Modifier.fillMaxSize()) {
                BananiAppMain()
            }
        }
    }
}
