#!/usr/bin/env python3
"""Cross-reference validator for CarverCraft.

Most of this mod is written without a local compiler, so this script catches the
breakage javac would — stale registry symbols — plus the breakage javac wouldn't:
items with no model, models with no texture, missing lang keys, recipes that name
nonexistent items, loot modifiers that aren't wired up, and orphaned assets.

Run from the repo root (or anywhere inside it):  python3 tools/validate.py
Exits non-zero if anything fails; run it before every commit.
"""

import json
import re
import sys
from pathlib import Path

# ---------------------------------------------------------------- repo layout

ROOT = Path(__file__).resolve().parent.parent
JAVA = ROOT / "src/main/java/com/forrestb/carvercraft"
ASSETS = ROOT / "src/main/resources/assets/carvercraft"
DATA = ROOT / "src/main/resources/data"

REGISTRY_CLASSES = [
    "ModItems", "ModBlocks", "ModBlockEntities", "ModMenus",
    "ModRecipes", "ModCreativeTabs", "ModLootModifiers", "ModCapabilities",
]

# Blocks that legitimately have no loot table (unbreakable, creative-only).
NO_LOOT_TABLE = {"creative_charger"}
# Blocks that legitimately have no crafting recipe.
NO_CRAFTING_RECIPE = {"creative_charger", "silver_ore", "deepslate_silver_ore"}

errors = []
warnings = []


def err(msg):
    errors.append(msg)


def warn(msg):
    warnings.append(msg)


# ------------------------------------------------- 1. Java symbol cross-check

def java_sources():
    return sorted(JAVA.rglob("*.java"))


def collect_registry_symbols():
    """Map class name -> set of public static field names it defines."""
    defined = {}
    field_re = re.compile(r"public\s+static\s+final\s+[\w<>.,?\s\[\]]+?\s+([A-Z][A-Z0-9_]*)\s*=")
    for cls in REGISTRY_CLASSES:
        path = JAVA / "registry" / f"{cls}.java"
        if not path.exists():
            err(f"registry class missing: {path.relative_to(ROOT)}")
            defined[cls] = set()
            continue
        defined[cls] = set(field_re.findall(path.read_text()))
    return defined


def check_java_symbols(defined):
    ref_re = re.compile(r"\b(" + "|".join(REGISTRY_CLASSES) + r")\.([A-Z][A-Z0-9_]*)\b")
    for src in java_sources():
        text = src.read_text()
        for m in ref_re.finditer(text):
            cls, sym = m.group(1), m.group(2)
            if sym not in defined.get(cls, set()):
                line = text[: m.start()].count("\n") + 1
                err(f"{src.relative_to(ROOT)}:{line}: {cls}.{sym} is referenced but never defined")


# --------------------------------------------- 2. registered content -> names

def collect_registered_items():
    """Item names registered in ModItems (plain items, rings, block items)."""
    text = (JAVA / "registry/ModItems.java").read_text()
    names = set()
    for m in re.finditer(r'\b(?:simple|ring)\("([a-z0-9_]+)"', text):
        names.add(m.group(1))
    for m in re.finditer(r'registerSimpleBlockItem\("([a-z0-9_]+)"', text):
        names.add(m.group(1))
    for m in re.finditer(r'ITEMS\.register\("([a-z0-9_]+)"', text):
        names.add(m.group(1))
    return names


def collect_registered_blocks():
    text = (JAVA / "registry/ModBlocks.java").read_text()
    return set(re.findall(r'\.(?:registerSimpleBlock|registerBlock)\("([a-z0-9_]+)"', text))


# ------------------------------------------------------- 3. asset cross-check

