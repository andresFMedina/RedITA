package com.itaeducativa.android.redita

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import com.itaeducativa.android.redita.data.modelos.Usuario
import com.itaeducativa.android.redita.ui.actividad.actividad.ui.ListaActividadesFragment
import com.itaeducativa.android.redita.ui.actividad.actividad.ui.MisActividadesFragment
import com.itaeducativa.android.redita.ui.actividad.actividad.viewmodels.NotificacionViewModel
import com.itaeducativa.android.redita.ui.actividad.actividad.viewmodels.NotificacionViewModelFactory
import com.itaeducativa.android.redita.ui.login.AutenticacionViewModel
import com.itaeducativa.android.redita.ui.login.AutenticacionViewModelFactory
import com.itaeducativa.android.redita.ui.usuario.PerfilFragment
import kotlinx.android.synthetic.main.activity_main.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class MainActivity : AppCompatActivity(), KodeinAware {
    override val kodein: Kodein by kodein()
    private val autenticacionFactory: AutenticacionViewModelFactory by instance()
    private val notificacionFactory: NotificacionViewModelFactory by instance()
    private lateinit var autenticacionViewModel: AutenticacionViewModel
    private lateinit var notificacionViewModel: NotificacionViewModel
    private lateinit var usuario: Usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val extras = intent.extras
        usuario = if (extras != null) {
            extras.getSerializable("usuario") as Usuario
        } else {
            savedInstanceState!!.getSerializable("usuario") as Usuario
        }

        Log.d("USuario", usuario.toString())

        autenticacionViewModel =
            ViewModelProviders.of(this, autenticacionFactory).get(AutenticacionViewModel::class.java)
        notificacionViewModel =
            ViewModelProviders.of(this, notificacionFactory).get(NotificacionViewModel::class.java)
        notificacionViewModel.getToken()

        bottomBarMenuPrincipal.menu.findItem(R.id.menuMisActividades).isVisible = usuario.rol == "Docente"
        bottomBarMenuPrincipal.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menuInicio -> {
                    openFragment(ListaActividadesFragment.newInstance())
                    true
                }
                R.id.menuPerfil -> {
                    openFragment(PerfilFragment.newInstance(usuario))
                    true

                }
                R.id.menuMisActividades -> {
                    openFragment(MisActividadesFragment.newInstance(usuario))
                    true
                }
                else -> false
            }
        }
        openFragment(ListaActividadesFragment())

    }

    private fun openFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        outState.putSerializable("usuario", usuario)

        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        usuario = savedInstanceState.getSerializable("usuario") as Usuario
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_principal_app_bar, menu!!)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.logout -> {
            autenticacionViewModel.logout(this)
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

}
