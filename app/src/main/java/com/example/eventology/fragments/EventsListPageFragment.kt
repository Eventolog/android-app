package com.example.eventology.fragments

import android.os.Bundle
import android.view.*
import com.example.eventology.R

/**
 * Fragment to show the events list
 */
class EventsListPageFragment : PageFragments(1) {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_page_events_list, container, false)
    }
}
