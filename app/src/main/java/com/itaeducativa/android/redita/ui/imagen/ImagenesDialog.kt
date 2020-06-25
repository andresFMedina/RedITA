package com.itaeducativa.android.redita.ui.imagen

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import com.itaeducativa.android.redita.R
import kotlinx.android.synthetic.main.dialog_seleccionar_imagenes.*

private const val TAG = "imagen_dialog"

class ImagenesDialog(private val imagenes: MutableList<Uri>) : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme_FullScreenDialog)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_seleccionar_imagenes, container, false)
    }

    override fun onStart() {
        super.onStart()
        if(dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog?.window?.setLayout(width, height)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.setNavigationOnClickListener {
            val dialogListener = activity as DialogListener
            dialogListener.onFinishDialog(mutableListOf())
            dismiss()
        }
        toolbar.inflateMenu(R.menu.menu_imagenes_dialog)
        toolbar.setOnMenuItemClickListener {
            val dialogListener = activity as DialogListener
            dialogListener.onFinishDialog(imagenes)
            dismiss()
            true
        }
        val adapter = ImagenesAdapter(imagenes)
        recyclerViewImagenesSeleccionadas.adapter = adapter

    }

    interface DialogListener {
        fun onFinishDialog(imagenes: MutableList<Uri>)
    }
}