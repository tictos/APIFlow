# 🚀 PostBoy — Client HTTP & Débugueur d'API Mobile

<div align="center">

  <!-- Logo / Hero Banner Placeholder -->
  <img src="docs/images/banner.png" alt="PostBoy Banner" width="100%" style="border-radius: 12px;" onerror="this.src='https://placehold.co/1200x400/0E0F12/3B82F6?text=PostBoy+%7C+Mobile+API+Client+%26+Debugger'; this.style.borderRadius='12px';" />

  <br/><br/>

  [![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
  [![Kotlin](https://img.shields.io/badge/Language-Kotlin%20100%25-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
  [![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
  [![Architecture](https://img.shields.io/badge/Architecture-MVVM%20%2B%20Flow-FF6D00?style=for-the-badge)](https://developer.android.com/topic/architecture)
  [![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)](LICENSE)

  <p align="center">
    <strong>PostBoy</strong> est une application Android moderne, performante et épurée conçue pour concevoir, tester, déboguer et organiser des requêtes API REST en déplacement, directement depuis un smartphone ou une tablette.
  </p>

  <p align="center">
    Développé avec passion par <strong>Mamadou Bobo Diallo (Tictos)</strong>
  </p>

</div>

---

## 👨‍💻 À propos de l'Auteur & Portfolio

| Développeur | **Mamadou Bobo Diallo** |
| :--- | :--- |
| **Surnom / Pseudo** | **Tictos** |
| **Rôle** | Concepteur & Développeur Android / Mobile |
| **Spécialités** | Kotlin, Jetpack Compose, Architecture Android moderne (MVVM / Clean Architecture), Intégrations API & UI/UX Design |
| **Portfolio / Contact** | [![Email](https://img.shields.io/badge/Contact-Email-D14836?style=flat-square&logo=gmail&logoColor=white)](mailto:mohamadoudiallo1213@gmail.com) [![GitHub](https://img.shields.io/badge/GitHub-Profile-181717?style=flat-square&logo=github&logoColor=white)](https://github.com/) [![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-0A66C2?style=flat-square&logo=linkedin&logoColor=white)](https://linkedin.com/) |

---

## 📸 Galerie & Captures d'Écran (Showcase UI)

<div align="center">

| 1. Constructeur de Requête | 2. Visualiseur de Réponse | 3. Collections & Projets | 4. Variables d'Environnement |
| :---: | :---: | :---: | :---: |
| <img src="docs/images/screenshot_builder.png" alt="Request Builder" width="220" onerror="this.src='https://placehold.co/300x650/111216/3B82F6?text=1.+Request+Builder';" /> | <img src="docs/images/screenshot_response.png" alt="Response Viewer" width="220" onerror="this.src='https://placehold.co/300x650/111216/10B981?text=2.+Response+Viewer';" /> | <img src="docs/images/screenshot_collections.png" alt="Collections" width="220" onerror="this.src='https://placehold.co/300x650/111216/EF4444?text=3.+Collections';" /> | <img src="docs/images/screenshot_env.png" alt="Environments" width="220" onerror="this.src='https://placehold.co/300x650/111216/F59E0B?text=4.+Environments';" /> |

</div>

---

## ✨ Fonctionnalités Clés

### ⚡ 1. Constructeur de Requêtes Avancé (Request Builder)
- **Support complet des verbes HTTP** : `GET`, `POST`, `PUT`, `DELETE`, `PATCH`, `HEAD`, `OPTIONS` avec indicateurs visuels distinctifs.
- **Gestionnaire d'URL & Paramètres de requête (*Query Params*)** : Édition clé-valeur avec possibilité d'activer ou désactiver des entrées individuellement.
- **Gestionnaire d'En-têtes (*Headers*)** : Configuration intuitive des headers HTTP (`Content-Type`, `User-Agent`, `Authorization`, etc.).
- **Modes d'Authentification Intégrés** :
  - `None` (Aucune)
  - `Bearer Token` (JWT & jetons d'accès)
  - `Basic Auth` (Identifiant & mot de passe)
  - `API Key` (Placement personnalisable dans l'en-tête ou en paramètre d'URL)
- **Gestionnaire de Corps de Requête (*Body*)** :
  - **JSON** avec formateur et indentation automatique (*Beautify JSON*).
  - **Raw Text** / **XML**.
  - **Form Data** (Gestion des formulaires multipart clé-valeur).

---

### 🔍 2. Visualiseur de Réponses Interactif & Performant
- **Coloration Syntaxique du JSON** : Mise en valeur intelligente des clés, chaînes, valeurs numériques, booléens et `null`.
- **Navigation Multi-Onglets** :
  - `Body` : Affichage du corps de réponse avec défilement fluide horizontal et vertical.
  - `Headers` : Liste détaillée des en-têtes retournés par le serveur avec compteur dynamique.
  - `Cookies` : Consultation des cookies de session.
- **Métriques en temps réel** : Code de statut HTTP (`200 OK`, `401 Unauthorized`, `500 Error`), temps de latence (`ms`), et taille exacte du paquet reçu (`Ko` / `Octets`).
- **Recherche & Filtrage en direct** : Recherche instantanée de termes au sein des réponses JSON complexes.
- **Outils Pratiques** : Copie instantanée dans le presse-papiers et réduction du corps (*Collapse All*).

---

### 📂 3. Organisation en Collections & Historique
- **Dossiers de Projets** : Regroupez vos endpoints par microservices ou fonctionnalités (*ex: User API, Auth Service, Payment Gateway*).
- **Historique Automatique** : Retrouvez l'historique complet de toutes vos exécutions et rechargez-les en un clic.
- **Codes Couleurs Personnalisables** : Identifiez vos collections d'un coup d'œil.

---

### 🌐 4. Gestion des Variables & Environnements
- **Variables Dynamiques** : Injectez des variables (`{{baseUrl}}`, `{{token}}`, `{{apiKey}}`) dans vos URLs, en-têtes et corps de requête.
- **Basculement instantané** : Passez d'un environnement à l'autre en un tap (*Développement*, *Staging*, *Production*).

---

### 💻 5. Générateur d'Extraits de Code (Code Snippets)
Exportez instantanément votre requête active vers le langage ou la bibliothèque de votre choix :
- **cURL** (Bash / Terminal)
- **Kotlin** (OkHttp / Retrofit)
- **JavaScript** (Fetch API)
- **Python** (Requests)

---

## 🏗️ Architecture & Technologies Utilisées

```
PostBoy/
│
├── 🎨 UI Layer (Jetpack Compose + Material 3)
│   ├── Screens (RequestBuilder, Collections, History, Environments)
│   ├── Components (MethodBadge, KeyValueEditor, FormattedResponseViewer)
│   └── Theme (Dark IDE Palette, Custom Typography)
│
├── 🧠 State & ViewModel (MVVM)
│   └── ApiViewModel (StateFlow, Coroutines, Gestion réactive de l'UI)
│
├── 🌐 Network Engine
│   ├── HttpExecutor (Moteur d'exécution OkHttp 4)
│   └── Network Fallback (Simulation & mode hors-ligne résilient)
│
└── 💾 Local Data Persistence (Room Database)
    ├── Entities (CollectionEntity, SavedRequestEntity, HistoryEntity)
    └── DAOs (Accès asynchrone sécurisé via KSP)
```

| Composant | Technologie / Librairie | Rôle |
| :--- | :--- | :--- |
| **Langage** | **Kotlin 2.0+** | Développement Android 100% natif |
| **UI Toolkit** | **Jetpack Compose & Material 3** | Interface déclarative, réactive et moderne |
| **Concurrence** | **Kotlin Coroutines & StateFlow** | Gestion asynchrone des flux et appels réseau non-bloquants |
| **Moteur Réseau** | **OkHttp 4** | Gestion des requêtes HTTP/HTTPS, compression et headers |
| **Persistance** | **Room Database + KSP** | Stockage SQLite local des collections, requêtes et historiques |
| **Design** | **Custom Dark IDE Theme** | Palette sombre professionnelle inspirée des IDEs de référence |

---

## 📦 Installation & Exécution en Local

### Prérequis
- Android Studio Ladybug (ou version plus récente)
- JDK 17+
- Appareil Android ou Émulateur avec **Android 8.0 (API 26)** ou supérieur

### Étapes

1. **Cloner le dépôt :**
   ```bash
   git clone https://github.com/votre-compte/PostBoy.git
   cd PostBoy
   ```

2. **Ouvrir le projet :**
   Lancez **Android Studio** et ouvrez le dossier racine du projet.

3. **Compiler & Lancer :**
   - Synchronisez les fichiers Gradle.
   - Sélectionnez votre appareil cible (émulateur ou smartphone physique).
   - Cliquez sur **Run (Shift + F10)**.

---

## 🔒 Confidentialité & Sécurité des Données

- **100% On-Device** : Vos clés d'API, jetons d'authentification et requêtes sont stockés exclusivement en local sur votre appareil via la base de données Room sécurisée.
- **Aucune Télémétrie Invasve** : Aucune donnée de requête ni information personnelle n'est envoyée à des serveurs tiers.

---

## 🤝 Contribution & Contact

Les suggestions et contributions sont les bienvenues ! Pour toute question, proposition d'amélioration ou opportunité professionnelle :

- **Auteur** : Mamadou Bobo Diallo (**Tictos**)
- **Email** : [mohamadoudiallo1213@gmail.com](mailto:mohamadoudiallo1213@gmail.com)

---

## 📜 Licence

Ce projet est distribué sous la licence **MIT**. Vous êtes libre de l'utiliser, l'étudier et l'adapter selon vos besoins.
