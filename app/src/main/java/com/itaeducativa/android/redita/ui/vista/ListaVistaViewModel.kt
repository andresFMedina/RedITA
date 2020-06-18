package com.itaeducativa.android.redita.ui.vista

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.itaeducativa.android.redita.data.modelos.Usuario

import com.itaeducativa.android.redita.data.modelos.Vista
import com.itaeducativa.android.redita.data.repositorios.RepositorioUsuario
import com.itaeducativa.android.redita.data.repositorios.RepositorioVista
import com.itaeducativa.android.redita.network.RequestListener

class ListaVistaViewModel(
    private val repositorioVista: RepositorioVista,
    private val repositorioUsuario: RepositorioUsuario
) : ViewModel() {
    private val listaVistas = MutableLiveData<List<Vista>>()
    val vista = MutableLiveData<Vista>()

    private val listaVistaAdapter = ListaVistaAdapter()

    var requestListener: RequestListener? = null


    fun guardarVistaEnFirestore(vista: Vista) {
        requestListener?.onStartRequest()
        repositorioVista.guardarVistaEnFirestore(vista).addOnSuccessListener {
            requestListener?.onSuccessRequest()
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
                            requestListener?.onSuccessRequest()
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
                requestListener?.onSuccessRequest()
            }
    }

    fun agregarVista(vista: Vista) {
        requestListener?.onStartRequest()
        repositorioVista.agregarVista(vista)
    }
}