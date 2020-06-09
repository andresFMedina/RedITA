package com.itaeducativa.android.redita.ui.actividad.actividad.viewmodels

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.itaeducativa.android.redita.data.modelos.Actividad
import com.itaeducativa.android.redita.data.modelos.Reaccion
import com.itaeducativa.android.redita.data.modelos.Usuario
import com.itaeducativa.android.redita.network.RequestListener
import com.itaeducativa.android.redita.ui.actividad.actividad.adapters.ImagenesAdapter
import com.itaeducativa.android.redita.util.startActividadActivity

class ActividadViewModel : ViewModel() {
    val nombre = MutableLiveData<String>()
    val descripcion = MutableLiveData<String>()
    val autor = MutableLiveData<String>()
    val fechaCreacionTimeStamp = MutableLiveData<String>()
    val tipoActividad = MutableLiveData<String>()
    val actividad = MutableLiveData<Actividad>()
    val meGusta = MutableLiveData<String>()
    val noMeGusta = MutableLiveData<String>()
    val comentarios = MutableLiveData<String>()
    val imagenPerfilUrl = MutableLiveData<String>()
    val imagenes = MutableLiveData<List<String>>()
    val reaccion = MutableLiveData<Reaccion>()
    private var fueConsultado = false

    var requestListener: RequestListener? = null

    val imagenesAdapter by lazy {
        ImagenesAdapter(imagenes.value)
    }

    fun bind(actividad: Actividad) {
        nombre.value = actividad.nombre
        descripcion.value = actividad.descripcion
        fechaCreacionTimeStamp.value = actividad.fechaCreacionTimeStamp
        tipoActividad.value = actividad.tipoActividad
        this.actividad.value = actividad
        meGusta.value = actividad.meGusta.toString()
        noMeGusta.value = actividad.noMeGusta.toString()
        comentarios.value = actividad.comentarios.toString()
        imagenes.value = actividad.imagenes
        if (actividad.autor == null) {
            bindAutor(actividad.referenciaAutor)
            imagenPerfilUrl.value = "gs://redita.appspot.com/img_profile.png"
        } else {
            autor.value = actividad.autor!!.nombreCompleto

            imagenPerfilUrl.value = actividad.autor!!.imagenPerfilUrl
        }
    }

    fun bindAutor(referenciaAutor: DocumentReference?) {
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
            requestListener?.onSuccessRequest()
        }
    }

    fun getReaccionByActividadIdYUsuarioUid(query: Query) {
        if(reaccion.value == null && !fueConsultado)
        {
            requestListener?.onStartRequest()
            query.get().addOnCompleteListener {
                if (it.isSuccessful) {
                    fueConsultado = true
                    requestListener?.onSuccessRequest()
                    reaccion.value = it.result?.firstOrNull()?.toObject(Reaccion::class.java)
                    actividad.value?.reaccion = reaccion.value
                } else {
                    requestListener?.onFailureRequest("Falló")
                }
            }}
    }


    fun verActividad(view: View) {
        view.context.startActividadActivity(actividad.value!!)
        Log.d("Escucha", "Im listening")
    }

}