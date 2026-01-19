# Ora - Documentation Technique

> Application Android de chat avec agents IA

## Vue d'ensemble

Ora est une application Android native développée en Kotlin avec Jetpack Compose. Elle permet aux utilisateurs d'interagir avec différents agents IA via une interface de chat en temps réel utilisant Server-Sent Events (SSE).

## Structure de la documentation

```
docs/
├── README.md                    # Ce fichier
├── TECH_STACK.md               # Stack technique complète
│
├── architecture/               # Documentation architecture
│   ├── README.md               # Vue d'ensemble
│   ├── CLEAN_ARCHITECTURE.md   # Clean Architecture
│   ├── MVI_PATTERN.md          # Pattern MVI
│   ├── DEPENDENCY_INJECTION.md # Hilt DI
│   ├── ERROR_HANDLING.md       # Gestion des erreurs
│   ├── NAVIGATION.md           # Navigation Compose
│   └── diagrams/               # Diagrammes
│
├── tech-spec/                  # Spécifications techniques
│   ├── README.md               # Index
│   ├── MARKDOWN_RENDERING.md   # Rendu Markdown custom
│   └── SSE_EVENTS.md           # Événements SSE streaming
│
├── libraries/                  # Documentation des librairies
│   ├── README.md               # Index
│   ├── ktor-client.md          # HTTP Client
│   ├── hilt.md                 # Dependency Injection
│   ├── jetpack-compose.md      # UI Framework
│   └── ...
│
├── features/                   # Documentation par feature
│   ├── AUTH.md                 # Authentification
│   ├── CHAT.md                 # Chat & Streaming
│   └── PROFILE.md              # Profil utilisateur
│
├── api/                        # Documentation API
│   ├── API_ENDPOINTS.md        # Endpoints REST
│   └── DATA_MODELS.md          # Modèles de données
│
└── requirements/               # Spécifications projet
```

## Informations projet

| Propriété | Valeur |
|-----------|--------|
| **Package** | `com.ora.app` |
| **Min SDK** | 26 (Android 8.0) |
| **Target SDK** | 36 |
| **Langage** | Kotlin 2.3.0 |
| **UI Framework** | Jetpack Compose |
| **Architecture** | Clean Architecture + MVI |

## Quick Links

- [Stack Technique](./TECH_STACK.md)
- [Architecture](./architecture/README.md)
- [Spécifications Techniques](./tech-spec/README.md)
- [Librairies](./libraries/README.md)
- [Features](./features/)
- [API Endpoints](./api/API_ENDPOINTS.md)

## Build & Run

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run tests
./gradlew test
```

## Configuration

L'URL de l'API est configurée dans `app/build.gradle.kts`:

```kotlin
buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:8090\"")
```
