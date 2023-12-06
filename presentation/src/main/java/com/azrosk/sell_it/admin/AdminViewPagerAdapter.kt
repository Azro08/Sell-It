package com.azrosk.sell_it.admin

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.azrosk.sell_it.admin.feedback.AdminFeedbackFragment
import com.azrosk.sell_it.admin.produts.AdminProductsFragment
import com.azrosk.sell_it.admin.users.AdminUsersFragment
import com.azrosk.sell_it.shared.profile.ProfileFragment

class AdminViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AdminUsersFragment()
            1 -> AdminProductsFragment()
            2 -> AdminFeedbackFragment()
            3 -> ProfileFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }


}