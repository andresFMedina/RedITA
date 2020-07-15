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
import com.itaeducativa.android.redita.ui.actividad.adapters.NombresAdapter
import com.itaeducativa.android.redita.util.getExtension

class ListaUsuarioViewModel(
    private val repositorioUsuario: RepositorioUsuario,
    private val repositorioStorage: RepositorioStorage
) : ViewModel() {

    val usuario: MutableLiveData<Usuario> = MutableLiveData()
    val email = MutableLiveData<String>()
    val nombreCompleto = MutableLiveData<String>()
    val telefono = MutableLiveData<String>()
    val rol = MutableLiveData<String>()
    val imagenPerfilUrl = MutableLiveData<String>()
    val cantidadMeGusta = MutableLiveData<String>()
    val cantidadNoMeGusta = MutableLiveData<String>()
    val cantidadComentarios = MutableLiveData<String>()
    val nombreEstudiante = MutableLiveData<String>("")
    val gradoEstudiante = MutableLiveData<String>()

    val listaUsuarios = MutableLiveData<List<Usuario>>()
    val listaUsuariosAdapter = UsuarioAdapter(this)

    var requestListener: RequestListener? = null
    var imageUploadListener: ImageUploadListener? = null

    var nombresUsuariosAdapter: NombresAdapter? = null

    fun bindUsuario(usuario: Usuario) {
        this.usuario.value = usuario
        email.value = usuario.email
        nombreCompleto.value = usuario.nombreCompleto
        telefono.value = usuario.telefono
        rol.value = usuario.rol
        imagenPerfilUrl.value = usuario.imagenPerfilUrl
        cantidadMeGusta.value = usuario.meGusta.toString()
        cantidadNoMeGusta.value = usuario.noMeGusta.toString()
        cantidadComentarios.value = usuario.comentarios.toString()
    }

    fun guardarUsuario(email: String, uid: String, urlImagen: String?) {
        val imagen = urlImagen ?: "gs://redita.appspot.com/img_profile.png"

        usuario.value = Usuario(
            nombreCompleto = nombreCompleto.value!!,
            email = email,
            rol = "",
            telefono = telefono.value!!,
            uid = uid,
            imagenPerfilUrl = imagen,
            nombreEstudiante = nombreEstudiante.value!!,
            gradoEstudiante = if (nombreEstudiante.value!!.isBlank()) "" else gradoEstudiante.value!!
        )

        requestListener?.onStartRequest()
        repositorioUsuario.guardarUsuario(usuario.value!!).addOnSuccessListener {
            repositorioUsuario.guardarNombreUsuarioFirestore(nombreCompleto.value!!, uid)
            requestListener?.onSuccessRequest(usuario.value)
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
                requestListener?.onSuccessRequest(usuario.value)

            })
    }

    fun getUsuarios(query: String = "") {
        repositorioUsuario.getUsuarios(query).addSnapshotListener { value, exception ->
            requestListener?.onStartRequest()
            if (exception != null) {
                requestListener?.onFailureRequest(exception.message!!)
                return@addSnapshotListener
            }
            val usuarios: MutableList<Usuario> = mutableListOf()
            for (v in value!!) {
                val usuario = v.toObject(Usuario::class.java)
                usuarios.add(usuario)
            }
            listaUsuarios.value = usuarios
            listaUsuariosAdapter.actualizarUsuarios(usuarios)
            requestListener?.onSuccessRequest(usuarios)
        }
    }

    fun modificarTelefono() {
        repositorioUsuario.modificarTelefono(telefono.value!!, usuario.value!!.uid)
    }

    fun modificarRol(rol: String, uid: String) {
        repositorioUsuario.modificarRolUsuario(rol, uid)
    }

    fun cambiarImagenPerfil(uriImagen: Uri, context: Context) {
        val ruta = "${System.currentTimeMillis()}.${getExtension(uriImagen, context)}"
        repositorioStorage.subirArchivoStorage(ruta, uriImagen).addOnSuccessListener {
            val url = "gs://redita.appspot.com${it.storage.path}"
            repositorioUsuario.cambiarUrlImagenPerfil(url, usuario.value!!.uid)
        }
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

    fun getNombresUsuarios(context: Context) {
        requestListener?.onStartRequest()
        repositorioUsuario.getNombresUsuarios().addSnapshotListener { value, exception ->
            if (exception != null) {
                requestListener?.onFailureRequest(exception.message!!)
                return@addSnapshotListener
            }
            val nombres: MutableList<String> = mutableListOf()
            for (doc in value!!) {
                val nombre = doc.getString("nombre")
                nombres.add(nombre!!)
            }
            nombresUsuariosAdapter = NombresAdapter(context, nombres)
            requestListener?.onSuccessRequest(nombres.toList())
        }
    }

}