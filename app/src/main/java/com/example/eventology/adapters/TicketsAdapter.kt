package com.example.eventology.adapters

import android.view.*
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventology.R
import com.example.eventology.data.models.Seat

class TicketsAdapter(
    private val seats: List<Seat>,
    private val onSelectionChanged: (List<Seat>) -> Unit
) : RecyclerView.Adapter<TicketsAdapter.TicketViewHolder>() {

    private val selectedSeats = mutableSetOf<Seat>()

    inner class TicketViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val seatLabel: TextView = view.findViewById(R.id.seatLabel)
        val seatCheckBox: CheckBox = view.findViewById(R.id.seatCheckBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_seat, parent, false)
        return TicketViewHolder(view)
    }

    override fun getItemCount(): Int = seats.size

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        val seat = seats[position]
        holder.seatLabel.text = "Fila ${seat.row}, seient ${seat.number}"
        holder.seatCheckBox.setOnCheckedChangeListener(null)
        holder.seatCheckBox.isChecked = selectedSeats.contains(seat)

        holder.seatCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedSeats.add(seat)
            } else {
                selectedSeats.remove(seat)
            }
            onSelectionChanged(selectedSeats.toList())
        }
    }
}