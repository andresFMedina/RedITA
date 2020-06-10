package com.itaeducativa.android.redita.ui.actividad.actividad.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.databinding.FragmentListaActividadesBinding
import com.itaeducativa.android.redita.network.RequestListener
import com.itaeducativa.android.redita.ui.actividad.actividad.viewmodels.ListaActividadesViewModel
import com.itaeducativa.android.redita.ui.actividad.actividad.viewmodels.ListaActividadesViewModelFactory
import kotlinx.android.synthetic.main.fragment_lista_actividades.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class ListaActividadesFragment : Fragment(), KodeinAware, RequestListener {

    override val kodein: Kodein by kodein()
    private val factory: ListaActividadesViewModelFactory by instance()

    private lateinit var viewModel: ListaActividadesViewModel

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentListaActividadesBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_lista_actividades, container, false
        )

        viewModel = ViewModelProviders.of(this, factory).get(ListaActividadesViewModel::class.java)

        binding.viewModel = viewModel
        viewModel.getListaActividades()

        viewModel.requestListener = this


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
        fun newInstance() =
            ListaActividadesFragment()
    }

    override fun onSuccessRequest() {
        progressBarListaActividades.visibility = View.GONE
    }

    override fun onFailureRequest(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onStartRequest() {
        progressBarListaActividades.visibility = View.VISIBLE
    }

    override fun onPause() {
        super.onPause()
        viewModel.requestListener = null
    }


}
