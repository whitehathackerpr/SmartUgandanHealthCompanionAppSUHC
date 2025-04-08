package com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.profile

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.data.model.UserProfile
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.data.repository.UserRepository
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.util.AppLanguage
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.util.StringResources
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.util.getTranslation
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRMedicalIDScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userRepository = remember { UserRepository() }
    var userProfile by remember { mutableStateOf<UserProfile?>(null) }
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showErrorDialog by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        userRepository.getCurrentUser().collect { profile ->
            userProfile = profile
            if (profile != null) {
                generateQRCode(profile)
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("QR Medical ID") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Your Medical ID QR Code",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            if (qrBitmap != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            bitmap = qrBitmap!!.asImageBitmap(),
                            contentDescription = "QR Code",
                            modifier = Modifier
                                .size(250.dp)
                                .padding(16.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Scan this QR code to access your medical information in case of emergency",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = {
                                    scope.launch {
                                        saveQRCodeToGallery(context, qrBitmap!!)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Save")
                            }
                            
                            Button(
                                onClick = {
                                    scope.launch {
                                        shareQRCode(context, qrBitmap!!)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Share")
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Medical Information",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                userProfile?.let { profile ->
                    MedicalInfoCard(
                        title = "Personal Information",
                        items = listOf(
                            "Name" to profile.name,
                            "Age" to "${profile.age} years",
                            "Gender" to profile.gender,
                            "Blood Type" to profile.bloodType
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (profile.allergies.isNotEmpty()) {
                        MedicalInfoCard(
                            title = "Allergies",
                            items = profile.allergies.map { "Allergy" to it }
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    if (profile.medicalConditions.isNotEmpty()) {
                        MedicalInfoCard(
                            title = "Medical Conditions",
                            items = profile.medicalConditions.map { "Condition" to it }
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    if (profile.medications.isNotEmpty()) {
                        MedicalInfoCard(
                            title = "Current Medications",
                            items = profile.medications.map { "Medication" to it }
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    if (profile.emergencyContacts.isNotEmpty()) {
                        MedicalInfoCard(
                            title = "Emergency Contacts",
                            items = profile.emergencyContacts.map { "Contact" to it }
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
        
        // Error Dialog
        showErrorDialog?.let { error ->
            AlertDialog(
                onDismissRequest = { showErrorDialog = null },
                title = { Text("Error") },
                text = { Text(error) },
                confirmButton = {
                    TextButton(onClick = { showErrorDialog = null }) {
                        Text("OK")
                    }
                }
            )
        }
        
        // Success Dialog
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                title = { Text("Success") },
                text = { Text("QR code saved to gallery") },
                confirmButton = {
                    TextButton(onClick = { showSuccessDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
fun MedicalInfoCard(
    title: String,
    items: List<Pair<String, String>>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            items.forEach { (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

private fun generateQRCode(userProfile: UserProfile): Bitmap {
    // Create a JSON object with the user's medical information
    val jsonObject = JSONObject().apply {
        put("name", userProfile.name)
        put("age", userProfile.age)
        put("gender", userProfile.gender)
        put("bloodType", userProfile.bloodType)
        put("allergies", JSONObject().apply {
            userProfile.allergies.forEachIndexed { index, allergy ->
                put("allergy$index", allergy)
            }
        })
        put("medicalConditions", JSONObject().apply {
            userProfile.medicalConditions.forEachIndexed { index, condition ->
                put("condition$index", condition)
            }
        })
        put("medications", JSONObject().apply {
            userProfile.medications.forEachIndexed { index, medication ->
                put("medication$index", medication)
            }
        })
        put("emergencyContacts", JSONObject().apply {
            userProfile.emergencyContacts.forEachIndexed { index, contact ->
                put("contact$index", contact)
            }
        })
    }
    
    val jsonString = jsonObject.toString()
    
    // Generate QR code
    val hints = HashMap<EncodeHintType, Any>()
    hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
    hints[EncodeHintType.MARGIN] = 1
    
    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(jsonString, BarcodeFormat.QR_CODE, 512, 512, hints)
    
    val width = bitMatrix.width
    val height = bitMatrix.height
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    
    for (x in 0 until width) {
        for (y in 0 until height) {
            bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.Black.toArgb() else Color.White.toArgb())
        }
    }
    
    return bitmap
}

private suspend fun saveQRCodeToGallery(context: android.content.Context, bitmap: Bitmap) {
    try {
        val filename = "SUHC_Medical_ID_${System.currentTimeMillis()}.png"
        val outputStream: FileOutputStream
        val imageFile = File(context.getExternalFilesDir(null), filename)
        
        outputStream = FileOutputStream(imageFile)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        
        // Add to gallery
        val values = android.content.ContentValues().apply {
            put(android.provider.MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(android.provider.MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(android.provider.MediaStore.Images.Media.RELATIVE_PATH, "Pictures/SUHC")
        }
        
        val uri = context.contentResolver.insert(
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )
        
        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private suspend fun shareQRCode(context: android.content.Context, bitmap: Bitmap) {
    try {
        val filename = "SUHC_Medical_ID_${System.currentTimeMillis()}.png"
        val imageFile = File(context.cacheDir, filename)
        
        FileOutputStream(imageFile).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }
        
        val uri = androidx.core.content.FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
        
        val shareIntent = android.content.Intent().apply {
            action = android.content.Intent.ACTION_SEND
            putExtra(android.content.Intent.EXTRA_STREAM, uri)
            type = "image/png"
            addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        context.startActivity(android.content.Intent.createChooser(shareIntent, "Share Medical ID"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
} 