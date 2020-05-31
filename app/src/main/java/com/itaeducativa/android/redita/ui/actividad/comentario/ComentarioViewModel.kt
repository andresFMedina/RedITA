package com.itaeducativa.android.redita.ui.actividad.comentario

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.itaeducativa.android.redita.data.modelos.Comentario

class ComentarioViewModel: ViewModel() {
    val comentario = MutableLiveData<String>()
    val fecha = MutableLiveData<String>()
    val usuario = MutableLiveData<String>()

    fun bind(comentario: Comentario) {
        this.comentario.value = comentario.comentario
        fecha.value = comentario.fecha.toDate().toString()
        usuario.value = comentario.usuario.nombreCompleto
    }
}