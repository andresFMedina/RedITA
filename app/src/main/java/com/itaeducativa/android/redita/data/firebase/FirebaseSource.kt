package com.itaeducativa.android.redita.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage

class FirebaseSource {

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    val firestoreDB: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    val firebaseStorage: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }

    val firebaseInstanceId: FirebaseInstanceId by lazy {
        FirebaseInstanceId.getInstance()
    }

    val firebaseMessaging: FirebaseMessaging by lazy {
        FirebaseMessaging.getInstance()
    }

    fun login(email: String, password: String) =
        firebaseAuth.signInWithEmailAndPassword(email, password)

    fun register(email: String, password: String) =
        firebaseAuth.createUserWithEmailAndPassword(email, password)

    fun logout() = firebaseAuth.signOut()

    fun currentUser() = firebaseAuth.currentUser
}