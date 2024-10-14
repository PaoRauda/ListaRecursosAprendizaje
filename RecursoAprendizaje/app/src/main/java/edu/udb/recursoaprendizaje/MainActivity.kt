package edu.udb.recursoaprendizaje

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ResourceAdapter
    private lateinit var api: ResourceApi
    private lateinit var editTextResourceId: EditText

    //Credenciales predeterminadas de autenticación
    val auth_username = "admin"
    val auth_password = "admin123"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fab_agregar: FloatingActionButton = findViewById<FloatingActionButton>(R.id.fab_agregar)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Crea una instancia de Retrofit con el cliente OkHttpClient
        val retrofit = Retrofit.Builder()
            .baseUrl("https://670c7fe07e5a228ec1d07cbf.mockapi.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Crea una instancia del servicio que utiliza la autenticación HTTP básica
        api = retrofit.create(ResourceApi::class.java)

        cargarDatos(api)

        editTextResourceId = findViewById(R.id.editTextResourceId)
        val buttonBuscar: Button = findViewById(R.id.buttonBuscar)

        buttonBuscar.setOnClickListener {
            val resourceIdStr = editTextResourceId.text.toString()
            val resourceId = resourceIdStr.toIntOrNull()
            if (resourceId != null) {
                buscarRecurso(resourceId, api)
            } else {
                Toast.makeText(this, "Por favor, ingrese un ID válido", Toast.LENGTH_SHORT).show()
            }
        }

        val buttonMostrarTodos: Button = findViewById(R.id.buttonMostrarTodos)

        buttonMostrarTodos.setOnClickListener {
            cargarDatos(api) // Llama a la función para cargar todos los recursos
        }

        // Cuando el usuario quiere agregar un nuevo registro
        fab_agregar.setOnClickListener(View.OnClickListener {
            val i = Intent(getBaseContext(), CrearResourceActivity::class.java)
            i.putExtra("auth_username", auth_username)
            i.putExtra("auth_password", auth_password)
            startActivity(i)
        })
    }

    override fun onResume() {
        super.onResume()
        cargarDatos(api)
    }

    private fun cargarDatos(api: ResourceApi) {
        val call = api.obtenerRecursos()
        call.enqueue(object : Callback<List<Resource>> {
            override fun onResponse(call: Call<List<Resource>>, response: Response<List<Resource>>) {
                if (response.isSuccessful) {
                    val resources = response.body()
                    if (resources != null) {
                        adapter = ResourceAdapter(resources)
                        recyclerView.adapter = adapter

                        // Establecemos el escuchador de clics en el adaptador
                        adapter.setOnItemClickListener(object : ResourceAdapter.OnItemClickListener {
                            override fun onItemClick(resource: Resource) {
                                val opciones = arrayOf("Modificar recurso", "Eliminar recurso")

                                AlertDialog.Builder(this@MainActivity)
                                    .setTitle(resource.name)
                                    .setItems(opciones) { dialog, index ->
                                        when (index) {
                                            0 -> Modificar(resource)
                                            1 -> eliminarRecurso(resource, api)
                                        }
                                    }
                                    .setNegativeButton("Cancelar", null)
                                    .show()
                            }
                        })
                    }
                } else {
                    val error = response.errorBody()?.string()
                    Log.e("API", "Error al obtener los recursos: $error")
                    Toast.makeText(
                        this@MainActivity,
                        "Error al obtener los recursos 1",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Resource>>, t: Throwable) {
                Log.e("API", "Error al obtener los recursos: ${t.message}")
                Toast.makeText(
                    this@MainActivity,
                    "Error al obtener los recursos 2",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun buscarRecurso(resourceId: Int, api: ResourceApi) {
        val llamada = api.obtenerRecursoPorId(resourceId)
        llamada.enqueue(object : Callback<Resource> {
            override fun onResponse(call: Call<Resource>, response: Response<Resource>) {
                if (response.isSuccessful && response.body() != null) {
                    val resourceData = response.body()!!

                    // Aquí puedes crear una lista con un solo recurso para el adaptador
                    val resourcesData = listOf(resourceData) // Convertir a lista

                    // Inicializamos el adapter con la lista de recursos
                    adapter = ResourceAdapter(resourcesData)
                    recyclerView.adapter = adapter

                    // Establecemos el escuchador de clics en el adaptador
                    adapter.setOnItemClickListener(object : ResourceAdapter.OnItemClickListener {
                        override fun onItemClick(resource: Resource) {
                            val opciones = arrayOf("Modificar recurso", "Eliminar recurso")

                            AlertDialog.Builder(this@MainActivity)
                                .setTitle(resource.name)
                                .setItems(opciones) { dialog, index ->
                                    when (index) {
                                        0 -> Modificar(resource)
                                        1 -> eliminarRecurso(resource, api)
                                    }
                                }
                                .setNegativeButton("Cancelar", null)
                                .show()
                        }
                    })
                } else {
                    Log.e("API", "Error en la respuesta: ${response.message()}")
                    Toast.makeText(this@MainActivity, "Error en la respuesta", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Resource>, t: Throwable) {
                Log.e("API", "Error al obtener el recurso: ${t.message}")
                Toast.makeText(this@MainActivity, "Error al obtener el recurso", Toast.LENGTH_SHORT).show()
            }
        })
    }




    private fun Modificar(resource: Resource) {
        // Creamos un intent para ir a la actividad de actualización de alumnos
        val i = Intent(getBaseContext(), ActualizarResourceActivity::class.java)
        // Pasamos el ID del alumno seleccionado a la actividad de actualización
        i.putExtra("resource_id", resource.id)
        i.putExtra("name", resource.name)
        i.putExtra("type", resource.type)
        i.putExtra("url", resource.url)
        i.putExtra("description", resource.description)
        // Iniciamos la actividad de actualización
        startActivity(i)
    }

    private fun eliminarRecurso(resource: Resource, api: ResourceApi) {
        //Log.e("API", "id : $resource")
        val llamada = api.eliminarRecurso(resource.id)
        llamada.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "Recurso eliminado", Toast.LENGTH_SHORT).show()
                    cargarDatos(api)
                } else {
                    val error = response.errorBody()?.string()
                    Log.e("API", "Error al eliminar recurso : $error")
                    Toast.makeText(this@MainActivity, "Error al eliminar recurso 1", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("API", "Error al eliminar recurso : $t")
                Toast.makeText(this@MainActivity, "Error al eliminar recurso 2", Toast.LENGTH_SHORT).show()
            }
        })
    }
}