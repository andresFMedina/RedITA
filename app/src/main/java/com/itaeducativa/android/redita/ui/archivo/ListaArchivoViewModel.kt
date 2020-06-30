package com.itaeducativa.android.redita.ui.archivo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.itaeducativa.android.redita.data.modelos.Archivo
import com.itaeducativa.android.redita.data.repositorios.RepositorioArchivo
import com.itaeducativa.android.redita.network.RequestListener

class ListaArchivoViewModel(
    private val repositorioArchivo: RepositorioArchivo
): ViewModel() {

    val listaArchivos = MutableLiveData<List<Archivo>>()
    var requestListener: RequestListener? = null

    fun guardarArchivoFirestore(archivo: Archivo) {
        requestListener?.onStartRequest()
        repositorioArchivo.guardarArchivoFirestore(archivo).addOnSuccessListener {
            requestListener?.onSuccessRequest()
        }.addOnFailureListener {
            requestListener?.onFailureRequest(it.message!!)
        }
    }

    fun getArchivosByActividadId(actividadId: String) {
        repositorioArchivo.getArchivosByActividadId(actividadId).addSnapshotListener { value, exception ->
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
            //listaUsuariosAdapter.actualizarUsuarios(usuarios)
            requestListener?.onSuccessRequest()
        }
    }
}