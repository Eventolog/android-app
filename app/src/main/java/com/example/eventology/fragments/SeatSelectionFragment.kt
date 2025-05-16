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
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_seat_selection, container, false)

        val backBtn = view.findViewById<View>(R.id.pageTopBar)
        backBtn.setOnClickListener {
            getAuthenticatedLayoutFragment().goBack()
        }

        val seatGrid = view.findViewById<GridLayout>(R.id.seatGrid)
        val confirmButton = view.findViewById<Button>(R.id.confirmButton)

        // CONFIGURA el nombre de files i columnes
        val numRows = 10
        val numCols = 6

        lifecycleScope.launch {
            val availableSeats = ApiServiceProvider.getDataService().getFreeSeats(event.id)
            val availableSeatMap = availableSeats.associateBy { "${it.row}${it.number}" }

            seatGrid.removeAllViews() // assegura’t de començar net

            for (row in 1..10) {
                for (col in 1..6) {
                    val seatNumber = "$row$col"
                    val margin = 4

                    val btn = Button(requireContext()).apply {
                        text = seatNumber
                        layoutParams = ViewGroup.MarginLayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        ).apply {
                            setMargins(margin, margin, margin, margin)
                        }

                        setPadding(0, 0, 0, 0)
                        textSize = 14f
                        setTextColor(Color.BLACK)

                        val seat = availableSeatMap[seatNumber]
                        if (seat != null) {
                            setBackgroundColor(Color.parseColor("#D0E8FF")) // Disponible
                            isEnabled = true
                            setOnClickListener {
                                if (selectedSeatIds.contains(seat.id)) {
                                    selectedSeatIds.remove(seat.id)
                                    setBackgroundColor(Color.parseColor("#D0E8FF"))
                                } else {
                                    selectedSeatIds.add(seat.id)
                                    setBackgroundColor(Color.parseColor("#4CAF50")) // Seleccionat
                                }
                            }
                        } else {
                            setBackgroundColor(Color.parseColor("#A0A0A0")) // No disponible
                            isEnabled = false
                        }
                    }

                    seatGrid.addView(btn)
                }
            }
        }


        confirmButton.setOnClickListener {
            lifecycleScope.launch {
                val success = ApiServiceProvider.getDataService()
                    .bookSeats(event.id, selectedSeatIds.toList())
                if (success) {
                    Toast.makeText(context, "Reserva completada!", Toast.LENGTH_SHORT).show()
                    getAuthenticatedLayoutFragment().loadPage(
                        EventsListPageFragment(
                            getAuthenticatedLayoutFragment()
                        )
                    )
                } else {
                    Toast.makeText(context, "Error en la reserva.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }
}