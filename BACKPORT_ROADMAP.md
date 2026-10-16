# Backport Roadmap

L'utilisateur décide de ce qui est ajouté. État détaillé de chaque fonctionnalité : `HANDOFF.md` §1.
**À mettre à jour à la fin de chaque session** (demande de l'utilisateur, 2026-10-16).

Sources de ce classement (2026-10-16) : comparaison des branches Ars Nouveau `1.20` (4.12.7) et `main` (5.13.3) —
fichiers ajoutés, notes de version de chaque release depuis la 5.9 (historique de `changelog.md`), titres des commits
avant. Les deux branches ont divergé le **2024-09-13** : depuis, `main` a reçu 945 commits, `1.20` 27 (maintenance,
dernier le 2025-06-23). La liste n'est pas exhaustive pour les petits correctifs.

**Contexte des décisions** :
- assets officiels Ars autorisés et crédités (2026-10-13) ;
- **Mixins autorisés** (2026-10-16) : ce qui était impossible car il fallait modifier Ars devient faisable (règles : `CLAUDE.md`) ;
- toujours **exclu par l'utilisateur** : ce qui est trop compliqué, notamment la nouvelle dimension (Planarium) ;
- aucune publication en ligne sans validation de l'utilisateur (qui fera valider par l'équipe d'Ars).

Légende difficulté : 🟢 facile · 🟡 moyenne · 🟠 difficile · 🔴 très difficile.

## Ordre de travail (convenu le 2026-10-16)
1. ~~Mise en place des Mixins, puis section A~~ — fait le 2026-10-16.
2. ~~Les 🟢 de B~~ (fait le 2026-10-16) ; **les 🟢 de C** (prochain).
3. Windburst, bateau en archwood, puis les 🟡 restants.
4. Correctifs (D) choisis.
5. Les 🔴 seulement si l'utilisateur le demande. Playtest global par l'utilisateur quand beaucoup de contenu est prêt.

## ✅ Fait (codé, compilé, serveur dédié OK — tests en jeu dans `TESTS.md`)
| Fonctionnalité | Date | Remarque |
|---|---|---|
| Enchanter's Gauntlet | 2026-10-06/13 | modèle et texture officiels ; rendu validé par l'utilisateur |
| Mob Jar : Sniffer, Chat, Golem de neige | 2026-10-13 | |
| Enchanter's Fishing Rod | 2026-10-13 | Lure / Luck of the Sea fonctionnels (pas dans l'officiel) |
| Glyphe Pantomime (forme) | 2026-10-16 | effets visuels = burst de Touch 4.12.7 |
| Glyphe Bubble + entité bulle | 2026-10-16 | passager « mouillé » ajouté ensuite par Mixin (A) |
| **Infrastructure Mixin** | 2026-10-16 | MixinGradle, refmap, `ars_backports.mixins.json` (`HANDOFF.md` §4.6) |
| A — bulle : passager « mouillé » | 2026-10-16 | `BubbleWetEntityMixin` |
| A — Mob Jars d'Ars en pause quand alimentés (7 comportements) | 2026-10-16 | `PassiveJarBehaviorMixin` |
| A — Poulet / Panda : produits dans l'inventaire adjacent | 2026-10-16 | `@Redirect` |
| A — bocal : ouvrir l'inventaire d'une entité-conteneur (sneak) ; inventaire exposé aux entonnoirs ; rendu vers le bas | 2026-10-16 | 3 mixins |
| B — **Dominion dispenser** + C — **bocaux posés au distributeur / cassés au piston** | 2026-10-16 | `ModDispenserBehaviors` + `JarPistonDestroyMixin` |
| B — **Grilles** (archwood, or, sourcestone, sourcestone lisse) | 2026-10-16 | `ItemGrate`, API de blocs 1.20.1 |
| B — **Source Lamp** | 2026-10-16 | logique de l'ampoule en cuivre réécrite ; bascule silencieuse (sons 1.21 absents) |
| B — **Starbuncle Plush** | 2026-10-16 | |
| B — **Tableaux** Starbuncle (animé) et Drygmy au repos | 2026-10-16 | registre 1.20.1 + `PaintingDropMixin` |
| B — **Ars Stencil + 8 motifs de bannière** | 2026-10-16 | la 4.12.7 n'avait aucun motif |
| B — **Panneaux archwood** (classiques + suspendus) | 2026-10-16 | la 4.12.7 n'en avait aucun ; `SignBlockEntityTypeMixin` |

