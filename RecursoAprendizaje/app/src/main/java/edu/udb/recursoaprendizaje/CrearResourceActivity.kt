package edu.udb.recursoaprendizaje

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CrearResourceActivity : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var typeEditText: EditText
    private lateinit var urlEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var crearButton: Button

    // Obtener las credenciales de autenticación
    var auth_username = ""
    var auth_password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_recurso)

        // Obtención de datos que envia actividad anterior
        val datos: Bundle? = intent.getExtras()
        if (datos != null) {
            auth_username = datos.getString("auth_username").toString()
            auth_password = datos.getString("auth_password").toString()
        }

        nameEditText = findViewById(R.id.editTextName)
        typeEditText = findViewById(R.id.editTextType)
        urlEditText = findViewById(R.id.editTextUrl)
        descriptionEditText = findViewById(R.id.editTextDescription)
        crearButton = findViewById(R.id.btnGuardar)

        crearButton.setOnClickListener {
            val nombre = nameEditText.text.toString()
            val tipo = typeEditText.text.toString()
            val url = urlEditText.text.toString()
            val descripcion = descriptionEditText.text.toString()

            val resource = Resource(0,nombre, tipo, url,descripcion)
            Log.e("API", "auth_username: $auth_username")
            Log.e("API", "auth_password: $auth_password")

            // Crea una instancia de Retrofit con el cliente OkHttpClient
            val retrofit = Retrofit.Builder()
                .baseUrl("https://670c7fe07e5a228ec1d07cbf.mockapi.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            // Crea una instancia del servicio que utiliza la autenticación HTTP básica
            val api = retrofit.create(ResourceApi::class.java)

            api.crearRecurso(resource).enqueue(object : Callback<Resource> {
                override fun onResponse(call: Call<Resource>, response: Response<Resource>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@CrearResourceActivity, "Recurso creado exitosamente", Toast.LENGTH_SHORT).show()
                        val i = Intent(getBaseContext(), MainActivity::class.java)
                        startActivity(i)
                    } else {
                        val error = response.errorBody()?.string()
                        Log.e("API", "Error crear recurso: $error")
                        Toast.makeText(this@CrearResourceActivity, "Error al crear el recurso", Toast.LENGTH_SHORT).show()
                    }

                }

                override fun onFailure(call: Call<Resource>, t: Throwable) {
                    Toast.makeText(this@CrearResourceActivity, "Error al crear el recurso", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}