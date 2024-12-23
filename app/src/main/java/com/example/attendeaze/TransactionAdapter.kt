package com.example.attendeaze

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter(private val transactions: List<Transaction>) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        // Inflate the layout for each item in the RecyclerView
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        // Bind the data (Transaction) to the ViewHolder
        val transaction = transactions[position]
        holder.bind(transaction)
    }

    override fun getItemCount(): Int = transactions.size

    // ViewHolder class to hold the views for each item in the RecyclerView
    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val amountTextView: TextView = itemView.findViewById(R.id.tv_transaction_amount)
        private val dateTextView: TextView = itemView.findViewById(R.id.tv_transaction_date)
        private val categoryTextView: TextView = itemView.findViewById(R.id.tv_transaction_category)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.tv_transaction_description)

        fun bind(transaction: Transaction) {
            // Bind data to the views
            amountTextView.text = "₹${transaction.amount}"
            dateTextView.text = transaction.date
            categoryTextView.text = "Category: ${transaction.category}"
            descriptionTextView.text = "Description: ${transaction.description}"

            // Change text color based on the transaction type (Income or Expense)
            if (transaction.type == "Income") {
                amountTextView.setTextColor(Color.GREEN) // Green for income
            } else if (transaction.type == "Expense") {
                amountTextView.setTextColor(Color.RED) // Red for expense
            }
        }
    }
}