Note : l'effet **Soaked** existe déjà en 4.12.7 (posé par Conjure Water, avec son mixin) — rien à porter
(le classement initial par noms de fichiers l'avait compté à tort comme nouveau).

## A. Compléments de ce qui est fait — ✅ terminé le 2026-10-16 (voir ci-dessus)

## B. Nouveau contenu 1.21
| Élément | Mixin | Difficulté | Notes |
|---|---|---|---|
| Glyphe **Windburst** | non | 🟡 | Wind Charge absente en 1.20.1 → rafale recréée (recul sans dégâts de blocs), sons/particules approchés |
| **Bateau en archwood** | non | 🟡 | entité dédiée (types de bateau non extensibles en 1.20.1) |
| **Warpbuncle** (Starbuncles qui empruntent les portails de téléportation) | oui (pathfinding d'Ars) | 🟡 | à analyser |
| Perk **Unbreakable** (armures d'Ars) | oui (`ItemStack#isDamageableItem`) | 🟡 | + enregistrement du perk et de sa recette |
| **Repository Catalog** | probablement | 🟠 | bloc + GUI + réseau, lié au stockage d'Ars |
| **Alakarkinos** (crabe, charm, chapeau, recettes) | non | 🔴 | mob complet, IA à états, recettes, intégration JEI ; modèle GeckoLib (Ars dépend déjà de GeckoLib) |
| **Spell Styles** (particules et sons personnalisables) → débloque **Prestidigitation** (glyphe, enchantement) et **Decor Blossom** | oui, beaucoup | 🔴 | refonte du cœur du rendu des sorts + GUI + données ; très invasif |
| **Nouveau livre de documentation** | oui | 🔴 | remplace tout le système d'Ars ; faible intérêt |
| **Planarium**, projecteur, Scryer, `dim_boundary` | oui | 🔴 | **exclu par l'utilisateur** (nouvelle dimension) |
| Mob Jar Armadillo, Breeze | — | — | **impossible** : mobs absents de 1.20.1 |

## C. Améliorations de confort sur des éléments existants d'Ars (Mixins)
| Élément | Difficulté | Notes |
|---|---|---|
| Donner des objets aux Starbuncles/Drygmies au clic droit | 🟢 | peut-être sans Mixin (événement d'interaction) |
| Listes noires (tags) : Toss, Intangible, rituels Overgrowth/Breeding/Sanctuary, Containment Jar (items) | 🟢–🟡 | Mixin dans chaque glyphe/rituel pour lire le tag |
| Underfoot cible sous le joueur et ses montures | 🟢 | |
| Rituels météo/heure dans les autres dimensions | 🟢 | |
| Source jars reliés aux tourelles | 🟡 | |
| Rune lancée sur une entité : résout directement sur elle | 🟡 | |
| Glyphes qui fonctionnent entre dimensions (warp, runes, projectiles) | 🟠 | plusieurs glyphes touchés |
| Enchanting Apparatus / Imbuement Chamber **orientables** | 🟠 | nouvelle propriété de bloc sur des blocs d'Ars + modèles/blockstates à remplacer |
| Lectern de stockage : recherche améliorée, bundles, auto-import coupé par redstone, cache | 🟠 | GUI + réseau du stockage |
| Interface du livre de sorts (liste continue, catégories, bouton info, raccourcis) | 🟠 | GUI d'Ars |

## D. Correctifs de la 1.21 absents de la 1.20 (Mixins ciblés, au cas par cas)
Exemples relevés : flèches de sort + Pierce (double lancer, traverse les blocs, crash au rechargement), duplication
arc de sort + Infinity, duplication Interact + seaux, fuites de mémoire (sortie/retour en monde, portails), armure
magique qui draine le mana sans rien réparer, Break qui supprime certains blocs (waystones, portes en fer — outil de
repli pioche), dégâts avec Hex / chute / Bastion, familiers qui ciblent leur maître, dalles doubles de sourcestone,
tomes appliquant leur réduction plusieurs fois, coût des augments mal configuré… 🟡 chacun : à lister précisément
(diff du code 1.20 vs main) avant de choisir. Alternative à proposer à l'utilisateur : soumettre ces correctifs
directement à la branche `1.20` d'Ars (pull requests), ce qui profiterait à tous sans addon.

## Ce qui manque pour avancer
- **Tests en jeu** (seul l'utilisateur peut jouer) : rien de ce qui est fait n'a encore été testé en jeu (`TESTS.md`),
  hormis l'apparence du Gauntlet.
- **Choix des priorités** par l'utilisateur dans A–D.
- Techniquement, rien d'externe : le code (LGPL) et les assets (autorisés) sont publics, et la mise en place des
  Mixins n'utilise que des outils publics (plugin MixinGradle, dépôt `repo.spongepowered.org`).
  Optionnel : JEI/EMI ou Create en dev (`runtimeOnly`) pour tester les intégrations.
