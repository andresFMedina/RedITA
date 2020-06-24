package com.itaeducativa.android.redita.ui.imagen

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ImagenesViewModel(): ViewModel() {
    val listaImagenes = MutableLiveData<MutableList<Uri>>()

    val adapter = ImagenesAdapter(listaImagenes.value!!)


}