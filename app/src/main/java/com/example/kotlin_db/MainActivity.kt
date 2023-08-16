package com.example.kotlin_db

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.kotlin_db.Room.ContactDao
import com.example.kotlin_db.Room.ContactDatabase
import com.example.kotlin_db.Screen.ContactScreen
import com.example.kotlin_db.ViewModel.ContactViewModel
import com.example.kotlin_db.ViewModel.ContactViewModelFactory
import com.example.kotlin_db.ui.theme.KotlindbTheme

class MainActivity : ComponentActivity() {
    private lateinit var contactViewModel: ContactViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contactDatabase = ContactDatabase.getInstance(this)
        val contactDao = contactDatabase.contactDao
        val contactViewModelFactory = ContactViewModelFactory(contactDao)
        contactViewModel = ViewModelProvider(this, contactViewModelFactory).get(ContactViewModel::class.java)
        setContent {
            KotlindbTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val state by contactViewModel.state.collectAsState()
                   ContactScreen(state = state, onEvent = contactViewModel::onEvent)
                }
            }
        }
    }
}

