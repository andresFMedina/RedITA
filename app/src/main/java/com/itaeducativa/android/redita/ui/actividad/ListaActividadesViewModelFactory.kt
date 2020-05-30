package com.itaeducativa.android.redita.ui.actividad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.itaeducativa.android.redita.data.repositorios.RepositorioActividad

@Suppress("UNCHECKED_CAST")
class ListaActividadesViewModelFactory(
    private val repositorioActividad: RepositorioActividad
): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ListaActividadesViewModel(repositorioActividad) as T
    }
}