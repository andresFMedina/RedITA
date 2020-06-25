package com.itaeducativa.android.redita.ui.usuario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.itaeducativa.android.redita.data.repositorios.RepositorioStorage
import com.itaeducativa.android.redita.data.repositorios.RepositorioUsuario

@Suppress("UNCHECKED_CAST")
class ListaUsuarioViewModelFactory (
    private val repositorioUsuario: RepositorioUsuario,
    private val repositorioStorage: RepositorioStorage
): ViewModelProvider.NewInstanceFactory(){

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ListaUsuarioViewModel(repositorioUsuario, repositorioStorage) as T
    }

}