def check_assets(items, blocks):
    lang_path = ASSETS / "lang/en_us.json"
    lang = json.loads(lang_path.read_text()) if lang_path.exists() else {}

    for name in sorted(items):
        model = ASSETS / f"models/item/{name}.json"
        if not model.exists():
            err(f"item '{name}': no model at assets/carvercraft/models/item/{name}.json")
        else:
            check_model_textures(model)
        if name in blocks:
            key = f"block.carvercraft.{name}"
        else:
            key = f"item.carvercraft.{name}"
        if key not in lang:
            err(f"item '{name}': missing lang key {key}")

    for name in sorted(blocks):
        state = ASSETS / f"blockstates/{name}.json"
        if not state.exists():
            err(f"block '{name}': no blockstate at assets/carvercraft/blockstates/{name}.json")
        else:
            for model_ref in blockstate_models(state):
                check_referenced_model(model_ref, f"blockstate {name}")
        if f"block.carvercraft.{name}" not in lang:
            err(f"block '{name}': missing lang key block.carvercraft.{name}")
        if name not in NO_LOOT_TABLE:
            loot = DATA / f"carvercraft/loot_table/blocks/{name}.json"
            if not loot.exists():
                err(f"block '{name}': no loot table at data/carvercraft/loot_table/blocks/{name}.json")

    # Orphans: models and lang keys that point at nothing registered.
    for model in sorted((ASSETS / "models/item").glob("*.json")):
        if model.stem not in items:
            warn(f"orphan item model: {model.relative_to(ROOT)} (no registered item '{model.stem}')")
    for key in lang:
        m = re.match(r"^(item|block)\.carvercraft\.([a-z0-9_]+)$", key)
        if not m:
            continue
        kind, name = m.group(1), m.group(2)
        if kind == "item" and name not in items:
            warn(f"orphan lang key: {key}")
        if kind == "block" and name not in blocks and name not in items:
            warn(f"orphan lang key: {key}")


def blockstate_models(path):
    try:
        data = json.loads(path.read_text())
    except json.JSONDecodeError as e:
        err(f"{path.relative_to(ROOT)}: invalid JSON ({e})")
        return []
    refs = []
    for variant in data.get("variants", {}).values():
        entries = variant if isinstance(variant, list) else [variant]
        for entry in entries:
            if "model" in entry:
                refs.append(entry["model"])
    return refs


def check_referenced_model(ref, ctx):
    if not ref.startswith("carvercraft:"):
        return
    rel = ref.split(":", 1)[1]
    path = ASSETS / f"models/{rel}.json"
    if not path.exists():
        err(f"{ctx}: references model {ref} but {path.relative_to(ROOT)} does not exist")
    else:
        check_model_textures(path)


def check_model_textures(path):
    try:
        data = json.loads(path.read_text())
    except json.JSONDecodeError as e:
        err(f"{path.relative_to(ROOT)}: invalid JSON ({e})")
        return
    for ref in data.get("textures", {}).values():
        if not ref.startswith("carvercraft:"):
            continue
        rel = ref.split(":", 1)[1]
        tex = ASSETS / f"textures/{rel}.png"
        if not tex.exists():
            err(f"{path.relative_to(ROOT)}: texture {ref} missing at {tex.relative_to(ROOT)}")


# -------------------------------------------------------- 4. data cross-check

def all_mod_item_ids(items, blocks):
    return {f"carvercraft:{n}" for n in items | blocks}


def iter_json_strings(node):
    if isinstance(node, dict):
        for v in node.values():
            yield from iter_json_strings(v)
    elif isinstance(node, list):
        for v in node:
            yield from iter_json_strings(v)
    elif isinstance(node, str):
        yield node


