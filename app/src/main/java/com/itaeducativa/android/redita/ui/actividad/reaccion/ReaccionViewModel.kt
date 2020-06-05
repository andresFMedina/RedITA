package com.itaeducativa.android.redita.ui.actividad.reaccion

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.itaeducativa.android.redita.data.modelos.Reaccion
import com.itaeducativa.android.redita.data.repositorios.RepositorioReaccion
import com.itaeducativa.android.redita.network.RequestListener

class ReaccionViewModel(
    private val repositorioReaccion: RepositorioReaccion
) : ViewModel() {

    var requestListener: RequestListener? = null
    var listaReacciones = MutableLiveData<List<Reaccion>>()


    fun getReaccionesByUsuario(usuarioUid: String) {
        requestListener?.onStartRequest()
        repositorioReaccion.getReaccionesByUsuarioUid(usuarioUid)
            .addSnapshotListener { value, exception ->
                if (exception != null) {
                    requestListener?.onFailureRequest(exception.message!!)
                    return@addSnapshotListener
                }

                val reacciones: MutableList<Reaccion> = mutableListOf()
                for (doc in value!!) {
                    val reaccion = doc.toObject(Reaccion::class.java)
                    reacciones.add(reaccion)
                }
                listaReacciones.value = reacciones
            }
    }

    fun getReaccionesByActividad(actividadId: String) {
        requestListener?.onStartRequest()
        repositorioReaccion.getReaccionesByActividadId(actividadId)
            .addSnapshotListener { value, exception ->
                if (exception != null) {
                    requestListener?.onFailureRequest(exception.message!!)
                    return@addSnapshotListener
                }

                val reacciones: MutableList<Reaccion> = mutableListOf()
                for (doc in value!!) {
                    val reaccion = doc.toObject(Reaccion::class.java)
                    reacciones.add(reaccion)
                }
                listaReacciones.value = reacciones
            }
    }
}