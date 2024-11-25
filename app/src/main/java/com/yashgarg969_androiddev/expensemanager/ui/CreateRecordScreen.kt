package com.yashgarg969_androiddev.expensemanager.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.datepicker.MaterialDatePicker
import com.yashgarg969_androiddev.expensemanager.R
import com.yashgarg969_androiddev.expensemanager.databinding.ActivityCreateRecordScreenBinding
import com.yashgarg969_androiddev.expensemanager.model.TransactionModel
import com.yashgarg969_androiddev.expensemanager.repository.UserRepository
import com.yashgarg969_androiddev.expensemanager.viewmodels.UserViewModel
import com.yashgarg969_androiddev.expensemanager.viewmodels.UserViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class CreateRecordScreen : AppCompatActivity() {

    private lateinit var createRecordScreenBinding: ActivityCreateRecordScreenBinding
    private var transactionType:String=""

    private lateinit var userRepository: UserRepository

    private lateinit var userViewModelFactory: UserViewModelFactory


    private lateinit var userViewModel: UserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createRecordScreenBinding= ActivityCreateRecordScreenBinding.inflate(layoutInflater)
        setContentView(createRecordScreenBinding.root)

        userRepository= UserRepository()
        userViewModelFactory = UserViewModelFactory(userRepository)
        userViewModel=  ViewModelProviders.of(this,userViewModelFactory).get(UserViewModel::class.java)
        initViews()
    }

    private fun initViews()
    {
        createRecordScreenBinding.dateTextView.setOnClickListener( View.OnClickListener {
            val date:String= selectDateFromCalendar()
            createRecordScreenBinding.dateTextView.text = date
        })

        createRecordScreenBinding.radioGroup.setOnCheckedChangeListener { radioGrp_, checkedId ->
            if (checkedId == R.id.radioIncome) {
                Toast.makeText(this, "Income Selected", Toast.LENGTH_SHORT).show()
                transactionType= "Income"
            } else if (checkedId == R.id.radioExpense) {
                Toast.makeText(this, "Expense Selected", Toast.LENGTH_SHORT).show()
                transactionType= "Expense"
            }
        }

        createRecordScreenBinding.SaveBtn.setOnClickListener {
            saveDetailsToDatabase()
            observeViewModel()
        }

        createRecordScreenBinding.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun saveDetailsToDatabase() {
        val transactionDate= createRecordScreenBinding.dateTextView.text.toString()
        val amount= createRecordScreenBinding.amountInput.text.toString()
        val description= createRecordScreenBinding.descInput.text.toString()

        if(transactionDate.trim().isEmpty())
            createRecordScreenBinding.dateTextView.error= "Date cannot be empty"

        if(amount.trim().isEmpty())
            createRecordScreenBinding.amountInput.error= "Amount cannot be empty"

        if(transactionType.trim().isEmpty())
            Toast.makeText(this,"Please select transaction type", Toast.LENGTH_SHORT).show()

        if(transactionDate.trim().isNotEmpty() && amount.trim().isNotEmpty() && transactionType.trim()
                .isNotEmpty())
        {
            val transaction:TransactionModel= TransactionModel(0,transactionDate,transactionType,amount,description)
            userViewModel.addTransaction(transaction)
        }
    }

    private fun observeViewModel()
    {
        userViewModel.transactionStatus.observe(this)
        {success->
            if(success)
            {
                Toast.makeText(applicationContext, "Transaction Added Successfully", Toast.LENGTH_LONG).show()
                finish()
            }
            else
            {
                Toast.makeText(applicationContext, "Transaction Added Successfully", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun selectDateFromCalendar(): String {

        var date: String = ""
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
            .build()

        datePicker.show(supportFragmentManager, "tag")

        datePicker
            .addOnNegativeButtonClickListener() {
                datePicker.dismiss()
            }
        datePicker
            .addOnPositiveButtonClickListener {
                date = SimpleDateFormat("dd-MM-yyy", Locale.getDefault()).format(Date(it))
                println("Date is "+ date)
                createRecordScreenBinding.dateTextView.text=date
            }
        return date
    }

}