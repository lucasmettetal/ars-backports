# HANDOFF — état exact du projet

Dernière mise à jour : 2026-10-06 (session locale Windows / VS Code — étape C codée et compilée).
Ce fichier décrit des **faits vérifiés**. Le mettre à jour à la fin de chaque étape.

## 1. Résumé
- Dépôt GitHub : `lucasmettetal/ars-nouveau`, branche `main` (choix de l'utilisateur, `ars-backports` n'étant pas accessible).
  Ce dépôt contient l'addon Ars Backports, **pas** Ars Nouveau. Il pourra être renommé `ars-backports` sur GitHub.
  Clone local conseillé : `git clone https://github.com/lucasmettetal/ars-nouveau.git ars-backports`.
- Phase 1 (analyse du Gauntlet 1.21.1 vs Ars 4.12.7) : **terminée**.
- Étape A (squelette Forge) : **VALIDÉE** — BUILD SUCCESSFUL le 2026-10-06 (commit `bd6c441`).
- Étape B (item basique) : **VALIDÉE** (commit `ec13911`) — build OK, `runClient` OK, et **vérifiée en jeu par
  l'utilisateur** le 2026-10-06 (onglet Ars, texture, noms, pile de 1).
- Étape C (comportement d'outil) : **codée, BUILD SUCCESSFUL sans avertissement Java** ; tests en jeu **à faire** (§4).
- Étapes D à G : **non commencées**.
- Prochaine action : tests en jeu de l'étape C, puis **étape D** (sorts, §8.3).

## 2. Contenu réel du dépôt
| Fichier | Rôle |
|---|---|
| `settings.gradle` | dépôts de plugins (Gradle Plugin Portal, maven.minecraftforge.net), plugin foojay (auto-téléchargement JDK 17), `rootProject.name = 'ars_backports'` |
| `build.gradle` | ForgeGradle `[6.0,6.2)`, toolchain Java 17, mappings officiels, runs client/server (+ remap des refmaps mixin), dépôts BlameJared / Maven Central / Curios / GeckoLib (filtrés par groupe), Ars en `implementation`, Curios + GeckoLib + MixinExtras en `runtimeOnly`, expansion de `mods.toml` |
| `gradle.properties` | versions : MC 1.20.1, Forge 47.4.10 (range `[47.4,)`), Ars `4.12.7.264` (range `[4.12.7,)`), runtime dev : Curios `5.14.1+1.20.1`, GeckoLib `4.8.4`, MixinExtras `0.4.1` ; modid, licence `LGPL-3.0-only`, version 0.1.0 |
| `gradlew`, `gradlew.bat`, `gradle/wrapper/*` | wrapper Gradle 8.8 |
| `src/main/java/fr/lucas/arsbackports/ArsBackports.java` | `@Mod("ars_backports")` ; enregistre `ModItems.ITEMS` sur le bus du mod ; ajoute le Gauntlet à l'onglet Ars via `BuildCreativeModeTabContentsEvent` (`CreativeTabRegistry.BLOCKS`, id `ars_nouveau:general`) |
| `src/main/java/fr/lucas/arsbackports/registry/ModItems.java` | `DeferredRegister<Item>` (`ForgeRegistries.ITEMS`), `ENCHANTERS_GAUNTLET` = `enchanters_gauntlet` |
| `src/main/java/fr/lucas/arsbackports/item/EnchantersGauntlet.java` | `extends ModItem` (Ars), `stacksTo(1)` ; comportement d'outil (étape C, voir §3) ; sorts/mana à venir |
| `src/main/resources/assets/ars_backports/lang/en_us.json` | « Enchanter's Gauntlet » |
| `src/main/resources/assets/ars_backports/lang/fr_fr.json` | « Gantelet d'enchanteur » (convention d'Ars FR : « Miroir d'enchanteur », « Épée d'enchanteur ») |
| `src/main/resources/assets/ars_backports/models/item/enchanters_gauntlet.json` | `minecraft:item/handheld`, `layer0` = `ars_backports:item/enchanters_gauntlet` |
| `src/main/resources/assets/ars_backports/textures/item/enchanters_gauntlet.png` | placeholder **original** 16×16 (gant brun, gemme violette, manchette dorée), généré pixel par pixel ; aucun asset Ars |
| `src/main/resources/META-INF/mods.toml` | dépendances obligatoires : forge, minecraft, `ars_nouveau` (`ordering="AFTER"`, `side="BOTH"`) |
| `src/main/resources/pack.mcmeta` | `pack_format` 15 (1.20.1) |
| `LICENSE` | texte LGPLv3 |
| `.gitignore`, `.gitattributes` | repris du MDK Forge 1.20.1 (+ `run-data`, `run-server`) ; `build/`, `.gradle/`, `run/` ignorés |
| `CLAUDE.md` | instructions permanentes |
| `BACKPORT_ROADMAP.md` | backports candidats (non commencés) |

