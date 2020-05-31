package com.itaeducativa.android.redita.ui.actividad.comentario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.itaeducativa.android.redita.data.repositorios.RepositorioComentario

@Suppress("UNCHECKED_CAST")
class ListaComentariosViewModelFactory(
    private val repositorioComentario: RepositorioComentario
): ViewModelProvider.NewInstanceFactory(){

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ListaComentariosViewModel(repositorioComentario) as T
    }
}