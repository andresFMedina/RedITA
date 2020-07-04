package com.itaeducativa.android.redita.ui.archivo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.data.modelos.Actividad
import com.itaeducativa.android.redita.databinding.ActivityListaArchivosBinding
import com.itaeducativa.android.redita.network.RequestListener
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class ListaArchivosActivity : AppCompatActivity(), KodeinAware, RequestListener {
    override val kodein: Kodein by kodein()

    private val listaArchivoViewModelFactory: ListaArchivoViewModelFactory by instance()
    private lateinit var listaArchivoViewModel: ListaArchivoViewModel

    private lateinit var actividad: Actividad


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actividad = if (intent.extras != null) {
            intent.extras?.getSerializable("actividad") as Actividad
        } else {
            savedInstanceState!!.getSerializable("actividad") as Actividad
        }

        val binding: ActivityListaArchivosBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_lista_archivos)

        listaArchivoViewModel = ViewModelProviders.of(this, listaArchivoViewModelFactory)
            .get(ListaArchivoViewModel::class.java)

        listaArchivoViewModel.requestListener = this

        binding.archivoViewModel = listaArchivoViewModel

        listaArchivoViewModel.getArchivosByActividadId(actividad)

        supportActionBar?.title = actividad.nombre
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onStartRequest() {

    }

    override fun onSuccessRequest() {

    }

    override fun onFailureRequest(message: String) {
        Log.e("Error", message)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onStop() {
        super.onStop()
        listaArchivoViewModel.requestListener = null
    }
}