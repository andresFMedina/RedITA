package com.itaeducativa.android.redita.ui.archivo

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.data.modelos.Archivo
import com.itaeducativa.android.redita.databinding.CardviewArchivosBinding
import com.itaeducativa.android.redita.ui.reaccion.ReaccionListener
import com.itaeducativa.android.redita.util.reaccionHandler
import com.itaeducativa.android.redita.util.startArchivoDetalladoActivity

class ListaArchivoAdapter(
    private val uidUsuarioActual: String
) : RecyclerView.Adapter<ListaArchivoAdapter.ViewHolder>() {
    private lateinit var listaArchivos: List<Archivo>

    var reaccionListener: ReaccionListener? = null

    class ViewHolder(private val binding: CardviewArchivosBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val viewModel = ArchivoViewModel()

        val imageButtonMeGusta: ImageButton = binding.layoutReacciones.imageButtonMeGusta
        val imageButtonNoMeGusta: ImageButton = binding.layoutReacciones.imageButtonNoMeGusta
        val imageButtonComentarios: ImageButton = binding.layoutReacciones.imageButtonComentarios

        fun bind(archivo: Archivo) {
            viewModel.bind(archivo)
            binding.viewModel = viewModel
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: CardviewArchivosBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.cardview_archivos,
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int =
        if (::listaArchivos.isInitialized) listaArchivos.size else 0


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val archivo = listaArchivos[position]
        holder.bind(archivo)

        val reaccion = archivo.reaccion
        reaccionHandler(
            publicacionId = archivo.id,
            publicacion = archivo,
            tipoPublicacion = "archivos",
            usuarioUid = uidUsuarioActual,
            reaccion = reaccion,
            imageButtonMeGusta = holder.imageButtonMeGusta,
            imageButtonNoMeGusta = holder.imageButtonNoMeGusta,
            reaccionListener = reaccionListener
        )
        holder.imageButtonComentarios.setOnClickListener {
            it.context.startArchivoDetalladoActivity(
                archivo
            )
        }
    }

    fun actualizarArchivos(archivos: List<Archivo>){
        listaArchivos = archivos
        notifyDataSetChanged()
    }
}