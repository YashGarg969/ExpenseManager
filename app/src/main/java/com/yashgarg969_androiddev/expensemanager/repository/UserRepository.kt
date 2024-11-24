package com.yashgarg969_androiddev.expensemanager.repository

import android.os.Build
import android.service.autofill.UserData
import android.util.Log
import android.view.SurfaceControl
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yashgarg969_androiddev.expensemanager.model.TransactionModel
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun saveUserData(userName: String, userEmail: String) {
        val uid = firebaseAuth.currentUser?.uid ?: return
        val userMap = mapOf(
            "userName" to userName,
            "userEmail" to userEmail
        )
        database.child("users").child(uid).setValue(userMap)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    suspend fun getUserData(): UserData? {
        val uid = firebaseAuth.currentUser?.uid ?: return null
        val userSnapshot = database.child("users").child(uid).get().await()
        return if (userSnapshot.exists())
            userSnapshot.getValue(UserData::class.java)
        else
            null

    }

    suspend fun addTransaction(transaction: TransactionModel): Result<Unit> {
        val uid = firebaseAuth.currentUser?.uid?:return Result.failure(Exception("User not logged in"))


        val transactionId = database.child("users").child(uid).child("transactions").push().key
            ?: return Result.failure(Exception("Failed to generate a unique transaction ID"))

        return try {
            val counterSnapshot = database.child("transactionCounter").get().await()
            val currentCounter = counterSnapshot.getValue(Int::class.java) ?: 0
            val newCounter = currentCounter + 1

            transaction.transactionNumber = newCounter

            database.child("users").child(uid).child("transactionCounter").setValue(newCounter).await()
            database.child("users").child(uid).child("transactions").child(transactionId)
                .setValue(transaction).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getTransactions(): LiveData<MutableList<TransactionModel>> {
        val transactionsList = MutableLiveData<MutableList<TransactionModel>>()

        val uid= firebaseAuth.currentUser?.uid?:return transactionsList

        database.child("users").child(uid).child("transactions")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val transactions = mutableListOf<TransactionModel>()
                    for (child in snapshot.children) {
                        val transaction = child.getValue(TransactionModel::class.java)
                        transaction?.let { transactions.add(transaction) }
                    }
                    transactionsList.postValue(transactions)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("Database Error-> Failed to retrieve transactions: ", error.message)
                }
            })
        return transactionsList
    }

    suspend fun deleteTransaction(transactionNumber: Int): Result<Unit> {
        val uid = firebaseAuth.currentUser?.uid?:return Result.failure(Exception("User not logged in"))

        val transactionsReference= database.child("users").child(uid).child("transactions")
        var transactionKey:String?= null
        return try {
            val snapshot = transactionsReference.get().await()
            var transactionKey: String? = null
            for (childSnapshot in snapshot.children) {
                val transaction = childSnapshot.getValue(TransactionModel::class.java)
                if (transaction?.transactionNumber == transactionNumber) {
                    transactionKey = childSnapshot.key
                    break
                }
            }
            if (transactionKey == null) {
                return Result.failure(Exception("Transaction with index $transactionNumber not found"))
            }
            transactionsReference.child(transactionKey).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getTransactions(filter:String): LiveData<MutableList<TransactionModel>> {
        val transactionsList = MutableLiveData<MutableList<TransactionModel>>()

        val uid= firebaseAuth.currentUser?.uid?:return transactionsList

        database.child("users").child(uid).child("transactions")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val transactions = mutableListOf<TransactionModel>()
                    for (child in snapshot.children) {
                        val transaction = child.getValue(TransactionModel::class.java)
                        transaction?.let {
                            // Filter based on `transactype` or `description` fields
                            if (filter.isBlank() || it.transactionType.contains(filter, ignoreCase = true) || it.description.contains(filter, ignoreCase = true)) {
                                transactions.add(it)
                            }
                        }
                    }
                    transactionsList.postValue(transactions)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("Database Error-> Failed to retrieve transactions: ", error.message)
                }
            })
        return transactionsList
    }

    suspend fun editTransaction(description:String, amount:String, transactionNumber:Int): Result<Boolean>
    {
        val uid = firebaseAuth.currentUser?.uid?:return Result.failure(Exception("User not logged in"))
        val transactionsReference= database.child("users").child(uid).child("transactions")
        var transactionId=""
        var success= false
        try {
            val snapshot = transactionsReference.get().await()
            for (childSnapshot in snapshot.children) {
                val transaction = childSnapshot.getValue(TransactionModel::class.java)
                if (transaction?.transactionNumber == transactionNumber) {
                    transactionId= childSnapshot.key.toString()
                    transaction.description= description
                    transaction.amount=amount
                    transactionsReference.child(transactionId).setValue(transaction)
                        .addOnSuccessListener{
                            success=true
                            Result.success(true)
                        }
                        .addOnFailureListener{
                            Result.failure<Exception>(it)
                        }
                    return Result.success(success)
                }
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
        return Result.success(success)

    }





}