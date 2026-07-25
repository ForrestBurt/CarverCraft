# CarverCraft

A lapidary and jewelry mod for Minecraft 1.21.1 (NeoForge).

Find rough stones in the world, tumble them smooth, silversmith them into rings, and wear them.

## Current content

**Silver.** Silver ore generates through the overworld with a deepslate variant. Smelt or blast raw silver into ingots. Tagged into the `c:` namespace so it unifies with other mods' silver.

**Rough stones.** Andesite, granite, and basalt occasionally drop rough jasper, garnet, and agate when mined — handled by a global loot modifier rather than overwriting vanilla loot tables, so it stacks with other mods.

**The Rock Tumbler.** Four independent lanes, each shaped like a furnace: input slot, progress arrow, output slot. Every lane runs its own clock, so loading lane three doesn't disturb lane one. Passive and unpowered by design — real tumbling takes about six weeks, and the slow clock is the point.

Tumbling is data-driven. Recipes live in `data/carvercraft/recipe/tumbling/` and each carries its own `time`, so harder stones stay on the wheel longer. The shipped times track Mohs hardness.

**Rings.** A silver band plus a polished stone makes a ring, worn in a Curios ring slot. Each stone grants an attribute while equipped, following the traditional lapidary associations:

| Ring | Effect |
|---|---|
| Jasper — "the supreme nurturer" | +4 max health |
| Garnet — vitality | +1 attack damage |
| Agate — stability | +2 armor |
| Ruby — passion | +1 luck |

## Adding a stone

Register the item, add a texture, a model, and a lang entry, then drop a recipe JSON into `data/carvercraft/recipe/tumbling/`. No machine code changes.

## Building

```
./gradlew build
```

Requires JDK 21. The jar lands in `build/libs/`.

## Dependencies

- [NeoForge](https://neoforged.net/) 21.1.x for Minecraft 1.21.1
- [Curios API](https://github.com/TheIllusiveC4/Curios) — provides the ring slots

## Roadmap

- Trim Saw and Faceting Station: powered machines with their own recipe types
- Data-driven jewelry enchantments
- Star garnet and Bruneau jasper

## License

All Rights Reserved. The project was generated from the NeoForge MDK; see `TEMPLATE_LICENSE.txt` for the template's own license.
