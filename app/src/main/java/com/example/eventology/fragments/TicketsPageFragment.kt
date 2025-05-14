package com.example.eventology.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.eventology.R

/**
 * Fragmento to show the tickets list.
 */
class TicketsPageFragment : PageFragments(2) {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_page_tickets, container, false)
    }
}