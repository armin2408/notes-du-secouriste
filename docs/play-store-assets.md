# Assets Play Store — Notes du Secouriste

## Icône

- **Play Console :** icône haute résolution **512 × 512** PNG (32 bits, sans transparence pour l’icône store si exigé).
- **Source projet :** `android/app/branding/logo-nds.png`, adaptive icon `ic_launcher` / `ic_launcher_round`.

## Captures d’écran (téléphone)

Minimum **2**, recommandé **4–8**. Format **PNG** ou **JPEG**, côté le plus long entre **320 px** et **3840 px**.

Scènes suggérées :

1. Accueil (liste + badge Bêta)
2. Saisie — bloc Victime
3. Saisie — Mesures (onglets horaires)
4. Récap transmission
5. Aide-mémoire

**Astuce :** captures depuis le Pixel en `adb exec-out screencap -p > capture.png`, ou outil Android Studio *Device Manager → Screenshot*.

## Bannière fonctionnalité (optionnel)

1024 × 500 PNG pour la mise en avant sur certains appareils.

## Vidéo (optionnel)

YouTube ou Google Ads — non requis pour la bêta.

## Dossier local

Déposer les exports finaux dans :

```
docs/store-assets/
  icon-512.png
  phone-01-accueil.png
  phone-02-saisie.png
  ...
```

*(Ce dossier peut rester vide dans le dépôt ; ajouter les PNG quand prêts.)*
