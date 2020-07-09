package com.itaeducativa.android.redita.ui.archivo

import android.net.Uri
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.itaeducativa.android.redita.data.modelos.Actividad
import com.itaeducativa.android.redita.data.modelos.Archivo
import com.itaeducativa.android.redita.data.repositorios.RepositorioArchivo
import com.itaeducativa.android.redita.data.repositorios.RepositorioAutenticacion
import com.itaeducativa.android.redita.data.repositorios.RepositorioStorage
import com.itaeducativa.android.redita.network.RequestListener
import com.itaeducativa.android.redita.util.startArchivoDetalladoActivity

class ListaArchivoViewModel(
    private val repositorioArchivo: RepositorioArchivo,
    private val repositorioStorage: RepositorioStorage,
    private val repositorioAutenticacion: RepositorioAutenticacion
) : ViewModel() {

    val listaArchivos = MutableLiveData<List<Archivo>>()
    val listaArchivoAdapter = ListaArchivoAdapter(repositorioAutenticacion.currentUser()!!.uid)
    var requestListener: RequestListener? = null

    fun guardarArchivoFirestore(archivo: Archivo, ruta: String, uri: Uri) {
        requestListener?.onStartRequest()
        repositorioArchivo.guardarArchivoFirestore(archivo).addOnSuccessListener {
            requestListener?.onSuccessRequest(archivo)
            subirArchivoStorage(archivo.id, ruta, uri)
        }.addOnFailureListener {
            requestListener?.onFailureRequest(it.message!!)
        }
    }

    fun getArchivosByActividadId(actividad: Actividad, limit: Long = 10) {
        repositorioArchivo.getArchivosByActividadId(actividad.id, limit)
            .addSnapshotListener { value, exception ->
                requestListener?.onStartRequest()
                if (exception != null) {
                    requestListener?.onFailureRequest(exception.message!!)
                    return@addSnapshotListener
                }
                val archivos: MutableList<Archivo> = mutableListOf()
                for (v in value!!) {
                    val archivo = v.toObject(Archivo::class.java)
                    archivos.add(archivo)
                }
                listaArchivos.value = archivos
                actividad.archivos = archivos
                listaArchivoAdapter.actualizarArchivos(archivos)
                requestListener?.onSuccessRequest(listaArchivos)
            }
    }

    fun subirArchivoStorage(archivoId: String, ruta: String, uri: Uri) {
        repositorioStorage.subirArchivoStorage(ruta, uri).addOnSuccessListener {
            val urlImagen = "gs://redita.appspot.com${it.storage.path}"
            repositorioArchivo.guardarUrlArchivoFirestore(archivoId, urlImagen)
        }
    }


}