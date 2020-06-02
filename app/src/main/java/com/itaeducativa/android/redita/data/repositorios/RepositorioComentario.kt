package com.itaeducativa.android.redita.data.repositorios

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.itaeducativa.android.redita.data.firebase.FirebaseSource
import com.itaeducativa.android.redita.data.modelos.Comentario

class RepositorioComentario
    (private val firebase: FirebaseSource) {

    private val firestoreDB by lazy {
        firebase.firestoreDB
    }

    private val ACTIVIDADES = "actividades"
    private val COMENTARIOS = "comentarios"

    fun agregarComentarioEnFirestorePorActividad(
        referenciaDocumentoActividad: String,
        comentario: Comentario
    ): Task<Void> =
        firestoreDB.collection(ACTIVIDADES).document(referenciaDocumentoActividad)
            .collection(COMENTARIOS).document(comentario.fecha.seconds.toString()).set(comentario)

    fun getComentariosEnFirestorePorActividad(referenciaDocumentoActividad: String): CollectionReference =
        firestoreDB.collection(ACTIVIDADES).document(referenciaDocumentoActividad)
            .collection(COMENTARIOS)


}