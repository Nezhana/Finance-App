package com.example.financeapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {
    private val _token = MutableLiveData<String?>()
    val token: LiveData<String?> = _token

    fun setToken(token: String) {
        _token.value = token
    }

    fun getToken(): String? {
        return _token.value
    }
}
