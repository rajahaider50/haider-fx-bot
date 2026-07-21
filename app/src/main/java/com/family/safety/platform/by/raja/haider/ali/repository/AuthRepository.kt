package com.family.safety.platform.by.raja.haider.ali.repository

import com.family.safety.platform.by.raja.haider.ali.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

    val currentUser: FirebaseUser? get() = auth.currentUser
    val isLoggedIn: Boolean get() = currentUser != null

    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.let {
                db.child("users").child(it.uid).child("lastSeen")
                    .setValue(System.currentTimeMillis()).await()
            }
            Result.success(auth.currentUser!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(name: String, email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user!!
            val userData = User(
                uid = user.uid,
                name = name,
                email = email,
                role = "parent",
                createdAt = System.currentTimeMillis(),
                lastSeen = System.currentTimeMillis(),
                isActive = true
            )
            db.child("users").child(user.uid).setValue(userData).await()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun forgotPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendEmailVerification(): Result<Unit> {
        return try {
            currentUser?.sendEmailVerification()?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun getUserDataFlow(uid: String): Flow<User?> = callbackFlow {
        val ref = db.child("users").child(uid)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(User::class.java))
            }
            override fun onCancelled(error: DatabaseError) {
                trySend(null)
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun updateUser(uid: String, updates: Map<String, Any>) {
        db.child("users").child(uid).updateChildren(updates).await()
    }

    suspend fun updateFcmToken(uid: String, token: String) {
        db.child("users").child(uid).child("fcmToken").setValue(token).await()
    }
}
