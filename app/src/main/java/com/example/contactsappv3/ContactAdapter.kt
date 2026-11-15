package com.example.contactsappv3

import android.graphics.Color
import android.net.Uri
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.contactsappv3.data.Contact

class ContactAdapter(
    private val onClick: (Contact) -> Unit,
    private val onAppeler: (Contact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    private var contacts: List<Contact> = emptyList()
    private var tailleSp = 16f
    private var couleur = Color.BLACK
    private var appelActif = false

    class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgPhoto: ImageView = view.findViewById(R.id.imgPhoto)
        val tvNomPrenom: TextView = view.findViewById(R.id.tvNomPrenom)
        val tvTelephone: TextView = view.findViewById(R.id.tvTelephone)
        val btnAppelerLigne: ImageButton = view.findViewById(R.id.btnAppelerLigne)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]

        holder.tvNomPrenom.text = "${contact.nom} ${contact.prenom}"
        holder.tvNomPrenom.setTextSize(TypedValue.COMPLEX_UNIT_SP, tailleSp)
        holder.tvNomPrenom.setTextColor(couleur)

        holder.tvTelephone.text = contact.telephone
        holder.tvTelephone.setTextSize(TypedValue.COMPLEX_UNIT_SP, tailleSp)

        if (contact.photoUri != null) {
            holder.imgPhoto.setImageURI(Uri.parse(contact.photoUri))
        } else {
            holder.imgPhoto.setImageResource(R.drawable.ic_person)
        }

        holder.btnAppelerLigne.visibility = if (appelActif) View.VISIBLE else View.GONE
        holder.btnAppelerLigne.setOnClickListener { onAppeler(contact) }
        holder.itemView.setOnClickListener { onClick(contact) }
    }

    override fun getItemCount(): Int = contacts.size

    fun afficher(nouvelleListe: List<Contact>) {
        contacts = nouvelleListe
        notifyDataSetChanged()
    }

    fun appliquerPreferences(tailleSp: Float, couleur: Int, appelActif: Boolean) {
        this.tailleSp = tailleSp
        this.couleur = couleur
        this.appelActif = appelActif
        notifyDataSetChanged()
    }
}