#!/usr/bin/env python3
"""Generate CarverCraft's story advancement tree, its stage tags, and its lang keys.

The recipe-book advancements (tools-generated elsewhere) only unlock recipes. This is
the tab a player actually reads: it walks the mod's real workflow — find rough, draw
wire, bend a band, then pick a finish — and each node is gated on the step before it,
so the tree itself is the tutorial.

Triggers are deliberately boring. `inventory_changed` on an item or tag is the most
stable trigger in the game and cannot silently stop firing the way a location or block
predicate can; the few exceptions (enchanting, trading) are noted at their nodes.

Run from the repo root.
"""

import json
import os

R = "src/main/resources"
ADV = f"{R}/data/carvercraft/advancement"
TAGS = f"{R}/data/carvercraft/tags/item"
LANG = f"{R}/assets/carvercraft/lang/en_us.json"


def write(path, obj):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w") as f:
        json.dump(obj, f, indent=2, ensure_ascii=False)
        f.write("\n")


# ------------------------------------------------------------- stage item tags
# Useful well beyond advancements: anything wanting "all rough stone" now has it.

STAGE_TAGS = {
    "rough_stones": ["rough_agate", "rough_jasper", "rough_bruneau_jasper", "rough_carnelian",
                     "rough_rose_quartz", "rough_malachite", "rough_peridot", "rough_garnet",
                     "rough_topaz", "rough_ruby", "rough_sapphire", "rough_star_garnet"],
    "tumbled_stones": ["tumbled_agate", "tumbled_jasper", "tumbled_bruneau_jasper",
                       "tumbled_carnelian", "tumbled_rose_quartz", "tumbled_malachite",
                       "tumbled_peridot"],
    "slabs": ["agate_slab", "jasper_slab", "bruneau_jasper_slab", "carnelian_slab",
              "rose_quartz_slab", "malachite_slab"],
    "cabochons": ["agate_cabochon", "jasper_cabochon", "bruneau_jasper_cabochon",
                  "carnelian_cabochon", "rose_quartz_cabochon", "malachite_cabochon"],
    "faceted_gems": ["faceted_peridot", "faceted_garnet", "faceted_topaz", "ruby",
                     "faceted_sapphire", "faceted_star_garnet", "faceted_diamond",
                     "faceted_emerald", "faceted_amethyst"],
    "wires": ["silver_wire", "sterling_silver_wire", "gold_wire", "electrum_wire",
              "rose_gold_wire"],
    "bands": ["silver_band", "sterling_silver_band", "gold_band", "electrum_band",
              "rose_gold_band"],
    "trinkets": ["agate_trinket", "jasper_trinket", "bruneau_jasper_trinket",
                 "carnelian_trinket", "rose_quartz_trinket", "malachite_trinket",
                 "peridot_trinket"],
    "cabochon_rings": ["agate_ring", "jasper_ring", "bruneau_jasper_ring", "carnelian_ring",
                       "rose_quartz_ring", "malachite_ring"],
}

# Items the village jeweler is the only source of *selling* — used to make the trade
# advancement specific without a villager-profession predicate, which vanilla lacks.
JEWELER_STOCK = ["silicon_carbide_grit", "polishing_compound", "silver_band",
                 "sterling_silver_band", "gold_band", "rose_gold_band", "electrum_band"]


def has_items(*ids):
    """inventory_changed on a concrete item list."""
    return {"trigger": "minecraft:inventory_changed",
            "conditions": {"items": [{"items": [f"carvercraft:{i}" if ":" not in i else i
                                                for i in ids]}]}}


def has_tag(tag):
    """inventory_changed on one of our stage tags."""
    return {"trigger": "minecraft:inventory_changed",
            "conditions": {"items": [{"items": f"#carvercraft:{tag}"}]}}


# --------------------------------------------------------------------- the tree
# (name, parent, icon, frame, title, description, criterion)

