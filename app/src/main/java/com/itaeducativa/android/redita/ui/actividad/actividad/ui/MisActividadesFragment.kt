package com.itaeducativa.android.redita.ui.actividad.actividad.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders

import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.data.modelos.Usuario
import com.itaeducativa.android.redita.databinding.FragmentMisActividadesBinding
import com.itaeducativa.android.redita.network.RequestListener
import com.itaeducativa.android.redita.ui.actividad.actividad.viewmodels.ListaActividadesViewModel
import com.itaeducativa.android.redita.ui.actividad.actividad.viewmodels.ListaActividadesViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.android.x.kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance


private const val ARG_USUARIO = "usuario"

class MisActividadesFragment : Fragment(), KodeinAware, RequestListener {
    // TODO: Rename and change types of parameters
    private var usuario: Usuario? = null
    override val kodein: Kodein by kodein()

    private val factory: ListaActividadesViewModelFactory by instance()
    private lateinit var viewModel: ListaActividadesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            usuario = it.getSerializable(ARG_USUARIO) as Usuario
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentMisActividadesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_mis_actividades, container, false)
        viewModel = ViewModelProviders.of(this, factory).get(ListaActividadesViewModel::class.java)

        binding.viewModel = viewModel
        viewModel.requestListener = this

        viewModel.getActividadesByAutorUid(usuario!!.uid)

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param usuario Parameter 1.
         * @return A new instance of fragment MisActividadesFragment.
         */
        // TODO: Rename and change types and number of parameters
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

    override fun onSuccessRequest() {

    }

    override fun onFailureRequest(message: String) {
        Log.e("error", message)
    }
}
