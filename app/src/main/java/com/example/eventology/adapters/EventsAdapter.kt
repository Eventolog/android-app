package com.example.eventology.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.eventology.R
import com.example.eventology.databinding.ItemEventBinding
import com.example.eventology.data.models.Event
import com.example.eventology.utils.DateUtils

/**
 * Adapter class for displaying a list of [Event] items in a RecyclerView.
 *
 * @param events The list of event objects to be displayed.
 */
class EventsAdapter(private val events: List<Event>) :
    RecyclerView.Adapter<EventsAdapter.EventViewHolder>() {

    /**
     * ViewHolder class that holds the layout views for an individual event item.
     *
     * @property binding The ViewBinding object for the item layout.
     */
    inner class EventViewHolder(private val binding: ItemEventBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds the data from an [Event] object to the layout views.
         *
         * @param event The event object containing the data to display.
         */
        fun bind(event: Event, context: Context) {
            binding.eventName.text = event.name
            val readableDate = DateUtils.toReadableDate(event.startTime)
            val differenceInMins = DateUtils.getDifferenceInMinutes(event.startTime, event.endTime)
            binding.eventTime.text = "$readableDate · ${differenceInMins} m"
            binding.eventDescription.text = event.description

            // fill the status chip
            var isUpcoming = DateUtils.isUpcoming(event.startTime)
            var chipStyle: Int? = null
            var chipText = ""
            var textColor: Int? = null;
            if(isUpcoming){
                chipStyle = R.drawable.chip_background_active
                chipText = context.getString(R.string.upcoming)
                textColor = context.getColor(R.color.white)
            }else{
                chipStyle = R.drawable.chip_background_inactive
                chipText = context.getString(R.string.finished)
                textColor = context.getColor(R.color.black)
            }
            binding.eventStatusChip.apply {
                text = chipText
                setBackgroundResource(chipStyle)
                setTextColor(textColor)
            }
        }
    }

    /**
     * Inflates the layout for each event item and returns a [EventViewHolder].
     *
     * @param parent The parent view group.
     * @param viewType The view type of the new view.
     * @return A new instance of [EventViewHolder].
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    /**
     * Returns the total number of events in the list.
     *
     * @return The size of the event list.
     */
    override fun getItemCount(): Int = events.size

    /**
     * Binds the event data to the view holder for a given position.
     *
     * @param holder The view holder which should be updated.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val context = holder.itemView.context
        holder.bind(events[position], context)
    }
}
