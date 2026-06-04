---
title: NOTES DU SECOURISTE
created: 2026-05-20
updated: 2026-05-21
status: final
stakes: launch
technical_id: intervention_notes
---

# PRD: NOTES DU SECOURISTE

## 0. Document Purpose

Ce PRD décrit la version **MVP (v1.0)** d'une application **Android** pour dématérialiser le bloc-notes du secouriste en France (PSE1 et PSE2). Il s'adresse au PM, aux workflows BMad en aval (UX, architecture, epics/stories) et au développeur.

**Entrées intégrées :** sessions Party Mode et agents John (PM) / Sally (UX), décisions validées par Marti le 2026-05-20. Détails techniques et UX étendus : `addendum.md`. Journal des décisions : `.decision-log.md`.

**Structure :** glossaire ancré, fonctionnalités groupées avec exigences fonctionnelles (FR) numérotées globalement, NFR transverses, hypothèses taguées `[ASSUMPTION]`.

---

## 1. Vision

Les secouristes en intervention s'appuient encore sur un **bloc-notes papier** pour structurer leurs observations et préparer la transmission — oralement, sur une autre feuille, ou dans un autre système. Ce rituel fonctionne sous stress, avec des gants, souvent au plus près de la victime ; le numérique n'a de valeur que s'il **gagne sur le papier en rapidité et fiabilité**, sans prétendre remplacer un document médical officiel.

**NOTES DU SECOURISTE** est une application **100 % hors ligne** qui permet de saisir des **notes secouriste** par **intervention**, de les consulter dans un **historique**, de s'appuyer sur une **aide mémoire** compacte (repères visuels en Markdown, personnalisable), et de générer un **script de transmission** local pour faciliter la dictée vers un chef d'équipe ou la régulation.

Le produit refuse le vocabulaire **« bilan »** (réservé aux documents officiels) et se concentre sur la **mémoire structurée de terrain** — une seule fiche unifiée PSE1/PSE2, des alertes sur constantes anormales, et une expérience pensée pour le **vif** : gros cibles, sauvegarde automatique, reprise instantanée.

---

## 2. Target User

### 2.1 Primary Persona

**Léa, 26 ans — secouriste bénévole PSE2**, interventions événementielles (sport, foule). Smartphone Android personnel. Formée aux gestes et au bloc-notes papier de son association. Saisit souvent **pendant** l'intervention, une main occupée, parfois avec des gants. Transmet **oralement** au chef d'équipe ou prépare l'appel 15 ; ne dépend pas d'un export PDF.

### 2.2 Jobs To Be Done

- **Sur intervention**, je veux **noter rapidement** ce qui compte (victime, constantes, gestes, évolution) pour ne rien oublier à la transmission.
- **En cas de doute**, je veux **un repère visuel immédiat** (protocole, checklist) sans feuilleter un manuel.
- **À la transmission**, je veux **un fil oral court et ordonné** basé sur ce que j'ai saisi — sans réseau.
- **Entre interventions**, je veux **retrouver** ce que j'ai noté (historique, relecture).
- **Dans la durée**, je veux **adapter** mes plages de constantes « normales » à ma pratique ou ma formation.

### 2.3 Non-Users (v1)

- Médecins régulateurs / SMUR utilisant un **bilan médical officiel** numérique.
- Structures exigeant **sync cloud** ou intégration SI SAMU en temps réel.
- Utilisateurs iOS (hors périmètre v1).
- PSC1 seul sans montée PSE — `[ASSUMPTION:]` formulaire identique mais contenu aide mémoire peut être trop riche ; pas de variante formulaire v1.

### 2.4 Key User Journeys

