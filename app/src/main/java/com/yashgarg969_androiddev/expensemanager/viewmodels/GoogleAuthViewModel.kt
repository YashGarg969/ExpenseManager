package com.yashgarg969_androiddev.expensemanager.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yashgarg969_androiddev.expensemanager.repository.GoogleAuthRepository
import kotlinx.coroutines.launch

class GoogleAuthViewModel(private val googleAuthRepository: GoogleAuthRepository) : ViewModel() {

    val signInStatus: LiveData<Boolean> = googleAuthRepository.signInStatus

    private val _newSignInStatus= MutableLiveData<Boolean>()
    val newSignInStatus: LiveData<Boolean> get() = _newSignInStatus

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error
//    val isSignedIn: LiveData<Boolean> get()= _isSignedIn

    fun checkUserSignedIn()
    {}

    fun signIn():Boolean
    {
        var success:Boolean= false
        viewModelScope.launch {
            try {
                val result= googleAuthRepository.signIn()
                result
                    .onSuccess {
                        _newSignInStatus.postValue(true)
                    }
                    .onFailure {
                        _newSignInStatus.postValue(false)
                    }
            }
            catch (e:Exception)
            {
                _newSignInStatus.postValue(false)
            }
        }
        return success
    }

    fun signOut()
    {
        viewModelScope.launch {
            try {
                googleAuthRepository.signOut()
            }
            catch (e:Exception)
            {
                _error.value= e.message
            }
        }
    }

}