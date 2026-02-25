package com.example.telegramstyle.newsapp.data.remote.firebase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import com.example.telegramstyle.newsapp.domain.common.AppError
import com.example.telegramstyle.newsapp.domain.common.Result
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileImageManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val storage: FirebaseStorage
) {
    companion object {
        const val MAX_SIZE_BYTES = 2 * 1024 * 1024L // 2 MB
        const val TARGET_SIZE = 512
        private val AVATAR_COLORS = listOf(
            0xFF1ABC9C.toInt(), 0xFF2ECC71.toInt(), 0xFF3498DB.toInt(),
            0xFF9B59B6.toInt(), 0xFFE74C3C.toInt(), 0xFFF39C12.toInt()
        )
    }
    
    fun validateImageSize(uri: Uri): Flow<Result<Boolean>> = flow {
        emit(Result.Loading)
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val size = inputStream?.available()?.toLong() ?: 0L
            inputStream?.close()
            emit(Result.Success(size <= MAX_SIZE_BYTES))
        } catch (e: Exception) {
            emit(Result.Error(AppError.ValidationError("Görsel boyutu kontrol edilemedi")))
        }
    }
    
    fun uploadProfileImage(userId: String, uri: Uri): Flow<Result<String>> = flow {
        emit(Result.Loading)
        try {
            val ref = storage.reference.child("profile_images/$userId.jpg")
            ref.putFile(uri).await()
            val downloadUrl = ref.downloadUrl.await().toString()
            emit(Result.Success(downloadUrl))
        } catch (e: Exception) {
            emit(Result.Error(AppError.NetworkError("Görsel yüklenemedi: ${e.message}")))
        }
    }
    
    fun resizeImage(bitmap: Bitmap): Bitmap {
        if (bitmap.width <= TARGET_SIZE && bitmap.height <= TARGET_SIZE) return bitmap
        val scale = TARGET_SIZE.toFloat() / maxOf(bitmap.width, bitmap.height)
        val newWidth = (bitmap.width * scale).toInt()
        val newHeight = (bitmap.height * scale).toInt()
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
    
    fun generateAvatar(displayName: String): Bitmap {
        val initials = displayName.split(" ")
            .take(2)
            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
            .joinToString("")
            .ifEmpty { "?" }
        
        val bitmap = Bitmap.createBitmap(TARGET_SIZE, TARGET_SIZE, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val bgColor = AVATAR_COLORS[displayName.hashCode().mod(AVATAR_COLORS.size).let { if (it < 0) it + AVATAR_COLORS.size else it }]
        
        canvas.drawColor(bgColor)
        
        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = TARGET_SIZE * 0.4f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        
        val yPos = (canvas.height / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2f)
        canvas.drawText(initials, canvas.width / 2f, yPos, textPaint)
        
        return bitmap
    }
    
    fun bitmapToByteArray(bitmap: Bitmap, quality: Int = 85): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        return stream.toByteArray()
    }
}
