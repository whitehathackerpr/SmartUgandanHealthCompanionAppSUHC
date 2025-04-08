package com.the4codexlabs.smartugandanhealthcompanionappsuhc

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.util.LanguageProvider

class SmartUgandanHealthCompanionApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        
        // Configure Firestore settings
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build()
        
        FirebaseFirestore.getInstance().firestoreSettings = settings
        
        // Apply current language settings
        LanguageProvider.applyLanguage(this)
    }
    
    override fun attachBaseContext(base: Context) {
        // Apply language settings to base context if needed
        super.attachBaseContext(base)
    }
    
    companion object {
        val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
        val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    }
} 