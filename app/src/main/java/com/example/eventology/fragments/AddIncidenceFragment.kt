package com.example.eventology.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.eventology.R
import com.example.eventology.data.services.ApiServiceProvider
import com.example.eventology.databinding.FragmentAddIncidenceBinding
import kotlinx.coroutines.launch

/**
 * Fragment that displays a form to create an incidence.
 *
 * @property authenticatedLayoutFragment Used for navigating from this fragment.
 */
class AddIncidenceFragment(
    private val authenticatedLayoutFragment: AuthenticatedLayoutFragment
) : PageFragments(4, authenticatedLayoutFragment) {

    private var _binding: FragmentAddIncidenceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Correct binding for this layout
        _binding = FragmentAddIncidenceBinding.inflate(inflater, container, false)

        binding.addIncidenceButton.setOnClickListener {
            var reason = binding.inputText.text.toString()

            when {
                reason.isEmpty() -> {
                    Toast.makeText(requireContext(), getString(R.string.reason_empty), Toast.LENGTH_SHORT).show()
                }
                reason.length > 500 -> {
                    Toast.makeText(requireContext(), getString(R.string.reason_too_long), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    lifecycleScope.launch {

                    Toast.makeText(requireContext(), getString(R.string.incidence_submitted), Toast.LENGTH_SHORT).show()
                    // create incidence
                    ApiServiceProvider.getDataService().createIncidence(reason)
                    // return to incidences page
                    authenticatedLayoutFragment.goBack()
                        }
                }
            }


        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
