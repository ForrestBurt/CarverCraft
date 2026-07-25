# HANDOFF

How to pick up CarverCraft in a new chat, in Cowork, or in Claude Code.

**Repo:** https://github.com/ForrestBurt/CarverCraft (public)

---

## 1. Read these first, in this order

| File | What it is |
|---|---|
| `CLAUDE.md` | The project brain. Current state, locked decisions, and *why*. Read all of it. |
| `DESIGN.md` | The material and tier design. Read before touching recipes, machines, or stones. |
| `README.md` | Player-facing description of what the mod does. |

`CLAUDE.md` is loaded automatically by Claude Code. In a web chat, paste it or point at the repo.

## 2. What this is

A lapidary and jewelry mod for **Minecraft 1.21.1 / NeoForge 21.1.242**, built from the
NeoForge MDK with ModDevGradle. Java 21. Mod id `carvercraft`, package
`com.forrestb.carvercraft`.

Find rough stone in the world, finish it three different ways, set it in metal, wear it.
The organizing idea is Metallurgy's — a broad material roster in tiers — but the tier axis
is **finish quality** (tumbled / cabbed / faceted), and every material and process is real.

Dependencies: **Curios API** (required, provides ring slots) and **Mekanism**
(dev runtime only, so there's something to test power against).

## 3. How to work on it

### Claude Code or Cowork (preferred)
Direct file access. Open the repo, edit in place, run `./gradlew build` locally. `CLAUDE.md`
is picked up automatically by Claude Code. For a Java/Gradle project Claude Code is the more
natural fit; Cowork can drive it as a tool.

### Web chat (the loop that built this)
Claude can't reach a local disk, so:

1. You push to GitHub.
2. Claude clones the public repo and works there.
3. Claude hands back a **git bundle**.
4. You pull it:
   ```
   git pull ~/Downloads/<bundle-name>.bundle main
   ```
5. You push again. Repeat.

Bundles contain the **complete history**, not a diff, so they're safe to pull out of order
or skip. If you've already got the commits, the pull just fast-forwards.

## 4. How to verify — this matters

**`.github/workflows/build.yml` runs `./gradlew build` on every push.**

Most of this code was written without a compiler available. Push, then open the repo's
**Actions** tab. Green check means it compiles; red X gives the exact javac error to paste
back. This is faster than a local Gradle sync and it is the standard verification step.

Assume anything recently written is **compile-unverified until CI says otherwise.**

Testing power in-game: place a **Creative Charger** (creative tab, unbreakable, no recipe)
against a machine. It pushes 100k FE/t into all six neighbours. Use it before blaming your
machines on Mekanism — it isolates them from a 200-block dependency.

## 5. Current state

Verified in-game: silver ore and worldgen, rough stones dropping from host rock, the
four-lane machine GUI, Curios rings with working attribute modifiers.

Written but not yet play-tested: the four-machine refactor (Rock Tumbler, Trim Saw,
Cabbing Machine, Faceting Machine), the 12-stone roster (Bruneau jasper included), the
Alloyer and its alloying recipes, the grit/polish consumable economy, the Brilliance
enchantment, trinkets, the creative charger, and Mekanism in the dev runtime.

Run `python3 tools/validate.py` before every commit — it cross-references registry
symbols, models, textures, lang, recipes, loot modifiers, and the Curios/Brilliance
ring tags. It exists because the creative tab once survived a refactor with fourteen
stale symbols and nothing noticed until CI.

## 6. Architecture worth knowing before editing

- `AbstractLapidaryBlockEntity` holds **all** machine logic: four independent lanes,
  ticking, recipe resolution and caching, energy, saving, menu. A new machine is a ~50-line
  subclass declaring its recipe type, hardness cap, and FE cost. Add machines there, not by
  copying an existing one.
- `LapidaryMachineBlock`, one `LapidaryMenu`, one `LapidaryScreen` do the same job for
  blocks and UI. The screen reads its background texture off the block entity.
- Four recipe types (`tumbling`, `sawing`, `cabbing`, `faceting`) share one generic
  `LapidaryRecipeSerializer`. Recipes carry `time` and `hardness`.
- Adding a stone: register items in `ModItems`, draw textures, add models, lang, a loot
  modifier, and recipe JSON. **No machine code changes.**

## 7. Landmines — read before "improving" anything

These all cost real debugging to find. Several look like bugs and are not.

1. **Never facet an opaque stone or cab a transparent one.** That constraint is the design.
   Jasper has nothing for facets to do; cabbing a diamond throws away its value.
2. **There is no slab in the faceting path, deliberately.** Slabbing is a cabbing workflow.
   Faceting rough is preformed and dopped, never sawn into sheets. Don't symmetrize the two
   paths back together.
3. **The tumbler is the only hardness gate (7.0).** Everything downstream runs to 10 because
   a diamond blade cuts anything. An earlier draft capped the saw at 8 and made corundum
   unobtainable.
4. **`RingItem.getAttributeModifiers` returns a `Multimap`, and that is correct for 1.21.1.**
   Curios moved to `ItemAttributeModifiers` in 1.21.4+. Do not "fix" this.
5. **`loadAdditional` force-resizes the item handler to `SLOT_COUNT` (now 9: 8 lane
   slots + the shared consumable slot).** That's deliberate — machines saved by older
   builds would otherwise size it differently and blow up lane indexing. If slots are
   ever added again, bump the constant; never remove the resize.
6. **`LapidaryMenu` reads the block pos exactly once** from the network buffer, via a private
   delegating constructor. Reading it twice desyncs the packet.
7. **GUI sheets must stay 256×256.** The default `blit` overload assumes that size; a taller
   sheet silently mis-samples every coordinate.
8. **Do not reintroduce** the circular progress ring (it mis-rendered and overlapped a slot)
   or batch processing (lanes are independent for good reasons).
9. **Bands come from wire, never from ingots.** The four-ingot ring pattern collides
   with half of modded Minecraft. `hammer + ingot -> 2 wire`, `4 wire -> band`. The
   extra step is the compatibility guarantee, not busywork.
10. **Faceted ring band layers are normalised against their ramp's brightest colour.**
   Normalise against the mid tone instead and the highlight clamps to white and can't
   be recovered. `tools/gen_ring_layers.py` prints the gold round-trip error — it must
   be 0.
11. **`JewelerStallInjector` reflects into `StructureTemplatePool.templates` and
   `rawTemplates`.** There is no vanilla/NeoForge API for appending to another
   namespace's template pool; this is the established pattern. It fails soft (log,
   no stalls) if the field names drift. Don't "fix" it into overwriting minecraft's
   houses.json — that would fight every other village mod.
