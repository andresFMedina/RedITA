package com.itaeducativa.android.redita.ui.usuario

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.EventListener
import com.itaeducativa.android.redita.data.modelos.Usuario
import com.itaeducativa.android.redita.data.repositorios.RepositorioStorage
import com.itaeducativa.android.redita.data.repositorios.RepositorioUsuario
import com.itaeducativa.android.redita.network.RequestListener
import com.itaeducativa.android.redita.ui.ImageUploadListener
import com.itaeducativa.android.redita.util.getExtension

class UsuarioViewModel(
    private val repositorioUsuario: RepositorioUsuario,
    private val repositorioStorage: RepositorioStorage
) : ViewModel() {

    val usuario: MutableLiveData<Usuario> = MutableLiveData()
    var email: String? = null
    var nombreCompleto: String? = null
    var telefono: String? = null

    var requestListener: RequestListener? = null
    var imageUploadListener: ImageUploadListener? = null

    fun guardarUsuario(email: String, uid: String, urlImagen: String?) {
        val imagen = if (urlImagen != null) urlImagen else "gs://redita.appspot.com/img_profile.png"

        usuario.value = Usuario(
            nombreCompleto = nombreCompleto!!,
            email = email,
            rol = "",
            telefono = telefono!!,
            uid = uid,
            imagenPerfilUrl = imagen
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

    fun uploadProfileImage(uriImagen: Uri, context: Context) {
        val ruta = "${System.currentTimeMillis()}.${getExtension(uriImagen, context)}"
        imageUploadListener?.onStartUploadImage()
        repositorioStorage.subirArchivoStorage(ruta, uriImagen).addOnSuccessListener {
            val url = "gs://redita.appspot.com${it.storage.path}"
            imageUploadListener?.onSuccessUploadImage(url)
        }.addOnFailureListener {
            imageUploadListener?.onFailureUploadImage(it.message!!)
        }
    }

}