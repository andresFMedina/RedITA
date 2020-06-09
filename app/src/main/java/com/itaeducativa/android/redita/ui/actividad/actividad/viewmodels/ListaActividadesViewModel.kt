package com.itaeducativa.android.redita.ui.actividad.actividad.viewmodels

import android.net.Uri
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.itaeducativa.android.redita.data.modelos.Actividad
import com.itaeducativa.android.redita.data.modelos.Reaccion
import com.itaeducativa.android.redita.data.repositorios.*
import com.itaeducativa.android.redita.network.RequestListener
import com.itaeducativa.android.redita.ui.actividad.actividad.adapters.ListaActividadesAdapter
import com.itaeducativa.android.redita.ui.actividad.actividad.adapters.MisActividadesAdapter
import com.itaeducativa.android.redita.util.startCrearActividadActivity

@Suppress("UNCHECKED_CAST")
class ListaActividadesViewModel(
    private val repositorioActividad: RepositorioActividad,
    private val repositorioUsuario: RepositorioUsuario,
    private val repositorioReaccion: RepositorioReaccion,
    private val repositorioAutenticacion: RepositorioAutenticacion,
    private val repositorioStorage: RepositorioStorage
) : ViewModel() {

    private val listaActividades: MutableLiveData<List<Actividad>> = MutableLiveData()

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

    fun getActividadesByAutorUid(uid: String) {
        requestListener?.onStartRequest()
        repositorioActividad.getActividadesByAutorUid(uid)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    listaActividades.value = null
                    requestListener?.onFailureRequest(e.message!!)
                    return@addSnapshotListener
                }
                Log.d("Query usuario", value.toString())
                val actividades: MutableList<Actividad> = mutableListOf()
                for (doc in value!!) {
                    Log.d("Documento", doc.data.toString())
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
        }.addOnFailureListener {
            requestListener?.onFailureRequest(it.message!!)
        }
    }

    fun eliminarReaccion(reaccion: Reaccion) {
        requestListener?.onStartRequest()
        repositorioReaccion.eliminarReaccion(reaccion.timestamp).addOnSuccessListener {
            repositorioActividad.restarReaccionActividad(reaccion.actividadId, reaccion.tipoReaccion)
            requestListener?.onSuccessRequest()
        }.addOnFailureListener {
            requestListener?.onFailureRequest(it.message!!)
        }
    }

    fun getReaccionByActividadIdYUsuarioUid(actividadId: String, usuarioUid: String) =
        repositorioReaccion.getReaccionesByActividadIdYUsuarioUid(actividadId, usuarioUid)


    fun goToCrearActividad(view: View) {
        view.context.startCrearActividadActivity()
    }

    private fun crearActividadByDocumentReference(doc: QueryDocumentSnapshot): Actividad {
        val actividad = Actividad(
            nombre = doc.getString("nombre")!!,
            descripcion = doc.getString("descripcion")!!,
            fechaCreacionTimeStamp = doc.getString("fechaCreacionTimeStamp")!!,
            tipoActividad = doc.getString("tipoActividad")!!,
            meGusta = if (doc.getLong("meGusta") != null) doc.getLong("meGusta")
                ?.toInt()!! else 0,
            noMeGusta = if (doc.getLong("noMeGusta") != null) doc.getLong("noMeGusta")
                ?.toInt()!! else 0,
            comentarios = if (doc.getLong("comentarios") != null) doc.getLong("comentarios")
                ?.toInt()!! else 0
        )
        val autorUid = doc.getString("autorUid")!!
        actividad.autorUid = autorUid
        actividad.imagenes = doc.get("imagenes") as List<String>?

        return actividad
    }
}