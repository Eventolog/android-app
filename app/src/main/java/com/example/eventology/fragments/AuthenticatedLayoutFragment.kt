package com.example.eventology.fragments

import java.util.Stack
import android.os.Bundle
import android.view.View
import android.content.Intent
import android.view.ViewGroup
import android.content.Context
import com.example.eventology.R
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.eventology.activities.LoginActivity

/**
 * Main frame that contains the bottom navbar and manages
 * main fragment display on page change
 */
class AuthenticatedLayoutFragment : Fragment() {
    private val pageHistory = Stack<PageFragments>()
    private var currentPage: PageFragments? = null

    private var incidencesClickCount = 0
    private var lastIncidencesClickTime = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_main, container, false)

    /**
     * Navigate to initial page and add logic to [NavbarFragment] to
     * properly handle pages navigation
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Load initial page
        loadPage(EventsListPageFragment(this))

        val navbarFragment = childFragmentManager.findFragmentById(R.id.navbar_fragment) as NavbarFragment

        navbarFragment.setOnNavigationItemSelectedListener { itemId ->
            when (itemId) {
                R.id.nav_tickets -> {
                    incidencesClickCount = 0
                    loadPage(TicketsPageFragment(this))
                }
                R.id.nav_events -> {
                    incidencesClickCount = 0
                    loadPage(EventsListPageFragment(this))
                }
                R.id.nav_incidences -> {
                    val now = System.currentTimeMillis()
                    if (now - lastIncidencesClickTime > 2000) {
                        incidencesClickCount = 0
                    }
                    incidencesClickCount++
                    lastIncidencesClickTime = now

                    if (incidencesClickCount >= 3) {
                        incidencesClickCount = 0
                        performLogout()
                    } else {
                        loadPage(IncidencesPageFragment(this))
                    }
                }
            }
        }
    }

    /**
     * Navigate to a fragment with animation depending of its page order defined
     * in the class [PageFragments] and add the previous page to the page history
     *
     * @param fragment fragment page to be loaded
     * @param recordHistory store the previous fragment to the one passed as param on the pageHistory
     */
    fun loadPage(fragment: PageFragments, recordHistory: Boolean = true) {
        if (recordHistory && currentPage != null) {
            pageHistory.push(currentPage!!)
        }

        // Create transaction
        val transaction = childFragmentManager.beginTransaction()

        // Set an animation to the fragment transaction
        setAnimationToFragmentTransaction(fragment, transaction)

        // Commit transaction
        transaction.replace(R.id.page_container, fragment)
            .commit()

        currentPage = fragment

        // Ensure the transaction is committed and view is available
        childFragmentManager.executePendingTransactions()

        // Set up back button if it exists
        val fragmentView = fragment.view
        val goBackBtn = fragmentView?.findViewById<View>(R.id.goBackBtn)
        goBackBtn?.setOnClickListener {
            goBack()
        }
    }

    /**
     * Based on if this.currentPage is not null or [PageFragments.getPageOrder] define
     * if the transaction will be a slide to the left or to the right
     * if this.currentPage is null or currentPage.getPageOrder is smaller than the
     * newFragment.getPageOrder it will be a slide to the right,
     * in the other hand if the currentPage.getPageOrder is bigger than newFragment.getPageOrder
     * it will be a slide to the left
     *
     * @param newFragment new fragment being loaded, used to get its [PageFragments.getPageOrder]
     * and compare to te one of the this.currentPage
     *
     * @param transaction the fragment transaction to set the animation
     */
    private fun setAnimationToFragmentTransaction(
        newFragment: PageFragments,
        transaction: FragmentTransaction){
        if(currentPage != null){
            if(currentPage!!.getPageOrder() > newFragment.getPageOrder()){
                transaction.setCustomAnimations(
                    R.anim.slide_in_left,      // Pop enter (when going back)
                    R.anim.slide_out_right,     // Pop exit
                    R.anim.slide_in_right,     // Enter
                    R.anim.slide_out_left
                )

            }else{

                transaction.setCustomAnimations(
                    R.anim.slide_in_right,     // Enter
                    R.anim.slide_out_left,     // Exit
                    R.anim.slide_in_left,      // Pop enter (when going back)
                    R.anim.slide_out_right     // Pop exit
                )

            }
        }else {
            transaction.setCustomAnimations(
                R.anim.slide_in_right,     // Enter
                R.anim.slide_out_left,     // Exit
                R.anim.slide_in_left,      // Pop enter (when going back)
                R.anim.slide_out_right     // Pop exit
            )

        }
    }

    /**
     * Logs out the current user by clearing the saved session token and
     * redirecting them to the [LoginActivity].
     *
     * This method performs the following steps:
     * - Removes the "token" entry from the shared preferences.
     * - Starts the [LoginActivity] with flags to clear the back stack, ensuring the user
     *   cannot navigate back to authenticated pages.
     * - Finishes the current activity to fully exit the authenticated session.
     */
    private fun performLogout() {
        val prefs = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE)
        prefs.edit().remove("token").apply()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    /**
     * Navigate to previous page fragment
     */
    fun goBack() {
        if (pageHistory.isNotEmpty()) {
            val previous = pageHistory.pop()
            loadPage(previous, recordHistory = false)
        }
    }
}
