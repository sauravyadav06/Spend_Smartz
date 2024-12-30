package com.example.attendeaze

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SeeAllActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var btnFilter: ImageButton
    private lateinit var incomeExpenseDatabase: IncomeExpenseDatabase

    private var allTransactions = listOf<Transaction>()  // Store all transactions
    private var filteredTransactions = listOf<Transaction>()  // Store filtered transactions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_see_all) // Use your XML layout

        recyclerView = findViewById(R.id.seeallrecyclerview)
        btnFilter = findViewById(R.id.btn_filter)

        recyclerView.layoutManager = LinearLayoutManager(this)
        incomeExpenseDatabase = IncomeExpenseDatabase(this)

        // Initially load all transactions
        loadAllTransactions()

        // Set up the filter button to open the bottom sheet
        btnFilter.setOnClickListener {
            showFilterBottomSheet()
        }
    }

    private fun loadAllTransactions() {
        // Fetch all transactions (merged income and expense)
        allTransactions = fetchLatestTransactions()

        // Initially show all transactions (no filter applied yet)
        filteredTransactions = allTransactions

        // Set up the adapter with the fetched data
        transactionAdapter = TransactionAdapter(filteredTransactions)
        recyclerView.adapter = transactionAdapter
    }

    private fun fetchLatestTransactions(): List<Transaction> {
        val transactionList = mutableListOf<Transaction>()

        // Fetch income and expense transactions from the database
        val incomeCursor = incomeExpenseDatabase.getAllIncome()
        val expenseCursor = incomeExpenseDatabase.getAllExpenses()

        // Add income and expense transactions to the list
        if (incomeCursor.moveToFirst()) {
            do {
                val amount =
                    incomeCursor.getDouble(incomeCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INCOME_AMOUNT))
                val category =
                    incomeCursor.getString(incomeCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INCOME_CATEGORY))
                val description =
                    incomeCursor.getString(incomeCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INCOME_DESCRIPTION))
                val date =
                    incomeCursor.getString(incomeCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INCOME_DATE))
                val time =
                    incomeCursor.getString(incomeCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INCOME_TIME))

                // Add income transaction to the list
                transactionList.add(
                    Transaction(
                        id = "0", amount = amount, category = category,
                        description = description, date = date,
                        type = "Income", time = time
                    )
                )
            } while (incomeCursor.moveToNext())
        }
        incomeCursor.close()

        if (expenseCursor.moveToFirst()) {
            do {
                val amount =
                    expenseCursor.getDouble(expenseCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_AMOUNT))
                val category =
                    expenseCursor.getString(expenseCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_CATEGORY))
                val description =
                    expenseCursor.getString(expenseCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_DESCRIPTION))
                val date =
                    expenseCursor.getString(expenseCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_DATE))
                val time =
                    expenseCursor.getString(expenseCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_TIME))

                // Add expense transaction to the list
                transactionList.add(
                    Transaction(
                        id = "0", amount = amount, category = category,
                        description = description, date = date,
                        type = "Expense", time = time
                    )
                )
            } while (expenseCursor.moveToNext())
        }
        expenseCursor.close()

        return transactionList
    }

    // Method to show the filter bottom sheet
    private fun showFilterBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val filterView = layoutInflater.inflate(R.layout.filter, null)

        val categorySpinner: Spinner = filterView.findViewById(R.id.spinnerCategory)
        val dateRangeSpinner: Spinner = filterView.findViewById(R.id.spinnerDateRange)
        val btnApplyFilter: Button = filterView.findViewById(R.id.btnApplyFilter)

        // Set up the Spinners with data
        val categoryOptions = arrayOf("All", "Income", "Expense")
        val dateRangeOptions = arrayOf("Today", "Yesterday", "One Month")

        categorySpinner.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryOptions)
        categorySpinner.setSelection(0)

        dateRangeSpinner.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, dateRangeOptions)
        dateRangeSpinner.setSelection(0)

        // Handle Apply Filter button click
        btnApplyFilter.setOnClickListener {
            val selectedCategory = categorySpinner.selectedItem.toString()
            val selectedDateRange = dateRangeSpinner.selectedItem.toString()

            // Apply the filter based on selected options
            applyFilter(selectedCategory, selectedDateRange)
            bottomSheetDialog.dismiss() // Close the bottom sheet
        }

        bottomSheetDialog.setContentView(filterView)
        bottomSheetDialog.show()
    }

    private fun applyFilter(category: String, dateRange: String) {
        // Filter the transactions based on the selected category and date range
        filteredTransactions = filterTransactions(category, dateRange)

        // Notify the adapter that the data has changed
        transactionAdapter.updateTransactions(filteredTransactions)
    }

    private fun filterTransactions(category: String, dateRange: String): List<Transaction> {
        val filteredList = mutableListOf<Transaction>()
        val currentDate = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        ).format(Date()) // Current date as yyyy-MM-dd

        for (transaction in allTransactions) {
            // Filter by category
            if (category != "All" && transaction.type != category) continue

            // Get the transaction date (ensure it is in the right format)
            val transactionDate =
                transaction.date // Assuming transaction.date is already in 'yyyy-MM-dd' format

            // Filter by date range
            when (dateRange) {
                "Today" -> {
                    // Ensure currentDate and transactionDate are both in the same format before comparing
                    if (transactionDate != currentDate) continue
                }

                "Yesterday" -> {
                    val yesterday = Calendar.getInstance()
                    yesterday.add(Calendar.DATE, -1)
                    val yesterdayDate =
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(yesterday.time)
                    if (transactionDate != yesterdayDate) continue
                }

                "One Month" -> {
                    val oneMonthAgo = Calendar.getInstance()
                    oneMonthAgo.add(Calendar.MONTH, -1)
                    val oneMonthAgoDate =
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(oneMonthAgo.time)
                    if (transactionDate < oneMonthAgoDate) continue
                }
            }

            // Add to filtered list if it matches
            filteredList.add(transaction)
        }

        return filteredList
    }
}






