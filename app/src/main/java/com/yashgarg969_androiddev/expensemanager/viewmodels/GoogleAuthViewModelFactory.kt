package com.yashgarg969_androiddev.expensemanager.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yashgarg969_androiddev.expensemanager.repository.GoogleAuthRepository

class GoogleAuthViewModelFactory(private val googleAuthRepository: GoogleAuthRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(GoogleAuthViewModel::class.java))
            {
                return GoogleAuthViewModel(googleAuthRepository) as T
            }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}