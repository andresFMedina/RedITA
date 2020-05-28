package com.itaeducativa.android.redita.data.repositorios

import com.itaeducativa.android.redita.data.firebase.FirebaseSource

class RepositorioAutenticacion (private val firebase: FirebaseSource){
    fun login(email: String, password: String) = firebase.login(email, password)

    fun register(email: String, password: String) = firebase.register(email, password)

    fun logout() = firebase.logout()

    fun currentUser() = firebase.currentUser()
}