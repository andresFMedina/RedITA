package com.itaeducativa.android.redita.ui.reaccion

import androidx.lifecycle.ViewModel
import com.itaeducativa.android.redita.data.modelos.Historial
import com.itaeducativa.android.redita.data.modelos.Publicacion
import com.itaeducativa.android.redita.data.modelos.Reaccion
import com.itaeducativa.android.redita.data.repositorios.RepositorioHistorial
import com.itaeducativa.android.redita.data.repositorios.RepositorioPublicacion
import com.itaeducativa.android.redita.data.repositorios.RepositorioReaccion
import com.itaeducativa.android.redita.data.repositorios.RepositorioUsuario
import com.itaeducativa.android.redita.network.RequestListener

class ReaccionViewModel(
    private val repositorioReaccion: RepositorioReaccion,
    private val repositorioPublicacion: RepositorioPublicacion,
    private val repositorioHistorial: RepositorioHistorial,
    private val repositorioUsuario: RepositorioUsuario
) : ViewModel() {

    var requestListener: RequestListener? = null


    fun crearReaccion(reaccion: Reaccion, publicacion: Publicacion) {
        requestListener?.onStartRequest()
        repositorioReaccion.crearReaccion(reaccion).addOnSuccessListener {
            repositorioPublicacion.aumentarInteraccion(
                reaccion.tipoPublicacion,
                reaccion.publicacionId,
                reaccion.tipoReaccion
            ).addOnSuccessListener {
                when (reaccion.tipoReaccion) {
                    "noMeGusta" -> publicacion.noMeGusta++
                    "meGusta" -> publicacion.meGusta++
                }
            }
            publicacion.reaccion = reaccion
            repositorioUsuario.sumarInteraccion(reaccion.tipoReaccion, reaccion.usuarioUid)

            val historial = Historial(
                usuarioUid = reaccion.usuarioUid,
                actividadId = reaccion.publicacionId,
                accion = when (reaccion.tipoReaccion) {
                    "noMeGusta" -> "No le gustó"
                    "meGusta" -> "Le gustó"
                    else -> ""
                },
                timestampAccion = reaccion.timestamp
            )
            repositorioHistorial.guardarHistorialFirestore(historial)
            val r: Reaccion? = reaccion
            requestListener?.onSuccessRequest(r)
        }.addOnFailureListener {
            requestListener?.onFailureRequest(it.message!!)
        }
    }

    fun eliminarReaccion(reaccion: Reaccion, publicacion: Publicacion) {
        requestListener?.onStartRequest()
        repositorioReaccion.eliminarReaccion(reaccion.timestamp).addOnSuccessListener {
            repositorioPublicacion.disminuirInteraccion(
                reaccion.tipoPublicacion,
                reaccion.publicacionId,
                reaccion.tipoReaccion
            ).addOnSuccessListener {
                when (reaccion.tipoReaccion) {
                    "noMeGusta" -> publicacion.noMeGusta--
                    "meGusta" -> publicacion.meGusta--
                }
                //requestListener?.onSuccessRequest()
            }
            repositorioUsuario.restarInteraccion(reaccion.tipoReaccion, reaccion.usuarioUid)
            repositorioHistorial.eliminarHistorial(reaccion.timestamp)

            //requestListener?.onSuccessRequest()
        }.addOnFailureListener {
            requestListener?.onFailureRequest(it.message!!)
        }
    }

    fun getReaccionByPublicacionIdYUsuarioUid(
        publicacionId: String,
        publicacion: Publicacion,
        usuarioUid: String
    ) {
        repositorioReaccion.getReaccionesByPublicacionIdYUsuarioUid(publicacionId, usuarioUid)
            .addSnapshotListener { snapshot, exception ->
                requestListener?.onStartRequest()
                if (exception != null) {
                    requestListener?.onFailureRequest(exception.message!!)
                    return@addSnapshotListener
                }

                val reaccion: Reaccion? = snapshot?.firstOrNull()?.toObject(Reaccion::class.java)
                publicacion.reaccion = reaccion
                requestListener?.onSuccessRequest(reaccion)
            }

    }
}