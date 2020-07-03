package com.itaeducativa.android.redita.data.repositorios

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.itaeducativa.android.redita.data.firebase.FirebaseSource
import com.itaeducativa.android.redita.data.modelos.Actividad

private const val AUTOR_UID = "autorUid"
private const val ACTIVIDADES = "actividades"
private const val FECHA_CREACION = "fechaCreacionTimeStamp"

class RepositorioActividad(private val firebase: FirebaseSource) {
    private val firestoreDB: FirebaseFirestore by lazy {
        firebase.firestoreDB
    }


    fun guardarActividadEnFirestore(actividad: Actividad): Task<Void> {
        val documentReference =
            firestoreDB.collection(ACTIVIDADES)
                .document(actividad.fechaCreacionTimeStamp)
        return documentReference.set(actividad)
    }

    fun guardarUrlImagenesEnFirestore(actividadId: String, urlImagen: String): Task<Void> {
        val documentReference =
            firestoreDB.collection(ACTIVIDADES).document(actividadId)
        return documentReference.update("imagenes", FieldValue.arrayUnion(urlImagen))
    }

    fun getActividades(
        ordenCampo: String,
        direccion: Query.Direction,
        query: String = "",
        tipo: String = ""
    ): Query {
        val collection = firestoreDB.collection(ACTIVIDADES)
        if (query != "") collection.orderBy("nombre").startAt(query) else collection.orderBy(
            ordenCampo,
            direccion
        )
        Log.i("Tipo Consulta", tipo);
        return collection.whereEqualTo("estaActivo", true)
        //.whereEqualTo("tipoActividad", tipo)
    }

    fun getActividadesById(id: String): DocumentReference =
        firestoreDB.collection(ACTIVIDADES).document(id)

    fun getActividadesByAutorUid(
        uid: String,
        orderBy: String = "fechaCreacionTimeStamp",
        query: String = ""
    ): Query {
        val collection =
            firestoreDB.collection(ACTIVIDADES).whereEqualTo(AUTOR_UID, uid)
        if (query != "") collection.orderBy(orderBy).startAt(query)
        return collection
    }


    fun eliminarActividad(actividad: Actividad): Task<Void> {
        val documentReference =
            firestoreDB.collection(ACTIVIDADES)
                .document(actividad.fechaCreacionTimeStamp)
        return documentReference.delete()
    }

    fun desactivarActividad(actividad: Actividad): Task<Void> =
        firestoreDB.collection(ACTIVIDADES).document(actividad.fechaCreacionTimeStamp)
            .update("estaActivo", false)


    fun sumarReaccionActividad(actividadId: String, reaccion: String): Task<Void> {
        val documentReference = firestoreDB.collection(ACTIVIDADES)
            .document(actividadId)
        return documentReference.update(reaccion, FieldValue.increment(1))
    }

    fun restarReaccionActividad(actividadId: String, reaccion: String): Task<Void> {
        val documentReference = firestoreDB.collection(ACTIVIDADES)
            .document(actividadId)
        return documentReference.update(reaccion, FieldValue.increment(-1))
    }

    fun sumarComentarios(actividadId: String): Task<Void> {
        val documentReference = firestoreDB.collection(ACTIVIDADES)
            .document(actividadId)
        return documentReference.update("comentarios", FieldValue.increment(1))
    }

    fun guardarUrlVideoEnFirestore(actividadId: String, urlVideo: String): Task<Void> {
        val documentReference =
            firestoreDB.collection(ACTIVIDADES).document(actividadId)
        return documentReference.update("video", urlVideo)
    }


}