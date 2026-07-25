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

See DESIGN.md for the full tier tree. Short version: **Metallurgy's structure — a broad
material roster arranged into tiers gated by machines — applied to lapidary, with Mohs
hardness as the tier axis and every material real.**

**Verified in-game (v0.1/v0.2):** silver ore + worldgen + smelting; rough stones from host
rock via global loot modifiers; the Rock Tumbler as four independent lanes; Curios rings
with attribute modifiers. `RingItem implements ICurioItem` returning a
`Multimap<Holder<Attribute>, AttributeModifier>` is confirmed correct for 1.21.1.

**Written this pass, NOT yet compiled** — the largest single change so far:
- `LapidaryRecipe` interface + one generic `LapidaryRecipeSerializer`; three types
  (`tumbling`, `sawing`, `faceting`). Every recipe carries `time` and `hardness`.
- `AbstractLapidaryBlockEntity` holds all lane/tick/recipe-cache/energy/menu logic.
  A new machine is now a ~50-line subclass declaring its recipe type, hardness cap,
  and FE cost. `LapidaryMachineBlock` and one shared `LapidaryMenu`/`LapidaryScreen`
  do the same for blocks and UI — the screen picks its texture off the block entity.
- **Trim Saw** (20 FE/t, 20k buffer) and **Faceting Station** (40 FE/t, 40k buffer),
  both exposing `Capabilities.EnergyStorage.BLOCK` and `Capabilities.ItemHandler.BLOCK`,
  so Mekanism/IE cables and hoppers work with no glue code.
- Roster: 11 stones. Opaque (agate, jasper, carnelian, rose quartz, malachite) tumble
  rough→polished. Transparent (garnet, peridot, topaz, sapphire, ruby, star garnet) go
  rough→preform→faceted. 13 rings, silver band for tumbled stones and gold for faceted.
- Alloys as plain crafting for now: sterling silver, electrum, rose gold.

Design decisions to hold:
- **The tumbler is the only hardness gate (7.0).** The saw and faceting station both run
  to 10 because a diamond blade cuts anything — their gate is power plus a second machine.
  An earlier draft capped the saw at 8, which made corundum unobtainable.
- Garnet moved from the tumbler path to the faceted path (7.5 is above the tumbler cap),
  so `polished_garnet` is gone and the garnet ring takes `faceted_garnet`.
- The original `ruby` item is preserved as the faceted end of the ruby chain rather than
  deleted — rough_ruby → ruby_preform → ruby.
- Star garnet is the endgame: Idaho's state gem, asterism from rutile inclusions, the only
  ring carrying two attribute modifiers, and the rarest drop in the mod.
- **Do not reintroduce** the circular progress ring or batch processing.

Project hygiene: MDK example scaffolding is stripped. `TEMPLATE_LICENSE.txt` is kept on
purpose — it's the MDK's own MIT notice and the gradle scaffolding is template-derived.

**`.github/workflows/build.yml` runs `./gradlew build` on every push.** That is a free
compile check — after pushing, the Actions tab says whether the code compiles. This is
the verification step for anything written without a local compiler.

TESTING VALUES to dial back before release: silver count=40/size=12/band -48..80
(release: 6/8/-48..32).

Textures are placeholder programmer art throughout. Next: an Alloyer/Crucible machine so
alloys stop being plain crafting, the cabochon path (slab→preform→cab), data-driven
jewelry enchantments, and Bruneau jasper as a distinct jasper variety.
