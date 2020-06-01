package com.itaeducativa.android.redita.network

interface RequestListener {
    fun onStartRequest()
    fun onSuccessRequest()
    fun onFailureRequest(message: String)
}