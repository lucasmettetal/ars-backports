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

# Checklist de tests — comportements Mob Jar

Capturer le mob avec un Containment Jar d'Ars (`/give @p ars_nouveau:mob_jar`) ; un coffre adjacent reçoit les objets.

| # | Test | Procédure | Attendu | État |
|---|---|---|---|---|
| J1 | Sniffer : graines | sniffer adulte en bocal, coffre collé, attendre 8 min (non alimenté) | graine de torchflower ou pitcher pod dans le coffre (sinon au-dessus du bocal), son du sniffer | [ ] |
| J2 | Sniffer : pause | alimenter le bocal en redstone | aucune production tant qu'il est alimenté ; la minuterie reprend ensuite | [ ] |
| J3 | Sniffer : bébé | bébé sniffer | rien avant qu'il soit adulte | [ ] |
| J4 | Sniffer : sauvegarde | quitter/revenir au bout de 4 min | la minuterie reprend (ne repart pas de zéro) | [ ] |
| J5 | Chat : signal | chat apprivoisé en bocal, poudre de redstone à côté | signal 15 quand le propriétaire est connecté | [ ] |
| J6 | Chat : déconnexion | propriétaire se déconnecte (multijoueur / `runServer`) | signal 0 juste après la déconnexion | [ ] |
| J7 | Chat : reconnexion | propriétaire se reconnecte | signal 15 | [ ] |
| J8 | Chat sauvage | chat non apprivoisé | signal 0 | [ ] |
| J9 | Chat : cadeau | dormir à moins de 10 blocs du bocal jusqu'au matin | ~70 % de chances d'un cadeau (ficelle, plume, peau de lapin…) dans le coffre | [ ] |
| J10 | Golem de neige | impulsion redstone (bouton) sur le bocal | boule de neige tirée par la face avant du bocal, son du golem | [ ] |
| J11 | Serveur dédié | J1, J5–J7, J10 sur `runServer` | identique, aucun crash | [ ] |

# Checklist de tests — Enchanter's Fishing Rod

Mise en place : `/give @p ars_backports:enchanters_fishing_rod`, un livre de sorts, une Scribes Table, de l'eau.

| # | Test | Procédure | Attendu | État |
|---|---|---|---|---|
| P1 | Onglet / rendu | onglet Ars Nouveau ; regarder la canne en main, au sol, en cadre | modèle 3D officiel, nom EN/FR | [ ] |
| P2 | Canne vierge | clic droit sans sort inscrit | message « Invalid Spell… », la ligne n'est **pas** lancée (comme l'officiel) | [ ] |
| P3 | Inscription | Scribes Table, `Harm` | accepté, tooltip `Touch, Harm` | [ ] |
| P4 | Refus de forme | `Projectile → Harm` | refusé avec le message de la canne | [ ] |
| P5 | Rendu lancé | lancer la ligne | modèle passe en texture « lancée », ligne vanilla visible depuis la canne | [ ] |
| P6 | Pêche normale | lancer dans l'eau, attendre, ramener | poisson / trésor comme une canne vanilla | [ ] |
| P7 | Lure / Luck of the Sea | enchanter (table : enchantabilité 1, ou enclume + livre) | acceptés, et effet réel (touche plus rapide / meilleur butin) ; Unbreaking/Mending refusés | [ ] |
| P8 | Sort sur entité | accrocher un mob, clic droit | le sort se lance sur le mob (dégâts pour Harm), la ligne reste ; re-clic = nouveau lancer | [ ] |
| P9 | Mana | survie, répéter P8 | mana consommé à chaque lancer, message Ars si insuffisant | [ ] |
| P10 | Recette | Enchanting Apparatus : canne vanilla (enchantée) + 2 blocs d'or + 2 blocs de Source | canne d'enchanteur, enchantements conservés | [ ] |
| P11 | Multijoueur | un autre joueur regarde | il voit le flotteur et la ligne ; texture « lancée » seulement pour le porteur | [ ] |
| P12 | Serveur dédié | P2, P5, P6, P8 sur `runServer` | identique, aucun crash | [ ] |

# Checklist de tests — glyphe Pantomime

Mise en place : `/give @p ars_backports:glyph_pantomime`, livre de sorts, Scribes Table ; en créatif tous les glyphes sont connus.

