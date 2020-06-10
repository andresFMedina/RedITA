package com.itaeducativa.android.redita.data.repositorios

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.itaeducativa.android.redita.data.firebase.FirebaseSource
import com.itaeducativa.android.redita.data.modelos.Usuario

class RepositorioUsuario (private val firebase: FirebaseSource) {

    private val firestoreDB by lazy { firebase.firestoreDB }
    private val USUARIOS = "usuarios"

    fun guardarUsuario(usuario: Usuario): Task<Void> {
        val documentReference = firestoreDB.collection(USUARIOS).document(usuario.uid)
        return documentReference.set(usuario)
    }

    fun modificarTelefono(telefono: String, usuarioUid: String): Task<Void> =
        firestoreDB.collection(USUARIOS).document(usuarioUid).update("telefono", telefono)

    fun getUsuarioByUid(uid: String): DocumentReference =
        firestoreDB.collection(USUARIOS).document(uid)

    fun cambiarUrlImagenPerfil(url: String, uid: String): Task<Void> =
        firestoreDB.collection(USUARIOS).document(uid).update("imagenPerfilUrl", url)

    fun sumarInteraccion(interaccion: String, uid: String): Task<Void> =
        firestoreDB.collection(USUARIOS).document(uid).update(interaccion, FieldValue.increment(1))

    fun restarInteraccion(interaccion: String, uid: String): Task<Void> =
        firestoreDB.collection(USUARIOS).document(uid).update(interaccion, FieldValue.increment(-1))

}