package com.itaeducativa.android.redita.ui.actividad.actividad.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
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
import com.itaeducativa.android.redita.util.*
import kotlinx.android.synthetic.main.activity_crear_actividad.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.*

private const val ACTION_RESULT_GET_IMAGES = 0
private const val ACTION_RESULT_GET_VIDEO = 1

class CrearActividadActivity : AppCompatActivity(), RequestListener, KodeinAware {
    override val kodein: Kodein by kodein()
    private val autenticacionFactory: AutenticacionViewModelFactory by instance()
    private val actividadFactory: ListaActividadesViewModelFactory by instance()

    private lateinit var autenticacionViewModel: AutenticacionViewModel
    private lateinit var listaActividadesViewModel: ListaActividadesViewModel
    private val actividadViewModel: ActividadViewModel = ActividadViewModel()
    private val imagenesUri = mutableListOf<Uri>()
    private var videoUri: Uri? = null

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
        supportActionBar?.title = "Crear actividad"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onStartRequest() {
        buttonCrearActividad.isEnabled = false
        buttonCrearActividad.text = getString(R.string.creando)
    }

    override fun onSuccessRequest() {
        buttonCrearActividad.isEnabled = true
        buttonCrearActividad.text = getText(R.string.crear_nueva_activivdad)
        showSnackbar(
            "Se ha creado la actividad, las imagenes se están subiendo",
            coordinatorCrearActividad
        )
    }

    override fun onFailureRequest(message: String) {
        showSnackbar(message, coordinatorCrearActividad)
    }

    fun crearActividad(view: View) {
        val actividad = Actividad(
            nombre = actividadViewModel.nombre.value!!,
            descripcion = actividadViewModel.descripcion.value!!,
            tipoActividad = actividadViewModel.tipoActividad.value!!,
            fechaCreacionTimeStamp = Timestamp(Date()).seconds.toString(),
            meGusta = 0,
            noMeGusta = 0,
            comentarios = 0
        )
        actividad.autorUid = autenticacionViewModel.usuario!!.uid
        listaActividadesViewModel.guardarActividadEnFirestore(actividad)
        if(imagenesUri.isNotEmpty()){
            for (imagen in imagenesUri) {
                val ruta = "${System.currentTimeMillis()}.${getExtension(imagen, this)}"
                listaActividadesViewModel.agregarImagenesAActividad(
                    actividad.fechaCreacionTimeStamp,
                    ruta,
                    imagen
                )
            }
        }
        if(videoUri != null) {
            val ruta = "${System.currentTimeMillis()}.${getExtension(videoUri!!, this)}"
            listaActividadesViewModel.agregarVideoAActividad(
                actividad.fechaCreacionTimeStamp,
                ruta,
                videoUri!!
            )
        }
    }

    fun elegirImagenes(view: View) {
        this.multipleFileChooser(this)
    }

    fun elegirVideo(view: View) {
        this.videoChooser(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ACTION_RESULT_GET_IMAGES && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                if (data.clipData != null) {
                    val cantidadImagenes = data.clipData!!.itemCount
                    textViewEstadoImagenes.text =
                        getString(R.string.se_han_agregado) + " " + cantidadImagenes + " imagenes"

                    var indexActual = 0

                    while (indexActual < cantidadImagenes) {
                        val uriImagen: Uri = data.clipData!!.getItemAt(indexActual).uri
                        imagenesUri.add(uriImagen)

                        indexActual++
                    }
                }
                if (data.data != null) {
                    imagenesUri.add(data.data!!)
                    textViewEstadoImagenes.text = getString(R.string.se_ha_agregado_una_imagen)
                }
            }
        }

        if (requestCode == ACTION_RESULT_GET_VIDEO && resultCode == Activity.RESULT_OK) {
            if (data != null && data.data != null) {
                videoUri = data.data!!
                textViewEstadoVideo.text = getString(R.string.se_ha_agregado_un_video)
            }
        }
    }
}
