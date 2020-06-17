package com.itaeducativa.android.redita.ui.vista

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.itaeducativa.android.redita.data.modelos.Vista
import com.itaeducativa.android.redita.data.repositorios.RepositorioVista
import com.itaeducativa.android.redita.network.RequestListener

class ListaVistaViewModel(
    private val repositorioVista: RepositorioVista
): ViewModel() {
    private val listaVistas = MutableLiveData<List<Vista>>()
    private val vista = MutableLiveData<Vista>()
    
    var requestListener: RequestListener? = null
    
    
    fun guardarVistaEnFirestore(vista: Vista) {
        requestListener?.onStartRequest()
        repositorioVista.guardarVistaEnFirestore(vista).addOnSuccessListener { 
            requestListener?.onSuccessRequest()
        }. addOnFailureListener { 
            requestListener?.onFailureRequest(it.message!!)
        }
    }

    fun getVistasByUsuario(usuarioUid: String) {
        requestListener?.onStartRequest()
        repositorioVista.getVistaByUsuarioUid(usuarioUid).addSnapshotListener { value, exception ->
            if(exception != null) {
                requestListener?.onFailureRequest(exception.message!!)
                return@addSnapshotListener
            }
            val vistas: MutableList<Vista> = mutableListOf()
            for (v in value!!) {
                val vista = v.toObject(Vista::class.java)
                vistas.add(vista)
            }
            listaVistas.value = vistas
            requestListener?.onSuccessRequest()
        }
    }

    fun getVistasByUsuarioYActividad(usuarioUid: String, actividadId: String) {
        requestListener?.onStartRequest()
        repositorioVista.getVistaByUsuarioYActividad(usuarioUid, actividadId).addSnapshotListener { value, exception ->
            if(exception != null) {
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