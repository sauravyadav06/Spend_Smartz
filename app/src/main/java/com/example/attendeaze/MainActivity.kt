package com.example.attendeaze


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private lateinit var btnIncome: ImageButton
    private lateinit var btnOutcome: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var incomeExpenseDatabase: IncomeExpenseDatabase
    private lateinit var tvIncomeValue: TextView
    private lateinit var tvExpenseValue: TextView
    private lateinit var tvTotalBalance: TextView
    private lateinit var seeAll: TextView
    private lateinit var analysis: ImageButton
    private lateinit var tvUserName: TextView
    private val sharedPreferencesName = "UserDetails"
    private val userNameKey = "user_name"
    private lateinit var dateFilterSpinner: Spinner

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
        dateFilterSpinner = findViewById(R.id.date_filter_spinner)

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

        // Set up the date filter spinner
        setupDateFilterSpinner()

        // Load data initially
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

    // Step 2: Set up spinner listener for filtering
    private fun setupDateFilterSpinner() {
        dateFilterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val filterOption = parent.getItemAtPosition(position).toString()
                val (startDate, endDate) = getDateRangeForFilter(filterOption)
                loadFilteredTransactions(startDate, endDate)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing if nothing is selected
            }
        }
    }

    // Step 3: Calculate the date range based on the filter option
    private fun getDateRangeForFilter(filterOption: String): Pair<String, String> {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val endDate = dateFormat.format(calendar.time) // Today's date
        val startDate: String

        when (filterOption) {
            "Daily" -> {
                startDate = endDate // For daily filter, start and end date are the same
            }
            "Weekly" -> {
                calendar.add(Calendar.DAY_OF_YEAR, -7) // Go back 7 days
                startDate = dateFormat.format(calendar.time)
            }
            "Monthly" -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1) // Start from the 1st of the current month
                startDate = dateFormat.format(calendar.time)
            }
            "Yearly" -> {
                calendar.set(Calendar.DAY_OF_YEAR, 1) // Start from the 1st day of the year
                startDate = dateFormat.format(calendar.time)
            }
            else -> {
                startDate = endDate // Default to today if no option is selected
            }
        }
        return Pair(startDate, endDate)
    }

    // Step 4: Load filtered transactions and update UI
    private fun loadFilteredTransactions(startDate: String, endDate: String) {
        // Fetch filtered transactions using date range
        val transactions = incomeExpenseDatabase.getTransactionsByDateRange(startDate, endDate)
        transactionAdapter.updateTransactions(transactions)

        // Update total income, total expense, and balance based on the filtered range
        val totalIncome = incomeExpenseDatabase.getTotalIncomeByDateRange(startDate, endDate)
        val totalExpense = incomeExpenseDatabase.getTotalExpenseByDateRange(startDate, endDate)

        tvIncomeValue.text = "₹$totalIncome"
        tvExpenseValue.text = "₹$totalExpense"
        tvTotalBalance.text = "₹${totalIncome - totalExpense}"
    }

    // Existing methods to load transactions and display total income, expense, and balance
    private fun loadTransactions() {
        val transactions = incomeExpenseDatabase.getAllTransactionsSorted()
        transactionAdapter.updateTransactions(transactions)
    }

    private fun displayTotalIncome() {
        val totalIncome = incomeExpenseDatabase.getTotalIncome()
        tvIncomeValue.text = "₹$totalIncome"
    }

    private fun displayTotalExpense() {
        val totalExpense = incomeExpenseDatabase.getTotalExpense()
        tvExpenseValue.text = "₹$totalExpense"
    }

    private fun displayTotalBalance() {
        val totalIncome = tvIncomeValue.text.toString().replace("₹", "").toDoubleOrNull() ?: 0.0
        val totalExpense = tvExpenseValue.text.toString().replace("₹", "").toDoubleOrNull() ?: 0.0
        tvTotalBalance.text = "₹${totalIncome - totalExpense}"
    }
}





