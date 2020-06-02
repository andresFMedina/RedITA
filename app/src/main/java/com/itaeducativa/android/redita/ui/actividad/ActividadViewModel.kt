package com.itaeducativa.android.redita.ui.actividad

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentReference
import com.itaeducativa.android.redita.data.modelos.Actividad
import com.itaeducativa.android.redita.data.modelos.Usuario
import com.itaeducativa.android.redita.network.RequestListener
import com.itaeducativa.android.redita.ui.actividad.reaccion.ReaccionListener
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

    var requestListener: RequestListener? = null

    fun bind(actividad: Actividad) {
        nombre.value = actividad.nombre
        descripcion.value = actividad.descripcion
        fechaCreacionTimeStamp.value = actividad.fechaCreacionTimeStamp?.toDate().toString()
        tipoActividad.value = actividad.tipoActividad
        this.actividad.value = actividad
        meGusta.value = actividad.meGusta.toString()
        noMeGusta.value = actividad.noMeGusta.toString()
        comentarios.value = actividad.comentarios.toString()
        if (actividad.autor == null)
            bindAutor(actividad.referenciaAutor)
        else
            autor.value = actividad.autor!!.nombreCompleto
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
            Log.d("Actividad", actividad.value?.autor?.nombreCompleto!!)
            requestListener?.onSuccessRequest()
        }
    }


    fun verActividad(view: View) {
        view.context.startActividadActivity(actividad.value!!)
    }

}