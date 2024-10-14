package edu.udb.recursoaprendizaje

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ResourceApi {
    @GET("recusos_aprendizaje/resource")
    fun obtenerRecursos() : Call<List<Resource>>

    @GET("recusos_aprendizaje/resource/{id}")
    fun obtenerRecursoPorId(@Path("id") id: Int): Call<Resource>
    @POST("recusos_aprendizaje/resource")
    fun crearRecurso(@Body resource: Resource): Call<Resource>
    @PUT("recusos_aprendizaje/resource/{id}")
    fun actualizarRecurso(@Path("id") id: Int, @Body resource: Resource): Call<Resource>
    @DELETE("recusos_aprendizaje/resource/{id}")
    fun eliminarRecurso(@Path("id") id: Int): Call<Void>
}