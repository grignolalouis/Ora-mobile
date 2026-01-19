# Architecture

## Vue d'ensemble

L'application Ora suit une architecture **Clean Architecture** combinée avec le pattern **MVI** (Model-View-Intent) pour la couche présentation.

## Couches

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                       │
│         (ViewModels, States, Intents, Effects)             │
├─────────────────────────────────────────────────────────────┤
│                      DOMAIN LAYER                           │
│              (Use Cases, Repository Interfaces)             │
├─────────────────────────────────────────────────────────────┤
│                       DATA LAYER                            │
│        (Repository Impl, API Services, Mappers)            │
├─────────────────────────────────────────────────────────────┤
│                       CORE LAYER                            │
│         (DI, Network, Storage, Error, Utils)               │
└─────────────────────────────────────────────────────────────┘
```

## Structure des packages

```
com.ora.app/
├── core/
│   ├── di/          # Modules Hilt (AppModule, NetworkModule, RepositoryModule)
│   ├── error/       # AppError, ErrorMapper, ApiException
│   ├── locale/      # LocaleHelper (i18n)
│   ├── network/     # HttpClientFactory, AuthInterceptor, ApiConfig
│   ├── session/     # AuthEventBus
│   ├── storage/     # TokenManager, ThemePreferences, LanguagePreferences
│   ├── util/        # Result<T>, DateTimeUtil
│   └── validation/  # ValidationUtils, ValidationConstants
│
├── data/
│   ├── mapper/      # UserMapper, AgentMapper, SessionMapper, SSEEventMapper
│   ├── remote/
│   │   ├── api/     # AuthApiService, AgentApiService
│   │   ├── dto/     # Request/Response DTOs
│   │   └── sse/     # SSEClient
│   └── repository/  # AuthRepositoryImpl, AgentRepositoryImpl, SessionRepositoryImpl
│
├── domain/
│   ├── model/       # User, Agent, Session, Message, Interaction, StreamEvent
│   ├── repository/  # AuthRepository, AgentRepository, SessionRepository (interfaces)
│   └── usecase/     # LoginUseCase, GetAgentsUseCase, StreamResponseUseCase, etc.
│
├── presentation/
│   ├── designsystem/
│   │   ├── components/  # Composants réutilisables
│   │   └── theme/       # OraTheme, Colors, Typography, Dimensions
│   ├── features/
│   │   ├── auth/        # Login, Register screens + ViewModel
│   │   ├── chat/        # Chat screen + components + ViewModel
│   │   └── profile/     # Profile screen + ViewModel
│   ├── mvi/             # MviViewModel, UiState, UiIntent, UiEffect
│   └── navigation/      # Routes, NavGraph
│
├── MainActivity.kt
└── OraApplication.kt
```

## Documentation détaillée

- [Clean Architecture](./CLEAN_ARCHITECTURE.md)
- [Pattern MVI](./MVI_PATTERN.md)
- [Injection de dépendances](./DEPENDENCY_INJECTION.md)
- [Gestion des erreurs](./ERROR_HANDLING.md)
- [Navigation](./NAVIGATION.md)

## Diagrammes

- [Diagramme de classes complet](./diagrams/class-diagram.md)
- [Diagramme d'architecture simplifié](./diagrams/architecture-diagram.svg)
