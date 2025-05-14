package com.example.eventology.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.eventology.R

/**
 * Main frame that contains the bottom navbar and manages
 * main fragment display on page change
 */
class AuthentiactedLayoutFragment : Fragment() {
    var currentPage: PageFragments? = null;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_main, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Cargar pantalla inicial
        loadPage(EventsListPageFragment())

        val navbarFragment = childFragmentManager.findFragmentById(R.id.navbar_fragment) as NavbarFragment

        navbarFragment.setOnNavigationItemSelectedListener { itemId ->
            when (itemId) {
                R.id.nav_tickets -> loadPage(TicketsPageFragment())
                R.id.nav_events -> loadPage(EventsListPageFragment())
                R.id.nav_incidences -> loadPage(IncidencesPageFragment())
            }
        }
    }

    private fun loadPage(fragment: PageFragments) {
        if(currentPage != null){
            if(currentPage!!.getPageOrder() > fragment.getPageOrder()){
                childFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.slide_in_left,      // Pop enter (when going back)
                        R.anim.slide_out_right,     // Pop exit
                        R.anim.slide_in_right,     // Enter
                        R.anim.slide_out_left
                    )
                    .replace(R.id.page_container, fragment)
                    .commit()
            }else{

                childFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.slide_in_right,     // Enter
                        R.anim.slide_out_left,     // Exit
                        R.anim.slide_in_left,      // Pop enter (when going back)
                        R.anim.slide_out_right     // Pop exit
                    )
                    .replace(R.id.page_container, fragment)
                    .commit()
            }
        }else{
            childFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,     // Enter
                    R.anim.slide_out_left,     // Exit
                    R.anim.slide_in_left,      // Pop enter (when going back)
                    R.anim.slide_out_right     // Pop exit
                )
                .replace(R.id.page_container, fragment)
                .commit()
        }

        currentPage = fragment
    }
}
