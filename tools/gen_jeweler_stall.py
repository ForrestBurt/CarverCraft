#!/usr/bin/env python3
"""Generate the jeweler's stall structure NBT.

A 7x5x7 open-front market stall: cobble floor, oak posts, brick half-walls (the
crucible's material), plank roof. Inside: a cabbing machine — the jeweler's job
site — a rock tumbler, and a stock chest pulling from the jeweler_stall loot
table. One jigsaw block at the front connects it to village streets exactly the
way vanilla houses connect (name minecraft:building_entrance, facing the road).

Written directly as vanilla structure NBT (gzip'd), DataVersion 3955 = 1.21.1.
Run from the repo root to regenerate.
"""

import gzip
import struct

OUT = "src/main/resources/data/carvercraft/structure/village/jeweler_stall.nbt"
DATA_VERSION = 3955
SIZE = (7, 5, 7)

# ------------------------------------------------------------- tiny NBT writer

def _name(s):
    b = s.encode("utf-8")
    return struct.pack(">H", len(b)) + b

def t_int(v):
    return struct.pack(">i", v)

def t_string(v):
    return _name(v)

def t_list(elem_type, payloads):
    return struct.pack(">bi", elem_type, len(payloads)) + b"".join(payloads)

def t_compound(entries):
    """entries: list of (tag_type, name, payload)."""
    out = b""
    for tag_type, name, payload in entries:
        out += struct.pack(">b", tag_type) + _name(name) + payload
    return out + b"\x00"

TAG_INT, TAG_STRING, TAG_LIST, TAG_COMPOUND = 3, 8, 9, 10

# ------------------------------------------------------------------- structure

palette = []
palette_index = {}

def state(name, props=None):
    key = (name, tuple(sorted((props or {}).items())))
    if key not in palette_index:
        entries = []
        if props:
            entries.append((TAG_COMPOUND, "Properties",
                            t_compound([(TAG_STRING, k, t_string(v)) for k, v in sorted(props.items())])))
        entries.append((TAG_STRING, "Name", t_string(name)))
        palette_index[key] = len(palette)
        palette.append(t_compound(entries))
    return palette_index[key]

COBBLE = state("minecraft:cobblestone")
LOG = state("minecraft:oak_log", {"axis": "y"})
BRICKS = state("minecraft:bricks")
PLANKS = state("minecraft:oak_planks")
CABBING = state("carvercraft:cabbing_machine", {"lit": "false"})
TUMBLER = state("carvercraft:rock_tumbler", {"lit": "false"})
CHEST = state("minecraft:chest", {"facing": "north", "type": "single", "waterlogged": "false"})
TORCH = state("minecraft:torch")
JIGSAW = state("minecraft:jigsaw", {"orientation": "north_up"})
AIR = state("minecraft:air")

blocks = {}

def put(x, y, z, s, nbt=None):
    blocks[(x, y, z)] = (s, nbt)

# Floor.
for x in range(7):
    for z in range(7):
        put(x, 0, z, COBBLE)

# Corner posts, y 1-3.
for x, z in ((0, 1), (6, 1), (0, 6), (6, 6)):
    for y in (1, 2, 3):
        put(x, y, z, LOG)

# Brick half-walls at y=1: sides and back.
for z in range(2, 6):
    put(0, 1, z, BRICKS)
    put(6, 1, z, BRICKS)
for x in range(1, 6):
    put(x, 1, 6, BRICKS)

# Torches on the side walls.
put(0, 2, 3, TORCH)
put(6, 2, 3, TORCH)

# The bench: job site, tumbler, stock chest.
put(2, 1, 4, CABBING)
put(4, 1, 4, TUMBLER)
put(5, 1, 5, CHEST, t_compound([
    (TAG_STRING, "id", t_string("minecraft:chest")),
    (TAG_STRING, "LootTable", t_string("carvercraft:chests/jeweler_stall")),
]))

# Roof.
for x in range(7):
    for z in range(1, 7):
        put(x, 4, z, PLANKS)

# Street connector, front center at ground level.
put(3, 1, 0, JIGSAW, t_compound([
    (TAG_STRING, "id", t_string("minecraft:jigsaw")),
    (TAG_STRING, "name", t_string("minecraft:building_entrance")),
    (TAG_STRING, "target", t_string("minecraft:empty")),
    (TAG_STRING, "pool", t_string("minecraft:empty")),
    (TAG_STRING, "final_state", t_string("minecraft:air")),
    (TAG_STRING, "joint", t_string("rollable")),
]))

# Air-fill the rest of the interior volume so terrain can't poke through, plus a
# clear entrance column above the jigsaw.
for x in range(7):
    for z in range(1, 7):
        for y in (1, 2, 3):
            if (x, y, z) not in blocks:
                put(x, y, z, AIR)
for x in (2, 3, 4):
    for y in (1, 2, 3):
        if (x, y, 0) not in blocks:
            put(x, y, 0, AIR)

# ------------------------------------------------------------------- assemble

block_payloads = []
for (x, y, z), (s, nbt) in sorted(blocks.items()):
    entries = [
        (TAG_LIST, "pos", t_list(TAG_INT, [t_int(x), t_int(y), t_int(z)])),
        (TAG_INT, "state", t_int(s)),
    ]
    if nbt is not None:
        entries.append((TAG_COMPOUND, "nbt", nbt))
    block_payloads.append(t_compound(entries))

root = t_compound([
    (TAG_LIST, "size", t_list(TAG_INT, [t_int(v) for v in SIZE])),
    (TAG_LIST, "entities", t_list(0, [])),
    (TAG_LIST, "blocks", t_list(TAG_COMPOUND, block_payloads)),
    (TAG_LIST, "palette", t_list(TAG_COMPOUND, palette)),
    (TAG_INT, "DataVersion", t_int(DATA_VERSION)),
])

raw = struct.pack(">b", TAG_COMPOUND) + _name("") + root
import os
os.makedirs(os.path.dirname(OUT), exist_ok=True)
with gzip.open(OUT, "wb") as f:
    f.write(raw)
print(f"wrote {OUT}: {len(blocks)} blocks, {len(palette)} palette entries")
