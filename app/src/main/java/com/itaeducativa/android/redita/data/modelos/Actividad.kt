package com.itaeducativa.android.redita.data.modelos

data class Actividad(
    val nombre: String,
    val descripcion: String,
    val autor: Usuario,
    val fechaCreacionTimeStamp: String,
    val tipoActividad: String,
    val archivos: List<String>?,
    val reacciones: List<Reaccion>?,
    val comentarios: List<Comentario>?
)
