package com.example.attendeaze

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import java.util.Calendar


class IncomeActivity : AppCompatActivity() {

    private lateinit var etIncomeAmount: EditText
    private lateinit var spinnerIncomeCategory: Spinner
    private lateinit var etIncomeDescription: EditText
    private lateinit var etIncomeDate: EditText
    private lateinit var btnSaveIncome: Button
    private lateinit var btnAddCategory: MaterialButton
    private lateinit var incomeExpenseDatabase: IncomeExpenseDatabase

    private val incomeCategoryPreferences = "IncomeCategoryPreferences"
    private val incomeCategoryKey = "income_categories"
    private lateinit var predefinedIncomeCategories: MutableList<String>
    private lateinit var incomeCategoryAdapter: ArrayAdapter<String>

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
        spinnerIncomeCategory = findViewById(R.id.spinner_income_category)
        etIncomeDescription = findViewById(R.id.et_income_description)
        etIncomeDate = findViewById(R.id.et_income_date)
        btnSaveIncome = findViewById(R.id.btn_save_income)
        btnAddCategory = findViewById(R.id.btn_add_category)

        // Initialize database helper
        incomeExpenseDatabase = IncomeExpenseDatabase(this)

        // Load income categories from SharedPreferences
        loadIncomeCategories()

        // Set up the category spinner
        setupIncomeCategorySpinner()

        // Show date picker dialog when clicking on the date field
        etIncomeDate.setOnClickListener {
            showDatePickerDialog(etIncomeDate)
        }

        // Handle Add Category button
        btnAddCategory.setOnClickListener {
            showAddIncomeCategoryDialog()
        }

        // Save income data when button is clicked
        btnSaveIncome.setOnClickListener {
            if (validateInput()) {
                val amount = etIncomeAmount.text.toString().toDouble()
                val category = spinnerIncomeCategory.selectedItem.toString()
                val description = etIncomeDescription.text.toString()
                val date = etIncomeDate.text.toString()

                // Capture the current time when the Save button is clicked
                val currentTime = getCurrentTime()

                // Insert the income data into the database
                val result = incomeExpenseDatabase.insertIncome(amount, category, description, date, currentTime)

                if (result != -1L) {
                    // Data inserted successfully
                    Toast.makeText(this, "Income saved successfully!", Toast.LENGTH_SHORT).show()

                    // Notify MainActivity that data was updated
                    setResult(Activity.RESULT_OK)

                    // Close this activity and return to MainActivity
                    finish()

                    clearInputFields()
                } else {
                    // Handle database error
                    Toast.makeText(this, "Failed to save income. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Set up the category spinner with predefined income categories
    private fun setupIncomeCategorySpinner() {
        // Adding the default "Select Category" to the list of categories
        val categoriesWithDefault = mutableListOf("Select Category").apply {
            addAll(predefinedIncomeCategories)
        }

        incomeCategoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoriesWithDefault)
        incomeCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerIncomeCategory.adapter = incomeCategoryAdapter
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

    // Show dialog to add a new income category
    private fun showAddIncomeCategoryDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add Income Category")

        val input = EditText(this)
        input.hint = "Category Name"
        builder.setView(input)

        builder.setPositiveButton("Add") { dialog, _ ->
            val newCategory = input.text.toString().trim()
            if (newCategory.isEmpty()) {
                Toast.makeText(this, "Category name cannot be empty", Toast.LENGTH_SHORT).show()
            } else if (predefinedIncomeCategories.contains(newCategory)) {
                Toast.makeText(this, "Category already exists", Toast.LENGTH_SHORT).show()
            } else {
                // Add the new category to the list
                predefinedIncomeCategories.add(newCategory)

                // Save the updated list to SharedPreferences
                saveIncomeCategories()

                // Immediately update the adapter and spinner
                incomeCategoryAdapter.clear()
                incomeCategoryAdapter.addAll(predefinedIncomeCategories)
                incomeCategoryAdapter.notifyDataSetChanged()

                // Set the spinner to the new category
                spinnerIncomeCategory.setSelection(predefinedIncomeCategories.size - 1)

                Toast.makeText(this, "Category added successfully!", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    // Validate input fields (all except description are mandatory)
    private fun validateInput(): Boolean {
        val amount = etIncomeAmount.text.toString()
        val category = spinnerIncomeCategory.selectedItem.toString()
        val date = etIncomeDate.text.toString()

        return when {
            amount.isEmpty() -> {
                Toast.makeText(this, "Please enter the income amount.", Toast.LENGTH_SHORT).show()
                false
            }
            category == "Select Category" -> {
                Toast.makeText(this, "Please select a category.", Toast.LENGTH_SHORT).show()
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
        etIncomeDescription.text.clear()
        etIncomeDate.text.clear()
        spinnerIncomeCategory.setSelection(0) // Reset spinner to the first item
    }

    // Save income categories to SharedPreferences
    private fun saveIncomeCategories() {
        val sharedPreferences = getSharedPreferences(incomeCategoryPreferences, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putStringSet(incomeCategoryKey, predefinedIncomeCategories.toSet())
        editor.apply()
    }

    // Load income categories from SharedPreferences
    private fun loadIncomeCategories() {
        val sharedPreferences = getSharedPreferences(incomeCategoryPreferences, Context.MODE_PRIVATE)
        val savedCategories = sharedPreferences.getStringSet(incomeCategoryKey, null)

        predefinedIncomeCategories = if (savedCategories != null) {
            savedCategories.toMutableList()
        } else {
            mutableListOf("Salary", "Freelancing", "Investment", "Other") // Default income categories
        }
    }

    // Function to get current time
    private fun getCurrentTime(): String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)

        // Format the current time as a string
        return "$hour:$minute:$second"
    }
}







