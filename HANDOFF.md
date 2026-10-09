# HANDOFF — état exact du projet

Dernière mise à jour : 2026-10-09 (session locale Windows / VS Code — Gauntlet étapes D, E, F codées, G rédigée).
Ce fichier décrit des **faits vérifiés**. Le mettre à jour à la fin de chaque étape.

## 1. Résumé
- Dépôt GitHub : **`lucasmettetal/ars-backports`** (renommé depuis `ars-nouveau` le 2026-10-09 ; l'ancienne URL
  redirige), branche `main`. Sur un clone existant : `git remote set-url origin https://github.com/lucasmettetal/ars-backports.git`.
  Nouveau clone : `git clone https://github.com/lucasmettetal/ars-backports.git`.
- Enchanter's Gauntlet :
  - A (squelette) : **validée** (`bd6c441`).
  - B (item, onglet, lang, modèle, texture) : **validée et vérifiée en jeu par l'utilisateur** (`ec13911`).
  - C (outil) : **codée et compilée** (`1cabc30`). Tests détaillés : `TESTS.md` 6–12.
  - D (sorts) : **codée et compilée** (`f87e8cd`).
  - E (réduction de mana 25 %) : **codée et compilée** (`50053c3`).
  - F (tooltip + recette Enchanting Apparatus) : **codée et compilée** (`3b5596d`) ; serveur dédié démarré sans erreur.
  - G (tests) : **checklist rédigée dans `TESTS.md`** ; tests en jeu C à F **pas encore faits**.
- Assets : texture placeholder originale. Message de demande d'autorisation (anglais) rédigé le 2026-10-09 pour le
  salon « Addon Discussion & Help » du Discord d'Ars ; envoi par l'utilisateur **non confirmé**, aucune réponse connue.
  Règles de ce Discord : anglais uniquement, pas de DM ni de mention sans permission, pas de cross-posting.
- Suite **proposée** (voir `BACKPORT_ROADMAP.md`, l'utilisateur a dit « tu peux continuer » sans valider l'ordre en détail) :
  finir/tester le Gauntlet, puis Mob Jar (Sniffer, Chat, Golem de neige), Enchanter's Fishing Rod, glyphes Pantomime
  puis Bubble. **Exclus par l'utilisateur** : ce qui est trop compliqué, comme la nouvelle dimension (Planarium).

