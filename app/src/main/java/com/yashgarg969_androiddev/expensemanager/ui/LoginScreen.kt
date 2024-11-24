package com.yashgarg969_androiddev.expensemanager.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.database.FirebaseDatabase
import com.yashgarg969_androiddev.expensemanager.R
import com.yashgarg969_androiddev.expensemanager.databinding.ActivityLoginScreenBinding
import com.yashgarg969_androiddev.expensemanager.repository.GoogleAuthRepository
import com.yashgarg969_androiddev.expensemanager.viewmodels.GoogleAuthViewModel
import com.yashgarg969_androiddev.expensemanager.viewmodels.GoogleAuthViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginScreen : AppCompatActivity() {

    private lateinit var loginScreenBinding: ActivityLoginScreenBinding
    private lateinit var googleAuthRepository:GoogleAuthRepository
    private lateinit var googleAuthViewModelFactory: GoogleAuthViewModelFactory
    private lateinit var googleAuthViewModel: GoogleAuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginScreenBinding= ActivityLoginScreenBinding.inflate(layoutInflater)

        googleAuthRepository= GoogleAuthRepository(this)
        googleAuthViewModelFactory= GoogleAuthViewModelFactory(googleAuthRepository = googleAuthRepository)

        googleAuthViewModel= ViewModelProvider(this,googleAuthViewModelFactory)[GoogleAuthViewModel::class.java]

        if(googleAuthRepository.isSignedIn())
        {
            val intent: Intent = Intent(applicationContext, DashboardScreen::class.java)
            startActivity(intent)
        }
        else
        {
            setContentView(loginScreenBinding.root)
            initViews()
        }
    }

    private fun initViews()
    {
        loginScreenBinding
            .signInBtn.setOnClickListener{
                login()

            }


    }
    private fun login()
    {
        googleAuthViewModel.signIn()
        googleAuthViewModel.newSignInStatus.observe(this)
        {
            success->
            if(success)
            {
                Toast.makeText(this,"Signed-In Successfully", Toast.LENGTH_SHORT).show()
                val intent: Intent = Intent(applicationContext, DashboardScreen::class.java)
                startActivity(intent)
                finish()
            }
            else
            {
                Toast.makeText(this,"Some error occurred while signing you in", Toast.LENGTH_SHORT).show()
            }
        }
    }
}