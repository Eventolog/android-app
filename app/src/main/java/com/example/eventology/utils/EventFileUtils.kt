package com.example.eventology.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object EventFileUtils {

    // Guarda bitmap en almacenamiento interno privado con nombre basado en eventId
    fun saveEventImage(context: Context, eventId: String, bitmap: Bitmap?): Boolean {
        return try {
            val file = File(context.filesDir, "event_image_$eventId.png")
            FileOutputStream(file).use { out ->
                bitmap?.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Carga el bitmap si existe, o null si no existe
    fun loadEventImage(context: Context, eventId: String): Bitmap? {
        val file = File(context.filesDir, "event_image_$eventId.png")
        return if (file.exists()) {
            BitmapFactory.decodeFile(file.absolutePath)
        } else {
            null
        }
    }

    // Elimina la imagen guardada para ese eventId (opcional)
    fun deleteEventImage(context: Context, eventId: String): Boolean {
        val file = File(context.filesDir, "event_image_$eventId.png")
        return if (file.exists()) {
            file.delete()
        } else {
            false
        }
    }

    fun getBitmapFromUri(context: Context, uri: Uri): Bitmap {
        val inputStream = context.contentResolver.openInputStream(uri)
        return BitmapFactory.decodeStream(inputStream!!)
    }

}
