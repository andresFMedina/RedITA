package com.itaeducativa.android.redita.ui.imagen

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.databinding.CardviewGuardarImagenBinding

class ImagenesAdapter(val imagenes: MutableList<Uri>): RecyclerView.Adapter<ImagenesAdapter.ViewHolder>() {

    class ViewHolder(binding: CardviewGuardarImagenBinding): RecyclerView.ViewHolder(binding.root){
        val imagen = binding.frameLayoutImagen.imageViewImagen
        val button = binding.frameLayoutImagen.imageButtonCerrarDialogoImagen
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: CardviewGuardarImagenBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.cardview_guardar_imagen,
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = imagenes.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imagen.setImageURI(imagenes[position])
        holder.button.setOnClickListener {
            imagenes.removeAt(position)
            notifyDataSetChanged()
        }
    }
}