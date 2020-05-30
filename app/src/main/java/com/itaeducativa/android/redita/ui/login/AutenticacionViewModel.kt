package com.itaeducativa.android.redita.ui.login

import androidx.lifecycle.ViewModel
import com.itaeducativa.android.redita.data.repositorios.RepositorioAutenticacion
import com.itaeducativa.android.redita.network.AutenticacionListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class AutenticacionViewModel(private val repositorio: RepositorioAutenticacion) : ViewModel() {
    var email: String? = null
    var password: String? = null

    var autenticacionListener: AutenticacionListener? = null

    private val disposables = CompositeDisposable()

    val usuario by lazy {
        repositorio.currentUser()
    }

    fun login() {
        if(email.isNullOrEmpty() || password.isNullOrEmpty()){
            autenticacionListener?.onFailure("No hay email ni contraseña")
            return
        }

        autenticacionListener?.onStarted()

        val disposable = repositorio.login(email!!, password!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                autenticacionListener?.onSuccess()
            },{
                autenticacionListener?.onFailure(it.message!!)
            })
        disposables.add(disposable)
    }

    fun singUp() {
        if(email.isNullOrEmpty() || password.isNullOrEmpty()) {
            autenticacionListener?.onFailure("No hay email o contraseña")
            return
        }
        autenticacionListener?.onStarted()
        val disposable = repositorio.register(email!!, password!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                autenticacionListener?.onSuccess()
            },{
                autenticacionListener?.onFailure(it.message!!)
            })

        disposables.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}