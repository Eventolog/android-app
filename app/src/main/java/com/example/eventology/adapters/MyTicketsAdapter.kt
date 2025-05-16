package com.example.eventology.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.eventology.R
import android.view.LayoutInflater
import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.example.eventology.data.models.Ticket

class MyTicketsAdapter(
    private val tickets: List<Ticket>,
    private val onTicketClicked: (Ticket) -> Unit
) : RecyclerView.Adapter<MyTicketsAdapter.TicketViewHolder>() {

    inner class TicketViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val eventName: TextView = view.findViewById(R.id.ticketEventName)
        val seatInfo: TextView = view.findViewById(R.id.ticketSeat)
        val reservationDate: TextView = view.findViewById(R.id.ticketDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ticket, parent, false)
        return TicketViewHolder(view)
    }

    override fun getItemCount(): Int = tickets.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        val ticket = tickets[position]
        holder.eventName.text = ticket.eventName
        holder.seatInfo.text = "Fila ${ticket.seatRow}, Seient ${ticket.seatNumber}"
        holder.reservationDate.text = "Reserva: ${ticket.reservationDate}"

        // Assignem el clickListener aquí amb ticket correcte
        holder.itemView.setOnClickListener {
            onTicketClicked(ticket)
        }
    }
}