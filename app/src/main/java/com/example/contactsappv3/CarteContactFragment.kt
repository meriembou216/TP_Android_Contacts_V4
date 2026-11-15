package com.example.contactsappv3

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class CarteContactFragment : Fragment(R.layout.fragment_carte_contact), OnMapReadyCallback {

    companion object {
        private const val ARG_LAT = "latitude"
        private const val ARG_LNG = "longitude"
        private const val ARG_NOM = "nom"

        fun newInstance(latitude: Double, longitude: Double, nom: String): CarteContactFragment {
            val fragment = CarteContactFragment()
            val bundle = Bundle()
            bundle.putDouble(ARG_LAT, latitude)
            bundle.putDouble(ARG_LNG, longitude)
            bundle.putString(ARG_NOM, nom)
            fragment.arguments = bundle
            return fragment
        }
    }

    private var latitude = 0.0
    private var longitude = 0.0
    private var nom = ""
    private lateinit var tvMessage: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvMessage = view.findViewById(R.id.tvMessageCarte)

        val args = requireArguments()
        latitude = args.getDouble(ARG_LAT, Double.NaN)
        longitude = args.getDouble(ARG_LNG, Double.NaN)
        nom = args.getString(ARG_NOM) ?: "Contact"

        if (latitude.isNaN() || longitude.isNaN() ||
            latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180
        ) {
            afficherMessage("Coordonnées GPS absentes ou invalides pour ce contact.")
            view.findViewById<View>(R.id.map_container).visibility = View.GONE
            return
        }

        if (!cleMapsConfiguree()) {
            afficherMessage("Clé API Google Maps non configurée : le fond de carte ne peut pas être chargé.")
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_container) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onResume() {
        super.onResume()
        requireActivity().title = "Position de $nom"
    }

    override fun onMapReady(carte: GoogleMap) {
        val position = LatLng(latitude, longitude)
        carte.addMarker(
            MarkerOptions()
                .position(position)
                .title(nom)
                .snippet("Lat : $latitude, Lng : $longitude")
        )?.showInfoWindow()
        carte.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 12f))
        carte.uiSettings.isZoomControlsEnabled = true
    }

    private fun afficherMessage(texte: String) {
        tvMessage.text = texte
        tvMessage.visibility = View.VISIBLE
    }

    private fun cleMapsConfiguree(): Boolean {
        return try {
            val info = requireContext().packageManager.getApplicationInfo(
                requireContext().packageName, PackageManager.GET_META_DATA
            )
            val cle = info.metaData?.getString("com.google.android.geo.API_KEY")
            !cle.isNullOrBlank() && cle != "CLE_A_CONFIGURER"
        } catch (e: Exception) {
            false
        }
    }
}