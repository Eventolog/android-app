import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.eventology.R
import com.example.eventology.constants.UserTypes
import com.example.eventology.databinding.FragmentEventDetailPageBinding
import com.example.eventology.utils.DateUtils
import com.example.eventology.data.models.Event
import com.example.eventology.data.services.ApiServiceProvider
import com.example.eventology.fragments.AuthenticatedLayoutFragment
import com.example.eventology.fragments.PageFragments
import com.example.eventology.fragments.SelectSeatFragment
import com.example.eventology.utils.ImageUtilityClass

class EventDetailPageFragment(
    private val event: Event,
    private val authenticatedLayoutFragment: AuthenticatedLayoutFragment
) : PageFragments(10, authenticatedLayoutFragment) {

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
        val readableDate = DateUtils.toReadableDate(event.startTime)
        val readableDuration = DateUtils.getReadableDuration(event.startTime, event.endTime)
        val durationTxt = context?.getString(R.string.duration)
        binding.eventDetailTime.text = "$readableDate · $durationTxt: $readableDuration"

        // user role
        val role = ApiServiceProvider.getDataService().getUser()?.type ?: UserTypes.ORGANIZER

        if (role == UserTypes.NORMAL) {
            binding.actionButton.setText(R.string.buyTicket)
            binding.actionButton.setOnClickListener {
                authenticatedLayoutFragment.loadPage(
                    SelectSeatFragment(authenticatedLayoutFragment, event.id)
                )
            }
        } else if (role == UserTypes.ORGANIZER) {
            binding.actionButton.setText(R.string.updateEventImage)
            binding.actionButton.setOnClickListener {
                val options = arrayOf("Take Picture", "Upload Image")
                AlertDialog.Builder(requireContext())
                    .setTitle("Select Option")
                    .setItems(options) { _, which ->
                        when (which) {
                            0 -> ImageUtilityClass.openCamera(this)
                            1 -> ImageUtilityClass.openGallery(this)
                        }
                    }
                    .show()
            }
        }

        // Go back logic
        binding.root.findViewById<ImageView>(R.id.goBackBtn).setOnClickListener {
            authenticatedLayoutFragment.goBack()
        }

        // TODO: handle other event details like image
        // Example: Load event image if available
        // binding.eventDetailImage.setImageResource(event.imageResId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}