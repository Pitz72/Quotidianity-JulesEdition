package com.quotidianity.auth

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.api.services.calendar.CalendarScopes
import com.quotidianity.data.User
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

class GoogleAuthUiClient(
    private val context: Context,
    private val signInClient: GoogleSignInClient
) {

    suspend fun signIn(): Intent {
        return signInClient.signInIntent
    }

    suspend fun signInWithIntent(intent: Intent): User? {
        val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
        return try {
            val account = task.getResult(ApiException::class.java)
            account?.toUser()
        } catch (e: ApiException) {
            e.printStackTrace()
            null
        }
    }

    suspend fun signOut() {
        try {
            signInClient.signOut().await()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    fun getSignedInUser(): User? {
        return GoogleSignIn.getLastSignedInAccount(context)?.toUser()
    }

    private fun GoogleSignInAccount.toUser(): User {
        return User(
            id = this.id!!,
            name = this.displayName ?: "No Name",
            email = this.email!!,
            profilePictureUrl = this.photoUrl?.toString()
        )
    }

    companion object {
        // TODO: Replace this with your Web client ID from Google Cloud Console
        private const val WEB_CLIENT_ID = "YOUR_WEB_CLIENT_ID"

        fun create(context: Context): GoogleAuthUiClient {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestIdToken(WEB_CLIENT_ID)
                .requestScopes(Scope(CalendarScopes.CALENDAR))
                .build()
            val client = GoogleSignIn.getClient(context, gso)
            return GoogleAuthUiClient(context, client)
        }
    }
}
