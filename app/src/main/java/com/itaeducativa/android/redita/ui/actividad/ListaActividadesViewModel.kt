package com.itaeducativa.android.redita.ui.actividad

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import com.itaeducativa.android.redita.data.modelos.Actividad
import com.itaeducativa.android.redita.data.repositorios.RepositorioActividad
import com.itaeducativa.android.redita.network.RequestListener

class ListaActividadesViewModel(
    private val repositorioActividad: RepositorioActividad
) : ViewModel() {

    val listaActividades: MutableLiveData<List<Actividad>> = MutableLiveData()

    val listaActividadesAdapter = ListaActividadesAdapter()
    var requestListener: RequestListener? = null

    fun guardarActividadEnFirestore(actividad: Actividad) {
        requestListener?.onStartRequest()
        repositorioActividad.guardarActividadEnFirestore(actividad).addOnFailureListener {
            requestListener?.onFailure(it.message!!)
        }
        requestListener?.onSuccess()
    }

    fun getListaActividades() {
        requestListener?.onStartRequest()
        repositorioActividad.getActividades()
            .addSnapshotListener(EventListener<QuerySnapshot> { value, e ->
                if (e != null) {
                    listaActividades.value = null
                    requestListener?.onFailure(e.message!!)
                    return@EventListener
                }

                val actividades: MutableList<Actividad> = mutableListOf()
                for (doc in value!!) {
                    val actividad = doc.toObject(Actividad::class.java)
                    actividades.add(actividad)
                }
                listaActividades.value = actividades
                requestListener?.onSuccess()
                listaActividadesAdapter.actualizarActividades(listaActividades.value as MutableList<Actividad>)
            })


    }
}