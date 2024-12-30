package com.example.attendeaze

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class TransactionAdapter(private var transactions: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    // ViewHolder class that holds the view for each item
    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val amount: TextView = view.findViewById(R.id.tv_transaction_amount)
        val date: TextView = view.findViewById(R.id.tv_transaction_date)
      //  val time: TextView = view.findViewById(R.id.tv_transaction_time)
        val category: TextView = view.findViewById(R.id.tv_transaction_category)
        val description: TextView = view.findViewById(R.id.tv_transaction_description)
    }

    // Inflates the layout for each item in the RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    // Binds data to each item in the RecyclerView
    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]

        // Set amount and color based on whether it's an income or expense
        holder.amount.text = "₹${transaction.amount}"
        if (transaction.type == "Income") {
            holder.amount.setTextColor(holder.itemView.context.getColor(R.color.green)) // Green for income
        } else if (transaction.type == "Expense") {
            holder.amount.setTextColor(holder.itemView.context.getColor(R.color.red)) // Red for expense
        }

        // Set the date, time, category, and description
        holder.date.text = transaction.date
       // holder.time.text = transaction.time
        holder.category.text = "Category: ${transaction.category}"
        holder.description.text = "Description: ${transaction.description}"
    }

    // Returns the total number of items in the RecyclerView
    override fun getItemCount(): Int {
        return transactions.size
    }

    // Method to update the list of transactions and notify the adapter
    fun updateTransactions(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged() // Notifies the RecyclerView to refresh its view with new data
    }
}