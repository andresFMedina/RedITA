package com.itaeducativa.android.redita.ui.imagen

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import com.itaeducativa.android.redita.BR
import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.databinding.DialogSeleccionarImagenesBinding
import kotlinx.android.synthetic.main.dialog_seleccionar_imagenes.*

private const val TAG = "imagen_dialog"

class ImagenesDialog : Fragment() {

    private lateinit var imagenesViewModel: ImagenesViewModel
    private lateinit var imagenes: MutableList<Uri>

    /*override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        val imagenesBinding: DialogSeleccionarImagenesBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.dialog_seleccionar_imagenes,
            container,
            true
        )

        imagenesBinding.viewModel = imagenesViewModel


    }*/


    /*override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setNavigationOnClickListener { dismiss() }
        toolbar.inflateMenu(R.menu.menu_imagenes_dialog)
        toolbar.setOnMenuItemClickListener {
            dismiss()
            true
        }

    }*/
}