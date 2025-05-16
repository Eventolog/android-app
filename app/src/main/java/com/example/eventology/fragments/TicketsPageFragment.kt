package com.example.eventology.fragments

import android.view.*
import android.os.Bundle
import android.widget.Toast
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventology.adapters.MyTicketsAdapter
import com.example.eventology.data.services.ApiServiceProvider
import com.example.eventology.databinding.FragmentPageTicketsBinding

/**
 * Fragment to show the list of user tickets.
 */
class TicketsPageFragment(
    private val authenticatedLayoutFragment: AuthenticatedLayoutFragment
) : PageFragments(2, authenticatedLayoutFragment) {

    private var _binding: FragmentPageTicketsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPageTicketsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            try {
                val tickets = ApiServiceProvider.getDataService().getMyTickets()
                val allEvents = ApiServiceProvider.getDataService().getAllEvents()

                binding.ticketsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                binding.ticketsRecyclerView.adapter = MyTicketsAdapter(tickets) { ticket ->

                    val event = allEvents.find { it.id == ticket.eventId }
                    if (event != null) {
                        authenticatedLayoutFragment.loadPage(
                            EventDetailPageFragment(event, authenticatedLayoutFragment)
                        )
                    } else {
                        Toast.makeText(requireContext(), "No s'ha trobat l'esdeveniment.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}