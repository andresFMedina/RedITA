package com.itaeducativa.android.redita.ui.archivo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.itaeducativa.android.redita.data.modelos.Archivo

class ArchivoViewModel: ViewModel() {
    val archivo = MutableLiveData<Archivo>()

    fun bind(archivo: Archivo){
       this.archivo.value = archivo
    }
}