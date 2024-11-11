package com.fsa.leaf_logic

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.fsa.leaf_logic.ui.notifications.CompletedFragment
import com.fsa.leaf_logic.ui.notifications.PendingNotificationFragment

class NotificationPagerAdapter(fragment: Fragment, private val plantaId: String) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2  // Número de abas

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PendingNotificationFragment().apply {
                arguments = Bundle().apply { putString("plantaId", plantaId) }
            }  // Aba de Pendentes
            else -> CompletedFragment().apply {
                arguments = Bundle().apply { putString("plantaId", plantaId) }
            }
        }
    }
}
