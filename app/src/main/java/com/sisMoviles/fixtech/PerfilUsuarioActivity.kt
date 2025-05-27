package com.sisMoviles.fixtech

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.sisMoviles.fixtech.adapters.PublicacionAdapter
import com.sisMoviles.fixtech.api.RetrofitClient
import com.sisMoviles.fixtech.databinding.ActivityPerfilusuarioBinding
import com.sisMoviles.fixtech.modelos.PublicacionModel
import com.sisMoviles.fixtech.modelos.UsuarioModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PerfilUsuarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilusuarioBinding
    private lateinit var adapter: PublicacionAdapter
    private var idSesion: Int = -1
    private var idPerfil: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilusuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("fixtech_prefs", MODE_PRIVATE)
        idSesion = prefs.getInt("id", -1)
        idPerfil = intent.getIntExtra("id_perfil", idSesion)

        val esPropio = idPerfil == idSesion

        binding.btnPerfilEditar.visibility = if (esPropio) View.VISIBLE else View.GONE
        binding.btnPerfilCerrarSesion.visibility = if (esPropio) View.VISIBLE else View.GONE

        // navegación
        binding.btnPerfilEditar.setOnClickListener {
            startActivity(Intent(this, EditarPerfilActivity::class.java))
        }

        binding.btnPerfilCerrarSesion.setOnClickListener {
            prefs.edit().clear().apply()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        binding.ibPerfilHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        binding.ibPerfilBorradores.setOnClickListener {
            startActivity(Intent(this, BorradorActivity::class.java))
        }

        if (esPropio) {
            val nombre = prefs.getString("nombre", "") ?: ""
            val apellido = prefs.getString("apellido", "") ?: ""
            val nickname = prefs.getString("nickname", "") ?: ""
            val correo = prefs.getString("correo", "") ?: ""
            val telefono = prefs.getString("telefono", "") ?: ""
            val fotoPerfil = prefs.getString("foto_perfil", "") ?: ""

            binding.tvPerfilNombre.text = "$nombre $apellido"
            binding.tvPerfilUser.text = nickname
            binding.tvPerfilCorreo.text = correo
            binding.tvPerfilTelefono.text = telefono

            Glide.with(this)
                .load("http://10.0.2.2/$fotoPerfil")
                .placeholder(R.drawable.profile)
                .circleCrop()
                .into(binding.ivPerfilUserImg)
        } else {
            binding.tvPerfilNombre.text = "Usuario"
            binding.tvPerfilCorreo.text = "-"
            binding.tvPerfilTelefono.text = "-"
            binding.tvPerfilUser.text = "-"
            cargarDatosUsuario(idPerfil)
        }

        cargarPublicacionesDeUsuario(idPerfil)
    }

    private fun cargarPublicacionesDeUsuario(idUsuario: Int) {
        RetrofitClient.instance.obtenerPublicacionesPorUsuario(idUsuario)
            .enqueue(object : Callback<List<PublicacionModel>> {
                override fun onResponse(
                    call: Call<List<PublicacionModel>>,
                    response: Response<List<PublicacionModel>>
                ) {
                    if (response.isSuccessful) {
                        val publicaciones = response.body() ?: emptyList()
                        adapter = PublicacionAdapter(
                            publicaciones,
                            onItemClick = { publicacion ->
                                val intent = Intent(this@PerfilUsuarioActivity, PerfilUsuarioActivity::class.java)
                                intent.putExtra("id_perfil", publicacion.id_usuario)
                                startActivity(intent)
                            },
                            onDeleteClick = { publicacion ->
                                mostrarDialogoEliminar(publicacion)
                            }
                        )

                        binding.rcPerfilPublicaciones.layoutManager = LinearLayoutManager(this@PerfilUsuarioActivity)
                        binding.rcPerfilPublicaciones.adapter = adapter
                    } else {
                        Log.e("PerfilAPI", "Error ${response.code()}: ${response.errorBody()?.string()}")
                        Toast.makeText(this@PerfilUsuarioActivity, "Error al cargar publicaciones", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<PublicacionModel>>, t: Throwable) {
                    Log.e("PerfilAPI", "Fallo de conexión: ${t.message}", t)
                    Toast.makeText(this@PerfilUsuarioActivity, "Fallo de red", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun mostrarDialogoEliminar(publicacion: PublicacionModel) {
        AlertDialog.Builder(this)
            .setTitle("¿Eliminar publicación?")
            .setMessage("Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                RetrofitClient.instance.eliminarPublicacion(publicacion.id)
                    .enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@PerfilUsuarioActivity, "Eliminado", Toast.LENGTH_SHORT).show()
                                cargarPublicacionesDeUsuario(idPerfil)
                            } else {
                                Toast.makeText(this@PerfilUsuarioActivity, "Error al eliminar", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Toast.makeText(this@PerfilUsuarioActivity, "Fallo de red", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        cargarPublicacionesDeUsuario(idPerfil)

        val prefs = getSharedPreferences("fixtech_prefs", MODE_PRIVATE)
        if (idPerfil == prefs.getInt("id", -1)) {
            val nombre = prefs.getString("nombre", "") ?: ""
            val apellido = prefs.getString("apellido", "") ?: ""
            val nickname = prefs.getString("nickname", "") ?: ""
            val correo = prefs.getString("correo", "") ?: ""
            val telefono = prefs.getString("telefono", "") ?: ""
            val fotoPerfil = prefs.getString("foto_perfil", "") ?: ""

            binding.tvPerfilNombre.text = "$nombre $apellido"
            binding.tvPerfilUser.text = nickname
            binding.tvPerfilCorreo.text = correo
            binding.tvPerfilTelefono.text = telefono

            Glide.with(this)
                .load("http://10.0.2.2/$fotoPerfil")
                .placeholder(R.drawable.profile)
                .circleCrop()
                .into(binding.ivPerfilUserImg)
        }
    }

    private fun cargarDatosUsuario(id: Int) {
        RetrofitClient.instance.obtenerUsuarioPorId(id)
            .enqueue(object : Callback<UsuarioModel> {
                override fun onResponse(call: Call<UsuarioModel>, response: Response<UsuarioModel>) {
                    if (response.isSuccessful) {
                        val usuario = response.body() ?: return
                        binding.tvPerfilNombre.text = "${usuario.nombre} ${usuario.apellidos}"
                        binding.tvPerfilUser.text = usuario.nickname
                        binding.tvPerfilCorreo.text = usuario.correo
                        binding.tvPerfilTelefono.text = usuario.telefono

                        Glide.with(this@PerfilUsuarioActivity)
                            .load("http://10.0.2.2/${usuario.foto_perfil}")
                            .placeholder(R.drawable.profile)
                            .circleCrop()
                            .into(binding.ivPerfilUserImg)
                    }
                }

                override fun onFailure(call: Call<UsuarioModel>, t: Throwable) {
                    Log.e("PerfilAPI", "Fallo al obtener usuario: ${t.message}", t)
                }
            })
    }
}
