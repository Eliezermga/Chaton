# Rapport sur le Projet Chaton

## 1. Description du projet

"Chaton" est une application de messagerie instantanée que nous avons développée. Son objectif est de permettre aux utilisateurs de communiquer entre eux de manière simple et instantanée.

**Utilité et fonctionnement :**

L'application permet aux utilisateurs de s'inscrire et de se connecter. Une fois connectés, ils peuvent voir une liste des autres utilisateurs inscrits et démarrer une nouvelle conversation en tête-à-tête ou continuer une discussion existante. Au sein d'une conversation, les messages sont envoyés et reçus en temps réel. L'application s'appuie entièrement sur les services Firebase pour son backend, ce qui inclut l'authentification des utilisateurs, la base de données en temps réel Firestore pour le stockage des messages et des conversations, ainsi que les notifications push pour informer les utilisateurs de nouveaux messages.

## 2. Structure du projet

Voici la structure que nous avons mise en place à la racine de notre projet :

*   `.git`: Dossier contenant les informations de notre repository Git.
*   `.idea`: Fichiers de configuration de l'IDE Android Studio.
*   `app`: Le module principal de notre application Android.
*   `gradle`: Contient le Gradle Wrapper, qui nous permet d'exécuter des tâches Gradle.
*   `gradlew` & `gradlew.bat`: Scripts pour exécuter le Gradle Wrapper.
*   `README.md`: Le fichier README de notre projet.
*   `.gitignore`: Fichier spécifiant les fichiers que Git doit ignorer.
*   `desing.json`: Un fichier de design pour la conception de l'interface.
*   `build.gradle.kts`: Notre fichier de build Gradle pour le projet.
*   `gradle.properties`: Fichier de propriétés pour Gradle.
*   `settings.gradle.kts`: Fichier de configuration des modules du projet.

## 3. Analyse du `build.gradle.kts`

Le fichier `build.gradle.kts` de notre module `app` contient la configuration et les dépendances suivantes :

*   **Configuration Android :**
    *   `namespace`: `com.mecatrogenie.chaton`
    *   `compileSdk`: 36
    *   `minSdk`: 24
    *   `targetSdk`: 36
    *   `versionCode`: 1
    *   `versionName`: "1.0"

*   **Dépendances utilisées :**
    *   **Firebase:**
        *   `firebase-auth`: Pour l'authentification des utilisateurs.
        *   `firebase-firestore`: Pour la base de données NoSQL en temps réel.
        *   `firebase-messaging`: Pour l'envoi de notifications push.
        *   `play-services-auth`: Pour l'intégration de l'authentification Google.
    *   **Interface Utilisateur (UI) :**
        *   `material`: Pour les composants Material Design.
        *   `constraintlayout`: Pour la création de layouts complexes.
        *   `coil`: Pour le chargement et l'affichage d'images.
        *   `circleindicator`: Pour afficher un indicateur de page.
        *   `lottie`: Pour intégrer des animations Lottie.
    *   **Tests :**
        *   `junit`: Pour les tests unitaires.
        *   `androidx.junit` et `androidx.espresso.core`: Pour les tests d'intégration et d'UI.

## 4. Architecture de l'application

Nous avons développé "Chaton" comme une application de messagerie instantanée en nous appuyant sur les services de **Firebase** pour le backend. L'architecture que nous avons suivie s'apparente à un modèle **Model-View-Controller (MVC)** :

*   **Modèle :** Les données sont représentées par nos objets (`User`, `Chat`, `Message`) et stockées dans **Firestore**.
*   **Vue :** Nos activités (`MainActivity`, `ChatActivity`, etc.) et leurs layouts XML sont responsables de l'affichage de l'interface.
*   **Contrôleur :** Nous avons placé la logique de l'application (gestion des interactions, récupération des données, mise à jour de la vue) directement dans les activités.

### 4.1. `MainActivity.kt`

`MainActivity` est le point d'entrée de l'application pour les utilisateurs connectés. Ses responsabilités sont :

*   **Vérification de l'authentification :** Rediriger vers l'écran de connexion si l'utilisateur n'est pas authentifié.
*   **Affichage des conversations :** Récupérer et afficher la liste des conversations de l'utilisateur depuis Firestore.
*   **Gestion des conversations non lues :** Déterminer si un message a été lu.
*   **Navigation :** Permettre de démarrer une nouvelle conversation ou d'accéder aux paramètres.
*   **Recherche :** Fournir une barre de recherche pour filtrer les conversations.
*   **Déconnexion :** Gérer la déconnexion et rediriger vers l'écran de connexion.

### 4.2. `NewChatActivity.kt`

Nous avons créé `NewChatActivity` pour gérer la création de nouvelles conversations. Elle assure les fonctions suivantes :

*   **Chargement des utilisateurs :** Récupérer et afficher tous les utilisateurs sauf l'utilisateur actuel.
*   **Recherche d'utilisateurs :** Permettre de trouver un utilisateur dans la liste.
*   **Création ou ouverture d'une conversation :** Ouvrir une conversation existante ou en créer une nouvelle si elle n'existe pas.
*   **Navigation :** Rediriger l'utilisateur vers `ChatActivity` une fois la conversation prête.

### 4.3. `ChatActivity.kt`

Dans `ChatActivity`, nous gérons l'affichage et l'envoi des messages. Ses fonctionnalités sont :

*   **Affichage des messages :** Charger et afficher les messages en temps réel.
*   **Envoi de messages :** Permettre à l'utilisateur d'envoyer de nouveaux messages.
*   **Marqueur de lecture :** Mettre à jour un timestamp pour marquer les messages comme lus.
*   **Gestion du clavier :** Ajuster l'interface pour que la saisie de texte reste visible.

## 5. Conclusion et Pistes d'Amélioration

Le projet "Chaton" est une application de chat fonctionnelle que nous avons développée en utilisant Firebase. Le code est structuré et fonctionnel.

Pour l'avenir, voici quelques pistes d'amélioration que nous envisageons :

*   **Gestion des erreurs :** Nous pourrions améliorer l'expérience utilisateur en affichant des messages plus clairs en cas d'échec des opérations réseau.
*   **Architecture :** Nous pourrions faire évoluer l'architecture vers **MVVM (Model-View-ViewModel)** pour mieux séparer la logique de l'interface, ce qui faciliterait les tests et la gestion de l'état.
*   **Tests :** Il serait bénéfique d'écrire plus de tests unitaires et d'intégration pour garantir la fiabilité de l'application à mesure qu'elle évolue.
*   **Interface utilisateur :** Nous pourrions explorer **Jetpack Compose** pour moderniser et simplifier le développement de l'interface.

Dans l'ensemble, c'est un projet avec une base solide, et ces améliorations pourraient le rendre plus robuste et moderne.
