# Hilt

**Version:** 2.58

## Description

Hilt est un framework d'injection de dépendances pour Android, basé sur Dagger. Il simplifie la configuration de Dagger en fournissant des composants prédéfinis pour Android (Application, Activity, ViewModel, etc.).

## Pourquoi Hilt ?

1. **Compile-time safety** - Les erreurs de dépendances sont détectées à la compilation, pas au runtime
2. **Standard Google** - Recommandé officiellement pour Android, bien intégré avec Jetpack
3. **Performance** - Code généré à la compilation, pas de réflexion au runtime
4. **Scopes prédéfinis** - SingletonComponent, ViewModelComponent, etc. déjà configurés
5. **Tooling** - Bon support IDE avec navigation vers les providers

## Alternatives considérées

- **Koin** : Plus simple mais validation au runtime (crashes possibles en prod)
- **Dagger pur** : Plus flexible mais configuration complexe et verbose
- **Manual DI** : Trop de boilerplate pour un projet de cette taille

## Usage dans Ora

Injection des repositories, use cases, et ViewModels. Gestion du scope Singleton pour les services partagés (TokenManager, HttpClient).
