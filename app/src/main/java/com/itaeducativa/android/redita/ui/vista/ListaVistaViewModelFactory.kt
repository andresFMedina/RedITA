package com.itaeducativa.android.redita.ui.vista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.itaeducativa.android.redita.data.repositorios.RepositorioHistorial
import com.itaeducativa.android.redita.data.repositorios.RepositorioUsuario
import com.itaeducativa.android.redita.data.repositorios.RepositorioVista

@Suppress("UNCHECKED_CAST")
class ListaVistaViewModelFactory(
    private val repositorioVista: RepositorioVista,
    private val repositorioUsuario: RepositorioUsuario,
    private val repositorioHistorial: RepositorioHistorial
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ListaVistaViewModel(repositorioVista, repositorioUsuario, repositorioHistorial) as T
    }
}