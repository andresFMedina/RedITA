package com.itaeducativa.android.redita.ui.usuario

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.databinding.FragmentListaUsuariosBinding
import com.itaeducativa.android.redita.network.RequestListener
import com.itaeducativa.android.redita.util.showInputMethod
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class ListaUsuariosFragment : Fragment(), KodeinAware, RequestListener {
    override val kodein: Kodein by kodein()

    private val factory: ListaUsuarioViewModelFactory by instance()
    private lateinit var viewModel: ListaUsuarioViewModel


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

    override fun onSuccessRequest() {

    }

    override fun onFailureRequest(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    override fun onStop() {
        super.onStop()
        viewModel.requestListener = null
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
                    Log.i("onQueryTextChange", newText)
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
            searchView.setOnQueryTextFocusChangeListener(View.OnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    context?.showInputMethod(activity!!, view.findFocus())
                }
            })
        }
        super.onCreateOptionsMenu(menu, inflater)
    }
}