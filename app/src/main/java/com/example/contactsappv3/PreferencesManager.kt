package com.example.contactsappv3

import android.content.Context
import android.graphics.Color
import androidx.preference.PreferenceManager

class PreferencesManager(context: Context) {

    companion object {
        const val CLE_TAILLE = "taille_police"
        const val CLE_COULEUR = "couleur_preferee"
        const val CLE_APPEL = "appel_depuis_liste"
    }

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)

    val tailleSp: Float
        get() = prefs.getString(CLE_TAILLE, "16")?.toFloatOrNull() ?: 16f

    val couleur: Int
        get() = try {
            Color.parseColor(prefs.getString(CLE_COULEUR, "#000000"))
        } catch (e: IllegalArgumentException) {
            Color.BLACK
        }

    val appelDepuisListe: Boolean
        get() = prefs.getBoolean(CLE_APPEL, false)
}