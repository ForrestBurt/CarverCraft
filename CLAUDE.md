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

See DESIGN.md for the full tier tree. **The tier axis is finish quality, not hardness** —
the same jasper can leave a tumbler as a baroque pebble or a cabbing machine as a domed,
sanded cabochon, and those are not the same object. This came from the developer, who cabs
rocks; the earlier hardness-tier draft was wrong and was replaced.

**Four machines, three finishes:**

| Machine | Power | Max hardness | Transform |
|---|---|---|---|
| Rock Tumbler | none | 7.0 | rough -> tumbled |
| Trim Saw | 20 FE/t | 10.0 | rough -> slab (opaque only) |
| Cabbing Machine | 30 FE/t | 10.0 | slab -> cabochon |
| Faceting Machine | 60 FE/t | 10.0 | rough -> faceted gem (transparent) |

Tumbled -> trinket (silver band, weak). Cabochon -> ring (sterling band). Faceted -> ring
(gold band, strongest). Opaque and transparent stone sets are disjoint, so `X_ring` is
unambiguous. The faceting machine is the only one that takes vanilla diamond, emerald,
and amethyst, and faceted diamond is the best ring in the mod.

**Verified in-game:** silver ore/worldgen/smelting, rough stones from host rock via global
loot modifiers, four-lane machine GUI, Curios rings. `RingItem implements ICurioItem`
returning `Multimap<Holder<Attribute>, AttributeModifier>` is correct for 1.21.1.

**Not yet compiled:** the four-machine refactor. `AbstractLapidaryBlockEntity` holds all
lane/tick/recipe-cache/energy/menu logic, so a machine is a ~50-line subclass declaring
its recipe type, hardness cap, and FE cost. `LapidaryMachineBlock`, one `LapidaryMenu`,
one `LapidaryScreen` (texture read off the block entity). Four recipe types share one
generic `LapidaryRecipeSerializer`. All machines expose ItemHandler; powered ones expose
EnergyStorage, so Mekanism/IE cables and hoppers work with no glue code.

Design decisions to hold:
- **Never facet an opaque stone or cab a transparent one.** That is the whole point.
- **There is no slab in the faceting path.** Slabbing is a cabbing workflow — you slice a
  nodule to trim flat-backed, domed blanks. Faceting rough is preformed and dopped, never
  sawn into sheets; diamond is cleaved/laser-sawn then bruted, emerald is too included to
  risk. Transparent stones go from rough straight into the faceting machine. Do not
  reintroduce transparent slabs.
- The tumbler is the only hardness gate (7.0). Everything downstream runs to 10 because a
  diamond blade cuts anything; those gates are cost and power. An earlier draft capped the
  saw at 8, which made corundum unobtainable.
- Only stones at or below 7.0 can be tumbled: agate, jasper, carnelian, rose quartz,
  malachite, peridot. Garnet, topaz, ruby, sapphire, and star garnet cannot.
- The original `ruby` item is preserved as the faceted end of the ruby chain.
- Star garnet is the endgame: Idaho's state gem, drawn with its asterism, rarest drop,
  only ring with two modifiers.
- **Do not reintroduce** the circular progress ring or batch processing.

Project hygiene: MDK example scaffolding stripped. `TEMPLATE_LICENSE.txt` kept on purpose.

**`.github/workflows/build.yml` runs `./gradlew build` on every push** — a free compile
check. This is the verification step for anything written without a local compiler.

**Power / Mekanism.** No integration code exists or is needed: the machines expose
`Capabilities.EnergyStorage.BLOCK` (Forge Energy) and Mekanism's Universal Cables speak
FE, so the capability system handles it. Mekanism is pulled in as `localRuntime` only
(CurseMaven, project 268560, file id in gradle.properties) purely so cables and
generators exist in-game to test against — it is not a compile dependency and is not
published. If it ever fails to resolve, comment that one line out; nothing depends on it.

A **Creative Charger** block (creative tab only, unbreakable, no recipe) pushes 100k FE/t
into all six neighbours and exposes itself as an infinite source. It exists so the powered
machines are testable without any power mod at all. Keep it creative-only.

Common-namespace tags now cover silver, the three alloys, and the six faceted gems
(`c:ingots/*`, `c:gems/*`, `c:ores/silver`, `c:raw_materials/silver`), so materials unify
with other mods. Full Mekanism 5x ore processing for silver is NOT implemented — that
would need dust, dirty dust, clump, shard, crystal, and two slurry chemicals per metal,
which is a project of its own.

TESTING VALUES to dial back: silver count=40/size=12/band -48..80 (release: 6/8/-48..32).

Textures are placeholder programmer art. Next: an Alloyer so alloys stop being plain
crafting, grit/polish as consumables, data-driven jewelry enchantments, Bruneau jasper.
