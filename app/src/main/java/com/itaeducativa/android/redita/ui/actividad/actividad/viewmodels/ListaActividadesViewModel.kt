package com.itaeducativa.android.redita.ui.actividad.actividad.viewmodels

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.itaeducativa.android.redita.data.modelos.Actividad
import com.itaeducativa.android.redita.data.repositorios.RepositorioActividad
import com.itaeducativa.android.redita.data.repositorios.RepositorioUsuario
import com.itaeducativa.android.redita.network.RequestListener
import com.itaeducativa.android.redita.ui.actividad.actividad.adapters.ListaActividadesAdapter
import com.itaeducativa.android.redita.ui.actividad.actividad.adapters.MisActividadesAdapter
import com.itaeducativa.android.redita.ui.actividad.reaccion.ReaccionListener
import com.itaeducativa.android.redita.util.startCrearActividadActivity

@Suppress("UNCHECKED_CAST")
class ListaActividadesViewModel(
    private val repositorioActividad: RepositorioActividad,
    private val repositorioUsuario: RepositorioUsuario
) : ViewModel(), ReaccionListener {

    private val listaActividades: MutableLiveData<List<Actividad>> = MutableLiveData()

    var requestListener: RequestListener? = null
    val listaActividadesAdapter by lazy {
        ListaActividadesAdapter(
            this
        )
    }

    val misActividadesAdapter: MisActividadesAdapter by lazy {
        MisActividadesAdapter()
    }


    fun guardarActividadEnFirestore(actividad: Actividad) {
        requestListener?.onStartRequest()
        repositorioActividad.guardarActividadEnFirestore(actividad).addOnFailureListener {
            requestListener?.onFailureRequest(it.message!!)
        }. addOnSuccessListener {
            requestListener?.onSuccessRequest()
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
                    val autorUid = doc.getString("autorUid")!!
                    actividad.autorUid = autorUid
                    actividad.imagenes = doc.get("imagenes") as List<String>?

                    val referenciaAutor = repositorioUsuario.getUsuarioByUid(autorUid)
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
                    val autorUid = doc.getString("autorUid")!!
                    actividad.autorUid = autorUid

                    actividades.add(actividad)
                }

                listaActividades.value = actividades
                requestListener?.onSuccessRequest()
                misActividadesAdapter.actualizarActividades(actividades)
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

    fun goToCrearActividad(view: View) {
        view.context.startCrearActividadActivity()
    }
}