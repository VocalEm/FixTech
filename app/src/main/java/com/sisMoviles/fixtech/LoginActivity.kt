package com.sisMoviles.fixtech

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sisMoviles.fixtech.api.RetrofitClient
import com.sisMoviles.fixtech.databinding.ActivityLoginBinding
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("fixtech_prefs", MODE_PRIVATE)
        if (prefs.contains("id")) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        // Navegar al registro
        binding.btnLoginRegistro.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }

        // Iniciar sesión
        binding.btnLogin.setOnClickListener {
            val email = binding.etLoginEmail.text.toString().trim()
            val password = binding.etLoginPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            RetrofitClient.instance.loginUsuario(email, password)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            val json = JSONObject(response.body()!!.string())
                            val nombre = json.getJSONObject("usuario").getString("NOMBRE")
                            Toast.makeText(
                                this@LoginActivity,
                                "Bienvenido $nombre",
                                Toast.LENGTH_SHORT
                            ).show()

                            val usuario = json.getJSONObject("usuario")
                            val prefs = getSharedPreferences("fixtech_prefs", MODE_PRIVATE).edit()

                            prefs.putInt("id", usuario.getInt("ID"))
                            prefs.putString("nombre", usuario.getString("NOMBRE"))
                            prefs.putString("apellido", usuario.getString("APELLIDOS"))
                            prefs.putString("nickname", usuario.getString("NICKNAME"))
                            prefs.putString("correo", usuario.getString("CORREO"))
                            prefs.putString("telefono", usuario.getString("TELEFONO"))
                            prefs.putString("foto_perfil", usuario.getString("FOTO_PERFIL"))

                            prefs.apply()

                            // Aquí puedes navegar a otra actividad
                            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            val errorJson = response.errorBody()?.string()
                            val errorMessage = JSONObject(errorJson ?: "{}")
                                .optString("error", "Error desconocido")
                            Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
                            Log.e("LoginAPI", "Error: $errorJson")
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Fallo de conexión: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("LoginAPI", "Fallo de red", t)
                    }
                })
        }
    }
}
