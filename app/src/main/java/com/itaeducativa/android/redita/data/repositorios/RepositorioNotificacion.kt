package com.itaeducativa.android.redita.data.repositorios

import com.itaeducativa.android.redita.data.firebase.FirebaseSource

class RepositorioNotificacion(
    private val firebase: FirebaseSource
) {

    fun getToken() = firebase.firebaseInstanceId.instanceId

    fun subscribeToActividades() = firebase.firebaseMessaging.subscribeToTopic("actividades")
}