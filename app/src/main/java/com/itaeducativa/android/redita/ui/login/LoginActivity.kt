package com.itaeducativa.android.redita.ui.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.itaeducativa.android.redita.FirebaseApplication
import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.databinding.ActivityLoginBinding
import kotlinx.android.synthetic.main.activity_login.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class LoginActivity : AppCompatActivity(), AutenticacionListener, KodeinAware {

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
        progressBarLogin.visibility = View.VISIBLE
    }

    override fun onSuccess() {
        progressBarLogin.visibility = GONE
    }

    override fun onFailure(mensaje: String) {
        progressBarLogin.visibility = View.GONE
        Toast.makeText(this,mensaje,Toast.LENGTH_SHORT).show()
    }
}
