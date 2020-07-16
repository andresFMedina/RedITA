package com.itaeducativa.android.redita.ui.actividad.viewmodels

import androidx.lifecycle.ViewModel
import com.itaeducativa.android.redita.data.repositorios.RepositorioNotificacion

class NotificacionViewModel(
    private val repositorioNotificacion: RepositorioNotificacion
): ViewModel() {

    fun getToken() {
        repositorioNotificacion.getToken()
    }

    fun subscribeToActividades(){
        repositorioNotificacion.subscribeToActividades()
    }
}