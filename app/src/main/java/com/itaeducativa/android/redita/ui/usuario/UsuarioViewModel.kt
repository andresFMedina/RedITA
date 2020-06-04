package com.itaeducativa.android.redita.ui.usuario

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import com.itaeducativa.android.redita.data.modelos.Usuario
import com.itaeducativa.android.redita.data.repositorios.RepositorioUsuario
import com.itaeducativa.android.redita.network.RequestListener

class UsuarioViewModel(private val repositorioUsuario: RepositorioUsuario): ViewModel() {

    val usuario : MutableLiveData<Usuario> = MutableLiveData()
    var email: String? = null
    var nombreCompleto: String? = null
    var telefono:String? = null

    var requestListener: RequestListener? = null

    fun guardarUsuario(email: String, uid: String) {
        usuario.value = Usuario(
            nombreCompleto = nombreCompleto!!,
            email = email,
            rol = "",
            telefono = telefono!!,
            uid = uid,
            imagenPerfilUrl = ""
        )
        requestListener?.onStartRequest()
        repositorioUsuario.guardarUsuario(usuario.value!!).addOnSuccessListener {
           requestListener?.onSuccessRequest()
        }.addOnFailureListener { exception ->
           requestListener?.onFailureRequest(exception.message!!)
        }

    }

    fun getUsuarioByUid(uid: String) {
        repositorioUsuario.getUsuarioByUid(uid)
            .addSnapshotListener(EventListener { value, e ->
                requestListener?.onStartRequest()
                if (e != null) {
                    usuario.value = null
                    requestListener?.onFailureRequest(e.message!!)
                    return@EventListener
                }
                usuario.value = value!!.toObject(Usuario::class.java)
                requestListener?.onSuccessRequest()

            })
    }



}