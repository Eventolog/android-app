package com.example.eventology.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.eventology.R
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Fragmento que contiene la barra de navegación inferior.
 * Expone un listener para que el padre maneje la navegación.
 */
class NavbarFragment : Fragment() {

    private var listener: ((Int) -> Unit)? = null

    fun setOnNavigationItemSelectedListener(listener: (Int) -> Unit) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_navbar, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val bottomNav = view.findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setOnItemSelectedListener {
            listener?.invoke(it.itemId)
            true
        }
    }
}
