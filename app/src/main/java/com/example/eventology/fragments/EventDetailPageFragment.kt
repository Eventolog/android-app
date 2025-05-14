import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.eventology.R
import com.example.eventology.databinding.FragmentEventDetailPageBinding
import com.example.eventology.utils.DateUtils
import com.example.eventology.data.models.Event
import com.example.eventology.fragments.AuthenticatedLayoutFragment
import com.example.eventology.fragments.PageFragments

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

        // Display event details
        binding.eventDetailName.text = event.name
        binding.eventDetailDescription.text = event.description
        var readeableDate = DateUtils.toReadableDate(event.startTime);
        var readeableDuration = DateUtils.getReadableDuration(event.startTime, event.endTime);
        var durationTxt = context?.getString(R.string.duration)
        binding.eventDetailTime.text = "${readeableDate} · ${durationTxt}: ${readeableDuration}"

        // Optionally handle other event details like image or location
        // Example: Load event image if available
        // binding.eventDetailImage.setImageResource(event.imageResId) // If the event has an image
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

