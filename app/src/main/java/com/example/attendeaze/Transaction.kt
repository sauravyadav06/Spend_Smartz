package com.example.attendeaze

data class Transaction(
    val id: Int,
    val amount: Double,
    val category: String,
    val description: String,
    val date: String,
    val type: String
)

