package com.itaeducativa.android.redita.ui.archivo

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.itaeducativa.android.redita.data.modelos.Actividad
import com.itaeducativa.android.redita.data.modelos.Archivo
import com.itaeducativa.android.redita.data.repositorios.RepositorioArchivo
import com.itaeducativa.android.redita.data.repositorios.RepositorioStorage
import com.itaeducativa.android.redita.network.RequestListener

class ListaArchivoViewModel(
    private val repositorioArchivo: RepositorioArchivo,
    private val repositorioStorage: RepositorioStorage
) : ViewModel() {

    val listaArchivos = MutableLiveData<List<Archivo>>()
    val listaArchivoAdapter = ListaArchivoAdapter()
    var requestListener: RequestListener? = null

    fun guardarArchivoFirestore(archivo: Archivo, ruta: String, uri: Uri) {
        requestListener?.onStartRequest()
        repositorioArchivo.guardarArchivoFirestore(archivo).addOnSuccessListener {
            requestListener?.onSuccessRequest()
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
                requestListener?.onSuccessRequest()
            }
    }

    fun subirArchivoStorage(archivoId: String, ruta: String, uri: Uri) {
        repositorioStorage.subirArchivoStorage(ruta, uri).addOnSuccessListener {
            val urlImagen = "gs://redita.appspot.com${it.storage.path}"
            repositorioArchivo.guardarUrlArchivoFirestore(archivoId, urlImagen)
        }
    }

}