12. **`TEMPLATE_LICENSE.txt` stays.** It's the MDK's own MIT notice and the gradle scaffolding
   is still template-derived.

## 8. Known-unfinished, roughly in priority order

1. **Revert the testing values before any release.** Silver worldgen is cranked to
   `count=40, size=12, band -48..80`; release values are `6 / 8 / -48..32`. Flagged in
   `CLAUDE.md`.
2. **Play-test the whole chain** end to end: the four lapidary machines (now with grit
   and polish in the consumable slot), the Alloyer melting the three alloys, the
   diamond ring, Bruneau jasper from tuff, Brilliance at the enchanting table, and
   jewelry durability — take hits until a trinket breaks, confirm falls don't wear it.
   Also new: electrum/rose gold ring variants (tooltip line, enchantability, max
   damage), the 1-in-100 drop rates, the jeweler villager claiming a cabbing machine
   and leveling through trades, and the stall generating in a NEW world's villages.
   Newest: the hammer surviving crafts and dying at 256, wire -> band, electrum and
   rose gold rings actually LOOKING like their metal, and recipes appearing in the
   recipe book as you pick up their ingredients.
3. **Mekanism 5x ore processing for silver** — needs dust, dirty dust, clump, shard, crystal,
   and two slurry chemicals. A project of its own, not a few recipe files.
4. **More jewelry enchantments** on the Brilliance pattern: datapack JSON first, code
   only where the Curios slot makes vanilla's effect plumbing unreachable.
5. **Real textures.** Everything is programmer art generated with Pillow; the newer
   sets have their generators committed under `tools/`.

## 9. Working style that produced this

- Small, runnable steps. The client should boot cleanly after every change.
- Validate content cross-references with a script before committing — every item having a
  model, texture, lang key, and no orphans. This has caught real breakage repeatedly.
- Explain load-bearing Minecraft/Java idioms (capabilities, client/server siding, registries,
  the event bus) briefly; skip general engineering explanation.
- When a design decision gets made in chat, write it into `CLAUDE.md` with the reasoning.
  The reasoning is the part no codebase scan can recover.
