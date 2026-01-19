# Kotlin Coroutines

**Version:** 1.9.0

## Description

Kotlin Coroutines est une librairie pour la programmation asynchrone. Elle permet d'écrire du code asynchrone de manière séquentielle et lisible, sans callbacks imbriqués.

## Pourquoi Kotlin Coroutines ?

1. **Syntaxe séquentielle** - Le code async ressemble à du code synchrone, facile à lire
2. **Structured concurrency** - Annulation automatique des tâches enfants, pas de memory leaks
3. **Native Kotlin** - Intégré au langage avec les mots-clés `suspend`, `async`, `launch`
4. **Flow** - Streams réactifs type-safe pour les données qui changent dans le temps
5. **Intégration Android** - `viewModelScope`, `lifecycleScope` gèrent automatiquement le cycle de vie

## Alternatives considérées

- **RxJava** : Plus puissant mais courbe d'apprentissage élevée, API complexe
- **Callbacks** : Callback hell, difficile à maintenir et tester

## Usage dans Ora

Toutes les opérations asynchrones : appels API, lecture/écriture DataStore, streaming SSE avec Flow, gestion d'état avec StateFlow.
