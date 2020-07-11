package com.itaeducativa.android.redita.ui

import android.net.Uri

interface VideoListener {
    fun onStartVideo()
    fun onSuccessVideo(uri: Uri)
    fun onFailureVideo(message: String)
}