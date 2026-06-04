# Politique de confidentialité via GitHub Pages

Dépôt GitHub : **`notes-du-secouriste`** (privé OK).

## Avant de pousser

1. Éditer `docs/index.html` :
   - remplacer `[À compléter : nom ou association]` ;
   - remplacer `contact@example.com` par votre e-mail réel.
2. Vérifier que `android/keystore.properties` et `android/release-keystore.jks` ne seront **pas** commités (voir `.gitignore` à la racine).

## Activer GitHub Pages

1. Sur GitHub : repo **notes-du-secouriste** → **Settings** → **Pages**.
2. **Build and deployment** → Source : **Deploy from a branch**.
3. Branch : **`main`** (ou `master`), dossier **`/docs`**.
4. **Save**. Attendre 1 à 5 minutes.

## URL pour la Play Console

Remplacez `VOTRE_COMPTE` par votre identifiant GitHub :

```
https://VOTRE_COMPTE.github.io/notes-du-secouriste/
```

Exemple : `https://martin.github.io/notes-du-secouriste/`

Coller cette URL dans **Fiche Play Store** → **Politique de confidentialité**.

> **Note :** le dépôt peut rester **privé** ; la page Pages publiée est en revanche **publique** (normal pour Google Play).

## Pousser le projet (première fois)

Depuis la racine du projet (`NOTES SECOURISTE`) :

```powershell
git init
git add .
git commit -m "Initial commit — Notes du Secouriste"
git branch -M main
git remote add origin https://github.com/VOTRE_COMPTE/notes-du-secouriste.git
git push -u origin main
```

## Vérification

- Ouvrir l’URL Pages en navigation privée (sans être connecté à GitHub).
- La politique doit s’afficher en français.

## Mise à jour

Modifier `docs/index.html`, commit, push — Pages se met à jour en quelques minutes.
