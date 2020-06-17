package com.itaeducativa.android.redita.ui.historial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.itaeducativa.android.redita.data.repositorios.RepositorioActividad
import com.itaeducativa.android.redita.data.repositorios.RepositorioHistorial
import com.itaeducativa.android.redita.data.repositorios.RepositorioUsuario

@Suppress("UNCHECKED_CAST")
class ListaHistorialViewModelFactory(
    private val repositorioHistorial: RepositorioHistorial,
    private val repositorioUsuario: RepositorioUsuario,
    private val repositorioActividad: RepositorioActividad
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ListaHistorialViewModel(
            repositorioHistorial,
            repositorioActividad,
            repositorioUsuario
        ) as T
    }
}