package com.itaeducativa.android.redita.data.modelos

import com.google.firebase.Timestamp
import java.io.Serializable
import java.util.*

class Actividad(
    var nombre: String,
    var descripcion: String,
    var fechaCreacionTimeStamp: Timestamp?,
    var tipoActividad: String
) : Serializable {
    lateinit var autor: Usuario
    var archivos: List<String>? = null
    var reacciones: List<Reaccion>? = null
    var comentarios: List<Comentario>? = null

    constructor():this("","", Timestamp(Date()),"")
}

