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
class IncomeActivity : AppCompatActivity() {

    private lateinit var etIncomeAmount: EditText
    private lateinit var etIncomeCategory: EditText
    private lateinit var etIncomeDescription: EditText
    private lateinit var etIncomeDate: EditText
    private lateinit var btnSaveIncome: Button
    private lateinit var incomeExpenseDatabase: IncomeExpenseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_incomeactivity)

        // Set up window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        etIncomeAmount = findViewById(R.id.et_income_amount)
        etIncomeCategory = findViewById(R.id.et_income_category)
        etIncomeDescription = findViewById(R.id.et_income_description)
        etIncomeDate = findViewById(R.id.et_income_date)
        btnSaveIncome = findViewById(R.id.btn_save_income)

        // Initialize database helper
        incomeExpenseDatabase = IncomeExpenseDatabase(this)

        // Show date picker dialog when clicking on the date field
        etIncomeDate.setOnClickListener {
            showDatePickerDialog(etIncomeDate)
        }

        // Save income data when button is clicked
        btnSaveIncome.setOnClickListener {
            if (validateInput()) {
                val amount = etIncomeAmount.text.toString().toDouble()
                val category = etIncomeCategory.text.toString()
                val description = etIncomeDescription.text.toString()
                val date = etIncomeDate.text.toString()

                // Insert the income data into the database
                val result = incomeExpenseDatabase.insertIncome(amount, category, description, date)

                if (result != -1L) {
                    // Data inserted successfully
                    Toast.makeText(this, "Income saved successfully!", Toast.LENGTH_SHORT).show()
                    clearInputFields()
                } else {
                    // Handle database error
                    Toast.makeText(this, "Failed to save income. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Show DatePickerDialog
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

    // Validate input fields
    private fun validateInput(): Boolean {
        val amount = etIncomeAmount.text.toString()
        val category = etIncomeCategory.text.toString()
        val description = etIncomeDescription.text.toString()
        val date = etIncomeDate.text.toString()

        return when {
            amount.isEmpty() -> {
                Toast.makeText(this, "Please enter the income amount.", Toast.LENGTH_SHORT).show()
                false
            }
            category.isEmpty() -> {
                Toast.makeText(this, "Please enter the income category.", Toast.LENGTH_SHORT).show()
                false
            }
            description.isEmpty() -> {
                Toast.makeText(this, "Please enter a description.", Toast.LENGTH_SHORT).show()
                false
            }
            date.isEmpty() -> {
                Toast.makeText(this, "Please select a date.", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    // Clear input fields after successful save
    private fun clearInputFields() {
        etIncomeAmount.text.clear()
        etIncomeCategory.text.clear()
        etIncomeDescription.text.clear()
        etIncomeDate.text.clear()
    }
}

