package com.itaeducativa.android.redita.data.repositorios

import com.google.firebase.firestore.CollectionReference
import com.itaeducativa.android.redita.data.firebase.FirebaseSource

class RepositorioComentario
    (private val firebase: FirebaseSource) {

    private val firestoreDB by lazy {
        firebase.firestoreDB
    }

    private val ACTIVIDADES = "actividades"
    private val COMENTARIOS = "comentarios"

    fun getComentariosEnFirestorePorActividad(referenciaDocumentoActividad: String): CollectionReference =
        firestoreDB.collection(ACTIVIDADES).document(referenciaDocumentoActividad)
            .collection(COMENTARIOS)


}