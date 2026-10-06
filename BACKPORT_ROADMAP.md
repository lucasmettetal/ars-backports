# Backport Roadmap

Aucun de ces backports n'est commencé. L'utilisateur décide de ce qui sera ajouté.

## Priorité actuelle
1. **Enchanter's Gauntlet** (en cours, voir `HANDOFF.md`)

## Candidats à étudier plus tard

Liste obtenue en comparant les noms d'items/blocs (`LibItemNames`, `LibBlockNames`) et les fichiers de glyphes
entre Ars Nouveau `1.20` (4.12.7, commit `2c74064b`) et `main` (5.13.3, commit `fd8c9520`).
**Les classements ci-dessous sont des estimations préliminaires** : aucun de ces éléments n'a encore été
analysé en profondeur. Chaque entrée doit faire l'objet d'une analyse 1.21.1 vs 4.12.7 (comme pour le Gauntlet)
avant toute décision.

### FACILE (probable)
| Fonctionnalité | Dépendances | Changements MC 1.21 | Code approx. | Risque de conflit avec 4.12.7 |
|---|---|---|---|---|
| Enchanter's Fishing Rod (`enchanters_fishing_rod`) | Ars caster API, entité `EnchantedHook` | Data Components, GeckoLib renderer | ~250 lignes + assets | Faible (même commit que le Gauntlet, mêmes mécanismes) |
| Starbuncle plush (`starbuncle_plush`) | aucune | aucun notable | petit + assets | Faible (décoratif) ; assets ARR |

### MOYEN (probable)
| Fonctionnalité | Dépendances | Changements MC 1.21 | Code approx. | Risque |
|---|---|---|---|---|
| Nouveaux glyphes : `EffectBubble`, `EffectWindburst`, `EffectPrestidigitation` | API glyphes Ars (`AbstractEffect`), parfois nouvelles entités (bulle) | Windburst s'inspire de la Wind Charge 1.21 (absente en 1.20.1) | 100–400 lignes chacun | Moyen : enregistrement de glyphes depuis un addon possible, mais IDs/recettes à ne pas faire entrer en collision |
| Forme `MethodPantomime` | API `AbstractCastMethod` | à analyser | à estimer | Moyen |
| Bateau en Archwood (`archwood_boat`) | entités bateau | API bateaux différente 1.20/1.21 | moyen | Faible |
| Decor blossom (`decor_blossom`) | blocs | faible | petit | Faible |

### DIFFICILE (probable)
| Fonctionnalité | Dépendances | Changements MC 1.21 | Code approx. | Risque |
|---|---|---|---|---|
| Storage Catalog / Repository controller (`repository_controller`, commit « repository catalog ») | système de stockage Ars (Storage Lectern, repositories) | Data Components, menus, réseau | important (GUI + réseau + logique de stockage) | Élevé : touche au système de stockage existant d'Ars 4.12.7 |
| Alakarkinos (charm, hat, token, spawn egg) | entité + IA, loot | à analyser | important | Moyen ; vérifier ce qui existe déjà en 4.12.7 |

### TRÈS DIFFICILE / NON RENTABLE (probable)
| Fonctionnalité | Dépendances | Changements MC 1.21 | Code approx. | Risque |
|---|---|---|---|---|
| Planarium / Planarium projector / Scryer planarium / `dim_boundary` | dimensions/structures, rendu | rendu et worldgen très différents | très important | Élevé |
| Refonte des timelines de particules / sons de sort (`particleTimeline`) | cœur du système de sorts | Data Components | invasif | Très élevé : nécessiterait de modifier Ars (Mixins), contraire aux principes |

### Autres QoL à étudier
- Parcourir `changelog.md` de la branche `main` d'Ars Nouveau pour les améliorations de qualité de vie.
- Ne retenir que ce qui peut être fait sans modifier Ars Nouveau ni ajouter de Mixin.
