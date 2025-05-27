package com.sisMoviles.fixtech

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sisMoviles.fixtech.api.RetrofitClient
import com.sisMoviles.fixtech.databinding.ActivityRegistroBinding
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

class RegistroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroBinding
    private val PICK_IMAGE_REQUEST = 1
    private var uriImagenSeleccionada: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Seleccionar imagen
        binding.btnRegistroImagen.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Registrar usuario
        binding.btnRegistroRegistro.setOnClickListener {
            val nombre = binding.etRegistroName.text.toString().trim()
            val apellido = binding.etRegistroApellidos.text.toString().trim()
            val email = binding.etRegistroEmail.text.toString().trim()
            val username = binding.etRegistroUsername.text.toString().trim()
            val password = binding.etRegistroPassword.text.toString().trim()
            val telefono = binding.etRegistroPhone.text.toString().trim()

            Log.d("RegistroInputs", "nombre: $nombre")
            Log.d("RegistroInputs", "apellido: $apellido")
            Log.d("RegistroInputs", "email: $email")
            Log.d("RegistroInputs", "username: $username")
            Log.d("RegistroInputs", "password: $password")
            Log.d("RegistroInputs", "telefono: $telefono")

            if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() ||
                username.isEmpty() || password.isEmpty() || telefono.isEmpty() ||
                uriImagenSeleccionada == null
            ) {
                Toast.makeText(this, "Completa todos los campos e imagen", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Crear cuerpo de texto
            val nombreBody = crearTextoPlano(nombre)
            val apellidoBody = crearTextoPlano(apellido)
            val emailBody = crearTextoPlano(email)
            val usernameBody = crearTextoPlano(username)
            val passwordBody = crearTextoPlano(password)
            val telefonoBody = crearTextoPlano(telefono)

            // Convertir Uri a File
            val inputStream = contentResolver.openInputStream(uriImagenSeleccionada!!)
            val file = File(cacheDir, "fotoPerfil.jpg")
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            val imagenRequest = file.asRequestBody("image/*".toMediaTypeOrNull())
            val imagenPart = MultipartBody.Part.createFormData("imagen", file.name, imagenRequest)

            // Enviar solicitud con Retrofit
            RetrofitClient.instance.registrarUsuarioConImagen(
                nombreBody, apellidoBody, emailBody, usernameBody,
                passwordBody, telefonoBody, imagenPart
            ).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@RegistroActivity, "Usuario registrado con imagen", Toast.LENGTH_SHORT).show()
                        Log.d("RegistroAPI", "Registro exitoso: ${response.code()}")
                    } else {
                        Log.e("RegistroAPI", "Error en respuesta: ${response.code()} - ${response.errorBody()?.string()}")
                        Toast.makeText(this@RegistroActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("RegistroAPI", "Fallo de red: ${t.message}", t)
                    Toast.makeText(this@RegistroActivity, "Fallo de red: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    // Muestra la imagen seleccionada en el ImageView
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            uriImagenSeleccionada = data.data
            binding.ivRegistroPreview.setImageURI(uriImagenSeleccionada)
        }
    }

    private fun crearTextoPlano(valor: String): RequestBody {
        return RequestBody.create("text/plain".toMediaTypeOrNull(), valor)
    }
}
