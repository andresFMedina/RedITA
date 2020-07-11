package com.itaeducativa.android.redita.data.repositorios

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.Query
import com.itaeducativa.android.redita.data.firebase.FirebaseSource
import com.itaeducativa.android.redita.data.modelos.Comentario

private const val COMENTARIOS = "comentarios"
private const val PUBLICACION_ID = "publicacionId"

class RepositorioComentario
    (private val firebase: FirebaseSource) {

    private val firestoreDB by lazy {
        firebase.firestoreDB
    }


    fun agregarComentarioEnFirestorePorPublicacion(comentario: Comentario): Task<Void> =
        firestoreDB.collection(COMENTARIOS).document(comentario.fecha)
            .set(comentario)

    fun getComentariosEnFirestorePorActividad(referenciaDocumentoActividad: String): Query =
        firestoreDB.collection(COMENTARIOS)
            .whereEqualTo(PUBLICACION_ID, referenciaDocumentoActividad)
            .orderBy("fecha", Query.Direction.DESCENDING)

    fun eliminarComentario(comentario: Comentario) =
        firestoreDB.collection(COMENTARIOS).document(comentario.fecha).delete()
}