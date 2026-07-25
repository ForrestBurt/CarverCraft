#!/usr/bin/env python3
"""Bruneau jasper textures, derived from the jasper set so silhouettes stay
family-consistent.

Regular jasper is drawn in exactly four stone colors plus a white glint; Bruneau
picture jasper remaps them onto a porcelain tan/cream ramp, then lays wavy brown
"scenery" bands across the big flat faces — the landscape look the Bruneau canyon
material is famous for. Band metal on the trinket and ring is left untouched.
"""

from PIL import Image

ITEM_DIR = "src/main/resources/assets/carvercraft/textures/item"

# jasper -> Bruneau porcelain ramp
REMAP = {
    (70, 24, 14): (66, 46, 30),      # outline / deepest shadow
    (112, 46, 28): (110, 80, 52),    # shadow
    (186, 88, 58): (196, 164, 118),  # base tan
    (226, 132, 96): (232, 212, 172), # cream highlight
}

BAND_DARK = (110, 80, 52, 255)
BAND_DEEP = (86, 60, 38, 255)
RED_ACCENT = (152, 84, 52, 255)     # the iron-red streak Bruneau material carries

# (source, dest, draw scenery bands?)
JOBS = [
    ("rough_jasper", "rough_bruneau_jasper", True),
    ("tumbled_jasper", "tumbled_bruneau_jasper", True),
    ("jasper_slab", "bruneau_jasper_slab", True),
    ("jasper_cabochon", "bruneau_jasper_cabochon", True),
    ("jasper_trinket", "bruneau_jasper_trinket", False),
    ("jasper_ring", "bruneau_jasper_ring", False),
]


def stone_mask(px):
    """Pixels that were jasper stone (got remapped), not band metal or glint."""
    mask = set()
    for y in range(16):
        for x in range(16):
            if px[x, y][3] and px[x, y][:3] in REMAP.values():
                mask.add((x, y))
    return mask


def remap(src, dst, bands):
    im = Image.open(f"{ITEM_DIR}/{src}.png").convert("RGBA")
    px = im.load()
    for y in range(16):
        for x in range(16):
            r, g, b, a = px[x, y]
            if a and (r, g, b) in REMAP:
                px[x, y] = REMAP[(r, g, b)] + (a,)

    if bands:
        mask = stone_mask(px)
        if mask:
            ys = [y for _, y in mask]
            y0, y1 = min(ys), max(ys)
            h = y1 - y0
            interior = {(x, y) for x, y in mask
                        if px[x, y][:3] != REMAP[(70, 24, 14)]}
            for base_frac, color in ((0.38, BAND_DARK), (0.62, BAND_DEEP)):
                base = y0 + round(h * base_frac)
                for x in range(16):
                    wave = (x * 3 // 7) % 2      # gentle 1px undulation
                    y = base + wave
                    if (x, y) in interior:
                        px[x, y] = color
            # One short iron-red streak under the top band.
            base = y0 + round(h * 0.48)
            for x in range(5, 11):
                if (x, base) in interior:
                    px[x, base] = RED_ACCENT

    im.save(f"{ITEM_DIR}/{dst}.png")


if __name__ == "__main__":
    for src, dst, bands in JOBS:
        remap(src, dst, bands)
    print("bruneau jasper textures written")
