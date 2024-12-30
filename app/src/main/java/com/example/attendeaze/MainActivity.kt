package com.example.attendeaze

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var btnIncome: ImageButton
    private lateinit var btnOutcome: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var tvIncomeValue: TextView
    private lateinit var tvExpenseValue: TextView
    private lateinit var tvTotalBalance: TextView
    private lateinit var seeAll: TextView
    private lateinit var analysis: ImageButton
    private lateinit var tvUserName: TextView
    private val sharedPreferencesName = "UserDetails"
    private val userNameKey = "user_name"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check if user details are already saved
        val sharedPreferences = getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString(userNameKey, null)

        if (userName == null) {
            // Redirect to UserInfoActivity if no details are saved
            val intent = Intent(this, UserInfoActivity::class.java)
            startActivity(intent)
            finish() // Close MainActivity to prevent back navigation
            return
        }

        // Initialize views
        tvUserName = findViewById(R.id.tv_user_name)
        btnIncome = findViewById(R.id.income)
        btnOutcome = findViewById(R.id.expense)
        tvIncomeValue = findViewById(R.id.tv_income_value)
        tvExpenseValue = findViewById(R.id.tv_expense_value)
        tvTotalBalance = findViewById(R.id.tv_total_balance)
        seeAll = findViewById(R.id.tv_see_all)
        analysis = findViewById(R.id.btn_analytics)
        recyclerView = findViewById(R.id.recyclerview)

        // Display the user name
        tvUserName.text = "Welcome, $userName!"

        // Set click listeners for buttons
        btnIncome.setOnClickListener { startActivity(Intent(this, IncomeActivity::class.java)) }
        btnOutcome.setOnClickListener { startActivity(Intent(this, ExpenseActivity::class.java)) }
        seeAll.setOnClickListener { startActivity(Intent(this, SeeAllActivity::class.java)) }
        analysis.setOnClickListener { startActivity(Intent(this, AnalyticsActivity::class.java)) }

        // Initialize database helper and adapter
        dbHelper = DatabaseHelper(this)
        transactionAdapter = TransactionAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = transactionAdapter

        // Load data
        loadTransactions()
        displayTotalIncome()
        displayTotalExpense()
        displayTotalBalance()
    }

    override fun onResume() {
        super.onResume()
        loadTransactions()
        displayTotalIncome()
        displayTotalExpense()
        displayTotalBalance()
    }

    private fun loadTransactions() {
        val transactions = fetchLatestTransactions()
        transactionAdapter.updateTransactions(transactions)
    }

    private fun fetchLatestTransactions(): List<Transaction> {
        val transactions = mutableListOf<Transaction>()

        // Fetch income and expense transactions
        dbHelper.readableDatabase.use { db ->
            db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_INCOME}", null).use { cursor ->
                while (cursor.moveToNext()) {
                    transactions.add(
                        Transaction(
                            id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INCOME_ID)).toString(),
                            amount = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INCOME_AMOUNT)),
                            category = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INCOME_CATEGORY)),
                            description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INCOME_DESCRIPTION)),
                            date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INCOME_DATE)),
                            time = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INCOME_TIME)),
                            type = "Income"
                        )
                    )
                }
            }

            db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_EXPENSE}", null).use { cursor ->
                while (cursor.moveToNext()) {
                    transactions.add(
                        Transaction(
                            id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_ID)).toString(),
                            amount = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_AMOUNT)),
                            category = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_CATEGORY)),
                            description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_DESCRIPTION)),
                            date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_DATE)),
                            time = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_TIME)),
                            type = "Expense"
                        )
                    )
                }
            }
        }

        // Sort transactions by date and time
        return transactions.sortedWith(
            compareByDescending<Transaction> { it.date }
                .thenByDescending { it.time }
        )
    }

    private fun displayTotalIncome() {
        val totalIncome = dbHelper.readableDatabase.rawQuery(
            "SELECT SUM(${DatabaseHelper.COLUMN_INCOME_AMOUNT}) FROM ${DatabaseHelper.TABLE_INCOME}",
            null
        ).use { if (it.moveToFirst()) it.getDouble(0) else 0.0 }

        tvIncomeValue.text = "₹$totalIncome"
    }

    private fun displayTotalExpense() {
        val totalExpense = dbHelper.readableDatabase.rawQuery(
            "SELECT SUM(${DatabaseHelper.COLUMN_EXPENSE_AMOUNT}) FROM ${DatabaseHelper.TABLE_EXPENSE}",
            null
        ).use { if (it.moveToFirst()) it.getDouble(0) else 0.0 }

        tvExpenseValue.text = "₹$totalExpense"
    }

    private fun displayTotalBalance() {
        val totalIncome = tvIncomeValue.text.toString().replace("₹", "").toDoubleOrNull() ?: 0.0
        val totalExpense = tvExpenseValue.text.toString().replace("₹", "").toDoubleOrNull() ?: 0.0
        tvTotalBalance.text = "₹${totalIncome - totalExpense}"
    }
}




