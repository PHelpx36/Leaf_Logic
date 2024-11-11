package com.fsa.leaf_logic.ui.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.fsa.leaf_logic.PlantPagerAdapter
import com.fsa.leaf_logic.Planta
import com.fsa.leaf_logic.R
import com.fsa.leaf_logic.RetrofitClient
import com.fsa.leaf_logic.UserViewModel
import com.fsa.leaf_logic.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val userViewModel: UserViewModel by activityViewModels()

        userViewModel.userId.value?.let { pesquisarPlantaPorUserId(it) }

        return binding.root
    }

    private fun pesquisarPlantaPorUserId(userId: String){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Chama o endpoint da API para obter as leituras
                val response = RetrofitClient.apiService.getPlantasPorUserId(userId)

                withContext(Dispatchers.Main) {
                    if (response.isNotEmpty()) {
                        // Exibe uma mensagem de sucesso
                        Toast.makeText(
                            requireContext(),
                            "Plantas carregadas!",
                            Toast.LENGTH_SHORT
                        ).show()

                        loadPlantas(response)
                        binding.progressBar.visibility = View.GONE
                        binding.textLoad.visibility = View.GONE
                    } else {
                        binding.progressBar.visibility = View.GONE
                        binding.textLoad.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "Nenhuma planta foi encontrada!",
                            Toast.LENGTH_SHORT
                        ).show()
                        loadbutton()
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    binding.textLoad.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Erro de rede: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: HttpException) {
                binding.progressBar.visibility = View.GONE
                binding.textLoad.visibility = View.GONE
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Erro HTTP: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                binding.textLoad.visibility = View.VISIBLE
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Erro: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun loadbutton() {
        binding.nomePlanta.visibility = View.VISIBLE
        binding.plusSign.visibility = View.VISIBLE

        binding.plusSign.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_to_nav_novaPlanta)
        }
    }

    private fun loadPlantas(plantas: List<Planta>) {
        binding.viewPager.visibility = View.VISIBLE
        binding.nextButton.visibility = View.VISIBLE
        binding.prevButton.visibility = View.VISIBLE

        val navController = findNavController()
        val adapter = PlantPagerAdapter(plantas, navController, requireContext())
        binding.viewPager.adapter = adapter

        binding.nextButton.setOnClickListener {
            if (binding.viewPager.currentItem < plantas.size - 1) {
                binding.viewPager.currentItem += 1
            }
        }

        binding.prevButton.setOnClickListener {
            if (binding.viewPager.currentItem > 0) {
                binding.viewPager.currentItem -= 1
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}