def check_data(items, blocks):
    known = all_mod_item_ids(items, blocks)
    known_recipe_types = {"carvercraft:" + t for t in collect_recipe_types()}
    # Datapack enchantments are ids too — the JSON file IS the enchantment.
    known_enchantments = {f"carvercraft:{p.stem}"
                          for p in (DATA / "carvercraft/enchantment").glob("*.json")}

    for path in sorted((DATA).rglob("*.json")):
        try:
            data = json.loads(path.read_text())
        except json.JSONDecodeError as e:
            err(f"{path.relative_to(ROOT)}: invalid JSON ({e})")
            continue
        # global_loot_modifiers entries are modifier ids, not item ids; the
        # list-vs-files agreement is checked separately below.
        if path.name == "global_loot_modifiers.json":
            continue
        # Any carvercraft: item id used anywhere in data must exist.
        for s in iter_json_strings(data):
            if s.startswith("carvercraft:") and "/" not in s:
                if s in known_recipe_types or s in known_enchantments or s == "carvercraft:stone_gem":
                    continue
                if s not in known:
                    err(f"{path.relative_to(ROOT)}: references '{s}' which is not a registered item/block")

    # Recipe type sanity: every carvercraft-typed recipe uses a registered type.
    for path in sorted((DATA / "carvercraft/recipe").rglob("*.json")):
        data = json.loads(path.read_text())
        rtype = data.get("type", "")
        if rtype.startswith("carvercraft:") and rtype not in known_recipe_types:
            err(f"{path.relative_to(ROOT)}: recipe type '{rtype}' is not registered in ModRecipes")

    # Every ring/trinket registered as a RingItem must be in the Curios ring tag,
    # or it silently can't be equipped.
    ring_names = set(re.findall(r'\bring\("([a-z0-9_]+)"', (JAVA / "registry/ModItems.java").read_text()))
    curios_tag = DATA / "curios/tags/item/ring.json"
    tagged = set()
    if curios_tag.exists():
        tagged = {v.split(":", 1)[1] for v in json.loads(curios_tag.read_text())["values"]}
    for missing in sorted(ring_names - tagged):
        err(f"ring item '{missing}' is not in data/curios/tags/item/ring.json — it can't be equipped")

    # ...and in the mod's own rings tag, or Brilliance can't roll on it.
    rings_tag = DATA / "carvercraft/tags/item/rings.json"
    if rings_tag.exists():
        in_rings = {v.split(":", 1)[1] for v in json.loads(rings_tag.read_text())["values"]}
        for missing in sorted(ring_names - in_rings):
            err(f"ring item '{missing}' is not in data/carvercraft/tags/item/rings.json — Brilliance can't apply")

    # Global loot modifiers: list and files must agree.
    glm = DATA / "neoforge/loot_modifiers/global_loot_modifiers.json"
    listed = set()
    if glm.exists():
        listed = {e.split(":", 1)[1] for e in json.loads(glm.read_text())["entries"]}
    on_disk = {p.stem for p in (DATA / "carvercraft/loot_modifiers").glob("*.json")}
    for missing in sorted(listed - on_disk):
        err(f"global_loot_modifiers lists '{missing}' but data/carvercraft/loot_modifiers/{missing}.json does not exist")
    for unlisted in sorted(on_disk - listed):
        err(f"loot modifier '{unlisted}' exists on disk but is not listed in global_loot_modifiers.json")


def collect_recipe_types():
    text = (JAVA / "registry/ModRecipes.java").read_text()
    return set(re.findall(r'type\("([a-z0-9_]+)"\)', text))


# ------------------------------------------------- 5. template expansion check

def check_templates():
    """Every ${token} in src/main/templates must be a key build.gradle expands.

    A missing key fails generateModMetadata before javac even runs — this broke
    every build for four commits and nobody noticed because nothing compiled the
    project locally.
    """
    gradle = ROOT / "build.gradle"
    if not gradle.exists():
        return
    m = re.search(r"replaceProperties\s*=\s*\[(.*?)\]", gradle.read_text(), re.DOTALL)
    if not m:
        warn("could not find replaceProperties in build.gradle — template check skipped")
        return
    keys = set(re.findall(r"([a-z_]+)\s*:", m.group(1)))
    for template in sorted((ROOT / "src/main/templates").rglob("*")):
        if not template.is_file():
            continue
        tokens = set(re.findall(r"\$\{([a-z_]+)\}", template.read_text()))
        for missing in sorted(tokens - keys):
            err(f"{template.relative_to(ROOT)}: uses ${{{missing}}} but build.gradle's "
                f"replaceProperties doesn't define it — generateModMetadata will fail")


# --------------------------------------------------------------------- report

def main():
    defined = collect_registry_symbols()
    check_java_symbols(defined)

    items = collect_registered_items()
    blocks = collect_registered_blocks()
    if not items:
        err("failed to parse any registered items out of ModItems.java — validator regexes need updating")
    check_assets(items, blocks)
    check_data(items, blocks)
    check_templates()

    for w in warnings:
        print(f"WARN  {w}")
    for e in errors:
        print(f"ERROR {e}")
    print(f"\n{len(items)} items, {len(blocks)} blocks checked: "
          f"{len(errors)} error(s), {len(warnings)} warning(s)")
    return 1 if errors else 0


if __name__ == "__main__":
    sys.exit(main())
