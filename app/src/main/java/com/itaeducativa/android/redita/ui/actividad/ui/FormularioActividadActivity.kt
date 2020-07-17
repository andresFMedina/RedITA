package com.itaeducativa.android.redita.ui.actividad.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.Timestamp
import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.data.modelos.Actividad
import com.itaeducativa.android.redita.data.modelos.Archivo
import com.itaeducativa.android.redita.databinding.ActivityCrearActividadBinding
import com.itaeducativa.android.redita.network.RequestListener
import com.itaeducativa.android.redita.ui.actividad.viewmodels.ActividadViewModel
import com.itaeducativa.android.redita.ui.actividad.viewmodels.ListaActividadesViewModel
import com.itaeducativa.android.redita.ui.actividad.viewmodels.ListaActividadesViewModelFactory
import com.itaeducativa.android.redita.ui.archivo.ListaArchivoViewModel
import com.itaeducativa.android.redita.ui.archivo.ListaArchivoViewModelFactory
import com.itaeducativa.android.redita.ui.imagen.SeleccionarImagenesDialog
import com.itaeducativa.android.redita.ui.login.AutenticacionViewModel
import com.itaeducativa.android.redita.ui.login.AutenticacionViewModelFactory
import com.itaeducativa.android.redita.util.*
import kotlinx.android.synthetic.main.activity_crear_actividad.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

private const val ACTION_RESULT_GET_IMAGES = 0
private const val ACTION_RESULT_GET_VIDEO = 1
private const val ACTIVIDAD = "Actividad"
private const val RETO = "Reto"

