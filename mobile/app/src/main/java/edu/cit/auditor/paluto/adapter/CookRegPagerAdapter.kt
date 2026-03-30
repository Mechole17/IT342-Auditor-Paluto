package edu.cit.auditor.paluto.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import edu.cit.auditor.paluto.CookRegistrationStep1Fragment
import edu.cit.auditor.paluto.CookRegistrationStep2Fragment

class CookRegPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CookRegistrationStep1Fragment()
            else -> CookRegistrationStep2Fragment()
        }
    }
}