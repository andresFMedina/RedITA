package com.itaeducativa.android.redita.ui.actividad.viewmodels

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.AbsListView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.itaeducativa.android.redita.data.modelos.Actividad
import com.itaeducativa.android.redita.data.repositorios.RepositorioActividad
import com.itaeducativa.android.redita.data.repositorios.RepositorioAutenticacion
import com.itaeducativa.android.redita.data.repositorios.RepositorioReaccion
import com.itaeducativa.android.redita.data.repositorios.RepositorioUsuario
import com.itaeducativa.android.redita.network.RequestListener
import com.itaeducativa.android.redita.ui.actividad.adapters.ListaActividadesAdapter
import com.itaeducativa.android.redita.ui.actividad.adapters.MisActividadesAdapter
import com.itaeducativa.android.redita.ui.actividad.adapters.NombresAdapter
import com.itaeducativa.android.redita.util.startFormularioActividadActivity


@Suppress("UNCHECKED_CAST")
class ListaActividadesViewModel(
    private val repositorioActividad: RepositorioActividad,
    private val repositorioUsuario: RepositorioUsuario,
    private val repositorioReaccion: RepositorioReaccion,
    private val repositorioAutenticacion: RepositorioAutenticacion
) : ViewModel() {

    private var isLastItemReached: Boolean = false
    private var isScrolling: Boolean = false
    val listaActividades: MutableLiveData<MutableList<Actividad>> = MutableLiveData()

    var requestListener: RequestListener? = null
    val listaActividadesAdapter by lazy {
        ListaActividadesAdapter(
            repositorioAutenticacion.currentUser()!!.uid
        )
    }

    val misActividadesAdapter = MisActividadesAdapter(this)


    var nombresActividadesAdapter: NombresAdapter? = null

    lateinit var onScrollListener: RecyclerView.OnScrollListener

    var lastVisible: DocumentSnapshot? = null


    fun guardarActividadEnFirestore(actividad: Actividad) {
        requestListener?.onStartRequest()
        repositorioActividad.guardarActividadEnFirestore(actividad).addOnFailureListener {
            requestListener?.onFailureRequest(it.message!!)
        }.addOnSuccessListener {
            repositorioActividad.guardarNombreActividadFirestore(
                actividad.nombre,
                actividad.id,
                actividad.categoria,
                actividad.autorUid!!
            )
            requestListener?.onSuccessRequest(actividad)

        }
    }

    fun getListaActividades(
        ordenCampo: String = "fechaCreacionTimeStamp",
        direccion: Query.Direction = Query.Direction.DESCENDING,
        query: String = "",
        tipo: String,
        cambioOrden: Boolean = false
    ) {
        requestListener?.onStartRequest()
        repositorioActividad.getActividades(ordenCampo, direccion, query, tipo)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    listaActividades.value = null
                    requestListener?.onFailureRequest(e.message!!)
                    return@addSnapshotListener
                }

                val actividades: MutableList<Actividad> = mutableListOf()
                for (doc in value!!) {
                    val actividad = crearActividadByDocumentReference(doc)
                    val referenciaAutor = repositorioUsuario.getUsuarioByUid(actividad.autorUid!!)
                    actividad.referenciaAutor = referenciaAutor
                    actividades.add(actividad)
                }
                listaActividades.value = actividades
                listaActividadesAdapter.actualizarActividades(listaActividades.value as MutableList<Actividad>)
                if (!value.isEmpty) {
                    lastVisible = value.documents.get(value.size() - 1)
                    if (cambioOrden) isLastItemReached = false

                    initScrollListener(
                        repositorioActividad.getActividadesNextPage(
                            ordenCampo,
                            direccion,
                            lastVisible!!,
                            "categoria",
                            tipo
                        ),
                        listaActividadesAdapter,
                        ordenCampo,
                        direccion,
                        "categoria",
                        tipo

                    )
                }
                requestListener?.onSuccessRequest(actividades)
            }
    }

    fun getActividadesByAutorUid(
        uid: String,
        orderBy: String = "fechaCreacionTimeStamp",
        query: String = ""
    ) {
        requestListener?.onStartRequest()
        repositorioActividad.getActividadesByAutorUid(uid, orderBy, query)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    listaActividades.value = null
                    requestListener?.onFailureRequest(e.message!!)
                    return@addSnapshotListener
                }

                val actividades: MutableList<Actividad> = mutableListOf()
                for (doc in value!!) {
                    val actividad = crearActividadByDocumentReference(doc)
                    actividades.add(actividad)
                }

                listaActividades.value = actividades
                misActividadesAdapter.actualizarActividades(actividades)
                if (!value.isEmpty) {
                    lastVisible = value.documents.get(value.size() - 1)

                    initScrollListener(
                        repositorioActividad.getActividadesNextPage(
                            orderBy,
                            Query.Direction.ASCENDING,
                            lastVisible!!,
                            "autorUid",
                            uid
                        ),
                        misActividadesAdapter,
                        orderBy,
                        Query.Direction.ASCENDING,
                        "autorUid",
                        uid

                    )
                }
                requestListener?.onSuccessRequest(actividades)

            }
    }

    /*fun eliminarActividad(actividad: Actividad) {
        requestListener?.onStartRequest()
        repositorioActividad.eliminarActividad(actividad).addOnSuccessListener {
            requestListener?.onSuccessRequest()
        }.addOnFailureListener {
            requestListener?.onFailureRequest(it.message!!)
        }
    }*/


    fun desactivarActividad(actividad: Actividad) {
        repositorioActividad.desactivarActividad(actividad).addOnSuccessListener {
            repositorioActividad.eliminarNombre(actividad.id)
        }
    }

    fun getNombresActividadesByCategoria(context: Context, categoria: String) {
        requestListener?.onStartRequest()
        repositorioActividad.getNombresActividad(categoria)
            .addSnapshotListener { value, exception ->
                if (exception != null) {
                    requestListener?.onFailureRequest(exception.message!!)
                    return@addSnapshotListener
                }
                val nombres: MutableList<String> = mutableListOf()
                for (doc in value!!) {
                    val nombre = doc.getString("nombre")
                    nombres.add(nombre!!)
                }
                nombresActividadesAdapter = NombresAdapter(context, nombres)
                requestListener?.onSuccessRequest(nombres.toList())
            }
    }

    fun getNombresActividadesByUidAutor(context: Context, uidAutor: String) {
        requestListener?.onStartRequest()
        repositorioActividad.getNombresActividadByAutorUid(uidAutor)
            .addSnapshotListener { value, exception ->
                if (exception != null) {
                    requestListener?.onFailureRequest(exception.message!!)
                    return@addSnapshotListener
                }
                val nombres: MutableList<String> = mutableListOf()
                for (doc in value!!) {
                    val nombre = doc.getString("nombre")
                    nombres.add(nombre!!)
                }
                nombresActividadesAdapter = NombresAdapter(context, nombres)
                requestListener?.onSuccessRequest(nombres.toList())
            }
    }

    fun initScrollListener(
        query: Query,
        adapter: Any,
        ordenCampo: String = "fechaCreacionTimeStamp",
        direccion: Query.Direction = Query.Direction.DESCENDING,
        nombreFiltro: String,
        filtro: String
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
                                listaActividades.value = null
                                requestListener?.onFailureRequest(e.message!!)
                                return@addSnapshotListener
                            }

                            val actividades: MutableList<Actividad> = mutableListOf()
                            for (doc in value!!) {
                                val actividad = crearActividadByDocumentReference(doc)
                                val referenciaAutor =
                                    repositorioUsuario.getUsuarioByUid(actividad.autorUid!!)
                                actividad.referenciaAutor = referenciaAutor
                                actividades.add(actividad)
                            }
                            listaActividades.value!!.addAll(actividades)
                            lastVisible = value.documents.get(value.size() - 1)
                            if (value.size() < 4) {
                                isLastItemReached = true;
                            }
                            Log.d("Pagina cargada", lastVisible.toString())
                            when (adapter) {
                                is ListaActividadesAdapter -> {
                                    adapter.actualizarActividades(listaActividades.value as MutableList<Actividad>)

                                }
                                is MisActividadesAdapter ->
                                    adapter.actualizarActividades(listaActividades.value as MutableList<Actividad>)
                            }
                            //listaActividadesAdapter.actualizarActividades(listaActividades.value as MutableList<Actividad>)
                            val nextQuery = repositorioActividad.getActividadesNextPage(
                                ordenCampo,
                                direccion,
                                lastVisible!!,
                                nombreFiltro,
                                filtro
                            )
                            initScrollListener(
                                nextQuery,
                                adapter,
                                ordenCampo,
                                direccion,
                                nombreFiltro,
                                filtro
                            )
                            requestListener?.onSuccessRequest(actividades)

                        }
                    }
                }
            }
    }

    fun goToCrearActividad(view: View) {
        view.context.startFormularioActividadActivity(null)
    }

    private fun crearActividadByDocumentReference(doc: QueryDocumentSnapshot): Actividad {
        val actividad = doc.toObject(Actividad::class.java)
        val autorUid = doc.getString("autorUid")!!
        actividad.autorUid = autorUid

        return actividad
    }

}