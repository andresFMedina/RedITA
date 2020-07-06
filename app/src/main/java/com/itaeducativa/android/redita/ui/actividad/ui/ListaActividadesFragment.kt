package com.itaeducativa.android.redita.ui.actividad.ui

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.OnFocusChangeListener
import android.widget.SearchView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.itaeducativa.android.redita.R
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
import com.itaeducativa.android.redita.util.showInputMethod
import kotlinx.android.synthetic.main.fragment_lista_actividades.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

private const val ARG_TIPO = "tipo"

class ListaActividadesFragment : Fragment(), KodeinAware, RequestListener, ReaccionListener {

    override val kodein: Kodein by kodein()
    private val factoryListaActividades: ListaActividadesViewModelFactory by instance()
    private val factoryListaArchivos: ListaArchivoViewModelFactory by instance()
    private val factoryReacion: ReaccionViewModelFactory by instance()
    private val factoryAutenticacion: AutenticacionViewModelFactory by instance()

    private lateinit var listaActividadViewModel: ListaActividadesViewModel
    private lateinit var listaArchivosViewModel: ListaArchivoViewModel
    private lateinit var reaccionViewModel: ReaccionViewModel
    private lateinit var autenticacionViewModel: AutenticacionViewModel

    private var seConsultaronArchivos = false
    private lateinit var tipo: String
    private lateinit var usuarioUid: String

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
        listaActividadViewModel.getListaActividades(
            tipo = tipo
        )

        listaActividadViewModel.requestListener = this
        listaArchivosViewModel.requestListener = this

        usuarioUid = autenticacionViewModel.usuario!!.uid

        return binding.root
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

    override fun onSuccessRequest() {
        progressBarListaActividades.visibility = View.GONE
        val listaActividades = listaActividadViewModel.listaActividades.value
        if (listaActividades != null && !seConsultaronArchivos) {
            for (actividad in listaActividades) {
                listaArchivosViewModel.getArchivosByActividadId(actividad)
                reaccionViewModel.getReaccionByPublicacionIdYUsuarioUid(
                    actividad.id,
                    actividad,
                    usuarioUid
                )
            }
            seConsultaronArchivos = true
            listaActividadViewModel.listaActividadesAdapter.notifyDataSetChanged()
        }
        Log.d("Lista", listaActividades.toString())
    }

    override fun onFailureRequest(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onStartRequest() {
        progressBarListaActividades.visibility = View.VISIBLE
    }

    override fun onPause() {
        super.onPause()
        listaActividadViewModel.requestListener = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_options, menu)
        val searchItem = menu.findItem(R.id.search)
        val searchManager: SearchManager =
            activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        var searchView: SearchView? = null
        val queryListener: SearchView.OnQueryTextListener

        if (searchItem != null) {
            searchView = searchItem.actionView as SearchView
        }

        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))

            queryListener = object : SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String): Boolean {
                    listaActividadViewModel.getListaActividades(
                        ordenCampo = "nombre",
                        query = newText,
                        tipo = tipo
                    )
                    return true
                }

                override fun onQueryTextSubmit(query: String): Boolean {
                    Log.i("onQueryTextSubmit", query)
                    return true
                }
            }
            searchView.setOnQueryTextListener(queryListener)
            searchView.isIconifiedByDefault = false
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
            reaccionViewModel.eliminarReaccion(reaccionVieja)
            if (reaccionNueva.tipoReaccion != reaccionVieja.tipoReaccion) reaccionViewModel.crearReaccion(
                reaccionNueva,
                publicacion
            )
            return
        }
        reaccionViewModel.crearReaccion(reaccionNueva, publicacion)
    }


}
