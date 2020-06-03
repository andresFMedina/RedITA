package com.itaeducativa.android.redita.ui.login

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.databinding.ActivitySingUpBinding
import com.itaeducativa.android.redita.network.AutenticacionListener
import com.itaeducativa.android.redita.network.RequestListener
import com.itaeducativa.android.redita.ui.usuario.UsuarioViewModel
import com.itaeducativa.android.redita.ui.usuario.UsuarioViewModelFactory
import com.itaeducativa.android.redita.util.startLoginActivity
import com.itaeducativa.android.redita.util.startMainActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sing_up.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class SingUpActivity : AppCompatActivity(), AutenticacionListener, RequestListener, KodeinAware {
    override val kodein: Kodein by kodein()
    private val autenticacionFactory: AutenticacionViewModelFactory by instance()
    private val usuarioFactory: UsuarioViewModelFactory by instance()

    private lateinit var usuarioViewModel: UsuarioViewModel
    private lateinit var autenticacionViewModel: AutenticacionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivitySingUpBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_sing_up)
        autenticacionViewModel = ViewModelProviders.of(this, autenticacionFactory)
            .get(AutenticacionViewModel::class.java)
        binding.autenticacionViewModel = autenticacionViewModel
        usuarioViewModel =
            ViewModelProviders.of(this, usuarioFactory).get(UsuarioViewModel::class.java)
        binding.usuarioViewModel = usuarioViewModel

        autenticacionViewModel.autenticacionListener = this
        usuarioViewModel.requestListener = this
    }

    override fun onStarted() {
        buttonRegistarse.isEnabled = false
        progressBarSingUp.visibility = View.VISIBLE
        layoutSignUp.visibility = View.GONE

    }

    override fun onSuccess() {
        progressBarSingUp.visibility = View.GONE
        val uid = autenticacionViewModel.usuario!!.uid
        usuarioViewModel.guardarUsuario(autenticacionViewModel.email!!, uid)
    }

    override fun onFailure(mensaje: String) {
        progressBarSingUp.visibility = View.GONE
        buttonRegistarse.isEnabled = true
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        layoutSignUp.visibility = View.VISIBLE
    }

    override fun onStartRequest() {
        progressBarSingUp.visibility = View.VISIBLE
    }

    override fun onSuccessRequest() {
        this.startMainActivity(usuarioViewModel.usuario.value!!)
    }

    override fun onFailureRequest(message: String) {
        buttonRegistarse.isEnabled = true
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun goToLoginActivity(view: View) {
        this.startLoginActivity()
    }

}
