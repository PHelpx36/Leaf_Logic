package com.fsa.leaf_logic.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.fsa.leaf_logic.NotificationPagerAdapter
import com.fsa.leaf_logic.RetrofitClient
import com.fsa.leaf_logic.databinding.FragmentNotificationBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

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

        binding.plantName.text = args.plantaNome
        getPendingNotifications(plantaId)
        getConcluidasNotifications(plantaId)

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

    private fun getPendingNotifications(plantaId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getNotificacoesPorPlantaConcluida(plantaId)

                withContext(Dispatchers.Main) {
                    if (response.isNotEmpty()) {
                        binding.placarPendentes.text = response.size.toString()

                    } else {
                        Toast.makeText(
                            context,
                            "Nenhuma notificação concluída.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Erro de rede: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Erro HTTP: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun getConcluidasNotifications(plantaId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getNotificacoesPorPlantaConcluida(plantaId)

                withContext(Dispatchers.Main) {
                    if (response.isNotEmpty()) {
                        binding.placarConcluidas.text = response.size.toString()

                    } else {
                        Toast.makeText(
                            context,
                            "Nenhuma notificação concluída.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Erro de rede: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Erro HTTP: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}