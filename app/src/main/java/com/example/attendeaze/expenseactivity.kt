package com.example.attendeaze

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

class ExpenseActivity : AppCompatActivity() {

    private lateinit var etExpenseAmount: EditText
    private lateinit var spinnerExpenseCategory: Spinner
    private lateinit var etExpenseDescription: EditText
    private lateinit var etExpenseDate: EditText
    private lateinit var btnSaveExpense: Button
    private lateinit var btnAddCategory: MaterialButton
    private lateinit var incomeExpenseDatabase: IncomeExpenseDatabase

    private val categoryPreferences = "CategoryPreferences"
    private val categoryKey = "categories"
    private lateinit var predefinedCategories: MutableList<String>
    private lateinit var categoryAdapter: ArrayAdapter<String>

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
        spinnerExpenseCategory = findViewById(R.id.spinner_expense_category)
        etExpenseDescription = findViewById(R.id.et_expense_description)
        etExpenseDate = findViewById(R.id.et_expense_date)
        btnSaveExpense = findViewById(R.id.btn_save_expense)
        btnAddCategory = findViewById(R.id.btn_add_category)

        // Initialize database helper
        incomeExpenseDatabase = IncomeExpenseDatabase(this)

        // Load categories from SharedPreferences
        loadCategories()

        // Set up the category spinner
        setupCategorySpinner()

        // Set date picker for the date EditText
        etExpenseDate.setOnClickListener {
            showDatePickerDialog(etExpenseDate)
        }

        // Handle Add Category button
        btnAddCategory.setOnClickListener {
            showAddCategoryDialog()
        }

        // Save expense data when button is clicked
        btnSaveExpense.setOnClickListener {
            if (validateInput()) {
                val amount = etExpenseAmount.text.toString().toDouble()
                val category = spinnerExpenseCategory.selectedItem.toString()
                val description = etExpenseDescription.text.toString()
                val date = etExpenseDate.text.toString()

                // Insert the expense data into the database
                val result = incomeExpenseDatabase.insertExpense(amount, category, description, date)

                if (result != -1L) {
                    // Data inserted successfully
                    Toast.makeText(this, "Expense saved successfully!", Toast.LENGTH_SHORT).show()
                    clearInputFields()
                } else {
                    // Handle database error
                    Toast.makeText(this, "Failed to save expense. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Set up the category spinner with predefined categories
    private fun setupCategorySpinner() {
        // Adding the default "Select Category" to the list of categories
        val categoriesWithDefault = mutableListOf("Select Category").apply {
            addAll(predefinedCategories)
        }

        categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoriesWithDefault)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerExpenseCategory.adapter = categoryAdapter
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

    // Show dialog to add a new category
    private fun showAddCategoryDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add Category")

        val input = EditText(this)
        input.hint = "Category Name"
        builder.setView(input)

        builder.setPositiveButton("Add") { dialog, _ ->
            val newCategory = input.text.toString().trim()
            if (newCategory.isEmpty()) {
                Toast.makeText(this, "Category name cannot be empty", Toast.LENGTH_SHORT).show()
            } else if (predefinedCategories.contains(newCategory)) {
                Toast.makeText(this, "Category already exists", Toast.LENGTH_SHORT).show()
            } else {
                predefinedCategories.add(newCategory) // Add to list
                categoryAdapter.notifyDataSetChanged() // Update spinner
                saveCategories() // Save updated list to SharedPreferences
                Toast.makeText(this, "Category added successfully!", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    // Validate input fields (all except description are mandatory)
    private fun validateInput(): Boolean {
        val amount = etExpenseAmount.text.toString()
        val category = spinnerExpenseCategory.selectedItem.toString()
        val date = etExpenseDate.text.toString()

        return when {
            amount.isEmpty() -> {
                Toast.makeText(this, "Please enter the expense amount.", Toast.LENGTH_SHORT).show()
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
        etExpenseAmount.text.clear()
        etExpenseDescription.text.clear()
        etExpenseDate.text.clear()
        spinnerExpenseCategory.setSelection(0) // Reset spinner to the first item
    }

    // Save categories to SharedPreferences
    private fun saveCategories() {
        val sharedPreferences = getSharedPreferences(categoryPreferences, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putStringSet(categoryKey, predefinedCategories.toSet())
        editor.apply()
    }

    // Load categories from SharedPreferences
    private fun loadCategories() {
        val sharedPreferences = getSharedPreferences(categoryPreferences, Context.MODE_PRIVATE)
        val savedCategories = sharedPreferences.getStringSet(categoryKey, null)

        predefinedCategories = if (savedCategories != null) {
            savedCategories.toMutableList()
        } else {
            mutableListOf("Food", "Transport", "Bills", "Other") // Default categories
        }
    }
}