Pas encore de recette, de tags, ni de logique de sort.

## 3. Dernier résultat de compilation / exécution
**2026-10-06 — étape C : `gradlew.bat build` → BUILD SUCCESSFUL**, 0 avertissement javac
(`-Xlint:deprecation` désormais activé dans `build.gradle`). Implémentation dans `EnchantersGauntlet` (API vérifiées
par `javap` dans `forge-1.20.1-47.4.10_mapped_official_1.20.1.jar`) :
- `getDestroySpeed(stack, state)` : 8.0 si `BlockTags.MINEABLE_WITH_PICKAXE/AXE/SHOVEL/HOE`, 1.5 si `BlockTags.SWORD_EFFICIENT`
  (**existe bien en 1.20.1**), sinon 1.0.
- `isCorrectToolForDrops(stack, state)` : tag mineable **ET** `TierSortingRegistry.isCorrectTierForDrops(Tiers.DIAMOND, state)`
  (même logique que `DiggerItem` patché par Forge → blocs `forge:needs_netherite_tool` refusés).
- `canPerformAction` : `PICKAXE_DIG, AXE_DIG, SHOVEL_DIG, HOE_DIG, SWORD_DIG, SHEARS_DIG` (pas de labour/écorçage, comme l'officiel).
- Incassable : aucune durabilité (`Item.Properties` sans `durability`) → `canBeDepleted()` false, jamais endommagé.
- Enchantements :
  - `isEnchantable(stack)` → true (le vanilla exige `canBeDepleted()`, donc obligatoire pour un item incassable) ;
  - `getEnchantmentValue(ItemStack)` → 15 (hook Forge ; la version sans paramètre est dépréciée par Forge.
    Chaîne vérifiée : `EnchantmentHelper` → `ItemStack.getEnchantmentValue()` → `Item.getEnchantmentValue(ItemStack)`) ;
  - `canApplyAtEnchantingTable` → `enchantment.category == EnchantmentCategory.DIGGER` (Efficiency, Fortune, Silk Touch ;
    pas Unbreaking/Mending, catégorie BREAKABLE). Vérifié en bytecode : `Enchantment.canEnchant` (enclume) →
    `canApplyAtEnchantingTable` → `ItemStack.canApplyAtEnchantingTable` → l'item. Les livres sur l'enclume suivent la même règle.

**Étape B : `gradlew.bat build` → BUILD SUCCESSFUL** ; `gradlew.bat runClient` → client lancé jusqu'au
**menu principal** (« Forge 47.4.10 / Minecraft 1.20.1 / 7 mods loaded » : minecraft, forge, ars_nouveau, curios,
geckolib, mixinextras, ars_backports). Aucune erreur/avertissement de modèle ou texture concernant `ars_backports`
(seuls des warnings internes à Ars sur `magelight_torch`, sans rapport).

Corrections nécessaires pour `runClient` (dev uniquement, aucun effet sur le jar ni sur `mods.toml`) :
1. Curios 5.14.1+1.20.1 et GeckoLib 4.8.4 en `runtimeOnly fg.deobf(...)` (dépendances obligatoires d'Ars).
2. `NoClassDefFoundError: com/llamalad7/mixinextras/MixinExtrasBootstrap` : Ars intègre MixinExtras par jarJar dans
   ses jars de release, mais **son jar Maven ne le contient pas** → `runtimeOnly "io.github.llamalad7:mixinextras-forge:0.4.1"`
   (Maven Central ; Ars exige `[0.2.0-beta.8,)`).
3. `InvalidAccessorException` sur `curios.mixins.json:AccessorEntity` : refmaps SRG non remappés en dev →
   propriétés de run du MDK Forge `mixin.env.remapRefMap=true` + `mixin.env.refMapRemappingFile=build/createSrgToMcp/output.srg`.

Étape A (rappel) : Java **Temurin 17.0.20.1+1**, Gradle 8.8, ForgeGradle **6.0.54**, Forge **1.20.1-47.4.10**,
Ars **`com.hollingsworth.ars_nouveau:ars_nouveau-1.20.1:4.12.7.264`** (seule build 4.12.7 publiée sur BlameJared).
Avertissement persistant sans impact : « Deprecated Gradle features … incompatible with Gradle 9.0 » (ForgeGradle).

### Environnement local de la machine Windows (PC « lucas »)
La machine n'avait ni Git ni Java ni winget. Installés en **portable**, sans droits admin, dans `C:\Users\lucas\tools\` :
- `tools\git\` : PortableGit 2.56.0.2 (inclut Git Credential Manager pour `git push`) ;
- `tools\jdk-17.0.20.1+1\` : Temurin JDK 17 (checksum SHA-256 vérifié).
`Path` **utilisateur** complété avec `tools\git\cmd` et `tools\jdk-17.0.20.1+1\bin`, `JAVA_HOME` utilisateur défini.
Identité Git configurée au niveau du dépôt : `lucasmettetal <lucas8237014@gmail.com>`.
Sur un autre PC : n'importe quel JDK 17 + Git suffisent.

## 4. Blocages / points à vérifier
1. **Tests en jeu de l'étape C (à faire par l'utilisateur, en survie)** :
   - pierre, minerais, bois, terre, culture/foin (houe) : minage rapide (≈ outil vitesse 8) et drops corrects ;
   - obsidienne / minerai de diamant : récoltés (tier diamant) ;
   - bloc `forge:needs_netherite_tool` (aucun en vanilla 1.20.1 ; test possible avec un mod ou un datapack ajoutant
     ce tag) : non récolté ;
   - feuilles / toile d'araignée / herbe : comportement cisaille (drop via `can_tool_perform_action shears_dig`) ;
   - table d'enchantement : enchantable ; enclume : Efficiency/Fortune/Silk Touch OK, Unbreaking/Mending refusés ;
   - durabilité : jamais de barre de dégâts.
2. `runServer` pas encore lancé (nécessitera `eula=true` dans `run-server/eula.txt`).

## 5. Erreurs connues
Aucune.

## 6. Prochaines actions exactes
1. ~~Étape A~~ — **fait**.
2. ~~Étape B~~ — **fait** (item, registre, onglet Ars, lang, modèle, texture placeholder, runClient OK).
3. ~~Étape C~~ — **codée et compilée** ; tests en jeu à confirmer (§4.1).
4. **Étape D (prochaine)** : lire `EnchantersMirror` 4.12.7 (jar `ars_nouveau-1.20.1-4.12.7.264`), puis implémenter
   `ICasterTool` (inscription Scribes Table sans forme, préfixe `MethodTouch.INSTANCE`, `use()` → `castSpell`). §8.3. Compiler.
   Étape E : `IManaDiscountEquipment` (25 %). Compiler.
5. Étape F : tooltip, recette Enchanting Apparatus (§8.5). Compiler.
6. Étape G : checklist de test en jeu, serveur dédié.

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
