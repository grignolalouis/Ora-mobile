# Défis Techniques - Ora

Documentation des aspects les plus complexes rencontrés lors du développement de l'application.

---

## 1. Streaming SSE avec Mise à Jour UI en Temps Réel

### Problématique

Le streaming des réponses IA via Server-Sent Events représente le défi technique majeur du projet. Contrairement aux requêtes HTTP classiques qui retournent une réponse complète, le SSE nécessite une gestion continue du flux de données avec mise à jour de l'interface à chaque token reçu.

### Difficultés rencontrées

**Gestion du cycle de vie Android** : Les connexions SSE doivent survivre aux rotations d'écran et changements de configuration tout en étant correctement fermées lors de la destruction de l'Activity. Une mauvaise gestion entraîne des fuites mémoire ou des connexions orphelines.

**Synchronisation thread UI** : Les événements SSE arrivent sur un thread IO. Chaque mise à jour du texte doit être dispatchée sur le Main thread via `Dispatchers.Main`, tout en évitant le blocage de l'UI lors de rafales d'événements rapides.

**Reconstruction du message** : Les tokens arrivent fragmentés (parfois un seul caractère). L'accumulation progressive dans un `StringBuilder` avec invalidation du state Compose à chaque ajout génère potentiellement des centaines de recompositions par seconde.

### Solution implémentée

```kotlin
// Buffering des événements avec debounce implicite
private val _messageBuffer = StringBuilder()
private var lastEmitTime = 0L

fun onDelta(token: String) {
    _messageBuffer.append(token)
    val now = System.currentTimeMillis()
    if (now - lastEmitTime > 16) { // ~60fps max
        emitCurrentState()
        lastEmitTime = now
    }
}
```

L'utilisation de `StateFlow` avec `conflate()` permet d'absorber les pics de charge en ne gardant que la dernière valeur lors de la collecte.

---

## 2. Rendu Markdown avec Coloration Syntaxique

### Problématique

Jetpack Compose ne propose pas de composant Markdown natif. L'affichage de code source avec coloration syntaxique pour 13 langages différents nécessite une solution hybride.

### Difficultés rencontrées

**Interopérabilité Compose/View** : Markwon génère des `Spannable` pour les `TextView` Android classiques. L'intégration dans Compose via `AndroidView` crée une rupture dans le système de recomposition et complique la gestion du thème (clair/sombre).

**Performance du parsing** : Le parsing regex pour la coloration syntaxique sur de longs blocs de code (>500 lignes) provoque des freezes perceptibles. Le parsing doit s'effectuer de manière asynchrone sans bloquer le rendu initial.

**Gestion des langages** : Chaque langage possède ses propres règles lexicales. Les expressions régulières doivent gérer les cas limites (strings multilignes, commentaires imbriqués, templates literals).

### Solution implémentée

Création d'un `SyntaxHighlighter` custom avec :
- Parsing lazy : coloration appliquée uniquement aux blocs visibles
- Cache LRU : résultats de parsing mémorisés par hash du contenu
- Règles regex optimisées : patterns compilés une seule fois au démarrage

```kotlin
object SyntaxHighlighter {
    private val cache = LruCache<Int, SpannableString>(50)

    fun highlight(code: String, language: String): SpannableString {
        val key = (code + language).hashCode()
        return cache.get(key) ?: parseAndCache(code, language, key)
    }
}
```

---

## 3. Architecture MVI avec État de Streaming

### Problématique

Le pattern MVI classique suppose des états discrets et des transitions atomiques. Le streaming introduit un état "en cours" continu qui évolue plusieurs fois par seconde.

### Difficultés rencontrées

**Explosion combinatoire des états** : Un message en streaming peut être simultanément en cours de réception, en cours de parsing Markdown, et en attente d'une réponse tool_call. La représentation de ces états combinés complexifie le sealed class `ChatState`.

**Cohérence des données** : Les événements SSE peuvent arriver dans le désordre (notamment lors de reconnexions). Garantir que l'UI reflète toujours un état cohérent requiert une logique de réconciliation.

**Annulation et nettoyage** : L'utilisateur peut annuler une requête en cours. Tous les coroutines, buffers et états temporaires doivent être correctement nettoyés sans laisser de données corrompues.

### Solution implémentée

Utilisation d'un état composite avec sous-états indépendants :

```kotlin
data class ChatState(
    val messages: List<Message>,
    val streamingState: StreamingState,
    val inputState: InputState,
    val error: ErrorState?
)

sealed class StreamingState {
    object Idle : StreamingState()
    data class Receiving(
        val messageId: String,
        val buffer: String,
        val isThinking: Boolean
    ) : StreamingState()
}
```

---

## 4. Gestion de l'Authentification avec Refresh Token

### Problématique

L'application doit maintenir une session utilisateur persistante tout en gérant l'expiration des tokens JWT de manière transparente.

### Difficultés rencontrées

**Race condition sur le refresh** : Plusieurs requêtes simultanées peuvent détecter un token expiré. Sans synchronisation, chacune tente un refresh, causant des erreurs 401 en cascade.

**Stockage sécurisé** : Les tokens doivent être persistés de manière chiffrée. `EncryptedSharedPreferences` introduit une latence notable au premier accès (~200ms) due à l'initialisation du KeyStore.

**Gestion des erreurs de refresh** : Un refresh échoué (token révoqué, serveur indisponible) doit déclencher une déconnexion propre sans perdre les données locales non synchronisées.

### Solution implémentée

Implémentation d'un `AuthInterceptor` avec mutex pour sérialiser les tentatives de refresh :

```kotlin
class AuthInterceptor : Interceptor {
    private val refreshMutex = Mutex()

    override fun intercept(chain: Chain): Response {
        val response = chain.proceed(addToken(chain.request()))

        if (response.code == 401) {
            return refreshMutex.withLock {
                // Double-check après acquisition du lock
                if (isTokenStillExpired()) {
                    refreshToken()
                }
                chain.proceed(addToken(chain.request()))
            }
        }
        return response
    }
}
```

---

## 5. Intégration OkHttp SSE dans Architecture Ktor

### Problématique

Ktor Client ne supporte pas nativement les Server-Sent Events. L'intégration d'OkHttp SSE aux côtés de Ktor nécessite une cohabitation de deux clients HTTP.

### Difficultés rencontrées

**Duplication de configuration** : Les headers d'authentification, timeouts et intercepteurs doivent être synchronisés entre les deux clients.

**Gestion des erreurs hétérogène** : Ktor utilise des exceptions Kotlin, OkHttp des callbacks Java. L'unification du modèle d'erreur vers le `Result<T>` du domain layer requiert des adapters.

**Injection de dépendances** : Hilt doit fournir les deux clients avec leurs configurations respectives sans créer de dépendances circulaires.

### Solution implémentée

Création d'un module Hilt dédié avec factory partagée pour la configuration commune :

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideOkHttpClient(authStore: AuthTokenStore): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(authStore))
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    fun provideKtorClient(okHttpClient: OkHttpClient): HttpClient {
        return HttpClient(OkHttp) {
            engine { preconfigured = okHttpClient }
        }
    }
}
```

---

## Conclusion

Ces défis techniques ont nécessité une compréhension approfondie de l'écosystème Android moderne et des compromis entre performance, maintenabilité et expérience utilisateur. Les solutions adoptées privilégient la robustesse et la testabilité, parfois au détriment de la simplicité du code.
