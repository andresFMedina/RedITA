package com.itaeducativa.android.redita.ui.actividad.viewmodels

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.itaeducativa.android.redita.data.repositorios.RepositorioStorage
import com.itaeducativa.android.redita.ui.VideoListener

class StorageViewModel(
    private val repositorioStorage: RepositorioStorage
) : ViewModel() {
    val uri = MutableLiveData<Uri>()

    var videoListener: VideoListener? = null

    fun getVideoUri(urlVideo: String) {
        videoListener?.onStartVideo()
        repositorioStorage.getReferenciaVideoDeUrl(urlVideo).addOnSuccessListener {
            uri.value = it
            videoListener?.onSuccessVideo(it)
        }.addOnFailureListener {
            videoListener?.onFailureVideo(it.message!!)
        }
    }
}