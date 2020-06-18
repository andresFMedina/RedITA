package com.itaeducativa.android.redita.ui.historial

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.itaeducativa.android.redita.data.modelos.Historial

class HistorialViewModel: ViewModel() {
    val fotoUsuario = MutableLiveData<String>()
    val nombreUsuario = MutableLiveData<String>()
    val accion = MutableLiveData<String>()
    val nombreActividad = MutableLiveData<String>()

    fun bind(historial: Historial) {
        fotoUsuario.value = historial.usuario?.imagenPerfilUrl
        Log.d("Foto", fotoUsuario.value)
        nombreUsuario.value = historial.usuario?.nombreCompleto
        accion.value = historial.accion
        nombreActividad.value = historial.actividad?.nombre
    }
}