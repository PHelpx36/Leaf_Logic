package com.fsa.leaf_logic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NotificationViewModel : ViewModel() {
    private val _notifications = MutableLiveData<List<Notificacao>?>()
    val notifications: MutableLiveData<List<Notificacao>?> get() = _notifications

    fun concluirNotificacao(notificacaoId: String) {
        // Atualiza a lista de notificações localmente
        val updatedNotifications = _notifications.value?.map {
            if (it.id.toString() == notificacaoId) {
                it.concluida = "True"  // Marca a notificação como concluída
            }
            it
        }
        _notifications.value = updatedNotifications  // Atualiza a LiveData
    }
}

