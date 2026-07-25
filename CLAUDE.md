# CLAUDE.md — CarverCraft

## Project
Lapidary & jewelry tech mod for Minecraft 1.21.1 on NeoForge (21.1.x), built from the MDK-1.21.1-ModDevGradle template. Mod id: `carvercraft`. Java 21, official Mojang mappings.

## Vision
A gem pipeline from geology to magic: find and tumble rough gems → cut them → silversmith jewelry → enchanted rings and amulets worn in Curios slots. Tone: geologically honest wherever possible. The developer is an amateur lapidary — real-world accuracy in rock/gem pairings is a feature, not flavor text.

## Locked design decisions
- **Curios API** is the baubles system (Baubles died with 1.12; Curios is the ecosystem standard). Rings/amulets/charms register into Curios slots; equipped effects use attribute modifiers.
- **Jewelry effects** use 1.21's data-driven enchantments (JSON) wherever possible; write code only when JSON can't express the effect.
- **Silver ore** worldgen is pure JSON: configured feature + placed feature + biome modifier. No worldgen code.
- **Rough gems drop from stone types** via NeoForge Global Loot Modifiers — e.g. andesite → jasper, granite → garnet, basalt → agate. Keep pairings geologically plausible; defer to the developer on geology.
- **Vanilla gems integrate**: lapis, emerald, diamond, amethyst (plus the mod's ruby) can be cut into faceted forms. High content-per-effort since players already have these materials.
- **Rock Tumbler** (first machine): passive, zero energy, deliberately slow — real tumbling takes weeks, and the in-game slowness is the flavor. No GUI in v0.1; right-click interaction only.
- **Trim Saw & Faceting Station** (later): FE-powered through capabilities so Mekanism/IE cables interoperate with zero glue code; full GUIs; JSON recipe types.
- **Idaho identity**: the endgame gem is the **star garnet** (Idaho's state gem, found in roughly two places on Earth), and **Bruneau jasper** exists as a distinct jasper variety. This is the mod's signature. Keep it.

## Build order
- **v0.1** — silver ore + deepslate variant, rough-gem loot drops, Rock Tumbler (block + block entity + server ticker), cut gem items
- **v0.2** — Curios dependency, silver ingot/band crafting, first rings (band + cut gem determines effect), attribute modifiers while equipped
- **v0.3** — Trim Saw + Faceting Station: FE buffers, Menu/Screen/ContainerData GUIs, custom recipe types
- **v0.4** — data-driven jewelry enchantments, jasper varieties, star garnet endgame content

## Conventions
- Registration via DeferredRegister. As content grows, split into ModItems / ModBlocks / ModBlockEntities / ModCreativeTabs classes instead of one god-class.
- Assets under `assets/carvercraft/`, data under `data/carvercraft/`.
- Hand-written JSON is fine early; move to datagen once item count justifies it.
- Machine logic is server-side only; never trust the client. Sync display state through ContainerData or block entity data packets.
- Keep change sets small and runnable — the Client run config should boot cleanly after every step.
- The existing `ruby` item is the hello-world artifact; fold it into the real gem set (rough ruby → cut ruby) rather than deleting it.

## Developer context
Experienced infrastructure engineer (Linux/HPC, strong git/CLI), newer to Java, Gradle, and Minecraft modding specifically. When a Java-ecosystem or Minecraft-specific idiom is load-bearing (capabilities, client/server siding, registries, the event bus), briefly explain the why. Skip explanations of general engineering concepts.

## Current state
**v0.1 complete and verified in-game.** Silver ore + worldgen + smelting, rough gems from andesite/granite/basalt via GLM, and the Rock Tumbler as **four independent lanes** (input slot -> horizontal progress arrow -> output slot, each on its own clock). Output slots are take-only; a lane stalls if its output is full or mismatched. Particles fire off a RUNNING blockstate (vanilla LIT) the ticker flips. GUI is 176x190.

Design notes worth keeping:
- The tumbler was originally a single 2x2 batch with a circular progress ring. The ring had two bugs (bottom-up reveal instead of a sweep; it overlapped the 4th slot) and batching was the wrong model. Horizontal furnace arrows + per-lane clocks replaced both. **Do not reintroduce the ring or batch processing.**
- `loadAdditional` force-resizes the item handler to 8 slots so tumblers saved by the old 4-slot build don't shrink it and blow up lane indexing.

**v0.2 written but NOT yet compiled** (first build with a third-party dependency): Curios API 9.5.1+1.21.1 via maven.theillusivec4.top, `compileOnly` for the `:api` classifier and `localRuntime` for the full jar, following the template's own JEI idiom. Two ring slots granted to players via datapack (`curios/slots/ring.json` + `curios/entities/player.json`), items bound through the vanilla tag `data/curios/tags/item/ring.json`. `RingItem implements ICurioItem` and returns a `Multimap<Holder<Attribute>, AttributeModifier>` from `getAttributeModifiers(SlotContext, ResourceLocation, ItemStack)` — that signature is the single most likely thing to need adjusting, since Curios moved from Multimap to ItemAttributeModifiers in 1.21.4+ and this targets 1.21.1.

Rings: silver band (crafting base, no effect) + jasper (+4 max health), garnet (+1 attack damage), agate (+2 armor), ruby (+1 luck). Craft the band from 4 silver ingots in a diamond, then shapeless band + polished gem.

TESTING VALUES to dial back before release: silver count=40/size=12/band -48..80 (release: 6/8/-48..32); TICKS_PER_GEM=30s.

Textures are placeholder programmer art throughout. Next after rings compile: v0.3 (Trim Saw + Faceting Station, FE + recipes) or data-driven jewelry enchantments.
