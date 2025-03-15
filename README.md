# Application de Biathlon - Entraînement et Statistiques

Une application Android pour suivre vos entraînements de biathlon, enregistrer vos performances et visualiser vos statistiques.

## Fonctionnalités

- **Récupération des catégories** : Chargement des catégories d'entraînement depuis une API
- **Enregistrement d'entraînements** : Chronométrage des sessions de course et de tir
- **Suivi des performances** : Enregistrement du nombre de cibles manquées
- **Statistiques détaillées** : Visualisation des performances avec des graphiques
- **Historique complet** : Consultation de l'ensemble des entraînements passés
- **Interface intuitive** : Navigation simple et efficace

## Captures d'écran

[Insérer des captures d'écran de l'application ici]

## Technologies utilisées

- Kotlin
- Room Database pour la persistance des données
- MPAndroidChart pour les visualisations graphiques
- Architecture MVVM
- Coroutines pour les opérations asynchrones
- Retrofit pour les appels API

## Installation

1. Clonez la branche 'main'
2. Ouvrez le projet dans Android Studio
3. Exécutez l'application sur un émulateur ou un appareil physique

## Structure du projet

- `app/src/main/java/com/example/exo4/` - Code source principal
  - `database/` - Classes de base de données et DAO
  - `model/` - Modèles de données
  - `repository/` - Couche d'accès aux données
  - `utils/` - Utilitaires (ChronometreManager, etc.)
  - `adapter/` - Adaptateurs pour les RecyclerViews
  - `*.kt` - Activités principales

## État d'avancement

### Fonctionnalités implémentées
- ✅ Récupération et affichage des catégories
- ✅ Entraînement selon le format de la catégorie
- ✅ Chronométrage et enregistrement des temps
- ✅ Gestion des sessions de tir
- ✅ Résumé de l'entraînement
- ✅ Sauvegarde des entraînements
- ✅ Historique des résumés
- ✅ Statistiques de base (temps moyen, répartition, distribution)
- ✅ Courbes de progression

### Fonctionnalités à implémenter
Une carte est présente dans l'application mais elle n'a pas les fonctionnalités demandées.
- ❌ Affichage des entraînements sur une carte

## Maquette

Lien vers la maquette FIGMA : [Maquette Figma](https://www.figma.com/design/ZTtxWaVSlaBkX7T8owEoem/iPhone-14-Pro---Phone-Template-(Community)?node-id=0-1&p=f&t=0e890BdQ5oPZfO73-0)

## Contact

Pour toute question ou suggestion, n'hésitez pas à me contacter :
- Email personnel : germain.glc@gmail.com
- Email scolaire : germain.bonnet@etu.univ-poitiers.fr

