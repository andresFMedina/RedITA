package com.itaeducativa.android.redita.data.modelos

data class MediaObject(
    var title: String,
    var mediaUrl: String,
    var thumbnail: String,
    var description: String
) {
    constructor():this("", "","","")
}