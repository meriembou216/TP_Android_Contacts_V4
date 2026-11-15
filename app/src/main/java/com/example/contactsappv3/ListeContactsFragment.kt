package com.example.contactsappv3

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.contactsappv3.data.Contact
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ListeContactsFragment : Fragment(R.layout.fragment_liste_contacts) {

    private val viewModel: ContactViewModel by activityViewModels()
    private lateinit var adapter: ContactAdapter
    private lateinit var etRecherche: EditText
    private lateinit var tvAucunResultat: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etRecherche = view.findViewById(R.id.etRecherche)
        tvAucunResultat = view.findViewById(R.id.tvAucunResultat)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerContacts)
        adapter = ContactAdapter(
            onClick = { contact -> ouvrirDetail(contact.id) },
            onAppeler = { contact -> appeler(contact) }
        )
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter
        recycler.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )

        view.findViewById<FloatingActionButton>(R.id.fabAjouter).setOnClickListener {
            ouvrirDetail(0L)
        }

        viewModel.contacts.observe(viewLifecycleOwner) { liste ->
            adapter.afficher(liste)
            if (liste.isEmpty()) {
                tvAucunResultat.text = if (viewModel.filtre.isEmpty()) {
                    "Aucun contact enregistré"
                } else {
                    "Aucun contact ne correspond à la recherche"
                }
                tvAucunResultat.visibility = View.VISIBLE
            } else {
                tvAucunResultat.visibility = View.GONE
            }
        }

        etRecherche.doAfterTextChanged { texte ->
            viewModel.rechercher(texte?.toString() ?: "")
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_liste, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.action_parametres) {
                    ouvrirParametres()
                    return true
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onResume() {
        super.onResume()
        requireActivity().title = "Mes contacts"
        val prefs = PreferencesManager(requireContext())
        adapter.appliquerPreferences(prefs.tailleSp, prefs.couleur, prefs.appelDepuisListe)
        viewModel.rechercher(etRecherche.text.toString())
    }

    private fun appeler(contact: Contact) {
        val numero = contact.telephone.trim()
        if (numero.isEmpty()) {
            Toast.makeText(requireContext(), "Ce contact n'a pas de numéro", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            startActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", numero, null)))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "Aucune application de téléphone trouvée", Toast.LENGTH_LONG).show()
        }
    }

    private fun ouvrirDetail(id: Long) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, DetailContactFragment.newInstance(id))
            .addToBackStack(null)
            .commit()
    }

    private fun ouvrirParametres() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, ParametresFragment())
            .addToBackStack(null)
            .commit()
    }
}