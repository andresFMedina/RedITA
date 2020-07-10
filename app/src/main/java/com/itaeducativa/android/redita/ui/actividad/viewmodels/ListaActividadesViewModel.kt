package com.itaeducativa.android.redita.ui.actividad.viewmodels

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.itaeducativa.android.redita.data.modelos.Actividad
import com.itaeducativa.android.redita.data.repositorios.*
import com.itaeducativa.android.redita.network.RequestListener
import com.itaeducativa.android.redita.ui.actividad.adapters.ListaActividadesAdapter
import com.itaeducativa.android.redita.ui.actividad.adapters.MisActividadesAdapter
import com.itaeducativa.android.redita.ui.actividad.adapters.NombresActividadesAdapter
import com.itaeducativa.android.redita.ui.actividad.adapters.NombresAdapter
import com.itaeducativa.android.redita.util.startFormularioActividadActivity


@Suppress("UNCHECKED_CAST")
class ListaActividadesViewModel(
    private val repositorioActividad: RepositorioActividad,
    private val repositorioUsuario: RepositorioUsuario,
    private val repositorioReaccion: RepositorioReaccion,
    private val repositorioAutenticacion: RepositorioAutenticacion
) : ViewModel() {

    val listaActividades: MutableLiveData<List<Actividad>> = MutableLiveData()
    val orden = MutableLiveData<String>()

    var requestListener: RequestListener? = null
    val listaActividadesAdapter by lazy {
        ListaActividadesAdapter(
            repositorioAutenticacion.currentUser()!!.uid
        )
    }

    val misActividadesAdapter: MisActividadesAdapter by lazy {
        MisActividadesAdapter(this)
    }

    var nombresActividadesAdapter: NombresAdapter? = null


    fun guardarActividadEnFirestore(actividad: Actividad) {
        requestListener?.onStartRequest()
        repositorioActividad.guardarActividadEnFirestore(actividad).addOnFailureListener {
            requestListener?.onFailureRequest(it.message!!)
        }.addOnSuccessListener {
            repositorioActividad.guardarNombreActividadFirestore(actividad.nombre, actividad.id)
            requestListener?.onSuccessRequest(actividad)

        }
    }

    fun getListaActividades(
        ordenCampo: String = "fechaCreacionTimeStamp",
        direccion: Query.Direction = Query.Direction.DESCENDING,
        query: String = "",
        tipo: String
    ) {
        requestListener?.onStartRequest()
        repositorioActividad.getActividades(ordenCampo, direccion, query, tipo)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    listaActividades.value = null
                    requestListener?.onFailureRequest(e.message!!)
                    return@addSnapshotListener
                }

                val actividades: MutableList<Actividad> = mutableListOf()
                for (doc in value!!) {
                    val actividad = crearActividadByDocumentReference(doc)
                    val referenciaAutor = repositorioUsuario.getUsuarioByUid(actividad.autorUid!!)
                    actividad.referenciaAutor = referenciaAutor
                    actividades.add(actividad)
                }
                listaActividades.value = actividades
                requestListener?.onSuccessRequest(actividades)
                listaActividadesAdapter.actualizarActividades(listaActividades.value as MutableList<Actividad>)
            }
    }

    fun getActividadesByAutorUid(
        uid: String,
        orderBy: String = "fechaCreacionTimeStamp",
        query: String = ""
    ) {
        requestListener?.onStartRequest()
        repositorioActividad.getActividadesByAutorUid(uid, orderBy, query)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    listaActividades.value = null
                    requestListener?.onFailureRequest(e.message!!)
                    return@addSnapshotListener
                }

                val actividades: MutableList<Actividad> = mutableListOf()
                for (doc in value!!) {
                    val actividad = crearActividadByDocumentReference(doc)
                    actividades.add(actividad)
                }

                listaActividades.value = actividades
                misActividadesAdapter.actualizarActividades(actividades)
                requestListener?.onSuccessRequest(actividades)

            }
    }

    /*fun eliminarActividad(actividad: Actividad) {
        requestListener?.onStartRequest()
        repositorioActividad.eliminarActividad(actividad).addOnSuccessListener {
            requestListener?.onSuccessRequest()
        }.addOnFailureListener {
            requestListener?.onFailureRequest(it.message!!)
        }
    }*/


    fun desactivarActividad(actividad: Actividad) {
        repositorioActividad.desactivarActividad(actividad).addOnSuccessListener {
            repositorioActividad.eliminarNombre(actividad.id)
        }
    }

    fun getNombresActividades(context: Context) {
        requestListener?.onStartRequest()
        repositorioActividad.getNombresActividad().addSnapshotListener { value, exception ->
            if (exception != null) {
                requestListener?.onFailureRequest(exception.message!!)
                return@addSnapshotListener
            }
            val nombres: MutableList<String> = mutableListOf()
            for (doc in value!!) {
                val nombre = doc.getString("nombre")
                nombres.add(nombre!!)
            }
            nombresActividadesAdapter = NombresAdapter(context, nombres)
            requestListener?.onSuccessRequest(nombres.toList())
        }
    }


    fun goToCrearActividad(view: View) {
        view.context.startFormularioActividadActivity(null)
    }

    private fun crearActividadByDocumentReference(doc: QueryDocumentSnapshot): Actividad {
        val actividad = doc.toObject(Actividad::class.java)
        val autorUid = doc.getString("autorUid")!!
        actividad.autorUid = autorUid

        return actividad
    }
}