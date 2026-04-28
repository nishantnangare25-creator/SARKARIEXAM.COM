package com.sarkari.exam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.sarkari.exam.data.AppConstants
import com.sarkari.exam.ui.BananiAppMain
import com.sarkari.exam.ui.theme.SarkariExamTheme

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
            SarkariExamTheme {
                BananiAppMain()
            }
        }
    }
}
