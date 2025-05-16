package com.example.eventology.fragments

import android.view.*
import android.widget.*
import android.os.Bundle
import android.graphics.Color
import com.example.eventology.R
import kotlinx.coroutines.launch
import android.annotation.SuppressLint
import androidx.lifecycle.lifecycleScope
import com.example.eventology.data.models.Event
import com.example.eventology.data.services.ApiServiceProvider

class SeatSelectionFragment(
    private val event: Event,
    authenticatedLayoutFragment: AuthenticatedLayoutFragment
) : PageFragments(11, authenticatedLayoutFragment) {

    private val selectedSeatIds = mutableSetOf<Int>()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_seat_selection, container, false)
        val seatGrid = view.findViewById<GridLayout>(R.id.seatGrid)
        val confirmButton = view.findViewById<Button>(R.id.confirmButton)

        lifecycleScope.launch {
            val seats = ApiServiceProvider.getDataService().getFreeSeats(event.id)

            seats.forEach { seat ->
                val btn = Button(requireContext()).apply {
                    text = "${seat.row}${seat.number}"
                    setBackgroundColor(Color.LTGRAY)
                    setOnClickListener {
                        if (selectedSeatIds.contains(seat.id)) {
                            selectedSeatIds.remove(seat.id)
                            setBackgroundColor(Color.LTGRAY)
                        } else {
                            selectedSeatIds.add(seat.id)
                            setBackgroundColor(Color.GREEN)
                        }
                    }
                }
                seatGrid.addView(btn)
            }
        }

        confirmButton.setOnClickListener {
            lifecycleScope.launch {
                val success = ApiServiceProvider.getDataService().bookSeats(event.id, selectedSeatIds.toList())
                if (success) {
                    Toast.makeText(context, "Reserva completada!", Toast.LENGTH_SHORT).show()
                    getAuthenticatedLayoutFragment().loadPage(EventsListPageFragment(getAuthenticatedLayoutFragment()))
                } else {
                    Toast.makeText(context, "Error en la reserva.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return view
    }
}