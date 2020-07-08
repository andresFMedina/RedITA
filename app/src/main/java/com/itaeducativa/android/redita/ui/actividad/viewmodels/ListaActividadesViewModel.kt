package com.itaeducativa.android.redita.ui.actividad.viewmodels

import android.content.Context
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
import com.itaeducativa.android.redita.util.startFormularioActividadActivity


private const val MAS_RECIENTE = "Más reciente"
private const val MAS_ANTIGUO = "Más antiguo"

@Suppress("UNCHECKED_CAST")
class ListaActividadesViewModel(
    private val repositorioActividad: RepositorioActividad,
    private val repositorioUsuario: RepositorioUsuario,
    private val repositorioReaccion: RepositorioReaccion,
    private val repositorioAutenticacion: RepositorioAutenticacion
) : ViewModel() {

    val listaActividades: MutableLiveData<List<Actividad>> = MutableLiveData()
    val entries = listOf(MAS_RECIENTE, MAS_ANTIGUO)
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

    var nombresActividadesAdapter: NombresActividadesAdapter? = null


    fun guardarActividadEnFirestore(actividad: Actividad) {
        requestListener?.onStartRequest()
        repositorioActividad.guardarActividadEnFirestore(actividad).addOnFailureListener {
            requestListener?.onFailureRequest(it.message!!)
        }.addOnSuccessListener {
            requestListener?.onSuccessRequest()

        }
    }

    fun onItemSelected(orden: Any) {
        this.orden.value = orden as String
        val direccion =
            if (this.orden.value!! == MAS_RECIENTE) Query.Direction.DESCENDING else Query.Direction.ASCENDING
        //getListaActividades(direccion = direccion)
    }

    fun getListaActividades(
        ordenCampo: String = "fechaCreacionTimeStamp",
        direccion: Query.Direction = Query.Direction.DESCENDING,
        query: String = "",
        tipo: String
    ) {
        orden.value = MAS_RECIENTE
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
                requestListener?.onSuccessRequest()
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
                requestListener?.onSuccessRequest()
                misActividadesAdapter.actualizarActividades(actividades)
            }
    }

    fun eliminarActividad(actividad: Actividad) {
        requestListener?.onStartRequest()
        repositorioActividad.eliminarActividad(actividad).addOnSuccessListener {
            requestListener?.onSuccessRequest()
        }.addOnFailureListener {
            requestListener?.onFailureRequest(it.message!!)
        }
    }



    fun desactivarActividad(actividad: Actividad) {
        repositorioActividad.desactivarActividad(actividad)
    }

    fun getNombresActividades(context: Context){
        requestListener?.onStartRequest()
        repositorioActividad.getNombresActividad().addSnapshotListener { value, exception ->
            if(exception != null){
                requestListener?.onFailureRequest(exception.message!!)
                return@addSnapshotListener
            }
            val nombres: MutableList<String> = mutableListOf()
            for (doc in value!!) {
                val nombre = doc.getString("nombre")
                nombres.add(nombre!!)
            }
            nombresActividadesAdapter = NombresActividadesAdapter(context, nombres)
            requestListener?.onSuccessRequest()

        }
    }




    fun goToCrearActividad(view: View) {
        view.context.startFormularioActividadActivity(null)
    }

    private fun crearActividadByDocumentReference(doc: QueryDocumentSnapshot): Actividad {
        val actividad = doc.toObject(Actividad::class.java)
        val autorUid = doc.getString("autorUid")!!
        actividad.autorUid = autorUid
        //actividad.imagenes = doc.get("imagenes") as List<String>?
        //actividad.video = doc.getString("video")

        return actividad
    }
}