# Markwon

**Version:** 4.6.2

## Description

Markwon est une librairie de rendu Markdown pour Android. Elle parse le Markdown et génère du texte formaté (Spannable) affichable dans un TextView.

## Pourquoi Markwon ?

1. **Complet** - Support CommonMark + extensions (tables, strikethrough, etc.)
2. **Syntax highlighting** - Coloration syntaxique du code via Prism4j
3. **Tables** - Support des tables GitHub Flavored Markdown
4. **Modulaire** - On active uniquement les plugins nécessaires
5. **Performant** - Optimisé pour Android, rendu natif TextView

## Alternatives considérées

- **Custom parser** : Trop de travail, risque de bugs
- **WebView** : Lourd, consommation mémoire élevée, mauvaise intégration native

## Usage dans Ora

Rendu des réponses de l'IA qui contiennent du Markdown : titres, listes, **gras**, *italique*, `code inline`, blocs de code avec coloration syntaxique, tableaux.
