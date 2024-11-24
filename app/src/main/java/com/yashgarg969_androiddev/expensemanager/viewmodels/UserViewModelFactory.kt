package com.yashgarg969_androiddev.expensemanager.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yashgarg969_androiddev.expensemanager.repository.UserRepository

class UserViewModelFactory(private val userRepository: UserRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(UserViewModel::class.java))
            return UserViewModel(userRepository) as T
        throw IllegalArgumentException("Unable to create user view model")
    }

}