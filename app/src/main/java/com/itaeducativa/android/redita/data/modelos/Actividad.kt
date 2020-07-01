package com.itaeducativa.android.redita.data.modelos

import com.google.firebase.firestore.DocumentReference
import java.io.Serializable

class Actividad(
    var nombre: String,
    var descripcion: String,
    var id: String,
    var fechaCreacionTimeStamp: String,
    var tipoActividad: String,
    var meGusta: Int,
    var noMeGusta: Int,
    var comentarios: Int,
    var estaActiva: Boolean,
    var horaInicio: String?,
    var fechaInicio: String?
) : Serializable {
    var autor: Usuario? = null
    var autorUid: String? = null
    var referenciaAutor: DocumentReference? = null
    var reaccion: Reaccion? = null
    var archivos: MutableList<Archivo>? = null


    constructor() : this("", "", "","", "", 0, 0, 0, false, "", "")

    override fun toString(): String {
        return "Actividad(nombre='$nombre', descripcion='$descripcion', id='$id', fechaCreacionTimeStamp='$fechaCreacionTimeStamp', tipoActividad='$tipoActividad', meGusta=$meGusta, noMeGusta=$noMeGusta, comentarios=$comentarios, estaActiva=$estaActiva, horaInicio=$horaInicio, fechaInicio=$fechaInicio, autor=$autor, autorUid=$autorUid, referenciaAutor=$referenciaAutor, reaccion=$reaccion, archivos=${archivos.toString()})"
    }


}

