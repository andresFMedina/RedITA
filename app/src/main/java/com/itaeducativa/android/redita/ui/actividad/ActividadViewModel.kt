package com.itaeducativa.android.redita.ui.actividad

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.itaeducativa.android.redita.data.modelos.Actividad
import com.itaeducativa.android.redita.util.startActividadActivity

class ActividadViewModel : ViewModel() {
    val nombre = MutableLiveData<String>()
    val descripcion = MutableLiveData<String>()
    val autor = MutableLiveData<String>()
    val fechaCreacionTimeStamp = MutableLiveData<String>()
    val tipoActividad = MutableLiveData<String>()
    val actividad = MutableLiveData<Actividad>()

    fun bind(actividad: Actividad) {
        nombre.value = actividad.nombre
        descripcion.value = actividad.descripcion
        //autor.value = if (actividad.autor != null) actividad.autor.nombreCompleto
        fechaCreacionTimeStamp.value = actividad.fechaCreacionTimeStamp?.toDate().toString()
        tipoActividad.value = actividad.tipoActividad
        this.actividad.value = actividad
    }

    fun verActividad(view: View) {
        view.context.startActividadActivity(actividad.value!!)
    }

}