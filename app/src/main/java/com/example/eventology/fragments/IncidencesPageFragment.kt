package com.example.eventology.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.eventology.R

/**
 * Fragment to show the incidences list
 *
 * @property authentiactedLayoutFragment fragment used to changes page from this page
 */
class IncidencesPageFragment(private val authentiactedLayoutFragment: AuthentiactedLayoutFragment)  : PageFragments(3, authentiactedLayoutFragment) {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_page_incidences, container, false)
    }
}