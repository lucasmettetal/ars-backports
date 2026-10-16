# HANDOFF — état exact du projet

Dernière mise à jour : **2026-10-16** (session locale Windows / VS Code — glyphe Pantomime).
Ce fichier décrit des **faits vérifiés** et doit permettre de reprendre le projet **sans aucun accès aux conversations
précédentes**. Le mettre à jour à la fin de chaque étape (voir `CLAUDE.md`).

## 0. Reprendre le projet (lire en premier)
1. `git clone https://github.com/lucasmettetal/ars-backports.git` (ou `git pull` sur un clone existant ; si l'ancien
   remote `ars-nouveau` est configuré : `git remote set-url origin https://github.com/lucasmettetal/ars-backports.git`).
2. Lire `CLAUDE.md` (règles permanentes), ce fichier, `BACKPORT_ROADMAP.md` (quoi faire ensuite), `TESTS.md` (tests en jeu).
3. Prérequis : Git + **JDK 17** (Gradle 8.8 est fourni par le wrapper ; tout le reste est téléchargé par Gradle).
4. `gradlew.bat build` (Windows) / `./gradlew build` → doit afficher `BUILD SUCCESSFUL`, jar dans `build/libs/`.
5. `gradlew.bat runClient` pour jouer en dev ; `gradlew.bat runServer` pour le serveur dédié
   (créer `run-server/eula.txt` contenant `eula=true` au premier lancement ; dossier ignoré par Git).
6. Prochaine action : voir **§6**.

## 1. État par fonctionnalité
| Fonctionnalité | État | Commits | Tests en jeu (`TESTS.md`) |
|---|---|---|---|
| Squelette Forge (étape A) | ✅ compilé | `bd6c441` | — |
| **Enchanter's Gauntlet** (étapes B–F) | ✅ codé, compilé, serveur dédié OK, assets officiels | `ec13911` → `2baa17c` | B (onglet, texture, noms) et rendu du modèle **validés par l'utilisateur** ; 5–36 **à faire** |
| **Mob Jar** : Sniffer, Chat, Golem de neige | ✅ codé, compilé, serveur dédié OK | `5f53d80` | J1–J11 **à faire** |
| **Enchanter's Fishing Rod** | ✅ codé, compilé, serveur dédié OK, assets officiels | `2ee04f1` | P1–P12 **à faire** |
| **Glyphe Pantomime** (forme) | ✅ codé, compilé, serveur dédié OK (glyphe + config enregistrés), icône officielle | `8036644` | G1–G9 **à faire** |
| **Glyphe Bubble** (effet) + entité bulle | ✅ codé, compilé, serveur dédié OK, client démarre, textures officielles | `85016a7` | B1–B12 **à faire** |

| **Mixins** + améliorations des Mob Jars d'Ars (section A) | ✅ codé, compilé, mixins appliqués serveur + client | `93925f4` | J12–J17 **à faire** |

Section A de la roadmap terminée (2026-10-16). Suite : `BACKPORT_ROADMAP.md` « Ordre de travail » et §6 ci-dessous.

Dernier résultat : **2026-10-16 — `gradlew.bat build` → BUILD SUCCESSFUL, 0 avertissement javac** ;
`gradlew.bat runServer` → `Done (18.687s)!`, glyphes `glyph_pantomime` et `glyph_bubble` + entité `ars_backports:bubble`
enregistrés, configs `config/ars_backports/glyph_*.toml` créées, aucune erreur de recette/tag, aucune erreur `ars_backports` ;
`gradlew.bat runClient` → menu principal, aucune erreur de modèle, texture ou renderer `ars_backports`.
**Rendu en jeu non vu** pour : canne (rangée/lancée), icônes des glyphes, bulle (quad + éclatement), contour Sensitive.

## 2. Décisions et contexte donnés par l'utilisateur
- **Cibles figées** : Minecraft 1.20.1, Forge 47.4.10, Java 17, Ars Nouveau 4.12.7 (`4.12.7.264`). Ne pas changer sans accord.
- **Mixins autorisés depuis le 2026-10-16** (et toute ressource nécessaire) : « tu peux prendre les mixins si tu y as accès
  et tout autre ressource nécessaire. Les créateurs sont ouverts aux addons pour leurs mods. » Règles d'usage : `CLAUDE.md`
  (« Principes »). Aucun Mixin n'existe encore dans le code (au 2026-10-16) ; il faudra d'abord ajouter la configuration
  Mixin au build (plugin MixinGradle + `ars_backports.mixins.json`). Pas de nouvelle dépendance de mod sans accord.
- **Publication du mod** (CurseForge/Modrinth/release) : seulement à la fin ; l'utilisateur fera valider par l'équipe
  d'Ars avant ; ne rien publier sans lui. **Push GitHub** : à chaque étape, sans demander.
- **Tests** : l'utilisateur fera un gros playtest plus tard ; avancer au maximum sans attendre (2026-10-16).
- **Roadmap** : la mettre à jour à chaque fin de session (demande du 2026-10-16).
- **Exclus par l'utilisateur** : ce qui est trop compliqué, notamment la nouvelle dimension (Planarium). Classement
  complet faisable / adapté / exclu dans `BACKPORT_ROADMAP.md`.
- L'utilisateur a jugé le Gauntlet conforme et a demandé de **continuer sur le reste** (2026-10-13) sans attendre
  l'ensemble des tests en jeu ; les tests restent à faire (`TESTS.md`).
- **Tout documenter** (demande explicite du 2026-10-13) : chaque décision, écart et procédure doit être écrit ici.
- **Assets officiels Ars** (textures/modèles = « All Rights Reserved », README d'Ars : « Addon authors are encouraged to
  reuse Ars Nouveau assets and to reach out in the discord ») :
  - Demande postée par l'utilisateur sur le Discord d'Ars, salon « General & Development Help » (un premier essai le
    2026-10-09 dans « Addon Discussion & Help » était le mauvais salon). Réponses reçues :
    *Mojobuncle* (Server Booster, 2026-10-09) : « not a spokesperson for ars… you'll be fine… only the very smallest
    chance they will say No » ; *Lyrellion* (« Supreme Addon Developer », 2026-10-12) : « Generally speaking, the use
    of Ars assets specifically for Ars addons is fine. »
  - Pas de « oui » formel de l'auteur/artiste, mais **l'utilisateur a décidé le 2026-10-13 d'utiliser les assets
    officiels**, crédités (`README.md`, champ `credits` de `mods.toml`), hors licence LGPL, à retirer si l'équipe Ars le demande.
  - Règles du Discord d'Ars : anglais uniquement ; pas de DM ni de mention sans permission ; pas de cross-posting.
