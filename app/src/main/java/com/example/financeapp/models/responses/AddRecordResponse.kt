package com.example.financeapp.models.responses

import com.example.financeapp.models.interfaces.PaymentMethod
import com.example.financeapp.models.interfaces.RecordType
import com.example.financeapp.models.interfaces.RepeatingType

data class AddRecordResponse (
    val title: String,
    val type: RecordType = RecordType.EXPENSE,
    val value: Double,
    val method: PaymentMethod = PaymentMethod.CASH,
    val date: String,
    val categoryId: String,
    val recurrent: Boolean = false,
    val repeating: RepeatingType? = null
)