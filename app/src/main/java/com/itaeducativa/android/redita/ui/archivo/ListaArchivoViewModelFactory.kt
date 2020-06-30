package com.itaeducativa.android.redita.ui.archivo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.itaeducativa.android.redita.data.repositorios.RepositorioArchivo

@Suppress("UNCHECKED_CAST")
class ListaArchivoViewModelFactory(
    private val repositorioArchivo: RepositorioArchivo
): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ListaArchivoViewModel(repositorioArchivo) as T
    }
}