package com.fsa.leaf_logic

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Base64
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView

class PlantPagerAdapter(
    private val plantas: List<Planta>,
    private val navController: NavController // Passa o NavController aqui
) : RecyclerView.Adapter<PlantPagerAdapter.PlantViewHolder>() {

    inner class PlantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomePlanta: TextView = itemView.findViewById(R.id.nomePlanta)
        val imagemPlanta: ImageView = itemView.findViewById(R.id.imagemPlanta)
        val dynamicContainer: LinearLayout = itemView.findViewById(R.id.dynamicContainer)
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
        notifications(holder.dynamicContainer)
    }

    private fun notifications(dynamicContainer: LinearLayout) {
        // Adiciona componentes dinamicamente ao dynamicContainer
        for (i in 1..4) {
            val textView = TextView(dynamicContainer.context)

            if (i != 4) {
                textView.apply {
                    text = "Notification $i"
                    textSize = 20f
                    setTextColor(Color.WHITE)
                    setBackgroundResource(R.drawable.retangular_shape)
                    gravity = Gravity.CENTER
                    setPadding(16, 8, 16, 8)
                }

                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, 16)
                }

                textView.layoutParams = layoutParams
            } else {
                textView.apply {
                    text = " Ver Histórico"
                    textSize = 15f
                    setTextColor(Color.WHITE)
                    setBackgroundResource(R.drawable.button)
                    gravity = Gravity.CENTER
                    setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_history_24, 0, 0, 0)
                    setPadding(25, 15, 25, 15)
                    setOnClickListener {
                        navController.navigate(R.id.action_nav_home_to_nav_slideshow)
                    }
                }

                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 20, 0, 16)
                }

                textView.layoutParams = layoutParams
            }

            // Adiciona o TextView ao dynamicContainer
            dynamicContainer.addView(textView)
        }
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
