package com.itaeducativa.android.redita.ui.usuario

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.itaeducativa.android.redita.data.modelos.Usuario

class UsuarioViewModel : ViewModel() {
    val usuario: MutableLiveData<Usuario> = MutableLiveData()
    val email = MutableLiveData<String>()
    val nombreCompleto = MutableLiveData<String>()
    val telefono = MutableLiveData<String>()
    val rol = MutableLiveData<String>()
    val imagenPerfilUrl = MutableLiveData<String>()
    val cantidadMeGusta = MutableLiveData<String>()
    val cantidadNoMeGusta = MutableLiveData<String>()
    val cantidadComentarios = MutableLiveData<String>()

    fun bindUsuario(usuario: Usuario) {
        this.usuario.value = usuario
        email.value = usuario.email
        nombreCompleto.value = usuario.nombreCompleto
        telefono.value = usuario.telefono
        rol.value = usuario.rol
        imagenPerfilUrl.value = usuario.imagenPerfilUrl
        cantidadMeGusta.value = usuario.meGusta.toString()
        cantidadNoMeGusta.value = usuario.noMeGusta.toString()
        cantidadComentarios.value = usuario.comentarios.toString()
    }
}