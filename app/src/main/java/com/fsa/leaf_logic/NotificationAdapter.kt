package com.fsa.leaf_logic

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.PopupWindow
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

        try {
            val date = formatIsoDateString(notification.dataEmissicao)
            holder.notificationDate.text = date
        } catch (e: Exception) {
            e.printStackTrace()
            holder.notificationDate.text = notification.dataEmissicao
        }

        holder.notificationText.text = notification.descricao

        holder.itemView.setOnClickListener {
            val inflater = LayoutInflater.from(holder.itemView.context)
            val popupView = inflater.inflate(R.layout.popup_notification, null)

            val popupWindow = PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
            )

            val fadeInAnimation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.fade_in)
            popupView.startAnimation(fadeInAnimation)

            val titleTextView = popupView.findViewById<TextView>(R.id.titleTextView)
            val messageTextView = popupView.findViewById<TextView>(R.id.messageTextView)
            titleTextView.text = "Notificação ${position + 1}"
            messageTextView.text = notification.descricao

            val closeButton = popupView.findViewById<Button>(R.id.closeButton)

            if(notification.concluida == "True"){
                closeButton.text = "Já Concluída!"
            }else{
                closeButton.setOnClickListener {
                    popupWindow.dismiss()
                }
            }
            popupWindow.showAtLocation(holder.itemView, Gravity.CENTER, 0, 0)
        }
    }

    fun formatIsoDateString(dateString: String): String {
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
        val outputDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val date: Date? = inputDateFormat.parse(dateString)
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
