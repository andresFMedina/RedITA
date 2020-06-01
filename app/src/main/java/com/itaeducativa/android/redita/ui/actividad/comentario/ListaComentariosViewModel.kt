package com.itaeducativa.android.redita.ui.actividad.comentario

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.EventListener
import com.itaeducativa.android.redita.data.modelos.Comentario
import com.itaeducativa.android.redita.data.repositorios.RepositorioComentario
import com.itaeducativa.android.redita.network.RequestListener

class ListaComentariosViewModel(private val repositorioComentario: RepositorioComentario) :
    ViewModel() {

    val listaComentarios: MutableLiveData<List<Comentario>> = MutableLiveData()

    val listaComentariosAdapter: ListaComentariosAdapter = ListaComentariosAdapter()

    var requestListener: RequestListener? = null

    fun getComentariosEnFirestorePorActividad(referenciaDocumentoActividad: String) {
        requestListener?.onStartRequest()
        repositorioComentario.getComentariosEnFirestorePorActividad(referenciaDocumentoActividad)
            .addSnapshotListener(EventListener { value, e ->
                if (e != null) {
                    listaComentarios.value = null
                    requestListener?.onFailure(e.message!!)
                    return@EventListener
                }

                val comentarios: MutableList<Comentario> = mutableListOf()
                for (doc in value!!) {
                    val comentario = Comentario(
                        comentario = doc.getString("comentario")!!,
                        fecha = doc.getTimestamp("fecha")!!
                    )
                    comentario.referenciaUsuario = doc.getDocumentReference("usuario")
                    Log.d("Comentario",comentario.toString())
                    comentarios.add(comentario)
                }
                listaComentarios.value = comentarios
                requestListener?.onSuccess()
                listaComentariosAdapter.actualizarComentarios(listaComentarios.value as MutableList<Comentario>)
            })
    }
}