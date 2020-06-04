package com.itaeducativa.android.redita.ui.actividad.actividad.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.data.modelos.Actividad
import com.itaeducativa.android.redita.databinding.ActivityCrearActividadBinding
import com.itaeducativa.android.redita.network.RequestListener
import com.itaeducativa.android.redita.ui.actividad.actividad.viewmodels.ActividadViewModel
import com.itaeducativa.android.redita.ui.actividad.actividad.viewmodels.ListaActividadesViewModel
import com.itaeducativa.android.redita.ui.actividad.actividad.viewmodels.ListaActividadesViewModelFactory
import com.itaeducativa.android.redita.ui.login.AutenticacionViewModel
import com.itaeducativa.android.redita.ui.login.AutenticacionViewModelFactory
import kotlinx.android.synthetic.main.activity_crear_actividad.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.*

class CrearActividadActivity : AppCompatActivity(), RequestListener, KodeinAware {
    override val kodein: Kodein by kodein()
    private val autenticacionFactory: AutenticacionViewModelFactory by instance()
    private val actividadFactory: ListaActividadesViewModelFactory by instance()

    private lateinit var autenticacionViewModel: AutenticacionViewModel
    private lateinit var listaActividadesViewModel: ListaActividadesViewModel
    val actividadViewModel: ActividadViewModel = ActividadViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityCrearActividadBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_crear_actividad)

        autenticacionViewModel = ViewModelProviders.of(this, autenticacionFactory)
            .get(AutenticacionViewModel::class.java)
        listaActividadesViewModel =
            ViewModelProviders.of(this, actividadFactory).get(ListaActividadesViewModel::class.java)

        binding.viewModel = actividadViewModel

        listaActividadesViewModel.requestListener = this
    }

    override fun onStartRequest() {
        buttonCrearActividad.isEnabled = false
        buttonCrearActividad.text = getString(R.string.creando)
    }

    override fun onSuccessRequest() {
        buttonCrearActividad.isEnabled = true
        buttonCrearActividad.text = getText(R.string.crear_nueva_activivdad)
        Snackbar.make(layoutCrearActividad, "Se ha creado la actividad", Snackbar.LENGTH_SHORT).show()
    }

    override fun onFailureRequest(message: String) {

    }

    fun crearActividad(view: View) {
        val actividad = Actividad(
            nombre = actividadViewModel.nombre.value!!,
            descripcion = actividadViewModel.descripcion.value!!,
            tipoActividad = actividadViewModel.tipoActividad.value!!,
            fechaCreacionTimeStamp = Timestamp(Date()),
            meGusta = 0,
            noMeGusta = 0,
            comentarios = 0
        )
        actividad.autorUid = autenticacionViewModel.usuario!!.uid
        listaActividadesViewModel.guardarActividadEnFirestore(actividad)
    }
}
