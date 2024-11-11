package com.fsa.leaf_logic.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fsa.leaf_logic.NotificationAdapter
import com.fsa.leaf_logic.R
import com.fsa.leaf_logic.RetrofitClient
import com.fsa.leaf_logic.databinding.FragmentPendingNotificationsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class PendingNotificationFragment : Fragment(R.layout.fragment_pending_notifications) {

    private lateinit var binding: FragmentPendingNotificationsBinding
    private lateinit var notificationAdapter: NotificationAdapter

    private val plantaId: String by lazy {
        requireArguments().getString("plantaId") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPendingNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notificationAdapter = NotificationAdapter(listOf()) // Inicializa com lista vazia
        binding.recyclerViewPending.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = notificationAdapter
        }

        // Chama a função para carregar as notificações
        getPendingNotifications(plantaId)
    }

    private fun getPendingNotifications(plantaId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getNotificacoesPorPlantaPendente(plantaId)

                withContext(Dispatchers.Main) {
                    if (response.isNotEmpty()) {
                        // Exibe uma mensagem de sucesso
                        Toast.makeText(
                            context,
                            "Notificações obtidas com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()
                        notificationAdapter.updateNotifications(response) // Atualiza o Adapter

                    } else {
                        Toast.makeText(
                            context,
                            "Nenhuma notificação encontrada",
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
}
