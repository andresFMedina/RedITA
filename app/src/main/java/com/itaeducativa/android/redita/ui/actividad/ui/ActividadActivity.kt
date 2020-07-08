package com.itaeducativa.android.redita.ui.actividad.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.Timestamp
import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.data.modelos.*
import com.itaeducativa.android.redita.databinding.ActivityActividadBinding
import com.itaeducativa.android.redita.network.RequestListener
import com.itaeducativa.android.redita.ui.VideoListener
import com.itaeducativa.android.redita.ui.actividad.viewmodels.*
import com.itaeducativa.android.redita.ui.archivo.ListaArchivoViewModel
import com.itaeducativa.android.redita.ui.archivo.ListaArchivoViewModelFactory
import com.itaeducativa.android.redita.ui.comentario.viewmodels.ListaComentariosViewModel
import com.itaeducativa.android.redita.ui.comentario.viewmodels.ListaComentariosViewModelFactory
import com.itaeducativa.android.redita.ui.login.AutenticacionViewModel
import com.itaeducativa.android.redita.ui.login.AutenticacionViewModelFactory
import com.itaeducativa.android.redita.ui.reaccion.ReaccionViewModel
import com.itaeducativa.android.redita.ui.reaccion.ReaccionViewModelFactory
import com.itaeducativa.android.redita.ui.vista.ListaVistaViewModel
import com.itaeducativa.android.redita.ui.vista.ListaVistaViewModelFactory
import com.itaeducativa.android.redita.util.hideKeyboard
import com.itaeducativa.android.redita.util.startListaArchivosActivity
import com.itaeducativa.android.redita.util.startVistasActivity
import kotlinx.android.synthetic.main.activity_actividad.*
import kotlinx.android.synthetic.main.linearlayout_reacciones.view.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class ActividadActivity : AppCompatActivity(), RequestListener, VideoListener, KodeinAware {
    override val kodein: Kodein by kodein()
    private val factory: ListaComentariosViewModelFactory by instance()
    private val autenticacionFactory: AutenticacionViewModelFactory by instance()
    private val storageViewModelFactory: StorageViewModelFactory by instance()
    private val vistaFactory: ListaVistaViewModelFactory by instance()
    private val listaActividadesViewModelFactory: ListaActividadesViewModelFactory by instance()
    private val listaArchivoViewModelFactory: ListaArchivoViewModelFactory by instance()
    private val reaccionViewModelFactory: ReaccionViewModelFactory by instance()

    private lateinit var actividad: Actividad
    private var vista: Vista? = null

    private lateinit var storageViewModel: StorageViewModel
    private lateinit var autenticacionViewModel: AutenticacionViewModel
    private lateinit var vistaViewModel: ListaVistaViewModel
    private lateinit var listaActividadesViewModel: ListaActividadesViewModel
    private lateinit var listaArchivoViewModel: ListaArchivoViewModel
    private lateinit var reaccionViewModel: ReaccionViewModel

    private lateinit var viewModelComentario: ListaComentariosViewModel
    private var esAutor: Boolean = false
    private var yaVisto: Boolean = false

    private lateinit var imageMeGusta: ImageButton
    private lateinit var imageNoMeGusta: ImageButton


    private var textoComentario: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actividad = if (intent.extras != null) {
            intent.extras?.getSerializable("actividad") as Actividad
        } else {
            savedInstanceState!!.getSerializable("actividad") as Actividad
        }

        val binding: ActivityActividadBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_actividad)

        val viewModelActividad = ViewModelProviders.of(this).get(ActividadViewModel::class.java)
        viewModelActividad.bind(actividad)

        setupViewModels()

        imageMeGusta = layoutReacciones.imageButtonMeGusta
        imageNoMeGusta = layoutReacciones.imageButtonNoMeGusta

        //if (actividad.video != null) storageViewModel.getVideoUri(actividad.video!!)
        videoActividad.visibility = View.GONE

        binding.viewModelActividad = viewModelActividad
        binding.viewModelComentario = viewModelComentario
        binding.textoComentario = textoComentario
        binding.viewModelArchivo = listaArchivoViewModel

        if (!actividad.archivos.isNullOrEmpty())
            listaArchivoViewModel.listaArchivoAdapter.actualizarArchivos(actividad.archivos!!)

        esAutor = autenticacionViewModel.usuario!!.uid == actividad.autorUid

        if (esAutor) {
            vistaViewModel.requestListener = null
            yaVisto = true
        } else {
            vistaViewModel.requestListener = this
        }
        listaActividadesViewModel.requestListener = this

        viewModelComentario.requestListener = this
        reaccionViewModel.requestListener = this
        storageViewModel.videoListener = this



        editTextComentario.setEndIconOnClickListener {
            val uid: String = autenticacionViewModel.usuario!!.uid
            textoComentario = inputComentario.text.toString().trim()
            if (textoComentario.isEmpty()) {
                inputComentario.error = getString(R.string.debes_escribir_un_comentario)
                return@setEndIconOnClickListener
            }
            val comentario = Comentario(
                textoComentario,
                Timestamp.now().seconds.toString(),
                uid,
                actividad.id,
                "actividades"
            )
            viewModelComentario.agregarComentariosEnFirestorePorPublicacion(comentario, actividad)
            hideKeyboard(this)
            inputComentario.setText("")
        }


        if (!esAutor)
            vistaViewModel.getVistasByUsuarioYActividad(
                autenticacionViewModel.usuario!!.uid,
                actividad.fechaCreacionTimeStamp
            )
        else viewModelComentario.getComentariosEnFirestorePorPublicacion(actividad.id)


        supportActionBar?.title = actividad.nombre
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        reaccionViewModel.getReaccionByPublicacionIdYUsuarioUid(
            actividad.id,
            actividad,
            autenticacionViewModel.usuario!!.uid
        )

        imageMeGusta.setOnClickListener {
            val reaccion = Reaccion(
                timestamp = Timestamp.now().seconds.toString(),
                tipoPublicacion = "actividades",
                usuarioUid = autenticacionViewModel.usuario!!.uid,
                tipoReaccion = "meGusta",
                publicacionId = actividad.id

            )
            onReaccion(reaccion, actividad.reaccion, actividad)

        }
        imageNoMeGusta.setOnClickListener {
            val reaccion = Reaccion(
                timestamp = Timestamp.now().seconds.toString(),
                tipoPublicacion = "actividades",
                usuarioUid = autenticacionViewModel.usuario!!.uid,
                tipoReaccion = "noMeGusta",
                publicacionId = actividad.id

            )
            onReaccion(reaccion, actividad.reaccion, actividad)

        }

    }

    fun onReaccion(
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


    fun setupViewModels() {
        viewModelComentario =
            ViewModelProviders.of(this, factory).get(ListaComentariosViewModel::class.java)

        autenticacionViewModel = ViewModelProviders.of(this, autenticacionFactory)
            .get(AutenticacionViewModel::class.java)

        storageViewModel =
            ViewModelProviders.of(this, storageViewModelFactory).get(StorageViewModel::class.java)

        vistaViewModel =
            ViewModelProviders.of(this, vistaFactory).get(ListaVistaViewModel::class.java)

        listaActividadesViewModel = ViewModelProviders.of(this, listaActividadesViewModelFactory)
            .get(ListaActividadesViewModel::class.java)

        listaArchivoViewModel = ViewModelProviders.of(this, listaArchivoViewModelFactory)
            .get(ListaArchivoViewModel::class.java)

        reaccionViewModel =
            ViewModelProviders.of(this, reaccionViewModelFactory).get(ReaccionViewModel::class.java)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onStartRequest() {

    }

    override fun onSuccessRequest() {
        if (!yaVisto) {
            vista = vistaViewModel.vista.value
            if (vista == null) {
                vista = Vista(
                    usuarioUid = autenticacionViewModel.usuario!!.uid,
                    actividadId = actividad.fechaCreacionTimeStamp,
                    timestamp = Timestamp.now().seconds.toString(),
                    vecesVisto = 1
                )
                vistaViewModel.guardarVistaEnFirestore(vista!!)
                viewModelComentario.getComentariosEnFirestorePorPublicacion(actividad.id)
                yaVisto = true
                return
            }

            vistaViewModel.agregarVista(vista!!)
            viewModelComentario.getComentariosEnFirestorePorPublicacion(actividad.id)
            yaVisto = true
        }
        imageMeGusta.setImageResource(R.drawable.ic_thumb_up_black_24dp)
        imageNoMeGusta.setImageResource(R.drawable.ic_thumb_down_black_24dp)
        if (actividad.reaccion != null) {
            when (actividad.reaccion!!.tipoReaccion) {
                "meGusta" -> imageMeGusta.setImageResource(R.drawable.ic_thumb_up_black_filled_24dp)
                "noMeGusta" -> imageNoMeGusta.setImageResource(R.drawable.ic_thumb_down_black_filled_24dp)
            }
        }
    }


    override fun onFailureRequest(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        Log.e("Error query", message)
    }

    override fun onStartVideo() {

    }

    override fun onSuccessVideo() {
        val mediaController = MediaController(this)
        mediaController.setMediaPlayer(videoActividad)
        videoActividad.setMediaController(mediaController)
        videoActividad.setVideoURI(storageViewModel.uri.value!!)
    }

    override fun onFailureVideo(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onStop() {
        super.onStop()
        autenticacionViewModel.autenticacionListener = null
        storageViewModel.videoListener = null
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        actividad = savedInstanceState.getSerializable("actividad") as Actividad
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("actividad", actividad)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_docente_actividad, menu!!)
        return esAutor
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.vistas -> {
            this.startVistasActivity(actividad)
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    fun verImagenes(view: View) {
        this.startListaArchivosActivity(actividad)
    }


}
