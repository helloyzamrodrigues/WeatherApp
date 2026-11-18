package com.weatherapp.db.fb

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore

class FBDatabase {

    interface Listener {
        fun onUserLoaded(user: FBUser)
        fun onUserSignOut()
        fun onCityAdded(city: FBCity)
        fun onCityUpdated(city: FBCity)
        fun onCityRemoved(city: FBCity)
    }

    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private var citiesListReg: ListenerRegistration? = null
    private var listener: Listener? = null

    fun setListener(listener: Listener) {
        this.listener = listener
        startListening()
    }

    init {
        auth.addAuthStateListener { auth ->
            if (auth.currentUser == null) {
                citiesListReg?.remove()
                citiesListReg = null
                listener?.onUserSignOut()
            } else {
                startListening()
            }
        }
    }

    private fun startListening() {
        val currentUser = auth.currentUser ?: return
        val currentListener = listener ?: return

        citiesListReg?.remove()

        val refCurrUser = db.collection("users").document(currentUser.uid)
        refCurrUser.get().addOnSuccessListener { snapshot ->
            val fbUser = snapshot.toObject(FBUser::class.java)
            if (fbUser != null && (!fbUser.name.isNullOrBlank() || !fbUser.email.isNullOrBlank())) {
                currentListener.onUserLoaded(fbUser)
            } else {
                val newUser = FBUser().apply {
                    this.name = currentUser.displayName ?: currentUser.email?.substringBefore("@") ?: ""
                    this.email = currentUser.email ?: ""
                }
                refCurrUser.set(newUser)
                currentListener.onUserLoaded(newUser)
            }
        }

        citiesListReg = refCurrUser.collection("cities")
            .addSnapshotListener { snapshots, ex ->
                if (ex != null) return@addSnapshotListener
                snapshots?.documentChanges?.forEach { change ->
                    val fbCity = change.document.toObject(FBCity::class.java)
                    when (change.type) {
                        DocumentChange.Type.ADDED -> listener?.onCityAdded(fbCity)
                        DocumentChange.Type.MODIFIED -> listener?.onCityUpdated(fbCity)
                        DocumentChange.Type.REMOVED -> listener?.onCityRemoved(fbCity)
                    }
                }
            }
    }

    fun register(user: FBUser) {
        if (auth.currentUser == null) return
        val uid = auth.currentUser!!.uid
        db.collection("users").document(uid).set(user)
    }

    fun add(city: FBCity) {
        if (auth.currentUser == null) return
        if (city.name == null) return
        val uid = auth.currentUser!!.uid
        db.collection("users").document(uid).collection("cities").document(city.name!!).set(city)
    }

    fun remove(city: FBCity) {
        if (auth.currentUser == null) return
        if (city.name == null) return
        val uid = auth.currentUser!!.uid
        db.collection("users").document(uid).collection("cities").document(city.name!!).delete()
    }
}
