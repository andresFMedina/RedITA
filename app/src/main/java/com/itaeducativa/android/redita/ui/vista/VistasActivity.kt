package com.itaeducativa.android.redita.ui.vista

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.data.modelos.Actividad
import com.itaeducativa.android.redita.databinding.ActivityVistasBinding
import com.itaeducativa.android.redita.network.RequestListener
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class VistasActivity : AppCompatActivity(), RequestListener, KodeinAware {
    override val kodein: Kodein by kodein()

    private val vistaFactory: ListaVistaViewModelFactory by instance()
    private lateinit var vistaViewModel: ListaVistaViewModel

    private lateinit var actividad: Actividad


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actividad = intent.extras!!.getSerializable("actividad") as Actividad

        val binding: ActivityVistasBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_vistas)

        vistaViewModel =
            ViewModelProviders.of(this, vistaFactory).get(ListaVistaViewModel::class.java)
        binding.viewModel = vistaViewModel

        vistaViewModel.requestListener = this
        vistaViewModel.getVistasPorParametro("actividadId", actividad.id)

        supportActionBar?.title = "Visto por"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onStartRequest() {

    }

    override fun onSuccessRequest() {

    }

    override fun onFailureRequest(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onStop() {
        vistaViewModel.requestListener = null
        super.onStop()
    }
}
