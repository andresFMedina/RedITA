package com.itaeducativa.android.redita.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.itaeducativa.android.redita.MainActivity
import com.itaeducativa.android.redita.data.modelos.Actividad
import com.itaeducativa.android.redita.data.modelos.Usuario
import com.itaeducativa.android.redita.ui.actividad.actividad.ui.ActividadActivity
import com.itaeducativa.android.redita.ui.actividad.actividad.ui.CrearActividadActivity
import com.itaeducativa.android.redita.ui.login.LoginActivity
import com.itaeducativa.android.redita.ui.login.SingUpActivity

fun Context.startMainActivity(usuario: Usuario) =
    Intent(this, MainActivity::class.java).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val bundle = Bundle()
        bundle.putSerializable("usuario", usuario)
        it.putExtras(bundle)
        startActivity(it)
    }

fun Context.startActividadActivity(actividad: Actividad) =
    Intent(this, ActividadActivity::class.java).also {
        val bundle = Bundle()
        bundle.putSerializable("actividad", actividad)
        it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        it.putExtras(bundle)
        startActivity(it)
    }

fun Context.startLoginActivity() = Intent(this, LoginActivity::class.java).also {
    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivity(it)
}

fun Context.startSingUpActivity() = Intent(this, SingUpActivity::class.java).also {
    it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    startActivity(it)
}

fun Context.startCrearActividadActivity() = Intent(this, CrearActividadActivity::class.java).also {
    it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    startActivity(it)
}

fun Context.hideKeyboard(activity: Activity) {
    val inputMethodManager: InputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    val view: View = activity.currentFocus?: View(activity)
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)

}
