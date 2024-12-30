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
        val transactions = getTransactions() // Get all transactions from the database
        val incomeEntries = mutableListOf<BarEntry>()
        val expenseEntries = mutableListOf<BarEntry>()
        val dateList = mutableListOf<String>()
        val dateTimeMap = mutableMapOf<String, Pair<Float, Float>>() // Map to hold sums of income and expense by date

        // Process each transaction
        transactions.forEach { transaction ->
            val date = transaction.date
            val amount = transaction.amount.toFloat()

            // Group by date and add amounts based on type (Income/Expense)
            if (transaction.type == "Income") {
                val current = dateTimeMap[date] ?: Pair(0f, 0f)
                dateTimeMap[date] = Pair(current.first + amount, current.second)
            } else {
                val current = dateTimeMap[date] ?: Pair(0f, 0f)
                dateTimeMap[date] = Pair(current.first, current.second + amount)
            }
        }

        // Prepare separate data entries for income and expense
        var index = 0f
        dateTimeMap.forEach { (date, amounts) ->
            incomeEntries.add(BarEntry(index, amounts.first)) // Income
            expenseEntries.add(BarEntry(index, amounts.second)) // Expense
            dateList.add(date)
            index++
        }

        // Create separate BarDataSets for income and expense
        val incomeDataSet = BarDataSet(incomeEntries, "Income")
        incomeDataSet.color = Color.GREEN

        val expenseDataSet = BarDataSet(expenseEntries, "Expense")
        expenseDataSet.color = Color.RED

        // Create BarData with both datasets
        val barData = BarData(incomeDataSet, expenseDataSet)

        // Set up bar width and group spacing
        val groupSpace = 0.4f
        val barSpace = 0.05f
        val barWidth = 0.2f
        barData.barWidth = barWidth

        // Set data to the chart
        incomeExpenseChart.data = barData

        // Configure X-axis labels and settings
        incomeExpenseChart.xAxis.valueFormatter = IndexAxisValueFormatter(dateList)
        incomeExpenseChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        incomeExpenseChart.xAxis.granularity = 1f
        incomeExpenseChart.xAxis.setLabelCount(dateList.size, true)
        incomeExpenseChart.xAxis.axisMinimum = 0f
        incomeExpenseChart.xAxis.axisMaximum = dateList.size.toFloat()
        incomeExpenseChart.groupBars(0f, groupSpace, barSpace) // Group bars together

        // Customize Y-axis
        incomeExpenseChart.axisLeft.apply {
            axisMinimum = 0f // Ensure bars start from 0
            granularity = 1f // Set Y-axis granularity for a clean visual
        }

        // Customize chart appearance
        incomeExpenseChart.description.text = "Income vs Expense" // Updated description
        incomeExpenseChart.setDrawValueAboveBar(true)
        incomeExpenseChart.animateY(1500, com.github.mikephil.charting.animation.Easing.EaseInOutQuad) // Animate chart
        incomeExpenseChart.invalidate() // Refresh the chart
    }

    private fun getTransactions(): List<Transaction> {
        val transactionList = mutableListOf<Transaction>()

        // Fetch income data from the database
        val incomeCursor = incomeExpenseDatabase.getAllIncome()
        if (incomeCursor.moveToFirst()) {
            do {
                val id = incomeCursor.getString(incomeCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INCOME_ID))
                val amount = incomeCursor.getDouble(incomeCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INCOME_AMOUNT))
                val category = incomeCursor.getString(incomeCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INCOME_CATEGORY))
                val description = incomeCursor.getString(incomeCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INCOME_DESCRIPTION))
                val date = incomeCursor.getString(incomeCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INCOME_DATE))
                val time = incomeCursor.getString(incomeCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INCOME_TIME))

                transactionList.add(Transaction(id = id, amount = amount, category = category, description = description, date = date, time = time, type = "Income"))
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
                val time = expenseCursor.getString(expenseCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_TIME))

                transactionList.add(Transaction(id = "0", amount = amount, category = category, description = description, date = date, time = time, type = "Expense"))
            } while (expenseCursor.moveToNext())
        }
        expenseCursor.close()

        return transactionList
    }
}



