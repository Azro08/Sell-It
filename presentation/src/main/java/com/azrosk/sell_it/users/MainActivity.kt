package com.azrosk.sell_it.users

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.azrosk.sell_it.R
import com.azrosk.sell_it.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val binding by viewBinding(ActivityMainBinding::bind)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        supportActionBar?.hide()
        binding.bottomNavView.background = null
        binding.bottomNavView.menu.getItem(2).isEnabled = false
        setBottomNavBar()
        binding.fab.setOnClickListener {
            findNavController(R.id.nav_host).navigate(R.id.addProductFragment)
        }
    }

    private fun setBottomNavBar() {
        val navController = findNavController(R.id.nav_host)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.productsListFragment,
                R.id.favoritesFragment,
                R.id.profileFragment,
                R.id.settingsFragment
            )
        )

        val topLevelDestinations = setOf(
            R.id.productsListFragment,
            R.id.favoritesFragment,
            R.id.profileFragment,
            R.id.settingsFragment
        )
        // Show the bottom navigation view for top-level destinations only
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in topLevelDestinations) {
                binding.bottomNavView.visibility = View.VISIBLE
                binding.fab.visibility = View.VISIBLE
                binding.bottomAppBar.visibility  = View.VISIBLE
            } else {
                binding.bottomNavView.visibility = View.GONE
                binding.fab.visibility = View.GONE
                binding.bottomAppBar.visibility  = View.GONE
            }
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNavView.setupWithNavController(navController)
    }

}