package com.yashgarg969_androiddev.expensemanager.ui

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProviders
import com.yashgarg969_androiddev.expensemanager.databinding.ActivityTransactionDetailBinding
import com.yashgarg969_androiddev.expensemanager.model.TransactionModel
import com.yashgarg969_androiddev.expensemanager.repository.UserRepository
import com.yashgarg969_androiddev.expensemanager.viewmodels.UserViewModel
import com.yashgarg969_androiddev.expensemanager.viewmodels.UserViewModelFactory

class TransactionDetailActivity : AppCompatActivity() {

    private lateinit var transactionDetailScreenBinding: ActivityTransactionDetailBinding
    private lateinit var userRepository: UserRepository
    private lateinit var userViewModelFactory: UserViewModelFactory
    private lateinit var userViewModel: UserViewModel


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        transactionDetailScreenBinding= ActivityTransactionDetailBinding.inflate(layoutInflater)
        setContentView(transactionDetailScreenBinding.root)

        userRepository= UserRepository()
        userViewModelFactory= UserViewModelFactory(userRepository)

        userViewModel= ViewModelProviders.of(this, userViewModelFactory).get(UserViewModel::class.java)

        initViews()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun initViews()
    {
        val transaction:TransactionModel?= intent.getParcelableExtra("transaction",TransactionModel::class.java)
        if(transaction!=null)
        {
            transactionDetailScreenBinding.descInput.text= transaction.description
            transactionDetailScreenBinding.amountInput.text= transaction.amount
        }

        transactionDetailScreenBinding.backBtn.setOnClickListener {
            finish()
        }

        transactionDetailScreenBinding.deleteBtn.setOnClickListener {
            if(transaction!=null)
            {
                deleteTransaction(transaction.transactionNumber)
            }
            else
                transactionDetailScreenBinding.deleteBtn.isActivated=false
        }

        transactionDetailScreenBinding.editBtn.setOnClickListener {
            editTransaction(transaction!!)
        }
    }

    private fun deleteTransaction(transactionNumber:Int)
    {
        userViewModel.deleteTransaction(transactionNumber)
        userViewModel.transactionDeletionStatus.observe(this)
        {
            success->
            if(success.contentEquals("Transaction Deleted Successfully",true)) {
                Toast.makeText(this, "Details Deleted Successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            else
                Toast.makeText(this, "Some error occurred while trying to delete it", Toast.LENGTH_SHORT).show()
        }
    }

    private fun editTransaction(transaction:TransactionModel)
    {
        userViewModel.editTransactionDetails("","",transaction.transactionNumber)
        userViewModel.editTransactionStatus.observe(this)
        {
            success->
            if(success)
                Toast.makeText(this,"Details Updated Successfully",Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(this,"Some error occurred while updating the details",Toast.LENGTH_SHORT).show()
        }
    }
}