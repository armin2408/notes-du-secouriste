# Notes du Secouriste

Application Android 100 % hors ligne de prise de notes pour secouristes (PSE1/PSE2). Bêta 0.3.1.
L'utilisateur n'est pas développeur : expliquer simplement, demander avant toute action irréversible. Répondre en français.

## Stack
Kotlin, Jetpack Compose, Material 3 Expressive, Room, Hilt, Coroutines/Flow, Navigation Compose. minSdk 26.
Modules : `:app`, `:core:core-data`, `:core:core-ui`, `:feature:feature-intervention-notes`, `:feature:feature-aide-memoire`.

## Commandes (depuis `android/`)
Java n'est pas dans le PATH : `$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"`
- `.\gradlew assembleDebug` — APK debug
- `.\gradlew test` — tests unitaires
- `.\gradlew installDebug` — installer sur le téléphone USB
- `.\gradlew bundleRelease` — AAB Play Store (keystore requis)

## Règles d'architecture
- Flux unidirectionnel : UI (Compose) → ViewModel → Repository → DAO Room. `UiState` exposé en `StateFlow`.
- Aucune logique métier dans les Composables.
- Room est la seule source de vérité ; sections de notes stockées en JSON (`sectionsJson`, `schemaVersion`).
- Aucune permission réseau, aucun cloud, aucun compte : données uniquement sur l'appareil.
- Pas de valeurs vitales dans les logs en release.
- Textes utilisateur en français dans `strings.xml` ; ne jamais employer le mot « bilan ». Identifiants de code en anglais.
- Palette terrain : vert `#02A459`, pas de violet.

## Secrets — ne jamais afficher ni commiter
`android/release-keystore.jks`, `android/keystore.properties`, `android/local.properties`.

## Documentation
- Architecture : `_bmad-output/planning-artifacts/architecture.md`
- État d'avancement : `_bmad-output/implementation-artifacts/implementation-status.md`
- Publication : `docs/play-store-beta.md`
