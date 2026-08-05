# API Debugger - Client HTTP & Débugueur d'API Mobile

**API Debugger** (APIFlow) est une application Android moderne, élégante et puissante conçue pour tester, déboguer et organiser des requêtes API REST directement depuis votre smartphone ou tablette, inspirée par des outils de référence comme Postman et Insomnia.

---

## 🚀 Fonctionnalités Principales

### 1. Constructeur de Requêtes (Request Builder)
- **Méthodes HTTP complètes** : Prise en charge de `GET`, `POST`, `PUT`, `DELETE`, `PATCH`, `HEAD`, `OPTIONS` avec des badges de couleur distinctifs.
- **Éditeur de Paramètres URL** : Ajout, modification et désactivation dynamique des paramètres de requête (*Query Params*).
- **Gestionnaire d'En-têtes (Headers)** : Clés-valeurs personnalisables avec préremplissage d'en-têtes standard (`Content-Type`, `Authorization`, etc.).
- **Modes d'Authentification** :
  - *Aucune* (None)
  - *Bearer Token* (JWT)
  - *Basic Auth* (Identifiant & Mot de passe)
  - *Clé API* (Dans l'en-tête ou dans les paramètres d'URL)
- **Formats de Corps de Requête (Body)** :
  - `JSON` avec formateur automatique (*Beautify JSON*)
  - `Raw Text` / `XML`
  - `Form Data` (Clés-valeurs multipart)

### 2. Exécution & Moteur Réseau
- **Exécution Réseau Réelle** : Propulsé par OkHttp et Coroutines Kotlin pour un transfert de données rapide et fluide.
- **Analyse de Performance** : Affichage automatique du code de statut HTTP (`200 OK`, `404 Not Found`, `500 Server Error`), de la latence en millisecondes (`ms`) et de la taille de la réponse (Ko / Octets).
- **Mode Résilience / Mocking** : Génération automatique de réponses factices en cas d'absence de connexion réseau pour continuer à tester le comportement de l'interface.

### 3. Visualiseur de Réponses Interactif
- **Coloration Syntaxique JSON** : Mise en évidence des clés, des chaînes de caractères, des nombres et des booléens pour une lisibilité maximale.
- **Navigation par Onglets** : Navigation fluide entre le corps (`Body`), les en-têtes reçus (`Headers`) et les cookies.
- **Recherche & Filtrage** : Recherche instantanée de termes dans le corps de réponse JSON ou texte.
- **Actions Rapides** : Copie en un clic dans le presse-papiers et option de partage.

### 4. Collections & Organisation
- **Dossiers Personnalisés** : Organisez vos requêtes par projet ou service (ex. *User API*, *Auth Service*, *Payment Gateway*).
- **Sauvegarde Locale** : Conservez vos requêtes favorites avec leur configuration complète (URL, méthodes, en-têtes et corps).
- **Code Coloration des Dossiers** : Attribuez une couleur distincte à chaque collection.

### 5. Gestion des Environnements & Variables
- **Variables Dynamiques** : Définissez des variables globales ou spécifiques à un environnement (`{{baseUrl}}`, `{{token}}`, `{{apiKey}}`) réutilisables dans vos URLs et en-têtes.
- **Basculement Rapide** : Changez d'environnement en un clic (*Dev*, *Staging*, *Prod*).

### 6. Historique des Requêtes
- Enregistrement automatique de toutes les requêtes exécutées pour un rejeu rapide (*Replay*).

### 7. Générateur de Code
- Exportation de la requête active vers plusieurs langages et bibliothèques :
  - **cURL** (Ligne de commande)
  - **Kotlin** (OkHttp / Retrofit)
  - **JavaScript** (Fetch API)
  - **Python** (Requests)

---

## 🛠️ Stack Technique & Architecture

- **Langage** : Kotlin (100%)
- **Interface Utilisateur** : Jetpack Compose & Material Design 3 (Thème Sombre Premium)
- **Architecture** : MVVM (Model-View-ViewModel) + StateFlow
- **Base de Données Locale** : Room Database + KSP (Kotlin Symbol Processing)
- **Réseau & HTTP** : OkHttp 4 & Retrofit 2
- **Coroutines & Flow** : Gestion asynchrone et réactive des flux de données

---

## 📁 Structure du Projet

```text
com.example/
├── data/
│   ├── local/            # Entités Room DAO et base de données (Collection, Request, History)
│   ├── model/            # Modèles de données (ApiRequestState, ApiResponseResult, Enums)
│   └── network/          # Exécuteur HTTP OkHttp & Mocking Fallback
├── ui/
│   ├── components/       # Composants réutilisables (MethodBadge, KeyValueEditor, FormattedResponseViewer)
│   ├── screens/          # Écrans principaux (RequestBuilderScreen, CollectionsScreen, HistoryScreen, EnvironmentsScreen)
│   ├── theme/            # Palette de couleurs Material 3 & typographie
│   └── ApiViewModel.kt   # Gestionnaire d'état principal ViewModel
└── MainActivity.kt       # Point d'entrée principal & barre de navigation
```

---

## 🖥️ Capture de l'Interface

L'application arbore une interface sombre inspirée des environnements de développement professionnels :
- **Barre supérieure** : Sélection active de l'environnement et raccourci vers l'historique.
- **Barre de navigation inférieure** : Accès direct à **Requests**, **Collections**, **History**, et **Settings**.

---

## 📄 Licence

Ce projet est sous licence MIT.
