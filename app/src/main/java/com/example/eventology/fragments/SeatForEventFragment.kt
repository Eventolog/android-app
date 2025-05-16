package com.example.eventology.fragments

import android.view.*
import android.os.Bundle
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import com.example.eventology.adapters.TicketsAdapter
import com.example.eventology.data.services.ApiServiceProvider
import com.example.eventology.databinding.FragmentPageTicketsBinding
import androidx.recyclerview.widget.LinearLayoutManager

/**
 * Fragment to show available seats for a specific event.
 */
class SeatsForEventFragment(
    authenticatedLayoutFragment: AuthenticatedLayoutFragment,
    private val eventId: Int
) : PageFragments(3, authenticatedLayoutFragment) {

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
                val seats = ApiServiceProvider.getDataService().getFreeSeats(eventId)

                binding.ticketsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                binding.ticketsRecyclerView.adapter = TicketsAdapter(seats) { selectedSeats ->
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