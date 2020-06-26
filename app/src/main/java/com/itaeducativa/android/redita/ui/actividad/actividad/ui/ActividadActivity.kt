package com.itaeducativa.android.redita.ui.actividad.actividad.ui

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
import com.itaeducativa.android.redita.data.modelos.Actividad
import com.itaeducativa.android.redita.data.modelos.Comentario
import com.itaeducativa.android.redita.data.modelos.Reaccion
import com.itaeducativa.android.redita.data.modelos.Vista
import com.itaeducativa.android.redita.databinding.ActivityActividadBinding
import com.itaeducativa.android.redita.network.RequestListener
import com.itaeducativa.android.redita.ui.VideoListener
import com.itaeducativa.android.redita.ui.actividad.actividad.viewmodels.*
import com.itaeducativa.android.redita.ui.actividad.comentario.viewmodels.ListaComentariosViewModel
import com.itaeducativa.android.redita.ui.actividad.comentario.viewmodels.ListaComentariosViewModelFactory
import com.itaeducativa.android.redita.ui.login.AutenticacionViewModel
import com.itaeducativa.android.redita.ui.login.AutenticacionViewModelFactory
import com.itaeducativa.android.redita.ui.vista.ListaVistaViewModel
import com.itaeducativa.android.redita.ui.vista.ListaVistaViewModelFactory
import com.itaeducativa.android.redita.util.hideKeyboard
import com.itaeducativa.android.redita.util.startVistasActivity
import kotlinx.android.synthetic.main.activity_actividad.*
import kotlinx.android.synthetic.main.linearlayout_reacciones.view.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.*

class ActividadActivity : AppCompatActivity(), RequestListener, VideoListener, KodeinAware {
    override val kodein: Kodein by kodein()
    private val factory: ListaComentariosViewModelFactory by instance()
    private val autenticacionFactory: AutenticacionViewModelFactory by instance()
    private val storageViewModelFactory: StorageViewModelFactory by instance()
    private val vistaFactory: ListaVistaViewModelFactory by instance()
    private val listaActividadesViewModelFactory: ListaActividadesViewModelFactory by instance()

    private lateinit var actividad: Actividad
    private var vista: Vista? = null
    private var reaccion: Reaccion? = null

    private lateinit var storageViewModel: StorageViewModel
    private lateinit var autenticacionViewModel: AutenticacionViewModel
    private lateinit var vistaViewModel: ListaVistaViewModel
    private lateinit var listaActividadesViewModel: ListaActividadesViewModel

    private lateinit var viewModelComentario: ListaComentariosViewModel
    private var esAutor: Boolean = false
    private var yaVisto: Boolean = false


    private var textoComentario: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actividad = if (intent.extras != null) {
            intent.extras?.getSerializable("actividad") as Actividad
        } else {
            savedInstanceState!!.getSerializable("actividad") as Actividad
        }

        reaccion = if (intent.extras != null) {
            intent.extras?.getSerializable("reaccion") as Reaccion?
        } else {
            savedInstanceState!!.getSerializable("reaccion") as Reaccion?
        }

