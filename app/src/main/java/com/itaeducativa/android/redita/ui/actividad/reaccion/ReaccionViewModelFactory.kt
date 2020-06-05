package com.itaeducativa.android.redita.ui.actividad.reaccion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.itaeducativa.android.redita.data.repositorios.RepositorioReaccion

@Suppress("UNCHECKED_CAST")
class ReaccionViewModelFactory(
    private val repositorioReaccion: RepositorioReaccion
): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ReaccionViewModel(repositorioReaccion) as T
    }
}