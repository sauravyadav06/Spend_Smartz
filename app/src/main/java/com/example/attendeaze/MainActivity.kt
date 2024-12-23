package com.example.attendeaze

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {

    private lateinit var btnIncome: ImageButton
    private lateinit var btnOutcome: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var incomeExpenseDatabase: IncomeExpenseDatabase
    private lateinit var tvIncomeValue: TextView
    private lateinit var tvExpenseValue: TextView
    private lateinit var tvTotalBalance: TextView // TextView for total balance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Enable edge-to-edge display
        enableEdgeToEdge()

        // Set up window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize buttons
        btnIncome = findViewById(R.id.income)
        btnOutcome = findViewById(R.id.expense)
        tvIncomeValue = findViewById(R.id.tv_income_value)
        tvExpenseValue = findViewById(R.id.tv_expense_value) // Add TextView for expense
        tvTotalBalance = findViewById(R.id.tv_total_balance) // TextView for total balance

        // Set up click listeners for navigating to Income and Expense activities
        btnIncome.setOnClickListener {
            val intent = Intent(this, IncomeActivity::class.java)
            startActivity(intent)
        }

        btnOutcome.setOnClickListener {
            val intent = Intent(this, ExpenseActivity::class.java)
            startActivity(intent)
        }

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the database helper
        incomeExpenseDatabase = IncomeExpenseDatabase(this)

        // Fetch and set transactions data to the RecyclerView
        loadTransactions()

        // Fetch and display total income, expense, and total balance
        displayTotalIncome()
        displayTotalExpense()
        displayTotalBalance()
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

    private fun displayTotalIncome() {
        // Fetch total income from the database
        val totalIncome = incomeExpenseDatabase.getTotalIncome()

        // Display the total income in the TextView
        tvIncomeValue.text = "₹${totalIncome}"
    }

    private fun displayTotalExpense() {
        // Fetch total expense from the database
        val totalExpense = incomeExpenseDatabase.getTotalExpense()

        // Display the total expense in the TextView
        tvExpenseValue.text = "₹${totalExpense}"
    }

    private fun displayTotalBalance() {
        // Fetch total income and total expense
        val totalIncome = incomeExpenseDatabase.getTotalIncome()
        val totalExpense = incomeExpenseDatabase.getTotalExpense()

        // Calculate the balance (income - expense)
        val totalBalance = totalIncome - totalExpense

        // Display the total balance in the TextView
        tvTotalBalance.text = "₹${totalBalance}"
    }
}
