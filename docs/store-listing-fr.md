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

## Description courte (80 caractères max)

```
Notes de terrain hors ligne pour secouristes PSE — victime, mesures, récap.
```

## Description complète

```
Notes du Secouriste remplace le bloc-notes papier du secouriste sur le terrain : saisie structurée, sauvegarde automatique, tout fonctionne sans réseau.

POUR QUI ?
Secouristes PSE1/PSE2 en intervention (événementiel, association, binôme) qui veulent noter vite et préparer une transmission orale.

FONCTIONNALITÉS (bêta)
• Interventions multiples — brouillon ou clôturée
• Victime — identité, âge, coordonnées
• Mesures — respiration, circulation, conscience (Glasgow), suspicion d’AVC, relevés horodatés
• Questionnaires SAMPLE et OPQRST
• Récap de transmission en lecture seule
• Aide-mémoire intégrée (Markdown, onglets personnalisables)
• Thème clair / sombre

HORS LIGNE
Aucun compte, aucun cloud : vos données restent sur votre téléphone.

IMPORTANT
Cette application ne produit pas de bilan médical officiel. Elle aide à structurer des notes secouriste ; la transmission et les gestes relèvent de votre formation et des protocoles en vigueur.

Version bêta : retours bienvenus pour améliorer l’ergonomie terrain.
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
