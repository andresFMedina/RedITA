package com.itaeducativa.android.redita.ui.actividad

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.itaeducativa.android.redita.data.modelos.Actividad
import com.itaeducativa.android.redita.data.repositorios.RepositorioActividad
import com.itaeducativa.android.redita.network.RequestListener
import com.itaeducativa.android.redita.ui.actividad.reaccion.ReaccionListener

class ListaActividadesViewModel(
    private val repositorioActividad: RepositorioActividad
) : ViewModel(), ReaccionListener {

    private val listaActividades: MutableLiveData<List<Actividad>> = MutableLiveData()

    var requestListener: RequestListener? = null
    val listaActividadesAdapter = ListaActividadesAdapter(this)

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
                        nombre = doc.getString("nombre")!!,
                        descripcion = doc.getString("descripcion")!!,
                        fechaCreacionTimeStamp = doc.getTimestamp("fechaCreacionTimeStamp")!!,
                        tipoActividad = doc.getString("tipoActividad")!!,
                        meGusta = if (doc.getLong("meGusta") != null) doc.getLong("meGusta")
                            ?.toInt()!! else 0,
                        noMeGusta = if (doc.getLong("noMeGusta") != null) doc.getLong("noMeGusta")
                            ?.toInt()!! else 0,
                        comentarios = if (doc.getLong("comentarios") != null) doc.getLong("comentarios")
                            ?.toInt()!! else 0
                    )
                    actividad.referenciaAutor = doc.getDocumentReference("autor")!!
                    actividades.add(actividad)
                }
                listaActividades.value = actividades
                requestListener?.onSuccessRequest()
                listaActividadesAdapter.actualizarActividades(listaActividades.value as MutableList<Actividad>)
            }


    }

    fun agregarReaccion(actividad: Actividad, reaccion: String) {
        when(reaccion) {
            "meGusta" -> repositorioActividad.agregarReaccion(actividad, reaccion,actividad.meGusta++).addOnSuccessListener{
                Log.d("Like", "Puesto ${reaccion} = ${actividad.meGusta}")
            }.addOnFailureListener {
                Log.e("error", it.message!!)
            }
            "noMeGusta" -> repositorioActividad.agregarReaccion(actividad, reaccion,actividad.noMeGusta++)
        }
    }

    override fun onMeGusta(actividad: Actividad) {
        agregarReaccion(actividad, "meGusta")
    }

    override fun onNoMeGusta(actividad: Actividad) {
        agregarReaccion(actividad, "noMeGusta")
    }
}