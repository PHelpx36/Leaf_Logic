package com.fsa.leaf_logic.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.marginBottom
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fsa.leaf_logic.R
import com.fsa.leaf_logic.databinding.FragmentHomeBinding
import com.fsa.leaf_logic.databinding.FragmentNovaPlantaBinding

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Use binding para acessar a View corretamente
        binding.plusSign.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_to_nav_novaPlanta)
        }

        val dynamicContainer = binding.dynamicContainer

        // Adicionar componentes dinamicamente
        for (i in 1..3) {
            val textView = TextView(requireContext())

            if(i != 3){
                textView.apply {
                    text = "Notification $i"
                    textSize = 25f
                    setTextColor(Color.WHITE)
                    setBackgroundResource(R.drawable.retangular_shape) // Aplicando o drawable
                    setPadding(8, 40, 8, 40)
                }
            }
            else{
                textView.apply {
                    text = "ver mais"
                    textSize = 25f
                    setTextColor(Color.WHITE)
                    setBackgroundResource(R.drawable.retangular_shape) // Aplicando o drawable
                    setPadding(8, 40, 8, 40)
                    setOnClickListener {
                        findNavController().navigate(R.id.action_nav_home_to_nav_slideshow)
                    }
                }
            }

            // Adiciona os componentes ao LinearLayout
            dynamicContainer.addView(textView)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}