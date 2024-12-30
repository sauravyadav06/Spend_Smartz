package com.example.attendeaze

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FilterBottomSheetFragment(private val onFilterApplied: (category: String?, dateRange: String?) -> Unit) : BottomSheetDialogFragment() {

    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerDateRange: Spinner
    private lateinit var btnApplyFilter: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.filter, container, false)

        spinnerCategory = view.findViewById(R.id.spinnerCategory)
        spinnerDateRange = view.findViewById(R.id.spinnerDateRange)
        btnApplyFilter = view.findViewById(R.id.btnApplyFilter)

        // Set up category spinner
        val categories = arrayOf("All", "Income", "Expense")
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = categoryAdapter

        // Set up date range spinner
        val dateRanges = arrayOf("Today", "Yesterday", "Last 30 days", "Last 90 days", "6 months", "One year")
        val dateRangeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, dateRanges)
        dateRangeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDateRange.adapter = dateRangeAdapter

        // Set up apply filter button
        btnApplyFilter.setOnClickListener {
            val selectedCategory = spinnerCategory.selectedItem.toString()
            val selectedDateRange = spinnerDateRange.selectedItem.toString()
            onFilterApplied(selectedCategory, selectedDateRange)  // Call the callback with selected values
            dismiss()  // Dismiss the bottom sheet after applying the filter
        }

        return view
    }
}
