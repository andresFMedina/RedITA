package com.itaeducativa.android.redita.util

import android.content.Context
import android.content.Intent
import com.itaeducativa.android.redita.MainActivity

fun Context.startMainActivity() =
    Intent(this, MainActivity::class.java).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }