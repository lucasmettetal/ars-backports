# Backport Roadmap

L'utilisateur décide de ce qui est ajouté. Rien n'est commencé en dehors du Gauntlet.

Classement établi le 2026-10-09 en comparant les branches Ars Nouveau `1.20` (4.12.7, `2c74064`) et `main`
(5.x / 1.21.1) — liste des glyphes, modèles d'items, blockstates et classes Java — puis en lisant le code de chaque
candidat. Chaque backport fera l'objet d'une analyse détaillée 1.21.1 vs 4.12.7 avant d'être codé.

**Contrainte commune** : textures/modèles Ars = All Rights Reserved (README : « Addon authors are encouraged to reuse
Ars Nouveau assets and to reach out in the discord »). Placeholders originaux tant qu'aucune autorisation n'est obtenue.
Les éléments purement décoratifs n'ont pas d'intérêt sans les assets officiels.

## En cours
1. **Enchanter's Gauntlet** — codé (étapes A à F), tests en jeu à faire (`TESTS.md`).

## ✅ Faisable, ordre proposé
| # | Fonctionnalité | Difficulté | Notes |
|---|---|---|---|
| 2 | Mob Jar : **Sniffer** (graines anciennes), **Chat** (signal redstone si apprivoisé + carte des chats), **Golem de neige** (boule de neige sur signal) | facile | pur code, aucun asset ; `JarBehaviorRegistry.register(EntityType, JarBehavior)` est public en 4.12.7 ; ces mobs existent en 1.20.1 |
| 3 | **Enchanter's Fishing Rod** + entité `EnchantedHook` | moyenne | ~250 lignes ; mêmes mécanismes `ICasterTool` que le Gauntlet ; Data Components → NBT/SpellCaster |
| 4 | Glyphe **Pantomime** (forme : lance sur un point devant le joueur) | facile–moyenne | timeline de particules 1.21 → particules existantes d'Ars 4.12.7 ; icône placeholder ; recette de glyphe |
| 5 | Glyphe **Bubble** + `BubbleEntity` | moyenne | ~350 lignes ; rendu simple sans GeckoLib ; l'aspect « mouillé » officiel passe par un Mixin → omis |

## ⚠️ Faisable mais adapté (pas identique)
| Fonctionnalité | Problème |
|---|---|
| Glyphe **Windburst** | basé sur la Wind Charge (absente en 1.20.1) : à recréer (souffle sans dégâts de blocs), sans particules/sons d'origine |
| **Source Lamp** | hérite de `CopperBulbBlock` (1.21) : comportement à réécrire ; assets |
| **Grilles** (archwood, gold, sourcestone, smooth sourcestone), **archwood hanging sign** | simples, surtout des assets |
| **Ars Stencil** (motif de bannière) | simple, intérêt = texture |
| **Bateau en archwood** | type de bateau non extensible en 1.20.1 → entité dédiée ; assets |
| **Starbuncle plush**, **Decor Blossom** | 100 % décoratifs → seulement avec les assets officiels |

## ❌ Exclus
| Fonctionnalité | Raison |
|---|---|
| Planarium / Planarium Projector / Scryer Planarium / `dim_boundary` | nouvelle dimension + rendu de mini-monde (exclu par l'utilisateur) |
| Timelines de particules / styles de sort, Prestidigitation (qui en dépend) | cœur du système de sorts → Mixins |
| Nouveau système de documentation | système complet remplaçant celui d'Ars |
| Repository Catalog | stockage + GUI + réseau, conflit probable avec le stockage 4.12.7 |
| Alakarkinos (crabe, charm, chapeau) | entité + IA + recettes + JEI/EMI + modèle animé, tout repose sur les assets |
| Mob Jar Armadillo, Breeze | mobs inexistants en 1.20.1 |
| Unbreakable perk, effet Soaked | uniquement par Mixin |

## Autres pistes
- Parcourir `changelog.md` de la branche `main` d'Ars pour d'éventuelles améliorations de qualité de vie,
  en ne retenant que ce qui se fait sans modifier Ars ni ajouter de Mixin.
