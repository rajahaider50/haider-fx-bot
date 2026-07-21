package com.family.safety.platform.by.raja.haider.ali.storage

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

object CloudinaryManager {

    private const val CLOUD_NAME = "family-safety-platform"
    private const val BASE_URL = "https://api.cloudinary.com/v1_1/$CLOUD_NAME"

    private val presets = mapOf(
        "profiles" to "family_profiles",
        "photos" to "family_photos",
        "screenshots" to "device_screenshots",
        "recordings" to "voice_recordings",
        "backups" to "device_backups"
    )

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    suspend fun uploadImage(context: Context, uri: Uri, folder: String = "profiles"): UploadResult {
        return upload(context, uri, folder, "image/*")
    }

    suspend fun uploadAudio(context: Context, uri: Uri, folder: String = "recordings"): UploadResult {
        return upload(context, uri, folder, "audio/*")
    }

    suspend fun uploadFile(context: Context, uri: Uri, folder: String = "backups"): UploadResult {
        return upload(context, uri, folder, "application/octet-stream")
    }

    private suspend fun upload(
        context: Context,
        uri: Uri,
        folder: String,
        mimeType: String
    ): UploadResult = withContext(Dispatchers.IO) {
        try {
            val file = uriToFile(context, uri, folder)
            val preset = presets[folder] ?: "ml_default"

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file", file.name,
                    file.asRequestBody(mimeType.toMediaType())
                )
                .addFormDataPart("upload_preset", preset)
                .addFormDataPart("folder", "family-safety/$folder")
                .build()

            val request = Request.Builder()
                .url("$BASE_URL/upload")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string()

            if (response.isSuccessful && body != null) {
                val json = JSONObject(body)
                UploadResult(
                    success = true,
                    url = json.optString("secure_url", ""),
                    publicId = json.optString("public_id", ""),
                    format = json.optString("format", "")
                )
            } else {
                UploadResult(success = false, error = "Upload failed: ${response.code}")
            }
        } catch (e: Exception) {
            UploadResult(success = false, error = e.message ?: "Unknown error")
        }
    }

    private fun uriToFile(context: Context, uri: Uri, folder: String): File {
        val cacheDir = File(context.cacheDir, "cloudinary_uploads/$folder")
        cacheDir.mkdirs()
        val file = File(cacheDir, "${System.currentTimeMillis()}_upload")
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    suspend fun deleteFile(publicId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("public_id", publicId)
                .build()

            val request = Request.Builder()
                .url("$BASE_URL/image/destroy")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}

data class UploadResult(
    val success: Boolean = false,
    val url: String = "",
    val publicId: String = "",
    val format: String = "",
    val error: String = ""
)