| # | Test | Procédure | Attendu | État |
|---|---|---|---|---|
| G1 | Glyphe visible | onglet des glyphes d'Ars, livre de sorts | « Glyph of Pantomime » / « Glyphe de Pantomime », icône officielle, rangé avec les formes | [ ] |
| G2 | Apprentissage | en survie, clic droit avec l'item du glyphe ; ou recette à la Scribes Table (8 verres) | glyphe débloqué | [ ] |
| G3 | Distance | sort `Pantomime → Break`, regarder à l'horizontale | casse le bloc à ~2 blocs devant les yeux, même en visant plus loin ou le vide | [ ] |
| G4 | Regard vers le bas | même sort, regarder vers le sol (> 45°) | bloc à ~3 blocs dans la direction du regard | [ ] |
| G5 | Amplify / Dampen | `Pantomime + Amplify → Break`, puis `Pantomime + Dampen → Break` | un bloc plus loin / plus près ; Dampen limité à 1 | [ ] |
| G6 | Sensitive | `Pantomime + Sensitive → Break` dans le livre en main | contour du bloc ciblé, à la couleur du sort, avant de lancer | [ ] |
| G7 | Effets visuels | lancer | burst de particules + lumière brève au point ciblé | [ ] |
| G8 | Refus | inscrire `Pantomime → Break` sur le Gantelet ou la canne | refusé (c'est une forme) | [ ] |
| G9 | Serveur dédié | G3, G5, G6 sur `runServer` | identique, aucun crash serveur | [ ] |

# Checklist de tests — glyphe Bubble

Mise en place : `/give @p ars_backports:glyph_bubble`, livre de sorts. Sorts conseillés : `Projectile → Bubble`, `Touch → Bubble`.

| # | Test | Procédure | Attendu | État |
|---|---|---|---|---|
| B1 | Glyphe visible | onglet des glyphes, livre | « Glyph of Bubble » / « Glyphe de Bulle », icône officielle | [ ] |
| B2 | Bulle sur un bloc | `Projectile → Bubble` contre un mur/sol | bulle visible (face caméra) qui monte lentement, disparaît après ~5 s avec animation d'éclatement + son | [ ] |
| B3 | Capture | `Projectile → Bubble` sur un mob | le mob est enfermé et monte ; bulle agrandie selon la taille du mob | [ ] |
| B4 | Éclatement par dégâts | frapper le mob capturé (après 1 tick) | la bulle éclate, le mob subit les dégâts + 5 bonus (+2 par Amplify) | [ ] |
| B5 | Bulle vide | frapper une bulle vide | elle éclate sans son d'attaque | [ ] |
| B6 | Capture au passage | lancer une bulle sur un bloc, faire passer un mob dedans | le mob est capturé | [ ] |
| B7 | Sneak | s'accroupir dans une bulle vide | le joueur n'est pas capturé | [ ] |
| B8 | Sortie | sortir d'une bulle (sneak) | pas recapturé par la même bulle | [ ] |
| B9 | Extend Time / Duration Down | ajouter les augments | durée plus longue / plus courte | [ ] |
| B10 | Mort du passager | tuer le mob capturé (ex. dans le vide / lave sous la bulle) | la bulle éclate | [ ] |
| B11 | Blacklist | viser un boss (Wither) ou un projectile | pas de capture | [ ] |
| B12 | Serveur dédié + multijoueur | B2–B4 sur `runServer`, deux joueurs | bulle visible par les deux, aucun crash | [ ] |

# Checklist de tests — améliorations des Mob Jars d'Ars (Mixins)

| # | Test | Procédure | Attendu | État |
|---|---|---|---|---|
| J12 | Pause redstone | poulet en bocal + levier ; alimenter | plus d'œufs tant que c'est alimenté (idem grenouille, panda, piglin, poisson-globe, mouton, villageois) | [ ] |
| J13 | Œufs en coffre | poulet en bocal, coffre collé au bocal | les œufs vont dans le coffre (sinon au-dessus du bocal) | [ ] |
| J14 | Panda | bébé panda en bocal + coffre | boules de slime (rares) dans le coffre | [ ] |
| J15 | Entité-conteneur | wagonnet coffre en bocal ; sneak + clic droit | son inventaire s'ouvre ; les objets sont conservés après rechargement | [ ] |
| J16 | Entonnoir | entonnoir sous / au-dessus d'un bocal avec wagonnet coffre (ou âne avec coffre) | l'entonnoir vide / remplit l'inventaire de l'entité | [ ] |
| J17 | Bocal vers le bas | poser un bocal orienté vers le bas avec un mob | le mob est rendu à l'intérieur du bocal | [ ] |
| B13 | Bulle mouillée | mob en feu capturé par une bulle | le feu s'éteint, il ne peut pas reprendre feu tant qu'il est dans la bulle | [ ] |

# Checklist de tests — distributeurs et pistons (1.21)

| # | Test | Procédure | Attendu | État |
|---|---|---|---|---|
| D1 | Source Jar au distributeur | distributeur + Source Jar (rempli) + impulsion | le bocal est posé debout devant, avec sa Source | [ ] |
| D2 | Containment Jar au distributeur | idem avec un bocal contenant un mob | posé tourné à l'opposé du distributeur, mob conservé | [ ] |
| D3 | Baguette de domination | distributeur + baguette liée, entité ou bloc devant | même effet qu'un clic droit du joueur avec la baguette | [ ] |
| D4 | Piston | piston qui pousse un Source Jar / Containment Jar | le bocal se casse et tombe en item (contenu conservé selon la loot table d'Ars) | [ ] |

# Checklist de tests — blocs et décorations (section B)

| # | Test | Procédure | Attendu | État |
|---|---|---|---|---|
| K1 | Grilles : rendu | poser les 4 grilles dans les 6 directions | modèle et texture officiels, orientation correcte | [ ] |
| K2 | Grille : items | jeter un item sur une grille horizontale | l'item tombe à travers | [ ] |
| K3 | Grille : liquide | seau d'eau sur une grille, bloc d'air dessous | l'eau passe en dessous ; sur un chaudron vide : il se remplit | [ ] |
| K4 | Grille : seau à travers | eau sous la grille, seau vide sur la grille | l'eau du dessous est récupérée | [ ] |
| K5 | Source Lamp | levier sur la lampe : on / off / on | chaque front montant bascule allumée/éteinte | [ ] |
| K6 | Source Lamp + Light | `Touch + Light + Dampen` sur la lampe | luminosité 14 ; comparateur = luminosité | [ ] |
| K7 | Plush | poser dans les 4 directions | modèle officiel, forme de collision correcte | [ ] |
| K8 | Tableaux : recette | Apparatus : tableau + éclat de Starbuncle (puis de Drygmy) | tableau Starbuncle (animé) / Drygmy au repos (4×4) | [ ] |
| K9 | Tableau cassé | casser un tableau Starbuncle | redonne un tableau Starbuncle (pas un tableau aléatoire) | [ ] |
| K10 | Stencil : métier à tisser | Ars Stencil dans le métier à tisser | 8 motifs d'Ars proposés | [ ] |
| K11 | Bannière | appliquer un motif, poser la bannière | texture du motif ; infobulle « Red Fire School » / « Feu élémentaire (rouge) » | [ ] |
| K12 | Serveur dédié | K3, K5, K8, K10 sur `runServer` | identique, aucun crash | [ ] |
| K13 | Panneaux archwood | crafter, poser au sol / au mur, écrire | texture archwood, écran d'édition, le panneau mural redonne l'item | [ ] |
| K14 | Panneaux suspendus | crafter (chaînes + bûches écorcées), poser au plafond / au mur, écrire | modèle suspendu archwood, écran d'édition des panneaux suspendus | [ ] |

# Checklist de tests — améliorations de confort (section C)

| # | Test | Procédure | Attendu | État |
|---|---|---|---|---|
| C1 | Starbuncle | clic droit avec une pépite d'or sur un Starbuncle sauvage | il prend la pépite (1) et s'apprivoise comme avec une pépite jetée | [ ] |
| C2 | Drygmy | clic droit avec un drop de Wilden sur un Drygmy sauvage | début de l'apprivoisement, item consommé (pas en créatif) | [ ] |
| C3 | Intangible | `Touch + Intangible + AOE` sur un mur avec de la bedrock | la bedrock n'est jamais traversée ; les blocs non récoltables de l'AOE non plus | [ ] |
| C4 | Fertility | ajouter une vache au tag `ars_nouveau:breed_blacklist` (datapack) | le rituel ne fait plus reproduire les vaches | [ ] |
| C5 | Overgrowth (bloc d'os) | bébé dans `ars_nouveau:overgrowth_blacklist` | n'est pas vieilli | [ ] |
| C6 | Sanctuary | `ars_nouveau:deny_spawn_ritual_whitelist` avec un animal ; blacklist avec un monstre | l'animal ne spawn plus naturellement ; le monstre si | [ ] |
| C7 | Underfoot | sur un cheval : `Underfoot → Heal` | soigne le cheval ; sans monture et sans entité dessous : bloc dessous | [ ] |
| C8 | Rituels dans le Nether | Cloudshaper / Sunrise / Moonfall dans le Nether | la météo / l'heure de l'Overworld change | [ ] |
