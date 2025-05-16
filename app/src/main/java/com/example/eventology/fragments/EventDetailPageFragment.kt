package com.example.eventology.fragments

import android.view.View
import android.os.Bundle
import android.app.Activity
import android.view.ViewGroup
import android.content.Intent
import android.graphics.Bitmap
import android.app.AlertDialog
import com.example.eventology.R
import android.view.LayoutInflater
import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import com.example.eventology.utils.DateUtils
import com.example.eventology.data.models.Event
import com.example.eventology.utils.CameraHelper
import com.example.eventology.constants.UserTypes
import com.example.eventology.utils.EventFileUtils
import androidx.activity.result.ActivityResultLauncher
import com.example.eventology.data.services.ApiServiceProvider
import androidx.activity.result.contract.ActivityResultContracts
import com.example.eventology.databinding.FragmentEventDetailPageBinding

/**
 * This fragment show the detail of an event, for the organizer it allows to update its image
 * and for the normal user it allows to buy event tickets
 */
class EventDetailPageFragment(private val event: Event, authenticatedLayoutFragment: AuthenticatedLayoutFragment) : PageFragments(10, authenticatedLayoutFragment) {

    private var _binding: FragmentEventDetailPageBinding? = null
    private val binding get() = _binding!!
    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventDetailPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Fills the data of the fragment layout with event details, also
     * adds button login depending of the [UserTypes] of the authenticated user
     */
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeCamera()
        initializeGalleryPicker()

        // load event image if exists
        val storedImage = EventFileUtils.loadEventImage(requireContext(), event.id.toString())
        if (storedImage != null) {
            binding.imageView.setImageBitmap(storedImage)
        } else {
            replaceImageToDefault()
        }

        // name
        binding.eventDetailName.text = event.name

        // desc
        binding.eventDetailDescription.text = event.description

        // date and duration
        val readableDate = DateUtils.toReadableDate(event.startTime)
        val readableDuration = DateUtils.getReadableDuration(event.startTime, event.endTime)
        val durationTxt = context?.getString(R.string.duration)
        binding.eventDetailTime.text = "$readableDate · ${durationTxt}: $readableDuration"

        // bottom text and redirection depend of the user role
        val role = ApiServiceProvider.getDataService().getUser()?.type ?: UserTypes.ORGANIZER
        if(role == UserTypes.NORMAL){
            binding.actionButton.setText(R.string.buyTicket)
        }else if (role == UserTypes.ORGANIZER) {


            binding.actionButton.setText(R.string.updateEventImage)
            binding.actionButton.setOnClickListener {
                val options = arrayOf(
                    getString(R.string.option_take_picture),
                    getString(R.string.option_upload_image),
                    getString(R.string.option_remove_image)
                )
                AlertDialog.Builder(requireContext())
                    .setTitle("Select Option")
                    .setItems(options) { _, which ->
                        when (which) {
                            0 -> replaceImageFromCamera()
                            1 -> replaceImageFromGallery()
                            2 -> replaceImageToDefault()
                        }
                    }
                    .show()
            }
        }
    }

    /**
     * Initialize the takePictureLauncher to listen on
     * success taken image [CameraHelper.checkPermissionAndOpenCamera]
     */
    private fun initializeCamera(){
        // initialize camera
        takePictureLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as? Bitmap
                binding.imageView.setImageBitmap(imageBitmap)
                EventFileUtils.saveEventImage(requireContext(), event.id.toString(), imageBitmap)
            }
        }

        // Setup helper with launcher
        CameraHelper.setup(takePictureLauncher)
    }

    /**
     * Initializes the gallery picker launcher using Android's Activity Result API.
     *
     * This launcher opens the device's gallery and allows the user to pick an image.
     * When the user selects an image, the function:
     * - Retrieves the image URI from the intent result.
     * - Converts it into a Bitmap using [EventFileUtils.getBitmapFromUri].
     * - Displays it in [binding.imageView].
     * - Saves the image locally using [EventFileUtils.saveEventImage] associated with the current event ID.
     *
     * This function must be called inside or before `onViewCreated`.
     */
    private fun initializeGalleryPicker() {
        pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val uri = result.data!!.data
                uri?.let {
                    val bitmap = EventFileUtils.getBitmapFromUri(requireContext(), it)
                    binding.imageView.setImageBitmap(bitmap)
                    EventFileUtils.saveEventImage(requireContext(), event.id.toString(), bitmap)
                }
            }
        }
    }

    /**
     * Replace image by the default
     */
    private fun replaceImageToDefault(){
        // set default image to the view
        binding.imageView.setImageDrawable(
            ContextCompat.getDrawable(requireContext(), R.drawable.default_event_deail_big_image)
        )

        // delete the event image at the FileStorage
        EventFileUtils.deleteEventImage(requireContext(), event.id.toString())
    }

    /**
     * Opens the camera, take an image and if success load it into
     * [binding.imageView]
     */
    private fun replaceImageFromCamera(){
        CameraHelper.checkPermissionAndOpenCamera(requireActivity())
    }

    /**
     * Opens the gallery, take an image and if success load it into
     * [binding.imageView]
     */
    private fun replaceImageFromGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        pickImageLauncher.launch(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

