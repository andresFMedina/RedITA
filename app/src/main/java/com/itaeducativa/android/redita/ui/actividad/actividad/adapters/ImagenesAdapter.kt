package com.itaeducativa.android.redita.ui.actividad.actividad.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.itaeducativa.android.redita.databinding.CardviewMultimediaBinding
import com.itaeducativa.android.redita.databinding.DialogImagenDetalladaBinding
import com.itaeducativa.android.redita.R


class ImagenesAdapter(private val listaImagenes: List<String>?) :
    RecyclerView.Adapter<ImagenesAdapter.ViewHolder>() {


    class ViewHolder(private val binding: CardviewMultimediaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val imageView = binding.imageViewMultimedia
        fun bind(urlImagen: String) {
            binding.imagen = urlImagen
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: CardviewMultimediaBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.cardview_multimedia,
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listaImagenes?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listaImagenes!![position])
        holder.imageView.setOnClickListener {
            val builder = AlertDialog.Builder(it.context)
            val binding: DialogImagenDetalladaBinding =
                DataBindingUtil.inflate(
                    LayoutInflater.from(it.context),
                    R.layout.dialog_imagen_detallada,
                    null,
                    false
                )
            val view = binding.root
            builder.setView(view)
            binding.url = listaImagenes!![position]

            val dialog = builder.create()
            binding.imageButtonCerrarDialogoImagen.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }
}