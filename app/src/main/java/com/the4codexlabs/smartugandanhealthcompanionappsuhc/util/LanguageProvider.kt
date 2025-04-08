package com.the4codexlabs.smartugandanhealthcompanionappsuhc.util

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

enum class AppLanguage(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    LUGANDA("lg", "Luganda"),
    SWAHILI("sw", "Swahili")
}

object LanguageProvider {
    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()
    
    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
    }
    
    fun applyLanguage(context: Context) {
        val locale = Locale(_currentLanguage.value.code)
        Locale.setDefault(locale)
        
        val resources = context.resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        context.createConfigurationContext(configuration)
    }
}

@Composable
fun currentAppLanguage(): AppLanguage {
    val language by LanguageProvider.currentLanguage.collectAsState()
    return language
}

// Translation resources
object StringResources {
    // Common
    val appName = mapOf(
        AppLanguage.ENGLISH to "Smart Ugandan Health Companion",
        AppLanguage.LUGANDA to "Ekyambe kyo Obulamu mu Uganda",
        AppLanguage.SWAHILI to "Msaidizi wa Afya wa Uganda"
    )
    
    // Screen titles
    val dashboard = mapOf(
        AppLanguage.ENGLISH to "Dashboard",
        AppLanguage.LUGANDA to "Ekitundu Ekikulu",
        AppLanguage.SWAHILI to "Dashibodi"
    )
    
    val healthRecords = mapOf(
        AppLanguage.ENGLISH to "Health Records",
        AppLanguage.LUGANDA to "Ebiwandiiko by'obulamu",
        AppLanguage.SWAHILI to "Rekodi za Afya"
    )
    
    val medicationReminders = mapOf(
        AppLanguage.ENGLISH to "Medication Reminders",
        AppLanguage.LUGANDA to "Okujjukiza eddagala",
        AppLanguage.SWAHILI to "Vikumbusho vya Dawa"
    )
    
    val symptomTracker = mapOf(
        AppLanguage.ENGLISH to "Symptom Tracker",
        AppLanguage.LUGANDA to "Okugoberera obubonero",
        AppLanguage.SWAHILI to "Kifuatiliaji cha Dalili"
    )
    
    val healthEducation = mapOf(
        AppLanguage.ENGLISH to "Health Education",
        AppLanguage.LUGANDA to "Okusomesa ku by'obulamu",
        AppLanguage.SWAHILI to "Elimu ya Afya"
    )
    
    val community = mapOf(
        AppLanguage.ENGLISH to "Community",
        AppLanguage.LUGANDA to "Ekibiina",
        AppLanguage.SWAHILI to "Jamii"
    )
    
    val wearables = mapOf(
        AppLanguage.ENGLISH to "Wearables",
        AppLanguage.LUGANDA to "Ebyambalo by'obulamu",
        AppLanguage.SWAHILI to "Vifaa vya Kuvaa"
    )
    
    val settings = mapOf(
        AppLanguage.ENGLISH to "Settings",
        AppLanguage.LUGANDA to "Entegeka",
        AppLanguage.SWAHILI to "Mipangilio"
    )
    
    val profile = mapOf(
        AppLanguage.ENGLISH to "Profile",
        AppLanguage.LUGANDA to "Ebikwata ku ggwe",
        AppLanguage.SWAHILI to "Wasifu"
    )
    
    // Actions
    val add = mapOf(
        AppLanguage.ENGLISH to "Add",
        AppLanguage.LUGANDA to "Yongerako",
        AppLanguage.SWAHILI to "Ongeza"
    )
    
    val edit = mapOf(
        AppLanguage.ENGLISH to "Edit",
        AppLanguage.LUGANDA to "Kyusa",
        AppLanguage.SWAHILI to "Hariri"
    )
    
    val delete = mapOf(
        AppLanguage.ENGLISH to "Delete",
        AppLanguage.LUGANDA to "Sanyizaawo",
        AppLanguage.SWAHILI to "Futa"
    )
    
    val save = mapOf(
        AppLanguage.ENGLISH to "Save",
        AppLanguage.LUGANDA to "Tereka",
        AppLanguage.SWAHILI to "Hifadhi"
    )
    
    val cancel = mapOf(
        AppLanguage.ENGLISH to "Cancel",
        AppLanguage.LUGANDA to "Sazaamu",
        AppLanguage.SWAHILI to "Ghairi"
    )
    
    // Settings
    val theme = mapOf(
        AppLanguage.ENGLISH to "Theme",
        AppLanguage.LUGANDA to "Endabika",
        AppLanguage.SWAHILI to "Mandhari"
    )
    
    val language = mapOf(
        AppLanguage.ENGLISH to "Language",
        AppLanguage.LUGANDA to "Olulimi",
        AppLanguage.SWAHILI to "Lugha"
    )
    
    val notifications = mapOf(
        AppLanguage.ENGLISH to "Notifications",
        AppLanguage.LUGANDA to "Obubaka",
        AppLanguage.SWAHILI to "Arifa"
    )
    
    val darkMode = mapOf(
        AppLanguage.ENGLISH to "Dark Mode",
        AppLanguage.LUGANDA to "Enzikiza",
        AppLanguage.SWAHILI to "Hali ya Giza"
    )
    
    val lightMode = mapOf(
        AppLanguage.ENGLISH to "Light Mode",
        AppLanguage.LUGANDA to "Ekitangaala",
        AppLanguage.SWAHILI to "Hali ya Mwanga"
    )
    
    val systemDefault = mapOf(
        AppLanguage.ENGLISH to "System Default",
        AppLanguage.LUGANDA to "Entegeka ya kompyuta",
        AppLanguage.SWAHILI to "Chaguo-Msingi cha Mfumo"
    )
}

@Composable
fun getTranslation(stringMap: Map<AppLanguage, String>): String {
    val language = currentAppLanguage()
    return stringMap[language] ?: stringMap[AppLanguage.ENGLISH] ?: ""
} 