# Ars Backports

An independent addon for **Ars Nouveau 4.12.7** on **Minecraft 1.20.1 / Forge 47.4+** that backports selected
features from the Ars Nouveau 1.21.1 versions, for players and modpacks that stay on 1.20.1.

It depends on Ars Nouveau and does not redistribute it. It uses the Ars API where possible, and a few targeted
mixins where 1.21 changed Ars' own blocks and behaviors.

## Features
- **Enchanter's Gauntlet** — a spell-casting multi-tool that mines at diamond level (pickaxe, axe, shovel, hoe).
  Inscribe it at the Scribes Table with a spell that has no form: Touch is added automatically, and spells
  cast with it get a 25 % mana discount. Crafted in the Enchanting Apparatus (leather, diamond, 2 gold blocks,
  2 source blocks).
- **Enchanter's Fishing Rod** — a fishing rod that holds a formless spell (Touch is added automatically): fish
  normally, or hook a creature and use the rod again to cast the spell on it. Lure and Luck of the Sea work.
  Crafted in the Enchanting Apparatus (fishing rod, 2 gold blocks, 2 source blocks; enchantments are kept).
- **Mob Jar behaviors**:
  - *Sniffer*: digs up ancient seeds every 8 minutes into adjacent inventories (paused while the jar is powered);
  - *Cat*: emits a redstone signal while its owner is online, and may bring a morning gift when its owner sleeps nearby;
  - *Snow Golem*: throws a snowball out of the jar on a redstone pulse.
- **Pantomime glyph** (form) — applies the spell to the block a couple of blocks in front of you, whatever you aim at.
  Amplify/Dampen move the target farther/closer; Sensitive outlines the targeted block.
- **Bubble glyph** (effect) — traps the creature it touches in a rising bubble; hurting the creature pops the bubble
  for bonus damage. Extend Time / Amplify increase its duration and damage. Creatures inside a bubble count as wet.
- **Mob Jar improvements** from 1.21: passive jar behaviors pause while the jar is powered; chickens and pandas put
  their eggs / slime balls into adjacent inventories; a jarred chest minecart or chest boat can be opened (sneak +
  use) and is accessible to hoppers; jars facing down render their mob correctly. Dispensers place Source and
  Containment Jars and use the Dominion Wand; pistons break jars.
- **Decorations and blocks**: archwood, gold, sourcestone and smooth sourcestone **grates** (liquids and items pass
  through, buckets work through them), the **Source Lamp** (toggled by redstone pulses, brightness set with Light),
  the **Starbuncle Plush**, the **Starbuncle** and **Resting Drygmy** paintings (Enchanting Apparatus), and the
  **Ars Stencil** with the 8 Ars banner patterns, and **archwood signs and hanging signs**.
- **Quality of life from 1.21**: hand a gold nugget / Wilden drop to wild Starbuncles and Drygmies; Underfoot targets
  your mount or the entity below you; weather and time rituals work in every dimension; new tags (same ids as 1.21):
  `ars_nouveau:intangible_blacklist`, `breed_blacklist`, `overgrowth_blacklist`, `deny_spawn_ritual_blacklist` /
  `_whitelist`; Intangible respects harvest levels on every block of its AOE.

More backports are planned, see [BACKPORT_ROADMAP.md](BACKPORT_ROADMAP.md).

## Requirements
- Minecraft 1.20.1, Forge 47.4 or newer
- Ars Nouveau 4.12.7 or newer (1.20.1), and its own dependencies (Curios, GeckoLib)

## Credits and licenses
- **Code**: adapted from [Ars Nouveau](https://github.com/baileyholl/Ars-Nouveau) by baileyholl and contributors,
  licensed under the **LGPL v3** (see [LICENSE](LICENSE)). This addon's code is also LGPL v3.
- **Assets**: all textures and models of this addon come from Ars Nouveau (1.21.1 versions). Ars Nouveau's textures and models
  are *All Rights Reserved* and remain the property of their authors; they are **not** covered by this repository's
  LGPL license. They are used here because Ars Nouveau encourages addon authors to reuse its assets, and the reuse
  was discussed on the Ars Nouveau Discord. They will be removed on request of the Ars Nouveau team.
  Their models were converted from GeckoLib to vanilla JSON models.

## Building
    gradlew build          # jar in build/libs/
    gradlew runClient      # development client (Windows: gradlew.bat)
