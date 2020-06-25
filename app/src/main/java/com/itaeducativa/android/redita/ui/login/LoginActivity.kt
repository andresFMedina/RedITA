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
import com.itaeducativa.android.redita.ui.usuario.ListaUsuarioViewModel
import com.itaeducativa.android.redita.ui.usuario.ListaUsuarioViewModelFactory
import com.itaeducativa.android.redita.util.TextWatcherValidacionVacio
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
    private val factoryListaUsuario: ListaUsuarioViewModelFactory by instance()

    private lateinit var autenticacionViewModel: AutenticacionViewModel
    private lateinit var listaUsuarioViewModel: ListaUsuarioViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityLoginBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_login)
        autenticacionViewModel = ViewModelProviders.of(this, factoryAutenticacion)
            .get(AutenticacionViewModel::class.java)
        listaUsuarioViewModel =
            ViewModelProviders.of(this, factoryListaUsuario).get(ListaUsuarioViewModel::class.java)
        binding.viewModel = autenticacionViewModel

        autenticacionViewModel.autenticacionListener = this
        listaUsuarioViewModel.requestListener = this


        inputEmailLogin.editText?.addTextChangedListener(TextWatcherValidacionVacio(inputEmailLogin))

        inputPasswordLogin.editText?.addTextChangedListener(
            TextWatcherValidacionVacio(
                inputPasswordLogin
            )
        )

    }

    fun login(view: View) {
        if (inputEmailLogin.editText?.text.toString().isEmpty()) {
            inputEmailLogin.error = getString(R.string.este_campo_no_puede_estar_vacio)
            return
        }
        if (inputPasswordLogin.editText?.text.toString().isEmpty()) {
            inputPasswordLogin.error = getString(R.string.este_campo_no_puede_estar_vacio)
            return
        }
        autenticacionViewModel.login()
    }

    override fun onStarted() {
        layoutEstadoLogin.visibility = View.VISIBLE
        textViewEstadoLogin.text = getString(R.string.autenticando)
        buttonIngresar.isEnabled = false
        layoutLogin.visibility = View.GONE

    }


    override fun onSuccess() {
        listaUsuarioViewModel.getUsuarioByUid(autenticacionViewModel.usuario!!.uid)
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
            listaUsuarioViewModel.getUsuarioByUid(it.uid)
            layoutEstadoLogin.visibility = View.VISIBLE
            textViewEstadoLogin.text = getString(R.string.autenticando)
            buttonIngresar.isEnabled = false
            layoutLogin.visibility = View.GONE
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
        startMainActivity(listaUsuarioViewModel.usuario.value!!)
    }

    override fun onFailureRequest(message: String) {
        textViewEstadoLogin.text = getString(R.string.error_encontrado)
        layoutEstadoLogin.visibility = View.GONE
        buttonIngresar.isEnabled = true
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        layoutLogin.visibility = View.VISIBLE
    }

    override fun onStop() {
        super.onStop()
        autenticacionViewModel.autenticacionListener = null
        listaUsuarioViewModel.requestListener = null
    }
}
