package com.itaeducativa.android.redita.ui.vista

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.itaeducativa.android.redita.data.modelos.Historial
import com.itaeducativa.android.redita.data.modelos.Usuario

import com.itaeducativa.android.redita.data.modelos.Vista
import com.itaeducativa.android.redita.data.repositorios.RepositorioHistorial
import com.itaeducativa.android.redita.data.repositorios.RepositorioUsuario
import com.itaeducativa.android.redita.data.repositorios.RepositorioVista
import com.itaeducativa.android.redita.network.RequestListener

class ListaVistaViewModel(
    private val repositorioVista: RepositorioVista,
    private val repositorioUsuario: RepositorioUsuario,
    private val repositorioHistorial: RepositorioHistorial
) : ViewModel() {
    private val listaVistas = MutableLiveData<List<Vista>>()
    val vista = MutableLiveData<Vista>()

    val listaVistaAdapter = ListaVistaAdapter()

    var requestListener: RequestListener? = null


    fun guardarVistaEnFirestore(vista: Vista) {
        requestListener?.onStartRequest()
        repositorioVista.guardarVistaEnFirestore(vista).addOnSuccessListener {
            val v: Vista? = vista
            requestListener?.onSuccessRequest(v)
            crearHistorial(vista)
        }.addOnFailureListener {
            requestListener?.onFailureRequest(it.message!!)
        }

    }

    fun getVistasPorParametro(nombreParametro: String, parametro: String) {
        requestListener?.onStartRequest()
        repositorioVista.getVistaPorParametro(nombreParametro, parametro)
            .addSnapshotListener { value, exception ->
                if (exception != null) {
                    requestListener?.onFailureRequest(exception.message!!)
                    return@addSnapshotListener
                }
                val vistas: MutableList<Vista> = mutableListOf()
                for (v in value!!) {
                    val vista = v.toObject(Vista::class.java)
                    repositorioUsuario.getUsuarioByUid(vista.usuarioUid)
                        .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                            vista.usuario = documentSnapshot!!.toObject(Usuario::class.java)
                            vistas.add(vista)
                            listaVistas.value = vistas
                            listaVistaAdapter.actualizarVistas(vistas)
                            requestListener?.onSuccessRequest(vistas)
                        }

                }

            }
    }

    fun getVistasByUsuarioYActividad(usuarioUid: String, actividadId: String) {
        requestListener?.onStartRequest()
        repositorioVista.getVistaByUsuarioYActividad(usuarioUid, actividadId)
            .addSnapshotListener { value, exception ->
                if (exception != null) {
                    requestListener?.onFailureRequest(exception.message!!)
                    return@addSnapshotListener
                }
                vista.value = value?.firstOrNull()?.toObject(Vista::class.java)
                requestListener?.onSuccessRequest(vista.value)
            }
    }

    fun agregarVista(vista: Vista) {
        requestListener?.onStartRequest()
        repositorioVista.agregarVista(vista)

        crearHistorial(vista, Timestamp.now().seconds.toString())
    }

    fun crearHistorial(vista: Vista, timestamp: String = vista.timestamp){
        val historial = Historial(
            usuarioUid = vista.usuarioUid,
            actividadId = vista.actividadId,
            accion = "vió",
            timestampAccion = timestamp
        )
        repositorioHistorial.guardarHistorialFirestore(historial)
    }
}