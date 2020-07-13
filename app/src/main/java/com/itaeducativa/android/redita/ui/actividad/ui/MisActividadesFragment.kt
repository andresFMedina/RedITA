package com.itaeducativa.android.redita.ui.actividad.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.data.modelos.Actividad
import com.itaeducativa.android.redita.data.modelos.Usuario
import com.itaeducativa.android.redita.databinding.FragmentMisActividadesBinding
import com.itaeducativa.android.redita.network.RequestListener
import com.itaeducativa.android.redita.ui.actividad.viewmodels.ListaActividadesViewModel
import com.itaeducativa.android.redita.ui.actividad.viewmodels.ListaActividadesViewModelFactory
import com.itaeducativa.android.redita.util.hideKeyboard
import com.itaeducativa.android.redita.util.showInputMethod
import com.itaeducativa.android.redita.util.startFormularioActividadActivity
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


private const val ARG_USUARIO = "usuario"

class MisActividadesFragment : Fragment(), KodeinAware, RequestListener,
    AdapterView.OnItemClickListener {
    private var usuario: Usuario? = null
    override val kodein: Kodein by kodein()

    private val factory: ListaActividadesViewModelFactory by instance()
    private lateinit var viewModel: ListaActividadesViewModel

    private lateinit var autocomplete: SearchView.SearchAutoComplete
    private lateinit var recyclerViewMisActividades: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            usuario = it.getSerializable(ARG_USUARIO) as Usuario
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentMisActividadesBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_mis_actividades, container, false)
        viewModel = ViewModelProviders.of(this, factory).get(ListaActividadesViewModel::class.java)

        binding.viewModel = viewModel
        viewModel.getActividadesByAutorUid(usuario!!.uid)

        recyclerViewMisActividades = binding.recyclerViewMisActividades

        viewModel.requestListener = this
        return binding.root
    }

    fun goToCrearActivity(view: View) {
        context!!.startFormularioActividadActivity(null)
    }

    override fun onResume() {
        super.onResume()
        viewModel.requestListener = this
        viewModel.getNombresActividadesByUidAutor(context!!, usuario!!.uid)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param usuario Parameter 1.
         * @return A new instance of fragment MisActividadesFragment.
         */
        @JvmStatic
        fun newInstance(usuario: Usuario) =
            MisActividadesFragment()
                .apply {
                    arguments = Bundle().apply {
                        putSerializable(ARG_USUARIO, usuario)
                    }
                }
    }

    override fun onStartRequest() {

    }

    override fun onSuccessRequest(response: Any?) {
        when (response) {
            is List<*> -> {
                if (response.isNotEmpty()) {
                    when (response[0]) {
                        is String -> autocomplete.setAdapter(
                            viewModel.nombresActividadesAdapter
                        )
                        is Actividad -> {
                            recyclerViewMisActividades.addOnScrollListener(viewModel.onScrollListener)
                            Log.d("Lista General", viewModel.listaActividades.value?.size.toString())
                        }
                    }
                }
            }

        }

    }

    override fun onFailureRequest(message: String) {
        Log.e("error", message)
    }

    override fun onStop() {
        super.onStop()
        viewModel.requestListener = null
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

            searchView.findViewById<AppCompatImageView>(R.id.search_close_btn).setOnClickListener {
                autocomplete.setText("")
                this.context!!.hideKeyboard(activity!!)
                viewModel.getActividadesByAutorUid(usuario!!.uid)
            }


            searchView.isIconfiedByDefault
            searchView.requestFocus()
            searchView.setOnQueryTextFocusChangeListener(View.OnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    context?.showInputMethod(activity!!, view.findFocus())
                }
            })
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val elementoSeleccionado: String = parent!!.getItemAtPosition(position) as String
        autocomplete.setText(elementoSeleccionado)
        viewModel.getActividadesByAutorUid(query = elementoSeleccionado, uid = usuario!!.uid)
    }
}
