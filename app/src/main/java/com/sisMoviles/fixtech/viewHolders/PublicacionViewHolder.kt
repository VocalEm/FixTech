package com.sisMoviles.fixtech.viewHolders

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sisMoviles.fixtech.R
import com.sisMoviles.fixtech.databinding.PublicacionItemBinding
import com.sisMoviles.fixtech.modelos.PublicacionModel

class PublicacionViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private var binding: PublicacionItemBinding = PublicacionItemBinding.bind(view)

    fun render(publicacionModel: PublicacionModel) {
        binding.tvPublicacionItemTitulo.text = publicacionModel.titulo
        binding.tvPublicacionItemDescripcion.text = publicacionModel.descripcion
        binding.tvPublicacionItemFecha.text = publicacionModel.fecha_creacion
        binding.tvPublicacionItemUsername.text = publicacionModel.autor

        val prefs = itemView.context.getSharedPreferences("fixtech_prefs", android.content.Context.MODE_PRIVATE)
        val idSesion = prefs.getInt("id", -1)

        binding.ibPublicacionItemBorrar.visibility =
            if (publicacionModel.id_usuario == idSesion) View.VISIBLE else View.GONE

        val urlUsuario = "http://10.0.2.2/${publicacionModel.foto_perfil}"
        Glide.with(binding.root.context)
            .load(urlUsuario)
            .placeholder(R.drawable.profile)
            .circleCrop()
            .into(binding.ivPublicacionItemUserImg)

        val urlImagen = "http://10.0.2.2/${publicacionModel.imagen}"
        Glide.with(binding.root.context)
            .load(urlImagen)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(binding.ivPublicacionItemPublicacionImg)

        Log.d("DEBUG_BORRAR", "Sesion: $idSesion | Publicacion de: ${publicacionModel.id_usuario}")

    }

    fun setOnDeleteClick(onDelete: () -> Unit) {
        binding.ibPublicacionItemBorrar.setOnClickListener { onDelete() }
    }
}
