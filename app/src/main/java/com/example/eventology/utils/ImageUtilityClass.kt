package com.example.eventology.utils

import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

object ImageUtilityClass {

    private var cameraLauncher: ActivityResultLauncher<Uri>? = null
    private var galleryLauncher: ActivityResultLauncher<String>? = null
    private var imageUri: Uri? = null

    fun setupLaunchers(
        fragment: Fragment,
        onImageSelected: (Uri) -> Unit
    ) {
        // Launcher to take picture
        cameraLauncher = fragment.registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && imageUri != null) {
                onImageSelected(imageUri!!)
            }
        }

        // Launcher to pick image from gallery
        galleryLauncher = fragment.registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                onImageSelected(uri)
            }
        }
    }

    fun openCamera(fragment: Fragment) {
        imageUri = createImageUri(fragment)
        imageUri?.let { cameraLauncher?.launch(it) }
    }

    fun openGallery(fragment: Fragment) {
        galleryLauncher?.launch("image/*")
    }

    private fun createImageUri(fragment: Fragment): Uri? {
        val contentResolver = fragment.requireContext().contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "new_image_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    }
}
