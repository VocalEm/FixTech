package com.sisMoviles.fixtech.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sisMoviles.fixtech.R
import com.sisMoviles.fixtech.modelos.PublicacionModel
import com.sisMoviles.fixtech.viewHolders.PublicacionViewHolder

class PublicacionAdapter(
    private val publicacionList: List<PublicacionModel>,
    private val onItemClick: (PublicacionModel) -> Unit,
    private val onDeleteClick: ((PublicacionModel) -> Unit)? = null
) : RecyclerView.Adapter<PublicacionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublicacionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.publicacion_item, parent, false)
        return PublicacionViewHolder(view)
    }

    override fun onBindViewHolder(holder: PublicacionViewHolder, position: Int) {
        val publicacion = publicacionList[position]
        holder.render(publicacion)

        // click en el card
        holder.itemView.setOnClickListener {
            onItemClick(publicacion)
        }

        // click en el botón de borrar (solo si es del usuario)
        holder.setOnDeleteClick {
            onDeleteClick?.invoke(publicacion)
        }
    }

    override fun getItemCount(): Int = publicacionList.size
}
