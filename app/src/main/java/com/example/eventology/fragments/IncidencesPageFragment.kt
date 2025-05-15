package com.example.eventology.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventology.adapters.IncidenceAdapter
import com.example.eventology.data.services.ApiServiceProvider
import com.example.eventology.databinding.FragmentPageIncidencesBinding
import kotlinx.coroutines.launch

/**
 * Fragment that displays a list of user-related incidences.
 *
 * A title and "Add Incidence" button are shown at the top.
 * The incidences are displayed in a vertical scrollable list using a RecyclerView.
 *
 * @property authenticatedLayoutFragment Used for communicating or navigating from this fragment.
 */
class IncidencesPageFragment(
    private val authenticatedLayoutFragment: AuthenticatedLayoutFragment
) : PageFragments(3, authenticatedLayoutFragment) {

    private var _binding: FragmentPageIncidencesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPageIncidencesBinding.inflate(inflater, container, false)

        // Setup RecyclerView layout manager
        binding.incidencesRecyclerView.layoutManager = LinearLayoutManager(context)

        // Setup Add Incidence button click
        binding.addIncidenceButton.setOnClickListener {
            Log.d("IncidencesPageFragment", "Add Incidence clicked")
        }

        // Fetch incidences and set up adapter
        lifecycleScope.launch {
            val incidences = ApiServiceProvider.getDataService().getMyIncidences()
            binding.incidencesRecyclerView.adapter = IncidenceAdapter(incidences)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
