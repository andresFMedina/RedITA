package com.itaeducativa.android.redita.data.modelos

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import java.io.Serializable
import java.util.*

class Actividad(
    var nombre: String,
    var descripcion: String,
    var fechaCreacionTimeStamp: Timestamp?,
    var tipoActividad: String
) : Serializable {
    var autor: Usuario? = null
    var referenciaAutor: DocumentReference? = null
    var archivos: List<String>? = null
    var reacciones: List<Reaccion>? = null
    var comentarios: List<Comentario>? = null

    constructor():this("","", Timestamp(Date()),"")
}

