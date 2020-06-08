package com.itaeducativa.android.redita.data.repositorios

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.itaeducativa.android.redita.data.firebase.FirebaseSource
import com.itaeducativa.android.redita.network.GlideApp
import de.hdodenhof.circleimageview.CircleImageView

class RepositorioStorage() {
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    fun subirFotoStorage(reference: String, imagenUri: Uri) =
        storage.reference.child(reference).putFile(imagenUri)

    companion object DataBindingAdapter {
        private val storage: FirebaseStorage = FirebaseStorage.getInstance()

        @BindingAdapter("bind:imageUrl")
        @JvmStatic
        fun getStorageReferenceCircleImageView(imageView: CircleImageView, url: String) {
            val storageReference = storage.getReferenceFromUrl(url)
            GlideApp.with(imageView)
                .load(storageReference)
                .into(imageView)
        }

        @BindingAdapter("bind:imageUrl")
        @JvmStatic
        fun getStorageReferenceImageView(imageView: ImageView, url: String?) {
            if (url != null && url != "") {
                val storageReference = storage.getReferenceFromUrl(url)
                GlideApp.with(imageView)
                    .load(storageReference)
                    .into(imageView)
            }
        }
    }
}