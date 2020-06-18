package com.itaeducativa.android.redita.data.repositorios

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.itaeducativa.android.redita.data.firebase.FirebaseSource
import com.itaeducativa.android.redita.data.modelos.Vista

private const val VISTAS = "vistas"

class RepositorioVista(
    private val firebase: FirebaseSource
) {
    private val firestoreDB: FirebaseFirestore by lazy {
        firebase.firestoreDB
    }

    fun guardarVistaEnFirestore(vista: Vista) =
        firestoreDB.collection(VISTAS).document(vista.timestamp).set(vista)

    fun agregarVista(vista: Vista) =
        firestoreDB.collection(VISTAS).document(vista.timestamp)
            .update("vecesVisto", FieldValue.increment(1))

    fun getVistaPorParametro(nombreParametro: String, parametro: String) =
        firestoreDB.collection(VISTAS).whereEqualTo(nombreParametro, parametro)

    fun getVistaByUsuarioYActividad(usuarioUid: String, actividadId: String) =
        firestoreDB.collection(VISTAS).whereEqualTo("usuarioUid", usuarioUid)
            .whereEqualTo("actividadId", actividadId)

}