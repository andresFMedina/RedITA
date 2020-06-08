package com.itaeducativa.android.redita.ui

interface ImageUploadListener {
    fun onStartUploadImage()
    fun onSuccessUploadImage(rutaImagen: String)
    fun onFailureUploadImage(message: String)
}