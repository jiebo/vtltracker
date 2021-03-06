package com.tijiebo.covidtracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tijiebo.covidtracker.ui.view.CountryDetailFragment
import com.tijiebo.covidtracker.ui.view.DashboardFragment

class MainActivity : AppCompatActivity(), NavigationController {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, DashboardFragment.newInstance(), DashboardFragment.TAG)
                .commitNow()
        }
    }

    override fun navigateToDetailsPage(countryId: String) {
        if (supportFragmentManager.isDestroyed) return
        supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_right,
                R.anim.slide_in_right,
                R.anim.slide_out_right,
            )
            add(
                R.id.container,
                CountryDetailFragment.newInstance(countryId),
                CountryDetailFragment.TAG
            )
            addToBackStack(null)
            commit()
        }
    }

    override fun onBackPressed() {
        if (!supportFragmentManager.popBackStackImmediate())
            super.onBackPressed()
    }
}

interface NavigationController {
    fun navigateToDetailsPage(countryId: String)
}