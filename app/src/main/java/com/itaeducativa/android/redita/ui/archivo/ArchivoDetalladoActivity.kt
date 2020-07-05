package com.itaeducativa.android.redita.ui.archivo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.Timestamp
import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.data.modelos.Archivo
import com.itaeducativa.android.redita.data.modelos.Comentario
import com.itaeducativa.android.redita.databinding.ActivityArchivoDetalladoBinding
import com.itaeducativa.android.redita.network.RequestListener
import com.itaeducativa.android.redita.ui.comentario.viewmodels.ListaComentariosViewModel
import com.itaeducativa.android.redita.ui.comentario.viewmodels.ListaComentariosViewModelFactory
import com.itaeducativa.android.redita.ui.login.AutenticacionViewModel
import com.itaeducativa.android.redita.ui.login.AutenticacionViewModelFactory
import com.itaeducativa.android.redita.util.hideKeyboard
import kotlinx.android.synthetic.main.activity_archivo_detallado.*
import kotlinx.android.synthetic.main.cardview_archivos.view.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class ArchivoDetalladoActivity : AppCompatActivity(), KodeinAware, RequestListener {
    override val kodein: Kodein by kodein()

    private val factoryComentarios: ListaComentariosViewModelFactory by instance()
    private val factoryAutenticacion: AutenticacionViewModelFactory by instance()

    private lateinit var archivoViewModel: ArchivoViewModel
    private lateinit var listaComentariosViewModel: ListaComentariosViewModel
    private lateinit var autenticacionViewModel: AutenticacionViewModel

    private lateinit var archivo: Archivo

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
            ViewModelProviders.of(this, factoryComentarios).get(ListaComentariosViewModel::class.java)

        autenticacionViewModel = ViewModelProviders.of(this, factoryAutenticacion).get(AutenticacionViewModel::class.java)

        binding.archivoViewModel = archivoViewModel
        binding.listaComentarioViewModel = listaComentariosViewModel

        archivoViewModel.bind(archivo)

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
            listaComentariosViewModel.agregarComentariosEnFirestorePorPublicacion(comentario)
            hideKeyboard(this)
            inputComentarioArchivo.setText("")
        }

        cardviewArchivo.imageViewImagen.setOnClickListener {

        }
    }

    override fun onStartRequest() {

    }

    override fun onSuccessRequest() {
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





}