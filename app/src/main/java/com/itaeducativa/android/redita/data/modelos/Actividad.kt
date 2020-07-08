package com.itaeducativa.android.redita.data.modelos

import com.google.firebase.firestore.DocumentReference
import java.io.Serializable

class Actividad(
    var categoria: String,
    var nombre: String,
    var descripcion: String,
    var id: String,
    var fechaCreacionTimeStamp: String,
    var tipoActividad: String,
    var estaActivo: Boolean,
    var horaInicio: String?,
    var fechaInicio: String?
) : Publicacion(), Serializable {
    var autor: Usuario? = null
    var autorUid: String? = null
    var referenciaAutor: DocumentReference? = null
    var archivos: MutableList<Archivo>? = null


    constructor() : this("", "", "", "", "", "",false, "", "")

    override fun toString(): String {
        return "Actividad(categoria='$categoria', nombre='$nombre', descripcion='$descripcion', id='$id', fechaCreacionTimeStamp='$fechaCreacionTimeStamp', tipoActividad='$tipoActividad', meGusta=$meGusta, noMeGusta=$noMeGusta, comentarios=$comentarios, estaActiva=$estaActivo, horaInicio=$horaInicio, fechaInicio=$fechaInicio, autor=$autor, autorUid=$autorUid, referenciaAutor=$referenciaAutor, reaccion=$reaccion, archivos=${archivos.toString()})"
    }


}

