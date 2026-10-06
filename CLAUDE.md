# Ars Backports

Instructions permanentes pour toute instance de Claude travaillant sur ce dépôt.
L'état d'avancement exact est dans `HANDOFF.md` (à lire en premier, à mettre à jour à chaque étape).
Les idées de backports futurs sont dans `BACKPORT_ROADMAP.md`.

## Objectif
Créer un addon indépendant Forge 1.20.1 qui backporte certaines fonctionnalités récentes
d'Ars Nouveau 1.21.1 (Ars 5.x) vers Ars Nouveau 4.12.7.

Premier objectif : **Enchanter's Gauntlet**. Ne commencer aucun autre backport sans accord explicite.

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

## Décisions Gauntlet
- appliquer réellement le niveau diamant avec `TierSortingRegistry` (blocs Netherite+ non récoltables),
  plutôt que reproduire le comportement 1.21.1 où le refus de tier est inopérant
- vitesse d'outil proche du Gauntlet officiel : 8.0 sur mineable/pickaxe, axe, shovel, hoe ; 1.5 sur sword_efficient ; 1.0 sinon
- item incassable (l'officiel n'a pas de durabilité) sauf découverte contraire
- modèle/rendu vanilla (modèle JSON) autant que possible
- pas de GeckoLib uniquement pour cet item
- texture placeholder tant que l'autorisation de réutiliser l'asset officiel n'est pas obtenue
  (assets Ars Nouveau = All Rights Reserved)
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
runtime d'Ars Nouveau (GeckoLib, Curios) devront être ajoutées en `runtimeOnly` (pas encore fait).

## Dépôts Maven nécessaires au build
`maven.minecraftforge.net`, `maven.blamejared.com` (Ars Nouveau), `libraries.minecraft.net`,
`piston-meta.mojang.com`, `piston-data.mojang.com`, `plugins.gradle.org`, `services.gradle.org`,
`repo.maven.apache.org` (+ `resources.download.minecraft.net` pour runClient).
