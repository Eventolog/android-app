package com.example.eventology.fragments

import android.os.Bundle
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
 **
 * @property authenticatedLayoutFragment Used for navigating from this fragment.
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
            val addIncidenceFragment = AddIncidenceFragment(authenticatedLayoutFragment)
            authenticatedLayoutFragment.loadPage(addIncidenceFragment)
        }

        // Fetch incidences and set up adapter
        lifecycleScope.launch {
            val incidences = ApiServiceProvider.getDataService().getMyIncidences()
            binding.incidencesRecyclerView.adapter = IncidenceAdapter(incidences, requireContext())
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
