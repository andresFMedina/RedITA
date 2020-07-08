package com.itaeducativa.android.redita.ui.archivo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.data.modelos.Actividad
import com.itaeducativa.android.redita.data.modelos.Publicacion
import com.itaeducativa.android.redita.data.modelos.Reaccion
import com.itaeducativa.android.redita.databinding.ActivityListaArchivosBinding
import com.itaeducativa.android.redita.network.RequestListener
import com.itaeducativa.android.redita.ui.login.AutenticacionViewModel
import com.itaeducativa.android.redita.ui.login.AutenticacionViewModelFactory
import com.itaeducativa.android.redita.ui.reaccion.ReaccionListener
import com.itaeducativa.android.redita.ui.reaccion.ReaccionViewModel
import com.itaeducativa.android.redita.ui.reaccion.ReaccionViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class ListaArchivosActivity : AppCompatActivity(), KodeinAware, RequestListener, ReaccionListener {
    override val kodein: Kodein by kodein()

    private val listaArchivoViewModelFactory: ListaArchivoViewModelFactory by instance()
    private val reaccionViewModelFactory: ReaccionViewModelFactory by instance()
    private val autenticacionViewModelFactory: AutenticacionViewModelFactory by instance()

    private lateinit var listaArchivoViewModel: ListaArchivoViewModel
    private lateinit var reaccionViewModel: ReaccionViewModel
    private lateinit var autenticacionViewModel: AutenticacionViewModel

    private lateinit var actividad: Actividad

    private var seConsultoReaccion: Boolean = false


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

        reaccionViewModel =
            ViewModelProviders.of(this, reaccionViewModelFactory)
                .get(ReaccionViewModel::class.java)

        autenticacionViewModel = ViewModelProviders.of(this, autenticacionViewModelFactory)
            .get(AutenticacionViewModel::class.java)

        listaArchivoViewModel.requestListener = this
        reaccionViewModel.requestListener = this

        binding.archivoViewModel = listaArchivoViewModel

        listaArchivoViewModel.getArchivosByActividadId(actividad)

        listaArchivoViewModel.listaArchivoAdapter.reaccionListener = this

        supportActionBar?.title = actividad.nombre
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onStartRequest() {

    }

    override fun onSuccessRequest() {
        val listaArchivos = listaArchivoViewModel.listaArchivos.value
        if (listaArchivos != null && !seConsultoReaccion) {
            for (actividad in listaArchivos) {
                reaccionViewModel.getReaccionByPublicacionIdYUsuarioUid(
                    actividad.id,
                    actividad,
                    autenticacionViewModel.usuario!!.uid
                )
            }
            seConsultoReaccion = true
            listaArchivoViewModel.listaArchivoAdapter.notifyDataSetChanged()
        }
        Log.d("Lista", listaArchivos.toString())
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
}