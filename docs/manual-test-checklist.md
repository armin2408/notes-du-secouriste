# Checklist test manuel — NOTES DU SECOURISTE

Référence : [`_bmad-output/implementation-artifacts/implementation-status.md`](../_bmad-output/implementation-artifacts/implementation-status.md)

---

## Epic 1 — Fondation & accueil

- [ ] App s’installe sur téléphone USB (`installDebug`)
- [ ] Accueil affiche **NOTES DU SECOURISTE**
- [ ] Aucun mot « bilan » visible dans l’UI
- [ ] **Nouvelle intervention** ouvre l’écran notes en &lt; 2 s
- [ ] Mode avion : création intervention OK
- [ ] **Aide mémoire** ouvre l’écran placeholder
- [ ] Liste des interventions sur l’accueil (ligne identité + date + statut brouillon)
- [ ] Tap sur une ligne → rouvre l’écran notes avec données
- [ ] Toggle thème clair/sombre depuis l’accueil
- [ ] **Réglages** : politique thème système / dernier choisi
- [ ] Suppression : appui long → sélection → supprimer une intervention
- [ ] Suppression multiple en mode sélection
- [ ] Retour arrière fonctionne

---

## Epic 2 — Saisie notes (blocs)

- [ ] Modification victime (nom, prénom, date naissance, âge, coordonnées) → indicateur sauvegarde puis « Enregistré »
- [ ] Kill app + relance → données victime intactes
- [ ] Bloc **COMMENTAIRE** persisté après kill app
- [ ] Date naissance invalide (ex. 99/99/9999) → bordure/icône erreur, saisie toujours possible
- [ ] En-têtes sticky : bloc MESURES + onglets restent visibles au scroll
- [ ] Sous-sections (Respiration, etc.) se collent sous Mesures + onglets
- [ ] **Récap** ouvre un écran lecture seule (Victime, relevés, commentaire)
- [ ] Récap : texte lisible (grande typo), retour aux notes OK
- [ ] Script / Clôturer affichent « Bientôt disponible »

---

## Epic 3 — Relevés vitaux (partiel)

- [ ] **Noter des mesures** crée le premier relevé à l’heure actuelle
- [ ] **Ajouter un relevé** → nouvel onglet horaire
- [ ] Saisie fréquence respiratoire / cardiaque (max 3 chiffres + suffixe)
- [ ] TA SYS et TA DIA côte à côte (max 3 chiffres + mmHg)
- [ ] Listes de choix : pills connectées, vert = 1ère option, jaune = autres
- [ ] Appui long onglet → modifier heure / date / supprimer relevé
- [ ] Heure : `:` inséré automatiquement, heures invalides refusées à la saisie
- [ ] Onglet affiche `HH:mm` si jour courant, sinon `dd/MM HH:mm`
- [ ] Données mesures intactes après kill app
- [ ] **Non testé / absent :** alertes couleur hors plage, ⓘ plages de référence

---

## Epic 4 — Transmission (partiel)

- [ ] Récap avec plusieurs relevés horodatés et TA 120/80 affichés correctement
- [ ] Récap vide si note sans donnée

## Epics 4–6 — À venir

- [ ] Script de transmission offline (Epic 4)
- [ ] Clôture → lecture seule (Epic 5)
- [ ] Aide mémoire fiches Markdown (Epic 6)
