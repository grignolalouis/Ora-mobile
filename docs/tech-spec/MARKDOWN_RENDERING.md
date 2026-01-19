# Rendu Markdown

Documentation du système de rendu Markdown personnalisé dans Ora.

## Vue d'ensemble

Les réponses de l'IA contiennent du Markdown (titres, listes, code, tableaux). Le rendu est géré par **Markwon** avec des personnalisations pour le thème et la coloration syntaxique.

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                     AssistantMessage                            │
│                           │                                     │
│                           ▼                                     │
│                    MarkdownText()                               │
│                           │                                     │
│              ┌────────────┴────────────┐                       │
│              │                         │                        │
│              ▼                         ▼                        │
│      Texte Markdown            Blocs de code                   │
│      (via Markwon)             (via CodeBlock)                 │
│              │                         │                        │
│              │                         ▼                        │
│              │                 SyntaxHighlighter               │
│              │                         │                        │
│              ▼                         ▼                        │
│         TextView                  AnnotatedString              │
│         (Android)                 (Compose)                    │
└─────────────────────────────────────────────────────────────────┘
```

## Fichiers

| Fichier | Rôle |
|---------|------|
| `MarkdownText.kt` | Composable principal, parsing et rendu |
| `CodeBlock.kt` | Affichage des blocs de code avec header |
| `SyntaxHighlighter.kt` | Coloration syntaxique par langage |

## MarkdownText.kt

### Fonctionnement

1. **Extraction des blocs de code** via regex : `` ```(\w*)\n([\s\S]*?)``` ``
2. **Segmentation** du contenu en `TextSegment` (Markdown) et `CodeSegment` (code)
3. **Rendu** : Markwon pour le texte, `CodeBlock` pour le code

### Plugins Markwon utilisés

| Plugin | Fonctionnalité |
|--------|----------------|
| `StrikethroughPlugin` | Texte barré `~~texte~~` |
| `TablePlugin` | Tableaux GFM |
| `LinkifyPlugin` | Détection auto des liens |
| `AbstractMarkwonPlugin` | Personnalisation du thème |

### Personnalisations thème

```kotlin
// Inline code
codeTextColor(textColor.toArgb())
codeBackgroundColor(codeBackgroundColor.toArgb())

// Titres (multiplicateurs de taille)
h1 = 1.5f, h2 = 1.25f, h3 = 1.125f, h4 = 1f, h5 = 0.875f, h6 = 0.75f

// Liens
linkColor(linkColor.toArgb())

// Block quotes
blockQuoteColor(codeBackgroundColor.toArgb())
```

### Typographie

- **Police** : Urbanist (Regular, Bold)
- **Taille** : 16sp
- **Interligne** : 1.5x
- **Espacement** : 0.01f

## CodeBlock.kt

### Structure

```
┌─────────────────────────────────────────┐
│ kotlin                      [📋 Copy]  │  ← Header
├─────────────────────────────────────────┤
│ fun hello() {                          │
│     println("Hello")                   │  ← Code (scrollable)
│ }                                      │
└─────────────────────────────────────────┘
```

### Fonctionnalités

- **Header** : Affiche le langage + bouton copier
- **Copie** : Feedback visuel "Copied!" pendant 2 secondes
- **Scroll horizontal** : Pour le code large
- **Thème adaptatif** : Couleurs light/dark

### Couleurs

**Dark theme (Gruvbox):**
```kotlin
background = #1D2021
headerBackground = #282828
text = #EBDBB2
secondaryText = #A89984
```

**Light theme:**
```kotlin
background = #F5F4F0
headerBackground = #E8E6E1
text = #1A1A18
secondaryText = #6B6B6B
```

## SyntaxHighlighter.kt

### Approche

Coloration **regex-based** (pas Prism4j pour la perf). Retourne un `AnnotatedString` avec des spans colorés.

### Langages supportés

| Langage | Aliases |
|---------|---------|
| Kotlin | `kotlin`, `kt` |
| Java | `java` |
| Python | `python`, `py` |
| JavaScript | `javascript`, `js`, `typescript`, `ts` |
| JSON | `json` |
| HTML/XML | `html`, `xml` |
| CSS | `css`, `scss`, `sass` |
| Bash | `bash`, `sh`, `shell`, `zsh` |
| SQL | `sql` |
| Go | `go`, `golang` |
| Rust | `rust`, `rs` |
| Swift | `swift` |

### Tokens colorés

| Token | Dark | Light |
|-------|------|-------|
| Keywords | `#FB4934` (rouge) | `#D32F2F` |
| Strings | `#B8BB26` (vert) | `#558B2F` |
| Numbers | `#D3869B` (violet) | `#7B1FA2` |
| Comments | `#928374` (gris) | `#757575` |
| Functions | `#8EC07C` (aqua) | `#00796B` |
| Types | `#FABD2F` (jaune) | `#F57C00` |
| Operators | `#FE8019` (orange) | `#E64A19` |
| Variables | `#83A598` (bleu) | `#1976D2` |

### Patterns de détection

```kotlin
// Strings
"\"[^\"\\\\]*(\\\\.[^\"\\\\]*)*\""

// Numbers
"\\b\\d+(\\.\\d+)?\\b"

// Comments
"//.*$" et "/\\*[\\s\\S]*?\\*/"

// Keywords (par langage)
"\\b(fun|val|var|class|if|else|when|...)\\b"

// Functions
"\\b([a-zA-Z_][a-zA-Z0-9_]*)\\s*\\("

// Types
"\\b[A-Z][a-zA-Z0-9_]*\\b"
```

## Syntaxe Markdown supportée

```markdown
# Titre 1
## Titre 2
### Titre 3

**Gras** et *italique*

~~Texte barré~~

- Liste à puces
- Item 2

1. Liste numérotée
2. Item 2

[Lien](https://example.com)

`code inline`

| Colonne 1 | Colonne 2 |
|-----------|-----------|
| Cellule   | Cellule   |
```

~~~markdown
```kotlin
fun hello() = println("Hello")
```
~~~

## Gestion des erreurs

- **Parse error** : Affiche le texte brut sans crash
- **Highlighting error** : Affiche le code sans coloration
- **Langage inconnu** : Fallback vers highlighting basique
