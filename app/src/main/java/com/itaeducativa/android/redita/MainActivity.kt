package com.itaeducativa.android.redita

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.itaeducativa.android.redita.ui.actividad.ListaActividadesFragment
import com.itaeducativa.android.redita.ui.usuario.PerfilFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
