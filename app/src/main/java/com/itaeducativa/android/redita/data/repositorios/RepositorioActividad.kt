package com.itaeducativa.android.redita.data.repositorios

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.itaeducativa.android.redita.data.firebase.FirebaseSource
import com.itaeducativa.android.redita.data.modelos.Actividad
import com.itaeducativa.android.redita.data.modelos.Reaccion

class RepositorioActividad(private val firebase: FirebaseSource) {
    private val firestoreDB: FirebaseFirestore by lazy {
        firebase.firestoreDB
    }

    private val ACTIVIDADES = "actividades"

    fun guardarActividadEnFirestore(actividad: Actividad): Task<Void> {
        val documentReference =
            firestoreDB.collection(ACTIVIDADES)
                .document(actividad.fechaCreacionTimeStamp.toString())
        return documentReference.set(actividad)
    }

    fun getActividades(): CollectionReference =
        firestoreDB.collection(ACTIVIDADES)

    fun eliminarActividad(actividad: Actividad): Task<Void> {
        val documentReference =
            firestoreDB.collection(ACTIVIDADES)
                .document(actividad.fechaCreacionTimeStamp.toString())
        return documentReference.delete()
    }

    fun agregarReaccion(actividad: Actividad, reaccion: String, valor: Int): Task<Void> {
        val documentReference = firestoreDB.collection(ACTIVIDADES)
            .document(actividad.fechaCreacionTimeStamp!!.seconds.toString())
        return documentReference.update(reaccion, valor)
    }


}