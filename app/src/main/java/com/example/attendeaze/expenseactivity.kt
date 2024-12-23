package com.example.attendeaze

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Calendar

class ExpenseActivity : AppCompatActivity() {

    private lateinit var etExpenseAmount: EditText
    private lateinit var etExpenseCategory: EditText
    private lateinit var etExpenseDescription: EditText
    private lateinit var etExpenseDate: EditText
    private lateinit var btnSaveExpense: Button
    private lateinit var incomeExpenseDatabase: IncomeExpenseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_expenseactivity)

        // Set up window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        etExpenseAmount = findViewById(R.id.et_expense_amount)
        etExpenseCategory = findViewById(R.id.et_expense_category)
        etExpenseDescription = findViewById(R.id.et_expense_description)
        etExpenseDate = findViewById(R.id.et_expense_date)
        btnSaveExpense = findViewById(R.id.btn_save_expense)

        // Initialize database helper
        incomeExpenseDatabase = IncomeExpenseDatabase(this)

        // Set date picker for the date EditText
        etExpenseDate.setOnClickListener {
            showDatePickerDialog(etExpenseDate)
        }

        // Save expense data when button is clicked
        btnSaveExpense.setOnClickListener {
            saveExpenseData()
        }
    }

    /**
     * Shows a date picker dialog to select a date.
     */
    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                editText.setText(formattedDate)
            }, year, month, day)

        datePickerDialog.show()
    }

    /**
     * Validates the inputs, saves the expense data, and shows appropriate feedback.
     */
    private fun saveExpenseData() {
        val amountText = etExpenseAmount.text.toString()
        val amount = amountText.toDoubleOrNull()
        val category = etExpenseCategory.text.toString().trim()
        val description = etExpenseDescription.text.toString().trim()
        val date = etExpenseDate.text.toString().trim()

        // Input validation
        when {
            amountText.isEmpty() || amount == null || amount <= 0 -> {
                etExpenseAmount.error = "Enter a valid amount"
                etExpenseAmount.requestFocus()
            }
            category.isEmpty() -> {
                etExpenseCategory.error = "Enter a category"
                etExpenseCategory.requestFocus()
            }
            description.isEmpty() -> {
                etExpenseDescription.error = "Enter a description"
                etExpenseDescription.requestFocus()
            }
            date.isEmpty() -> {
                etExpenseDate.error = "Select a date"
                etExpenseDate.requestFocus()
            }
            else -> {
                // Insert the expense data into the database
                val result = incomeExpenseDatabase.insertExpense(amount, category, description, date)

                if (result != -1L) {
                    Toast.makeText(this, "Expense saved successfully!", Toast.LENGTH_SHORT).show()
                    // Clear the input fields
                    clearFields()
                } else {
                    Toast.makeText(this, "Failed to save expense. Try again.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Clears all input fields after successful data insertion.
     */
    private fun clearFields() {
        etExpenseAmount.text.clear()
        etExpenseCategory.text.clear()
        etExpenseDescription.text.clear()
        etExpenseDate.text.clear()
    }
}
