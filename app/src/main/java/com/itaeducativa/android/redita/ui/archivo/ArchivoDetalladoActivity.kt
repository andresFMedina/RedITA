package com.itaeducativa.android.redita.ui.archivo

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.firebase.Timestamp
import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.data.modelos.Archivo
import com.itaeducativa.android.redita.data.modelos.Comentario
import com.itaeducativa.android.redita.data.modelos.Publicacion
import com.itaeducativa.android.redita.data.modelos.Reaccion
import com.itaeducativa.android.redita.databinding.ActivityArchivoDetalladoBinding
import com.itaeducativa.android.redita.databinding.DialogImagenDetalladaBinding
import com.itaeducativa.android.redita.network.RequestListener
import com.itaeducativa.android.redita.ui.VideoListener
import com.itaeducativa.android.redita.ui.actividad.viewmodels.StorageViewModel
import com.itaeducativa.android.redita.ui.actividad.viewmodels.StorageViewModelFactory
import com.itaeducativa.android.redita.ui.comentario.viewmodels.ListaComentariosViewModel
import com.itaeducativa.android.redita.ui.comentario.viewmodels.ListaComentariosViewModelFactory
import com.itaeducativa.android.redita.ui.login.AutenticacionViewModel
import com.itaeducativa.android.redita.ui.login.AutenticacionViewModelFactory
import com.itaeducativa.android.redita.ui.reaccion.ReaccionViewModel
import com.itaeducativa.android.redita.ui.reaccion.ReaccionViewModelFactory
import com.itaeducativa.android.redita.util.hideKeyboard
import kotlinx.android.synthetic.main.activity_archivo_detallado.*
import kotlinx.android.synthetic.main.linearlayout_reacciones.view.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class ArchivoDetalladoActivity : AppCompatActivity(), KodeinAware, RequestListener, VideoListener {

    override val kodein: Kodein by kodein()

    private val factoryComentarios: ListaComentariosViewModelFactory by instance()
    private val factoryAutenticacion: AutenticacionViewModelFactory by instance()
    private val factoryReaccion: ReaccionViewModelFactory by instance()
    private val factoryStorage: StorageViewModelFactory by instance()

    private lateinit var archivoViewModel: ArchivoViewModel
    private lateinit var listaComentariosViewModel: ListaComentariosViewModel
    private lateinit var autenticacionViewModel: AutenticacionViewModel
    private lateinit var reaccionViewModel: ReaccionViewModel
    private lateinit var storageViewModel: StorageViewModel

    private lateinit var archivo: Archivo

    private lateinit var imageMeGusta: ImageButton
    private lateinit var imageNoMeGusta: ImageButton

    private var player: SimpleExoPlayer? = null
    private var playerView: PlayerView? = null

    private lateinit var imageView: ImageView

    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        archivo = if (intent.extras != null) {
            intent.extras?.getSerializable("archivo") as Archivo
        } else {
            savedInstanceState!!.getSerializable("archivo") as Archivo
        }
        val binding: ActivityArchivoDetalladoBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_archivo_detallado)

        archivoViewModel = ArchivoViewModel()
        listaComentariosViewModel =
            ViewModelProviders.of(this, factoryComentarios)
                .get(ListaComentariosViewModel::class.java)

        autenticacionViewModel = ViewModelProviders.of(this, factoryAutenticacion)
            .get(AutenticacionViewModel::class.java)

        reaccionViewModel =
            ViewModelProviders.of(this, factoryReaccion).get(ReaccionViewModel::class.java)

        storageViewModel =
            ViewModelProviders.of(this, factoryStorage).get(StorageViewModel::class.java)

        binding.archivoViewModel = archivoViewModel
        binding.listaComentarioViewModel = listaComentariosViewModel

        imageMeGusta = binding.layoutReaccionesArchivos.imageButtonMeGusta
        imageNoMeGusta = binding.layoutReaccionesArchivos.imageButtonNoMeGusta
        playerView = binding.videoView
        imageView = binding.imageViewImagen

        archivoViewModel.bind(archivo)

        reaccionViewModel.requestListener = this
        listaComentariosViewModel.requestListener = this

        storageViewModel.videoListener = this

        listaComentariosViewModel.getComentariosEnFirestorePorPublicacion(archivo.id)

        editTextComentarioArchivo.setEndIconOnClickListener {
            val uid: String = autenticacionViewModel.usuario!!.uid
            val textoComentario = inputComentarioArchivo.text.toString().trim()
            if (textoComentario.isEmpty()) {
                inputComentarioArchivo.error = getString(R.string.debes_escribir_un_comentario)
                return@setEndIconOnClickListener
            }
            val comentario = Comentario(
                textoComentario,
                Timestamp.now().seconds.toString(),
                uid,
                archivo.id,
                "archivos"
            )
            listaComentariosViewModel.agregarComentariosEnFirestorePorPublicacion(
                comentario,
                archivo
            )
            hideKeyboard(this)
            inputComentarioArchivo.setText("")
        }

        reaccionViewModel.getReaccionByPublicacionIdYUsuarioUid(
            archivo.id,
            archivo,
            autenticacionViewModel.usuario!!.uid
        )

        setupListeners()

        if (archivo.tipo == "video") {
            binding.imageViewImagen.visibility = View.GONE
            binding.videoView.visibility = View.VISIBLE

            storageViewModel.getVideoUri(archivo.url)
        }


    }

    private fun setupListeners() {
        imageMeGusta.setOnClickListener {
            val reaccion = Reaccion(
                timestamp = Timestamp.now().seconds.toString(),
                tipoPublicacion = "archivos",
                usuarioUid = autenticacionViewModel.usuario!!.uid,
                tipoReaccion = "meGusta",
                publicacionId = archivo.id

            )
            onReaccion(reaccion, archivo.reaccion, archivo)

        }
        imageNoMeGusta.setOnClickListener {
            val reaccion = Reaccion(
                timestamp = Timestamp.now().seconds.toString(),
                tipoPublicacion = "archivos",
                usuarioUid = autenticacionViewModel.usuario!!.uid,
                tipoReaccion = "noMeGusta",
                publicacionId = archivo.id

            )
            onReaccion(reaccion, archivo.reaccion, archivo)
        }

        imageView.setOnClickListener {
            //MaterialAlertDialogBuilder(it.context!!).show()
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
            binding.url = archivo.url

            val dialog = builder.create()
            binding.imageButtonCerrarDialogoImagen.setOnClickListener {
                dialog.dismiss()
            }

            binding.imageButtonGuardarImagenDialogo.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(
                            it.context,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            100
                        )
                    } else {
                        saveImageToStorage(binding.imageViewImagen);
                    }
                } else {
                    saveImageToStorage(binding.imageViewImagen);
                }
            }

            dialog.show()
        }
    }

    private fun saveImageToStorage(imageViewImagen: ImageView) {
        val externalStorageState = Environment.getExternalStorageState()
        if (externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
            val storageDirectory = Environment.getExternalStorageDirectory().toString()
            val folder = File(storageDirectory, "RedITA")
            if (!folder.exists()) {
                folder.mkdirs()
            }

            val path = Environment.getExternalStorageDirectory()
                .toString() + File.separator + "RedITA" + File.separator + String.format(
                "%d.jpg",
                System.currentTimeMillis()
            )
            val file = File(path)
            try {
                val stream: OutputStream = FileOutputStream(file)
                val draw: BitmapDrawable = imageViewImagen.getDrawable() as BitmapDrawable
                val bitmap: Bitmap = draw.getBitmap()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                stream.flush()
                stream.close()
                Toast.makeText(this, "Foto guardada en este dispositivo", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            Toast.makeText(
                this,
                "No se puede acceder al almacenamiento interno del dispositivo",
                Toast.LENGTH_SHORT
            ).show();
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    override fun onStartRequest() {

    }

    override fun onSuccessRequest(response: Any?) {
        when (response) {
            is Reaccion? -> getReaccion(response)
            is Archivo -> {
                layoutReaccionesArchivos.textViewCantidadMeGustaPublicacion.text =
                    archivo.meGusta.toString()
                layoutReaccionesArchivos.textViewCantidadNoMeGustaPublicacion.text =
                    archivo.noMeGusta.toString()
                layoutReaccionesArchivos.textViewCantidadComentariosPublicacion.text =
                    archivo.comentarios.toString()
            }
        }
    }

    private fun getReaccion(reaccion: Reaccion?) {
        imageMeGusta.setImageResource(R.drawable.ic_thumb_up_black_24dp)
        imageNoMeGusta.setImageResource(R.drawable.ic_thumb_down_black_24dp)
        if (reaccion != null) {
            when (reaccion.tipoReaccion) {
                "meGusta" -> imageMeGusta.setImageResource(R.drawable.ic_thumb_up_black_filled_24dp)
                "noMeGusta" -> imageNoMeGusta.setImageResource(R.drawable.ic_thumb_down_black_filled_24dp)
            }
        }
    }

    override fun onFailureRequest(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        archivo = savedInstanceState.getSerializable("actividad") as Archivo
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("actividad", archivo)
        super.onSaveInstanceState(outState)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    fun onReaccion(
        reaccionNueva: Reaccion,
        reaccionVieja: Reaccion?,
        publicacion: Publicacion
    ) {
        if (reaccionVieja != null) {
            reaccionViewModel.eliminarReaccion(reaccionVieja, publicacion)
            imageMeGusta.setImageResource(R.drawable.ic_thumb_up_black_24dp)
            imageNoMeGusta.setImageResource(R.drawable.ic_thumb_down_black_24dp)
            if (reaccionNueva.tipoReaccion != reaccionVieja.tipoReaccion) reaccionViewModel.crearReaccion(
                reaccionNueva,
                publicacion
            )
            return
        }
        reaccionViewModel.crearReaccion(reaccionNueva, publicacion)
    }

    private fun initializePlayer(uri: Uri) {
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(
                DefaultRenderersFactory(this),
                DefaultTrackSelector(),
                DefaultLoadControl()
            )
            playerView?.player = player
            player?.setPlayWhenReady(playWhenReady)
            player?.seekTo(currentWindow, playbackPosition)
        }
        val mediaSource = buildMediaSource(uri)
        player!!.prepare(mediaSource, true, false)
    }

    private fun buildMediaSource(uri: Uri): MediaSource {

        val userAgent = "exoplayer-codelab"

        if (uri.getLastPathSegment()!!.contains("mp3") || uri.getLastPathSegment()!!
                .contains("mp4")
        ) {
            return ExtractorMediaSource.Factory(DefaultHttpDataSourceFactory(userAgent))
                .createMediaSource(uri)
        } else {
            return HlsMediaSource.Factory(DefaultHttpDataSourceFactory(userAgent))
                .createMediaSource(uri)
        }
    }

    private fun releasePlayer() {
        if (player != null) {
            playbackPosition = player!!.getCurrentPosition()
            currentWindow = player!!.getCurrentWindowIndex()
            playWhenReady = player!!.getPlayWhenReady()
            player!!.release()
            player = null
        }
    }

    override fun onStartVideo() {
    }

    override fun onSuccessVideo(uri: Uri) {
        initializePlayer(uri)
    }

    override fun onFailureVideo(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onStop() {
        releasePlayer()
        super.onStop()
    }


}