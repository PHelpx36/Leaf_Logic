package com.fsa.leaf_logic.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
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

        val imageView = binding.imageInsidePlusSign

        // Defina a imagem dinamicamente
        imageView.setImageResource(R.drawable.planta_icon) // Substitua pelo seu drawable

        val dynamicContainer = binding.dynamicContainer

        // Adicionar componentes dinamicamente
        for (i in 1..4) {
            val textView = TextView(requireContext())

            if(i != 4){
                textView.apply {
                    text = "Notification $i"
                    textSize = 20f
                    setTextColor(Color.WHITE)
                    setBackgroundResource(R.drawable.retangular_shape)
                    gravity = Gravity.CENTER
                    setPadding(16, 8, 16, 8) // Padding para afastar o texto das bordas
                }

                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, // Largura total do contêiner
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, 16) // Margem inferior para espaçamento entre componentes
                }

                textView.layoutParams = layoutParams
            }
            else{
                textView.apply {
                    text = " Ver Histórico"
                    textSize = 15f
                    setTextColor(Color.WHITE)
                    setBackgroundResource(R.drawable.button)
                    gravity = Gravity.CENTER
                    setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_history_24, 0, 0, 0)
                    setPadding(25, 15, 25, 15) // Padding para afastar o texto das bordas
                    setOnClickListener {
                        findNavController().navigate(R.id.action_nav_home_to_nav_slideshow)
                    }
                }

                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 20, 0, 16) // Margem inferior para espaçamento entre componentes
                }

                textView.layoutParams = layoutParams
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