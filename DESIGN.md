# CarverCraft — Material & Tier Design

The organizing idea, borrowed from Metallurgy: **a broad roster of materials arranged
into tiers, gated by machines.** Where Metallurgy invented forty fantasy metals, every
material here is real — and so is every process.

The tier axis is **finish quality**, not hardness. That's how the craft actually works:
the same piece of jasper can come out of a tumbler as a baroque pebble or off a cabbing
machine as a domed, sanded, polished cabochon, and those are not the same object.

---

## The three finishes

**Tumbled — the cheap tier.** Rough rock goes in a barrel with grit and comes out
smooth and rounded. No sawing, no shaping, no skill, no power. Weeks of waiting for a
baroque pebble. Makes trinkets.

```
rough stone --[Rock Tumbler]--> tumbled stone --> trinket
```

**Cabbed — the working tier.** Saw a slab, grind it to a domed preform, sand it up
through the grits, polish. This is what you do with opaque stone: jasper, agate,
malachite. A cabochon is a finished piece of jewelry stock.

```
rough stone --[Trim Saw]--> slab --[Cabbing Machine]--> cabochon --> ring
```

**Faceted — the precision tier.** Transparent gems only. Cut against an index wheel
so the facets return light instead of scattering it. The most expensive machine and
the best gear, and it's the only thing that will do anything worthwhile with a diamond.

```
rough gem --[Faceting Machine]--> faceted gem --> ring
```

**Note there is no slab in the faceting path, and that is deliberate.** Slabbing is a
cabbing workflow: you slice a nodule into flat sheets so you can trace and trim cabochon
blanks with a flat back and a domed top. A faceter never wants a slice — the goal is to
preserve as much weight and clarity from the crystal as possible, so faceting rough gets
preformed on a lap and dopped, not sawn into sheets. Diamond is cleaved or laser-sawn
along the grain and then bruted round; emerald is so included that the step cut is named
for the compromise. Neither goes anywhere near a slab saw.

You would never facet jasper either. It's opaque — there's nothing for the facets to do.

The result is an honest asymmetry: the cabbing path uses two cheap machines and more
steps, the faceting path one expensive machine and one long step.

## The machines

| Machine | Power | Max hardness | Transform | Consumes |
|---|---|---|---|---|
| **Rock Tumbler** | none | 7.0 | rough → tumbled | grit, 1 per lane cycle |
| **Trim Saw** | 20 FE/t | 10.0 | rough → slab (opaque) | — |
| **Cabbing Machine** | 30 FE/t | 10.0 | slab → cabochon | polish, 1 per cabochon |
| **Faceting Machine** | 60 FE/t | 10.0 | rough → faceted gem (transparent) | — |
| **Alloyer** | 80 FE/t | n/a | two metals → alloy | — |

The tumbler is the only hardness gate, and it's a real one: silicon carbide grit in a
rubber barrel tops out around quartz. You genuinely cannot tumble a sapphire. Everything
downstream of the trim saw runs to 10 because a diamond blade cuts anything — those
gates are cost and power, not hardness.

The faceting machine is deliberately the expensive one. It is also the only machine
that accepts vanilla diamonds, emeralds, and amethyst.

## Shop supplies

The tumbler and the cabbing machine consume what the real machines consume.
**Silicon carbide grit** is 2 sand + any coal → 4 — the Acheson process, which really is
just quartz sand and carbon in an electric furnace. **Polishing compound** is jeweler's
rouge, red iron oxide: raw iron ground fine with grit, 1 + 1 → 4. The saw and the
faceting machine consume nothing; diamond blades and charged laps wear on a scale no
per-cut item models honestly.

Grit is charged when a lane's clock leaves zero — into the barrel at the start of the
tumble, like the real thing — so running lanes finish even if the supply slot runs dry
mid-cycle.

## Stone roster

### Opaque — sawn and cabbed

| Stone | Mohs | Found in | Tumbles? | Ring effect |
|---|---|---|---|---|
| Agate | 7.0 | basalt | yes | +2 armor |
| Jasper | 7.0 | andesite | yes | +4 max health |
| **Bruneau Jasper** | 7.0 | tuff, rare | yes | +2 safe fall distance |
| Carnelian | 7.0 | tuff | yes | +attack speed |
| Rose Quartz | 7.0 | granite | yes | +4 absorption |
| Malachite | 4.0 | copper ore | yes | +2 armor toughness |

**Bruneau jasper** is the mod's second Idaho signature: picture jasper from the Bruneau
river canyon, porcelain tan with landscape banding. It forms in rhyolitic volcanics, so
tuff is the closest vanilla host — a geology judgment call, open to veto. The canyon
stone takes the edge off the canyon: longer safe falls. One modifier only; two belongs
to the star garnet.

### Transparent — sawn and faceted

| Stone | Mohs | Found in | Tumbles? | Ring effect |
|---|---|---|---|---|
| Peridot | 7.0 | basalt | yes | +10% movement speed |
| Garnet (almandine) | 7.5 | granite | no | +1.5 attack damage |
| Topaz | 8.0 | deepslate | no | +1 luck |
| Ruby (corundum) | 9.0 | deepslate | no | +2.5 attack damage |
| Sapphire (corundum) | 9.0 | deepslate | no | +knockback resistance |
| **Star Garnet** | 7.5 | deepslate, rare | no | the endgame stone |

**Star garnet** is the one that matters. Idaho's state gem, found in commercial quantity
in exactly two places on Earth — Idaho and India. The asterism comes from rutile
inclusions, which is why it sits at 7.5 and is still the rarest thing in the mod.

### Vanilla gems — faceting only

Diamond, emerald, and amethyst go straight to the faceting machine. Faceted diamond is
the best ring in the mod.

## Bands and jewelry

| Band | Takes | Makes | Durability |
|---|---|---|---|
| Silver | tumbled stones | trinket — weak | 150 |
| Sterling Silver | cabochons | ring — solid | 250 |
| Gold | faceted gems | ring — strongest | 350 |

Jewelry wears like armor: every equipped piece takes durability when its wearer takes
a hit armor would care about (armor's own formula, max(1, damage/4)), and damage that
bypasses armor — falls included — spares it. Durability follows the band because
that's what breaks on a real ring: the setting, not the stone. Unbreaking and Mending
apply, and a broken ring takes its bonus with it.

Opaque and transparent stones are disjoint sets, so a stone's ring is unambiguous:
jasper's comes from a cabochon, sapphire's from a faceted gem.

## Alloys

Real jeweller's alloys at real ratios, melted in the Alloyer — order-agnostic across
its two slots, and the silver side takes any `c:ingots/silver`, so other mods' silver
melts fine.

| Alloy | Melt |
|---|---|
| Sterling Silver | 7 silver + 1 copper → 8 |
| Electrum | 1 silver + 1 gold → 2 |
| Rose Gold | 3 gold + 1 copper → 4 |

## Enchantments

**Brilliance I–III** — a better polish returns more light: the ring's inherent effect
grows 15% per level. Defined entirely in JSON (`data/carvercraft/enchantment/`), rolls
at the enchanting table on `#carvercraft:rings`. Future jewelry enchantments should
follow the same pattern: datapack definition, and code only where a Curios slot makes
vanilla's effect plumbing unreachable.

## Not yet built

- Mekanism 5x ore processing for silver (dust, dirty dust, clump, shard, crystal, slurries)
- More enchantments on the Brilliance pattern
- Real textures — everything is programmer art from the generators in `tools/`
