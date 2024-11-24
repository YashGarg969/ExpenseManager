package com.yashgarg969_androiddev.expensemanager.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yashgarg969_androiddev.expensemanager.databinding.TransactionItemLayoutBinding
import com.yashgarg969_androiddev.expensemanager.model.TransactionModel

class TransactionsAdapter(
    val transactions: MutableList<TransactionModel>,
    private val onItemClick: (TransactionModel) -> Unit,
    val onDeleteClick: (TransactionModel) -> Unit
) : RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(private val binding: TransactionItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: TransactionModel) {
            binding.amountTextView.text = transaction.amount
            binding.descriptionTextView.text = transaction.description
            binding.transactionTypeTextView.text = transaction.transactionType
            binding.transactionDateTextView.text = transaction.date

            binding.root.setOnClickListener {
                onItemClick(transaction)
            }
            binding.swipeDeleteBtn.setOnClickListener {
                onDeleteClick(transaction)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = TransactionItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        if (position in transactions.indices) {
            holder.bind(transactions[position])
        }
    }

    fun removeItem(position: Int) {
        if (position in transactions.indices) {
            transactions.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, transactions.size - position)
        } else {
            println("Invalid position: $position. List size: ${transactions.size}")
        }
    }

    fun setData(newTransactions: List<TransactionModel>) {
        transactions.clear()
        transactions.addAll(newTransactions)
        notifyDataSetChanged()
    }
}
