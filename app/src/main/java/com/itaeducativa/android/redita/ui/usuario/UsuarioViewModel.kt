package com.itaeducativa.android.redita.ui.usuario

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.itaeducativa.android.redita.data.modelos.Usuario
import com.itaeducativa.android.redita.data.repositorios.RepositorioUsuario

class UsuarioViewModel(private val repositorioUsuario: RepositorioUsuario): ViewModel() {

    val usuario : MutableLiveData<Usuario> = MutableLiveData()

    fun guardarUsuario(usuario: Usuario) =
        repositorioUsuario.guardarUsuario(usuario)

    fun getUsuarioByUid(uid: String) =
         repositorioUsuario.getUsuarioByUid(uid)


}