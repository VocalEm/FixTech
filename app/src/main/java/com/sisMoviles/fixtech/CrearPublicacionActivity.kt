package com.sisMoviles.fixtech

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.sisMoviles.fixtech.api.RetrofitClient
import com.sisMoviles.fixtech.databinding.ActivityCrearpublicacionBinding
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

class CrearPublicacionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrearpublicacionBinding
    private var imagenUri: Uri? = null
    private val PICK_IMAGE = 1001
    private var esEdicion = false
    private var idBorrador = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearpublicacionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        esEdicion = extras?.containsKey("id") == true
        idBorrador = extras?.getInt("id", -1) ?: -1

        var imagenOriginal: String? = null

        if (esEdicion) {
            binding.etCrearPublicacionName.setText(extras?.getString("titulo", ""))
            binding.etCrearPublicacionDescripcion.setText(extras?.getString("descripcion", ""))
            binding.btnCrearPublicacionCrear.text = "Actualizar"

            imagenOriginal = extras?.getString("imagen", "")
            if (!imagenOriginal.isNullOrEmpty()) {
                val urlCompleta = "http://10.0.2.2/$imagenOriginal"
                Glide.with(this)
                    .load(urlCompleta)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(binding.ivCrearPublicacionFoto)
            }
        }


        binding.btnCrearPublicacionImagen.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE)
        }

        binding.btnCrearPublicacionCrear.setOnClickListener {
            val titulo = binding.etCrearPublicacionName.text.toString().trim()
            val descripcion = binding.etCrearPublicacionDescripcion.text.toString().trim()
            val esBorrador = if (binding.swCrearPublicacionBorradores.isChecked) "1" else "0"
            val idUsuario = getSharedPreferences("fixtech_prefs", MODE_PRIVATE).getInt("id", -1)

            if (titulo.isEmpty() || descripcion.isEmpty() || idUsuario == -1) {
                Toast.makeText(this, "Llena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val tituloBody = crearTextoPlano(titulo)
            val descripcionBody = crearTextoPlano(descripcion)
            val idUsuarioBody = crearTextoPlano(idUsuario.toString())
            val isBorradorBody = crearTextoPlano(esBorrador)

            val imagenPart = if (imagenUri != null) {
                val file = uriToFile(imagenUri!!)
                val imagenBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("imagen", file.name, imagenBody)
            } else null

            val imagenActualBody = if (imagenUri == null && !imagenOriginal.isNullOrEmpty()) {
                crearTextoPlano(imagenOriginal)
            } else null

            if (esEdicion && idBorrador != -1) {
                RetrofitClient.instance.actualizarBorrador(
                    crearTextoPlano(idBorrador.toString()),
                    tituloBody,
                    descripcionBody,
                    idUsuarioBody,
                    isBorradorBody,
                    imagenPart,
                    imagenActualBody
                ).enqueue(callback("Borrador actualizado"))
            } else {
                if (imagenPart == null) {
                    Toast.makeText(this, "Selecciona una imagen", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                RetrofitClient.instance.crearPublicacion(
                    tituloBody,
                    descripcionBody,
                    idUsuarioBody,
                    isBorradorBody,
                    imagenPart
                ).enqueue(callback("Publicación creada"))
            }
        }

    }

    private fun callback(mensajeExito: String) = object : Callback<ResponseBody> {
        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            if (response.isSuccessful) {
                Toast.makeText(this@CrearPublicacionActivity, mensajeExito, Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Log.e("Crear/Actualizar", "Error: ${response.code()} - ${response.errorBody()?.string()}")
                Toast.makeText(this@CrearPublicacionActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            Log.e("Crear/Actualizar", "Fallo de red: ${t.message}", t)
            Toast.makeText(this@CrearPublicacionActivity, "Fallo de red", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            imagenUri = data.data
            binding.ivCrearPublicacionFoto.setImageURI(imagenUri)
        }
    }

    private fun crearTextoPlano(valor: String): RequestBody {
        return RequestBody.create("text/plain".toMediaTypeOrNull(), valor)
    }

    private fun uriToFile(uri: Uri): File {
        val inputStream = contentResolver.openInputStream(uri)
        val file = File(cacheDir, "imagen_publicacion.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        return file
    }
}
