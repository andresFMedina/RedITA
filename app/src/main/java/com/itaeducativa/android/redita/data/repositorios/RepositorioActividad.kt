package com.itaeducativa.android.redita.data.repositorios

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.itaeducativa.android.redita.data.firebase.FirebaseSource
import com.itaeducativa.android.redita.data.modelos.Actividad

private const val AUTOR_UID = "autorUid"
private const val ACTIVIDADES = "actividades"

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

    fun getActividades(): CollectionReference =
        firestoreDB.collection(ACTIVIDADES)

    fun getActividadesByAutorUid(uid: String): Query =
        firestoreDB.collection(ACTIVIDADES).whereEqualTo(AUTOR_UID, uid)

    fun eliminarActividad(actividad: Actividad): Task<Void> {
        val documentReference =
            firestoreDB.collection(ACTIVIDADES)
                .document(actividad.fechaCreacionTimeStamp)
        return documentReference.delete()
    }

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


}