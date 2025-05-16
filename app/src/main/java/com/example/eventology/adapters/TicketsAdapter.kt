package com.example.eventology.adapters

import android.view.*
import android.widget.CheckBox
import android.widget.TextView
import com.example.eventology.R
import android.annotation.SuppressLint
import com.example.eventology.data.models.Seat
import androidx.recyclerview.widget.RecyclerView

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

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        val seat = seats[position]
        holder.seatLabel.text = "Fila ${seat.row}, seient ${seat.number}"

        holder.seatCheckBox.setOnCheckedChangeListener(null)
        holder.seatCheckBox.isChecked = selectedSeats.contains(seat)

        // 1. Gestiona clic directe sobre el CheckBox
        holder.seatCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedSeats.add(seat)
            } else {
                selectedSeats.remove(seat)
            }
            onSelectionChanged(selectedSeats.toList())
        }

        // 2. Clic a tota la línia → canvia el CheckBox
        holder.itemView.setOnClickListener {
            val newChecked = !holder.seatCheckBox.isChecked
            holder.seatCheckBox.isChecked = newChecked
        }

        // 3. Alterna color de text
        val context = holder.itemView.context
        val textColor = if (position % 2 == 0) {
            context.getColor(R.color.row_even)
        } else {
            context.getColor(R.color.primary)
        }
        holder.seatLabel.setTextColor(textColor)
    }
}