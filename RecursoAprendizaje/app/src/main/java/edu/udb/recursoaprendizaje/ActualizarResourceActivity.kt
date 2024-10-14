package edu.udb.recursoaprendizaje

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ActualizarResourceActivity: AppCompatActivity() {

    private lateinit var api: ResourceApi
    private var resource: Resource? = null

    private lateinit var nameEditText: EditText
    private lateinit var typeEditText: EditText
    private lateinit var urlEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var actualizarButton: Button

    // Obtener las credenciales de autenticación
    val auth_username = "admin"
    val auth_password = "admin123"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actualizar_recurso)

        nameEditText = findViewById(R.id.nameEditText)
        typeEditText = findViewById(R.id.typeEditText)
        urlEditText = findViewById(R.id.urlEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        actualizarButton = findViewById(R.id.actualizarButton)


        // Crea una instancia de Retrofit con el cliente OkHttpClient
        val retrofit = Retrofit.Builder()
            .baseUrl("https://670c7fe07e5a228ec1d07cbf.mockapi.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Crea una instancia del servicio que utiliza la autenticación HTTP básica
        val api = retrofit.create(ResourceApi::class.java)

        // Obtener el ID del alumno de la actividad anterior
        val recursoId = intent.getIntExtra("recurso_id", -1)
        Log.e("API", "alumnoId : $recursoId")

        val nombre = intent.getStringExtra("name").toString()
        val tipo = intent.getStringExtra("type").toString()
        val url = intent.getStringExtra("url").toString()
        val descripcion = intent.getStringExtra("description").toString()

        nameEditText.setText(nombre)
        typeEditText.setText(tipo)
        urlEditText.setText(url)
        descriptionEditText.setText(descripcion)

        val resource = Resource(0,nombre,tipo,url,descripcion)

        // Configurar el botón de actualización
        actualizarButton.setOnClickListener {
            if (resource != null) {
                // Crear un nuevo objeto Alumno con los datos actualizados
                val recursoActualizado = Resource(
                    recursoId,
                    nameEditText.text.toString(),
                    typeEditText.text.toString(),
                    urlEditText.text.toString(),
                    descriptionEditText.text.toString(),
                )
                //Log.e("API", "alumnoActualizado : $alumnoActualizado")

                val jsonRecursoActualizado = Gson().toJson(recursoActualizado)
                Log.d("API", "JSON enviado: $jsonRecursoActualizado")

                val gson = GsonBuilder()
                    .setLenient() // Agrega esta línea para permitir JSON malformado
                    .create()

                // Realizar una solicitud PUT para actualizar el objeto Alumno
                api.actualizarRecurso(recursoId, recursoActualizado).enqueue(object :
                    Callback<Resource> {
                    override fun onResponse(call: Call<Resource>, response: Response<Resource>) {
                        if (response.isSuccessful && response.body() != null) {
                            // Si la solicitud es exitosa, mostrar un mensaje de éxito en un Toast
                            Toast.makeText(this@ActualizarResourceActivity, "Recurso actualizado correctamente", Toast.LENGTH_SHORT).show()
                            val i = Intent(getBaseContext(), MainActivity::class.java)
                            startActivity(i)
                        } else {
                            // Si la respuesta del servidor no es exitosa, manejar el error
                            try {
                                val errorJson = response.errorBody()?.string()
                                val errorObj = JSONObject(errorJson)
                                val errorMessage = errorObj.getString("message")
                                Toast.makeText(this@ActualizarResourceActivity, errorMessage, Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                // Si no se puede parsear la respuesta del servidor, mostrar un mensaje de error genérico
                                Toast.makeText(this@ActualizarResourceActivity, "Error al actualizar el recurso", Toast.LENGTH_SHORT).show()
                                Log.e("API", "Error al parsear el JSON: ${e.message}")
                            }
                        }
                    }

                    override fun onFailure(call: Call<Resource>, t: Throwable) {
                        // Si la solicitud falla, mostrar un mensaje de error en un Toast
                        Log.e("API", "onFailure : $t")
                        Toast.makeText(this@ActualizarResourceActivity, "Error al actualizar el recurso", Toast.LENGTH_SHORT).show()

                        // Si la respuesta JSON está malformada, manejar el error
                        try {
                            val gson = GsonBuilder().setLenient().create()
                            val error = t.message ?: ""
                            val resource = gson.fromJson(error, Resource::class.java)
                        } catch (e: JsonSyntaxException) {
                            Log.e("API", "Error al parsear el JSON: ${e.message}")
                        } catch (e: IllegalStateException) {
                            Log.e("API", "Error al parsear el JSON: ${e.message}")
                        }
                    }
                })
            }
        }
    }
}