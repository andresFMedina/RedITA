package com.itaeducativa.android.redita.ui.comentario.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentReference
import com.itaeducativa.android.redita.data.modelos.Comentario
import com.itaeducativa.android.redita.data.modelos.Usuario
import com.itaeducativa.android.redita.network.RequestListener
import com.itaeducativa.android.redita.util.getFechaTimestamp

class ComentarioViewModel : ViewModel() {
    val comentario = MutableLiveData<String>()
    val fecha = MutableLiveData<String>()
    val usuario = MutableLiveData<String>()
    val imagenPerfilUrl = MutableLiveData<String>()
    val objetoComentario = MutableLiveData<Comentario>()

    var requestListener: RequestListener? = null

    fun bind(comentario: Comentario) {
        this.comentario.value = comentario.comentario
        fecha.value = getFechaTimestamp(comentario.fecha)
        objetoComentario.value = comentario
        if (comentario.usuario != null) {
            usuario.value = comentario.usuario!!.nombreCompleto
            imagenPerfilUrl.value = comentario.usuario!!.imagenPerfilUrl
        }
    }

    private fun bindUsuario(referenciaUsuario: DocumentReference?) {
        referenciaUsuario!!.addSnapshotListener { value, exception ->
            requestListener?.onStartRequest()
            if (exception != null) {
                requestListener?.onFailureRequest(exception.message!!)
                return@addSnapshotListener
            }

            objetoComentario.value!!.usuario = value!!.toObject(Usuario::class.java)
            objetoComentario.value!!.usuarioReference = null
            requestListener?.onSuccessRequest(objetoComentario.value!!.usuario)
        }
    }
}