# Ars Backports

Instructions permanentes pour toute instance de Claude travaillant sur ce dépôt.
L'état d'avancement exact est dans `HANDOFF.md` (à lire en premier, à mettre à jour à chaque étape).
Les idées de backports futurs sont dans `BACKPORT_ROADMAP.md`.

## Objectif
Créer un addon indépendant Forge 1.20.1 qui backporte certaines fonctionnalités récentes
d'Ars Nouveau 1.21.1 (Ars 5.x) vers Ars Nouveau 4.12.7.

Premier objectif (fait) : **Enchanter's Gauntlet**. Ensuite : backports listés dans `BACKPORT_ROADMAP.md`,
dans l'ordre indiqué dans `HANDOFF.md` §6 (l'utilisateur a demandé de « continuer sur le reste » le 2026-10-13).
Ne jamais commencer un élément ⚠️ ou ❌ de la roadmap sans accord explicite.

## Environnement
- Minecraft 1.20.1
- Forge 47.4.10 (ForgeGradle 6, Gradle 8.8 via le wrapper)
- Java 17 (toolchain Gradle ; le jar final cible Java 17)
- Ars Nouveau 4.12.7 (seule dépendance de mod, obligatoire)
- modid : `ars_backports`
- package : `fr.lucas.arsbackports`
- mappings : `official` (Mojang)
- licence du code : LGPL-3.0 (le code est adapté d'Ars Nouveau, lui-même LGPLv3)

## Principes
- ne jamais modifier directement Ars Nouveau (ni son jar, ni ses sources)
- addon séparé, dépendant d'Ars Nouveau
- compiler après chaque étape importante (`gradlew build`)
- ne pas poursuivre une étape si le build échoue ; lire l'erreur entière, inspecter l'API réelle, corriger la cause
- vérifier les APIs réelles avant utilisation (sources Ars `1.20` = 4.12.7, Forge `1.20.1`)
- ne pas inventer de méthodes/classes ; pas de pseudo-code pour masquer une erreur
- aucun Mixin ou Access Transformer sauf nécessité démontrée (et expliquée à l'utilisateur avant)
- compatibilité serveur dédié obligatoire : aucune classe client chargée côté serveur
- éviter les dépendances inutiles (pas de GeckoLib, Curios, Patchouli, JEI… en dépendance)
- ne jamais supprimer une fonctionnalité pour faire passer la compilation sans prévenir
- architecture simple : pas de couches d'abstraction inutiles

## Documentation (demande explicite de l'utilisateur)
- tout documenter : l'utilisateur travaille sur plusieurs PC, et une nouvelle session doit pouvoir reprendre
  **sans accès aux conversations précédentes** ; `HANDOFF.md` est la source de vérité de l'état du projet
- après chaque fonctionnalité : build, test de démarrage serveur dédié (`HANDOFF.md` §5.3), mise à jour de
  `HANDOFF.md` (état, fichiers, détails techniques, **écarts avec l'officiel**, prochaine action), `TESTS.md`
  (checklist en jeu), `BACKPORT_ROADMAP.md`, `README.md` (liste des fonctionnalités), puis commit + push sur `main`
- ne jamais affirmer dans la doc une chose non vérifiée (ex. tests en jeu non faits = « à faire »)

## Conventions de code
- casters à Touch implicite (Gauntlet, canne) : implémenter `item/TouchCaster` (refus des formes, préfixe Touch sur une copie, tooltip)
- code client uniquement dans `client/` (`@Mod.EventBusSubscriber(value = Dist.CLIENT)`)
- Mob Jar : un `JarBehavior` par mob dans `mobjar/`, enregistré dans `JarBehaviors.register()`
- assets officiels : copier depuis la branche `main` d'Ars, convertir les modèles GeckoLib avec `tools/geo_to_vanilla_model.awk`
- messages : clés `ars_backports.*` (les clés `ars_nouveau.*` ajoutées en 1.21 n'existent pas en 4.12.7) ; en_us + fr_fr
- recettes : format 4.12.7 (`recipes/`, `output`, `reagent` en liste, tags `forge:` au lieu de `c:`)

## Décisions Gauntlet
- appliquer réellement le niveau diamant avec `TierSortingRegistry` (blocs Netherite+ non récoltables),
  plutôt que reproduire le comportement 1.21.1 où le refus de tier est inopérant
- vitesse d'outil proche du Gauntlet officiel : 8.0 sur mineable/pickaxe, axe, shovel, hoe ; 1.5 sur sword_efficient ; 1.0 sinon
- item incassable (l'officiel n'a pas de durabilité) sauf découverte contraire
- modèle/rendu vanilla (modèle JSON) autant que possible
- pas de GeckoLib uniquement pour cet item
- assets officiels Ars autorisés depuis le 2026-10-13 (décision de l'utilisateur, voir HANDOFF « Assets ») :
  les réutiliser en les créditant (README + `credits` de `mods.toml`) ; ils restent All Rights Reserved, hors LGPL ;
  les retirer si l'équipe Ars le demande. Convertir les modèles GeckoLib en modèles JSON vanilla quand c'est possible.
- réduction de mana de 25 % comme l'original (`IManaDiscountEquipment`)
- sort automatiquement préfixé par Touch à l'inscription ; les sorts contenant une forme sont refusés
- reprendre autant que possible le comportement d'`EnchantersMirror` 4.12.7 (même structure, Touch au lieu de Self)
- recette officielle (Enchanting Apparatus) : réactif cuir + 1 diamant + 2 blocs d'or + 2 blocs de Source, `keepNbtOfReagent = true`

## Commandes importantes
Windows :

    gradlew.bat build
    gradlew.bat runClient
    gradlew.bat runServer

Linux/macOS : `./gradlew build` (etc.).

Le jar se trouve dans `build/libs/`. Pour `runClient`/`runServer` en dev, les dépendances
runtime d'Ars Nouveau (Curios, GeckoLib, MixinExtras) sont en `runtimeOnly` (dev uniquement, jamais dans `mods.toml`).

## Dépôts Maven nécessaires au build
`maven.minecraftforge.net`, `maven.blamejared.com` (Ars Nouveau), `libraries.minecraft.net`,
`piston-meta.mojang.com`, `piston-data.mojang.com`, `plugins.gradle.org`, `services.gradle.org`,
`repo.maven.apache.org` (+ `resources.download.minecraft.net` pour runClient),
`maven.theillusivec4.top` (Curios), `dl.cloudsmith.io` (GeckoLib) pour les runs de dev.
