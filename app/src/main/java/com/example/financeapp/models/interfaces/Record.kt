package com.example.financeapp.models.interfaces

data class Record(
    val title: String,
    val type: RecordType = RecordType.EXPENSE,
    val value: Double,
    val method: PaymentMethod = PaymentMethod.CASH,
    val date: String,
    val categoryId: String,
    val recurrent: Boolean = false,
    val repeating: RepeatingType? = null
)

enum class RecordType {
    INCOME, EXPENSE
}

enum class PaymentMethod {
    CASH, CARD
}

enum class RepeatingType {
    DAILY, WEEKLY, MONTHLY, YEARLY
}