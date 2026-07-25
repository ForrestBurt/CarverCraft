# CarverCraft

A lapidary and jewelry mod for Minecraft 1.21.1 (NeoForge).

Find rough stones in the world, tumble them smooth, silversmith them into rings, and wear them.

## Current content

**Silver.** Silver ore generates through the overworld with a deepslate variant. Smelt or blast raw silver into ingots. Tagged into the `c:` namespace so it unifies with other mods' silver.

**Rough stones.** Andesite, granite, and basalt occasionally drop rough jasper, garnet, and agate when mined — handled by a global loot modifier rather than overwriting vanilla loot tables, so it stacks with other mods.

**Five machines, three finishes.** The Rock Tumbler (passive, slow, four independent lanes) turns rough stone into tumbled pebbles for trinkets. The Trim Saw and Cabbing Machine (FE-powered) slab and dome opaque stone into cabochons. The Faceting Machine (FE-powered, the expensive one) cuts transparent gems — including vanilla diamond, emerald, and amethyst — into faceted stones. The Alloyer melts real jeweller's alloys at real ratios: sterling silver, electrum, rose gold. Machines expose standard item and energy capabilities, so hoppers and any FE cable (Mekanism, Immersive Engineering) just work.

**Shop supplies.** The tumbler runs on silicon carbide grit (sand + coal — the Acheson process) and the cabbing machine finishes with polishing compound (jeweler's rouge, ground from raw iron). One per cycle, loaded at the start, like the real bench.

**Twelve stones, honestly paired.** Agate from basalt, jasper from andesite, garnet from granite, corundum from deepslate — every stone drops from a geologically plausible host, and opaque stones are cabbed while transparent ones are faceted, never the reverse. The two Idaho signatures: **star garnet**, the state gem and the mod's endgame, and **Bruneau jasper**, the picture jasper from the Bruneau canyon.

**Rings.** A band plus a finished stone makes jewelry, worn in a Curios ring slot: tumbled stone in silver makes a weak trinket, a cabochon in sterling makes a solid ring, a faceted gem in gold makes the strongest tier. Each stone grants an attribute while worn — jasper heals, garnet hits, sapphire stands firm, Bruneau jasper softens a canyon fall. The **Brilliance** enchantment (I–III, enchanting table) grows any ring's effect by 15% per level. Jewelry wears like armor when you take hits — the band's durability, not the stone's — and Unbreaking and Mending both apply.

**The bench.** A jeweler's hammer draws ingots into wire, and wire bends into bands — the hammer stays put, wearing a little with each draw. Beyond the flavor it's a compatibility guarantee: no CarverCraft recipe uses the four-ingot ring pattern that half of modded Minecraft fights over.

## Adding a stone

Register the items, add textures, models, lang entries, a loot modifier for the host rock, and recipe JSON for the machines it passes through. No machine code changes.

## Building

```
./gradlew build
```

Requires JDK 21. The jar lands in `build/libs/`.

## Dependencies

- [NeoForge](https://neoforged.net/) 21.1.x for Minecraft 1.21.1
- [Curios API](https://github.com/TheIllusiveC4/Curios) — provides the ring slots

## Roadmap

- Mekanism 5x ore processing for silver
- More jewelry enchantments on the Brilliance pattern
- Real textures (everything is generated programmer art for now)

## Contributing / picking this up

See `HANDOFF.md` for project orientation, the verification workflow, and the design
constraints that are load-bearing. `CLAUDE.md` holds current state and decisions;
`DESIGN.md` holds the material and tier design.

## License

All Rights Reserved. The project was generated from the NeoForge MDK; see `TEMPLATE_LICENSE.txt` for the template's own license.
