package com.itaeducativa.android.redita.ui.usuario

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import com.itaeducativa.android.redita.data.modelos.Usuario
import com.itaeducativa.android.redita.data.repositorios.RepositorioUsuario

class UsuarioViewModel(private val repositorioUsuario: RepositorioUsuario): ViewModel() {

    val usuario : MutableLiveData<Usuario> = MutableLiveData()

    fun guardarUsuario(usuario: Usuario) =
        repositorioUsuario.guardarUsuario(usuario)

    fun getUsuarioByUid(uid: String): LiveData<Usuario> {
        repositorioUsuario.getUsuarioByUid(uid)
            .addSnapshotListener(EventListener { value, e ->
                if (e != null) {
                    usuario.value = null
                    return@EventListener
                }
                usuario.value = value!!.toObject(Usuario::class.java)

            })

        return usuario
    }



}