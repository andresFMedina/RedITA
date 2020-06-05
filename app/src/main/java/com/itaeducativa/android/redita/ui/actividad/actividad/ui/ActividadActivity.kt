package com.itaeducativa.android.redita.ui.actividad.actividad.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.Timestamp
import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.data.modelos.Actividad
import com.itaeducativa.android.redita.data.modelos.Comentario
import com.itaeducativa.android.redita.databinding.ActivityActividadBinding
import com.itaeducativa.android.redita.network.RequestListener
import com.itaeducativa.android.redita.ui.actividad.actividad.viewmodels.ActividadViewModel
import com.itaeducativa.android.redita.ui.actividad.comentario.viewmodels.ListaComentariosViewModel
import com.itaeducativa.android.redita.ui.actividad.comentario.viewmodels.ListaComentariosViewModelFactory
import com.itaeducativa.android.redita.ui.login.AutenticacionViewModel
import com.itaeducativa.android.redita.ui.login.AutenticacionViewModelFactory
import kotlinx.android.synthetic.main.activity_actividad.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import org.kodein.di.android.kodein
import java.util.*

class ActividadActivity : AppCompatActivity(), RequestListener, KodeinAware {
    override val kodein: Kodein by kodein()
    private val factory: ListaComentariosViewModelFactory by instance()
    private val autenticacionFactory: AutenticacionViewModelFactory by instance()

    private lateinit var actividad: Actividad

    var textoComentario: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actividad = intent.extras?.getSerializable("actividad") as Actividad
        val seconds = intent.extras?.getLong("seconds")
        val nanoseconds = intent.extras?.getInt("nanoseconds")
        actividad.fechaCreacionTimeStamp = Timestamp(seconds!!, nanoseconds!!)
        val binding: ActivityActividadBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_actividad)
        val viewModelActividad = ViewModelProviders.of(this).get(ActividadViewModel::class.java)
        viewModelActividad.bind(actividad)
        val viewModelComentario = ViewModelProviders.of(this, factory).get(ListaComentariosViewModel::class.java)
        val autenticacionViewModel = ViewModelProviders.of(this, autenticacionFactory).get(AutenticacionViewModel::class.java)

        binding.viewModelActividad = viewModelActividad
        binding.viewModelComentario = viewModelComentario
        binding.textoComentario = textoComentario

        viewModelComentario.requestListener = this

        viewModelComentario.getComentariosEnFirestorePorActividad(actividad.fechaCreacionTimeStamp!!.seconds.toString())

        editTextComentario.setEndIconOnClickListener {
            val uid: String = autenticacionViewModel.usuario!!.uid
            val timestamp = actividad.fechaCreacionTimeStamp!!.seconds.toString()
            textoComentario = editTextComentario.editText!!.text.toString().trim()
            val comentario = Comentario(textoComentario, Timestamp(Date()),uid, timestamp)
            viewModelComentario.agregarComentariosEnFirestorePorActividad(comentario)
        }

    }

    override fun onStartRequest() {

    }

    override fun onSuccessRequest() {

    }

    override fun onFailureRequest(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}
