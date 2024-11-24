package com.yashgarg969_androiddev.expensemanager.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransactionModel(
    var transactionNumber: Int=0,
    val date: String="",
    val transactionType: String="",
    var amount: String="",
    var description: String=""
) : Parcelable