- Dépôt GitHub renommé de `ars-nouveau` en **`ars-backports`** le 2026-10-09 (l'ancienne URL redirige).

## 3. Contenu du dépôt
| Fichier | Rôle |
|---|---|
| `settings.gradle` | dépôts de plugins (Gradle Plugin Portal, Sponge, MinecraftForge), plugin foojay, `rootProject.name = 'ars_backports'` |
| `src/main/resources/ars_backports.mixins.json`, `.../mixin/**` | configuration et classes Mixin (§4.6) |
| `.../mobjar/JarEntityItemHandler.java` | inventaire exposé par un bocal (§4.6) |
| `.../registry/ModDispenserBehaviors.java` | comportements de distributeur 1.21 : bocaux, baguette de domination (§4.6) |
| `build.gradle` | ForgeGradle `[6.0,6.2)` (résolu 6.0.54) + MixinGradle 0.7.38 (§4.6), toolchain Java 17, mappings officiels, runs client/server (+ propriétés `mixin.env.remapRefMap`/`refMapRemappingFile`, nécessaires pour que les mixins de Curios/Ars fonctionnent en dev), dépôts BlameJared / Maven Central / Curios / GeckoLib filtrés par groupe, Ars en `implementation`, Curios + GeckoLib + MixinExtras en `runtimeOnly` (dépendances d'Ars, dev uniquement), processeur Mixin, `MixinConfigs` dans le manifeste, `-Xlint:deprecation` |
| `gradle.properties` | MC 1.20.1, Forge 47.4.10 (`[47.4,)`), Ars `4.12.7.264` (`[4.12.7,)`), dev : Curios `5.14.1+1.20.1`, GeckoLib `4.8.4`, MixinExtras `0.4.1` ; processeur Mixin `0.8.5` ; modid `ars_backports`, `LGPL-3.0-only`, version 0.1.0 |
| `src/main/java/fr/lucas/arsbackports/ArsBackports.java` | `@Mod` ; enregistre `ModItems.ITEMS` ; `FMLCommonSetupEvent` → `enqueueWork(JarBehaviors::register)` ; ajoute Gauntlet et canne à l'onglet Ars (`CreativeTabRegistry.BLOCKS`, id `ars_nouveau:general`) |
| `.../registry/ModItems.java` | `DeferredRegister<Item>` : `enchanters_gauntlet`, `enchanters_fishing_rod` |
| `.../item/TouchCaster.java` | interface commune (étend `ICasterTool`) des lanceurs « Touch implicite » : refus des sorts avec forme, préfixe `MethodTouch` sur une **copie** du sort, message d'erreur (`invalidSpellKey()`), helpers de tooltip (`appendSpellTooltip`, `spellTooltipImage`) |
| `.../item/EnchantersGauntlet.java` | Gauntlet : outil + `TouchCaster` + `IManaDiscountEquipment` (§4.1) |
| `.../item/EnchantersFishingRod.java` | canne : `TouchCaster`, pêche vanilla + sort sur l'entité accrochée (§4.3) |
| `.../entity/EnchantedHook.java` | hameçon de la canne (sous-classe de `FishingHook`, §4.3) |
| `.../client/ClientSetup.java` | **client uniquement** (`@EventBusSubscriber(value = Dist.CLIENT)`) : propriété d'item `cast` de la canne |
| `.../mobjar/JarBehaviors.java`, `CatBehavior.java`, `SnifferBehavior.java`, `SnowGolemBehavior.java` | comportements Mob Jar (§4.2) |
| `.../glyph/Glyphs.java` | enregistrement des glyphes (`GlyphRegistry.registerSpell`), appelé **dans le constructeur du mod** (§4.4) |
| `.../glyph/MethodPantomime.java` | forme Pantomime (§4.4) |
| `.../client/PantomimeOutlineRenderer.java` | **client uniquement** : contour du bloc visé avec Pantomime + Sensitive (§4.4) |
| `.../glyph/EffectBubble.java` | effet Bubble (§4.5) |
| `.../entity/BubbleEntity.java` | entité bulle + ses gestionnaires d'événements Forge (§4.5) |
| `.../registry/ModEntities.java` | `DeferredRegister<EntityType<?>>` : `ars_backports:bubble` (0.5 × 0.5, portée de suivi 10) |
| `.../client/BubbleRenderer.java` | **client uniquement** : quad face caméra + 5 images d'éclatement ; enregistré dans `ClientSetup.onRegisterRenderers` |
| `.../textures/entity/bubble.png`, `bubble_pop1..5.png`, `.../textures/item/glyph_bubble.png`, `.../models/item/glyph_bubble.json` | textures officielles (bulle, éclatement, icône) |
| `src/main/resources/data/ars_backports/recipes/glyph_bubble.json` | 3 × `forge:feathers`, seau d'eau, `#minecraft:boats`, `ars_nouveau:water_essence` (traduction des tags `c:` officiels) |
| `src/main/resources/data/ars_backports/tags/entity_types/bubble_blacklist.json` | entités jamais capturées (copie de l'officiel : `#forge:bosses`, la bulle, projectiles d'Ars et vanilla, Create optionnel) |
| `.../models/item/glyph_pantomime.json`, `.../textures/item/glyph_pantomime.png` | icône du glyphe (texture officielle `textures/item/pantomime.png` de la branche `main`) |
| `src/main/resources/data/ars_backports/recipes/glyph_pantomime.json` | recette de glyphe (Scribes Table) : 8 × `forge:glass`, 27 exp |
| `src/main/resources/assets/ars_backports/lang/en_us.json`, `fr_fr.json` | noms + messages d'erreur (`ars_backports.gauntlet.invalid`, `ars_backports.fishing_rod.invalid`) ; textes anglais officiels 1.21, français calqué sur les traductions d'Ars (« … d'enchanteur ») |
| `.../models/item/enchanters_gauntlet.json` | modèle 3D vanilla converti du GeckoLib officiel (§5.1) |
| `.../models/item/enchanters_fishing_rod.json`, `enchanters_fishing_rod_cast.json` | modèle de la canne converti (§5.1) + variante « lancée » (même géométrie, autre texture) via override `cast` |
| `.../textures/item/enchanters_gauntlet.png`, `enchanters_fishing_rod.png`, `enchanters_fishing_rod_cast.png` | textures **officielles** Ars 32×32 (`enchanters_rod_stowed.png` / `enchanters_rod_cast.png` renommées) |
| `src/main/resources/data/ars_backports/recipes/enchanters_gauntlet.json`, `enchanters_fishing_rod.json` | recettes Enchanting Apparatus (format 4.12.7) |
| `src/main/resources/data/forge/tags/items/tools/fishing_rods.json` | ajoute la canne au tag `forge:tools/fishing_rods` (équivalent du `c:tools/fishing_rod` officiel) |
| `src/main/resources/META-INF/mods.toml`, `pack.mcmeta` | dépendances forge/minecraft/ars_nouveau (`ordering="AFTER"`), champ `credits` ; pack_format 15 |
| `tools/geo_to_vanilla_model.awk` | convertisseur GeckoLib → modèle JSON vanilla (§5.1) |
| `README.md` | présentation publique (anglais), crédits et licences |
| `TESTS.md` | checklists de tests en jeu (Gauntlet 1–36, Mob Jar J1–J11, canne P1–P12) |
| `CLAUDE.md`, `HANDOFF.md`, `BACKPORT_ROADMAP.md` | mémoire du projet |

## 4. Détails techniques par fonctionnalité
Toutes les API ont été lues dans les sources Ars (branche `1.20` @ `2c74064` = 4.12.7, branche `main` = 1.21.1)
et vérifiées par `javap` dans les jars résolus (§5.2). Les **écarts** avec l'officiel 1.21.1 sont signalés.

### 4.1 Enchanter's Gauntlet
- **Outil** : vitesse 8.0 (`BlockTags.MINEABLE_WITH_PICKAXE/AXE/SHOVEL/HOE`), 1.5 (`BlockTags.SWORD_EFFICIENT`), 1.0 sinon ;
  drops si tag mineable **et** `TierSortingRegistry.isCorrectTierForDrops(Tiers.DIAMOND, state)` ; `canPerformAction` :
  `PICKAXE/AXE/SHOVEL/HOE/SWORD/SHEARS_DIG` ; pas de durabilité (incassable) ; `isEnchantable` true (obligatoire : le
  vanilla exige `canBeDepleted()`) ; `getEnchantmentValue(ItemStack)` 15 (hook Forge ; la version sans paramètre est
  dépréciée) ; `canApplyAtEnchantingTable` → catégorie `DIGGER` (Efficiency, Fortune, Silk Touch ; enclume comprise,
  vérifié en bytecode : `Enchantment.canEnchant` → `canApplyAtEnchantingTable`).
  **Écart volontaire** : le tier diamant est réellement appliqué (en 1.21.1 la règle `deniesDrops` est placée après les
  `minesAndDrops` et ne s'applique jamais) ; sans effet en vanilla (aucun bloc vanilla n'exige la Netherite).
- **Sorts** (calqué sur `EnchantersMirror` 4.12.7, Touch au lieu de Self) : `use()` →
  `caster.castSpell(level, player, hand, Component.translatable(invalidSpellKey()), caster.getSpell())`.
  `ISpellCaster.castSpell` 4.12.7 gère tout (côté client `pass`, sort invalide → message, raytrace `0.5 + getBlockReach()`,
  Scribes Table ignorée, BlockEntity ignoré sans sneak sauf tag `IGNORE_TILE`, entité → `onCastOnEntity`, bloc →
  `onCastOnBlock`, sinon `onCast`). Inscription via `ICasterTool.onScribe` (Scribes Table, sneak + clic droit avec livre
  ou parchemin). Stockage `SpellCaster` NBT (`ars_nouveau:caster`), 1 slot. Le sort du livre n'est pas modifié
  (le Miroir 4.12.7, lui, mute le sort reçu).
- **Mana** : `getManaDiscount(stack, spell) = (int) (spell.getCost() * 0.25)` ; `ManaUtil.getPlayerDiscounts` additionne
  curios + armure + objet lanceur. Touch + Break : 15 → 12. En créatif, Ars autorise le lancement sans mana suffisant
  mais retire quand même le mana (`SpellResolver.enoughMana`/`expendMana`).
- **Tooltip** : glyphes (`SpellTooltip`) si `Config.GLYPH_TOOLTIPS` et sans Shift, texte (`getInformation`) avec Shift ;
  `Screen.hasShiftDown()` n'est appelé que dans des méthodes exécutées côté client (même schéma qu'Ars). Barre de mana
  fournie par `ICasterTool` (`IDisplayMana`).
- **Recette** : reagent `forge:leather` ; pedestals `forge:gems/diamond`, 2× `forge:storage_blocks/gold`,
  2× `forge:storage_blocks/source` ; `sourceCost` 0 ; `keepNbtOfReagent` true.

### 4.2 Mob Jar (Sniffer, Chat, Golem de neige)
- Enregistrés au `FMLCommonSetupEvent` via `JarBehaviorRegistry.register(EntityType, JarBehavior)` (API publique 4.12.7 ;
  Ars 4.12.7 n'enregistre rien pour ces trois types). Helpers 1.21 absents de 4.12.7 recréés dans `JarBehaviors` :
  `isPowered` (`MobJar.POWERED`) et `insertOrCreateItem` (`InventoryManager.fromTile(tile).insertStack`, reste lâché
  au-dessus du bocal ; `InventoryManager.fromTile` est identique dans les deux versions).
- **Golem de neige** : copie fidèle (boule de neige par la face `MobJar.FACING` sur front montant ; `onRedstonePower`
  est appelé par `MobJar.neighborChanged` 4.12.7).
- **Sniffer** : loot `sniffer_digging` toutes les 8 min pour un adulte, bocal non alimenté, insertion dans les inventaires
  adjacents. **Écarts** : `Sniffer.DATA_DROP_SEED_AT_TICK` est **privé** en 1.20.1 (accès = AT ou réflexion, refusés)
  → compteur stocké dans `tile.getExtraDataTag()` (clé `ars_backports:sniffer_ticks`), sauvegardé avec le bocal, donc
  il survit au rechargement (le 1.21 repart de zéro) ; événement d'entité 63 (animation 1.21) non envoyé ; loot via
  `server.getLootData().getLootTable(...)` (API 1.20.1).
- **Chat** : signal redstone 15 si le propriétaire est connecté (`getSignalPower`, lu par `MobJar.getSignal` 4.12.7),
  positions suivies dans un `LevelPosMap` (API 4.12.7) ; cadeau du matin (`SleepFinishedTimeEvent`, 70 %, propriétaire
  à ≤ 10 blocs, `getSleepTimer() >= 100`). **Écarts** : Ars 1.21 utilise son propre `PlayerPostLogOutEvent` (déclenché
  après le retrait du joueur) ; en Forge 1.20.1, `PlayerLoggedOutEvent` est déclenché **avant** le retrait
  (`cat.getOwner()` trouverait encore le joueur) → mise à jour des voisins reportée au tick suivant
  (`server.tell(new TickTask(server.getTickCount(), ...))`) ; **bug officiel corrigé** : dans la boucle des cadeaux,
  `return` (qui arrêtait tous les bocaux suivants dès un chat sans cadeau) remplacé par `continue`.

### 4.3 Enchanter's Fishing Rod
- Comportement officiel : canne à pêche normale ; si une entité est accrochée, utiliser la canne **lance le sort** sur
  elle (au lieu de remonter la ligne), autant de fois que voulu tant que la ligne tient. Un seul sort, sans forme,
  Touch préfixé (`TouchCaster`). Sans sort valide, la canne **ne se lance pas** (message `ars_backports.fishing_rod.invalid`),
  comme en 1.21.1. Pas de durabilité, pas de réduction de mana.
- `use()` : copie du 1.21.1 (lui-même `FishingRodItem#use`) avec les appels 1.20.1 : `EnchantmentHelper.getFishingSpeedBonus`
  / `getFishingLuckBonus`, `hurtAndBreak(i, player, p -> p.broadcastBreakEvent(hand))` (sans effet : pas de durabilité).
  `SpellContext(level, spell, player, new PlayerCaster(player), itemstack)` créé au lancer après
  `caster.modifySpellBeforeCasting(...)`.
- `EnchantedHook extends FishingHook`, construit avec le constructeur **public vanilla** `FishingHook(Player, Level, luck, lure)`.
  **Écarts** : le 1.21 enregistre un type d'entité `enchanters_hook` (rendu par `FishingHookRenderer`) mais appelle
  `super(entityType, level)` et **perd la chance et le leurre** ; ici le type reste `minecraft:fishing_bobber` → Lure et
  Luck of the Sea fonctionnent, les clients voient un flotteur vanilla (ligne et rendu vanilla), aucun type d'entité ni
  renderer à enregistrer. Le sort n'existe que côté serveur (comme en 1.21). `castSpell()` réutilise le même
  `SpellContext` à chaque lancer, comme l'officiel (`SpellResolver.resolveAllEffects` remet le compteur et l'annulation à zéro).
- `canPerformAction` → `ToolActions.DEFAULT_FISHING_ROD_ACTIONS` (nécessaire : Forge 1.20.1 teste `FISHING_ROD_CAST`
  dans `FishingHook.shouldStopFishing` et dans le rendu de la ligne).
- Enchantements : `isEnchantable` true, `getEnchantmentValue(ItemStack)` 1, `canApplyAtEnchantingTable` → catégorie
  `FISHING_ROD` (Lure, Luck of the Sea ; le 1.21 n'est que dans `enchantable/fishing`).
- Rendu : modèle converti (3 cubes dont un plan) + variante `cast` ; propriété d'item `minecraft:cast`
  (`ResourceLocation.withDefaultNamespace`, présent dans Forge ≥ 47.4 ; le constructeur `ResourceLocation(String)` est
  déprécié « for removal ») enregistrée dans `ClientSetup` avec le prédicat de la canne vanilla. **Écart** : le
  renderer 1.21 change la texture selon que *le joueur local* pêche, quel que soit le porteur ; ici, selon le porteur.
- Recette : reagent `minecraft:fishing_rod` ; pedestals 2× `forge:storage_blocks/gold`, 2× `forge:storage_blocks/source` ;
  `sourceCost` 0 ; `keepNbtOfReagent` true (les enchantements de la canne vanilla sont conservés).

### 4.4 Glyphe Pantomime (forme)
- **Enregistrement d'un glyphe par un addon en 4.12.7** (vérifié dans l'addon d'exemple officiel, branche `1.20.x`,
  et dans `GlyphRegistry` 4.12.7) : `GlyphRegistry.registerSpell(part)` **pendant la construction du mod** (il crée la
  config serveur du glyphe dans le conteneur du mod actif, et Ars crée l'item du glyphe — classe `Glyph`, nom de registre
  = celui du glyphe — au moment de l'enregistrement des items). L'addon fournit : `models/item/<id>.json`, la texture,
  les clés `<modid>.glyph_name.<id>` et `<modid>.glyph_desc.<id>` (le nom de l'item est « Glyph of » + `glyph_name`),
  et une recette `ars_nouveau:glyph` (format 4.12.7 : `"inputItems": [{"item": {...}}]`, `"output": "<id>"`, `exp`, `count`).
  Id : `ars_backports:glyph_pantomime` (l'officiel est `ars_nouveau:glyph_pantomime`).
- Logique copiée du 1.21.1 : la cible est le bloc à 2 blocs (regard horizontal, `xRot < 45`) ou 3 blocs (regard vers le
  bas) devant les yeux, −1 avec Dampen (limite 1), +1 par Amplify ; l'effet est résolu là quel que soit ce qui a été
  cliqué (bloc, entité, vide). Coût 5 ; augments Amplify, Dampen, Sensitive ; description identique.
- **Écarts** : en 1.21.1, l'effet visuel et le son viennent du système de « particle timelines » (absent de 4.12.7 ;
  timeline par défaut = burst) → remplacés par ce que fait le Touch de 4.12.7 : `PacketANEffect` BURST à la couleur du
  sort + `PacketAddFadingLight`. Les descriptions par augment (`addAugmentDescriptions`, clés `augment_desc`) n'existent
  pas en 4.12.7 → non reprises (le texte du glyphe les décrit déjà).
- **Sensitive** : contour du bloc visé dessiné côté client (`RenderLevelStageEvent`, étape `AFTER_TRIPWIRE_BLOCKS`,
  priorité `LOWEST`, comme le 1.21) quand le caster en main principale porte un sort Pantomime + Sensitive. Couleur :
  `spell.color` (le 1.21 prend la couleur de la timeline). Lignes via `renderBuffers().bufferSource()` +
  `endBatch(RenderType.lines())` (le 1.21 utilise l'`outlineBufferSource`).
- Les casters à Touch implicite (Gauntlet, canne) refusent ce glyphe : c'est une forme.

### 4.5 Glyphe Bubble (effet) et entité bulle
- **Effet** (copie du 1.21.1) : sur un bloc → bulle au point d'impact ; sur une entité → bulle + capture immédiate.
  Durée `100 + durée × extend_time` ticks, dégâts à l'éclatement `damage + amp × amp_value + bonus de dégâts`
  (config : `damage` 5, `amp_value` 2, `extend_time` 3 — défauts officiels). Coût 20, école Eau, augments Extend Time,
  Amplify, Dampen, Duration Down. Config créée avec `addExtendTimeTicksConfig` (même clé `extend_time` que
  l'`addExtendTimeConfig` officiel, déprécié « for removal » en 4.12.7 ; la valeur est utilisée en ticks).
- **Entité** (`Projectile`, copie du 1.21.1) : monte de 0.1 bloc/tick sans collision, capture la première entité
  touchée (sauf tag `ars_backports:bubble_blacklist`, joueurs endormis, entités déjà sorties de cette bulle), éclate à la
  fin de sa durée, quand on la frappe vide (ou avec un item), ou quand son passager subit des dégâts (bonus de dégâts
  si la bulle a plus d'1 tick et que le passager n'est pas son lanceur ; + attribut `PerkAttributes.SPELL_DAMAGE_BONUS`
  du lanceur). Frapper une bulle qui porte une créature frappe la créature. Disparaît 5 ticks après l'éclatement.
  Événements Forge (`@Mod.EventBusSubscriber`) : `AttackEntityEvent` ; `LivingDamageEvent` (équivalent Forge 1.20.1 du
  `LivingDamageEvent.Pre` NeoForge : dégâts après réductions). Sauvegarde : `maxAge`, `damage`, `age`.
- **Écarts** (le 1.21.1 utilise des Mixins / événements internes d'Ars) :
  - capture qui ignore le délai de remontée (`EntityAccessor` Mixin sur `boardingCooldown`) → `startRiding(this, true)`
    qui saute seulement `Entity#canRide` (sneak + délai ; vérifié en bytecode), le test du sneak étant refait à la main ;
    comme `force` saute aussi `canAddPassenger`, la boucle s'arrête à la première capture (1 seul passager) ;
  - éclatement silencieux à la mort du passager (`EntityPreRemovalEvent` d'Ars) → surcharge de `removePassenger`
    (`Entity#setRemoved` fixe la raison du retrait puis appelle `stopRiding`, vérifié en bytecode) ;
  - passager « mouillé » (`EntityMixin` 1.21) → **repris le 2026-10-16** par `mixin/BubbleWetEntityMixin` (§4.6) ;
    l'effet Soaked existe déjà en 4.12.7 (`ModPotions.SOAKED_EFFECT`, posé par Conjure Water, avec son propre mixin) ;
  - position du passager : `getPassengerRidingPosition` (1.21) → `positionRider` (1.20.1) : item 0.25 au-dessus, sinon à l'origine ;
  - rendu : le shader `worldEntityIcon` d'Ars 1.21 n'existe pas en 4.12.7 → `RenderType.entityTranslucent` (vanilla),
    même quad, même échelle, mêmes images ;
  - `ParticleUtil.inRange` (classe client d'Ars, importe `ClientLevel`) n'est pas utilisé côté commun → tirage aléatoire équivalent.
- Recette : tags `c:feathers` / `c:` → `forge:feathers` ; `ars_nouveau:water_essence` existe en 4.12.7.

### 4.6 Mixins (infrastructure) et améliorations des Mob Jars d'Ars (section A de la roadmap, 2026-10-16)
- **Infrastructure** : plugin `org.spongepowered.mixin` 0.7.38 (dépôt `repo.spongepowered.org`, déclaré dans
  `settings.gradle` et `build.gradle`), bloc `mixin { add sourceSets.main, "ars_backports.refmap.json"; config
  "ars_backports.mixins.json" }`, `annotationProcessor org.spongepowered:mixin:0.8.5:processor` (version embarquée par
  Forge 1.20.1), `MixinConfigs` dans le manifeste du jar. Config : `src/main/resources/ars_backports.mixins.json`
  (`compatibilityLevel` JAVA_17 comme Ars ; `defaultRequire` 1 = une injection introuvable fait planter au chargement,
  donc toute erreur se voit au démarrage). Le refmap généré mappe vers les noms SRG (production) ; en dev l'avertissement
  « Reference map … could not be read » est normal. Pas de MixinExtras (non garanti à l'exécution) : `@Inject`/`@Redirect` simples.
  Vérification : dans `run*/logs/debug.log`, chaque mixin doit apparaître en « Mixing <mixin> from ars_backports.mixins.json into <cible> ».
- **Mixins existants** (tous vérifiés appliqués au démarrage serveur/client, sans erreur d'injection) :
  | Mixin | Cible | Reproduit (1.21.1) |
  |---|---|---|
  | `BubbleWetEntityMixin` | `Entity#isInWaterOrRain` (RETURN), `#setRemainingFireTicks` (HEAD) | entité vivante sur une bulle = mouillée, ne prend pas feu (`EntityMixin`) ; coexiste avec le mixin Soaked d'Ars 4.12.7 |
  | `jar.PassiveJarBehaviorMixin` | `tick` de `ChickenBehavior`, `FrogBehavior`, `PandaBehavior`, `PiglinBehavior`, `PufferfishBehavior`, `SheepBehavior`, `VillagerBehavior` (HEAD) | « disable mob jar behaviors with redstone power » (commit `a97a3389`) : pause tant que le bocal est alimenté ; ces ticks ne font rien côté client en 4.12.7 (vérifié), annuler tout le tick est équivalent |
  | `jar.JarInventoryOutputMixin` | `ChickenBehavior#tick`, `@Redirect` de `spawnAtLocation` | œufs insérés dans les inventaires adjacents (`insertOrCreateItem`, commit #1644) |
  | `jar.PandaInventoryOutputMixin` | `PandaBehavior#afterSneeze`, `@Redirect` de `Level#addFreshEntity` | boule de slime insérée dans les inventaires adjacents (#1644) |
  | `jar.MobJarContainerMixin` | `MobJar#use`, avant `dispatchBehavior` | sneak + clic droit sur un bocal contenant une entité-conteneur (wagonnet/bateau coffre) ouvre son inventaire (#1644) |
  | `jar.MobJarItemHandlerMixin` | `MobJarTile` (ajout de `getCapability`) | le bocal expose l'inventaire de l'entité aux entonnoirs/tuyaux (#1644 : capability NeoForge + `SavingItemHandler`) ; ici `mobjar/JarEntityItemHandler`, qui relit l'entité à chaque appel (pas de LazyOptional périmé) et marque le bocal modifié |
  | `client.MobJarRendererMixin` | `MobJarRenderer#render`, avant `Direction#getRotation` | correctif de rendu des bocaux orientés vers le bas (#1644) |
- `jar.JarPistonDestroyMixin` (`SourceJar`, `MobJar` : ajout de `IForgeBlock#getPistonPushReaction` → `DESTROY`) :
  un piston casse les bocaux de Source (et Creative, sous-classe) et les Containment Jars (1.21 #1661 ; aucune classe de
  leur hiérarchie ne surcharge ce hook en 4.12.7, vérifié par `javap`).
- **Distributeurs** (`registry/ModDispenserBehaviors`, sans Mixin, enregistrés au `FMLCommonSetupEvent` via
  `enqueueWork`) : copie du `DispenserBehaviorRegistry` 1.21 — pose des Source Jars / Creative Source Jars (debout) et des
  Containment Jars (tournés à l'opposé du distributeur) avec `UnbiasedDirectionalPlaceContext`, et utilisation de la
  baguette de domination (#1783) sur l'entité vivante devant le distributeur, sinon sur le bloc (via `ANFakePlayer`).
  Seul changement : accesseurs 1.20.1 de `BlockSource` (`getLevel/getPos/getBlockState`).
- Écarts : le Sniffer garde son compteur dans le bocal (le 1.21 utilise un Access Transformer sur
  `Sniffer.DATA_DROP_SEED_AT_TICK` ; notre version survit au rechargement, laissée telle quelle).

## 5. Méthodes réutilisables
### 5.1 Conversion des modèles GeckoLib → JSON vanilla
Les items officiels 1.21 utilisent GeckoLib (`models/item/<item>.json` avec `"parent": "builtin/entity"` +
`geo/<nom>.geo.json` + texture). Pour éviter GeckoLib, `tools/geo_to_vanilla_model.awk` convertit les cubes
(1 os, sans rotation, box UV) en éléments vanilla ; puis on recopie le bloc `display` du modèle officiel.
Exemple (Gauntlet) :

    awk -v CUBES="-3,0,-4,6,6,6,0,0;-3,0,2,3,1,2,10,12;-3,1,2,1,4,2,0,15;-3,5,2,3,1,2,0,12" \
        -v TEX="ars_backports:item/enchanters_gauntlet" -v TW=32 -f tools/geo_to_vanilla_model.awk

Le script régénère à l'identique les modèles du Gauntlet et de la canne (vérifié). Règles détaillées en tête du script.
Le rendu du Gauntlet converti a été **validé visuellement** par l'utilisateur.

### 5.2 Vérifier une API sans IDE
Jars utilisés par Gradle (après un premier build) :
- Forge/Minecraft mappé : `~/.gradle/caches/forge_gradle/minecraft_user_repo/net/minecraftforge/forge/1.20.1-47.4.10_mapped_official_1.20.1/forge-1.20.1-47.4.10_mapped_official_1.20.1.jar`
- Ars mappé : `~/.gradle/caches/forge_gradle/deobf_dependencies/com/hollingsworth/ars_nouveau/ars_nouveau-1.20.1/4.12.7.264_mapped_official_1.20.1/…jar`

`javap -cp <jar> <classe>` (signatures), `javap -c -p` (bytecode). Sources Ars : §11 (clone `--filter=blob:none`, puis
`git show HEAD:<chemin>`).

### 5.3 Test de démarrage serveur dédié (fait après chaque fonctionnalité)
`gradlew.bat runServer`, attendre `Done (…)!` dans la console, chercher dans `run-server/logs/debug.log` :
`Parsing error loading recipe` et les lignes `ars_backports` contenant `error`/`exception` (attendu : aucune), puis arrêter
le serveur (`stop` dans la console, ou tuer le processus Java `forgeserveruserdev`). Messages normaux au premier lancement :
configs « not correct. Correcting », `Failed to load properties from file: server.properties`, exceptions netty
`Reflective setAccessible` (bruit de debug).

## 6. Prochaines actions exactes
Suivre « Ordre de travail » de `BACKPORT_ROADMAP.md` sans attendre de tests (playtest global plus tard) :
1. **Section B, éléments 🟢** : Dominion dispenser, Archwood hanging signs, grilles (`ItemGrate`), Source Lamp,
   Ars Stencil, Starbuncle Plush, tableaux Drygmy/Starbuncle. Pour chacun : lire le code `main`, porter, assets officiels.
2. **Section C, éléments 🟢** (Mixins/événements).
3. Puis Windburst, bateau en archwood, autres 🟡 ; correctifs (D).
4. Avant la publication (seulement avec l'utilisateur) : choisir une version (`mod_version`, actuellement 0.1.0), relire `README.md`.

## 7. Environnement
- Java **Temurin 17.0.20.1+1**, Gradle 8.8 (wrapper), ForgeGradle 6.0.54, Forge 1.20.1-47.4.10, mappings `official`,
  Ars `com.hollingsworth.ars_nouveau:ars_nouveau-1.20.1:4.12.7.264` (seule build 4.12.7 publiée sur `maven.blamejared.com`).
- Avertissement Gradle permanent sans impact : « Deprecated Gradle features … incompatible with Gradle 9.0 » (ForgeGradle).
- **PC « lucas » (Windows 10 LTSC)** : ni Git ni Java ni winget à l'origine → installés en **portable** dans
  `C:\Users\lucas\tools\` (PortableGit 2.56.0.2, Temurin 17.0.20.1) ; `Path` et `JAVA_HOME` **utilisateur** mis à jour.
  Identité Git du dépôt (config locale) : `lucasmettetal <lucas8237014@gmail.com>`. Le push utilise Git Credential Manager.
- Autre PC : n'importe quel JDK 17 + Git.

## 8. Blocages / points à vérifier
1. **Tests en jeu** (utilisateur) : `TESTS.md` — Gauntlet 5–36 (priorité 13–17, 25, 27, 31, 36), Mob Jar J1–J11,
   canne P1–P12, Pantomime G1–G9, Bubble B1–B12. Rendus jamais vus en jeu : canne (rangée/lancée), icônes des glyphes,
   bulle, contour Sensitive.
2. JEI absent de l'environnement de dev (test 3) : l'ajouter en `runtimeOnly` si besoin (demander avant).
3. Erreurs connues : aucune.

## 9. Annexe — analyse du Gauntlet officiel (vérifiée dans le code source)
Sources : Ars Nouveau branche `1.20` (= 4.12.7, `version = '4.12.7'`, commit `2c74064b`) et branche `main`
(5.13.3, MC 1.21.1, NeoForge 21.1.228, commit `fd8c9520`). Gauntlet ajouté par le commit `db497ab27`
(« Caster gauntlet and fishing rod », mars 2025, ≈ Ars 5.7) ; enchantabilité corrigée par `de83f9018` (avril 2025).

Fichiers 1.21.1 : `common/items/EnchantersGauntlet.java`, `client/renderer/item/GauntletRenderer.java`,
`models/item/enchanters_gauntlet.json` (`builtin/entity`), `geo/enchanters_gauntlet.geo.json` (1 os « gauntlet », 4 cubes),
`textures/item/enchanters_gauntlet.png` (32×32), recette `recipe/enchanters_gauntlet.json`, tags, lang, `Documentation.java`.
Aucun événement, réseau ou capability spécifique.

| Point | Comportement officiel 1.21.1 |
|---|---|
| Classe | `ModItem implements ICasterTool, GeoItem, IManaDiscountEquipment`, `stacksTo(1)` |
| Vitesse | `Tool.Rule.minesAndDrops(tag, 8.0F)` pour `mineable/pickaxe`, `axe`, `shovel`, `hoe` ; `overrideSpeed(SWORD_EFFICIENT, 1.5F)` ; défaut 1.0 |
| Tier | `deniesDrops(INCORRECT_FOR_DIAMOND_TOOL)` placé APRÈS les minesAndDrops → inopérant (première règle gagnante) ; tag vide en vanilla. Doc officielle : « diamond hardness ». **Décision : vrai tier diamant via `TierSortingRegistry`.** |
| Durabilité | aucune (`damagePerBlock 1` mais pas de `MAX_DAMAGE`) → incassable |
| Dégâts | aucun attribut → dégâts à mains nues |
| Actions | PICKAXE/AXE/SHOVEL/HOE/SWORD/SHEARS `_DIG` |
| Enchantabilité | `getEnchantmentValue` 15, `isEnchantable` true ; tags `enchantable/mining` + `mining_loot` → Efficiency, Fortune, Silk Touch (table + enclume) |
| Clic gauche | vanilla (minage/attaque) |
| Clic droit | `use()` → `castSpell(level, player, hand, "ars_nouveau.gauntlet.invalid")` : raytrace (portée + 0.5) ; entité vivante → `onCastOnEntity`, bloc → `onCastOnBlock`, sinon `onCast` |
| Sneak | sans sneak, viser un BlockEntity (hors tag `IGNORE_TILE`) annule le lancement ; Scribes Table toujours ignorée |
| Inscription | Gauntlet posé sur la Scribes Table, sneak + clic droit avec spellbook/parchemin ; refus si le sort contient une forme (`AbstractCastMethod`) ; Touch préfixé (`scribeModifiedSpell`) |
| Stockage | un seul slot (Data Component `SPELL_CASTER`) |
| Mana | coût normal (Touch inclus, 5) ; `getManaDiscount = (int)(spell.getCost() * 0.25)` ; ex. Touch+Break = 15 → 12 |
| Cooldown | aucun |
| Mana insuffisant | `SpellResolver.enoughMana` : message `ars_nouveau.spell.no_mana` + `NotEnoughManaPacket` |
| Aucun sort | sort vide invalide → message `ars_nouveau.gauntlet.invalid` (« Invalid spell. Gauntlets accept Effects and Augments only. ») |
| Tooltip | `SpellTooltip` (glyphes) si `Config.GLYPH_TOOLTIPS`, texte si Shift |
| Particularités | renderer teinte un os `"blade"` inexistant (code mort) ; animation référencée inexistante ; un slot vide inscrit donne `[Touch]` |

Recette officielle (`ars_nouveau:enchanting_apparatus`) : reagent `c:leathers` ; pedestals 1× `c:gems/diamond`,
2× `c:storage_blocks/gold`, 2× `c:storage_blocks/source` ; `sourceCost` 0 ; `keepNbtOfReagent` true.

## 10. Annexe — équivalents 1.20.1 vérifiés pour le Gauntlet (Ars 4.12.7 / Forge 1.20.1)
### 10.1 Modèle principal
`EnchantersMirror` (4.12.7) est le modèle : `use()` → `caster.castSpell(world, player, hand, msg, caster.getSpell())`,
`isScribedSpellValid` (aucune forme), `sendInvalidMessage`, `setSpell` qui préfixe `MethodSelf.INSTANCE`,
`getManaDiscount = (int)(spell.getCost() * .25)`. Pour le Gauntlet : `MethodTouch.INSTANCE` à la place.

### 10.2 Outil (pas de Data Component TOOL en 1.20.1)
- `getDestroySpeed(ItemStack, BlockState)` : 8.0 si `BlockTags.MINEABLE_WITH_PICKAXE/AXE/SHOVEL/HOE`, 1.5 si `SWORD_EFFICIENT`, sinon 1.0.
- `isCorrectToolForDrops(ItemStack, BlockState)` (hook Forge `IForgeItem`) : tag mineable ET
  `TierSortingRegistry.isCorrectTierForDrops(Tiers.DIAMOND, state)` (même schéma que le patch Forge de `DiggerItem`).
- `canPerformAction(ItemStack, ToolAction)` : `ToolActions.PICKAXE_DIG, AXE_DIG, SHOVEL_DIG, HOE_DIG, SWORD_DIG, SHEARS_DIG` (présents dans Forge 1.20.1).
- Enchantements : `canApplyAtEnchantingTable(stack, ench)` → `ench.category == EnchantmentCategory.DIGGER` ;
  `getEnchantmentValue` 15 ; `isEnchantable` true. Le patch Forge fait passer `Enchantment.canEnchant` (enclume)
  par `canApplyAtEnchantingTable` → une seule surcharge couvre table et enclume.
- Forge 1.20.1 génère des loot tables vanilla utilisant `can_tool_perform_action` (shears_dig) pour feuilles/herbes (25 tables) → comportement cisaille comme en 1.21 (à tester en jeu).

### 10.3 Sorts et mana
- `ICasterTool` (4.12.7) : `getSpellCaster(stack)` → `new SpellCaster(stack)` ; hooks `isScribedSpellValid(ISpellCaster, Player, InteractionHand, ItemStack, Spell)`,
  `setSpell(ISpellCaster, Player, InteractionHand, ItemStack, Spell)` (équivalent de `scribeModifiedSpell` 1.21), `sendInvalidMessage(Player)`,
  `getInformation(ItemStack, Level, List<Component>, TooltipFlag)`.
- `SpellCaster` 4.12.7 : NBT sous la clé `ars_nouveau:caster` dans le tag de l'ItemStack (`current_slot`, `spells.spell0`, `flavor`, `is_hidden`, `hidden_recipe`) ; `getMaxSlots()` = 1.
- `ISpellCaster.castSpell(...)` 4.12.7 : même logique que 1.21 (raytrace `player.getBlockReach() + 0.5`, tiles, Scribes Table, `onCastOnEntity/Block/onCast`), retourne `pass` côté client.
- `Spell` 4.12.7 est mutable (`public List<AbstractSpellPart> recipe`) ; constructeurs `Spell()`, `Spell(List)`, `Spell(AbstractSpellPart...)`, `clone()`. Préférer construire une nouvelle liste plutôt que muter le sort reçu.
- `IManaDiscountEquipment` identique ; `ManaUtil.getPlayerDiscounts(entity, spell, casterStack)` lit bien le caster tenu.
- `SpellResolver.getResolveCost` = `spell.getCost() - discounts` puis `SpellCostCalcEvent` ; `enoughMana` gère le message.
- Scribes Table : `ScribesBlock` appelle `IScribeable.onScribe` si le joueur sneak ; `SpellBook` implémente `ICasterTool`.
- Coûts vérifiés : Touch 5, Break 10, Harm 15 (identiques 4.12.7 / 5.x).
- Barre de mana : `ICasterTool` étend `IDisplayMana` (`shouldDisplay` true).

### 10.4 Réseau / serveur
Aucun networking custom : `use()` s'exécute côté serveur, le NBT de l'ItemStack se synchronise nativement,
inscription côté serveur via `ScribesBlock`. Aucun Mixin ni Access Transformer nécessaire.
Côté tooltip : `Screen.hasShiftDown()`/`SpellTooltip` ne doivent être appelés que depuis du code client
(Ars le fait directement dans ses items ; on peut isoler dans une petite classe client par prudence).

### 10.5 Assets, recette, tags
- Licence : code Ars LGPLv3 ; textures et modèles « All Rights Reserved » (README : réutilisation encouragée après contact sur Discord). Assets officiels du Gauntlet utilisés depuis le 2026-10-13 (voir §1).
- Rendu : modèle 3D officiel converti en JSON vanilla (§5.1), sans GeckoLib, sans code client.
- Recette 1.20.1 (format vérifié sur `enchanters_mirror.json` 4.12.7) :
  `"type": "ars_nouveau:enchanting_apparatus"`, `"reagent": [{"tag": "forge:leather"}]`,
  `"pedestalItems": [{"tag": "forge:gems/diamond"}, {"tag": "forge:storage_blocks/gold"} ×2, {"tag": "forge:storage_blocks/source"} ×2]`,
  `"output": {"item": "ars_backports:enchanters_gauntlet"}`, `"sourceCost": 0`, `"keepNbtOfReagent": true`.
  Tous ces tags existent (Forge `Tags.Items.LEATHER`, `GEMS_DIAMOND`, `STORAGE_BLOCKS_GOLD` ; `forge:storage_blocks/source` généré par Ars 1.20). Aucun ingrédient 1.21-only.
- Tag `c:tools/mining_tool` : n'existe pas en Forge 1.20.1 → omis. Tags `enchantable/*` : n'existent pas → remplacés par `canApplyAtEnchantingTable`.
- Documentation in-game : Ars 4.12.7 utilise Patchouli (`patchouli_books/worn_notebook`) ; une page pourrait être ajoutée en pur data plus tard (optionnel).

## 11. Où retrouver les sources de référence
    git clone --filter=blob:none -b 1.20 https://github.com/baileyholl/Ars-Nouveau   # 4.12.7
    git clone --filter=blob:none -b main https://github.com/baileyholl/Ars-Nouveau   # 5.x / 1.21.1
    git clone --filter=blob:none --depth 1 -b 1.20.1 https://github.com/MinecraftForge/MinecraftForge
Addon d'exemple officiel : `https://github.com/baileyholl/Ars-Nouveau-Example-Addon` (branche `1.20.x`).
