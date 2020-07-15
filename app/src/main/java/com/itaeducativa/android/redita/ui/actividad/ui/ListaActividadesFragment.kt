package com.itaeducativa.android.redita.ui.actividad.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.OnFocusChangeListener
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.Query
import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.data.modelos.Actividad
import com.itaeducativa.android.redita.data.modelos.Publicacion
import com.itaeducativa.android.redita.data.modelos.Reaccion
import com.itaeducativa.android.redita.databinding.FragmentListaActividadesBinding
import com.itaeducativa.android.redita.network.RequestListener
import com.itaeducativa.android.redita.ui.actividad.viewmodels.ListaActividadesViewModel
import com.itaeducativa.android.redita.ui.actividad.viewmodels.ListaActividadesViewModelFactory
import com.itaeducativa.android.redita.ui.archivo.ListaArchivoViewModel
import com.itaeducativa.android.redita.ui.archivo.ListaArchivoViewModelFactory
import com.itaeducativa.android.redita.ui.login.AutenticacionViewModel
import com.itaeducativa.android.redita.ui.login.AutenticacionViewModelFactory
import com.itaeducativa.android.redita.ui.reaccion.ReaccionListener
import com.itaeducativa.android.redita.ui.reaccion.ReaccionViewModel
import com.itaeducativa.android.redita.ui.reaccion.ReaccionViewModelFactory
import com.itaeducativa.android.redita.util.hideKeyboard
import com.itaeducativa.android.redita.util.showInputMethod
import kotlinx.android.synthetic.main.fragment_lista_actividades.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

private const val ARG_TIPO = "tipo"
private const val MAS_RECIENTE = "Más Reciente"
private const val MAS_ANTIGUO = "Más antiguo"

