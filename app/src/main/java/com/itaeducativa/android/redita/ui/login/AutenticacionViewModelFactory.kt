package com.itaeducativa.android.redita.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.itaeducativa.android.redita.data.repositorios.RepositorioAutenticacion

@Suppress("UNCHECKED_CAST")
class AutenticacionViewModelFactory(
    private val repositorioAutenticacion: RepositorioAutenticacion
) : ViewModelProvider.NewInstanceFactory() {

    override fun<T : ViewModel?> create(modelClass: Class<T>): T {
        return AutenticacionViewModel(repositorioAutenticacion) as T
    }

}