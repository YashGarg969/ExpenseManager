package com.yashgarg969_androiddev.expensemanager.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.yashgarg969_androiddev.expensemanager.R
import com.yashgarg969_androiddev.expensemanager.adapters.TransactionsAdapter
import com.yashgarg969_androiddev.expensemanager.callbacks.SwipeDeletionCallback
import com.yashgarg969_androiddev.expensemanager.databinding.ActivityDashboardScreenBinding
import com.yashgarg969_androiddev.expensemanager.model.TransactionModel
import com.yashgarg969_androiddev.expensemanager.repository.UserRepository
import com.yashgarg969_androiddev.expensemanager.viewmodels.UserViewModel
import com.yashgarg969_androiddev.expensemanager.viewmodels.UserViewModelFactory

class DashboardScreen : AppCompatActivity() {

    private lateinit var dashboardScreenBinding: ActivityDashboardScreenBinding
    private lateinit var userRepository: UserRepository
    private lateinit var userViewModelFactory: UserViewModelFactory
    private lateinit var userViewModel: UserViewModel
    private lateinit var transactionAdapter:TransactionsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dashboardScreenBinding= ActivityDashboardScreenBinding.inflate(layoutInflater)
        setContentView(dashboardScreenBinding.root)

        userRepository= UserRepository()
        userViewModelFactory= UserViewModelFactory(userRepository)
        userViewModel= ViewModelProviders.of(this,userViewModelFactory).get(UserViewModel::class.java)
        initViews()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun initViews()
    {
        observeLiveTransactions()

        dashboardScreenBinding.searchInput.setOnEditorActionListener { v, actionId, event ->

            if(actionId==EditorInfo.IME_ACTION_SEARCH)
            {
                userViewModel.getFilteredResults(dashboardScreenBinding.searchInput.text.toString());
                userViewModel.filteredTransactionList.observe(this)
                {
                    updateRecyclerView(it)
                }
                true
            }
            else
                false
        }


        // Observe transactions from ViewModel
        userViewModel.transactionsList.observe(this) { transactions ->
            setupRecyclerView(transactions)
            updateRecyclerView(transactions)
        }

        dashboardScreenBinding.addTransactionBtn.setOnClickListener {
            val intent:Intent= Intent(this,CreateRecordScreen::class.java)
            startActivity(intent)
        }
    }

    private fun observeLiveTransactions()
    {
        userViewModel.transactionsList.observe(this)
        {
            transactions->
        }
    }

    private fun setupRecyclerView(transactions: MutableList<TransactionModel>)
    {
        transactionAdapter= TransactionsAdapter(transactions , onItemClick = { transaction ->
            val intent = Intent(this, TransactionDetailActivity::class.java).apply {
                putExtra("transaction", transaction) // Pass transaction
            }
            startActivity(intent)
        },
            onDeleteClick = {
                println(it.toString())
            })
        dashboardScreenBinding.recView.apply {
            layoutManager = LinearLayoutManager(this@DashboardScreen)
            adapter = transactionAdapter
        }

        val itemTouchHelper= ItemTouchHelper(SwipeDeletionCallback(transactionAdapter, userViewModel))
        itemTouchHelper.attachToRecyclerView(dashboardScreenBinding.recView)
    }
    private fun updateRecyclerView(transactions: MutableList<TransactionModel>) {
        transactionAdapter = TransactionsAdapter(transactions, onItemClick = { transaction ->

            val intent = Intent(this, TransactionDetailActivity::class.java).apply {
                putExtra("transaction", transaction)
            }
            startActivity(intent)
        },
            onDeleteClick = {
            })
        dashboardScreenBinding.recView.adapter = transactionAdapter
    }
}