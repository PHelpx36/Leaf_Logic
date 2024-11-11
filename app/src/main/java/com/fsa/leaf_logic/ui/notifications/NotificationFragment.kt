package com.fsa.leaf_logic.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.fsa.leaf_logic.NotificationPagerAdapter
import com.fsa.leaf_logic.databinding.FragmentNotificationBinding
import com.google.android.material.tabs.TabLayoutMediator

class NotificationFragment : Fragment() {

    private val args: NotificationFragmentArgs by navArgs()

    private var _binding: FragmentNotificationBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val plantaId = args.plantaId
        val pendentes = args.pendentes

        val tabLayout = binding.tabLayout
        val viewPager = binding.viewPager

        // Inicializar o adapter
        val adapter = NotificationPagerAdapter(this, plantaId)
        viewPager.adapter = adapter

        // Conectar o TabLayout ao ViewPager
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Pendentes"
                else -> "Concluídas"
            }
        }.attach()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}