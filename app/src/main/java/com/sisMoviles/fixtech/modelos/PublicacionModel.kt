package com.sisMoviles.fixtech.modelos

data class PublicacionModel(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val imagen: String,
    val fecha_creacion: String,
    val autor: String,
    val id_usuario: Int,
    val foto_perfil : String
)
