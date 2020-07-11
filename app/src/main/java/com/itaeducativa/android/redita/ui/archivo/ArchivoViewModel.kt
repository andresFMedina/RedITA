package com.itaeducativa.android.redita.ui.archivo

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.itaeducativa.android.redita.data.modelos.Archivo
import com.itaeducativa.android.redita.data.modelos.Reaccion
import com.itaeducativa.android.redita.util.startArchivoDetalladoActivity

class ArchivoViewModel : ViewModel() {
    val archivo = MutableLiveData<Archivo>()
    val meGusta = MutableLiveData<String>()
    val noMeGusta = MutableLiveData<String>()
    val comentarios = MutableLiveData<String>()
    val reaccion = MutableLiveData<Reaccion>()

    fun bind(archivo: Archivo) {
        this.archivo.value = archivo
        if (archivo.tipo == "imagen") {
            this.archivo.value?.urlImagen = archivo.url
        }
        meGusta.value = archivo.meGusta.toString()
        noMeGusta.value = archivo.noMeGusta.toString()
        comentarios.value = archivo.comentarios.toString()
        reaccion.value = archivo.reaccion
    }

    fun verArchivoDetalladoActivity(view: View) {
        view.context.startArchivoDetalladoActivity(archivo.value!!)
    }
}