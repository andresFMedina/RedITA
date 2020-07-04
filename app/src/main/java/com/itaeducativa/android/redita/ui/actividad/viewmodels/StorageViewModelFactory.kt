package com.itaeducativa.android.redita.ui.actividad.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.itaeducativa.android.redita.data.repositorios.RepositorioStorage

@Suppress("UNCHECKED_CAST")
class StorageViewModelFactory(
    private val repositorioStorage: RepositorioStorage
): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return StorageViewModel(repositorioStorage) as T
    }
}