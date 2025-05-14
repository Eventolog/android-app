package com.example.eventology.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment

/**
 * Base class for fragments used in page navigation.
 * Accepts an `order` to indicate the position in the navbar/page stack.
 *
 * @property order The order of the fragment, used for transitions or sorting.
 * @property authenticatedLayoutFragment fragment used to manage page changes from displayed fragments
 */
open class PageFragments(private val order: Int, private val authenticatedLayoutFragment: AuthenticatedLayoutFragment) : Fragment() {

    /**
     * Returns the page order index for this fragment.
     */
    fun getPageOrder(): Int {
        return order
    }

    /**
     * Returns the [AuthenticatedLayoutFragment] used to display page fragments
     */
    fun getAuthenticatedLayoutFragment(): AuthenticatedLayoutFragment{
        return authenticatedLayoutFragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Optional: use order for logic here
    }
}