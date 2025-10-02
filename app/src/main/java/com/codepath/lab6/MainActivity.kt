package com.codepath.lab6

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.codepath.lab6.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


private const val TAG = "MainActivity/"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val fragmentManager : FragmentManager = supportFragmentManager
        val parksFragment : ParksFragment = ParksFragment()
        val campgroundFragment : CampgroundFragment = CampgroundFragment()

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        // handle navigation selection
        bottomNavigationView.setOnItemSelectedListener { item ->
            lateinit var fragment: Fragment
            when (item.itemId) {
                R.id.nav_parks -> fragment = parksFragment
                R.id.nav_campgrounds -> fragment = campgroundFragment
            }
            replaceFragment(fragment)
            true
        }

        // Set default selection
        bottomNavigationView.selectedItemId = R.id.nav_parks
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_frame_layout, fragment)
        fragmentTransaction.commit()
    }
}