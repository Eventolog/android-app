package com.example.eventology.fragments

import android.view.*
import android.os.Bundle
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import com.example.eventology.data.models.Seat
import com.example.eventology.adapters.TicketsAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventology.adapters.MyTicketsAdapter
import com.example.eventology.data.services.ApiServiceProvider
import com.example.eventology.databinding.FragmentPageTicketsBinding

/**
 * Fragment to show the list of tickets or available seats.
 *
 * @property authenticatedLayoutFragment Used to navigate to other pages.
 */
class TicketsPageFragment(private val authenticatedLayoutFragment: AuthenticatedLayoutFragment) : PageFragments(2, authenticatedLayoutFragment) {

    private var _binding: FragmentPageTicketsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPageTicketsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            try {
                val tickets = ApiServiceProvider.getDataService().getMyTickets()

                binding.ticketsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                binding.ticketsRecyclerView.adapter = MyTicketsAdapter(tickets) { ticket ->
                    // Quan cliques un tiquet, mostres els seients per aquell event
                    showSeatsForEvent(ticket.id)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun showSeatsForEvent(eventId: Int) {
        lifecycleScope.launch {
            try {
                val seats = ApiServiceProvider.getDataService().getFreeSeats(eventId)

                binding.ticketsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                binding.ticketsRecyclerView.adapter = TicketsAdapter(seats) { selectedSeats ->
                    // Tractament de les butaques seleccionades
                    println("Seleccionades: $selectedSeats")
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