TREE = [
    ("root", None, "rough_jasper", "task",
     "CarverCraft",
     "Pick up a rough stone. Every material and process past this point is real.",
     has_tag("rough_stones")),

    # --- the metal side -----------------------------------------------------
    ("silver", "root", "silver_ingot", "task",
     "Silver Lining",
     "Smelt raw silver into an ingot. Jewelry starts with metal, not stone.",
     has_items("silver_ingot")),
    ("hammer", "silver", "jewelers_hammer", "task",
     "On the Bench",
     "Forge a jeweler's hammer. It stays in the grid and wears a little with every draw.",
     has_items("jewelers_hammer")),
    ("wire", "hammer", "silver_wire", "task",
     "Drawn Out",
     "Draw an ingot into wire. One ingot, two lengths — this is what bands are made of.",
     has_tag("wires")),
    ("band", "wire", "silver_band", "task",
     "Band Together",
     "Bend four lengths of wire into a band. Silver for trinkets, sterling for cabochons, "
     "gold for faceted stones.",
     has_tag("bands")),
    ("alloyer", "silver", "alloyer", "task",
     "Crucible",
     "Build an Alloyer. Real jeweller's alloys are melted at real ratios, not mashed "
     "together on a bench.",
     has_items("alloyer")),
    ("sterling", "alloyer", "sterling_silver_ingot", "task",
     "Ninety-Two Point Five",
     "Melt seven silver with one copper. That ratio is why sterling is stamped 925.",
     has_items("sterling_silver_ingot")),
    ("electrum", "alloyer", "electrum_ingot", "task",
     "The Enchanter's Metal",
     "Melt silver with gold. Electrum takes enchantment more eagerly than anything else "
     "on the bench — and wears out faster for it.",
     has_items("electrum_ingot")),
    ("rose_gold", "alloyer", "rose_gold_ingot", "task",
     "Hardest Gold There Is",
     "Melt three gold with one copper. Rose gold outlasts every other setting in the mod, "
     "which is true of the real alloy too.",
     has_items("rose_gold_ingot")),

    # --- the tumbling line --------------------------------------------------
    ("tumbler", "root", "rock_tumbler", "task",
     "Six Weeks in a Barrel",
     "Build a Rock Tumbler. No power, no skill, no hurry — four lanes and a lot of waiting.",
     has_items("rock_tumbler")),
    ("grit", "tumbler", "silicon_carbide_grit", "task",
     "Grit",
     "Cook sand and coal into silicon carbide. The barrel doesn't turn for free.",
     has_items("silicon_carbide_grit")),
    ("tumbled", "grit", "tumbled_jasper", "task",
     "Baroque",
     "Pull a tumbled stone out of the barrel — smooth, rounded, and no two the same shape.",
     has_tag("tumbled_stones")),
    ("trinket", "tumbled", "jasper_trinket", "task",
     "Pocket Piece",
     "Set a tumbled stone in a silver band. The cheap tier, and the first thing you can wear.",
     has_tag("trinkets")),

    # --- the cabbing line ---------------------------------------------------
    ("trim_saw", "tumbler", "trim_saw", "task",
     "First Cut",
     "Build a Trim Saw. A diamond blade cuts anything, so from here on the gate is power, "
     "not hardness.",
     has_items("trim_saw")),
    ("slab", "trim_saw", "jasper_slab", "task",
     "Slabbed",
     "Slice a nodule into flat stock. Slabbing is a cabbing workflow — faceting rough never "
     "sees a saw.",
     has_tag("slabs")),
    ("cabbing", "slab", "cabbing_machine", "task",
     "The Wheel",
     "Build a Cabbing Machine. Grind a dome, sand up through the grits, polish.",
     has_items("cabbing_machine")),
    ("polish", "cabbing", "polishing_compound", "task",
     "Rouge",
     "Grind iron into jeweler's rouge. The last stage of every cabochon is red powder and "
     "patience.",
     has_items("polishing_compound")),
    ("cabochon", "polish", "jasper_cabochon", "task",
     "Domed and Polished",
     "Finish a cabochon. Flat back, domed top, and a shine you put there yourself.",
     has_tag("cabochons")),
    ("cab_ring", "cabochon", "jasper_ring", "goal",
     "Set in Sterling",
     "Set a cabochon in a sterling band. The working tier — solid, and it lasts.",
     has_tag("cabochon_rings")),

    # --- the faceting line --------------------------------------------------
    ("faceting", "cabbing", "faceting_machine", "task",
     "Index and Dop",
     "Build a Faceting Machine. The most expensive machine in the mod, and the only one "
     "worth pointing at a diamond.",
     has_items("faceting_machine")),
    ("faceted", "faceting", "faceted_topaz", "goal",
     "Return the Light",
     "Cut a faceted gem. Facets exist to throw light back at you instead of scattering it — "
     "which is why you would never facet jasper.",
     has_tag("faceted_gems")),
    ("diamond_ring", "faceted", "diamond_ring", "goal",
     "A Diamond Ring",
     "Set a faceted diamond in gold. The best ring in the mod, and the only thing worth "
     "doing with a diamond.",
     has_items("diamond_ring")),
    ("brilliance", "faceted", "faceted_star_garnet", "goal",
     "Brilliance",
     "Enchant a ring. A better polish returns more light — fifteen percent more of whatever "
     "the stone already does, per level.",
     # enchanted_item fires at the table; the item predicate keeps it to our jewelry.
     {"trigger": "minecraft:enchanted_item",
      "conditions": {"item": {"items": "#carvercraft:rings"}}}),

    # --- the signatures -----------------------------------------------------
    ("bruneau", "root", "rough_bruneau_jasper", "task",
     "Picture Jasper",
     "Find Bruneau jasper. It comes out of a canyon an hour south of Boise and looks like "
     "a landscape someone painted into the rock.",
     has_items("rough_bruneau_jasper")),
    ("jeweler", "band", "emerald", "task",
     "Regular Customer",
     "Trade with a village jeweler. They keep grit, rouge, and bands in stock — and no "
     "amount of emeralds will buy a star garnet.",
     # Vanilla has no villager-profession predicate, so this is pinned to stock that
     # only the jeweler ever sells.
     {"trigger": "minecraft:villager_trade",
      "conditions": {"item": {"items": [f"carvercraft:{i}" for i in JEWELER_STOCK]}}}),
    ("star_garnet", "diamond_ring", "star_garnet_ring", "challenge",
     "Idaho",
     "Wear a star garnet. The state gem, found in commercial quantity in two places on "
     "Earth, and the only ring here that carries two gifts at once.",
     has_items("star_garnet_ring")),
]

