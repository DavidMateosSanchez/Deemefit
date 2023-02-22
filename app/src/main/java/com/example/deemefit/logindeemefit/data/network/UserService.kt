package com.example.deemefit.logindeemefit.data.network

import com.example.deemefit.logindeemefit.ui.signin.model.UserSignIn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserService @Inject constructor(private val firebase: FirebaseClient) {

    companion object {
        const val USER_COLLECTION = "Usuarios"
    }

    suspend fun createUserTable(userSignIn: UserSignIn) = kotlin.runCatching {

        val user = hashMapOf(
            "email" to userSignIn.email
        )

        firebase.db.collection(USER_COLLECTION).document(userSignIn.email).set(user).await()
    }.isSuccess
}