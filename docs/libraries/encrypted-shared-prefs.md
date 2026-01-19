# Security Crypto (EncryptedSharedPreferences)

**Version:** 1.1.0-alpha06

## Description

AndroidX Security Crypto fournit des implémentations chiffrées de SharedPreferences. Les données sont chiffrées avec AES256-GCM et les clés sont stockées dans le Android KeyStore (protégé par hardware).

## Pourquoi EncryptedSharedPreferences ?

1. **Sécurité** - Les tokens JWT sont chiffrés au repos, pas lisibles même avec root
2. **KeyStore hardware** - Clés de chiffrement protégées par le TEE/Secure Element
3. **API familière** - Même interface que SharedPreferences, migration facile
4. **Standard Google** - Solution recommandée pour le stockage sécurisé sur Android

## Alternatives considérées

- **SharedPreferences** : Pas de chiffrement, tokens en clair sur le device
- **KeyStore direct** : API bas niveau, complexe à utiliser correctement
- **SQLCipher** : Overkill pour quelques valeurs, plus lourd

## Usage dans Ora

Stockage sécurisé des tokens d'authentification (access token, refresh token) et de leur date d'expiration dans TokenManager.
