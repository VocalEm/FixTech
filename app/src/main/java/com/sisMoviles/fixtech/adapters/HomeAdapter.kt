package com.sisMoviles.fixtech.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sisMoviles.fixtech.R
import com.sisMoviles.fixtech.modelos.PublicacionModel
import com.sisMoviles.fixtech.viewHolders.PublicacionViewHolder

class HomeAdapter (private val homeList:List<PublicacionModel>) : RecyclerView.Adapter<PublicacionViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublicacionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.publicacion_item, parent, false)
        return PublicacionViewHolder(view)
    }

    override fun getItemCount(): Int {
        return homeList.size
    }

    override fun onBindViewHolder(holder: PublicacionViewHolder, position: Int) {
        holder.render(homeList[position])
    }

}