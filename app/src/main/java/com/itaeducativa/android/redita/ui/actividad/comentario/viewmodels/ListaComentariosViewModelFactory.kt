package com.itaeducativa.android.redita.ui.actividad.comentario.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.itaeducativa.android.redita.data.repositorios.RepositorioComentario
import com.itaeducativa.android.redita.data.repositorios.RepositorioUsuario

@Suppress("UNCHECKED_CAST")
class ListaComentariosViewModelFactory(
    private val repositorioComentario: RepositorioComentario,
    private val repositorioUsuario: RepositorioUsuario
): ViewModelProvider.NewInstanceFactory(){

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ListaComentariosViewModel(
            repositorioComentario,
            repositorioUsuario
        ) as T
    }
}