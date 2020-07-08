package com.itaeducativa.android.redita.ui.archivo

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.Timestamp
import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.data.modelos.Archivo
import com.itaeducativa.android.redita.data.modelos.Comentario
import com.itaeducativa.android.redita.data.modelos.Publicacion
import com.itaeducativa.android.redita.data.modelos.Reaccion
import com.itaeducativa.android.redita.databinding.ActivityArchivoDetalladoBinding
import com.itaeducativa.android.redita.network.RequestListener
import com.itaeducativa.android.redita.ui.comentario.viewmodels.ListaComentariosViewModel
import com.itaeducativa.android.redita.ui.comentario.viewmodels.ListaComentariosViewModelFactory
import com.itaeducativa.android.redita.ui.login.AutenticacionViewModel
import com.itaeducativa.android.redita.ui.login.AutenticacionViewModelFactory
import com.itaeducativa.android.redita.ui.reaccion.ReaccionViewModel
import com.itaeducativa.android.redita.ui.reaccion.ReaccionViewModelFactory
import com.itaeducativa.android.redita.util.hideKeyboard
import kotlinx.android.synthetic.main.activity_actividad.*
import kotlinx.android.synthetic.main.activity_archivo_detallado.*
import kotlinx.android.synthetic.main.cardview_archivos.view.*
import kotlinx.android.synthetic.main.linearlayout_reacciones.view.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class ArchivoDetalladoActivity : AppCompatActivity(), KodeinAware, RequestListener {
    override val kodein: Kodein by kodein()

    private val factoryComentarios: ListaComentariosViewModelFactory by instance()
    private val factoryAutenticacion: AutenticacionViewModelFactory by instance()
    private val factoryReaccion: ReaccionViewModelFactory by instance()

    private lateinit var archivoViewModel: ArchivoViewModel
    private lateinit var listaComentariosViewModel: ListaComentariosViewModel
    private lateinit var autenticacionViewModel: AutenticacionViewModel
    private lateinit var reaccionViewModel: ReaccionViewModel

    private lateinit var archivo: Archivo

    private lateinit var imageMeGusta: ImageButton
    private lateinit var imageNoMeGusta: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        archivo = if (intent.extras != null) {
            intent.extras?.getSerializable("archivo") as Archivo
        } else {
            savedInstanceState!!.getSerializable("archivo") as Archivo
        }
        val binding: ActivityArchivoDetalladoBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_archivo_detallado)

        archivoViewModel = ArchivoViewModel()
        listaComentariosViewModel =
            ViewModelProviders.of(this, factoryComentarios)
                .get(ListaComentariosViewModel::class.java)

        autenticacionViewModel = ViewModelProviders.of(this, factoryAutenticacion)
            .get(AutenticacionViewModel::class.java)

        reaccionViewModel =
            ViewModelProviders.of(this, factoryReaccion).get(ReaccionViewModel::class.java)

        binding.archivoViewModel = archivoViewModel
        binding.listaComentarioViewModel = listaComentariosViewModel

        imageMeGusta = layoutReacciones.imageButtonMeGusta
        imageNoMeGusta = layoutReacciones.imageButtonNoMeGusta

        archivoViewModel.bind(archivo)

        reaccionViewModel.requestListener = this
        listaComentariosViewModel.requestListener = this

        listaComentariosViewModel.getComentariosEnFirestorePorPublicacion(archivo.id)

        editTextComentarioArchivo.setEndIconOnClickListener {
            val uid: String = autenticacionViewModel.usuario!!.uid
            val textoComentario = inputComentarioArchivo.text.toString().trim()
            if (textoComentario.isEmpty()) {
                inputComentarioArchivo.error = getString(R.string.debes_escribir_un_comentario)
                return@setEndIconOnClickListener
            }
            val comentario = Comentario(
                textoComentario,
                Timestamp.now().seconds.toString(),
                uid,
                archivo.id,
                "archivos"
            )
            listaComentariosViewModel.agregarComentariosEnFirestorePorPublicacion(comentario, archivo)
            hideKeyboard(this)
            inputComentarioArchivo.setText("")
        }

        reaccionViewModel.getReaccionByPublicacionIdYUsuarioUid(
            archivo.id,
            archivo,
            autenticacionViewModel.usuario!!.uid
        )

        imageMeGusta.setOnClickListener {
            val reaccion = Reaccion(
                timestamp = Timestamp.now().seconds.toString(),
                tipoPublicacion = "actividades",
                usuarioUid = autenticacionViewModel.usuario!!.uid,
                tipoReaccion = "meGusta",
                publicacionId = archivo.id

            )
            onReaccion(reaccion, archivo.reaccion, archivo)

        }
        imageNoMeGusta.setOnClickListener {
            val reaccion = Reaccion(
                timestamp = Timestamp.now().seconds.toString(),
                tipoPublicacion = "actividades",
                usuarioUid = autenticacionViewModel.usuario!!.uid,
                tipoReaccion = "noMeGusta",
                publicacionId = archivo.id

            )
            onReaccion(reaccion, archivo.reaccion, archivo)
        }

    }

    override fun onStartRequest() {

    }

    override fun onSuccessRequest() {
        if (archivo.reaccion != null) {
            when (archivo.reaccion!!.tipoReaccion) {
                "meGusta" -> imageMeGusta.setImageResource(R.drawable.ic_thumb_up_black_filled_24dp)
                "noMeGusta" -> imageNoMeGusta.setImageResource(R.drawable.ic_thumb_down_black_filled_24dp)
            }
        }
    }

    override fun onFailureRequest(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        archivo = savedInstanceState.getSerializable("actividad") as Archivo
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("actividad", archivo)
        super.onSaveInstanceState(outState)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
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


}