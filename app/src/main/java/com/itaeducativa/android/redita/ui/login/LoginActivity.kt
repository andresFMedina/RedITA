package com.itaeducativa.android.redita.ui.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.databinding.ActivityLoginBinding
import com.itaeducativa.android.redita.network.AutenticacionListener
import com.itaeducativa.android.redita.network.RequestListener
import com.itaeducativa.android.redita.ui.usuario.UsuarioViewModel
import com.itaeducativa.android.redita.ui.usuario.UsuarioViewModelFactory
import com.itaeducativa.android.redita.util.startMainActivity
import com.itaeducativa.android.redita.util.startSingUpActivity
import kotlinx.android.synthetic.main.activity_login.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class LoginActivity : AppCompatActivity(),
    AutenticacionListener, KodeinAware, RequestListener {

    override val kodein by kodein()
    private val factoryAutenticacion: AutenticacionViewModelFactory by instance()
    private val factoryUsuario: UsuarioViewModelFactory by instance()

    private lateinit var autenticacionViewModel: AutenticacionViewModel
    private lateinit var usuarioViewModel: UsuarioViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityLoginBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_login)
        autenticacionViewModel = ViewModelProviders.of(this, factoryAutenticacion)
            .get(AutenticacionViewModel::class.java)
        usuarioViewModel =
            ViewModelProviders.of(this, factoryUsuario).get(UsuarioViewModel::class.java)
        binding.viewModel = autenticacionViewModel

        autenticacionViewModel.autenticacionListener = this
        usuarioViewModel.requestListener = this


    }

    override fun onStarted() {
        buttonIngresar.isEnabled = false
        layoutEstadoLogin.visibility = View.VISIBLE
        textViewEstadoLogin.text = getString(R.string.autenticando)
        layoutLogin.visibility = View.GONE
    }

    override fun onSuccess() {
        usuarioViewModel.getUsuarioByUid(autenticacionViewModel.usuario!!.uid)
    }

    override fun onFailure(mensaje: String) {
        textViewEstadoLogin.text = getString(R.string.error_encontrado)
        layoutEstadoLogin.visibility = View.GONE
        buttonIngresar.isEnabled = true
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        layoutLogin.visibility = View.VISIBLE
    }

    override fun onStart() {
        super.onStart()
        autenticacionViewModel.usuario?.let {
            usuarioViewModel.getUsuarioByUid(it.uid)
        }
    }

    fun goToSignUpActivity(view: View) {
        this.startSingUpActivity()
    }

    override fun onStartRequest() {
        textViewEstadoLogin.text = getString(R.string.cargando_informacion)
    }

    override fun onSuccessRequest() {
        layoutEstadoLogin.visibility = View.GONE
        startMainActivity(usuarioViewModel.usuario.value!!)
    }

    override fun onFailureRequest(message: String) {
        textViewEstadoLogin.text = getString(R.string.error_encontrado)
        layoutEstadoLogin.visibility = View.GONE
        buttonIngresar.isEnabled = true
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        layoutLogin.visibility = View.VISIBLE
    }
}
