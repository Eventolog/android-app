package com.example.eventology.fragments

import android.view.View
import android.os.Bundle
import android.app.Activity
import android.view.ViewGroup
import android.content.Intent
import android.graphics.Bitmap
import android.app.AlertDialog
import com.example.eventology.R
import kotlinx.coroutines.launch
import android.view.LayoutInflater
import android.annotation.SuppressLint
import android.provider.MediaStore
import android.widget.VideoView
import androidx.lifecycle.lifecycleScope
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
class EventDetailPageFragment(
    private val event: Event,
    authenticatedLayoutFragment: AuthenticatedLayoutFragment
) : PageFragments(10, authenticatedLayoutFragment) {

    private var _binding: FragmentEventDetailPageBinding? = null
    private val binding get() = _binding!!
    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>

    private lateinit var pickVideoLauncher: ActivityResultLauncher<Intent>
    private lateinit var videoView: VideoView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventDetailPageBinding.inflate(inflater, container, false)
        videoView = binding.root.findViewById(R.id.videoView)
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
        initializeVideoPicker()

        // Primer: comprovem si hi ha un vídeo guardat
        val videoFile = EventFileUtils.getSavedVideoFile(requireContext(), event.id.toString())
        if (videoFile != null) {
            binding.imageView.visibility = View.GONE
            videoView.visibility = View.VISIBLE
            videoView.setVideoPath(videoFile.absolutePath)

            videoView.setOnCompletionListener {
                videoView.start()
            }
            videoView.start()
        } else {
            // Si no hi ha vídeo, carreguem la imatge si existeix
            val storedImage = EventFileUtils.loadEventImage(requireContext(), event.id.toString())
            if (storedImage != null) {
                binding.imageView.visibility = View.VISIBLE
                videoView.visibility = View.GONE
                binding.imageView.setImageBitmap(storedImage)
            } else {
                replaceImageToDefault()
            }
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
        if (role == UserTypes.NORMAL) {
            binding.actionButton.setText(R.string.buyTicket)

            // Verifica si hi ha seients disponibles
            lifecycleScope.launch {
                val seats = ApiServiceProvider.getDataService().getFreeSeats(event.id)

                if (seats.isEmpty()) {
                    binding.actionButton.isEnabled = false
                    binding.actionButton.setText(R.string.no_tickets_available)
                    binding.actionButton.setBackgroundColor(
                        ContextCompat.getColor(requireContext(), android.R.color.darker_gray)
                    )
                } else {
                    binding.actionButton.setOnClickListener {
                        getAuthenticatedLayoutFragment().loadPage(
                            SeatSelectionFragment(event, getAuthenticatedLayoutFragment())
                        )
                    }
                }
            }
        } else if (role == UserTypes.ORGANIZER) {
            binding.actionButton.setText(R.string.updateEventImage)
            binding.actionButton.setOnClickListener {
                val options = arrayOf(
                    getString(R.string.option_take_picture),
                    getString(R.string.option_upload_image),
                    getString(R.string.option_take_video),
                    getString(R.string.option_remove_image),
                    getString(R.string.option_remove_video)
                )
                AlertDialog.Builder(requireContext())
                    .setTitle("Select Option")
                    .setItems(options) { _, which ->
                        when (which) {
                            0 -> replaceImageFromCamera()
                            1 -> replaceImageFromGallery()
                            2 -> replaceImageToVideo()
                            3 -> replaceImageToDefault()
                            4 -> replaceImageToDefaultVideo()
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
    private fun initializeCamera() {
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
                val uri = result.data!!.data // Check de dades
                uri?.let {
                    val bitmap = EventFileUtils.getBitmapFromUri(requireContext(), it) // Conversió a Bitmap
                    binding.imageView.setImageBitmap(bitmap)
                    EventFileUtils.saveEventImage(requireContext(), event.id.toString(), bitmap)
                }
            }
        }
    }

    private fun initializeVideoPicker() {
        pickVideoLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val uri = result.data!!.data
                uri?.let {
                    // Oculta imatge
                    binding.imageView.visibility = View.GONE

                    // Mostra el vídeo
                    videoView.visibility = View.VISIBLE
                    videoView.setVideoURI(it)

                    videoView.setOnCompletionListener {
                        videoView.start()
                    }
                    videoView.start()

                    // Opcional: desa la URI en arxiu o DB
                    EventFileUtils.saveEventVideoUri(requireContext(), event.id.toString(), it)
                }
            }
        }
    }

    /**
     * Replace image by the default
     */
    private fun replaceImageToDefault() {
        // set default image to the view
        binding.imageView.setImageDrawable(
            ContextCompat.getDrawable(requireContext(), R.drawable.default_event_deail_big_image)
        )

        // delete the event image at the FileStorage
        EventFileUtils.deleteEventImage(requireContext(), event.id.toString())
    }

    private fun replaceImageToDefaultVideo() {
        // Elimina el fitxer de vídeo si existeix
        val deleted = EventFileUtils.deleteEventVideo(requireContext(), event.id.toString())

        // Oculta el vídeo
        videoView.visibility = View.GONE
        videoView.stopPlayback()

        // Mostra la imatge per defecte
        binding.imageView.visibility = View.VISIBLE
        replaceImageToDefault()
    }

    /**
     * Opens the camera, take an image and if success load it into
     * [binding.imageView]
     */
    private fun replaceImageFromCamera() {
        CameraHelper.checkPermissionAndOpenCamera(requireActivity())
    }

    /**
     * Opens the gallery, take an image and if success load it into
     * [binding.imageView]
     */
    private fun replaceImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        pickImageLauncher.launch(intent)
    }

    private fun replaceImageToVideo() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15) // max 15"
            putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1) // low quality)
        }
        pickVideoLauncher.launch(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}