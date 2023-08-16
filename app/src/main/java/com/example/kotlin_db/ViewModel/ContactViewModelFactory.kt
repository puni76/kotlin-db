package com.example.kotlin_db.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kotlin_db.Room.ContactDao

class ContactViewModelFactory(private val contactDao: ContactDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContactViewModel(contactDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
