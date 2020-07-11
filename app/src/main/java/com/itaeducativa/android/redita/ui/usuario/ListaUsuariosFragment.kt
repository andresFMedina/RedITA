package com.itaeducativa.android.redita.ui.usuario

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.data.modelos.Actividad
import com.itaeducativa.android.redita.data.modelos.Reaccion
import com.itaeducativa.android.redita.databinding.FragmentListaUsuariosBinding
import com.itaeducativa.android.redita.network.RequestListener
import com.itaeducativa.android.redita.util.hideKeyboard
import com.itaeducativa.android.redita.util.showInputMethod
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class ListaUsuariosFragment : Fragment(), KodeinAware, RequestListener,
    AdapterView.OnItemClickListener, SearchView.OnCloseListener {
    override val kodein: Kodein by kodein()

    private val factory: ListaUsuarioViewModelFactory by instance()
    private lateinit var viewModel: ListaUsuarioViewModel

    private lateinit var autocomplete: SearchView.SearchAutoComplete

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentListaUsuariosBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_lista_usuarios,
                container,
                false
            )
        viewModel = ViewModelProviders.of(this, factory).get(ListaUsuarioViewModel::class.java)
        viewModel.requestListener = this
        binding.viewModel = viewModel

        viewModel.getUsuarios()

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment ListaUsuariosFragment.
         */
        @JvmStatic
        fun newInstance() =
            ListaUsuariosFragment()

    }

    override fun onStartRequest() {

    }

    override fun onSuccessRequest(response: Any?) {
        when (response) {
            is List<*> -> {
                if (response.isNotEmpty()) {
                    when (response[0]) {
                        is String -> autocomplete.setAdapter(
                            viewModel.nombresUsuariosAdapter
                        )
                    }
                }
            }
        }
    }

    override fun onFailureRequest(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        Log.e("Error", message)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getNombresUsuarios(context!!)
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

            searchView.setOnCloseListener(this)

            searchView.findViewById<AppCompatImageView>(R.id.search_close_btn).setOnClickListener {
                autocomplete.setText("")
                this.context!!.hideKeyboard(activity!!)
                viewModel.getUsuarios()
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
        viewModel.getUsuarios(query = elementoSeleccionado)
    }

    override fun onClose(): Boolean {
        this.context!!.hideKeyboard(activity!!)
        viewModel.getUsuarios()
        return true
    }
}