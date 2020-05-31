package com.itaeducativa.android.redita.ui.actividad

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.Timestamp
import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.data.modelos.Actividad
import com.itaeducativa.android.redita.databinding.ActivityActividadBinding
import com.itaeducativa.android.redita.network.RequestListener
import com.itaeducativa.android.redita.ui.actividad.comentario.ListaComentariosViewModel
import com.itaeducativa.android.redita.ui.actividad.comentario.ListaComentariosViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import org.kodein.di.android.kodein

class ActividadActivity : AppCompatActivity(), RequestListener, KodeinAware {
    override val kodein: Kodein by kodein()
    private val factory: ListaComentariosViewModelFactory by instance()

    private lateinit var actividad: Actividad

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

        binding.viewModelActividad = viewModelActividad
        binding.viewModelComentario = viewModelComentario

        viewModelComentario.requestListener = this

    }

    override fun onStartRequest() {
        TODO("Not yet implemented")
    }

    override fun onSuccess() {
        TODO("Not yet implemented")
    }

    override fun onFailure(message: String) {
        TODO("Not yet implemented")
    }
}
