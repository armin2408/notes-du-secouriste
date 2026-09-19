# Notes du Secouriste

Application Android **100 % hors ligne** pour structurer les notes de terrain en secourisme (PSE) : comme un bloc-notes numérique pensé pour l’intervention, pas un dossier médical.

> **Version bêta** — `0.3.0-beta` (test fermé Google Play)  
> Outil d’**aide à la prise de notes**. Elle ne remplace ni un bilan officiel, ni les outils imposés par une association ou un service de secours, ni l’avis d’un professionnel de santé.

**Dépôt :** [github.com/armin2408/notes-du-secouriste](https://github.com/armin2408/notes-du-secouriste)  
**Confidentialité :** [armin2408.github.io/notes-du-secouriste](https://armin2408.github.io/notes-du-secouriste/)

---

## À quoi ça sert ?

Sur le terrain, le secouriste doit souvent mémoriser rapidement :

- qui est la victime ;
- ce qu’il observe (respiration, pouls, conscience, etc.) ;
- l’évolution dans le temps ;
- les éléments à transmettre à l’oral (chef d’équipe, régulation, relève).

**Notes du Secouriste** aide à **saisir vite**, **retrouver clairement**, puis **préparer une synthèse** (PDF) — sans compte, sans cloud éditeur, sans réseau obligatoire.

---

## Pour qui ?

- Secouristes **PSE1 / PSE2** (événementiel, association, binôme, garde)
- Situations où un carnet papier est trop lent ou illisible
- Zones blanches / mode avion : l’app continue de fonctionner

---

## Parcours typique d’une intervention

1. **Créer une intervention** depuis l’accueil  
2. Renseigner la **victime** (identité, âge, coordonnées)  
3. Ajouter des **relevés horodatés** au fil de l’action (plusieurs onglets / heures)  
4. Compléter **SAMPLE** / **OPQRST** si utile  
5. Joindre des **photos** locales si besoin  
6. Ouvrir la **synthèse PDF** pour relire, zoomer, partager (ex. impression / envoi hors app)  
7. S’appuyer sur l’**aide-mémoire** personnel entre deux interventions  

La saisie est **autosauvegardée** : on peut quitter l’écran sans perdre le travail.

---

## Fonctionnalités (bêta 0.3.0)

### Accueil & interventions
- Liste des interventions (brouillons)
- Création rapide, sélection multiple, suppression
- Accès direct à la synthèse depuis une ligne
- Badge **Bêta**, thème clair / sombre

### Victime
- Nom, prénom, date de naissance, âge calculé
- Coordonnées utiles à la transmission

### Mesures (relevés horodatés)
Blocs structurés, avec couleurs métier (normal / à surveiller / alerte / critique) :

| Domaine | Exemples de saisie |
|---------|--------------------|
| **Respiration** | Fréquence, amplitude, régularité… |
| **Circulation** | Pouls, TA (SYS/DIA), remplissage… |
| **Conscience** | Glasgow (Y/V/M + total) |
| **Suspicion AVC** | Signes orientés |
| **Température / glycémie** | Valeurs numériques avec repères visuels |

Plusieurs relevés par intervention : chaque onglet correspond à un **horaire**.

### Questionnaires
- **SAMPLE** — antécédents / contexte
- **OPQRST** — douleur / symptômes

### Photos
- Pièces jointes stockées **uniquement sur l’appareil**
- Associées à l’intervention pour la synthèse

### Profil secouriste
- Identité / contact renseignés dans les réglages
- Réutilisés sur les exports PDF

### Synthèse PDF
- Aperçu intégré (zoom, défilement)
- Orientation portrait / paysage
- Partage via les apps du téléphone
- Contenu : victime, mesures, questionnaires, profil, photos

### Aide-mémoire
- Onglets personnalisables (créer, masquer, réordonner, dupliquer)
- Éditeur enrichi : gras, italique, listes, liens, images
- Recherche et sauvegarde automatique
- Contenu local, indépendant des interventions

### Accueil guidé & journal
- Carrousel de bienvenue au premier lancement
- **Journal des mises à jour** depuis les réglages

### Interface
- Material 3 Expressive
- Palette **terrain** (bleu `#1565C0`, sans violet)
- Clair / sombre, edge-to-edge, barre de navigation floutée (3 boutons)

---

## Ce qui n’est pas encore dans la bêta

- Script de transmission guidé
- Clôture / verrouillage d’intervention
- Alertes vitales automatiques

---

## Confidentialité (en bref)

| Point | Comportement |
|-------|----------------|
| Compte éditeur | **Non** |
| Serveur / cloud éditeur | **Non** |
| Stockage | **Local** (téléphone) |
| Permission Internet | **Absente** du manifeste |

Détail : [`docs/privacy-policy-fr.md`](docs/privacy-policy-fr.md) · [page web](https://armin2408.github.io/notes-du-secouriste/)

---

## Prérequis

- Téléphone / tablette **Android 8.0+** (API 26)
- Pour développer : [Android Studio](https://developer.android.com/studio) + JDK 17

---

## Démarrage rapide (développeurs)

Le code est dans [`android/`](android/).

```powershell
cd android
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
.\gradlew installDebug
```

Guide détaillé (USB, modules, release) : [`android/README.md`](android/README.md).

### Structure du dépôt

```
├── android/     # Application Gradle (app + core + features)
├── docs/        # Play Store, confidentialité, checklists
└── README.md
```

| Module | Rôle |
|--------|------|
| `:app` | Navigation, accueil, réglages, onboarding |
| `:core:core-data` | Room, repositories, préférences |
| `:core:core-ui` | Thème Material 3, couleurs sémantiques |
| `:feature:feature-intervention-notes` | Notes, mesures, PDF |
| `:feature:feature-aide-memoire` | Aide-mémoire |
| `:feature:feature-onboarding` | Bienvenue + journal |

**Stack :** Kotlin · Jetpack Compose · Material 3 Expressive · Room · Hilt · DataStore  
**SDK :** min 26 · target **36** · compile 37

### Bundle Play Store

```powershell
cd android
.\gradlew bundleRelease
```

Sortie : `android/app/build/outputs/bundle/release/app-release.aab`  
Guide : [`docs/play-store-beta.md`](docs/play-store-beta.md) · textes fiche : [`docs/store-listing-fr.md`](docs/store-listing-fr.md)

---

## Contribution & retours

Bêta **fermée** : les retours testeurs (bugs, ergonomie terrain, formulations) sont les bienvenus via Issues GitHub ou le canal indiqué aux bêta-testeurs.

---

_Développé pour les secouristes sur le terrain._
