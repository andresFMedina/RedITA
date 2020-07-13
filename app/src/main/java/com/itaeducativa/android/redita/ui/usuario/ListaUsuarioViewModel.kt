package com.itaeducativa.android.redita.ui.usuario

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.AbsListView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.Query
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
    val gradoEstudiante = MutableLiveData<String>("4B")

    val listaUsuarios = MutableLiveData<MutableList<Usuario>>()
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

    private var isLastItemReached: Boolean = false
    private var isScrolling: Boolean = false

    lateinit var onScrollListener: RecyclerView.OnScrollListener

    var lastVisible: DocumentSnapshot? = null

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
            if (!value.isEmpty) lastVisible = value.documents.get(value.size() - 1)
            initScrollListener(repositorioUsuario.getUsuariosNextPage(lastVisible!!))
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

    fun initScrollListener(
        query: Query
    ) {
        onScrollListener =
            object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(
                    recyclerView: RecyclerView,
                    newState: Int
                ) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                        isScrolling = true
                    }
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val linearLayoutManager =
                        recyclerView.layoutManager as LinearLayoutManager?
                    val firstVisibleItemPosition =
                        linearLayoutManager!!.findFirstVisibleItemPosition()
                    val visibleItemCount = linearLayoutManager.childCount
                    val totalItemCount = linearLayoutManager.itemCount
                    if (isScrolling && firstVisibleItemPosition + visibleItemCount == totalItemCount && !isLastItemReached) {
                        isScrolling = false
                        requestListener?.onStartRequest()
                        query.addSnapshotListener { value, e ->
                            if (e != null) {
                                listaUsuarios.value = null
                                requestListener?.onFailureRequest(e.message!!)
                                return@addSnapshotListener
                            }
                            val usuarios: MutableList<Usuario> = mutableListOf()
                            for (v in value!!) {
                                val usuario = v.toObject(Usuario::class.java)
                                usuarios.add(usuario)
                            }

                            listaUsuarios.value!!.addAll(usuarios)
                            lastVisible = value.documents.get(value.size() - 1)
                            if (value.size() < 6) {
                                isLastItemReached = true
                            }
                            Log.d("Pagina cargada", lastVisible.toString())

                            listaUsuariosAdapter.actualizarUsuarios(listaUsuarios.value!!)
                            val nextQuery = repositorioUsuario.getUsuariosNextPage(lastVisible!!)
                            initScrollListener(nextQuery)
                            requestListener?.onSuccessRequest(usuarios)

                        }
                    }
                }
            }
    }

}