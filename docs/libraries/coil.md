# Coil

**Version:** 3.0.4

## Description

Coil (Coroutine Image Loader) est une librairie de chargement d'images pour Android. Elle gère le téléchargement, le cache (mémoire et disque), et l'affichage des images.

## Pourquoi Coil ?

1. **Kotlin-first** - API Kotlin native avec coroutines intégrées
2. **Compose natif** - Composable `AsyncImage` officiel, pas besoin de wrapper
3. **Léger** - ~1500 méthodes vs ~3000 pour Glide
4. **Moderne** - Maintenu activement, API moderne
5. **Cache efficace** - Double cache mémoire + disque

## Alternatives considérées

- **Glide** : Librairie Java, nécessite Accompanist pour Compose, plus lourd
- **Picasso** : Pas de cache disque, moins maintenu

## Usage dans Ora

Chargement et affichage des photos de profil utilisateur (avatar).
