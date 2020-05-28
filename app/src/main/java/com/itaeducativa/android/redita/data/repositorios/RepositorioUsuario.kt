package com.itaeducativa.android.redita.data.repositorios

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.itaeducativa.android.redita.data.firebase.FirebaseSource
import com.itaeducativa.android.redita.data.modelos.Usuario

class RepositorioUsuario (private val firebase: FirebaseSource) {

    private val firestoreDB by lazy { firebase.firestoreDB }
    private val USUARIOS = "usuarios"

    fun guardarUsuario(usuario: Usuario): Task<Void> {
        val documentReference = firestoreDB.collection(USUARIOS).document(usuario.uid)
        return documentReference.set(usuario)
    }

    fun getUsuarioByUid(uid: String): DocumentReference =
        firestoreDB.collection(USUARIOS).document(uid)
}