class ListaActividadesFragment : Fragment(), KodeinAware, RequestListener, ReaccionListener,
    SearchView.OnCloseListener, AdapterView.OnItemClickListener,
    AdapterView.OnItemSelectedListener {

    override val kodein: Kodein by kodein()
    private val factoryListaActividades: ListaActividadesViewModelFactory by instance()
    private val factoryListaArchivos: ListaArchivoViewModelFactory by instance()
    private val factoryReacion: ReaccionViewModelFactory by instance()
    private val factoryAutenticacion: AutenticacionViewModelFactory by instance()

    private lateinit var listaActividadViewModel: ListaActividadesViewModel
    private lateinit var listaArchivosViewModel: ListaArchivoViewModel
    private lateinit var reaccionViewModel: ReaccionViewModel
    private lateinit var autenticacionViewModel: AutenticacionViewModel
    private lateinit var autocomplete: SearchView.SearchAutoComplete

    private lateinit var tipo: String
    private lateinit var usuarioUid: String

    private lateinit var recyclerViewActividades: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tipo = it.getString(ARG_TIPO)!!
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentListaActividadesBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_lista_actividades, container, false
        )

        listaActividadViewModel = ViewModelProviders.of(this, factoryListaActividades)
            .get(ListaActividadesViewModel::class.java)
        listaArchivosViewModel =
            ViewModelProviders.of(this, factoryListaArchivos).get(ListaArchivoViewModel::class.java)
        reaccionViewModel =
            ViewModelProviders.of(this, factoryReacion).get(ReaccionViewModel::class.java)
        autenticacionViewModel = ViewModelProviders.of(this, factoryAutenticacion)
            .get(AutenticacionViewModel::class.java)

        listaActividadViewModel.listaActividadesAdapter.reaccionListener = this

        binding.viewModel = listaActividadViewModel

        initRecyclerView(binding)


        listaActividadViewModel.requestListener = this
        listaArchivosViewModel.requestListener = this
        reaccionViewModel.requestListener = this

        usuarioUid = autenticacionViewModel.usuario!!.uid

        binding.itemQuantitySpinner.onItemSelectedListener = this

        val arrayAdapter = ArrayAdapter(
            context!!,
            android.R.layout.simple_spinner_item,
            listOf(MAS_RECIENTE, MAS_ANTIGUO)
        )
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.itemQuantitySpinner.adapter = arrayAdapter


        return binding.root
    }

    private fun initRecyclerView(binding: FragmentListaActividadesBinding) {
        recyclerViewActividades = binding.recyclerViewActividades
        linearLayoutManager = LinearLayoutManager(context!!)
        recyclerViewActividades.layoutManager = linearLayoutManager
    }

    override fun onResume() {
        super.onResume()
        listaActividadViewModel.requestListener = this
        listaActividadViewModel.getListaActividades(
            tipo = tipo
        )
        listaActividadViewModel.getNombresActividadesByCategoria(context!!, tipo)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ListaActividadesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(tipo: String) =
            ListaActividadesFragment()
                .apply {
                    arguments = Bundle().apply {
                        putString(ARG_TIPO, tipo)
                    }
                }
    }

    override fun onSuccessRequest(response: Any?) {
        when (response) {
            is List<*> -> {
                if (response.isNotEmpty()) {
                    when (response[0]) {
                        is Actividad -> {
                            obtenerObjetosActividad()
                            recyclerViewActividades.addOnScrollListener(listaActividadViewModel.onScrollListener)
                            Log.d("Lista General", listaActividadViewModel.listaActividades.value?.size.toString())
                        }
                        is String -> autocomplete.setAdapter(
                            listaActividadViewModel.nombresActividadesAdapter
                        )
                    }
                }
            }
            is Reaccion? -> reaccionConsultada(response)
        }
        progressBarListaActividades.visibility = View.GONE

    }


    override fun onFailureRequest(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        Log.e("Error", message)
    }

    override fun onStartRequest() {
        progressBarListaActividades.visibility = View.VISIBLE
    }

    override fun onPause() {
        super.onPause()
        listaActividadViewModel.requestListener = null
    }

    fun obtenerObjetosActividad() {
        val listaActividades = listaActividadViewModel.listaActividades.value
        if (listaActividades != null) {
            for (actividad in listaActividades) {
                listaArchivosViewModel.getArchivosByActividadId(actividad)
                reaccionViewModel.getReaccionByPublicacionIdYUsuarioUid(
                    actividad.id,
                    actividad,
                    usuarioUid
                )
            }
            Log.d("Lista", listaActividades.toString())
            listaActividadViewModel.listaActividadesAdapter.notifyDataSetChanged()
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_options, menu)
        val searchItem = menu.findItem(R.id.search)
        var searchView: SearchView? = null

        if (searchItem != null) {
            searchView = MenuItemCompat.getActionView(searchItem) as SearchView
        }

        if (searchView != null) {
            autocomplete = searchView.findViewById(androidx.appcompat.R.id.search_src_text)
            autocomplete.setDropDownBackgroundResource(R.color.colorWhite)
            autocomplete.threshold = 1

            autocomplete.setOnItemClickListener(this)
            //searchView.setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))

            searchView.setOnCloseListener(this)

            searchView.findViewById<AppCompatImageView>(R.id.search_close_btn).setOnClickListener {
                autocomplete.setText("")
                this.context!!.hideKeyboard(activity!!)
                listaActividadViewModel.getListaActividades(tipo = tipo)
            }


            searchView.isIconfiedByDefault
            searchView.requestFocus()
            searchView.setOnQueryTextFocusChangeListener(OnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    context?.showInputMethod(activity!!, view.findFocus())
                }
            })
        }
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onReaccion(
        reaccionNueva: Reaccion,
        reaccionVieja: Reaccion?,
        publicacion: Publicacion
    ) {
        if (reaccionVieja != null) {
            reaccionViewModel.eliminarReaccion(reaccionVieja, publicacion)
            if (reaccionNueva.tipoReaccion != reaccionVieja.tipoReaccion) reaccionViewModel.crearReaccion(
                reaccionNueva,
                publicacion
            )
            return
        }
        reaccionViewModel.crearReaccion(reaccionNueva, publicacion)
    }

    fun reaccionConsultada(reaccion: Reaccion?) {
        if (reaccion != null) {
            val listaActividades = listaActividadViewModel.listaActividades.value
            val index =
                listaActividades?.indexOfFirst { actividad -> actividad.id == reaccion.publicacionId }
            listaActividadViewModel.listaActividadesAdapter.notifyItemChanged(index!!)
        }
    }

    override fun onClose(): Boolean {
        this.context!!.hideKeyboard(activity!!)
        listaActividadViewModel.getListaActividades(tipo = tipo)
        return false
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val elementoSeleccionado: String = parent!!.getItemAtPosition(position) as String
        autocomplete.setText(elementoSeleccionado)
        listaActividadViewModel.getListaActividades(query = elementoSeleccionado, tipo = tipo)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item = parent!!.getItemAtPosition(position) as String
        when (item) {
            MAS_ANTIGUO -> listaActividadViewModel.getListaActividades(
                direccion = Query.Direction.ASCENDING,
                tipo = tipo
            )
            MAS_RECIENTE -> listaActividadViewModel.getListaActividades(
                direccion = Query.Direction.DESCENDING,
                tipo = tipo
            )
        }


    }
}
