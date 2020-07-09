package com.itaeducativa.android.redita.ui.historial

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.itaeducativa.android.redita.data.modelos.Actividad
import com.itaeducativa.android.redita.data.modelos.Historial
import com.itaeducativa.android.redita.data.modelos.Usuario
import com.itaeducativa.android.redita.data.repositorios.RepositorioActividad
import com.itaeducativa.android.redita.data.repositorios.RepositorioHistorial
import com.itaeducativa.android.redita.data.repositorios.RepositorioUsuario
import com.itaeducativa.android.redita.network.RequestListener

class ListaHistorialViewModel(
    private val repositorioHistorial: RepositorioHistorial,
    private val repositorioActividad: RepositorioActividad,
    private val repositorioUsuario: RepositorioUsuario
) : ViewModel() {
    private val listaHistorial = MutableLiveData<List<Historial>>()
    val listaHistorialAdapter = ListaHistorialAdapter()

    var requestListener: RequestListener? = null


    fun getHistorial() {
        requestListener?.onStartRequest()
        repositorioHistorial.getListaHistorial().addSnapshotListener { snapshot, exception ->
            snapshotListener(snapshot, exception)
        }
    }

    fun getHistorialByUsuarioUid(usuarioUid: String) {
        requestListener?.onStartRequest()
        repositorioHistorial.getListaHistorialByUsuarioUid(usuarioUid)
            .addSnapshotListener { snapshot, exception ->
                snapshotListener(snapshot, exception)
            }
    }

    fun snapshotListener(snapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
        if (exception != null) {
            requestListener?.onFailureRequest(exception.message!!)
        }

        val historiales: MutableList<Historial> = mutableListOf()
        for (data in snapshot!!) {
            val historial = data.toObject(Historial::class.java)
            repositorioActividad.getActividadesById(historial.actividadId)
                .addSnapshotListener { snapshotActividad, exceptionActividad ->
                    historial.actividad = snapshotActividad?.toObject(Actividad::class.java)
                    repositorioUsuario.getUsuarioByUid(historial.usuarioUid)
                        .addSnapshotListener { snapshotUsuario, exceptionUsuario ->
                            historial.usuario = snapshotUsuario?.toObject(Usuario::class.java)
                            historiales.add(historial)
                            listaHistorial.value = historiales
                            listaHistorialAdapter.actualizarHistorial(historiales)
                            requestListener?.onSuccessRequest(historiales)
                        }
                }


        }

    }


}