package com.itaeducativa.android.redita.data.repositorios

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.itaeducativa.android.redita.data.firebase.FirebaseSource
import com.itaeducativa.android.redita.data.modelos.Actividad

private const val AUTOR_UID = "autorUid"
private const val ACTIVIDADES = "actividades"
private const val FECHA_CREACION = "fechaCreacionTimeStamp"
private const val NOMBRES_ACTIVIDADES = "nombresActividades"

class RepositorioActividad(private val firebase: FirebaseSource) {
    private val firestoreDB: FirebaseFirestore by lazy {
        firebase.firestoreDB
    }


    fun guardarActividadEnFirestore(actividad: Actividad): Task<Void> {
        val documentReference =
            firestoreDB.collection(ACTIVIDADES)
                .document(actividad.id)
        return documentReference.set(actividad)
    }

    fun guardarNombreActividadFirestore(
        nombre: String,
        actividadId: String,
        categoriaActividad: String,
        uidAutor: String
    ): Task<Void> {
        val data = hashMapOf(
            "nombre" to nombre,
            "categoriaActividad" to categoriaActividad,
            "uidAutor" to uidAutor
        )
        return firestoreDB.collection(NOMBRES_ACTIVIDADES).document(actividadId).set(data)
    }

    fun getActividades(
        ordenCampo: String,
        direccion: Query.Direction,
        query: String = "",
        categoria: String
    ): Query {
        val collection = firestoreDB.collection(ACTIVIDADES).orderBy(ordenCampo, direccion)
        var q = collection.whereEqualTo("estaActivo", true)
        if (query.isNotBlank()) q = collection.whereEqualTo("nombre", query)
        return q.whereEqualTo("categoria", categoria)
    }

    fun getActividadesById(id: String): DocumentReference =
        firestoreDB.collection(ACTIVIDADES).document(id)

    fun getActividadesByAutorUid(
        uid: String,
        orderBy: String = FECHA_CREACION,
        query: String = ""
    ): Query {
        val collection =
            firestoreDB.collection(ACTIVIDADES).whereEqualTo(AUTOR_UID, uid)
        return collection.whereEqualTo("estaActivo", true)
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

    fun eliminarNombre(actividadId: String): Task<Void> =
        firestoreDB.collection(NOMBRES_ACTIVIDADES).document(actividadId).delete()

    fun getNombresActividad(categoriaActividad: String): Query =
        firestoreDB.collection(NOMBRES_ACTIVIDADES)
            .whereEqualTo("categoriaActividad", categoriaActividad)

    fun getNombresActividadByAutorUid(uidAutor: String): Query =
        firestoreDB.collection(NOMBRES_ACTIVIDADES).whereEqualTo("uidAutor", uidAutor)

    fun cambiarNombreActividadFirestore(nombre: String, actividadId: String) =
        firestoreDB.collection(NOMBRES_ACTIVIDADES).document(actividadId).update("nombre", nombre)

}