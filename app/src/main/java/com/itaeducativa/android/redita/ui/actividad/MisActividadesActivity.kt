package com.itaeducativa.android.redita.ui.actividad

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.databinding.ActivityMisActividadesBinding
import com.itaeducativa.android.redita.network.RequestListener
import com.itaeducativa.android.redita.util.startCrearActividadActivity
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class MisActividadesActivity : AppCompatActivity(), RequestListener, KodeinAware {
    override val kodein: Kodein by kodein()

    private val factory: ListaActividadesViewModelFactory by instance()
    private lateinit var viewModel: ListaActividadesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMisActividadesBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_mis_actividades)
        viewModel = ViewModelProviders.of(this, factory).get(ListaActividadesViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.requestListener = this

        viewModel.getListaActividades()

    }

    override fun onStartRequest() {

    }

    override fun onSuccessRequest() {

    }

    override fun onFailureRequest(message: String) {

    }

    fun goToCrearActivity(view: View) {
        this.startCrearActividadActivity()
    }
}
