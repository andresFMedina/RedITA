package com.itaeducativa.android.redita.data.repositorios

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.itaeducativa.android.redita.data.firebase.FirebaseSource
import com.itaeducativa.android.redita.data.modelos.Reaccion

private const val REACCIONES = "reacciones"
private const val USUARIO_UID = "usuarioUid"
private const val ACTIVIDAD_ID = "actividadId"

class RepositorioReaccion(private val firebase: FirebaseSource) {
    private val firestoreDB: FirebaseFirestore by lazy {
        firebase.firestoreDB
    }

    fun crearReaccion(reaccion: Reaccion) =
        firestoreDB.collection(REACCIONES).document(reaccion.timestamp).set(reaccion)

    fun eliminarReaccion(reaccionTimestamp: String) =
        firestoreDB.collection(REACCIONES).document(reaccionTimestamp).delete()

    fun getReaccionesByUsuarioUid(usuarioUid: String) =
        firestoreDB.collection(REACCIONES).whereEqualTo(USUARIO_UID, usuarioUid)

    fun getReaccionesByActividadId(actividadId: String) =
        firestoreDB.collection(REACCIONES).whereEqualTo(ACTIVIDAD_ID, actividadId)

    fun getReaccionesByActividadIdYUsuarioUid(actividadId: String, usuarioUid: String): Query {
        var query = firestoreDB.collection(REACCIONES).whereEqualTo(ACTIVIDAD_ID, actividadId)
        query = query.whereEqualTo(USUARIO_UID, usuarioUid)
        return query
    }
}