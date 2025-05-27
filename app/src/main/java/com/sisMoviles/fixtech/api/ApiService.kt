package com.sisMoviles.fixtech.api

import com.sisMoviles.fixtech.modelos.BorradorModel
import com.sisMoviles.fixtech.modelos.PublicacionModel
import com.sisMoviles.fixtech.modelos.UsuarioModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET

import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    @Multipart
    @POST("registro")
    fun registrarUsuarioConImagen(
        @Part("nombre") nombre: RequestBody,
        @Part("apellido") apellido: RequestBody,
        @Part("email") email: RequestBody,
        @Part("username") username: RequestBody,
        @Part("password") password: RequestBody,
        @Part("telefono") telefono: RequestBody,
        @Part imagen: MultipartBody.Part // <-- este es el archivo
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("login")
    fun loginUsuario(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<ResponseBody>

    @Multipart
    @POST("crearpublicacion")
    fun crearPublicacion(
        @Part("titulo") titulo: RequestBody,
        @Part("descripcion") descripcion: RequestBody,
        @Part("id_usuario") idUsuario: RequestBody,
        @Part("is_borrador") isBorrador: RequestBody,
        @Part imagen: MultipartBody.Part
    ): Call<ResponseBody>

    @GET("publicaciones")
    fun obtenerPublicaciones(): Call<List<PublicacionModel>>

    @GET("publicaciones/usuario/{id}")
    fun obtenerPublicacionesPorUsuario(
        @Path("id") idUsuario: Int
    ): Call<List<PublicacionModel>>

    @GET("borradores/usuario/{id}")
    fun obtenerBorradoresPorUsuario(
        @Path("id") idUsuario: Int
    ): Call<List<BorradorModel>>

    @Multipart
    @POST("borrador/actualizar")
    fun actualizarBorrador(
        @Part("id") id: RequestBody,
        @Part("titulo") titulo: RequestBody,
        @Part("descripcion") descripcion: RequestBody,
        @Part("id_usuario") idUsuario: RequestBody,
        @Part("is_borrador") isBorrador: RequestBody,
        @Part imagen: MultipartBody.Part?,
        @Part("imagen_actual") imagenActual: RequestBody? = null
    ): Call<ResponseBody>

    @GET("usuario/{id}")
    fun obtenerUsuarioPorId(
        @Path("id") id: Int
    ): Call<UsuarioModel>

    @Multipart
    @POST("usuario/actualizar")
    fun actualizarUsuario(
        @Part("id") id: RequestBody,
        @Part("nickname") nickname: RequestBody,
        @Part("password") password: RequestBody,
        @Part fotoPerfil: MultipartBody.Part? = null
    ): Call<ResponseBody>

    @GET("publicaciones/buscar/{query}")
    fun buscarPublicaciones(@Path("query") query: String): Call<List<PublicacionModel>>

    @FormUrlEncoded
    @POST("publicacion/eliminar")
    fun eliminarPublicacion(@Field("id") id: Int): Call<ResponseBody>




}
