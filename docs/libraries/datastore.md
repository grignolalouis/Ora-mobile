# DataStore Preferences

**Version:** 1.1.1

## Description

DataStore est une solution de stockage de données moderne d'Android. Elle remplace SharedPreferences avec une API basée sur Kotlin coroutines et Flow.

## Pourquoi DataStore ?

1. **Thread-safe** - Opérations asynchrones, pas de risque d'ANR
2. **Flow natif** - Observation réactive des changements de préférences
3. **Transactions atomiques** - Pas de données corrompues en cas de crash
4. **Type-safe** - Clés typées, pas de cast dangereux
5. **Coroutines** - API `suspend`, s'intègre naturellement avec le reste du code

## Alternatives considérées

- **SharedPreferences** : API synchrone, pas thread-safe, pas de Flow
- **Room** : Trop complexe pour de simples préférences clé-valeur

## Usage dans Ora

Stockage des préférences utilisateur : thème (clair/sombre/système) et langue (FR/EN/ES/système).
