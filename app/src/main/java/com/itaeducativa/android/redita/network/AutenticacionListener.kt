package com.itaeducativa.android.redita.network

interface AutenticacionListener {
    fun onStarted()
    fun onSuccess()
    fun onFailure(mensaje: String)
}