- **UJ-1. Léa saisit des notes secouriste sur le vif**
  - **Persona + contexte :** PSE2, victime au sol, binôme au téléphone.
  - **Entry state :** App ouverte, accueil, hors ligne.
  - **Path :** Tap **Nouvelle intervention** → saisie par blocs (victime, constantes, gestes…) → alerte couleur si TA hors plage → tap ⓘ pour repères âge → sauvegarde auto à chaque champ.
  - **Climax :** Tout est horodaté localement ; elle peut quitter l'écran et revenir sans perte.
  - **Resolution :** Intervention en statut **brouillon** ou **clôturée**.
  - **Edge case :** Gant mouillé — cibles larges ; pas de popup bloquant sur alerte rouge.

- **UJ-2. Léa consulte un repère aide mémoire**
  - **Entry state :** Doute sur PLS, intervention en cours ou pause.
  - **Path :** Accueil → **icône Aide mémoire** (header) → carte situation → repères Markdown + schéma.
  - **Climax :** Information en &lt; 10 s, une main.
  - **Resolution :** Retour aux notes secouriste sans perdre le contexte.

- **UJ-3. Léa prépare la transmission orale**
  - **Entry state :** Notes suffisantes, chef d'équipe ou appel imminent.
  - **Path :** Ouvrir intervention → **Script de transmission** → lecture écran structurée.
  - **Climax :** Phrases courtes, ordre de dictée naturel, constantes alertées reprises.
  - **Resolution :** Transmission orale ; pas d'export fichier en v1.

- **UJ-4. Léa clôture et retrouve une intervention passée**
  - **Path :** Clôturer → statut **clôturée** (lecture seule) → **liste accueil** → ouvrir en lecture.
  - **Edge case :** Erreur après clôture → créer une **nouvelle intervention** (pas de copie automatique — D-033).

---

## 3. Glossary

- **Intervention** — Événement terrain unique (horodaté) portant zéro ou une saisie active de notes secouriste. Statuts : **brouillon** ou **clôturée**.
- **Notes secouriste** — Contenu structuré saisi pour une intervention ; remplace le bloc-notes papier. **Ne pas** appeler « bilan », « fiche bilan » ou « bilan médical » dans le produit.
- **intervention_notes** — Identifiant technique du module de saisie (code, persistance, packages).
- **Bloc** — Section de saisie dans les notes secouriste (ex. victime, constantes, gestes).
- **Repère** — Unité visuelle scannable dans l'aide mémoire (carte, puces courtes, schéma).
- **Aide mémoire** — Bibliothèque locale de fiches Markdown compactes (pack par défaut + édition utilisateur).
- **Constante** — Type de mesure (TA, FC, FR, SpO2, etc.) pouvant avoir **plusieurs relevés** au cours d'une intervention.
- **Relevé** — Une mesure **horodatée** d'une constante donnée ; plusieurs relevés possibles par constante et par intervention. **Stockage** : date + heure complètes (`recordedAt`). **Affichage** par défaut : **HH:mm** uniquement. **Antidatage** : l'utilisateur peut modifier manuellement la date et l'heure d'un relevé.
- **Relevé TA** — Un relevé de tension avec **deux saisies consécutives** : systolique puis diastolique (même horodatage).
- **Valeurs de référence** — Plages min/max par constante et **tranche d'âge**, personnalisables.
- **Alerte** — Indication visuelle (couleur) qu'une constante est hors plage ; **non bloquante**.
- **Script de transmission** — Texte généré **localement** (règles, sans cloud) pour faciliter la dictée orale.
- **Clôturée** — Intervention figée en **lecture seule** ; modification interdite (duplication seulement).

---

## 4. Features

### 4.1 Accueil & navigation

**Description :** Écran d'accueil **hub unique** : barre supérieure (titre, **icône seule** Aide mémoire, **toggle thème**), CTA **Nouvelle intervention** (sous-titre « Notes secouriste ») **au-dessus** d'une **liste** des interventions passées (reprise en un tap). Pas d'écran Historique séparé ; pas de libellé « Hors ligne ». Realise UJ-1, UJ-2, UJ-4.

**Functional Requirements:**

