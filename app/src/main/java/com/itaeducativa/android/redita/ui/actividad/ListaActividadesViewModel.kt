package com.itaeducativa.android.redita.ui.actividad

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.itaeducativa.android.redita.data.modelos.Actividad
import com.itaeducativa.android.redita.data.repositorios.RepositorioActividad
import com.itaeducativa.android.redita.network.RequestListener

class ListaActividadesViewModel(
    private val repositorioActividad: RepositorioActividad
) : ViewModel() {

    private val listaActividades: MutableLiveData<List<Actividad>> = MutableLiveData()

    var requestListener: RequestListener? = null
    val listaActividadesAdapter = ListaActividadesAdapter()

    fun guardarActividadEnFirestore(actividad: Actividad) {
        requestListener?.onStartRequest()
        repositorioActividad.guardarActividadEnFirestore(actividad).addOnFailureListener {
            requestListener?.onFailureRequest(it.message!!)
        }
        requestListener?.onSuccessRequest()
    }

    fun getListaActividades() {
        requestListener?.onStartRequest()
        repositorioActividad.getActividades()
            .addSnapshotListener { value, e ->
                if (e != null) {
                    listaActividades.value = null
                    requestListener?.onFailureRequest(e.message!!)
                    return@addSnapshotListener
                }

                val actividades: MutableList<Actividad> = mutableListOf()
                for (doc in value!!) {
                    Log.d("Documento", doc.data.toString())
                    val actividad = Actividad(
                        nombre=doc.getString("nombre")!!,
                        descripcion = doc.getString("descripcion")!!,
                        fechaCreacionTimeStamp = doc.getTimestamp("fechaCreacionTimeStamp")!!,
                        tipoActividad = doc.getString("tipoActividad")!!
                    )
                    actividad.referenciaAutor = doc.getDocumentReference("autor")!!
                    actividades.add(actividad)
                }
                listaActividades.value = actividades
                requestListener?.onSuccessRequest()
                listaActividadesAdapter.actualizarActividades(listaActividades.value as MutableList<Actividad>)
            }


    }
}