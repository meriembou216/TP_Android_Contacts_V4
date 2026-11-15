# TP Android Contacts V4 – Bouaoun Meriem (L3DSI)

Application Android (Kotlin, XML) de gestion de contacts avec persistance SQLite
et préférences utilisateur.

## Fonctionnalités
- Base SQLite `contacts.db` (SQLiteOpenHelper, DAO, requêtes paramétrées)
- CRUD : ajout (bouton +), consultation, modification, suppression avec confirmation
- Recherche en SQL (LIKE) sur nom, prénom et téléphone, liste triée (COLLATE NOCASE)
- Préférences : taille de police, couleur du nom, bouton Appeler dans la liste
- Appeler, e-mail, galerie, caméra, carte Google Maps

## Architecture
- `data/` : Contact, ContactContract, ContactDbHelper (singleton), ContactDao
- `ContactViewModel` : accès à la base dans viewModelScope + Dispatchers.IO
- `MainActivity` + fragments : ListeContactsFragment, DetailContactFragment,
  CarteContactFragment, ParametresFragment
- `PreferencesManager` : lecture centralisée des SharedPreferences

## Configuration de la clé Google Maps
La clé n'est pas publiée. Ajouter dans `local.properties` (ignoré par Git) :
MAPS_API_KEY=VOTRE_CLE_ICI