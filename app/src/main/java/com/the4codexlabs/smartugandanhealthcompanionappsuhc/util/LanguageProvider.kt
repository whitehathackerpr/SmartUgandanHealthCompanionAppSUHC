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
    SWAHILI("sw", "Swahili"),
    RUNYANKOLE("nyn", "Runyankole")
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
        AppLanguage.SWAHILI to "Msaidizi wa Afya wa Uganda",
        AppLanguage.RUNYANKOLE to "Omubeezi w'Obulamu mu Uganda"
    )
    
    // Screen titles
    val dashboard = mapOf(
        AppLanguage.ENGLISH to "Dashboard",
        AppLanguage.LUGANDA to "Ekitundu Ekikulu",
        AppLanguage.SWAHILI to "Dashibodi",
        AppLanguage.RUNYANKOLE to "Ahabw'Obulamu"
    )
    
    val healthRecords = mapOf(
        AppLanguage.ENGLISH to "Health Records",
        AppLanguage.LUGANDA to "Ebiwandiiko by'obulamu",
        AppLanguage.SWAHILI to "Rekodi za Afya",
        AppLanguage.RUNYANKOLE to "Ebyandikire by'Obulamu"
    )
    
    val medicationReminders = mapOf(
        AppLanguage.ENGLISH to "Medication Reminders",
        AppLanguage.LUGANDA to "Okujjukiza eddagala",
        AppLanguage.SWAHILI to "Vikumbusho vya Dawa",
        AppLanguage.RUNYANKOLE to "Okwibuka Edagala"
    )
    
    val symptomTracker = mapOf(
        AppLanguage.ENGLISH to "Symptom Tracker",
        AppLanguage.LUGANDA to "Okugoberera obubonero",
        AppLanguage.SWAHILI to "Kifuatiliaji cha Dalili",
        AppLanguage.RUNYANKOLE to "Okuronda Obubonezi"
    )
    
    val healthEducation = mapOf(
        AppLanguage.ENGLISH to "Health Education",
        AppLanguage.LUGANDA to "Okusomesa ku by'obulamu",
        AppLanguage.SWAHILI to "Elimu ya Afya",
        AppLanguage.RUNYANKOLE to "Elimu y'Obulamu"
    )
    
    val community = mapOf(
        AppLanguage.ENGLISH to "Community",
        AppLanguage.LUGANDA to "Ekibiina",
        AppLanguage.SWAHILI to "Jamii",
        AppLanguage.RUNYANKOLE to "Abantu"
    )
    
    val wearables = mapOf(
        AppLanguage.ENGLISH to "Wearables",
        AppLanguage.LUGANDA to "Ebyambalo by'obulamu",
        AppLanguage.SWAHILI to "Vifaa vya Kuvaa",
        AppLanguage.RUNYANKOLE to "Ebikozire"
    )
    
    val settings = mapOf(
        AppLanguage.ENGLISH to "Settings",
        AppLanguage.LUGANDA to "Entegeka",
        AppLanguage.SWAHILI to "Mipangilio",
        AppLanguage.RUNYANKOLE to "Entegeka"
    )
    
    val profile = mapOf(
        AppLanguage.ENGLISH to "Profile",
        AppLanguage.LUGANDA to "Ebikwata ku ggwe",
        AppLanguage.SWAHILI to "Wasifu",
        AppLanguage.RUNYANKOLE to "Ebikukwatako"
    )
    
    // Actions
    val add = mapOf(
        AppLanguage.ENGLISH to "Add",
        AppLanguage.LUGANDA to "Yongerako",
        AppLanguage.SWAHILI to "Ongeza",
        AppLanguage.RUNYANKOLE to "Yongeraho"
    )
    
    val edit = mapOf(
        AppLanguage.ENGLISH to "Edit",
        AppLanguage.LUGANDA to "Kyusa",
        AppLanguage.SWAHILI to "Hariri",
        AppLanguage.RUNYANKOLE to "Hindura"
    )
    
    val delete = mapOf(
        AppLanguage.ENGLISH to "Delete",
        AppLanguage.LUGANDA to "Sanyizaawo",
        AppLanguage.SWAHILI to "Futa",
        AppLanguage.RUNYANKOLE to "Sanguraho"
    )
    
    val save = mapOf(
        AppLanguage.ENGLISH to "Save",
        AppLanguage.LUGANDA to "Tereka",
        AppLanguage.SWAHILI to "Hifadhi",
        AppLanguage.RUNYANKOLE to "Bika"
    )
    
    val cancel = mapOf(
        AppLanguage.ENGLISH to "Cancel",
        AppLanguage.LUGANDA to "Sazaamu",
        AppLanguage.SWAHILI to "Ghairi",
        AppLanguage.RUNYANKOLE to "Reka"
    )
    
    // Settings
    val theme = mapOf(
        AppLanguage.ENGLISH to "Theme",
        AppLanguage.LUGANDA to "Endabika",
        AppLanguage.SWAHILI to "Mandhari",
        AppLanguage.RUNYANKOLE to "Endabika"
    )
    
    val language = mapOf(
        AppLanguage.ENGLISH to "Language",
        AppLanguage.LUGANDA to "Olulimi",
        AppLanguage.SWAHILI to "Lugha",
        AppLanguage.RUNYANKOLE to "Orurimi"
    )
    
    val notifications = mapOf(
        AppLanguage.ENGLISH to "Notifications",
        AppLanguage.LUGANDA to "Obubaka",
        AppLanguage.SWAHILI to "Arifa",
        AppLanguage.RUNYANKOLE to "Obubaka"
    )
    
    val darkMode = mapOf(
        AppLanguage.ENGLISH to "Dark Mode",
        AppLanguage.LUGANDA to "Enzikiza",
        AppLanguage.SWAHILI to "Hali ya Giza",
        AppLanguage.RUNYANKOLE to "Ekizikiza"
    )
    
    val lightMode = mapOf(
        AppLanguage.ENGLISH to "Light Mode",
        AppLanguage.LUGANDA to "Ekitangaala",
        AppLanguage.SWAHILI to "Hali ya Mwanga",
        AppLanguage.RUNYANKOLE to "Ekitangaala"
    )
    
    val systemDefault = mapOf(
        AppLanguage.ENGLISH to "System Default",
        AppLanguage.LUGANDA to "Entegeka ya kompyuta",
        AppLanguage.SWAHILI to "Chaguo-Msingi cha Mfumo",
        AppLanguage.RUNYANKOLE to "Entegeka y'Ekikora"
    )
    
    val edit_profile = mapOf(
        AppLanguage.ENGLISH to "Edit Profile",
        AppLanguage.LUGANDA to "Kyusa Ebikwata ku ggwe",
        AppLanguage.SWAHILI to "Hariri Wasifu",
        AppLanguage.RUNYANKOLE to "Hindura Ebikukwatako"
    )
    
    val name = mapOf(
        AppLanguage.ENGLISH to "Name",
        AppLanguage.LUGANDA to "Linnya",
        AppLanguage.SWAHILI to "Jina",
        AppLanguage.RUNYANKOLE to "Eizina"
    )
    
    val phone_number = mapOf(
        AppLanguage.ENGLISH to "Phone Number",
        AppLanguage.LUGANDA to "Namba y'essimu",
        AppLanguage.SWAHILI to "Namba ya Simu",
        AppLanguage.RUNYANKOLE to "Enamba y'Esimu"
    )
    
    val email = mapOf(
        AppLanguage.ENGLISH to "Email",
        AppLanguage.LUGANDA to "Email",
        AppLanguage.SWAHILI to "Barua Pepe",
        AppLanguage.RUNYANKOLE to "Email"
    )
    
    val tap_to_change_photo = mapOf(
        AppLanguage.ENGLISH to "Tap to change photo",
        AppLanguage.LUGANDA to "Tandika okukyusa foto",
        AppLanguage.SWAHILI to "Gusa kubadilisha picha",
        AppLanguage.RUNYANKOLE to "Reka okuhindura ifoto"
    )
    
    val error = mapOf(
        AppLanguage.ENGLISH to "Error",
        AppLanguage.LUGANDA to "Kizibu",
        AppLanguage.SWAHILI to "Hitilafu",
        AppLanguage.RUNYANKOLE to "Obuhungu"
    )
    
    val ok = mapOf(
        AppLanguage.ENGLISH to "OK",
        AppLanguage.LUGANDA to "OK",
        AppLanguage.SWAHILI to "OK",
        AppLanguage.RUNYANKOLE to "OK"
    )
    
    val logout = mapOf(
        AppLanguage.ENGLISH to "Logout",
        AppLanguage.LUGANDA to "Fuluma",
        AppLanguage.SWAHILI to "Toka",
        AppLanguage.RUNYANKOLE to "Reka"
    )
    
    val logout_confirmation = mapOf(
        AppLanguage.ENGLISH to "Are you sure you want to logout?",
        AppLanguage.LUGANDA to "Okakasa ko oyagala okufuluma?",
        AppLanguage.SWAHILI to "Una uhakika unataka kutoka?",
        AppLanguage.RUNYANKOLE to "Okakasa ko oyagala okureka?"
    )
    
    val privacy = mapOf(
        AppLanguage.ENGLISH to "Privacy",
        AppLanguage.LUGANDA to "Obwesiimba",
        AppLanguage.SWAHILI to "Faragha",
        AppLanguage.RUNYANKOLE to "Obwesiimba"
    )
}

@Composable
fun getTranslation(stringMap: Map<AppLanguage, String>): String {
    val language = currentAppLanguage()
    return stringMap[language] ?: stringMap[AppLanguage.ENGLISH] ?: ""
} 