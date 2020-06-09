package com.itaeducativa.android.redita.ui

interface VideoListener {
    fun onStartVideo()
    fun onSuccessVideo()
    fun onFailureVideo(message: String)
}