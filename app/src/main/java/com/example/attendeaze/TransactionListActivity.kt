package com.example.attendeaze

import android.database.Cursor
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar

class TransactionListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var incomeExpenseDatabase: IncomeExpenseDatabase
    private lateinit var filterButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_list)

        // Initialize RecyclerView and database
        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)

        incomeExpenseDatabase = IncomeExpenseDatabase(this)

        // Fetch transactions and set them in RecyclerView
        loadTransactions()

        // Initialize the filter button and set its onClickListener
        filterButton = findViewById(R.id.btn_filter)
        filterButton.setOnClickListener {
            // Show the filter dialog or another UI to let the user select the filter
            showFilterDialog()
        }
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

    private fun showFilterDialog() {
        val dialogView = layoutInflater.inflate(R.layout.layout_filter_bottom_sheet, null)

        val categorySpinner: Spinner = dialogView.findViewById(R.id.spinnerCategory)
        val dateRangeSpinner: Spinner = dialogView.findViewById(R.id.spinnerDateRange)
        val applyButton: Button = dialogView.findViewById(R.id.btnApplyFilter)

        val categories = listOf("All", "Income", "Expense")
        val dateRanges = listOf("Today", "Yesterday", "Last 30 days", "Last 90 days", "6 months", "Last year")

        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter

        val dateRangeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, dateRanges)
        dateRangeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dateRangeSpinner.adapter = dateRangeAdapter

        val builder = AlertDialog.Builder(this)
            .setTitle("Filter Transactions")
            .setView(dialogView)

        val dialog = builder.create()
        dialog.show()

        // Apply filter when button is clicked
        applyButton.setOnClickListener {
            val selectedCategory = categorySpinner.selectedItem.toString()
            val selectedDateRange = dateRangeSpinner.selectedItem.toString()

            // Apply the filter and reload transactions
            applyFilter(selectedCategory, selectedDateRange)
            dialog.dismiss() // Dismiss the dialog after applying the filter
        }
    }

    private fun applyFilter(category: String, dateRange: String) {
        // Fetch transactions from the database (unfiltered)
        val allTransactions = getTransactions()

        // Filter the transactions based on category and date range
        val filteredTransactions = allTransactions.filter { transaction ->
            val categoryMatches = category == "All" || transaction.category == category
            val dateMatches = when (dateRange) {
                "Today" -> transaction.date.isToday()
                "Yesterday" -> transaction.date.isYesterday()
                "Last 30 days" -> transaction.date.isWithinLastDays(30)
                "Last 90 days" -> transaction.date.isWithinLastDays(90)
                "6 months" -> transaction.date.isWithinLastMonths(6)
                "Last year" -> transaction.date.isWithinLastYears(1)
                else -> true
            }
            categoryMatches && dateMatches
        }

        // Update the RecyclerView with the filtered transactions
        transactionAdapter.updateData(filteredTransactions)
    }

    // Date range helper methods

    fun String.isToday(): Boolean {
        val today = Calendar.getInstance()
        val transactionDate = SimpleDateFormat("yyyy-MM-dd").parse(this)
        return transactionDate?.let {
            it.year == today.get(Calendar.YEAR) &&
                    it.month == today.get(Calendar.MONTH) &&
                    it.date == today.get(Calendar.DAY_OF_MONTH)
        } ?: false
    }

    fun String.isYesterday(): Boolean {
        val yesterday = Calendar.getInstance()
        yesterday.add(Calendar.DAY_OF_YEAR, -1)
        val transactionDate = SimpleDateFormat("yyyy-MM-dd").parse(this)
        return transactionDate?.let {
            it.year == yesterday.get(Calendar.YEAR) &&
                    it.month == yesterday.get(Calendar.MONTH) &&
                    it.date == yesterday.get(Calendar.DAY_OF_MONTH)
        } ?: false
    }

    fun String.isWithinLastDays(days: Int): Boolean {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -days)
        val transactionDate = SimpleDateFormat("yyyy-MM-dd").parse(this)
        return transactionDate?.after(cal.time) ?: false
    }

    fun String.isWithinLastMonths(months: Int): Boolean {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, -months)
        val transactionDate = SimpleDateFormat("yyyy-MM-dd").parse(this)
        return transactionDate?.after(cal.time) ?: false
    }

    fun String.isWithinLastYears(years: Int): Boolean {
        val cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, -years)
        val transactionDate = SimpleDateFormat("yyyy-MM-dd").parse(this)
        return transactionDate?.after(cal.time) ?: false
    }
}



