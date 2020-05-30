package com.itaeducativa.android.redita.network

interface RequestListener {
    fun onStartRequest()
    fun onSuccess()
    fun onFailure(message: String)
}