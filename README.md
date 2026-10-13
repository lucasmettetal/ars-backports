# Ars Backports

An independent addon for **Ars Nouveau 4.12.7** on **Minecraft 1.20.1 / Forge 47.4+** that backports selected
features from the Ars Nouveau 1.21.1 versions, for players and modpacks that stay on 1.20.1.

It depends on Ars Nouveau and does not modify or redistribute it. No mixins: it only uses the existing Ars API.

## Features
- **Enchanter's Gauntlet** — a spell-casting multi-tool that mines at diamond level (pickaxe, axe, shovel, hoe).
  Inscribe it at the Scribes Table with a spell that has no form: Touch is added automatically, and spells
  cast with it get a 25 % mana discount. Crafted in the Enchanting Apparatus (leather, diamond, 2 gold blocks,
  2 source blocks).

More backports are planned, see [BACKPORT_ROADMAP.md](BACKPORT_ROADMAP.md).

## Requirements
- Minecraft 1.20.1, Forge 47.4 or newer
- Ars Nouveau 4.12.7 or newer (1.20.1), and its own dependencies (Curios, GeckoLib)

## Credits and licenses
- **Code**: adapted from [Ars Nouveau](https://github.com/baileyholl/Ars-Nouveau) by baileyholl and contributors,
  licensed under the **LGPL v3** (see [LICENSE](LICENSE)). This addon's code is also LGPL v3.
- **Assets**: the Enchanter's Gauntlet texture and model come from Ars Nouveau. Ars Nouveau's textures and models
  are *All Rights Reserved* and remain the property of their authors; they are **not** covered by this repository's
  LGPL license. They are used here because Ars Nouveau encourages addon authors to reuse its assets, and the reuse
  was discussed on the Ars Nouveau Discord. They will be removed on request of the Ars Nouveau team.
  The gauntlet model was converted from GeckoLib to a vanilla JSON model.

## Building
    gradlew build          # jar in build/libs/
    gradlew runClient      # development client (Windows: gradlew.bat)
