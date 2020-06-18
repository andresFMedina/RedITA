package com.itaeducativa.android.redita.ui.vista

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.itaeducativa.android.redita.data.modelos.Vista

class VistaViewModel: ViewModel() {
    val fotoUsuario = MutableLiveData<String>()
    val nombreUsuario = MutableLiveData<String>()
    val vecesVisto = MutableLiveData<String>()

    fun bind(vista: Vista) {
        if (vista.usuario != null) {
            fotoUsuario.value = vista.usuario!!.imagenPerfilUrl
            nombreUsuario.value = vista.usuario!!.nombreCompleto
        }
        vecesVisto.value = vista.vecesVisto.toString()
    }
}