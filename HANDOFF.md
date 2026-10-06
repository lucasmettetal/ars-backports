# HANDOFF — état exact du projet

Dernière mise à jour : 2026-10-06 (session locale Windows / VS Code — étape A validée).
Ce fichier décrit des **faits vérifiés**. Le mettre à jour à la fin de chaque étape.

## 1. Résumé
- Dépôt GitHub : `lucasmettetal/ars-nouveau`, branche `main` (choix de l'utilisateur, `ars-backports` n'étant pas accessible).
  Ce dépôt contient l'addon Ars Backports, **pas** Ars Nouveau. Il pourra être renommé `ars-backports` sur GitHub.
  Clone local conseillé : `git clone https://github.com/lucasmettetal/ars-nouveau.git ars-backports`.
- Phase 1 (analyse du Gauntlet 1.21.1 vs Ars 4.12.7) : **terminée**.
- Étape A (squelette Forge) : **VALIDÉE — BUILD SUCCESSFUL le 2026-10-06** (voir §3).
- Étapes B à G : **non commencées**.
- Prochaine action : **étape B** (item `enchanters_gauntlet` basique, voir §6).

## 2. Contenu réel du dépôt
| Fichier | Rôle |
|---|---|
| `settings.gradle` | dépôts de plugins (Gradle Plugin Portal, maven.minecraftforge.net), plugin foojay (auto-téléchargement JDK 17), `rootProject.name = 'ars_backports'` |
| `build.gradle` | ForgeGradle `[6.0,6.2)`, toolchain Java 17, mappings officiels, runs client/server, dépôt BlameJared (groupe `com.hollingsworth.ars_nouveau` uniquement), dépendance `fg.deobf("com.hollingsworth.ars_nouveau:ars_nouveau-1.20.1:${ars_version}")`, expansion de `mods.toml` |
| `gradle.properties` | versions : MC 1.20.1, Forge 47.4.10 (range `[47.4,)`), Ars `4.12.7.264` (range de chargement `[4.12.7,)`), modid, licence `LGPL-3.0-only`, version 0.1.0 |
| `gradlew`, `gradlew.bat`, `gradle/wrapper/*` | wrapper Gradle 8.8 (généré avec la distribution officielle 8.8) |
| `src/main/java/fr/lucas/arsbackports/ArsBackports.java` | classe `@Mod("ars_backports")`, constructeur `ArsBackports(FMLJavaModLoadingContext)`, log uniquement |
| `src/main/resources/META-INF/mods.toml` | dépendances obligatoires : forge, minecraft, `ars_nouveau` (`ordering="AFTER"`, `side="BOTH"`) |
| `src/main/resources/pack.mcmeta` | `pack_format` 15 (1.20.1) |
| `LICENSE` | texte LGPLv3 (copié de `license.txt` d'Ars Nouveau branche 1.20) |
| `.gitignore`, `.gitattributes` | repris du MDK Forge 1.20.1 (+ `run-data`, `run-server`) |
| `CLAUDE.md` | instructions permanentes |
| `BACKPORT_ROADMAP.md` | backports candidats (non commencés) |

Aucun item, aucune texture, aucune recette n'existe encore.

## 3. Dernier résultat de compilation
**2026-10-06 — `gradlew.bat build` → BUILD SUCCESSFUL** (3 min 50 au premier lancement, caches vides),
sur Windows 10 LTSC, en local :
- Java réellement utilisé : **Temurin 17.0.20.1+1** (Eclipse Adoptium), amd64. Le toolchain Java 17 est satisfait
  directement par ce JDK (foojay n'a rien téléchargé).
- Gradle 8.8 (wrapper), ForgeGradle résolu : **6.0.54**.
- Forge **1.20.1-47.4.10**, mappings `official` 1.20.1.
- Ars Nouveau résolu : **`com.hollingsworth.ars_nouveau:ars_nouveau-1.20.1:4.12.7.264`** (seule build 4.12.7
  publiée sur `maven.blamejared.com`, vérifié dans `maven-metadata.xml` ; `latest` = 4.12.7.264).
- Jar produit : `build/libs/ars_backports-1.20.1-0.1.0.jar` (2,2 Ko : `ArsBackports.class`, `mods.toml` expansé
  correctement, `pack.mcmeta`, manifest).
- Corrections nécessaires : **une seule** — `ars_version=4.12.7.+` remplacé par `4.12.7.264` (reproductibilité).
  Aucun changement de code ni de build.gradle. Le constructeur `@Mod(FMLJavaModLoadingContext)` compile avec 47.4.10.
- Seul avertissement : « Deprecated Gradle features … incompatible with Gradle 9.0 » (provient de ForgeGradle, sans impact).

Historique : en session cloud, le build échouait uniquement parce que le proxy bloquait les dépôts Maven.

### Environnement local de la machine Windows (PC « lucas »)
La machine n'avait ni Git ni Java ni winget. Installés en **portable**, sans droits admin, dans `C:\Users\lucas\tools\` :
- `tools\git\` : PortableGit 2.56.0.2 (inclut Git Credential Manager pour `git push`) ;
- `tools\jdk-17.0.20.1+1\` : Temurin JDK 17 (checksum SHA-256 vérifié).
`Path` **utilisateur** complété avec `tools\git\cmd` et `tools\jdk-17.0.20.1+1\bin`, `JAVA_HOME` utilisateur défini.
(Redémarrer VS Code pour qu'il voie ces variables.) Sur un autre PC : n'importe quel JDK 17 + Git suffisent.

## 4. Blocages / points à vérifier
1. `BlockTags.SWORD_EFFICIENT` : présumé présent en 1.20.1, à vérifier à la compilation de l'étape C.
2. Dev runs (`runClient`/`runServer`) : non encore lancés. Il faudra probablement ajouter GeckoLib et Curios en
   `runtimeOnly` (dépendances obligatoires d'Ars 4.12.7 : `curios [1.19-5.0.7.1,)`, `geckolib [4.2.1,)`), dépôts
   `dl.cloudsmith.io/public/geckolib3/geckolib/maven/` et `maven.theillusivec4.top`. Pas de dépendance du mod.

## 5. Erreurs connues
Aucune.

## 6. Prochaines actions exactes
1. ~~Étape A : build~~ — **fait** (2026-10-06).
2. ~~Fixer `ars_version`~~ — **fait** (`4.12.7.264`).
3. **Étape B (prochaine)** : `ModItems` (DeferredRegister) + item `enchanters_gauntlet` basique (`stacksTo(1)`), lang en_us/fr_fr,
   texture placeholder + modèle JSON `item/generated`, onglet créatif d'Ars (`CreativeTabRegistry.BLOCKS`, id `ars_nouveau:general`)
   via `BuildCreativeModeTabContentsEvent`. Compiler, puis `runClient` (ajouter Curios/GeckoLib en runtimeOnly si nécessaire).
4. Étape C : outil (§8.2). Compiler.
5. Étape D/E : intégration sorts + mana (§8.3). Compiler.
6. Étape F : modèle JSON vanilla, texture placeholder, tooltip, recette (§8.5). Compiler.
7. Étape G : comparer au comportement officiel (§7), checklist de test en jeu, serveur dédié.

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
