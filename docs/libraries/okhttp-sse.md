# OkHttp SSE

**Version:** 4.12.0

## Description

OkHttp SSE est un module d'OkHttp pour gérer les Server-Sent Events (SSE). SSE est un protocole permettant au serveur d'envoyer des données en continu vers le client via une connexion HTTP persistante.

## Pourquoi OkHttp SSE ?

1. **Maturité** - OkHttp est une librairie éprouvée et stable, utilisée massivement
2. **Compatibilité** - Fonctionne avec le moteur OkHttp de Ktor
3. **Simplicité** - API callback simple pour recevoir les événements
4. **Fiabilité** - Gestion robuste des connexions longue durée

## Alternatives considérées

- **Ktor SSE** : Plus récent, moins testé en production
- **WebSocket** : Bidirectionnel (overkill), plus complexe côté serveur
- **Polling** : Inefficace, latence élevée, consommation réseau

## Usage dans Ora

Streaming des réponses de l'IA en temps réel. Quand l'utilisateur envoie un message, le serveur répond via SSE avec des événements delta (contenu progressif), thinking, tool calls, etc.
