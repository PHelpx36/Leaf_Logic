package com.fsa.leaf_logic

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class PlantPagerAdapter(
    private val plantas: List<Planta>,
    private val navController: NavController,
    private val context: Context
) : RecyclerView.Adapter<PlantPagerAdapter.PlantViewHolder>() {

    inner class PlantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomePlanta: TextView = itemView.findViewById(R.id.nomePlanta)
        val imagemPlanta: ImageView = itemView.findViewById(R.id.imagemPlanta)
        val dynamicContainer: LinearLayout = itemView.findViewById(R.id.dynamicContainer)
        val chartsNav: Button = itemView.findViewById(R.id.charts)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_plant, parent, false)
        return PlantViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val planta = plantas[position]
        holder.nomePlanta.text = planta.nome

        val string = carregarImagemBase64(planta.imagem)
        // Carregue a imagem usando Glide ou Picasso
        if(string != null){
            holder.imagemPlanta.setImageBitmap(string)
        }
        else{
            holder.imagemPlanta.setImageResource(R.drawable.baseline_local_florist_24)
        }

        holder.dynamicContainer.removeAllViews()

        // Chama a função notifications para adicionar notificações dinamicamente
        getNotifications(planta.id.toString(), holder.dynamicContainer, planta.nome)

        holder.chartsNav.setOnClickListener{
            val bundle = Bundle().apply {
                putString("equipamentoId", planta.equipamentoId.toString())
            }
            navController.navigate(R.id.action_nav_home_to_nav_slideshow, bundle)
        }
    }

    private fun notifications(dynamicContainer: LinearLayout, notifications: List<Notificacao>, plantaId: String, plantaNome: String) {
        var layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 0, 0, 16)
        }

        val specialLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 16, 0, 16) // ajuste de margens conforme necessário
            gravity = Gravity.CENTER_HORIZONTAL // centraliza horizontalmente no LinearLayout
        }

        if(notifications.isNotEmpty()){
            for (i in 0..2) {
                val textView = TextView(dynamicContainer.context)
                if(notifications.count() > i){
                    textView.apply {
                        text = notifications[i].dever
                        textSize = 20f
                        setTextColor(Color.WHITE)
                        setBackgroundResource(R.drawable.retangular_shape)
                        gravity = Gravity.CENTER
                        setPadding(16, 8, 16, 8)
                        setOnClickListener {
                            // Infla o layout do pop-up
                            val inflater = LayoutInflater.from(dynamicContainer.context)
                            val popupView = inflater.inflate(R.layout.popup_notification, null)

                            // Cria o PopupWindow
                            val popupWindow = PopupWindow(
                                popupView,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                true // Permite fechar ao clicar fora
                            )

                            val fadeInAnimation = AnimationUtils.loadAnimation(dynamicContainer.context, R.anim.fade_in)
                            popupView.startAnimation(fadeInAnimation) // Aplica a animação ao pop-up

                            // Define as informações da notificação
                            val titleTextView = popupView.findViewById<TextView>(R.id.titleTextView)
                            val messageTextView = popupView.findViewById<TextView>(R.id.messageTextView)
                            titleTextView.text = "Notificação ${i + 1}"
                            messageTextView.text = notifications[i].descricao // Ajuste conforme a estrutura da sua notificação

                            // Botão de fechar o pop-up
                            val closeButton = popupView.findViewById<Button>(R.id.closeButton)
                            closeButton.setOnClickListener {
                                ConcluirNotificacao(notifications[i].id.toString(), textView, dynamicContainer)
                                popupWindow.dismiss()
                            }

                            // Exibe o pop-up no centro da tela
                            popupWindow.showAtLocation(dynamicContainer, Gravity.CENTER, 0, 0)
                        }

                        textView.layoutParams = layoutParams
                    }
                }

                if(i == 2){
                    textView.apply {
                        text = " Ver Histórico"
                        textSize = 15f
                        setTextColor(Color.WHITE)
                        setBackgroundResource(R.drawable.button)
                        gravity = Gravity.CENTER
                        setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_history_24, 0, 0, 0)
                        setPadding(25, 15, 25, 15)
                        setOnClickListener {
                            val bundle = Bundle().apply {
                                putString("plantaId", plantaId)
                                putString("plantaNome", plantaNome)
                                putString("pendentes", notifications.size.toString())
                            }

                            navController.navigate(R.id.action_nav_home_to_nav_notification, bundle)
                        }
                    }

                    textView.layoutParams = specialLayoutParams
                }

                dynamicContainer.addView(textView)
            }
        }else{
            val textView = TextView(dynamicContainer.context)

            val fullWidthLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                200 // altura maior; ajuste conforme necessário
            ).apply {
                setMargins(0, 16, 0, 16) // margens conforme necessário
            }

            textView.apply {
                text = "Tudo em dia!"
                textSize = 20f
                setTextColor(Color.WHITE)
                setBackgroundColor(Color.parseColor("#006400")) // verde escuro
                gravity = Gravity.CENTER // centraliza o texto no meio do TextView
                setPadding(16, 8, 16, 8) // ajuste do padding conforme necessário
            }

            // Aplica o layoutParams específico para largura total e altura maior
            textView.layoutParams = fullWidthLayoutParams
            dynamicContainer.addView(textView)

            textView.apply {
                text = " Ver Histórico"
                textSize = 15f
                setTextColor(Color.WHITE)
                setBackgroundResource(R.drawable.button)
                gravity = Gravity.CENTER
                setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_history_24, 0, 0, 0)
                setPadding(25, 15, 25, 15)
                setOnClickListener {
                    val bundle = Bundle().apply {
                        putString("plantaId", plantaId)
                    }

                    navController.navigate(R.id.action_nav_home_to_nav_notification, bundle)
                }
            }

            textView.layoutParams = specialLayoutParams

            dynamicContainer.addView(textView)
        }
    }

    private fun getNotifications(plantaId: String, dynamicContainer: LinearLayout, plantaNome: String){
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

                        notifications(dynamicContainer, response, plantaId, plantaNome)

                    } else {
                        Toast.makeText(
                            context,
                            "Nenhuma notificações encontrada",
                            Toast.LENGTH_SHORT
                        ).show()
                        notifications(dynamicContainer, response, plantaId, plantaNome)
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

    private fun ConcluirNotificacao(notificationId: String, textView: TextView, dynamicContainer: LinearLayout){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.concluirNotificacao(notificationId)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        // Exibe uma mensagem de sucesso
                        Toast.makeText(
                            context,
                            "Notificações concluída com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()

                        apagarComponenteNotificacao(textView, dynamicContainer)

                    } else {
                        Toast.makeText(
                            context,
                            "Erro, não foi possivel completar essa ação.",
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

    private fun apagarComponenteNotificacao(textView: TextView, dynamicContainer: LinearLayout) {
        // Remover o TextView do layout
        dynamicContainer.removeView(textView)
    }

    private fun carregarImagemBase64(base64String: String) : Bitmap? {
        // Decodifica a string Base64 em um array de bytes
        if(base64String != null){
            val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        }

        return null
    }

    override fun getItemCount(): Int = plantas.size
}
