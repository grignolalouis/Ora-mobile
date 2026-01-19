# Librairies

Documentation des librairies utilisées dans Ora avec justification des choix.

## Index

| Librairie | Version | Usage |
|-----------|---------|-------|
| [Ktor Client](./ktor-client.md) | 3.0.2 | Client HTTP pour les appels API |
| [Hilt](./hilt.md) | 2.58 | Injection de dépendances |
| [Jetpack Compose](./jetpack-compose.md) | BOM 2025.01.00 | Framework UI déclaratif |
| [Kotlin Coroutines](./kotlin-coroutines.md) | 1.9.0 | Programmation asynchrone |
| [Kotlinx Serialization](./kotlinx-serialization.md) | 1.7.3 | Sérialisation JSON |
| [DataStore](./datastore.md) | 1.1.1 | Stockage préférences |
| [OkHttp SSE](./okhttp-sse.md) | 4.12.0 | Streaming Server-Sent Events |
| [Security Crypto](./encrypted-shared-prefs.md) | 1.1.0-alpha06 | Stockage chiffré des tokens |
| [Coil](./coil.md) | 3.0.4 | Chargement d'images |
| [Markwon](./markwon.md) | 4.6.2 | Rendu Markdown |

## Critères de sélection

Chaque librairie a été choisie selon ces critères :

- **Kotlin-first** : API native Kotlin, pas de wrapper Java
- **Coroutines** : Support natif des suspend functions et Flow
- **Maintenance** : Mises à jour régulières, communauté active
- **Performance** : Impact minimal sur la taille APK et les performances runtime
