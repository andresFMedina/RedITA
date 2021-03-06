package com.itaeducativa.android.redita.ui.archivo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.itaeducativa.android.redita.data.repositorios.RepositorioArchivo
import com.itaeducativa.android.redita.data.repositorios.RepositorioAutenticacion
import com.itaeducativa.android.redita.data.repositorios.RepositorioStorage

@Suppress("UNCHECKED_CAST")
class ListaArchivoViewModelFactory(
    private val repositorioArchivo: RepositorioArchivo,
    private val repositorioStorage: RepositorioStorage,
    private val repositorioAutenticacion: RepositorioAutenticacion
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ListaArchivoViewModel(
            repositorioArchivo,
            repositorioStorage,
            repositorioAutenticacion
        ) as T
    }
}