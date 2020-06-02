package com.itaeducativa.android.redita

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import com.itaeducativa.android.redita.databinding.ActivityMainBinding
import com.itaeducativa.android.redita.ui.actividad.ListaActividadesFragment
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        autenticacionViewModel =
            ViewModelProviders.of(this, factory).get(AutenticacionViewModel::class.java)

        bottomBarMenuPrincipal.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menuInicio -> {
                    openFragment(ListaActividadesFragment.newInstance("", ""))
                    true
                }
                R.id.menuPerfil -> {
                    openFragment(PerfilFragment.newInstance("", ""))
                    true

                }
                R.id.logout -> {
                    autenticacionViewModel.logout(this)
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
}