## 2. Contenu réel du dépôt
| Fichier | Rôle |
|---|---|
| `settings.gradle` | dépôts de plugins, plugin foojay, `rootProject.name = 'ars_backports'` |
| `build.gradle` | ForgeGradle `[6.0,6.2)` (6.0.54), toolchain Java 17, mappings officiels, runs client/server (+ remap des refmaps mixin), dépôts BlameJared / Maven Central / Curios / GeckoLib filtrés par groupe, Ars en `implementation`, Curios + GeckoLib + MixinExtras en `runtimeOnly`, `-Xlint:deprecation` |
| `gradle.properties` | MC 1.20.1, Forge 47.4.10 (`[47.4,)`), Ars `4.12.7.264` (`[4.12.7,)`), dev : Curios `5.14.1+1.20.1`, GeckoLib `4.8.4`, MixinExtras `0.4.1` ; modid, `LGPL-3.0-only`, version 0.1.0 |
| `src/main/java/fr/lucas/arsbackports/ArsBackports.java` | `@Mod` ; enregistre `ModItems.ITEMS` ; ajoute le Gauntlet à l'onglet Ars (`CreativeTabRegistry.BLOCKS`, `ars_nouveau:general`) |
| `src/main/java/fr/lucas/arsbackports/registry/ModItems.java` | `DeferredRegister<Item>`, `ENCHANTERS_GAUNTLET` |
| `src/main/java/fr/lucas/arsbackports/item/EnchantersGauntlet.java` | `ModItem implements ICasterTool, IManaDiscountEquipment` : outil (C), sorts (D), mana (E), tooltip (F) — détail §3 |
| `src/main/resources/assets/ars_backports/lang/en_us.json`, `fr_fr.json` | nom de l'item + `ars_backports.gauntlet.invalid` (texte officiel 1.21 / tournure FR du Miroir d'Ars) |
| `src/main/resources/assets/ars_backports/models/item/enchanters_gauntlet.json` | `minecraft:item/handheld` |
| `src/main/resources/assets/ars_backports/textures/item/enchanters_gauntlet.png` | placeholder original 16×16 |
| `src/main/resources/data/ars_backports/recipes/enchanters_gauntlet.json` | recette Enchanting Apparatus (format 4.12.7) |
| `src/main/resources/META-INF/mods.toml`, `pack.mcmeta` | dépendances forge/minecraft/ars_nouveau ; pack_format 15 |
| `TESTS.md` | checklist de 36 tests (+ refus d'enchantements, incassable, cisailles) avec procédures |
| `CLAUDE.md`, `HANDOFF.md`, `BACKPORT_ROADMAP.md` | mémoire du projet |

## 3. Dernier résultat de compilation / exécution
**2026-10-09 — `gradlew.bat build` → BUILD SUCCESSFUL** après chacune des étapes D, E, F, 0 avertissement javac.
**`gradlew.bat runServer` (serveur dédié)** : `Done (29.868s)!`, aucun crash, aucune classe client chargée,
aucune erreur « Parsing error loading recipe » (seuls messages : création des configs au premier lancement).
`run-server/eula.txt` créé avec `eula=true` (dossier ignoré par Git).

Implémentation (API lues dans les sources Ars branche `1.20` @ `2c74064` et vérifiées dans le jar 4.12.7.264) :
- **D — sorts** (calqué sur `EnchantersMirror` 4.12.7) :
  - `use()` → `caster.castSpell(level, player, hand, Component.translatable("ars_backports.gauntlet.invalid"), caster.getSpell())`.
    `ISpellCaster.castSpell` 4.12.7 fait déjà : retour `pass` côté client, sort invalide → message, raytrace
    `0.5 + player.getBlockReach()`, Scribes Table ignorée, BlockEntity ignoré sans sneak (sauf tag `IGNORE_TILE`),
    entité vivante → `onCastOnEntity`, bloc → `onCastOnBlock`, sinon `onCast`. Rien n'est réimplémenté.
  - `isScribedSpellValid` : aucune `AbstractCastMethod` dans `spell.recipe`.
  - `setSpell` : **nouvelle** liste `[MethodTouch.INSTANCE] + spell.recipe`, appliquée à `spell.clone().setRecipe(...)`
    (le sort lu dans le livre n'est pas modifié, contrairement au Miroir) puis `ICasterTool.super.setSpell`.
  - `sendInvalidMessage` : `PortUtil.sendMessageNoSpam`. Inscription via `ICasterTool.onScribe` (Scribes Table, sneak),
    qui copie aussi couleur/nom/son du livre. Stockage : `SpellCaster` NBT (`ars_nouveau:caster`), 1 slot.
  - Clé de langue propre `ars_backports.gauntlet.invalid` (`ars_nouveau.gauntlet.invalid` n'existe pas en 4.12.7).
- **E — mana** : `IManaDiscountEquipment.getManaDiscount(stack, spell)` = `(int) (spell.getCost() * 0.25)`.
  `ManaUtil.getPlayerDiscounts` additionne curios + armure + objet lanceur (`casterStack`). Touch + Break : 15 → 12.
  Mana insuffisant / créatif gérés par `SpellResolver.enoughMana` (en créatif : lancement autorisé, mana tout de même retiré).
- **F — tooltip** : comme le 1.21 et les casters 4.12.7 (`CasterTome`, `SpellParchment`) : `getTooltipImage` →
  `new SpellTooltip(caster)` si `Config.GLYPH_TOOLTIPS` et sans Shift ; `appendHoverText` → `getInformation` (texte)
  avec Shift ou si les glyphes sont désactivés. `SpellTooltip` est un record commun ; son rendu est enregistré par Ars.
  `Screen.hasShiftDown()` n'est appelé que dans des méthodes exécutées côté client (même schéma qu'Ars ; serveur dédié OK).
  Barre de mana : fournie par `ICasterTool` (`IDisplayMana.shouldDisplay` = true).
- **F — recette** : `ars_nouveau:enchanting_apparatus`, reagent `[{"tag":"forge:leather"}]`, pedestals
  `forge:gems/diamond`, 2× `forge:storage_blocks/gold`, 2× `forge:storage_blocks/source`, `sourceCost` 0,
  `keepNbtOfReagent` true. Tags vérifiés dans les jars Forge 47.4.10 et Ars 4.12.7.264.
- **C — outil** (rappel) : vitesse 8.0 (mineable pickaxe/axe/shovel/hoe), 1.5 (`SWORD_EFFICIENT`), 1.0 ; drops si tag
  mineable ET `TierSortingRegistry.isCorrectTierForDrops(Tiers.DIAMOND, state)` ; 6 `ToolActions` `_DIG` ; incassable ;
  `isEnchantable` true ; `getEnchantmentValue(ItemStack)` 15 ; `canApplyAtEnchantingTable` → catégorie `DIGGER`.

Corrections de dev (étape B) : Curios/GeckoLib/MixinExtras en `runtimeOnly` (le jar Maven d'Ars n'embarque pas
MixinExtras) ; propriétés `mixin.env.remapRefMap` / `refMapRemappingFile` pour les mixins des dépendances en dev.

Environnement : Java **Temurin 17.0.20.1+1**, Gradle 8.8, ForgeGradle 6.0.54, Forge 1.20.1-47.4.10,
Ars `com.hollingsworth.ars_nouveau:ars_nouveau-1.20.1:4.12.7.264`.

### Environnement local de la machine Windows (PC « lucas »)
Git et JDK installés en **portable** dans `C:\Users\lucas\tools\` (PortableGit 2.56.0.2, Temurin 17.0.20.1) ;
`Path` et `JAVA_HOME` **utilisateur** mis à jour. Identité Git du dépôt : `lucasmettetal <lucas8237014@gmail.com>`.
Sur un autre PC : n'importe quel JDK 17 + Git suffisent.

## 4. Blocages / points à vérifier
1. **Tests en jeu du Gauntlet à faire par l'utilisateur** : `TESTS.md` (5, 6–12, 13–36). Priorité : 13–17, 25, 27, 31, 36.
2. JEI n'est pas dans l'environnement de dev (test 3) : l'ajouter en `runtimeOnly` si besoin (demander avant).
3. Réponse du Discord d'Ars sur les assets : si accord, remplacer texture/modèle (le modèle officiel est GeckoLib ;
   préférer une conversion en modèle JSON vanilla) et ajouter les crédits.

## 5. Erreurs connues
Aucune.

## 6. Prochaines actions exactes
1. Tests en jeu du Gauntlet (`TESTS.md`) ; corriger tout écart constaté.
2. Puis, dans l'ordre validé : comportements Mob Jar (Sniffer, Chat, Golem de neige, via `JarBehaviorRegistry.register`),
   Enchanter's Fishing Rod, glyphe Pantomime, glyphe Bubble. Analyse 1.21.1 vs 4.12.7 avant chaque backport.

## 7. Analyse du Gauntlet officiel (vérifiée dans le code source)
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

## 8. Équivalents 1.20.1 vérifiés (Ars 4.12.7 / Forge 1.20.1)
### 8.1 Modèle principal
`EnchantersMirror` (4.12.7) est le modèle : `use()` → `caster.castSpell(world, player, hand, msg, caster.getSpell())`,
`isScribedSpellValid` (aucune forme), `sendInvalidMessage`, `setSpell` qui préfixe `MethodSelf.INSTANCE`,
`getManaDiscount = (int)(spell.getCost() * .25)`. Pour le Gauntlet : `MethodTouch.INSTANCE` à la place.

### 8.2 Outil (pas de Data Component TOOL en 1.20.1)
- `getDestroySpeed(ItemStack, BlockState)` : 8.0 si `BlockTags.MINEABLE_WITH_PICKAXE/AXE/SHOVEL/HOE`, 1.5 si `SWORD_EFFICIENT`, sinon 1.0.
- `isCorrectToolForDrops(ItemStack, BlockState)` (hook Forge `IForgeItem`) : tag mineable ET
  `TierSortingRegistry.isCorrectTierForDrops(Tiers.DIAMOND, state)` (même schéma que le patch Forge de `DiggerItem`).
- `canPerformAction(ItemStack, ToolAction)` : `ToolActions.PICKAXE_DIG, AXE_DIG, SHOVEL_DIG, HOE_DIG, SWORD_DIG, SHEARS_DIG` (présents dans Forge 1.20.1).
- Enchantements : `canApplyAtEnchantingTable(stack, ench)` → `ench.category == EnchantmentCategory.DIGGER` ;
  `getEnchantmentValue` 15 ; `isEnchantable` true. Le patch Forge fait passer `Enchantment.canEnchant` (enclume)
  par `canApplyAtEnchantingTable` → une seule surcharge couvre table et enclume.
- Forge 1.20.1 génère des loot tables vanilla utilisant `can_tool_perform_action` (shears_dig) pour feuilles/herbes (25 tables) → comportement cisaille comme en 1.21 (à tester en jeu).

### 8.3 Sorts et mana
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

### 8.4 Réseau / serveur
Aucun networking custom : `use()` s'exécute côté serveur, le NBT de l'ItemStack se synchronise nativement,
inscription côté serveur via `ScribesBlock`. Aucun Mixin ni Access Transformer nécessaire.
Côté tooltip : `Screen.hasShiftDown()`/`SpellTooltip` ne doivent être appelés que depuis du code client
(Ars le fait directement dans ses items ; on peut isoler dans une petite classe client par prudence).

### 8.5 Assets, recette, tags
- Licence : code Ars LGPLv3 ; textures et modèles « All Rights Reserved » (README : réutilisation encouragée après contact sur Discord). **Placeholder uniquement** pour l'instant.
- Rendu : modèle JSON vanilla (`item/generated` ou éléments 3D convertis), sans GeckoLib, sans code client.
- Recette 1.20.1 (format vérifié sur `enchanters_mirror.json` 4.12.7) :
  `"type": "ars_nouveau:enchanting_apparatus"`, `"reagent": [{"tag": "forge:leather"}]`,
  `"pedestalItems": [{"tag": "forge:gems/diamond"}, {"tag": "forge:storage_blocks/gold"} ×2, {"tag": "forge:storage_blocks/source"} ×2]`,
  `"output": {"item": "ars_backports:enchanters_gauntlet"}`, `"sourceCost": 0`, `"keepNbtOfReagent": true`.
  Tous ces tags existent (Forge `Tags.Items.LEATHER`, `GEMS_DIAMOND`, `STORAGE_BLOCKS_GOLD` ; `forge:storage_blocks/source` généré par Ars 1.20). Aucun ingrédient 1.21-only.
- Tag `c:tools/mining_tool` : n'existe pas en Forge 1.20.1 → omis. Tags `enchantable/*` : n'existent pas → remplacés par `canApplyAtEnchantingTable`.
- Documentation in-game : Ars 4.12.7 utilise Patchouli (`patchouli_books/worn_notebook`) ; une page pourrait être ajoutée en pur data plus tard (optionnel).

## 9. Où retrouver les sources de référence
    git clone --filter=blob:none -b 1.20 https://github.com/baileyholl/Ars-Nouveau   # 4.12.7
    git clone --filter=blob:none -b main https://github.com/baileyholl/Ars-Nouveau   # 5.x / 1.21.1
    git clone --filter=blob:none --depth 1 -b 1.20.1 https://github.com/MinecraftForge/MinecraftForge
Addon d'exemple officiel : `https://github.com/baileyholl/Ars-Nouveau-Example-Addon` (branche `1.20.x`).
