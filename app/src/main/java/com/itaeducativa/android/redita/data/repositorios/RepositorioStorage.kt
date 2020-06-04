package com.itaeducativa.android.redita.data.repositorios

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.google.firebase.storage.FirebaseStorage
import com.itaeducativa.android.redita.data.firebase.FirebaseSource
import com.itaeducativa.android.redita.network.GlideApp
import de.hdodenhof.circleimageview.CircleImageView

class RepositorioStorage (){
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