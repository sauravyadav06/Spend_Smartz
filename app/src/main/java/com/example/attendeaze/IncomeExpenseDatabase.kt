package com.example.attendeaze

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log

class IncomeExpenseDatabase(context: Context) {

    private val dbHelper: DatabaseHelper = DatabaseHelper(context)

    // Method to insert income data
    fun insertIncome(
        amount: Double,
        category: String,
        description: String,
        date: String,
        currentTime: String
    ): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_INCOME_AMOUNT, amount)
            put(DatabaseHelper.COLUMN_INCOME_CATEGORY, category)
            put(DatabaseHelper.COLUMN_INCOME_DESCRIPTION, description)
            put(DatabaseHelper.COLUMN_INCOME_DATE, date)
            put(DatabaseHelper.COLUMN_INCOME_TIME, currentTime)
        }
        return db.insert(DatabaseHelper.TABLE_INCOME, null, values)
    }

    // Method to insert expense data
    fun insertExpense(
        amount: Double,
        category: String,
        description: String,
        date: String,
        currentTime: String
    ): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_EXPENSE_AMOUNT, amount)
            put(DatabaseHelper.COLUMN_EXPENSE_CATEGORY, category)
            put(DatabaseHelper.COLUMN_EXPENSE_DESCRIPTION, description)
            put(DatabaseHelper.COLUMN_EXPENSE_DATE, date)
            put(DatabaseHelper.COLUMN_EXPENSE_TIME, currentTime)
        }
        return db.insert(DatabaseHelper.TABLE_EXPENSE, null, values)
    }

    // Method to update income data (if necessary)
    fun updateIncome(id: Long, amount: Double, category: String, description: String, date: String): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_INCOME_AMOUNT, amount)
            put(DatabaseHelper.COLUMN_INCOME_CATEGORY, category)
            put(DatabaseHelper.COLUMN_INCOME_DESCRIPTION, description)
            put(DatabaseHelper.COLUMN_INCOME_DATE, date)
        }
        return db.update(DatabaseHelper.TABLE_INCOME, values, "${DatabaseHelper.COLUMN_INCOME_ID} = ?", arrayOf(id.toString()))
    }

    // Method to update expense data (if necessary)
    fun updateExpense(id: Long, amount: Double, category: String, description: String, date: String): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_EXPENSE_AMOUNT, amount)
            put(DatabaseHelper.COLUMN_EXPENSE_CATEGORY, category)
            put(DatabaseHelper.COLUMN_EXPENSE_DESCRIPTION, description)
            put(DatabaseHelper.COLUMN_EXPENSE_DATE, date)
        }
        return db.update(DatabaseHelper.TABLE_EXPENSE, values, "${DatabaseHelper.COLUMN_EXPENSE_ID} = ?", arrayOf(id.toString()))
    }

    // Method to fetch all income records, ordered by date in descending order
    fun getAllIncome(): Cursor {
        val db = dbHelper.readableDatabase
        return db.query(
            DatabaseHelper.TABLE_INCOME, // Table name
            arrayOf(
                DatabaseHelper.COLUMN_INCOME_ID,         // Add the ID column
                DatabaseHelper.COLUMN_INCOME_AMOUNT,
                DatabaseHelper.COLUMN_INCOME_CATEGORY,
                DatabaseHelper.COLUMN_INCOME_DESCRIPTION,
                DatabaseHelper.COLUMN_INCOME_DATE,
                DatabaseHelper.COLUMN_INCOME_TIME        // Include the time column
            ),
            null, // No selection criteria
            null, // No selection args
            null, // No group by
            null, // No having
            "${DatabaseHelper.COLUMN_INCOME_TIME} DESC" // Order by time descending
        )
    }


    // Method to fetch all expense records, ordered by date in descending order
    fun getAllExpenses(): Cursor {
        val db = dbHelper.readableDatabase
        return db.query(
            DatabaseHelper.TABLE_EXPENSE, // Table name
            arrayOf(
                DatabaseHelper.COLUMN_EXPENSE_AMOUNT,
                DatabaseHelper.COLUMN_EXPENSE_CATEGORY,
                DatabaseHelper.COLUMN_EXPENSE_DESCRIPTION,
                DatabaseHelper.COLUMN_EXPENSE_DATE,
                DatabaseHelper.COLUMN_EXPENSE_TIME // Add time column here
            ),
            null, // No selection criteria
            null, // No selection args
            null, // No group by
            null, // No having
            "${DatabaseHelper.COLUMN_EXPENSE_TIME} DESC" // Order by time descending
        )
    }

    // Method to fetch all transactions sorted by date and time (income and expense)
    fun getAllTransactionsSorted(): List<Transaction> {
        val db = dbHelper.readableDatabase // FIX: dbHelper instead of `readableDatabase`
        val transactions = mutableListOf<Transaction>()

        // Fetch data from the income table
        val incomeCursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_INCOME}", null)
        while (incomeCursor.moveToNext()) {
            val transaction = Transaction(
                id = incomeCursor.getString(incomeCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INCOME_ID)),
                amount = incomeCursor.getDouble(incomeCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INCOME_AMOUNT)),
                category = incomeCursor.getString(incomeCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INCOME_CATEGORY)),
                description = incomeCursor.getString(incomeCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INCOME_DESCRIPTION)),
                date = incomeCursor.getString(incomeCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INCOME_DATE)),
                time = incomeCursor.getString(incomeCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INCOME_TIME)),
                type = "Income"
            )
            transactions.add(transaction)
        }
        incomeCursor.close()

        // Fetch data from the expense table
        val expenseCursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_EXPENSE}", null)
        while (expenseCursor.moveToNext()) {
            val transaction = Transaction(
                id = expenseCursor.getString(expenseCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_ID)),
                amount = expenseCursor.getDouble(expenseCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_AMOUNT)),
                category = expenseCursor.getString(expenseCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_CATEGORY)),
                description = expenseCursor.getString(expenseCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_DESCRIPTION)),
                date = expenseCursor.getString(expenseCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_DATE)),
                time = expenseCursor.getString(expenseCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_TIME)),
                type = "Expense"
            )
            transactions.add(transaction)
        }
        expenseCursor.close()

        // Sort by date and time (latest on top)
        return transactions.sortedWith(compareByDescending<Transaction> { it.date }.thenByDescending { it.time })
    }

    // Method to delete income by ID
    fun deleteIncome(id: Long): Int {
        val db = dbHelper.writableDatabase
        return db.delete(DatabaseHelper.TABLE_INCOME, "${DatabaseHelper.COLUMN_INCOME_ID} = ?", arrayOf(id.toString()))
    }

    // Method to delete expense by ID
    fun deleteExpense(id: Long): Int {
        val db = dbHelper.writableDatabase
        return db.delete(DatabaseHelper.TABLE_EXPENSE, "${DatabaseHelper.COLUMN_EXPENSE_ID} = ?", arrayOf(id.toString()))
    }

    // Method to get total income
    fun getTotalIncome(): Double {
        val db = dbHelper.readableDatabase
        val query = "SELECT SUM(${DatabaseHelper.COLUMN_INCOME_AMOUNT}) FROM ${DatabaseHelper.TABLE_INCOME}"
        val cursor = db.rawQuery(query, null)

        var totalIncome = 0.0
        if (cursor.moveToFirst()) {
            totalIncome = cursor.getDouble(0) // Get the sum from the first column
        }
        cursor.close()
        return totalIncome
    }

    // Method to get total expense
    fun getTotalExpense(): Double {
        val db = dbHelper.readableDatabase
        val query = "SELECT SUM(${DatabaseHelper.COLUMN_EXPENSE_AMOUNT}) FROM ${DatabaseHelper.TABLE_EXPENSE}"
        val cursor = db.rawQuery(query, null)

        var totalExpense = 0.0
        if (cursor.moveToFirst()) {
            totalExpense = cursor.getDouble(0) // Get the sum from the first column
        }
        cursor.close()
        return totalExpense
    }
    fun getAllTransaction(): Cursor {
        val db = dbHelper.readableDatabase
        return db.query(
            DatabaseHelper.TABLE_INCOME, // Table name
            arrayOf(
                DatabaseHelper.COLUMN_INCOME_ID,
                DatabaseHelper.COLUMN_INCOME_AMOUNT,
                DatabaseHelper.COLUMN_INCOME_CATEGORY,
                DatabaseHelper.COLUMN_INCOME_DESCRIPTION,
                DatabaseHelper.COLUMN_INCOME_DATE,
                DatabaseHelper.COLUMN_INCOME_TIME        // Include the time column
            ),
            null, // No selection criteria
            null, // No selection args
            null, // No group by
            null, // No having
            "${DatabaseHelper.COLUMN_INCOME_TIME} DESC" // Order by time descending
        )
    }

}
