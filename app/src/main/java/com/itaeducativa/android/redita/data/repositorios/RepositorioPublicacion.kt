package com.itaeducativa.android.redita.data.repositorios

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.itaeducativa.android.redita.data.firebase.FirebaseSource

class RepositorioPublicacion(
    private val firebase: FirebaseSource
) {

    private val firestoreDB: FirebaseFirestore by lazy {
        firebase.firestoreDB
    }

    fun aumentarInteraccion(coleccion: String, id: String, interaccion: String): Task<Void> {
        val document: DocumentReference = firestoreDB.collection(coleccion).document(id)
        return document.update(interaccion, FieldValue.increment(1))
    }

    fun disminuirInteraccion(coleccion: String, id: String, interaccion: String) =
        firestoreDB.collection(coleccion).document(id).update(interaccion, FieldValue.increment(-1))
}