        val binding: ActivityActividadBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_actividad)

        val viewModelActividad = ViewModelProviders.of(this).get(ActividadViewModel::class.java)
        viewModelActividad.bind(actividad)

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

        if (actividad.video != null) storageViewModel.getVideoUri(actividad.video!!)
        else videoActividad.visibility = View.GONE

        binding.viewModelActividad = viewModelActividad
        binding.viewModelComentario = viewModelComentario
        binding.textoComentario = textoComentario

        vistaViewModel.requestListener = this

        viewModelComentario.requestListener = this
        storageViewModel.videoListener = this



        editTextComentario.setEndIconOnClickListener {
            val uid: String = autenticacionViewModel.usuario!!.uid
            val timestamp = actividad.fechaCreacionTimeStamp
            textoComentario = inputComentario.text.toString().trim()
            if (textoComentario.isEmpty()) {
                inputComentario.error = getString(R.string.debes_escribir_un_comentario)
                return@setEndIconOnClickListener
            }
            val comentario = Comentario(textoComentario, Timestamp(Date()), uid, timestamp)
            viewModelComentario.agregarComentariosEnFirestorePorActividad(comentario)
            hideKeyboard(this)
            inputComentario.setText("")
        }
        esAutor = autenticacionViewModel.usuario!!.uid == actividad.autorUid

        if (!esAutor)
            vistaViewModel.getVistasByUsuarioYActividad(
                autenticacionViewModel.usuario!!.uid,
                actividad.fechaCreacionTimeStamp
            )
        else viewModelComentario.getComentariosEnFirestorePorActividad(actividad.fechaCreacionTimeStamp)


        supportActionBar?.title = actividad.nombre
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val imageMeGusta = layoutReacciones.imageButtonMeGusta
        val imageNoMeGusta = layoutReacciones.imageButtonNoMeGusta

        if (reaccion != null) {
            when (reaccion!!.tipoReaccion) {
                "meGusta" -> imageMeGusta.setImageResource(R.drawable.ic_thumb_up_black_filled_24dp)
                "noMeGusta" -> imageNoMeGusta.setImageResource(R.drawable.ic_thumb_down_black_filled_24dp)
            }
        }

        imageMeGusta.setOnClickListener {
            reaccion = imageButtonsEvent(
                imageButton = imageMeGusta,
                iconoVacio = R.drawable.ic_thumb_up_black_24dp,
                iconoLleno = R.drawable.ic_thumb_up_black_filled_24dp,
                imageButtonReaccionDiferente = imageNoMeGusta,
                iconoVacioDiferente = R.drawable.ic_thumb_down_black_24dp,
                tipoReaccion = "meGusta"
            )
        }
        imageNoMeGusta.setOnClickListener {
            reaccion = imageButtonsEvent(
                imageButton = imageNoMeGusta,
                iconoVacio = R.drawable.ic_thumb_down_black_24dp,
                iconoLleno = R.drawable.ic_thumb_down_black_filled_24dp,
                imageButtonReaccionDiferente = imageMeGusta,
                iconoVacioDiferente = R.drawable.ic_thumb_up_black_24dp,
                tipoReaccion = "noMeGusta"
            )
        }

    }

    private fun objetoReaccion(tipoReaccion: String, actividadId: String): Reaccion =
        Reaccion(
            usuarioUid = autenticacionViewModel.usuario!!.uid,
            tipoReaccion = tipoReaccion,
            actividadId = actividadId,
            timestamp = Timestamp(Date()).seconds.toString()
        )

    private fun imageButtonsEvent(
        imageButton: ImageButton,
        iconoVacio: Int,
        iconoLleno: Int,
        imageButtonReaccionDiferente: ImageButton,
        iconoVacioDiferente: Int,
        tipoReaccion: String
    ): Reaccion? {
        imageButtonReaccionDiferente.setImageResource(iconoVacioDiferente)
        if (reaccion == null) {
            val r: Reaccion =
                objetoReaccion(tipoReaccion, actividad.fechaCreacionTimeStamp)

            listaActividadesViewModel.crearReaccion(r)
            imageButton.setImageResource(iconoLleno)

            return r
        } else {
            listaActividadesViewModel.eliminarReaccion(reaccion!!)
            imageButton.setImageResource(iconoVacio)
            if (tipoReaccion != reaccion!!.tipoReaccion) {
                val r: Reaccion =
                    objetoReaccion(tipoReaccion, actividad.fechaCreacionTimeStamp)

                listaActividadesViewModel.crearReaccion(r)
                imageButton.setImageResource(iconoLleno)
                return r
            }

            return null
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onStartRequest() {

    }

    override fun onSuccessRequest() {
        vista = vistaViewModel.vista.value
        if (!yaVisto) {
            if (vista == null) {
                vista = Vista(
                    usuarioUid = autenticacionViewModel.usuario!!.uid,
                    actividadId = actividad.fechaCreacionTimeStamp,
                    timestamp = Timestamp.now().seconds.toString(),
                    vecesVisto = 1
                )
                vistaViewModel.guardarVistaEnFirestore(vista!!)
                viewModelComentario.getComentariosEnFirestorePorActividad(actividad.fechaCreacionTimeStamp)
                yaVisto = true
                return
            }

            vistaViewModel.agregarVista(vista!!)
            viewModelComentario.getComentariosEnFirestorePorActividad(actividad.fechaCreacionTimeStamp)
            yaVisto = true
        }
    }


    override fun onFailureRequest(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        Log.e("Error query", message)
    }

    override fun onStartVideo() {
        Log.d("Poniendo video", actividad.video!!)
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


}
