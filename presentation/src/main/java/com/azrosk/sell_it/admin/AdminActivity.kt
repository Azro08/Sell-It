package com.azrosk.sell_it.admin

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.azrosk.sell_it.R
import com.azrosk.sell_it.databinding.ActivityAdminBinding
import com.azrosk.sell_it.shared.auth.AuthActivity
import com.azrosk.sell_it.shared.language.LanguageFragment
import com.azrosk.sell_it.util.AuthManager
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AdminActivity : AppCompatActivity(R.layout.activity_admin) {
    private val binding by viewBinding(ActivityAdminBinding::bind)
    @Inject
    lateinit var authManager: AuthManager
    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setViewPager()
        setMenu()
    }

    private fun setMenu() {
        this.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.admin_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.itemAdminLanguage -> {
                        val languageFragment = LanguageFragment()
                        languageFragment.show(supportFragmentManager, "languageFragment")
                    }

                    R.id.itemAdminLogout -> {
                        authManager.removeUser()
                        authManager.removeRole()
                        firebaseAuth.signOut()
                        startActivity(Intent(this@AdminActivity, AuthActivity::class.java))
                        finish()
                    }
                }
                return true
            }
        }, this, Lifecycle.State.CREATED)
    }

    private fun setViewPager() {

        val adapter = AdminViewPagerAdapter(this)
        binding.adminViewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.adminViewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.users)
                1 -> getString(R.string.products)
                2 -> getString(R.string.feedback)
                3 -> getString(R.string.profile)
                else -> ""
            }
        }.attach()

    }

}