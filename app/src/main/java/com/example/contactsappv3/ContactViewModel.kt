package com.example.contactsappv3

import android.app.Application
import android.database.sqlite.SQLiteException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.contactsappv3.data.Contact
import com.example.contactsappv3.data.ContactDao
import com.example.contactsappv3.data.ContactDbHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = ContactDao(ContactDbHelper.getInstance(application))

    private val _contacts = MutableLiveData<List<Contact>>()
    val contacts: LiveData<List<Contact>> = _contacts

    var filtre: String = ""
        private set

    private var jobChargement: Job? = null

    fun rechercher(texte: String) {
        filtre = texte.trim()
        charger()
    }

    fun charger() {
        val f = filtre
        jobChargement?.cancel()
        jobChargement = viewModelScope.launch {
            val liste = withContext(Dispatchers.IO) {
                try {
                    if (f.isEmpty()) dao.getAll() else dao.search(f)
                } catch (e: SQLiteException) {
                    emptyList()
                }
            }
            _contacts.value = liste
        }
    }

    suspend fun getContact(id: Long): Contact? = withContext(Dispatchers.IO) {
        try {
            dao.getById(id)
        } catch (e: SQLiteException) {
            null
        }
    }

    suspend fun enregistrer(contact: Contact): Boolean {
        val ok = withContext(Dispatchers.IO) {
            try {
                if (contact.id == 0L) dao.insert(contact) != -1L
                else dao.update(contact) > 0
            } catch (e: SQLiteException) {
                false
            }
        }
        if (ok) charger()
        return ok
    }

    suspend fun supprimer(id: Long): Boolean {
        val ok = withContext(Dispatchers.IO) {
            try {
                dao.delete(id) > 0
            } catch (e: SQLiteException) {
                false
            }
        }
        if (ok) charger()
        return ok
    }
}