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
import com.itaeducativa.android.redita.util.startMainActivity
import com.itaeducativa.android.redita.util.startSingUpActivity
import kotlinx.android.synthetic.main.activity_login.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class LoginActivity : AppCompatActivity(),
    AutenticacionListener, KodeinAware {

    override val kodein by kodein()
    private val factory: AutenticacionViewModelFactory by instance()

    private lateinit var viewModel: AutenticacionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityLoginBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewModel = ViewModelProviders.of(this, factory).get(AutenticacionViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.autenticacionListener = this

    }

    override fun onStarted() {
        buttonIngresar.isEnabled = false
        progressBarLogin.visibility = View.VISIBLE
        layoutLogin.visibility = View.GONE
    }

    override fun onSuccess() {
        progressBarLogin.visibility = View.GONE
        startMainActivity()
    }

    override fun onFailure(mensaje: String) {
        progressBarLogin.visibility = View.GONE
        buttonIngresar.isEnabled = true
        Toast.makeText(this,mensaje,Toast.LENGTH_SHORT).show()
        layoutLogin.visibility = View.VISIBLE
    }

    override fun onStart() {
        super.onStart()
        viewModel.usuario?.let {
            startMainActivity()
        }
    }

    fun goToSignUpActivity(view: View){
        this.startSingUpActivity()
    }
}
