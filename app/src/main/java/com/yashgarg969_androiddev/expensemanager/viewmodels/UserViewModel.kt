package com.yashgarg969_androiddev.expensemanager.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yashgarg969_androiddev.expensemanager.model.TransactionModel
import com.yashgarg969_androiddev.expensemanager.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(val userRepository: UserRepository): ViewModel() {

    private val _transactionStatus= MutableLiveData<Boolean>()
    val transactionStatus: LiveData<Boolean> get() = _transactionStatus

    private val _editTransactionStatus= MutableLiveData<Boolean>()
    val editTransactionStatus: LiveData<Boolean> get() = _editTransactionStatus

    private val _transactionDeletionStatus= MutableLiveData<String>()
    val transactionDeletionStatus: LiveData<String> get()= _transactionDeletionStatus


    var transactionsList: LiveData<MutableList<TransactionModel>> = userRepository.getTransactions()

    lateinit var filteredTransactionList: LiveData<MutableList<TransactionModel>>


    fun addTransaction(transaction:TransactionModel)
    {
        viewModelScope.launch {
            try {
                val result= userRepository.addTransaction(transaction = transaction)
                result.onSuccess {
                    _transactionStatus.postValue(true)
                }
                    .onFailure { exception->
                        _transactionStatus.postValue(false)
                    }
            }
            catch (e:Exception)
            {
                e.printStackTrace()
                _transactionStatus.postValue(false)
            }
        }
    }

    fun deleteTransaction(transactionNumber:Int)
    {
        viewModelScope.launch {
            val result= userRepository.deleteTransaction(transactionNumber)
            result.onSuccess {
                _transactionDeletionStatus.postValue("Transaction Deleted Successfully")
            }
                .onFailure {
                    _transactionDeletionStatus.postValue("Error: ${it.message}")
                }
        }
    }

    fun getFilteredResults(filter:String)
    {
        filteredTransactionList = userRepository.getTransactions(filter)
    }

    fun editTransactionDetails(desc:String, amount:String, transactionNumber:Int)
    {
        viewModelScope.launch {
            val result= userRepository.editTransaction(desc,amount, transactionNumber)
            result.onSuccess {
                _editTransactionStatus.postValue(true)
            }
                .onFailure {
                    _editTransactionStatus.postValue(false)
                }
        }
    }
}