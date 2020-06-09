package com.itaeducativa.android.redita

import android.app.Application
import com.itaeducativa.android.redita.data.firebase.FirebaseSource
import com.itaeducativa.android.redita.data.repositorios.*
import com.itaeducativa.android.redita.ui.actividad.actividad.viewmodels.ListaActividadesViewModelFactory
import com.itaeducativa.android.redita.ui.actividad.actividad.viewmodels.StorageViewModelFactory
import com.itaeducativa.android.redita.ui.actividad.comentario.viewmodels.ListaComentariosViewModelFactory
import com.itaeducativa.android.redita.ui.actividad.reaccion.ReaccionViewModelFactory
import com.itaeducativa.android.redita.ui.login.AutenticacionViewModelFactory
import com.itaeducativa.android.redita.ui.usuario.UsuarioViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class FirebaseApplication : Application(), KodeinAware {

    override val kodein = Kodein.lazy {
        import(androidXModule(this@FirebaseApplication))

        bind() from singleton { FirebaseSource() }
        bind() from singleton { RepositorioAutenticacion(instance()) }
        bind() from singleton { RepositorioStorage() }
        bind() from provider { AutenticacionViewModelFactory(instance()) }
        bind() from singleton { RepositorioUsuario(instance()) }
        bind() from provider { UsuarioViewModelFactory(instance(), instance()) }
        bind() from singleton { RepositorioActividad(instance()) }
        bind() from provider {
            ListaActividadesViewModelFactory(
                instance(),
                instance(),
                instance(),
                instance(),
                instance()
            )
        }
        bind() from singleton { RepositorioComentario(instance()) }
        bind() from provider {
            ListaComentariosViewModelFactory(
                instance(),
                instance(),
                instance()
            )
        }
        bind() from singleton { RepositorioReaccion(instance()) }
        bind() from provider { ReaccionViewModelFactory(instance()) }
        bind() from provider { StorageViewModelFactory(instance()) }
    }

}