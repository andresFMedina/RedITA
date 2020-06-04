package com.itaeducativa.android.redita.data.modelos

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import java.io.Serializable
import java.util.*

class Actividad(
    var nombre: String,
    var descripcion: String,
    var fechaCreacionTimeStamp: Timestamp?,
    var tipoActividad: String,
    var meGusta: Int,
    var noMeGusta: Int,
    var comentarios: Int
) : Serializable {
    var autor: Usuario? = null
    var autorUid: String? = null
    var referenciaAutor: DocumentReference? = null
    var imagenes: List<String>? = null


    constructor():this("","", Timestamp(Date()),"",0,0,0)
}

