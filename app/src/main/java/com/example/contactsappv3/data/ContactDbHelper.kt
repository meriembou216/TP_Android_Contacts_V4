package com.example.contactsappv3.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.contactsappv3.data.ContactContract.ContactEntry

class ContactDbHelper private constructor(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "contacts.db"
        const val DATABASE_VERSION = 1

        private const val SQL_CREATE = """
            CREATE TABLE ${ContactEntry.TABLE_NAME} (
                ${ContactEntry.COL_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${ContactEntry.COL_NOM} TEXT NOT NULL,
                ${ContactEntry.COL_PRENOM} TEXT NOT NULL,
                ${ContactEntry.COL_TELEPHONE} TEXT NOT NULL,
                ${ContactEntry.COL_EMAIL} TEXT,
                ${ContactEntry.COL_PHOTO_URI} TEXT,
                ${ContactEntry.COL_LATITUDE} REAL,
                ${ContactEntry.COL_LONGITUDE} REAL
            )"""

        private const val SQL_DELETE = "DROP TABLE IF EXISTS ${ContactEntry.TABLE_NAME}"

        // ----- Singleton : UNE seule instance pour toute l'application -----
        @Volatile
        private var instance: ContactDbHelper? = null

        fun getInstance(context: Context): ContactDbHelper {
            return instance ?: synchronized(this) {
                instance ?: ContactDbHelper(context.applicationContext).also { instance = it }
            }
        }
    }

    // Appelé UNE SEULE FOIS : quand le fichier contacts.db n'existe pas encore
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE)
        insererContactsDemo(db)
    }

    // Appelé quand DATABASE_VERSION augmente (ex. 1 → 2)
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Version 1 : on recrée simplement la table.
        // (Le bonus "favori" demandera une vraie migration sans perte de données.)
        db.execSQL(SQL_DELETE)
        onCreate(db)
    }

    private fun insererContactsDemo(db: SQLiteDatabase) {
        val demo = listOf(
            Contact(nom = "Dupont", prenom = "Claire", telephone = "06 12 34 56 78",
                email = "claire.dupont@example.com", latitude = 33.8754, longitude = 10.8575),
            Contact(nom = "Martin", prenom = "Thomas", telephone = "07 23 45 67 89",
                email = "thomas.martin@example.com", latitude = 36.8065, longitude = 10.1815),
            Contact(nom = "Bernard", prenom = "Sophie", telephone = "06 98 76 54 32",
                email = "sophie.bernard@example.com", latitude = 34.7406, longitude = 10.7603),
            Contact(nom = "Petit", prenom = "Lucas", telephone = "+216 71 22 33 44",
                email = "lucas.petit@example.com"),                 // sans GPS
            Contact(nom = "Moreau", prenom = "Julien", telephone = "06 55 44 33 22",
                email = "julien.moreau@example.com")                // sans GPS
        )
        for (contact in demo) {
            db.insert(ContactEntry.TABLE_NAME, null, ContactDao.versContentValues(contact))
        }
    }
}