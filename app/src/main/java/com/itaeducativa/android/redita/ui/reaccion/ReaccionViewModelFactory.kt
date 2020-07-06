package com.itaeducativa.android.redita.ui.reaccion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.itaeducativa.android.redita.data.repositorios.RepositorioHistorial
import com.itaeducativa.android.redita.data.repositorios.RepositorioPublicacion
import com.itaeducativa.android.redita.data.repositorios.RepositorioReaccion
import com.itaeducativa.android.redita.data.repositorios.RepositorioUsuario

@Suppress("UNCHECKED_CAST")
class ReaccionViewModelFactory(
    private val repositorioReaccion: RepositorioReaccion,
    private val repositorioPublicacion: RepositorioPublicacion,
    private val repositorioHistorial: RepositorioHistorial,
    private val repositorioUsuario: RepositorioUsuario
): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ReaccionViewModel(repositorioReaccion, repositorioPublicacion, repositorioHistorial, repositorioUsuario) as T
    }
}