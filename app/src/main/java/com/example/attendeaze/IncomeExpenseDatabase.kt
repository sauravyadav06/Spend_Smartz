package com.example.attendeaze
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class IncomeExpenseDatabase(context: Context) {

    private val dbHelper: DatabaseHelper = DatabaseHelper(context)

    // Method to insert income data
    fun insertIncome(amount: Double, category: String, description: String, date: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_INCOME_AMOUNT, amount)
            put(DatabaseHelper.COLUMN_INCOME_CATEGORY, category)
            put(DatabaseHelper.COLUMN_INCOME_DESCRIPTION, description)
            put(DatabaseHelper.COLUMN_INCOME_DATE, date)
        }
        return db.insert(DatabaseHelper.TABLE_INCOME, null, values)
    }

    // Method to insert expense data
    fun insertExpense(amount: Double, category: String, description: String, date: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_EXPENSE_AMOUNT, amount)
            put(DatabaseHelper.COLUMN_EXPENSE_CATEGORY, category)
            put(DatabaseHelper.COLUMN_EXPENSE_DESCRIPTION, description)
            put(DatabaseHelper.COLUMN_EXPENSE_DATE, date)
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
            null, // All columns
            null, // No selection criteria
            null, // No selection args
            null, // No group by
            null, // No having
            "${DatabaseHelper.COLUMN_INCOME_DATE} DESC" // Order by date descending
        )
    }

    // Method to fetch all expense records, ordered by date in descending order
    fun getAllExpenses(): Cursor {
        val db = dbHelper.readableDatabase
        return db.query(
            DatabaseHelper.TABLE_EXPENSE, // Table name
            null, // All columns
            null, // No selection criteria
            null, // No selection args
            null, // No group by
            null, // No having
            "${DatabaseHelper.COLUMN_EXPENSE_DATE} DESC" // Order by date descending
        )
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


}
