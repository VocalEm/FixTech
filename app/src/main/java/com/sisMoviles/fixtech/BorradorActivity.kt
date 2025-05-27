package com.sisMoviles.fixtech

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sisMoviles.fixtech.adapters.BorradorAdapter
import com.sisMoviles.fixtech.api.RetrofitClient
import com.sisMoviles.fixtech.databinding.ActivityBorradoresBinding
import com.sisMoviles.fixtech.modelos.BorradorModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BorradorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBorradoresBinding
    private lateinit var adapter: BorradorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBorradoresBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val idUsuario = getSharedPreferences("fixtech_prefs", MODE_PRIVATE).getInt("id", -1)

        if (idUsuario != -1) {
            cargarBorradores(idUsuario)
        } else {
            Toast.makeText(this, "ID de usuario no disponible", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarBorradores(idUsuario: Int) {
        RetrofitClient.instance.obtenerBorradoresPorUsuario(idUsuario)
            .enqueue(object : Callback<List<BorradorModel>> {
                override fun onResponse(
                    call: Call<List<BorradorModel>>,
                    response: Response<List<BorradorModel>>
                ) {
                    if (response.isSuccessful) {
                        val borradores = response.body() ?: emptyList()

                        adapter = BorradorAdapter(borradores) { borrador ->
                            val intent = Intent(this@BorradorActivity, CrearPublicacionActivity::class.java)
                            intent.putExtra("id", borrador.id)
                            intent.putExtra("titulo", borrador.titulo)
                            intent.putExtra("descripcion", borrador.descripcion)
                            intent.putExtra("imagen", borrador.imagen) // 🔥 aquí agregas la ruta
                            startActivity(intent)
                        }

                        binding.rcBorradores.layoutManager = LinearLayoutManager(this@BorradorActivity)
                        binding.rcBorradores.adapter = adapter
                    } else {
                        Log.e("BorradorAPI", "Error ${response.code()}: ${response.errorBody()?.string()}")
                        Toast.makeText(this@BorradorActivity, "Error al cargar borradores", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<BorradorModel>>, t: Throwable) {
                    Log.e("BorradorAPI", "Fallo de red: ${t.message}", t)
                    Toast.makeText(this@BorradorActivity, "Fallo de red", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onResume() {
        super.onResume()
        val prefs = getSharedPreferences("fixtech_prefs", MODE_PRIVATE)
        val idUsuario = prefs.getInt("id", -1)

        cargarBorradores(idUsuario) // o cargarBorradores(idUsuario)
    }
}

