# Stack Technique

## Langages & Plateformes

| Technologie | Version | Description |
|-------------|---------|-------------|
| **Kotlin** | 2.3.0 | Langage principal |
| **Android SDK** | 36 | Target SDK |
| **Min SDK** | 26 | Android 8.0 Oreo |
| **JVM Target** | 11 | Java Virtual Machine |

## UI & Présentation

| Librairie | Version | Usage |
|-----------|---------|-------|
| **Jetpack Compose** | BOM 2025.01.00 | Framework UI déclaratif |
| **Material 3** | (via BOM) | Design system |
| **Material Icons Extended** | (via BOM) | Icônes |
| **Navigation Compose** | 2.8.5 | Navigation entre écrans |
| **Coil** | 3.0.4 | Chargement d'images |
| **Markwon** | 4.6.2 | Rendu Markdown |

## Architecture & DI

| Librairie | Version | Usage |
|-----------|---------|-------|
| **Hilt** | 2.58 | Injection de dépendances |
| **Hilt Navigation Compose** | 1.2.0 | Intégration Hilt/Navigation |
| **Lifecycle ViewModel** | 2.10.0 | Gestion cycle de vie |
| **Lifecycle Runtime Compose** | 2.10.0 | Collecte de states |

## Réseau & API

| Librairie | Version | Usage |
|-----------|---------|-------|
| **Ktor Client** | 3.0.2 | Client HTTP |
| **Ktor OkHttp Engine** | 3.0.2 | Moteur HTTP |
| **Ktor Content Negotiation** | 3.0.2 | Sérialisation requêtes |
| **Ktor Logging** | 3.0.2 | Logs réseau |
| **OkHttp SSE** | 4.12.0 | Server-Sent Events |
| **Kotlinx Serialization** | 1.7.3 | Sérialisation JSON |

## Stockage & Sécurité

| Librairie | Version | Usage |
|-----------|---------|-------|
| **DataStore Preferences** | 1.1.1 | Préférences utilisateur |
| **Security Crypto** | 1.1.0-alpha06 | Stockage chiffré (tokens) |

## Asynchrone

| Librairie | Version | Usage |
|-----------|---------|-------|
| **Kotlinx Coroutines Core** | 1.9.0 | Programmation asynchrone |
| **Kotlinx Coroutines Android** | 1.9.0 | Support Android |

## Tests

| Librairie | Version | Usage |
|-----------|---------|-------|
| **JUnit** | 4.13.2 | Tests unitaires |
| **MockK** | 1.13.10 | Mocking |
| **Truth** | 1.4.2 | Assertions |
| **Turbine** | 1.1.0 | Test de Flows |
| **Ktor Client Mock** | 3.0.2 | Mock HTTP |
| **Espresso** | 3.7.0 | Tests UI |

## Build Tools

| Outil | Version | Usage |
|-------|---------|-------|
| **Android Gradle Plugin** | 8.13.2 | Build system |
| **KSP** | 2.3.3 | Annotation processing |
| **Kotlin Compose Plugin** | 2.3.0 | Compilation Compose |
| **Kotlin Serialization Plugin** | 2.3.0 | Génération sérialisation |

## Diagramme des dépendances

```
┌─────────────────────────────────────────────────────────────┐
│                         APPLICATION                         │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐ │
│  │   Compose   │  │    Hilt     │  │     Navigation      │ │
│  │  Material3  │  │             │  │      Compose        │ │
│  └─────────────┘  └─────────────┘  └─────────────────────┘ │
│                                                             │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐ │
│  │    Ktor     │  │   OkHttp    │  │      Kotlinx        │ │
│  │   Client    │  │     SSE     │  │   Serialization     │ │
│  └─────────────┘  └─────────────┘  └─────────────────────┘ │
│                                                             │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐ │
│  │  DataStore  │  │  Security   │  │     Coroutines      │ │
│  │             │  │   Crypto    │  │                     │ │
│  └─────────────┘  └─────────────┘  └─────────────────────┘ │
│                                                             │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐ │
│  │    Coil     │  │   Markwon   │  │     Lifecycle       │ │
│  │   (images)  │  │ (markdown)  │  │    ViewModel        │ │
│  └─────────────┘  └─────────────┘  └─────────────────────┘ │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

## Choix techniques clés

| Choix | Justification |
|-------|---------------|
| **Ktor vs Retrofit** | API Kotlin-first, meilleure intégration coroutines, multiplateforme |
| **Hilt vs Koin** | Compile-time safety, standard Google, meilleur tooling |
| **Compose vs XML** | UI déclarative moderne, moins de boilerplate, state management natif |
| **DataStore vs SharedPrefs** | Type-safe, coroutines, Flow support |
| **OkHttp SSE vs Ktor SSE** | Plus mature, meilleur support reconnexion |
