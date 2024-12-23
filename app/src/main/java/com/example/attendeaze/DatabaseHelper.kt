package com.example.attendeaze

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "expense_tracker.db"
        private const val DATABASE_VERSION = 1

        // Table names and column names for both income and expense
        const val TABLE_INCOME = "income"
        const val TABLE_EXPENSE = "expense"

        // Income table column names
        const val COLUMN_INCOME_ID = "income_id"
        const val COLUMN_INCOME_AMOUNT = "income_amount"
        const val COLUMN_INCOME_CATEGORY = "income_category"
        const val COLUMN_INCOME_DESCRIPTION = "income_description"
        const val COLUMN_INCOME_DATE = "income_date"

        // Expense table column names
        const val COLUMN_EXPENSE_ID = "expense_id"
        const val COLUMN_EXPENSE_AMOUNT = "expense_amount"
        const val COLUMN_EXPENSE_CATEGORY = "expense_category"
        const val COLUMN_EXPENSE_DESCRIPTION = "expense_description"
        const val COLUMN_EXPENSE_DATE = "expense_date"

        // SQL statements to create the income and expense tables with distinct column names
        private const val CREATE_TABLE_INCOME = """
            CREATE TABLE $TABLE_INCOME (
                $COLUMN_INCOME_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_INCOME_AMOUNT REAL,
                $COLUMN_INCOME_CATEGORY TEXT,
                $COLUMN_INCOME_DESCRIPTION TEXT,
                $COLUMN_INCOME_DATE TEXT
            )
        """

        private const val CREATE_TABLE_EXPENSE = """
            CREATE TABLE $TABLE_EXPENSE (
                $COLUMN_EXPENSE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_EXPENSE_AMOUNT REAL,
                $COLUMN_EXPENSE_CATEGORY TEXT,
                $COLUMN_EXPENSE_DESCRIPTION TEXT,
                $COLUMN_EXPENSE_DATE TEXT
            )
        """
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE_INCOME)
        db?.execSQL(CREATE_TABLE_EXPENSE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_INCOME")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_EXPENSE")
        onCreate(db)
    }
}
