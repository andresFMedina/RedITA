package com.itaeducativa.android.redita.data.repositorios

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.itaeducativa.android.redita.data.firebase.FirebaseSource
import com.itaeducativa.android.redita.data.modelos.Historial

private const val HISTORIAL = "historial"

class RepositorioHistorial(
    private val firebase: FirebaseSource
) {

    private val firestoreDB: FirebaseFirestore by lazy {
        firebase.firestoreDB
    }

    fun guardarHistorialFirestore(historial: Historial) = firestoreDB.collection(HISTORIAL)
        .document(historial.timestampAccion).set(historial)

    fun getListaHistorial() = firestoreDB.collection(HISTORIAL).orderBy("timestamp")

    fun getListaHistorialByUsuarioUid(usuarioUid: String) = firestoreDB.collection(HISTORIAL)
        .whereEqualTo("usuarioUid", usuarioUid)
        .orderBy("timestampAccion", Query.Direction.DESCENDING)

    fun eliminarHistorial(historialId: String) = firestoreDB.collection(HISTORIAL)
        .document(historialId).delete()
}