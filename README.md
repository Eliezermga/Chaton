# Chaton

Chaton est une application de messagerie instantanée pour Android, conçue pour permettre des conversations simples et en temps réel. Elle s'appuie sur Firebase pour l'authentification, la base de données et les notifications.

## Fonctionnalités

*   **Authentification Google :** Connexion simple et sécurisée via les comptes Google.
*   **Intégration (Onboarding) :** Une série d'écrans de bienvenue pour guider les nouveaux utilisateurs.
*   **Messagerie en Temps Réel :** Des discussions fluides avec des mises à jour instantanées grâce à Firestore.
*   **Liste de Discussions :** Un écran principal qui affiche toutes les conversations en cours et indique les messages non lus.
*   **Création de Nouvelles Discussions :** Lancez de nouvelles conversations en sélectionnant un utilisateur dans une liste.
*   **Recherche d'Utilisateurs :** Trouvez facilement d'autres utilisateurs pour commencer à discuter.
*   **Notifications Push :** Restez informé des nouveaux messages même lorsque l'application est en arrière-plan.
*   **Design Moderne :** Une interface utilisateur épurée et moderne basée sur les derniers principes de Material 3.

## Installation

Pour utiliser cette application depuis son dépôt GitHub, suivez ces étapes :

1.  **Cloner le Dépôt**
    ```bash
    git clone https://github.com/Eliezermga/Chaton
    ```

2.  **Ouvrir dans Android Studio**
    *   Lancez Android Studio.
    *   Choisissez `Open` et sélectionnez le dossier du projet que vous venez de cloner.

3.  **Configurer Firebase**
    Cette application utilise Firebase. Pour qu'elle fonctionne, vous devez la connecter à votre propre projet Firebase.
    *   Rendez-vous sur la [console Firebase](https://console.firebase.google.com/).
    *   Créez un nouveau projet.
    *   Ajoutez une application Android à ce projet.
        *   Le nom du package doit être `com.mecatrogenie.chaton`.
    *   Téléchargez le fichier de configuration `google-services.json`.
    *   Placez ce fichier dans le dossier `app` de votre projet (`Chaton/app/`).

4.  **Compiler et Lancer l'Application**
    *   Attendez que Gradle synchronise le projet.
    *   Lancez l'application sur un émulateur ou un appareil physique.

## Fonctionnement et Technologies

L'application est construite autour des technologies modernes du développement Android.

*   **Langage :** L'application est entièrement écrite en **Kotlin**.
*   **Architecture UI :** Elle utilise des **Activités** et des **Vues XML** avec les dernières versions des bibliothèques AndroidX.
*   **Design :** L'interface utilisateur est basée sur **Material 3**, offrant un design moderne et personnalisable.
*   **Bibliothèques Principales :**
    *   **Android KTX :** Pour un code Kotlin plus concis et idiomatique.
    *   **Material Components :** Pour les composants de l'interface utilisateur.
    *   **RecyclerView & ViewPager2 :** Pour l'affichage de listes et d'écrans glissants.
    *   **Glide :** Pour le chargement et la mise en cache d'images.
*   **Services Firebase :**
    *   **Firebase Authentication :** Gère l'authentification des utilisateurs de manière sécurisée via le fournisseur de connexion Google.
    *   **Cloud Firestore :** Utilisé comme base de données NoSQL en temps réel pour stocker :
        *   Les informations des utilisateurs (`users`).
        *   Les conversations (`chats`).
        *   Les messages (`messages`).
    *   **Firebase Cloud Messaging (FCM) :** Permet d'envoyer des notifications push pour les nouveaux messages.
