package com.example.contactsappv3.data

import android.content.ContentValues
import android.database.Cursor
import com.example.contactsappv3.data.ContactContract.ContactEntry

class ContactDao(private val helper: ContactDbHelper) {

    companion object {

        private const val ORDRE =
            "${ContactEntry.COL_NOM} COLLATE NOCASE ASC, ${ContactEntry.COL_PRENOM} COLLATE NOCASE ASC"


        fun versContentValues(c: Contact): ContentValues {
            val v = ContentValues()
            v.put(ContactEntry.COL_NOM, c.nom)
            v.put(ContactEntry.COL_PRENOM, c.prenom)
            v.put(ContactEntry.COL_TELEPHONE, c.telephone)
            v.put(ContactEntry.COL_EMAIL, c.email)
            v.put(ContactEntry.COL_PHOTO_URI, c.photoUri)
            if (c.latitude != null) v.put(ContactEntry.COL_LATITUDE, c.latitude)
            else v.putNull(ContactEntry.COL_LATITUDE)
            if (c.longitude != null) v.put(ContactEntry.COL_LONGITUDE, c.longitude)
            else v.putNull(ContactEntry.COL_LONGITUDE)
            return v
        }
    }


    fun insert(contact: Contact): Long {
        return helper.writableDatabase.insert(ContactEntry.TABLE_NAME, null, versContentValues(contact))
    }


    fun update(contact: Contact): Int {
        return helper.writableDatabase.update(
            ContactEntry.TABLE_NAME,
            versContentValues(contact),
            "${ContactEntry.COL_ID} = ?",
            arrayOf(contact.id.toString())
        )
    }


    fun delete(id: Long): Int {
        return helper.writableDatabase.delete(
            ContactEntry.TABLE_NAME,
            "${ContactEntry.COL_ID} = ?",
            arrayOf(id.toString())
        )
    }


    fun getAll(): List<Contact> {
        helper.readableDatabase.query(
            ContactEntry.TABLE_NAME, null, null, null, null, null, ORDRE
        ).use { cursor ->
            return lireListe(cursor)
        }
    }


    fun getById(id: Long): Contact? {
        helper.readableDatabase.query(
            ContactEntry.TABLE_NAME, null,
            "${ContactEntry.COL_ID} = ?", arrayOf(id.toString()),
            null, null, null
        ).use { cursor ->
            return if (cursor.moveToFirst()) lireContact(cursor) else null
        }
    }


    fun search(texte: String): List<Contact> {
        val motif = "%${texte.trim()}%"
        val motifTel = "%${texte.replace(" ", "")}%"
        val selection = """
            ${ContactEntry.COL_NOM} LIKE ? OR
            ${ContactEntry.COL_PRENOM} LIKE ? OR
            (${ContactEntry.COL_PRENOM} || ' ' || ${ContactEntry.COL_NOM}) LIKE ? OR
            (${ContactEntry.COL_NOM} || ' ' || ${ContactEntry.COL_PRENOM}) LIKE ? OR
            REPLACE(${ContactEntry.COL_TELEPHONE}, ' ', '') LIKE ?
        """
        helper.readableDatabase.query(
            ContactEntry.TABLE_NAME, null, selection,
            arrayOf(motif, motif, motif, motif, motifTel),
            null, null, ORDRE
        ).use { cursor ->
            return lireListe(cursor)
        }
    }


    private fun lireListe(cursor: Cursor): List<Contact> {
        val liste = mutableListOf<Contact>()
        while (cursor.moveToNext()) {
            liste.add(lireContact(cursor))
        }
        return liste
    }

    private fun lireContact(cursor: Cursor): Contact {
        val iEmail = cursor.getColumnIndexOrThrow(ContactEntry.COL_EMAIL)
        val iPhoto = cursor.getColumnIndexOrThrow(ContactEntry.COL_PHOTO_URI)
        val iLat = cursor.getColumnIndexOrThrow(ContactEntry.COL_LATITUDE)
        val iLng = cursor.getColumnIndexOrThrow(ContactEntry.COL_LONGITUDE)
        return Contact(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(ContactEntry.COL_ID)),
            nom = cursor.getString(cursor.getColumnIndexOrThrow(ContactEntry.COL_NOM)),
            prenom = cursor.getString(cursor.getColumnIndexOrThrow(ContactEntry.COL_PRENOM)),
            telephone = cursor.getString(cursor.getColumnIndexOrThrow(ContactEntry.COL_TELEPHONE)),
            email = if (cursor.isNull(iEmail)) null else cursor.getString(iEmail),
            photoUri = if (cursor.isNull(iPhoto)) null else cursor.getString(iPhoto),
            latitude = if (cursor.isNull(iLat)) null else cursor.getDouble(iLat),
            longitude = if (cursor.isNull(iLng)) null else cursor.getDouble(iLng)
        )
    }
}