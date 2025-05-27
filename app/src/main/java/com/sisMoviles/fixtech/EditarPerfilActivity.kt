package com.sisMoviles.fixtech

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.sisMoviles.fixtech.api.RetrofitClient
import com.sisMoviles.fixtech.databinding.ActivityEditarPerfilBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class EditarPerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarPerfilBinding
    private var imagenUri: Uri? = null
    private val PICK_IMAGE = 1001
    private var fotoPerfilActual: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("fixtech_prefs", MODE_PRIVATE)
        val idUsuario = prefs.getInt("id", -1)
        val nickname = prefs.getString("nickname", "") ?: ""
        val fotoPerfil = prefs.getString("foto_perfil", "") ?: ""
        val nombreCompleto = prefs.getString("nombre", "") + " " + prefs.getString("apellido", "")
        fotoPerfilActual = fotoPerfil

        binding.tvEditarUserNombre.text = nombreCompleto
        binding.etEditarUserNickname.setText(nickname)

        Glide.with(this)
            .load("http://10.0.2.2/$fotoPerfil")
            .placeholder(R.drawable.profile)
            .circleCrop()
            .into(binding.ivEditarUserImg)

        // Cargar nueva imagen
        binding.ibEditarUserCargarImg.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE)
        }

        // Guardar cambios
        binding.buttonsave.setOnClickListener {
            val nuevoNick = binding.etEditarUserNickname.text.toString().trim()
            val nuevaPass = binding.etEditarUserPassword.text.toString().trim()

            if (nuevoNick.isEmpty() || idUsuario == -1) {
                Toast.makeText(this, "Nickname requerido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nickBody = crearTextoPlano(nuevoNick)
            val passBody = crearTextoPlano(nuevaPass)
            val idBody = crearTextoPlano(idUsuario.toString())
            val imagenPart = if (imagenUri != null) {
                val file = uriToFile(imagenUri!!)
                val imagenBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("foto_perfil", file.name, imagenBody)
            } else null

            RetrofitClient.instance.actualizarUsuario(
                idBody, nickBody, passBody, imagenPart
            ).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@EditarPerfilActivity, "Perfil actualizado", Toast.LENGTH_SHORT).show()

                        // ✅ Actualizar SharedPreferences
                        val prefs = getSharedPreferences("fixtech_prefs", MODE_PRIVATE)
                        val editor = prefs.edit()

                        editor.putString("nickname", binding.etEditarUserNickname.text.toString())

                        if (imagenUri != null) {
                            // asumimos que la API devuelve el nuevo nombre del archivo como respuesta (puedes mejorar esto)
                            val nuevoNombreArchivo = "uploads/${System.currentTimeMillis()}_${idUsuario}.jpg"
                            editor.putString("foto_perfil", nuevoNombreArchivo)
                        }

                        editor.apply()

                        finish()

                } else {
                        Toast.makeText(this@EditarPerfilActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@EditarPerfilActivity, "Fallo de red", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // Volver
        binding.buttonback.setOnClickListener {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            imagenUri = data.data
            binding.ivEditarUserImg.setImageURI(imagenUri)
        }
    }

    private fun crearTextoPlano(valor: String): RequestBody {
        return RequestBody.create("text/plain".toMediaTypeOrNull(), valor)
    }

    private fun uriToFile(uri: Uri): File {
        val inputStream = contentResolver.openInputStream(uri)
        val file = File(cacheDir, "foto_perfil.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        return file
    }
}