#### FR-1: Créer une intervention

L'utilisateur peut démarrer une **nouvelle intervention** depuis l'accueil en un geste, avec horodatage immédiat et statut **brouillon**, sans réseau. Realise UJ-1.

**Consequences (testable):**
- Une nouvelle ligne `Intervention` existe en base locale &lt; 2 s après le tap.
- L'écran de saisie des notes secouriste s'affiche sans étape intermédiaire obligatoire.

#### FR-2: Libellés produit sans « bilan »

L'interface MVP n'affiche jamais le mot « bilan » (boutons, titres, messages, script de transmission).

**Consequences (testable):**
- Revue i18n / copy : zéro occurrence de « bilan » / « fiche bilan » dans les strings v1.
- Aucun libellé, badge ou bannière « Hors ligne » (ou équivalent) dans l'UI MVP.

#### FR-18: Thème clair/sombre et politique d'ouverture

L'utilisateur bascule entre thème **clair** et **sombre** (toggle en haut à droite sur l'accueil) et configure dans **Réglages → Apparence** la politique à l'ouverture de l'app : **Thème système** (défaut) ou **Dernier thème choisi**.

**Consequences (testable):**
- Toggle : changement visuel immédiat ; persistance du dernier choix explicite `LIGHT` ou `DARK`.
- Politique **Thème système** (défaut au premier lancement) : au cold start, thème = réglage système Android.
- Politique **Dernier thème choisi** : au cold start, thème = **dernier** clair/sombre choisi dans l'app ; le thème système Android est **ignoré**.
- Les deux thèmes respectent WCAG AA sur textes principaux des écrans critiques.

---

### 4.2 Notes secouriste (intervention_notes)

**Description :** Saisie structurée unifiée PSE1/PSE2 par blocs, optimisée terrain (gros contrôles, claviers adaptés, sauvegarde auto, reprise). Realise UJ-1, UJ-4.

**Functional Requirements:**

#### FR-3: Formulaire unifié PSE1/PSE2

L'utilisateur saisit les **notes secouriste** via un schéma unique couvrant PSE1 et PSE2. `[ASSUMPTION:]` Schéma v1 aligné sur un référentiel papier à valider avec 3 fiches réelles d'associations.

**Consequences (testable):**
- Un seul `schemaVersion` actif en MVP.
- Champs couvrent au minimum : contexte/lieu, victime/âge, constantes, gestes/soins, évolution, éléments de transmission.

#### FR-4: Saisie par blocs avec persistance automatique

L'utilisateur remplit des **blocs** scrollables ; chaque modification est persistée localement sans action « Enregistrer ». Realise UJ-1.

**Consequences (testable):**
- Kill app mid-saisie → données du bloc en cours conservées.
- Reprise au même bloc et scroll `[ASSUMPTION:]`.

#### FR-5: Ergonomie terrain

L'utilisateur peut saisir avec des contraintes terrain (gants, une main, faible luminosité). Realise UJ-1.

**Consequences (testable):**
- Zones tactiles principales ≥ 48 dp.
- Contraste texte/fond conforme WCAG AA sur écrans critiques.
- Thèmes clair et sombre disponibles (FR-18) ; contraste WCAG AA.

#### FR-6: Relevés multiples horodatés par constante

Pour chaque **constante** (TA, FC, FR, SpO2, etc.), l'utilisateur peut enregistrer **plusieurs relevés** au cours de la même intervention.

