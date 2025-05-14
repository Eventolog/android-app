package com.example.eventology.fragments

import EventDetailPageFragment
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventology.adapters.EventsAdapter
import com.example.eventology.data.models.Event
import com.example.eventology.data.services.ApiServiceProvider
import com.example.eventology.databinding.FragmentPageEventsListBinding
import kotlinx.coroutines.launch

/**
 * [EventsListPageFragment] is a [Fragment] responsible for displaying a list of events.
 *
 * It fetches the data asynchronously using a coroutine and populates a RecyclerView
 * with the list of events via [EventsAdapter].
 *
 * This fragment uses ViewBinding via [FragmentPageEventsListBinding] to interact with its layout.
 *
 * @property authenticatedLayoutFragment fragment used to changes page from this page
 */
class EventsListPageFragment(private val authenticatedLayoutFragment: AuthenticatedLayoutFragment) : PageFragments(1, authenticatedLayoutFragment) {

    // Backing property for view binding to avoid memory leaks
    private var _binding: FragmentPageEventsListBinding? = null

    /**
     * Non-null access to the binding instance.
     * Only valid between [onCreateView] and [onDestroyView].
     */
    private val binding get() = _binding!!

    /**
     * Called to have the fragment instantiate its user interface view.
     * Sets up the binding to the layout using ViewBinding.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views.
     * @param container The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState Previous state, if available.
     * @return The root view of the binding.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPageEventsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Called after the view has been created.
     * Launches a coroutine to fetch the list of events and display them in the RecyclerView.
     *
     * @param view The view returned by [onCreateView].
     * @param savedInstanceState Previous state, if available.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Use lifecycleScope to safely call suspend functions within fragment lifecycle
        lifecycleScope.launch {
            try {
                // Fetch events from the API service
                var events = ApiServiceProvider.getDataService().getAllEvents()
                events = events
                    .sortedByDescending { it.startTime }

                println("events size: " + events.size)
                val onEventClick: (Event) -> Unit = { event ->
                    val eventDetailPageFragment = EventDetailPageFragment(event, authenticatedLayoutFragment)
                    authenticatedLayoutFragment.loadPage(eventDetailPageFragment)
                }
                // Setup RecyclerView with fetched events
                binding.eventsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                binding.eventsRecyclerView.adapter = EventsAdapter(events, onEventClick)
            } catch (e: Exception) {
                // Log the exception (could be improved with UI error messaging)
                e.printStackTrace()
            }
        }
    }

    /**
     * Called when the view created by the fragment is being destroyed.
     * Clears the binding to avoid memory leaks.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