class CrearActividadActivity : AppCompatActivity(), RequestListener, KodeinAware,
    SeleccionarImagenesDialog.DialogListener {
    override val kodein: Kodein by kodein()
    private val autenticacionFactory: AutenticacionViewModelFactory by instance()
    private val actividadFactory: ListaActividadesViewModelFactory by instance()
    private val listaArchivoViewModelFactory: ListaArchivoViewModelFactory by instance()

    private lateinit var autenticacionViewModel: AutenticacionViewModel
    private lateinit var listaActividadesViewModel: ListaActividadesViewModel
    private lateinit var listaArchivoViewModel: ListaArchivoViewModel

    private val actividadViewModel: ActividadViewModel = ActividadViewModel()
    private var imagenesUri = mutableListOf<Uri>()
    private var videoUri: Uri? = null

    private var actividad: Actividad? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actividad =
            if (intent.extras != null) intent.extras?.getSerializable("actividad") as Actividad else null

        val binding: ActivityCrearActividadBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_crear_actividad)

        autenticacionViewModel = ViewModelProviders.of(this, autenticacionFactory)
            .get(AutenticacionViewModel::class.java)
        listaActividadesViewModel =
            ViewModelProviders.of(this, actividadFactory).get(ListaActividadesViewModel::class.java)
        listaArchivoViewModel =
            ViewModelProviders.of(this, listaArchivoViewModelFactory)
                .get(ListaArchivoViewModel::class.java)


        binding.viewModel = actividadViewModel

        if (this.actividad != null) {
            actividadViewModel.bind(this.actividad!!)
        }

        listaActividadesViewModel.requestListener = this
        if (actividad == null) supportActionBar?.title = "Crear actividad"
        else supportActionBar?.title = actividad!!.nombre
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        inputCategoriaActividad.editText?.addTextChangedListener(
            TextWatcherValidacionVacio(
                inputCategoriaActividad
            )
        )

        inputNombreActividad.editText?.addTextChangedListener(
            TextWatcherValidacionVacio(
                inputNombreActividad
            )
        )

        inputDescripcionActividad.editText?.addTextChangedListener(
            TextWatcherValidacionVacio(
                inputDescripcionActividad
            )
        )

        inputTipoActividad.editText?.addTextChangedListener(
            TextWatcherValidacionVacio(
                inputTipoActividad
            )
        )

        inputFechaInicio.editText?.addTextChangedListener(
            TextWatcherValidacionVacio(
                inputFechaInicio
            )
        )

        inputHoraInicio.editText?.addTextChangedListener(
            TextWatcherValidacionVacio(
                inputHoraInicio
            )
        )

        inputFechaInicio.editText?.setOnClickListener {
            obtenerFecha(this, inputFechaInicio.editText!!)
        }
        inputHoraInicio.editText?.setOnClickListener {
            obtenerHora(this, inputHoraInicio.editText!!)
        }

        val items = listOf(ACTIVIDAD, RETO)
        val adapter = ArrayAdapter(this, R.layout.list_item, items)
        (inputCategoriaActividad.editText as? AutoCompleteTextView)?.setAdapter(adapter)
    }

    override fun onResume() {
        listaActividadesViewModel.requestListener = this
        super.onResume()
    }

    override fun onStartRequest() {
        buttonCrearActividad.isEnabled = false
        buttonCrearActividad.text = getString(R.string.creando)
    }

    override fun onSuccessRequest(response: Any?) {
        when(response) {
            is Actividad -> actividadCreada()
        }

    }

    fun actividadCreada(){
        buttonCrearActividad.isEnabled = true
        buttonCrearActividad.text = getText(R.string.crear_nueva_activivdad)
        var mensaje = "Se ha guardado la actividad."
        if(imagenesUri.isEmpty()) mensaje += " Las imagenes se están subiendo."
        showSnackbar(
            mensaje,
            coordinatorCrearActividad
        )
    }

    override fun onFailureRequest(message: String) {
        showSnackbar(message, coordinatorCrearActividad)
    }

    fun guardarActividad(view: View) {
        val textoCategoriaActividad = inputCategoriaActividad.editText!!.text.toString()
        val textoNombreActividad = inputNombreActividad.editText!!.text.toString()
        val textoDescripcionActividad = inputDescripcionActividad.editText!!.text.toString()
        val textoTipoActividad = inputTipoActividad.editText!!.text.toString()

        lateinit var actividad: Actividad

        if (textoCategoriaActividad.isEmpty()) {
            inputCategoriaActividad.error = getString(R.string.este_campo_no_puede_estar_vacio)
            return
        }

        if (textoNombreActividad.isEmpty()) {
            inputNombreActividad.error = getString(R.string.este_campo_no_puede_estar_vacio)
            return
        }

        if (textoDescripcionActividad.isEmpty()) {
            inputDescripcionActividad.error = getString(R.string.este_campo_no_puede_estar_vacio)
            return
        }

        if (textoTipoActividad.isEmpty()) {
            inputTipoActividad.error = getString(R.string.este_campo_no_puede_estar_vacio)
            return
        }
        if (this.actividad != null) {
            actividad = this.actividad!!
            actividad.categoria = actividadViewModel.categoria.value!!
            actividad.nombre = actividadViewModel.nombre.value!!
            actividad.descripcion = actividadViewModel.descripcion.value!!
            actividad.tipoActividad = actividadViewModel.tipoActividad.value!!
            actividad.horaInicio = actividadViewModel.horaInicio.value
            actividad.fechaInicio = actividadViewModel.fechaInicio.value
        } else {

            actividad = Actividad(
                categoria = actividadViewModel.categoria.value!!,
                nombre = actividadViewModel.nombre.value!!,
                descripcion = actividadViewModel.descripcion.value!!,
                id = "actividad${Timestamp.now().seconds}",
                tipoActividad = actividadViewModel.tipoActividad.value!!,
                fechaCreacionTimeStamp = Timestamp.now().seconds.toString(),
                horaInicio = actividadViewModel.horaInicio.value,
                fechaInicio = actividadViewModel.fechaInicio.value,
                estaActivo = true
            )
            actividad.autorUid = autenticacionViewModel.usuario!!.uid
        }
        actividad.autorUid = autenticacionViewModel.usuario!!.uid
        listaActividadesViewModel.guardarActividadEnFirestore(actividad)
        if (imagenesUri.isNotEmpty()) {
            for (imagen in imagenesUri) {
                val archivo = Archivo(
                    id = "archivo${System.currentTimeMillis()}",
                    actividadId = actividad.id,
                    timestamp = Timestamp.now().seconds.toString(),
                    url = "",
                    tipo = "imagen"
                )
                val ruta = "${System.currentTimeMillis()}.${getExtension(imagen, this)}"
                listaArchivoViewModel.guardarArchivoFirestore(archivo, ruta, imagen)

            }
        }
        if (videoUri != null) {
            val archivo = Archivo(
                id = "archivo${System.currentTimeMillis()}",
                actividadId = actividad.id,
                timestamp = Timestamp.now().seconds.toString(),
                url = "",
                tipo = "video"
            )
            val ruta = "${System.currentTimeMillis()}.${getExtension(videoUri!!, this)}"
            listaArchivoViewModel.guardarArchivoFirestore(archivo, ruta, videoUri!!)

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
                    var indexActual = 0
                    //val imagenesUri: MutableList<Uri> = mutableListOf()

                    while (indexActual < cantidadImagenes) {
                        val uriImagen: Uri = data.clipData!!.getItemAt(indexActual).uri
                        imagenesUri.add(uriImagen)

                        indexActual++
                    }
                    abrirSeleccionarImagenesDialog(imagenesUri)
                }
                if (data.data != null) {
                    imagenesUri.add(data.data!!)
                    abrirSeleccionarImagenesDialog(imagenesUri)
                    //textViewEstadoImagenes.text = getString(R.string.se_ha_agregado_una_imagen)
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

    private fun abrirSeleccionarImagenesDialog(imagenes: MutableList<Uri>) {
        val seleccionarImagenesDialog = SeleccionarImagenesDialog(imagenes)
        val ft = supportFragmentManager.beginTransaction()
        val prev = supportFragmentManager.findFragmentByTag("imagen_dialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        seleccionarImagenesDialog.show(ft, "imagen_dialog")

    }

    override fun onStop() {
        super.onStop()
        autenticacionViewModel.autenticacionListener = null
        listaActividadesViewModel.requestListener = null
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onFinishDialog(imagenes: MutableList<Uri>) {
        imagenesUri = imagenes
        val cantidadImagenes = imagenes.size
        val text =
            if (cantidadImagenes > 0)
                getString(R.string.se_han_agregado) + " " + cantidadImagenes + " imagenes"
            else getString(R.string.no_se_han_seleccionado_imagenes)
        textViewEstadoImagenes.text = text
    }
}
