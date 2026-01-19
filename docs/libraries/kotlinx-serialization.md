# Kotlinx Serialization

**Version:** 1.7.3

## Description

Kotlinx Serialization est une librairie de sérialisation multiplateforme pour Kotlin. Elle permet de convertir des objets Kotlin en JSON (et autres formats) et vice-versa.

## Pourquoi Kotlinx Serialization ?

1. **Kotlin native** - Utilise les annotations Kotlin, comprend la nullability
2. **Compile-time** - Code généré à la compilation, pas de réflexion au runtime
3. **Type-safe** - Erreurs de typage détectées à la compilation
4. **Intégration Ktor** - Plugin officiel pour Ktor Client, sérialisation automatique
5. **Multiplateforme** - Fonctionne avec Kotlin Multiplatform

## Alternatives considérées

- **Gson** : Librairie Java, utilise la réflexion, ne comprend pas la nullability Kotlin
- **Moshi** : Bon mais nécessite des adapters, moins intégré avec l'écosystème Kotlin

## Usage dans Ora

Sérialisation/désérialisation de tous les DTOs pour les requêtes et réponses API, parsing des événements SSE.
