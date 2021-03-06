package com.itaeducativa.android.redita.data.repositorios

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.itaeducativa.android.redita.data.firebase.FirebaseSource
import com.itaeducativa.android.redita.data.modelos.Usuario

private const val NOMBRES_USUARIOS = "nombresUsuarios"

class RepositorioUsuario(private val firebase: FirebaseSource) {

    private val firestoreDB by lazy { firebase.firestoreDB }
    private val USUARIOS = "usuarios"

    fun guardarUsuario(usuario: Usuario): Task<Void> {
        val documentReference = firestoreDB.collection(USUARIOS).document(usuario.uid)
        return documentReference.set(usuario)
    }

    fun modificarTelefono(telefono: String, usuarioUid: String): Task<Void> =
        firestoreDB.collection(USUARIOS).document(usuarioUid).update("telefono", telefono)

    fun modificarRolUsuario(rol: String, usuarioUid: String): Task<Void> =
        firestoreDB.collection(USUARIOS).document(usuarioUid).update("rol", rol)

    fun getUsuarioByUid(uid: String): DocumentReference =
        firestoreDB.collection(USUARIOS).document(uid)

    fun getUsuarios(query: String = ""): Query {
        val q = firestoreDB.collection(USUARIOS)
        if (query.isNotBlank()) return q.whereEqualTo("nombreCompleto", query)
        return q.orderBy("nombreCompleto").limit(6)
    }

    fun cambiarUrlImagenPerfil(url: String, uid: String): Task<Void> =
        firestoreDB.collection(USUARIOS).document(uid).update("imagenPerfilUrl", url)

    fun sumarInteraccion(interaccion: String, uid: String): Task<Void> =
        firestoreDB.collection(USUARIOS).document(uid).update(interaccion, FieldValue.increment(1))

    fun restarInteraccion(interaccion: String, uid: String): Task<Void> =
        firestoreDB.collection(USUARIOS).document(uid).update(interaccion, FieldValue.increment(-1))

    fun getNombresUsuarios(): CollectionReference =
        firestoreDB.collection(NOMBRES_USUARIOS)

    fun guardarNombreUsuarioFirestore(
        nombre: String,
        usuarioUid: String
    ): Task<Void> {
        val data = hashMapOf(
            "nombre" to nombre
        )
        return firestoreDB.collection(NOMBRES_USUARIOS).document(usuarioUid).set(data)
    }

    fun getUsuariosNextPage(lastVisible: DocumentSnapshot): Query =
        firestoreDB.collection(USUARIOS).orderBy("nombreCompleto")
            .startAfter(lastVisible)
            .limit(6)


}