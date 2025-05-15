package com.example.eventology.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.eventology.R
import com.example.eventology.databinding.FragmentPageIncidencesBinding

/**
 * Fragment that displays a form to create incidence
 *
 * @property authenticatedLayoutFragment Used for navigating from this fragment.
 */
class AddIncidenceFragment(
    private val authenticatedLayoutFragment: AuthenticatedLayoutFragment
) : PageFragments(4, authenticatedLayoutFragment){
    private var _binding: FragmentPageIncidencesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPageIncidencesBinding.inflate(inflater, container, false)
        inflater.inflate(R.layout.fragment_main, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}