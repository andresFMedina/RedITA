package com.itaeducativa.android.redita.ui.comentario.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.itaeducativa.android.redita.data.repositorios.*

@Suppress("UNCHECKED_CAST")
class ListaComentariosViewModelFactory(
    private val repositorioComentario: RepositorioComentario,
    private val repositorioUsuario: RepositorioUsuario,
    private val repositorioHistorial: RepositorioHistorial,
    private val repositorioPublicacion: RepositorioPublicacion
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ListaComentariosViewModel(
            repositorioComentario,
            repositorioUsuario,
            repositorioPublicacion,
            repositorioHistorial
        ) as T
    }
}