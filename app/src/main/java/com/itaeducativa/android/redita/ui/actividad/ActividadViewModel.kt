package com.itaeducativa.android.redita.ui.actividad

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.itaeducativa.android.redita.data.modelos.Actividad

class ActividadViewModel : ViewModel() {
    val nombre = MutableLiveData<String>()
    val descripcion = MutableLiveData<String>()
    val autor = MutableLiveData<String>()
    val fechaCreacionTimeStamp = MutableLiveData<String>()
    val tipoActividad = MutableLiveData<String>()

    fun bind(actividad: Actividad) {
        nombre.value = actividad.nombre
        descripcion.value = actividad.descripcion
        autor.value = actividad.autor.nombreCompleto
        fechaCreacionTimeStamp.value = actividad.fechaCreacionTimeStamp
        tipoActividad.value = actividad.tipoActividad
    }

}