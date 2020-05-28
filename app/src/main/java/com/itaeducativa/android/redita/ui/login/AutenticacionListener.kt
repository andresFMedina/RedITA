package com.itaeducativa.android.redita.ui.login

interface AutenticacionListener {
    fun onStarted()
    fun onSuccess()
    fun onFailure(mensaje: String)
}