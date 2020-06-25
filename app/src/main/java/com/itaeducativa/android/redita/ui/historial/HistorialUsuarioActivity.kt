package com.itaeducativa.android.redita.ui.historial

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.data.modelos.Usuario
import com.itaeducativa.android.redita.databinding.ActivityHistorialUsuarioBinding
import com.itaeducativa.android.redita.network.RequestListener
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class HistorialUsuarioActivity : AppCompatActivity(), RequestListener, KodeinAware {
    override val kodein: Kodein by kodein()

    private val factory: ListaHistorialViewModelFactory by instance()
    private lateinit var viewModel: ListaHistorialViewModel

    private lateinit var usuario: Usuario


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val extras = intent.extras
        usuario = if (extras != null) {
            extras.getSerializable("usuario") as Usuario
        } else {
            savedInstanceState!!.getSerializable("usuario") as Usuario
        }
        val binding: ActivityHistorialUsuarioBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_historial_usuario)
        viewModel = ViewModelProviders.of(this, factory).get(ListaHistorialViewModel::class.java)
        viewModel.requestListener = this

        binding.viewModel = viewModel

        viewModel.getHistorialByUsuarioUid(usuario.uid)

        supportActionBar?.title = usuario.nombreCompleto
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onStartRequest() {

    }

    override fun onSuccessRequest() {

    }

    override fun onFailureRequest(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        outState.putSerializable("usuario", usuario)

        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        usuario = savedInstanceState.getSerializable("usuario") as Usuario
    }
}