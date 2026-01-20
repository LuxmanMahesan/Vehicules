# GES Véhicules - Application de Gestion d'Entrée/Sortie de Véhicules

Application Android native en Jetpack Compose pour gérer les entrées et sorties de véhicules sur le site du **Jardin d'Acclimatation**.

## 📋 Table des matières

- [Fonctionnalités](#-fonctionnalités)
- [Architecture](#-architecture)
- [Prérequis](#-prérequis)
- [Installation](#-installation)
- [Utilisation](#-utilisation)
- [Structure du projet](#-structure-du-projet)
- [Gestion des shifts](#-gestion-des-shifts)
- [Génération de PDF](#-génération-de-pdf)
- [FAQ](#-faq)

---

## 🚀 Fonctionnalités

### Gestion des véhicules
- ✅ **Enregistrement de l'arrivée** d'un véhicule avec :
  - Plaque d'immatriculation
  - Nom de l'entreprise/transporteur
  - Nom du chauffeur
  - Téléphone du chauffeur (optionnel)
  - Destination sur site (Entrepôt, Atelier, Bureaux, Boreal, Restauration, Autre)
  - Contact sur site
  - Photo du bon de livraison
  - Notes (optionnel)
  - Porte d'entrée (Porte Eugénie par défaut)

- ✅ **Enregistrement de la sortie** d'un véhicule avec :
  - Choix de la porte de sortie
  - Calcul automatique du temps passé sur site

- ✅ **Rétablissement sur site** : Possibilité de remettre un véhicule sorti comme présent (en cas d'erreur)

- ✅ **Édition** d'une entrée de véhicule

### Affichage
- 📱 **Écran d'accueil** : Liste de tous les véhicules actuellement sur site
  - Triés par ordre d'arrivée (le plus ancien en haut)
  - Compteur du nombre de véhicules présents
  - Bouton pour marquer la sortie
  - Bouton pour éditer

- 📊 **Véhicules du shift** : Liste de tous les véhicules du shift en cours
  - **Véhicules sur site** : Affichés en ROUGE avec fond coloré
  - **Véhicules sortis** : Affichés en NOIR
  - Triés par ordre d'arrivée (le plus ancien en haut)
  - Bouton "Rétablir sur site" pour les véhicules sortis

### Gestion des shifts
- ⏰ **2 shifts par jour** :
  - Shift 1 : 07h00 - 19h00
  - Shift 2 : 19h00 - 07h00 (lendemain)

- 🔔 **Modal de changement de shift** :
  - Apparaît automatiquement à 7h et 19h
  - Demande le nom de l'agent qui prend le shift
  - Non-dismissible (obligatoire)
  - Intelligente : Ne s'affiche pas pendant l'ajout/édition d'un véhicule, mais juste après

### Génération de rapports PDF
- 📄 **Rapport PDF automatique** à chaque changement de shift contenant :
  - En-tête avec date, site, agent, numéro de shift
  - Liste de TOUS les véhicules (présents ET sortis)
  - **Véhicules encore sur site** : En ROUGE avec mention "(ENCORE SUR SITE)"
  - **Véhicules sortis** : En noir avec temps passé sur site
  - Informations détaillées : plaque, société, chauffeur, téléphone, destination, contact site, portes d'entrée/sortie, horaires, notes
  - Section photos des bons de livraison
  - Layout 2 colonnes optimisé pour éviter les débordements
  - Gestion intelligente du wrapping de texte

- 🗑️ **Nettoyage automatique** : Suppression des PDFs de plus de 7 jours

- 📧 **Envoi par email** : Configuration SMTP pour envoi automatique des rapports

### Historique
- 📚 **Consultation des PDFs** : Accès à tous les rapports générés (7 derniers jours)
- 📤 **Partage** : Possibilité de partager/ouvrir les PDFs

---

## 🏗️ Architecture

L'application suit une **Clean Architecture** avec séparation claire des couches :

```
app/
├── domain/              # Logique métier
│   ├── model/          # Entités métier (VehicleEntry, Shift, etc.)
│   ├── usecase/        # Cas d'usage
│   └── repository/     # Interfaces des repositories
├── data/               # Couche de données
│   ├── local/         # Base de données Room
│   │   ├── dao/       # Data Access Objects
│   │   ├── entity/    # Entités de base de données
│   │   └── db/        # Configuration de la base
│   ├── mapper/        # Conversion Entity ↔ Domain
│   └── repository/    # Implémentations des repositories
├── ui/                # Interface utilisateur (Jetpack Compose)
│   ├── screen_*/      # Écrans de l'application
│   ├── components/    # Composants réutilisables
│   ├── navigation/    # Navigation
│   └── theme/         # Thème Material 3
├── service/           # Services (WorkManager pour les shifts)
├── util/              # Utilitaires
│   ├── pdf/          # Génération de PDF
│   └── email/        # Envoi d'emails
└── di/                # Injection de dépendances
```

### Technologies utilisées
- **Kotlin** : Langage principal
- **Jetpack Compose** : UI moderne et déclarative
- **Room Database** : Persistance locale
- **Coroutines & Flow** : Programmation asynchrone et réactive
- **WorkManager** : Tâches en arrière-plan (changement de shift)
- **CameraX** : Prise de photo
- **Android PDF API** : Génération de PDF
- **JavaMail** : Envoi d'emails

---

## 📱 Prérequis

- **Android Studio** : Hedgehog (2023.1.1) ou supérieur
- **Android SDK** : API 26 (Android 8.0 Oreo) minimum
- **JDK** : Version 17 ou supérieur
- **Gradle** : Version 8.13 (inclus via Gradle Wrapper)

---

## 🔧 Installation

### 1. Cloner le repository

```bash
git clone https://github.com/LuxmanMahesan/Vehicules.git
cd Vehicules
git checkout claude/fix-vehicle-synchronization-8hOnk
```

### 2. Ouvrir dans Android Studio

1. Lancez **Android Studio**
2. Cliquez sur **"Open"** (ou File → Open)
3. Naviguez vers le dossier `Vehicules/GES_vehicules`
4. Cliquez sur **"OK"**

### 3. Synchroniser Gradle

Android Studio va automatiquement détecter le projet Gradle et vous proposer de synchroniser.
- Cliquez sur **"Sync Now"** dans la bannière qui apparaît
- Ou cliquez sur l'icône d'éléphant 🐘 dans la barre d'outils (Sync Project with Gradle Files)
- Attendez que la synchronisation se termine (peut prendre quelques minutes la première fois)

### 4. Configurer un appareil

#### Option A - Émulateur Android (recommandé pour les tests)

1. Allez dans **Tools → Device Manager**
2. Cliquez sur **"Create Device"**
3. Sélectionnez un appareil (ex: Pixel 6)
4. Choisissez une image système (API 34 ou supérieur recommandé)
5. Cliquez sur **"Finish"**

#### Option B - Appareil physique

1. Activez le **Mode développeur** sur votre appareil Android :
   - Allez dans Paramètres → À propos du téléphone
   - Tapez 7 fois sur "Numéro de build"
2. Activez le **Débogage USB** :
   - Paramètres → Système → Options pour les développeurs → Débogage USB
3. Connectez votre téléphone avec un câble USB
4. Autorisez le débogage USB quand demandé

### 5. Lancer l'application

1. Sélectionnez votre appareil (émulateur ou physique) dans la liste déroulante en haut
2. Cliquez sur le bouton **Run** ▶️ (vert) ou appuyez sur **Shift + F10**
3. L'application va se compiler (première compilation peut prendre 2-3 minutes)
4. L'APK sera installé et l'application se lancera automatiquement

---

## 📖 Utilisation

### Premier lancement

1. **Modal de shift** : Au premier lancement, une modal vous demandera d'entrer votre nom et prénom
2. **Permissions** : Acceptez les permissions demandées (caméra, stockage)
3. **Configuration email** (optionnel) : Allez dans le menu pour configurer l'envoi automatique des PDFs par email

### Enregistrer l'arrivée d'un véhicule

1. Sur l'écran d'accueil, cliquez sur le bouton **"Ajouter"** (➕)
2. Remplissez le formulaire :
   - Plaque d'immatriculation (obligatoire)
   - Nom de l'entreprise (obligatoire)
   - Nom du chauffeur (obligatoire)
   - Téléphone du chauffeur
   - Destination sur site
   - Contact sur site (obligatoire)
   - Notes
   - Photo du bon de livraison
3. Cliquez sur **"Enregistrer"**
4. Le véhicule apparaît immédiatement sur l'écran d'accueil

### Enregistrer la sortie d'un véhicule

1. Sur l'écran d'accueil, trouvez le véhicule dans la liste
2. Cliquez sur le bouton **"Marquer sortie"**
3. Choisissez la porte de sortie
4. Le véhicule disparaît de l'écran d'accueil et apparaît dans "Véhicules du shift" en noir

### Consulter les véhicules du shift

1. Sur l'écran d'accueil, cliquez sur **"Véhicules du shift"**
2. Vous verrez :
   - **Véhicules sur site** (en rouge) : Encore présents sur le site
   - **Véhicules sortis** (en noir) : Déjà partis pendant ce shift
3. Pour rétablir un véhicule sorti (en cas d'erreur), cliquez sur **"Rétablir sur site"**

### Éditer un véhicule

1. Sur l'écran d'accueil, cliquez sur le bouton **"Éditer"** du véhicule
2. Modifiez les informations souhaitées
3. Cliquez sur **"Enregistrer les modifications"**

### Changement de shift (7h ou 19h)

1. À 7h et 19h, une modal apparaît automatiquement
2. Entrez votre nom et prénom
3. Cliquez sur **"Confirmer"**
4. Un PDF est automatiquement généré et envoyé par email (si configuré)
5. Tous les véhicules sortis sont supprimés de la base
6. Les véhicules encore sur site restent visibles

### Consulter l'historique des PDFs

1. Allez dans **"Véhicules du shift"**
2. Cliquez sur **"Historique PDF"**
3. Vous verrez la liste des 7 derniers jours de rapports
4. Cliquez sur un rapport pour l'ouvrir ou le partager

---

## 📂 Structure du projet

```
GES_vehicules/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/ges/vehiclegate/
│   │   │   │   ├── MainActivity.kt           # Point d'entrée de l'app
│   │   │   │   ├── domain/                   # Logique métier
│   │   │   │   ├── data/                     # Données et repositories
│   │   │   │   ├── ui/                       # Interface utilisateur
│   │   │   │   ├── service/                  # Services (WorkManager)
│   │   │   │   ├── util/                     # Utilitaires
│   │   │   │   └── di/                       # Injection de dépendances
│   │   │   ├── res/                          # Ressources (layouts, strings, etc.)
│   │   │   └── AndroidManifest.xml           # Manifeste de l'application
│   ├── build.gradle.kts                      # Configuration Gradle du module
│   └── proguard-rules.pro                    # Règles de ProGuard
├── build.gradle.kts                          # Configuration Gradle du projet
├── settings.gradle.kts                       # Paramètres Gradle
└── gradle/                                   # Gradle Wrapper
```

---

## ⏰ Gestion des shifts

### Logique de base

L'application utilise un système de **2 shifts par jour** :

| Shift | Horaire | Description |
|-------|---------|-------------|
| **Shift 1** | 07h00 - 19h00 | Shift de jour |
| **Shift 2** | 19h00 - 07h00 | Shift de nuit |

### Date du rapport

La date du rapport est calculée selon cette logique :

- **Shift 1** (7h-19h) : Date du jour
- **Shift 2** (19h-7h) :
  - Si on est entre 19h et minuit → Date du jour
  - Si on est entre minuit et 7h → Date de la veille

**Exemple :**
- Shift 2 du 20/01/2026 à 22h00 → Rapport daté du 20/01/2026
- Shift 2 du 21/01/2026 à 03h00 → Rapport daté du 20/01/2026 (shift commencé la veille)

### Changement automatique de shift

L'application utilise **WorkManager** pour gérer les changements de shift automatiques :

1. **À 07h00** : Génération du rapport du Shift 2
2. **À 19h00** : Génération du rapport du Shift 1
3. Le WorkManager planifie toujours le prochain changement après chaque exécution
4. Même si l'application est fermée, le changement de shift se déclenchera

### Modal de shift intelligente

La modal de changement de shift :

- ✅ S'affiche automatiquement à 7h et 19h
- ✅ Est obligatoire (non-dismissible)
- ✅ **Ne s'affiche PAS** pendant l'ajout ou l'édition d'un véhicule
- ✅ S'affiche **juste après** que l'utilisateur ait terminé son action
- ✅ Vérifie toutes les 30 secondes si un changement de shift est nécessaire
- ✅ Vérifie aussi à chaque changement de page

---

## 📄 Génération de PDF

### Contenu du PDF

Chaque rapport PDF contient :

#### 1. En-tête
- Date du rapport (ex: 20/01/2026)
- Nom du site : "JARDIN D'ACCLIMATATION"
- Nom de l'agent
- Numéro et horaires du shift (ex: "Shift n°1 (07h-19h)")

#### 2. Liste des véhicules

Pour chaque véhicule, affichage sur 2 colonnes :

**Colonne 1 :**
- Plaque d'immatriculation
- Société/Transporteur
- Nom du chauffeur
- Téléphone (si renseigné)
- Destination

**Colonne 2 :**
- Contact site
- Porte d'entrée
- Porte de sortie
- Heure d'arrivée
- Heure de départ (si sorti)

**Pleine largeur :**
- Notes (si présentes)
- Temps sur site (si sorti)

#### 3. Mise en forme spéciale

- **Véhicules sur site** : Texte en ROUGE avec "(ENCORE SUR SITE)"
- **Véhicules sortis** : Texte en noir
- Tri par ordre d'arrivée (le plus ancien en haut)

#### 4. Section Photos

Page séparée avec :
- Photos des bons de livraison
- Numéro de véhicule et plaque sous chaque photo
- Redimensionnement automatique pour tenir sur la page

### Optimisations PDF

- **Wrapping intelligent** : Le texte trop long est automatiquement coupé et réparti sur plusieurs lignes
- **Coupure de mots** : Les mots trop longs sont coupés caractère par caractère pour éviter les débordements
- **Gestion des pages** : Nouvelle page automatique quand il n'y a plus de place
- **Marges sécurisées** : 35px de marge de chaque côté pour éviter la perte d'information

### Stockage des PDFs

- **Dossier** : `Android/data/com.ges.vehiclegate/files/`
- **Format du nom** : `Rapport du [date] - JARDIN D'ACCLIMATATION - [agent] - [Shift].pdf`
- **Exemple** : `Rapport du 20-01-2026 - JARDIN D'ACCLIMATATION - Jean Dupont - Shift 1.pdf`
- **Nettoyage** : Suppression automatique des PDFs de plus de 7 jours

---

## ❓ FAQ

### L'application ne se lance pas

**Solutions :**
1. Vérifiez que vous avez accepté toutes les permissions (caméra, stockage)
2. Assurez-vous que votre appareil a Android 8.0 ou supérieur
3. Essayez de redémarrer Android Studio et de nettoyer le projet : Build → Clean Project → Rebuild Project

### La modal de shift n'apparaît pas

**Vérifications :**
1. Vérifiez l'heure système de votre appareil (doit être 7h ou 19h)
2. Si vous êtes sur l'écran d'ajout ou d'édition, terminez votre action d'abord
3. La modal devrait apparaître automatiquement dans les 30 secondes suivant le changement d'heure

### Les véhicules disparaissent

**C'est normal dans ces cas :**
- Après un changement de shift, tous les véhicules **sortis** sont supprimés
- Les véhicules **sur site** restent toujours visibles
- Si un véhicule a disparu par erreur, utilisez "Rétablir sur site" dans "Véhicules du shift" (tant que le shift n'a pas changé)

### Le PDF ne contient pas tous les véhicules

**Vérifications :**
1. Les véhicules archivés (fin de journée) ne sont pas inclus
2. Tous les véhicules non archivés (sur site + sortis) doivent être dans le PDF
3. Vérifiez que le véhicule n'a pas été supprimé avant la génération du PDF

### L'email ne s'envoie pas

**Solutions :**
1. Vérifiez votre configuration email dans les paramètres
2. Vérifiez votre connexion Internet
3. Si l'envoi automatique échoue, l'application ouvrira votre client email par défaut

### Comment récupérer un véhicule marqué comme sorti par erreur ?

1. Allez dans **"Véhicules du shift"**
2. Trouvez le véhicule dans la liste (en noir)
3. Cliquez sur **"Rétablir sur site"**
4. Le véhicule réapparaît sur l'écran d'accueil

### L'application crash ou affiche un écran blanc

**Solutions :**
1. Assurez-vous d'avoir la dernière version du code (git pull)
2. Nettoyez et recompilez : Build → Clean Project → Rebuild Project
3. Supprimez les données de l'application et relancez
4. Si le problème persiste, consultez les logs dans Android Studio (Logcat)

---

## 👨‍💻 Développement

### Branches

- `main` : Branche principale (production)
- `claude/fix-vehicle-synchronization-8hOnk` : Branche de développement actuelle avec les dernières corrections

### Commandes utiles

```bash
# Récupérer les dernières modifications
git pull origin claude/fix-vehicle-synchronization-8hOnk

# Nettoyer le projet (si problèmes de compilation)
./gradlew clean

# Compiler l'APK debug
./gradlew assembleDebug

# Compiler l'APK release
./gradlew assembleRelease

# Lancer les tests
./gradlew test
```

### Logs importants

Les logs de l'application sont taggés par composant :
- `ShiftScheduler` : Logs des changements de shift
- `ShiftChangeWorker` : Logs de la génération de PDF et envoi d'emails
- `PdfReportGenerator` : Logs de la génération de PDF
- `GmailSender` : Logs de l'envoi d'emails

Pour voir les logs dans Android Studio, allez dans **View → Tool Windows → Logcat**.

---

## 📄 Licence

Ce projet est la propriété de GES (Gestion d'Entrée/Sortie) et du Jardin d'Acclimatation.

---

## 📞 Support

Pour toute question ou problème :
1. Consultez d'abord cette documentation
2. Vérifiez les logs dans Logcat
3. Contactez l'équipe de développement

---

**Version** : 1.0.0
**Dernière mise à jour** : 20 Janvier 2026
**Développé avec** : ❤️ et Jetpack Compose
