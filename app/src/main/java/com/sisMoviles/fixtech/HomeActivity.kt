package com.sisMoviles.fixtech

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sisMoviles.fixtech.adapters.PublicacionAdapter
import com.sisMoviles.fixtech.api.RetrofitClient
import com.sisMoviles.fixtech.databinding.ActivityHomeBinding
import com.sisMoviles.fixtech.modelos.PublicacionModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var adapter: PublicacionAdapter
    private var publicacionesList: List<PublicacionModel> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ibHomeBuscar.setOnClickListener {
            val texto = binding.etHomeBuscar.text.toString().trim()
            if (texto.isNotEmpty()) {
                buscarPublicaciones(texto)
            } else {
                cargarPublicaciones()
            }
        }


        binding.rcHomepublicaciones.layoutManager = LinearLayoutManager(this)

        binding.ibHomeUser.setOnClickListener {
            val intent = Intent(this, PerfilUsuarioActivity::class.java)
            startActivity(intent)
        }

        binding.ibHomeAgregarPublicacion.setOnClickListener {
            val intent = Intent(this, CrearPublicacionActivity::class.java)
            startActivity(intent)
        }

        binding.ibHomeButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        cargarPublicaciones()
    }

    private fun cargarPublicaciones() {
        RetrofitClient.instance.obtenerPublicaciones()
            .enqueue(object : Callback<List<PublicacionModel>> {
                override fun onResponse(
                    call: Call<List<PublicacionModel>>,
                    response: Response<List<PublicacionModel>>
                ) {
                    if (response.isSuccessful) {
                        publicacionesList = response.body() ?: emptyList()

                        // 🔁 Adapter con click que abre perfil del autor
                        adapter = PublicacionAdapter(
                            publicacionesList,
                            onItemClick = { publicacion ->
                                val intent = Intent(this@HomeActivity, PerfilUsuarioActivity::class.java)
                                intent.putExtra("id_perfil", publicacion.id_usuario)
                                startActivity(intent)
                            }
                        )


                        binding.rcHomepublicaciones.adapter = adapter
                        Log.d("HomeAPI", "Publicaciones cargadas: ${publicacionesList.size}")
                    } else {
                        Log.e("HomeAPI", "Error ${response.code()}: ${response.errorBody()?.string()}")
                        Toast.makeText(this@HomeActivity, "Error al cargar publicaciones", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<PublicacionModel>>, t: Throwable) {
                    Log.e("HomeAPI", "Fallo de conexión: ${t.message}", t)
                    Toast.makeText(this@HomeActivity, "Fallo de red", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onResume() {
        super.onResume()
        cargarPublicaciones()
    }

    private fun buscarPublicaciones(query: String) {
        RetrofitClient.instance.buscarPublicaciones(query)
            .enqueue(object : Callback<List<PublicacionModel>> {
                override fun onResponse(
                    call: Call<List<PublicacionModel>>,
                    response: Response<List<PublicacionModel>>
                ) {
                    if (response.isSuccessful) {
                        val resultados = response.body() ?: emptyList()
                        adapter = PublicacionAdapter(
                            publicacionesList,
                            onItemClick = { publicacion ->
                                val intent = Intent(this@HomeActivity, PerfilUsuarioActivity::class.java)
                                intent.putExtra("id_perfil", publicacion.id_usuario)
                                startActivity(intent)
                            },
                            onDeleteClick = { publicacion ->
                                AlertDialog.Builder(this@HomeActivity)
                                    .setTitle("¿Eliminar publicación?")
                                    .setMessage("Esta acción no se puede deshacer.")
                                    .setPositiveButton("Eliminar") { _, _ ->
                                        RetrofitClient.instance.eliminarPublicacion(publicacion.id)
                                            .enqueue(object : Callback<ResponseBody> {
                                                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                                    if (response.isSuccessful) {
                                                        Toast.makeText(this@HomeActivity, "Publicación eliminada", Toast.LENGTH_SHORT).show()
                                                        cargarPublicaciones() // 🔄 recargar lista
                                                    } else {
                                                        Toast.makeText(this@HomeActivity, "Error al eliminar", Toast.LENGTH_SHORT).show()
                                                    }
                                                }

                                                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                                    Toast.makeText(this@HomeActivity, "Fallo de red", Toast.LENGTH_SHORT).show()
                                                }
                                            })
                                    }
                                    .setNegativeButton("Cancelar", null)
                                    .show()
                            }
                        )

                        binding.rcHomepublicaciones.adapter = adapter
                    } else {
                        Toast.makeText(this@HomeActivity, "Error al buscar", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<PublicacionModel>>, t: Throwable) {
                    Toast.makeText(this@HomeActivity, "Fallo de red", Toast.LENGTH_SHORT).show()
                }
            })
    }

}
