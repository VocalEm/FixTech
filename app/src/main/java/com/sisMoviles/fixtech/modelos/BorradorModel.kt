package com.sisMoviles.fixtech.modelos


data class BorradorModel(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val imagen: String,             // para mostrar la imagen de la publicación
    val fecha_creacion: String,    // si quieres mostrar cuándo fue creado
    val autor: String              // o simplemente puedes usar el nickname
)
