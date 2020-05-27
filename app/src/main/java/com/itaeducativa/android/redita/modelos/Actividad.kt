package com.itaeducativa.android.redita.modelos

data class Actividad(
    val nombre: String,
    val descripcion: String,
    val autor: Usuario,
    val fechaCreacion: String,
    val tipoActividad: String,
    val archivos: List<String>?,
    val reacciones: List<Reaccion>?,
    val comentarios: List<Comentario>?
)
