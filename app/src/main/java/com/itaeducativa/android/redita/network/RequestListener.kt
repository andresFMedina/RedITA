package com.itaeducativa.android.redita.network

interface RequestListener {
    fun onStartRequest()
    fun onSuccessRequest(response: Any?)
    fun onFailureRequest(message: String)
}