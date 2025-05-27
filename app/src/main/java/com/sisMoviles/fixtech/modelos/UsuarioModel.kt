package com.sisMoviles.fixtech.modelos

data class UsuarioModel(
    val id: Int,
    val nombre: String,
    val apellidos: String,
    val correo: String,
    val telefono: String,
    val foto_perfil: String,
    val nickname: String
)
