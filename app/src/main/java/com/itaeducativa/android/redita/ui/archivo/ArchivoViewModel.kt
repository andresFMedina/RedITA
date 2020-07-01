package com.itaeducativa.android.redita.ui.archivo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.itaeducativa.android.redita.data.modelos.Archivo

class ArchivoViewModel : ViewModel() {
    val archivo = MutableLiveData<Archivo>()
    val meGusta = MutableLiveData<String>()
    val noMeGusta = MutableLiveData<String>()
    val comentarios = MutableLiveData<String>()

    fun bind(archivo: Archivo) {
        this.archivo.value = archivo
        meGusta.value = archivo.meGusta.toString()
        noMeGusta.value = archivo.noMeGusta.toString()
        comentarios.value = archivo.comentarios.toString()
    }
}