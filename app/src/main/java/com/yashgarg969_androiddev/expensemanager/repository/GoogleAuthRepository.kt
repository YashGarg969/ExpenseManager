package com.yashgarg969_androiddev.expensemanager.repository

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.yashgarg969_androiddev.expensemanager.R
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class GoogleAuthRepository(private val context: Context) {

    private val credentialManager = CredentialManager.create(context)
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userRepository= UserRepository()

    private val _signInStatus = MutableLiveData<Boolean>()
    val signInStatus: LiveData<Boolean> get() = _signInStatus

    fun isSignedIn(): Boolean {
        return firebaseAuth.currentUser!=null
    }

    suspend fun signIn(): Result<Unit> {

        if (isSignedIn()){
            _signInStatus.postValue(true)
            Result.success(true)
        }
        return try {
            val result = buildCredentialRequest()
            val success= handleSignIn(result)
            _signInStatus.postValue(success)
            Result.success(Unit)

        } catch (e: Exception) {
            e.printStackTrace()
            _signInStatus.postValue(false)
            if (e is CancellationException)
                throw e
            return Result.failure(e)
        }
    }

    private suspend fun buildCredentialRequest(): GetCredentialResponse {
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(
                GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(R.string.web_client_id.toString())
                    .setAutoSelectEnabled(false)
                    .build()
            )
            .build()
        return credentialManager.getCredential(request = request, context = context)
    }

    suspend fun signOut() {
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
        firebaseAuth.signOut()
        _signInStatus.postValue(false)
    }

    private suspend fun handleSignIn(response: GetCredentialResponse): Boolean {
        val credential = response.credential
        (if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            return try {
                val tokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                val authCredential = GoogleAuthProvider.getCredential(tokenCredential.idToken, null)

                val authResult = firebaseAuth.signInWithCredential(authCredential).await()
                authResult.user!!.email?.let {
                    authResult!!.user!!.displayName?.let { it1 ->
                        userRepository.saveUserData(
                            it1,
                            it
                        )
                    }
                }
                authResult.user != null

            } catch (e: GoogleIdTokenParsingException) {
                Log.d("GoogleIdTokenParsingError", e.message.toString())
                false;
            }
        } else {
            Log.d("SignIn Error", "Credential is not GoogleIdTokenCredential")
            return false
        })
    }
}