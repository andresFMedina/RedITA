package com.itaeducativa.android.redita

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import com.itaeducativa.android.redita.data.modelos.Usuario
import com.itaeducativa.android.redita.ui.actividad.actividad.ui.ListaActividadesFragment
import com.itaeducativa.android.redita.ui.actividad.actividad.ui.MisActividadesFragment
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
    private val factory: AutenticacionViewModelFactory by instance()
    private lateinit var autenticacionViewModel: AutenticacionViewModel
    private lateinit var usuario: Usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val extras = intent.extras
        if (extras != null) {
            usuario = extras.getSerializable("usuario") as Usuario
        } else {
            usuario = savedInstanceState!!.getSerializable("usuario") as Usuario
        }

        Log.d("USuario", usuario.toString())

        autenticacionViewModel =
            ViewModelProviders.of(this, factory).get(AutenticacionViewModel::class.java)

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
                R.id.logout -> {
                    autenticacionViewModel.logout(this)
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
}
