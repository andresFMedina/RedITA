package com.itaeducativa.android.redita.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap

fun getExtension(uri: Uri, context: Context): String {
    val contentResolver: ContentResolver = context.contentResolver
    val mimeTypeMap: MimeTypeMap = MimeTypeMap.getSingleton()
    return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))!!
}