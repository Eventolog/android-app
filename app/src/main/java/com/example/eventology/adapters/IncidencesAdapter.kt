package com.example.eventology.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventology.R
import com.example.eventology.constants.IncidenceTypes
import com.example.eventology.data.models.Incidence
import kotlin.Int

/**
 * Adapter to display a list of [Incidence] objects in a RecyclerView.
 *
 * Each item displays the reason on the left and the status on the right.
 *
 * @property incidences List of incidence items to display.
 */
class IncidenceAdapter(private val incidences: List<Incidence>, private val context: Context) :
    RecyclerView.Adapter<IncidenceAdapter.IncidenceViewHolder>() {

    /**
     * ViewHolder class that holds references to the views for each item in the list.
     *
     * @param view The root view of the individual list item layout.
     */
    inner class IncidenceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val reasonTextView: TextView = view.findViewById(R.id.incidenceReason)
        val statusTextView: TextView = view.findViewById(R.id.incidenceStatus)
    }

    /**
     * Called when RecyclerView needs a new [IncidenceViewHolder] of the given type to represent an item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncidenceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_incidence, parent, false)
        return IncidenceViewHolder(view)
    }

    /**
     * Capitalize the first letter of a string
     *
     * @param text string to capitalize
     * @return capitalized string
     */
    private fun capitalizeWords(text: String): String {
        return text.split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
    }

    /**
     * Returns a localized, human-readable string for a given incidence status key.
     *
     * @param statusKey The internal status string (e.g., "open", "in_progress", "closed").
     * @return A user-facing, localized status string.
     */
    fun getLocalizedIncidenceStatus(statusKey: String): String {
        return when (statusKey) {
            IncidenceTypes.OPEN -> context.getString(R.string.status_open)
            IncidenceTypes.IN_PROGRESS -> context.getString(R.string.status_in_progress)
            IncidenceTypes.CLOSED -> context.getString(R.string.status_closed)
            else -> statusKey // fallback to raw string if not recognized
        }
    }


    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder The ViewHolder which should be updated.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: IncidenceViewHolder, position: Int) {
        val incidence = incidences[position]
        holder.reasonTextView.text = incidence.reason
        val localizedStatus = getLocalizedIncidenceStatus(incidence.status)
        holder.statusTextView.text = capitalizeWords(localizedStatus)
        var chipStyle: Int?

        if(incidence.status.equals(IncidenceTypes.CLOSED)){
            chipStyle = R.drawable.chip_background_incidence_finished
        }else{
            chipStyle = R.drawable.chip_background_incidence_pendent_open
        }

        holder.statusTextView.setBackgroundResource(chipStyle)
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     */
    override fun getItemCount(): Int = incidences.size
}
