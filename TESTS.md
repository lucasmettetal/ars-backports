# Checklist de tests — Enchanter's Gauntlet

Cocher `[x]` au fur et à mesure, noter la date et le résultat en cas d'échec.
Lancer : `gradlew.bat runClient` (solo) / `gradlew.bat runServer` (serveur dédié, `run-server/eula.txt` = `eula=true`).

Mise en place conseillée (monde créatif, triche activée) :

    /give @p ars_backports:enchanters_gauntlet
    /give @p ars_nouveau:novice_spell_book
    /give @p ars_nouveau:scribes_table
    /give @p ars_nouveau:enchanting_apparatus
    /give @p ars_nouveau:arcane_pedestal 5
    /give @p ars_nouveau:arcane_core

Le test 24 (mana insuffisant) doit se faire en survie (`/gamemode survival`) : en créatif, Ars retire bien le mana
mais autorise le lancement même sans mana suffisant (`SpellResolver.enoughMana`).

## Démarrage
| # | Test | Procédure | Attendu | État |
|---|---|---|---|---|
| 1 | Minecraft démarre | `runClient` | menu principal, « 7 mods loaded » | [x] 2026-10-06 (Claude) |
| 2 | Serveur dédié démarre | `runServer` | `Done (...)!`, aucune erreur | [x] 2026-10-09 (Claude, étape F) |
| 3 | Visible dans JEI | installer JEI 1.20.1 en `runtimeOnly` ou dans le profil de jeu | Gantelet + recette visibles | [ ] |
| 4 | Onglet créatif | onglet Ars Nouveau | Gantelet présent, texture, nom EN/FR | [x] 2026-10-06 (utilisateur) |
| 5 | Recette Enchanting Apparatus | cuir sur le core, 1 diamant + 2 blocs d'or + 2 blocs de Source sur les piédestaux, activer | Gantelet obtenu, 0 Source consommée | [ ] |

## Outil (en survie)
| # | Test | Procédure | Attendu | État |
|---|---|---|---|---|
| 6 | Pierre | miner | rapide (≈ pioche vitesse 8), drop cobblestone | [ ] |
| 7 | Minerais | fer, or, diamant, redstone | drops normaux | [ ] |
| 8 | Tier diamant | obsidienne, minerai de diamant | récoltés | [ ] |
| 9 | Bloc Netherite-tier | bloc d'un mod/datapack dans `forge:needs_netherite_tool` | aucun drop | [ ] (aucun bloc vanilla) |
| 10 | Efficiency | enclume + livre Efficiency V | accepté, minage plus rapide | [ ] |
| 11 | Fortune | Fortune III sur minerai de diamant | drops multipliés | [ ] |
| 12 | Silk Touch | Silk Touch sur pierre / minerai | bloc intact | [ ] |
| — | Refus | enclume : Unbreaking, Mending, Sharpness | refusés | [ ] |
| — | Incassable | miner longtemps | jamais de barre d'usure | [ ] |
| — | Cisailles | feuilles, herbe, toile | drop comme avec des cisailles | [ ] |

## Sorts
| # | Test | Procédure | Attendu | État |
|---|---|---|---|---|
| 13 | Scribes Table | poser le Gantelet sur la table, sneak + clic droit avec le livre de sorts | « Set spell. » | [ ] |
| 14 | Sort sans Form | livre : `Break` seul | accepté | [ ] |
| 15 | Refus Projectile | livre : `Projectile → Break` | message « Invalid spell. Gauntlets accept Effects and Augments only. », sort inchangé | [ ] |
| 16 | Touch ajouté | après inscription de `Break` | tooltip : glyphes `Touch, Break` (Shift : texte) | [ ] |
| 17 | Touch + Break | clic droit sur un bloc | bloc cassé | [ ] |
| 18 | Touch + Harm | clic droit sur un mob | dégâts | [ ] |
| 19 | Touch + Break + Smelt | sur minerai de fer | lingot | [ ] |
| 20 | Touch + Break + Item Pickup | sur un bloc | item dans l'inventaire | [ ] |
| 21 | Amplify | `Break + Amplify` sur obsidienne | cassée | [ ] |
| 22 | AOE | `Break + AOE` | zone 3×3 | [ ] |

## Mana
| # | Test | Procédure | Attendu | État |
|---|---|---|---|---|
| 23 | Mana suffisant | survie, lancer `Break` | mana diminue | [ ] |
| 24 | Mana insuffisant | vider le mana | message Ars « no mana », rien ne se passe | [ ] |
| 25 | Réduction 25 % | `Touch + Break` (brut 15) | 12 consommés | [ ] |
| 26 | Réduction combinée | même sort avec armure/anneau de réduction Ars | réductions additionnées | [ ] |

## Cas particuliers
| # | Test | Procédure | Attendu | État |
|---|---|---|---|---|
| 27 | Aucun sort inscrit | clic droit avec un Gantelet vierge | message « Invalid spell… » | [ ] |
| 28 | Clic droit entité | viser un mob à portée | effet sur le mob | [ ] |
| 29 | Clic droit bloc | viser un bloc | effet sur le bloc | [ ] |
| 30 | Clic droit vide | viser le ciel | sort lancé sans cible (Touch ne fait rien), mana selon Ars | [ ] |
| 31 | Sneak + BlockEntity | viser un coffre : sans sneak → rien ; avec sneak → sort | comportement d'`EnchantersMirror` | [ ] |
| 32 | Reconnexion | quitter / revenir | sort toujours inscrit | [ ] |
| 33 | Changement de dimension | aller au Nether | sort conservé, fonctionne | [ ] |
| 34 | Mort du joueur | mourir, récupérer l'item | sort conservé | [ ] |
| 35 | Multijoueur | 2 clients sur `runServer` | effets visibles par les deux | [ ] |
| 36 | Serveur dédié | tests 13–30 sur `runServer` | identique au solo, aucun crash serveur | [ ] |
