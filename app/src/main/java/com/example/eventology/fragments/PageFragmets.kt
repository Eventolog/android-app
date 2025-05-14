package com.example.eventology.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment

/**
 * Base class for fragments used in page navigation.
 * Accepts an `order` to indicate the position in the navbar/page stack.
 *
 * @property order The order of the fragment, used for transitions or sorting.
 */
open class PageFragments(private val order: Int) : Fragment() {

    /**
     * Returns the page order index for this fragment.
     */
    fun getPageOrder(): Int {
        return order
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Optional: use order for logic here
    }
}