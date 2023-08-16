package com.example.kotlin_db.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin_db.Room.Contact
import com.example.kotlin_db.Room.ContactDao
import com.example.kotlin_db.Room.ContactEvent
import com.example.kotlin_db.Room.ContactState
import com.example.kotlin_db.Room.SortType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ContactViewModel(private val dao: ContactDao):ViewModel() {

    private val _sortType= MutableStateFlow(SortType.FIRST_NAME)
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _contacts=_sortType.flatMapLatest { _sortType->
        when(_sortType){
            SortType.FIRST_NAME -> dao.getContactsOrderByFirstName()
            SortType.LAST_NAME -> dao.getContactsOrderByLastName()
            SortType.PHONE_NUMBER -> dao.getContactsOrderByPhoneNumber()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val _state= MutableStateFlow(ContactState())
    val state = combine(_state,_sortType,_contacts){state,sortType,contacts->
        state.copy(
            contacts=contacts,
            sortType=sortType,

        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ContactState())
    fun onEvent(event: ContactEvent){
        when(event){
            is ContactEvent.DeleteContact -> { viewModelScope.launch { dao.deleteContact(event.contact) } }
            ContactEvent.HideDialog -> { _state.update { it.copy(isAddingContact = false) } }
            ContactEvent.SaveContact -> {
                val firstName=state.value.firstName
                val lastName=state.value.lastName
                val phoneNumber=state.value.phoneNumber
                if (firstName.isBlank()||lastName.isBlank()||phoneNumber.isBlank()){
                    return
                }
                val contact= Contact(
                 firstName=firstName,
                 lastName=lastName,
                 phoneNumber=phoneNumber,
                )
                viewModelScope.launch { dao.upsertContact(contact) }
                _state.update { it.copy(isAddingContact = false, firstName = "", lastName = "", phoneNumber = "") }
            }
            is ContactEvent.SetFirstName -> {_state.update { it.copy(firstName = event.firstName) }}
            is ContactEvent.SetLastName -> {_state.update { it.copy(lastName = event.lastName) }}
            is ContactEvent.SetPhoneNumber -> {_state.update { it.copy(phoneNumber = event.phoneNumber) }}
            ContactEvent.ShowDialog -> {_state.update { it.copy(isAddingContact = true) }}
            is ContactEvent.SortContacts -> {_sortType.value=event.sortType}
        }
    }
}