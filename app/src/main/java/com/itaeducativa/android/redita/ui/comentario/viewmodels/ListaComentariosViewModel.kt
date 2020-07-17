package com.itaeducativa.android.redita.ui.comentario.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.EventListener
import com.itaeducativa.android.redita.data.modelos.*
import com.itaeducativa.android.redita.data.repositorios.*
import com.itaeducativa.android.redita.network.RequestListener
import com.itaeducativa.android.redita.ui.comentario.adapters.ListaComentariosAdapter

class ListaComentariosViewModel(
    private val repositorioComentario: RepositorioComentario,
    private val repositorioUsuario: RepositorioUsuario,
    private val repositorioPublicacion: RepositorioPublicacion,
    private val repositorioHistorial: RepositorioHistorial,
    private val repositorioAutenticacion: RepositorioAutenticacion
) :
    ViewModel() {

    private val listaComentarios = MutableLiveData<MutableList<Comentario>>()

    val listaComentariosAdapter: ListaComentariosAdapter =
        ListaComentariosAdapter(repositorioAutenticacion.currentUser()!!.uid, this)

    var requestListener: RequestListener? = null

    fun agregarComentariosEnFirestorePorPublicacion(
        comentario: Comentario,
        publicacion: Publicacion
    ) {
        requestListener?.onStartRequest()
        repositorioComentario.agregarComentarioEnFirestorePorPublicacion(comentario)
            .addOnFailureListener {
                requestListener?.onFailureRequest(it.message!!)
            }.addOnSuccessListener {
                if (comentario.tipoPublicacion == "actividades") {
                    val historial = Historial(
                        usuarioUid = comentario.usuarioUid,
                        actividadId = comentario.publicacionId,
                        accion = "Comentó",
                        timestampAccion = comentario.fecha
                    )

                    repositorioHistorial.guardarHistorialFirestore(historial)
                    requestListener?.onSuccessRequest(comentario)
                }
                if (comentario.tipoPublicacion == "archivos") {
                    val archivo = publicacion as Archivo
                    val historial = Historial(
                        usuarioUid = comentario.usuarioUid,
                        actividadId = archivo.actividadId,
                        accion = if (archivo.tipo == "imagen") "Comentó una imagen de" else "Comentó un video de",
                        timestampAccion = comentario.fecha
                    )
                    repositorioHistorial.guardarHistorialFirestore(historial)
                    requestListener?.onSuccessRequest(comentario)
                }
                repositorioPublicacion.aumentarInteraccion(
                    comentario.tipoPublicacion,
                    comentario.publicacionId,
                    "comentarios"
                ).addOnSuccessListener {
                    publicacion.comentarios++
                    requestListener?.onSuccessRequest(publicacion)
                }.addOnFailureListener {
                    requestListener?.onFailureRequest(it.message!!)
                }
                repositorioUsuario.sumarInteraccion("comentarios", comentario.usuarioUid)
                    .addOnFailureListener {
                        requestListener?.onFailureRequest(it.message!!)
                    }
            }
    }

    fun getComentariosEnFirestorePorPublicacion(publicacionId: String) {
        requestListener?.onStartRequest()
        repositorioComentario.getComentariosEnFirestorePorActividad(publicacionId)
            .addSnapshotListener(EventListener { value, e ->
                if (e != null) {
                    listaComentarios.value = null
                    requestListener?.onFailureRequest(e.message!!)
                    return@EventListener
                }

                val comentarios: MutableList<Comentario> = mutableListOf()
                for (doc in value!!) {
                    val comentario = doc.toObject(Comentario::class.java)
                    repositorioUsuario.getUsuarioByUid(comentario.usuarioUid)
                        .addSnapshotListener { snapshot, exception ->
                            if (exception != null) {
                                comentario.usuario = null
                                requestListener?.onFailureRequest(exception.message!!)
                                return@addSnapshotListener
                            }
                            val usuario = snapshot!!.toObject(Usuario::class.java)
                            comentario.usuario = usuario
                            listaComentarios.value = comentarios
                            requestListener?.onSuccessRequest(comentarios)
                            listaComentariosAdapter.actualizarComentarios(listaComentarios.value as MutableList<Comentario>)
                        }
                    Log.d("Comentario", comentario.toString())
                    comentarios.add(comentario)
                }

            })
    }

    fun eliminarComentario(comentario: Comentario, publicacion: Publicacion) {
        repositorioComentario.eliminarComentario(comentario).addOnSuccessListener {
            repositorioPublicacion.disminuirInteraccion(
                comentario.tipoPublicacion,
                comentario.publicacionId,
                "comentarios"
            ).addOnSuccessListener {
                publicacion.comentarios--
                requestListener?.onSuccessRequest(publicacion)
            }.addOnFailureListener {
                requestListener?.onFailureRequest(it.message!!)
            }
            repositorioHistorial.eliminarHistorial(comentario.fecha)
            repositorioUsuario.restarInteraccion("comentarios", comentario.usuarioUid)
                .addOnFailureListener {
                    requestListener?.onFailureRequest(it.message!!)
                }
            listaComentarios.value!!.remove(comentario)
            listaComentariosAdapter.actualizarComentarios(listaComentarios.value!!)
        }
    }
}