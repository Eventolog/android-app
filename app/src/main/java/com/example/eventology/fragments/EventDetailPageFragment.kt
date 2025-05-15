import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.eventology.R
import com.example.eventology.constants.UserTypes
import com.example.eventology.databinding.FragmentEventDetailPageBinding
import com.example.eventology.utils.DateUtils
import com.example.eventology.data.models.Event
import com.example.eventology.data.services.ApiServiceProvider
import com.example.eventology.fragments.AuthenticatedLayoutFragment
import com.example.eventology.fragments.PageFragments
import com.example.eventology.utils.ImageUtilityClass

class EventDetailPageFragment(private val event: Event, private val authenticatedLayoutFragment: AuthenticatedLayoutFragment) : PageFragments(10, authenticatedLayoutFragment) {

    private var _binding: FragmentEventDetailPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEventDetailPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                val options = arrayOf("Take Picture", "Upload Image")
                AlertDialog.Builder(requireContext())
                    .setTitle("Select Option")
                    .setItems(options) { dialog, which ->
                        when (which) {
                            0 -> ImageUtilityClass.openCamera(this)
                            1 -> ImageUtilityClass.openGallery(this)
                        }
                    }
                    .show()
            }
        }


        // TODO: handle other event details like image
        // Example: Load event image if available
        // binding.eventDetailImage.setImageResource(event.imageResId) // If the event has an image
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

