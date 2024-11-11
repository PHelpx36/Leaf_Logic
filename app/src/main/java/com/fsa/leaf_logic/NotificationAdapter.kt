package com.fsa.leaf_logic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationAdapter(private var notifications: List<Notificacao>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val notificationDate: TextView = itemView.findViewById(R.id.notification_date)
        val notificationText: TextView = itemView.findViewById(R.id.notification_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]

        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        try {
            val date = inputFormat.parse(notification.dataEmissicao)
            holder.notificationDate.text = outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            holder.notificationDate.text = notification.dataEmissicao
        }

        holder.notificationText.text = notification.descricao
    }

    fun formatIsoDateString(dateString: String): String {
        // Formato de entrada (ISO 8601 com milissegundos e "T" entre data e hora)
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())

        // Formato de saída desejado
        val outputDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        // Converte a string de data recebida para um objeto Date
        val date: Date? = inputDateFormat.parse(dateString)

        // Formata o objeto Date no novo formato
        return if (date != null) {
            outputDateFormat.format(date)
        } else {
            "Data inválida" // Caso a conversão falhe
        }
    }

    override fun getItemCount(): Int = notifications.size

    // Função para atualizar os dados
    fun updateNotifications(newNotifications: List<Notificacao>) {
        notifications = newNotifications
        notifyDataSetChanged() // Notifica que os dados mudaram
    }
}
