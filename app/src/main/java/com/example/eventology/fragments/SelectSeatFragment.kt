package com.example.eventology.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.view.ViewGroup
import com.example.eventology.R
import kotlinx.coroutines.launch
import android.view.LayoutInflater
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventology.data.services.ApiServiceProvider
import com.example.eventology.databinding.FragmentPageTicketsBinding

class SelectSeatFragment(
    private val authenticatedLayoutFragment: AuthenticatedLayoutFragment,
    private val eventId: Int
) : PageFragments(11, authenticatedLayoutFragment) {

    private var _binding: FragmentPageTicketsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPageTicketsBinding.inflate(inflater, container, false)
        return binding.root
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        lifecycleScope.launch {
//            try {
//                val seats = ApiServiceProvider.getDataService().getFreeSeats(eventId)
//
//                binding.ticketsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
//                binding.ticketsRecyclerView.adapter = TicketsAdapter(seats) { selectedSeats ->
//                    if (selectedSeats.isNotEmpty()) {
//                        lifecycleScope.launch {
//                            try {
//                                ApiServiceProvider.getDataService().bookSeats(eventId, listOf(selectedSeats[0].id))
//                                Toast.makeText(context, R.string.ticket_reserved, Toast.LENGTH_SHORT).show()
//                                authenticatedLayoutFragment.goBack()
//                            } catch (e: Exception) {
//                                e.printStackTrace()
//                            }
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}