**Horodatage :**
- **Persistance** : chaque relevé stocke un instant complet **date + heure** (`recordedAt`, ISO 8601 ou équivalent local).
- **Affichage liste** : **HH:mm** uniquement (sauf si la date du relevé diffère du jour de l'intervention — alors afficher aussi la date, ex. `23/05 14:32`).
- **Saisie / modification** : à la création, défaut = maintenant ; l'utilisateur peut **antidater** via un écran ou sheet **Date + Heure** (date picker + heure/minute) pour corriger un relevé saisi en retard.

**Valeurs :**
- Constantes **scalaires** (FC, FR, SpO2, etc.) : une valeur numérique par relevé.
- **TA** : **deux saisies consécutives** dans le même relevé — **systolique** puis **diastolique** — un seul horodatage partagé.

Realise UJ-1.

**Consequences (testable):**
- L'utilisateur peut ajouter au moins 2 relevés pour la même constante sans quitter l'intervention.
- En base, chaque relevé a un `recordedAt` avec date et heure ; l'UI liste affiche au minimum `HH:mm`.
- L'utilisateur peut ouvrir l'édition date/heure d'un relevé et changer **jour et heure** ; la liste reflète le changement (avec date visible si ≠ jour de l'intervention).
- TA : un relevé ne peut être « complet » que si systolique **et** diastolique sont renseignés (ou les deux vides en brouillon).
- Suppression d'un relevé possible avec confirmation si seul relevé de la constante `[ASSUMPTION]`.
- Récap et script de transmission listent les relevés avec horodatage (`HH:mm`, + date si pertinent) et TA en forme `120/80`.

#### FR-7: Alertes constantes non bloquantes

Lorsqu'un **relevé** est hors **valeurs de référence**, l'entrée affiche une **alerte** couleur (au moins deux niveaux : limite / critique) sans empêcher la saisie ni la navigation. Realise UJ-1.

**Consequences (testable):**
- Valeur hors plage → couleur visible en &lt; 500 ms après validation du relevé.
- Aucun modal obligatoire pour continuer.
- Chaque relevé dans une liste peut avoir un état d'alerte distinct.

#### FR-8: Valeurs de référence personnalisables

L'utilisateur configure les **valeurs de référence** par constante et **tranche d'âge** ; un ⓘ sur le champ affiche les plages de la tranche active ; réinitialisation aux défauts usine possible. Realise UJ-1.

**Consequences (testable):**
- Modifier une plage dans Réglages → reflété sur la prochaine saisie.
- ⓘ affiche les valeurs de la tranche liée à l'âge saisi `[ASSUMPTION:]` ou tranche sélectionnée manuellement si âge absent.

#### FR-9: Récap écran pour transmission

L'utilisateur consulte un **récapitulatif lisible** des notes secouriste sur écran (gros caractères, ordre de dictée) sans export fichier. Realise UJ-3.

**Consequences (testable):**
- Récap accessible depuis intervention brouillon ou clôturée.
- Lisible à ~1 m sur écran 6" `[ASSUMPTION:]`.

#### FR-10: Script de transmission offline

L'utilisateur génère un **script de transmission** par règles locales (sans appel réseau ni LLM cloud) à partir des champs saisis. Realise UJ-3.

**Consequences (testable):**
- Génération en mode avion &lt; 3 s.
- Contenu ordonné : contexte → victime → constantes (**relevés horodatés**, avec mention alertes) → gestes → évolution.
- Mention utilisateur du type « Relire avant transmission ».

#### FR-11: Historique des interventions (liste sur l'accueil)

L'utilisateur consulte sur **l'écran d'accueil** (sous le CTA Nouvelle intervention) la liste des **interventions** triées par date (brouillon et clôturées). Pas d'écran « Historique » dédié. Realise UJ-4.

**Consequences (testable):**
- Chaque ligne : ligne 1 `NOM Prénom - Âge` (ou placeholder métier si identité absente) ; ligne 2 `date - heure` ; indicateur de statut (pastille couleur).
- Ouverture d'une intervention depuis la ligne.
- État vide : message + CTA Nouvelle intervention visible.

#### FR-12: Clôture en lecture seule

L'utilisateur **clôture** une intervention ; elle passe en statut **clôturée** et devient **lecture seule**. Realise UJ-4.

