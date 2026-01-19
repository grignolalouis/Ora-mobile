# Ktor Client

**Version:** 3.0.2

## Description

Ktor Client est un client HTTP multiplateforme développé par JetBrains. Il permet d'effectuer des requêtes HTTP (GET, POST, etc.) vers des APIs REST de manière asynchrone.

## Pourquoi Ktor Client ?

1. **Kotlin-first** - API native Kotlin avec DSL fluide, contrairement à Retrofit qui est un wrapper Java
2. **Coroutines natives** - Toutes les requêtes sont des `suspend fun`, pas besoin d'adapter
3. **Multiplateforme** - Prêt pour Kotlin Multiplatform si on veut partager le code réseau avec iOS
4. **Modulaire** - On ajoute uniquement les plugins nécessaires (logging, serialization, auth)
5. **Cohérence** - Même éditeur que Kotlin (JetBrains)

## Alternatives considérées

- **Retrofit** : Plus mature mais API Java, nécessite des adapters pour coroutines
- **OkHttp seul** : Trop bas niveau, beaucoup de boilerplate

## Usage dans Ora

Utilisé pour toutes les requêtes API : authentification, gestion des agents, sessions, envoi de messages.
