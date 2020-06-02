package com.itaeducativa.android.redita.ui.actividad.comentario

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.EventListener
import com.itaeducativa.android.redita.data.modelos.Comentario
import com.itaeducativa.android.redita.data.repositorios.RepositorioComentario
import com.itaeducativa.android.redita.data.repositorios.RepositorioUsuario
import com.itaeducativa.android.redita.network.RequestListener

class ListaComentariosViewModel(
    private val repositorioComentario: RepositorioComentario,
    private val repositorioUsuario: RepositorioUsuario
) :
    ViewModel() {

    val listaComentarios: MutableLiveData<List<Comentario>> = MutableLiveData()

    val listaComentariosAdapter: ListaComentariosAdapter = ListaComentariosAdapter()

    var requestListener: RequestListener? = null

    fun agregarComentariosEnFirestorePorActividad(
        referenciaDocumentoActividad: String,
        comentario: Comentario,
        uid: String
    ) {
        val usuarioReference: DocumentReference = repositorioUsuario.getUsuarioByUid(uid)
        comentario.referenciaUsuario = usuarioReference
        requestListener?.onStartRequest()
        repositorioComentario.agregarComentarioEnFirestorePorActividad(
            referenciaDocumentoActividad,
            comentario
        ).addOnFailureListener {
            requestListener?.onFailureRequest(it.message!!)
        }.addOnSuccessListener {
            requestListener?.onSuccessRequest()
        }
    }

    fun getComentariosEnFirestorePorActividad(referenciaDocumentoActividad: String) {
        requestListener?.onStartRequest()
        repositorioComentario.getComentariosEnFirestorePorActividad(referenciaDocumentoActividad)
            .addSnapshotListener(EventListener { value, e ->
                if (e != null) {
                    listaComentarios.value = null
                    requestListener?.onFailureRequest(e.message!!)
                    return@EventListener
                }

                val comentarios: MutableList<Comentario> = mutableListOf()
                for (doc in value!!) {
                    val comentario = Comentario(
                        comentario = doc.getString("comentario")!!,
                        fecha = doc.getTimestamp("fecha")!!
                    )
                    comentario.referenciaUsuario = doc.getDocumentReference("referenciaUsuario")
                    Log.d("Comentario", comentario.toString())
                    comentarios.add(comentario)
                }
                listaComentarios.value = comentarios
                requestListener?.onSuccessRequest()
                listaComentariosAdapter.actualizarComentarios(listaComentarios.value as MutableList<Comentario>)
            })
    }
}