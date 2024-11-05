package com.fsa.leaf_logic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class PlantPagerAdapter(private val plantas: List<Planta>) : RecyclerView.Adapter<PlantPagerAdapter.PlantViewHolder>() {

    inner class PlantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomePlanta: TextView = itemView.findViewById(R.id.nomePlanta)
        val imagemPlanta: ImageView = itemView.findViewById(R.id.imagemPlanta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_plant, parent, false)
        return PlantViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val planta = plantas[position]
        holder.nomePlanta.text = planta.nome
        // Carregue a imagem usando Glide ou Picasso
        Picasso.get()
            .load(planta.imagem)
            .into(holder.imagemPlanta)
    }

    override fun getItemCount(): Int = plantas.size
}
