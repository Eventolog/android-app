package com.example.eventology.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventology.R
import com.example.eventology.adapters.IncidenceAdapter
import com.example.eventology.data.models.Incidence
import com.example.eventology.data.services.ApiServiceProvider
import kotlinx.coroutines.launch

/**
 * Fragment to show the incidences list
 *
 * @property authenticatedLayoutFragment fragment used to changes page from this page
 */
class IncidencesPageFragment(private val authenticatedLayoutFragment: AuthenticatedLayoutFragment)
    : PageFragments(3, authenticatedLayoutFragment) {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_page_incidences, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.incidencesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        lifecycleScope.launch {

            var incidences = ApiServiceProvider.getDataService().getMyIncidences()

            val adapter = IncidenceAdapter(incidences)
            recyclerView.adapter = adapter
        }

        return view
    }
}