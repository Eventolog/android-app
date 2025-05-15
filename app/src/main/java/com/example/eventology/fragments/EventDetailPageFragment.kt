import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.eventology.R
import com.example.eventology.constants.UserTypes
import com.example.eventology.databinding.FragmentEventDetailPageBinding
import com.example.eventology.utils.DateUtils
import com.example.eventology.data.models.Event
import com.example.eventology.data.services.ApiServiceProvider
import com.example.eventology.fragments.AuthenticatedLayoutFragment
import com.example.eventology.fragments.PageFragments
import com.example.eventology.utils.CameraHelper
import com.example.eventology.utils.EventFileUtils

/**
 * This fragment show the detail of an event, for the organizer it allows to update its image
 * and for the normal user it allows to buy event tickets
 */
class EventDetailPageFragment(private val event: Event, private val authenticatedLayoutFragment: AuthenticatedLayoutFragment) : PageFragments(10, authenticatedLayoutFragment) {

    private var _binding: FragmentEventDetailPageBinding? = null
    private val binding get() = _binding!!
    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEventDetailPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Fills the data of the fragment layout with event details, also
     * adds button login depending of the [UserTypes] of the authenticated user
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       initializeCamera()

        // load event image if exists
        val storedImage = EventFileUtils.loadEventImage(requireContext(), event.id.toString())
        if (storedImage != null) {
            binding.imageView.setImageBitmap(storedImage)
        } else {
            replaceImageToDeafult()
        }

        // name
        binding.eventDetailName.text = event.name

        // desc
        binding.eventDetailDescription.text = event.description

        // date and duration
        var readeableDate = DateUtils.toReadableDate(event.startTime);
        var readeableDuration = DateUtils.getReadableDuration(event.startTime, event.endTime);
        var durationTxt = context?.getString(R.string.duration)
        binding.eventDetailTime.text = "${readeableDate} · ${durationTxt}: ${readeableDuration}"

        // bottom text and redirection depend of the user role
        var role = ApiServiceProvider.getDataService().getUser()?.type ?: UserTypes.ORGANIZER
        if(role.equals(UserTypes.NORMAL)){
            binding.actionButton.setText(R.string.buyTicket)
        }else if (role.equals(UserTypes.ORGANIZER)) {


            binding.actionButton.setText(R.string.updateEventImage)
            binding.actionButton.setOnClickListener {
                val options = arrayOf("Take Picture", "Upload Image", "Remove Image")
                AlertDialog.Builder(requireContext())
                    .setTitle("Select Option")
                    .setItems(options) { dialog, which ->
                        when (which) {
                            0 -> replaceImageFromCamera()
                            1 -> replaceImageFromGallery()
                            2 -> replaceImageToDeafult()
                        }
                    }
                    .show()
            }
        }


    }

    /**
     * Initalize the takePictureLauncher to listen on
     * succes taken image [ameraHelper.checkPermissionAndOpenCamera]
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
     * Replace image by the default
     */
    private fun replaceImageToDeafult(){
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

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

