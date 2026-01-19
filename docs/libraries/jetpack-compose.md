# Jetpack Compose

**Version:** BOM 2025.01.00

## Description

Jetpack Compose est le toolkit UI moderne d'Android. Il permet de construire des interfaces utilisateur de manière déclarative en Kotlin, remplaçant le système de vues XML traditionnel.

## Pourquoi Jetpack Compose ?

1. **Déclaratif** - L'UI est une fonction de l'état, plus facile à comprendre et maintenir
2. **Moins de boilerplate** - Pas de findViewById, pas d'adapters pour RecyclerView
3. **State management intégré** - `remember`, `mutableStateOf`, `collectAsState` natifs
4. **Tout en Kotlin** - Pas de XML, pas de data binding, un seul langage
5. **Material 3** - Design system moderne intégré nativement
6. **Preview** - Visualisation des composants directement dans l'IDE

## Alternatives considérées

- **XML Views** : Ancien système, plus de boilerplate, gestion d'état complexe
- **Flutter** : Nécessite Dart, pas natif Android

## Usage dans Ora

Toute l'interface utilisateur : écrans (Login, Chat, Profile), composants réutilisables (boutons, inputs, messages), navigation, theming.
