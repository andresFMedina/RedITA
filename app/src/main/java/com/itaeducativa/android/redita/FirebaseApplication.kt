package com.itaeducativa.android.redita

import android.app.Application
import com.itaeducativa.android.redita.data.firebase.FirebaseSource
import com.itaeducativa.android.redita.data.repositorios.*
import com.itaeducativa.android.redita.ui.actividad.actividad.viewmodels.ListaActividadesViewModelFactory
import com.itaeducativa.android.redita.ui.actividad.actividad.viewmodels.NotificacionViewModelFactory
import com.itaeducativa.android.redita.ui.actividad.actividad.viewmodels.StorageViewModelFactory
import com.itaeducativa.android.redita.ui.actividad.comentario.viewmodels.ListaComentariosViewModelFactory
import com.itaeducativa.android.redita.ui.actividad.reaccion.ReaccionViewModelFactory
import com.itaeducativa.android.redita.ui.archivo.ListaArchivoViewModelFactory
import com.itaeducativa.android.redita.ui.historial.ListaHistorialViewModelFactory
import com.itaeducativa.android.redita.ui.login.AutenticacionViewModelFactory
import com.itaeducativa.android.redita.ui.usuario.ListaUsuarioViewModelFactory
import com.itaeducativa.android.redita.ui.vista.ListaVistaViewModelFactory
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
        bind() from singleton { RepositorioNotificacion(instance()) }
        bind() from provider { NotificacionViewModelFactory(instance()) }
        bind() from provider { AutenticacionViewModelFactory(instance()) }
        bind() from singleton { RepositorioUsuario(instance()) }
        bind() from provider { ListaUsuarioViewModelFactory(instance(), instance()) }
        bind() from singleton { RepositorioActividad(instance()) }
        bind() from provider {
            ListaActividadesViewModelFactory(
                instance(),
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
                instance(),
                instance()
            )
        }
        bind() from singleton { RepositorioReaccion(instance()) }
        bind() from provider { ReaccionViewModelFactory(instance()) }
        bind() from provider { StorageViewModelFactory(instance()) }
        bind() from singleton { RepositorioVista(instance()) }
        bind() from provider { ListaVistaViewModelFactory(instance(), instance(), instance()) }
        bind() from singleton { RepositorioHistorial(instance()) }
        bind() from provider { ListaHistorialViewModelFactory(instance(), instance(), instance()) }
        bind() from singleton { RepositorioArchivo(instance()) }
        bind() from provider { ListaArchivoViewModelFactory(instance()) }
    }

}