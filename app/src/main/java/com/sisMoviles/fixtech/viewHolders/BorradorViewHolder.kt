package com.sisMoviles.fixtech.viewHolders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sisMoviles.fixtech.databinding.BorradorItemBinding
import com.sisMoviles.fixtech.modelos.BorradorModel

class BorradorViewHolder(
    view: View,
    private val onEditarClick: (BorradorModel) -> Unit
) : RecyclerView.ViewHolder(view) {

    private val binding = BorradorItemBinding.bind(view)

    fun render(borradorModel: BorradorModel) {
        binding.tvBorradorTitulo.text = borradorModel.titulo
        binding.tvBorradorDescripcion.text = borradorModel.descripcion


        binding.ibBorradorEditar.setOnClickListener {
            onEditarClick(borradorModel)
        }
    }
}
