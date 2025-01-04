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
    private lateinit var incomeExpenseDatabase: IncomeExpenseDatabase // Use helper class
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

        // Initialize IncomeExpenseDatabase
        incomeExpenseDatabase = IncomeExpenseDatabase(this)
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
        // Use the helper class to fetch all transactions sorted
        val transactions = incomeExpenseDatabase.getAllTransactionsSorted()
        transactionAdapter.updateTransactions(transactions)
    }

    private fun displayTotalIncome() {
        // Use the helper class method to get total income
        val totalIncome = incomeExpenseDatabase.getTotalIncome()
        tvIncomeValue.text = "₹$totalIncome"
    }

    private fun displayTotalExpense() {
        // Use the helper class method to get total expenses
        val totalExpense = incomeExpenseDatabase.getTotalExpense()
        tvExpenseValue.text = "₹$totalExpense"
    }

    private fun displayTotalBalance() {
        // Calculate the total balance by subtracting total expenses from total income
        val totalIncome = tvIncomeValue.text.toString().replace("₹", "").toDoubleOrNull() ?: 0.0
        val totalExpense = tvExpenseValue.text.toString().replace("₹", "").toDoubleOrNull() ?: 0.0
        tvTotalBalance.text = "₹${totalIncome - totalExpense}"
    }
}





