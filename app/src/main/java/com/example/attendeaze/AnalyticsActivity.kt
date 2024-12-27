package com.example.attendeaze

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter


class AnalyticsActivity : AppCompatActivity() {

    private lateinit var incomeExpenseChart: BarChart
    private lateinit var incomeExpenseDatabase: IncomeExpenseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analytics)

        // Initialize the chart and database
        incomeExpenseChart = findViewById(R.id.barChart)
        incomeExpenseDatabase = IncomeExpenseDatabase(this)

        // Customize the chart appearance
        incomeExpenseChart.setBackgroundColor(Color.parseColor("#F0F0F0"))
        incomeExpenseChart.setDrawBarShadow(true)
        incomeExpenseChart.setExtraOffsets(10f, 10f, 10f, 10f)

        // Display graph data
        showAnalyticsGraph()
    }

    private fun showAnalyticsGraph() {
        val transactions = getTransactions()  // Get all transactions from the database
        val dateList = mutableListOf<String>()
        val incomeAmounts = mutableListOf<Float>()
        val expenseAmounts = mutableListOf<Float>()

        transactions.forEach { transaction ->
            dateList.add(transaction.date)
            if (transaction.type == "Income") {
                incomeAmounts.add(transaction.amount.toFloat())
                expenseAmounts.add(0f)
            } else {
                expenseAmounts.add(transaction.amount.toFloat())
                incomeAmounts.add(0f)
            }
        }

        // Prepare data entries for BarChart
        val entries = mutableListOf<BarEntry>()
        for (i in dateList.indices) {
            entries.add(BarEntry(i.toFloat(), floatArrayOf(incomeAmounts[i], expenseAmounts[i])))
        }

        // Create BarDataSet and BarData
        val dataSet = BarDataSet(entries, "Income vs Expense")
        dataSet.colors = listOf(Color.GREEN, Color.RED)  // Green for Income, Red for Expense
        dataSet.valueTextColor = Color.WHITE  // White text for value above bars
        dataSet.setBarBorderWidth(1f)
        dataSet.setBarBorderColor(Color.BLACK)

        val barData = BarData(dataSet)
        incomeExpenseChart.data = barData

        // Set X-axis labels
        incomeExpenseChart.xAxis.valueFormatter = IndexAxisValueFormatter(dateList)
        incomeExpenseChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        incomeExpenseChart.xAxis.granularity = 1f

        // Customize chart appearance
        incomeExpenseChart.description.text = "Income vs Expense"
        incomeExpenseChart.setDrawValueAboveBar(true)
        incomeExpenseChart.animateY(1500, com.github.mikephil.charting.animation.Easing.EaseInOutQuad)  // Animate chart on entry

        // Refresh the chart
        incomeExpenseChart.invalidate()
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