**Consequences (testable):**
- Aucun champ éditable après clôture.
- Tentative d'édition → message explicite (lecture seule) ; pas d'action **Dupliquer** (D-033).

**Notes:** Export PDF et export texte fichier — **hors MVP** (décision D-009). Lien discret aide mémoire depuis constante alertée — P1 `[ASSUMPTION:]`.

#### ~~FR-13: Dupliquer une intervention clôturée~~ (retiré — D-033)

---

### 4.3 Aide mémoire

**Description :** Fiches Markdown ultra compactes avec **repères** et schémas ; pack par défaut ; édition et personnalisation sur téléphone. 100 % offline. Realise UJ-2.

**Functional Requirements:**

#### FR-14: Consulter l'aide mémoire par situation

L'utilisateur parcourt des cartes **situation** → détail **repères** (Markdown rendu + image). Realise UJ-2.

**Consequences (testable):**
- Navigation en 2 taps max depuis accueil jusqu'au contenu d'une fiche.
- Fonctionne sans réseau.

#### FR-15: Pack par défaut embarqué

L'application livre un pack initial (5–10 fiches PSE) en assets locaux. Realise UJ-2.

**Consequences (testable):**
- Première ouverture offline → pack visible.

#### FR-16: Personnaliser l'aide mémoire

L'utilisateur édite le Markdown (éditeur simplifié) et peut ajouter/modifier des fiches et images locales. Realise UJ-2.

**Consequences (testable):**
- Modification persistée ; visible après redémarrage app.
- Édition hors flux « intervention active » `[ASSUMPTION:]` ou avec avertissement si intervention brouillon ouverte.

---

### 4.4 Paramètres & données

**Description :** Réglages des valeurs de référence et suppression manuelle. Conformité minimale RGPD (données santé potentielles).

**Functional Requirements:**

#### FR-17: Suppression manuelle

L'utilisateur peut **supprimer** manuellement une intervention (liste accueil : icône poubelle ou sélection multiple), avec confirmation. **Pas** de purge automatique ni de réglage de durée de rétention (D-034). Realise UJ-4.

**Consequences (testable):**
- Suppression manuelle → données non récupérables (intervention + notes en cascade).
- Aucun job en arrière-plan ne supprime des interventions par âge.

---

## 5. Non-Goals (Explicit)

- Document ou workflow **« bilan médical »** officiel.
- **Export PDF** ou export fichier texte en v1.
- **Synchronisation cloud**, comptes utilisateur, multi-appareils.
- **LLM cloud** ou toute fonction nécessitant le réseau pour le cœur métier.
- Intégration **SAMU**, DPI, ou SI tiers.
- Variantes formulaire distinctes PSE1 vs PSE2 en v1.
- CMS distant pour l'aide mémoire.
- Version **iOS** en v1.
- Mode **formation/simulation** dédié `[ASSUMPTION:]` flag simple reporté v1.1.
- **Duplication** d'une intervention clôturée (copie pré-remplie) — D-033.
- **Purge automatique** ou réglage de durée de rétention — D-034.

---

## 6. MVP Scope

### 6.1 In Scope

- Android, 100 % offline (saisie, historique, aide mémoire, script de transmission).
- Accueil hub : liste interventions + Nouvelle intervention ; Aide mémoire (icône header) ; thème clair/sombre (FR-18).
- Notes secouriste (`intervention_notes`) unifiées PSE1/PSE2.
- Relevés multiples horodatés par constante + alertes + valeurs de référence personnalisables + ⓘ.
- Récap écran + script de transmission rule-based.
- Historique, clôture lecture seule.
- Aide mémoire Markdown (pack défaut + édition).
- Suppression manuelle des interventions (liste accueil).

### 6.2 Out of Scope for MVP