ROOT_BACKGROUND = "minecraft:textures/block/andesite.png"


def build():
    lang_add = {}
    for name, parent, icon, frame, title, description, criterion in TREE:
        display = {
            "icon": {"id": f"carvercraft:{icon}" if icon != "emerald" else "minecraft:emerald"},
            "title": {"translate": f"advancement.carvercraft.{name}.title"},
            "description": {"translate": f"advancement.carvercraft.{name}.description"},
            "frame": frame,
            "show_toast": True,
            "announce_to_chat": True,
            "hidden": False,
        }
        if parent is None:
            display["background"] = ROOT_BACKGROUND

        adv = {"display": display,
               "criteria": {"unlock": criterion},
               "requirements": [["unlock"]]}
        if parent is not None:
            adv = {"parent": f"carvercraft:{parent}", **adv}
        # A challenge is worth something.
        if frame == "challenge":
            adv["rewards"] = {"experience": 500}

        write(f"{ADV}/{name}.json", adv)
        lang_add[f"advancement.carvercraft.{name}.title"] = title
        lang_add[f"advancement.carvercraft.{name}.description"] = description

    for tag, items in STAGE_TAGS.items():
        write(f"{TAGS}/{tag}.json",
              {"replace": False, "values": [f"carvercraft:{i}" for i in items]})

    with open(LANG) as f:
        lang = json.load(f)
    lang.update(lang_add)
    with open(LANG, "w") as f:
        json.dump(lang, f, indent=2, ensure_ascii=False)
        f.write("\n")

    print(f"{len(TREE)} advancements, {len(STAGE_TAGS)} stage tags, "
          f"{len(lang_add)} lang keys")


if __name__ == "__main__":
    build()
