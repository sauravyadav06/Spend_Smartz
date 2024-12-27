package com.example.attendeaze

import android.database.Cursor
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TransactionListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var incomeExpenseDatabase: IncomeExpenseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_list)

        // Initialize RecyclerView and database
        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)

        incomeExpenseDatabase = IncomeExpenseDatabase(this)

        // Fetch transactions and set them in RecyclerView
        loadTransactions()
    }

    private fun loadTransactions() {
        // Fetch the transactions (income and expenses)
        val transactions = getTransactions()

        // Set up the adapter with the fetched transactions
        transactionAdapter = TransactionAdapter(transactions)
        recyclerView.adapter = transactionAdapter
    }

    private fun getTransactions(): List<Transaction> {
        val transactionList = mutableListOf<Transaction>()

        // Fetch income data from the database
        val incomeCursor = incomeExpenseDatabase.getAllIncome()
        if (incomeCursor.moveToFirst()) {
            do {
                val amount = incomeCursor.getDouble(incomeCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INCOME_AMOUNT))
                val category = incomeCursor.getString(incomeCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INCOME_CATEGORY))
                val description = incomeCursor.getString(incomeCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INCOME_DESCRIPTION))
                val date = incomeCursor.getString(incomeCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INCOME_DATE))

                // Add Income transactions with the type "Income"
                transactionList.add(Transaction(0, amount, category, description, date, "Income"))
            } while (incomeCursor.moveToNext())
        }
        incomeCursor.close()

        // Fetch expense data from the database
        val expenseCursor = incomeExpenseDatabase.getAllExpenses()
        if (expenseCursor.moveToFirst()) {
            do {
                val amount = expenseCursor.getDouble(expenseCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_AMOUNT))
                val category = expenseCursor.getString(expenseCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_CATEGORY))
                val description = expenseCursor.getString(expenseCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_DESCRIPTION))
                val date = expenseCursor.getString(expenseCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_DATE))

                // Add Expense transactions with the type "Expense"
                transactionList.add(Transaction(0, amount, category, description, date, "Expense"))
            } while (expenseCursor.moveToNext())
        }
        expenseCursor.close()

        return transactionList
    }
}

