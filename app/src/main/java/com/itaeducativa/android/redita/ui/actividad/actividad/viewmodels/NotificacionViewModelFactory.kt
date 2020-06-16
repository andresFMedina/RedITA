package com.itaeducativa.android.redita.ui.actividad.actividad.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.itaeducativa.android.redita.data.repositorios.RepositorioNotificacion

@Suppress("UNCHECKED_CAST")
class NotificacionViewModelFactory(
    private val repositorioNotificacion: RepositorioNotificacion
): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NotificacionViewModel(repositorioNotificacion) as T
    }
}