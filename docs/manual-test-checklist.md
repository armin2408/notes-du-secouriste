# Checklist test manuel — NOTES DU SECOURISTE

**Version cible :** `0.2.1-beta`  
Référence : [`_bmad-output/implementation-artifacts/implementation-status.md`](../_bmad-output/implementation-artifacts/implementation-status.md)

---

## Epic 1 — Fondation & accueil

- [ ] App s’installe sur téléphone USB (`installDebug`)
- [ ] Accueil affiche **Notes du Secouriste** + badge Bêta
- [ ] Aucun mot « bilan » visible dans l’UI
- [ ] **Nouvelle intervention** ouvre l’écran notes en &lt; 2 s
- [ ] Mode avion : création intervention OK
- [ ] **Aide-mémoire** ouvre l’éditeur / onglets (pas un placeholder)
- [ ] Liste des interventions sur l’accueil (identité + date)
- [ ] Icône **Récap** sur une ligne → ouvre le récap
- [ ] Tap sur une ligne → rouvre l’écran notes avec données
- [ ] Toggle thème clair/sombre depuis l’accueil
- [ ] **Réglages** : politique thème système / dernier choisi
- [ ] Suppression : appui long → sélection → supprimer une intervention
- [ ] Suppression multiple en mode sélection
- [ ] Navigation 3 boutons : barre transparente avec flou visible au scroll
- [ ] Retour arrière fonctionne

---

## Epic 2 — Saisie notes (blocs)

- [ ] Modification victime (nom, prénom, date naissance, âge, coordonnées) → indicateur sauvegarde puis « Enregistré »
- [ ] Kill app + relance → données victime intactes
- [ ] Blocs **SAMPLE** / **OPQRST** saisissables et persistés
- [ ] Bloc **COMMENTAIRE** persisté après kill app
- [ ] Date naissance invalide (ex. 99/99/9999) → bordure/icône erreur, saisie toujours possible
- [ ] En-têtes sticky : bloc MESURES + onglets restent visibles au scroll
- [ ] Sous-sections (Respiration, etc.) se collent sous Mesures + onglets
- [ ] Bouton **Récap** visible **en fin de formulaire** (après scroll)
- [ ] **Récap** ouvre un écran lecture seule (Victime, relevés, questionnaires, commentaire)
- [ ] Récap : tableau / typo lisible, retour aux notes OK

---

## Epic 3 — Relevés vitaux (partiel)

- [ ] **Noter des mesures** crée le premier relevé à l’heure actuelle
- [ ] **Ajouter un relevé** → nouvel onglet horaire
- [ ] Saisie fréquence respiratoire / cardiaque (max 3 chiffres + suffixe)
- [ ] TA SYS et TA DIA côte à côte (max 3 chiffres + mmHg)
- [ ] Glasgow : score et interprétation visibles
- [ ] Température et glycémie saisissables
- [ ] Listes de choix : pills connectées, vert = 1ère option, jaune = autres
- [ ] Appui long onglet → modifier heure / date / supprimer relevé
- [ ] Heure : `:` inséré automatiquement, heures invalides refusées à la saisie
- [ ] Onglet affiche `HH:mm` si jour courant, sinon `dd/MM HH:mm`
- [ ] Données mesures intactes après kill app
- [ ] **Absent (attendu) :** alertes couleur hors plage, ⓘ plages de référence

---

## Epic 4 — Transmission (partiel)

- [ ] Récap avec plusieurs relevés horodatés et TA 120/80 affichés correctement
- [ ] Récap inclut SAMPLE / OPQRST si renseignés
- [ ] Récap vide / minimal si note sans donnée

---

## Epic 6 — Aide-mémoire

- [ ] Créer / renommer / masquer / réordonner un onglet
- [ ] Éditeur : gras, italique, souligné, titre, liste, lien
- [ ] Autosave (indicateur Enregistrement… / Enregistré)
- [ ] Recherche : plusieurs occurrences, navigation précédent / suivant
- [ ] Insertion d’image depuis la galerie
- [ ] Lien PDF s’ouvre dans une app externe
- [ ] Kill app + relance → contenu onglet intact

---

## À venir (ne pas attendre en 0.2.1)

- [ ] Script de transmission offline (Epic 4)
- [ ] Clôture → lecture seule (Epic 5)
- [ ] Profil secouriste (réglages)
- [ ] Export PDF synthèse d’intervention
- [ ] Photos dans une note d’intervention
- [ ] Alertes / plages vitales (Epic 3.4–3.6)
