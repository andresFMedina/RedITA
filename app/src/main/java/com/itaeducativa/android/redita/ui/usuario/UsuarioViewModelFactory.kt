package com.itaeducativa.android.redita.ui.usuario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.itaeducativa.android.redita.data.repositorios.RepositorioUsuario

@Suppress("UNCHECKED_CAST")
class UsuarioViewModelFactory (
    private val repositorioUsuario: RepositorioUsuario
): ViewModelProvider.NewInstanceFactory(){

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UsuarioViewModel(repositorioUsuario) as T
    }

}