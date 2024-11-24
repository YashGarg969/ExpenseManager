package com.yashgarg969_androiddev.expensemanager.callbacks

import android.graphics.Canvas
import android.opengl.Visibility
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.yashgarg969_androiddev.expensemanager.R
import com.yashgarg969_androiddev.expensemanager.adapters.TransactionsAdapter
import com.yashgarg969_androiddev.expensemanager.viewmodels.UserViewModel

class SwipeDeletionCallback(private val adapter: TransactionsAdapter, private val viewModel:UserViewModel) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val deleteIcon = itemView.findViewById<ImageButton>(R.id.swipeDeleteBtn)

        if (dX < 0) {
            deleteIcon.visibility = View.VISIBLE
        } else {
            deleteIcon.visibility = View.GONE
        }

        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

        val position= viewHolder.adapterPosition
        println("Position is "+ position)
        println("Size"+  adapter.transactions.size)

        if (position >= 0 && position < adapter.transactions.size) {

            println("Transaction dataa is "+ adapter.transactions[position].toString())

            val itemView = viewHolder.itemView
            val deleteBtn = itemView.findViewById<ImageButton>(R.id.swipeDeleteBtn)
            deleteBtn.visibility = View.VISIBLE
            val transaction = adapter.transactions[position]

            println("Transaction number is "+ transaction.transactionNumber)

            viewModel.deleteTransaction(transaction.transactionNumber)
            adapter.onDeleteClick(transaction)
            adapter.removeItem(position)
        } else {
            adapter.notifyItemChanged(position)
        }
    }
}