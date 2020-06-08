package com.itaeducativa.android.redita.ui.login

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.itaeducativa.android.redita.data.repositorios.RepositorioAutenticacion
import com.itaeducativa.android.redita.network.AutenticacionListener
import com.itaeducativa.android.redita.util.startLoginActivity

class AutenticacionViewModel(private val repositorio: RepositorioAutenticacion) : ViewModel() {
    var email: String? = null
    var password: String? = null

    var autenticacionListener: AutenticacionListener? = null


    var usuario = repositorio.currentUser()


    fun login() {
        if(email.isNullOrEmpty() || password.isNullOrEmpty()){
            autenticacionListener?.onFailure("No hay email ni contraseña")
            return
        }

        autenticacionListener?.onStarted()

        repositorio.login(email!!, password!!).addOnSuccessListener {
            usuario = it.user
            autenticacionListener?.onSuccess()

        }.addOnFailureListener {
            autenticacionListener?.onFailure(it.message!!)
        }

    }

    fun singUp() {
        if(email.isNullOrEmpty() || password.isNullOrEmpty()) {
            autenticacionListener?.onFailure("No hay email o contraseña")
            return
        }
        autenticacionListener?.onStarted()
        repositorio.register(email!!, password!!).addOnSuccessListener {
            usuario = it.user
            autenticacionListener?.onSuccess()
        }.addOnFailureListener {
            autenticacionListener?.onFailure(it.message!!)
        }
    }



    fun logout(context: Context) {
        repositorio.logout()
        context.startLoginActivity()
    }

}