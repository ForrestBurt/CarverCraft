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
**v0.1 and v0.2 complete and verified in-game.**

- Silver ore + worldgen + smelting; rough gems from andesite/granite/basalt via a global loot modifier.
- **Rock Tumbler**: four independent lanes, each furnace-shaped (input slot -> horizontal progress arrow -> output slot) on its own clock. Output slots are take-only; a lane stalls if its output is full or mismatched. Particles fire off a RUNNING blockstate (vanilla LIT) the ticker flips. GUI is 176x190.
- **Curios rings**: two ring slots via datapack, items bound through the vanilla tag `data/curios/tags/item/ring.json`. `RingItem implements ICurioItem` returning a `Multimap<Holder<Attribute>, AttributeModifier>` — confirmed correct for 1.21.1 (Curios only moved to `ItemAttributeModifiers` in 1.21.4+). Silver band + jasper (+4 max health), garnet (+1 attack damage), agate (+2 armor), ruby (+1 luck).
- **Tumbling is data-driven** (this pass, not yet compiled): custom `RecipeType` + `RecipeSerializer` using vanilla `SingleRecipeInput`, recipes at `data/carvercraft/recipe/tumbling/`. Each recipe carries its own `time`, so harder stones tumble longer — times track Mohs hardness (agate 400t, jasper 500t, garnet 600t). The old hardcoded `polishedResult()` if-chain is gone. Recipes are resolved once per lane and cached on the input item, since a RecipeManager lookup every tick per lane per tumbler would be real cost. ContainerData now carries 4 lane clocks + 4 per-lane totals.

Design notes worth keeping:
- The tumbler was originally a single 2x2 batch with a circular progress ring. The ring had two bugs (bottom-up reveal instead of a sweep; it overlapped the 4th slot) and batching was the wrong model. **Do not reintroduce the ring or batch processing.**
- `loadAdditional` force-resizes the item handler to 8 slots so tumblers saved by the old 4-slot build don't shrink it and blow up lane indexing.
- Adding a stone now takes: one item registration in ModItems, a texture, a model, a lang line, and one recipe JSON. No machine code changes. The Trim Saw and Faceting Station should copy this recipe pattern rather than inventing their own.

TESTING VALUES to dial back before release: silver count=40/size=12/band -48..80 (release: 6/8/-48..32). Tumbling times are also short for testing.

Project hygiene: the NeoForge MDK example scaffolding has been stripped — no `Config.java` (the dirt-block/magic-number sample), one client class instead of two, no `examplemod.*` lang keys, `neoforge.mods.toml` and `build.gradle` cleared of template comment blocks, real README. `TEMPLATE_LICENSE.txt` is deliberately kept: it's the MDK's own MIT notice and the gradle scaffolding is still template-derived.

**`.github/workflows/build.yml` runs `./gradlew build` on every push.** That is a free compile check — after pushing, the repo's Actions tab says whether the code actually compiles. Use it to verify work written without a local compiler.

Textures are placeholder programmer art throughout. Next: v0.3 (Trim Saw + Faceting Station, FE capability + their own recipe types), star garnet / Bruneau jasper content, or data-driven jewelry enchantments.
