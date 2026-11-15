package com.example.contactsappv3

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class ParametresFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onResume() {
        super.onResume()
        requireActivity().title = "Paramètres"
    }
}