| Élément | Raison | Cible |
|---------|--------|-------|
| Export PDF / texte | Transmission orale/SI autre ; décision produit | v2+ |
| Sync cloud | Offline total ; complexité RGPD | v2+ |
| LLM on-device | Coût/complexité ; script rule-based suffit v1 | v1.1+ |
| Intégration SAMU | Partenaires, cycles longs | v2+ |
| Photos / signatures | Non validé terrain | v1.1+ |
| Verrouillage biométrique app | Recommandé addendum ; `[ASSUMPTION:]` MVP+ | v1.0 ou v1.1 |

---

## 7. Cross-Cutting NFRs

### 7.1 Performance

- Saisie : persistance champ &lt; 500 ms.
- Ouverture aide mémoire : &lt; 2 s cold start carte.

### 7.2 Disponibilité & offline

- Toutes les FR MVP utilisables en **mode avion** sans dégradation fonctionnelle (hors liens externes optionnels dans aide mémoire — marqués « nécessite connexion »).

### 7.3 Security & privacy

- Données locales traitées comme **données de santé** potentielles (minimisation des champs, pas de télémétrie invasive MVP).
- Chiffrement au repos recommandé (addendum A.5).
- Hébergement : aucun en MVP.

### 7.4 Accessibility

- WCAG AA sur parcours critique (saisie, récap, aide mémoire).
- TalkBack : ordre de focus = ordre de dictée des blocs.

### 7.5 Platform

- **Android** natif ; `[ASSUMPTION:]` API min 26+ (Android 8).

---

## 8. Constraints and Guardrails

### Privacy (RGPD)

- Minimiser les champs identifiants ; documenter base légale et durée de conservation avant publication store.
- Pas de transfert vers pays tiers en v1.

### Safety

- Alertes et script de transmission : disclaimers — outil d'aide, ne remplace pas formation ni protocole officiel de l'organisme.

### Terminology guardrail

- Interdiction copy « bilan » — contrôle QA sur strings.

---

## 9. Success Metrics

**Primary**

- **SM-1:** 80 % des bêta-testeurs terrain déclarent la saisie « plus rapide ou équivalente au papier » sur 3 interventions simulées. Valide FR-4, FR-5, FR-6.
- **SM-2:** 100 % génération script de transmission réussie offline sur appareils cibles. Valide FR-10.

**Secondary**

- **SM-3:** ≥ 1 consultation aide mémoire par session d'intervention simulée (usage réel du repère). Valide FR-14.

**Counter-metrics (do not optimize)**

- **SM-C1:** Nombre de champs remplis par intervention — ne pas augmenter la friction pour « complétude » au détriment du temps terrain. Counterbalance tentation d'élargir le formulaire.

---

## 10. Open Questions

1. **Schéma exact des blocs** — quels champs obligatoires vs optionnels ? Nécessite 3 fiches papier d'associations. (O-003)
2. **Tranches d'âge** — libellés et bornes exactes pour valeurs de référence.
3. **Verrouillage app** — P0 ou MVP+ ?
4. **Lien alerte constante → fiche aide mémoire** — P0 ou P1 ?

---

## 11. Assumptions Index

- **§2.3** — PSC1 hors variante formulaire v1.
- **§4.2 FR-3** — Schéma aligné sur fiches papier à collecter.
- **§4.2 FR-4** — Reprise scroll/bloc après kill app.
- **§4.2 FR-5** — Mode sombre ou thème haute lisibilité.
- **§4.2 FR-6** — TA sys+dia ; stockage date+heure ; affichage HH:mm ; antidatage manuel ; suppression dernier relevé avec confirmation si seul.
- **§4.2 FR-8** — ⓘ lié à âge saisi ou tranche manuelle.
- **§4.2 FR-9** — Lisibilité récap à 1 m.
- **§4.2 FR-12** — Pas de duplication après clôture (D-033).
- **§4.2** — Lien alerte → aide mémoire en P1.
- **§4.3 FR-16** — Édition aide mémoire hors intervention active.
- **§6.2** — Verrouillage biométrique MVP ou v1.1.
- **§7.5** — API min Android 26.
