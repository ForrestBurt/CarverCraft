# CarverCraft — Material & Tier Design

The organizing idea, borrowed from Metallurgy: **a broad roster of materials, arranged
into tiers, gated by processing machines.** Where Metallurgy invented forty fantasy
metals, every material here is real, and the tier axis is real too — **Mohs hardness**.

Harder stone needs harder abrasive and better equipment. That isn't a balance knob,
it's how the craft actually works.

---

## The two paths

Real lapidary splits along stone transparency, and so does this mod.

**Opaque stones get tumbled.** Chalcedony, jasper, malachite. Rough rock goes in a
barrel with grit and comes out smooth. No cutting required.

```
rough stone --[Rock Tumbler]--> polished stone
```

**Transparent stones get cut.** You saw a preform off the rough, then facet it.
Tumbling a sapphire would be a waste of a sapphire.

```
rough stone --[Trim Saw]--> preform --[Faceting Station]--> faceted gem
```

## The machines are the tiers

| Machine | Power | Max hardness | Transform |
|---|---|---|---|
| **Rock Tumbler** | none (passive) | 7.0 | rough → polished |
| **Trim Saw** | FE | 10.0 | rough → preform |
| **Faceting Station** | FE | 10.0 | preform → faceted |

The tumbler is deliberately the free entry tier — a rubber barrel and silicon carbide
grit. It tops out at quartz hardness because that's roughly where cheap abrasive stops
being useful, and it is the mod's real hardness gate: you genuinely cannot tumble a
sapphire.

The saw and the faceting station both run to 10, because a diamond blade cuts anything.
Their gate is power and a second machine, not hardness. Recipes still carry a hardness
value so a future hand-tool tier (or a harder abrasive progression) has something to
read.

## Stone roster

### Opaque — tumbler path

| Stone | Mohs | Found in | Ring effect |
|---|---|---|---|
| Agate | 7.0 | basalt | +2 armor |
| Jasper | 7.0 | andesite | +4 max health |
| Carnelian | 7.0 | tuff | +1 attack speed |
| Rose Quartz | 7.0 | granite | regeneration-adjacent |
| Malachite | 4.0 | copper ore | +mining speed |

Malachite is a copper carbonate that forms in oxidized copper deposits, so it drops
from copper ore rather than a stone type. Softest thing on the list by a wide margin.

### Transparent — saw and facet path

| Stone | Mohs | Found in | Ring effect |
|---|---|---|---|
| Garnet (almandine) | 7.5 | granite | +1 attack damage |
| Peridot (olivine) | 7.0 | basalt | +movement speed |
| Topaz | 8.0 | deepslate | +luck |
| Ruby (corundum) | 9.0 | deepslate | +2 attack damage |
| Sapphire (corundum) | 9.0 | deepslate | +knockback resistance |
| **Star Garnet** | 7.5 | deepslate, rare | the endgame stone |

**Star garnet** is the one that matters. Idaho's state gem, and it is found in
commercial quantity in exactly two places on Earth — Idaho and India. The asterism
comes from rutile inclusions, not hardness, which is why it sits at 7.5 and is still
the rarest thing in the mod. It should be hard to find and worth the trip.

## Metals and alloys

The Metallurgy alloy system, except the alloys are ones a jeweler actually uses.

| Alloy | Recipe | Real? |
|---|---|---|
| Sterling Silver | 7 silver + 1 copper | yes — 92.5% silver is the standard |
| Electrum | silver + gold | yes — naturally occurring, used since antiquity |
| Rose Gold | 3 gold + 1 copper | yes — copper is what makes it pink |

## Band tiers

The band scales what the stone is worth, which keeps the combination count sane —
one band tier per processing tier rather than every metal against every stone.

| Band | Takes | Effect |
|---|---|---|
| Silver | polished (tumbled) stones | base |
| Gold | faceted gems | amplified |

## Not yet built

- Cabochon path: slab → preform → cab, the workflow between tumbling and faceting
- An Alloyer/Crucible machine (alloys are plain crafting recipes for now)
- Data-driven jewelry enchantments
- Bruneau jasper as a distinct jasper variety
