package com.itaeducativa.android.redita.ui.actividad.actividad.viewmodels

import android.net.Uri
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.itaeducativa.android.redita.data.modelos.Actividad
import com.itaeducativa.android.redita.data.modelos.Historial
import com.itaeducativa.android.redita.data.modelos.Reaccion
import com.itaeducativa.android.redita.data.repositorios.*
import com.itaeducativa.android.redita.network.RequestListener
import com.itaeducativa.android.redita.ui.actividad.actividad.adapters.ListaActividadesAdapter
import com.itaeducativa.android.redita.ui.actividad.actividad.adapters.MisActividadesAdapter
import com.itaeducativa.android.redita.util.startFormularioActividadActivity


private const val MAS_RECIENTE = "Más reciente"
private const val MAS_ANTIGUO = "Más antiguo"

@Suppress("UNCHECKED_CAST")
class ListaActividadesViewModel(
    private val repositorioActividad: RepositorioActividad,
    private val repositorioUsuario: RepositorioUsuario,
    private val repositorioReaccion: RepositorioReaccion,
    private val repositorioAutenticacion: RepositorioAutenticacion,
    private val repositorioStorage: RepositorioStorage,
    private val repositorioHistorial: RepositorioHistorial
) : ViewModel() {

    val listaActividades: MutableLiveData<List<Actividad>> = MutableLiveData()
    val entries = listOf(MAS_RECIENTE, MAS_ANTIGUO)
    val orden = MutableLiveData<String>()

    var requestListener: RequestListener? = null
    val listaActividadesAdapter by lazy {
        ListaActividadesAdapter(
            this,
            repositorioAutenticacion.currentUser()!!.uid
        )
    }

    val misActividadesAdapter: MisActividadesAdapter by lazy {
        MisActividadesAdapter(this)
    }


    fun guardarActividadEnFirestore(actividad: Actividad) {
        requestListener?.onStartRequest()
        repositorioActividad.guardarActividadEnFirestore(actividad).addOnFailureListener {
            requestListener?.onFailureRequest(it.message!!)
        }.addOnSuccessListener {
            requestListener?.onSuccessRequest()

        }
    }

    fun agregarImagenesAActividad(actividadId: String, rutaImagen: String, imagen: Uri) {
        repositorioStorage.subirArchivoStorage(rutaImagen, imagen).addOnSuccessListener {
            val urlImagen = "gs://redita.appspot.com${it.storage.path}"
            repositorioActividad.guardarUrlImagenesEnFirestore(actividadId, urlImagen)
        }
    }

    fun agregarVideoAActividad(actividadId: String, rutaVideo: String, video: Uri) {
        repositorioStorage.subirArchivoStorage(rutaVideo, video).addOnSuccessListener {
            val urlVideo = "gs://redita.appspot.com${it.storage.path}"
            repositorioActividad.guardarUrlVideoEnFirestore(actividadId, urlVideo)
        }
    }

    fun onItemSelected(orden: Any) {
        this.orden.value = orden as String
        val direccion =
            if (this.orden.value!! == MAS_RECIENTE) Query.Direction.DESCENDING else Query.Direction.ASCENDING
        getListaActividades(direccion = direccion)
    }

    fun getListaActividades(
        ordenCampo: String = "fechaCreacionTimeStamp",
        direccion: Query.Direction = Query.Direction.DESCENDING,
        query: String = ""
    ) {
        orden.value = MAS_RECIENTE
        requestListener?.onStartRequest()
        repositorioActividad.getActividades(ordenCampo, direccion, query)
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

    fun crearReaccion(reaccion: Reaccion) {
        requestListener?.onStartRequest()
        repositorioReaccion.crearReaccion(reaccion).addOnSuccessListener {
            requestListener?.onSuccessRequest()
            repositorioActividad.sumarReaccionActividad(reaccion.actividadId, reaccion.tipoReaccion)
            repositorioUsuario.sumarInteraccion(reaccion.tipoReaccion, reaccion.usuarioUid)
            val historial = Historial(
                usuarioUid = reaccion.usuarioUid,
                actividadId = reaccion.actividadId,
                accion = when (reaccion.tipoReaccion) {
                    "noMeGusta" -> "No le gustó"
                    "meGusta" -> "Le gustó"
                    else -> ""
                },
                timestampAccion = reaccion.timestamp
            )
            repositorioHistorial.guardarHistorialFirestore(historial)
        }.addOnFailureListener {
            requestListener?.onFailureRequest(it.message!!)
        }
    }

    fun eliminarReaccion(reaccion: Reaccion) {
        requestListener?.onStartRequest()
        repositorioReaccion.eliminarReaccion(reaccion.timestamp).addOnSuccessListener {
            repositorioActividad.restarReaccionActividad(
                reaccion.actividadId,
                reaccion.tipoReaccion
            )
            repositorioUsuario.restarInteraccion(reaccion.tipoReaccion, reaccion.usuarioUid)
            repositorioHistorial.eliminarHistorial(reaccion.timestamp)
            requestListener?.onSuccessRequest()
        }.addOnFailureListener {
            requestListener?.onFailureRequest(it.message!!)
        }
    }

    fun desactivarActividad(actividad: Actividad) {
        repositorioActividad.desactivarActividad(actividad)
    }

    fun getReaccionByActividadIdYUsuarioUid(actividadId: String, usuarioUid: String) =
        repositorioReaccion.getReaccionesByActividadIdYUsuarioUid(actividadId, usuarioUid)


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