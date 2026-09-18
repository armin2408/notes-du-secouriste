# Fiche Play Store (brouillon) — Notes du Secouriste

Textes à copier dans [Google Play Console](https://play.google.com/console). Adapter le ton si besoin ; respecter les limites de caractères Google.

| Champ | Limite | Statut |
|-------|--------|--------|
| Titre de l’app | 30 car. | OK ci-dessous |
| Description courte | 80 car. | OK |
| Description complète | 4000 car. | OK |
| Notes de version | 500 car. | modèle bêta |

## Identité

| Élément | Valeur proposée |
|---------|-----------------|
| **Nom** | Notes du Secouriste |
| **Package** | `com.notesdusecouriste.app` |
| **Catégorie** | Santé et bien-être *(ou Outils si revue trop stricte)* |
| **Tags** | secourisme, PSE, intervention, notes, hors ligne |
| **Prix** | Gratuit |
| **Pays** | France (puis extension si souhaité) |

## Titre (30 caractères max)

```
Notes du Secouriste
```

## Brève description (80 caractères max)

**Recommandée (73 car.)** — à coller dans « Brève description » :

```
Notes de terrain hors ligne pour secouristes PSE — victime, mesures, récap.
```

**Alternative (79 car.)** :

```
Notes PSE hors ligne : victime, mesures, Glasgow, SAMPLE, récap. Version bêta.
```

## Description complète (4000 caractères max)

**~1 950 car.** — marge large sous la limite :

```
Notes du Secouriste aide le secouriste à structurer ses observations sur le terrain, comme un bloc-notes numérique pensé pour l’intervention : saisie rapide, sauvegarde automatique, aucune connexion requise.

━━━ POUR QUI ? ━━━
Secouristes PSE1 et PSE2 (événementiel, association, binôme, garde) qui souhaitent noter pendant l’action et préparer une transmission orale claire vers le chef d’équipe ou la régulation.

━━━ FONCTIONNALITÉS (version bêta) ━━━
• Interventions multiples (brouillons)
• Victime — identité, date de naissance, âge, coordonnées
• Mesures structurées — respiration, circulation, conscience (score de Glasgow), suspicion d’AVC, température, glycémie
• Relevés horodatés — plusieurs onglets par intervention
• Questionnaires SAMPLE et OPQRST
• Récap de transmission — lecture seule, synthèse type tableau
• Aide-mémoire intégrée — éditeur enrichi, onglets personnalisables, recherche, images
• Thème clair ou sombre, interface edge-to-edge

━━━ 100 % HORS LIGNE ━━━
Pas de compte, pas de cloud éditeur : les données saisies restent sur votre téléphone. Fonctionne en zone blanche ou sans réseau.

━━━ AVERTISSEMENT IMPORTANT ━━━
Notes du Secouriste est un outil d’aide à la prise de notes de terrain. Elle ne remplace en aucun cas les documents officiels de bilan ni les applications ou outils numériques utilisés ou imposés par les institutions, services de secours et associations de secourisme (protection civile, Croix-Rouge, structures événementielles, etc.).

L’usage de cette application ne dispense pas du respect des protocoles, chartes et obligations de votre organisation. La transmission, les gestes et les décisions relèvent de votre formation et du cadre légal de votre mission.

━━━ VERSION BÊTA ━━━
Application en test : l’ergonomie et les fonctionnalités évoluent. Vos retours sont les bienvenus pour améliorer l’outil sur le terrain.

Politique de confidentialité : https://armin2408.github.io/notes-du-secouriste/
```

## Notes de version (0.2.1 bêta)

```
Version bêta 0.2.1 — test fermé.

• Aide-mémoire : éditeur enrichi (gras, italique, listes, liens), sauvegarde automatique, recherche corrigée
• Interface : barre de navigation transparente avec flou (navigation 3 boutons)
• Notes d'intervention : bouton Récap en fin de formulaire (plus de barre fixe en bas)
• Corrections et améliorations diverses

Merci de signaler les bugs via [email ou formulaire à compléter].
```

## Notes de version (exemple 0.1.0 bêta)

```
Première version bêta publique.
• Saisie interventions, mesures, récap
• Aide-mémoire
• Fonctionnement 100 % hors ligne
Merci de signaler les bugs via [email ou formulaire à compléter].
```

## Avertissement / clause (à afficher aussi dans la fiche si possible)

```
Outil d’aide à la prise de notes — ne remplace pas un document médical réglementé ni l’avis d’un professionnel de santé.
```

## Assets graphiques à préparer

Voir `docs/play-store-assets.md`.

## URL politique de confidentialité

Héberger `docs/privacy-policy-fr.md` (GitHub Pages, site association, etc.) et coller l’URL HTTPS dans la console.

Exemple : `https://votre-domaine.fr/notes-du-secouriste/confidentialite`
