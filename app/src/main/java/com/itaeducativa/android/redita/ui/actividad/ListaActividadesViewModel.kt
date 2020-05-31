package com.itaeducativa.android.redita.ui.actividad

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.EventListener
import com.itaeducativa.android.redita.data.modelos.Actividad
import com.itaeducativa.android.redita.data.modelos.Usuario
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
            requestListener?.onFailure(it.message!!)
        }
        requestListener?.onSuccess()
    }

    fun getListaActividades() {
        requestListener?.onStartRequest()
        repositorioActividad.getActividades()
            .addSnapshotListener(EventListener { value, e ->
                if (e != null) {
                    listaActividades.value = null
                    requestListener?.onFailure(e.message!!)
                    return@EventListener
                }

                val actividades: MutableList<Actividad> = mutableListOf()
                for (doc in value!!) {
                    val actividad = Actividad(
                        nombre=doc.getString("nombre")!!,
                        descripcion = doc.getString("descripcion")!!,
                        fechaCreacionTimeStamp = doc.getTimestamp("fechaCreacionTimeStamp")!!,
                        tipoActividad = doc.getString("tipoActividad")!!
                    )
                    doc.getDocumentReference("autor")?.addSnapshotListener {response, exc ->
                        val usuario = response?.toObject(Usuario::class.java)
                        actividad.autor = usuario!!
                    }
                    actividades.add(actividad)
                }
                listaActividades.value = actividades
                requestListener?.onSuccess()
                listaActividadesAdapter.actualizarActividades(listaActividades.value as MutableList<Actividad>)
            })


    }
}