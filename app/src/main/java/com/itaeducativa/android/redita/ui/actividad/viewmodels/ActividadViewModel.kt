package com.itaeducativa.android.redita.ui.actividad.viewmodels

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentReference
import com.itaeducativa.android.redita.data.modelos.Actividad
import com.itaeducativa.android.redita.data.modelos.Archivo
import com.itaeducativa.android.redita.data.modelos.Reaccion
import com.itaeducativa.android.redita.data.modelos.Usuario
import com.itaeducativa.android.redita.network.RequestListener
import com.itaeducativa.android.redita.util.startActividadActivity

class ActividadViewModel : ViewModel() {
    var categoria = MutableLiveData<String>()
    val nombre = MutableLiveData<String>()
    val descripcion = MutableLiveData<String>()
    val autor = MutableLiveData<String>()
    private val fechaCreacionTimeStamp = MutableLiveData<String>()
    val tipoActividad = MutableLiveData<String>()
    val actividad = MutableLiveData<Actividad>()
    val meGusta = MutableLiveData<String>()
    val noMeGusta = MutableLiveData<String>()
    val comentarios = MutableLiveData<String>()
    val imagenPerfilUrl = MutableLiveData<String>()
    val imagenes = MutableLiveData<List<String>>()
    val reaccion = MutableLiveData<Reaccion>()
    val horaInicio = MutableLiveData<String>()
    val fechaInicio = MutableLiveData<String>()
    private val listaArchivos = MutableLiveData<List<Archivo>>()


    var requestListener: RequestListener? = null

    /*val imagenesAdapter by lazy {
         ImagenesAdapter(imagenes.value)
     }*/

    fun bind(actividad: Actividad) {
        categoria.value = actividad.categoria
        nombre.value = actividad.nombre
        descripcion.value = actividad.descripcion
        fechaCreacionTimeStamp.value = actividad.id
        tipoActividad.value = actividad.tipoActividad
        this.actividad.value = actividad
        meGusta.value = actividad.meGusta.toString()
        noMeGusta.value = actividad.noMeGusta.toString()
        comentarios.value = actividad.comentarios.toString()
        horaInicio.value = actividad.horaInicio
        fechaInicio.value = actividad.fechaInicio
        listaArchivos.value = actividad.archivos
        reaccion.value = actividad.reaccion
        if (actividad.autor == null) {
            bindAutor(actividad.referenciaAutor)
            imagenPerfilUrl.value = "gs://redita.appspot.com/img_profile.png"
        } else {
            autor.value = actividad.autor!!.nombreCompleto

            imagenPerfilUrl.value = actividad.autor!!.imagenPerfilUrl
        }

        if(reaccion.value != null) this.actividad.value!!.reaccion
    }

    private fun bindAutor(referenciaAutor: DocumentReference?) {
        referenciaAutor?.addSnapshotListener { value, exception ->
            requestListener?.onStartRequest()
            if (exception != null) {
                requestListener?.onFailureRequest(exception.message!!)
                return@addSnapshotListener
            }
            actividad.value!!.autor = value?.toObject(Usuario::class.java)!!
            autor.value = actividad.value!!.autor!!.nombreCompleto
            actividad.value!!.referenciaAutor = null
            imagenPerfilUrl.value = actividad.value!!.autor!!.imagenPerfilUrl
            requestListener?.onSuccessRequest(actividad.value!!.autor)
        }
    }

    fun verActividad(view: View) {
        actividad.value!!.referenciaAutor = null
        view.context.startActividadActivity(actividad.value!!)
    }

}