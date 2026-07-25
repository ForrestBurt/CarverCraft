#!/usr/bin/env python3
"""Textures for the jeweler's bench: the hammer and the five wires.

The hammer is a handheld item, so it's drawn on the usual bottom-left-to-top-right
diagonal vanilla tools use. The wires are coils, drawn once as a shape and then
recolored per metal from the same palettes the matching bands use — so a coil of
electrum wire and an electrum band read as the same stuff.
"""

from PIL import Image

ITEM_DIR = "src/main/resources/assets/carvercraft/textures/item"

# Metal ramps: (outline, dark, mid, light, highlight) — lifted from the band textures.
METALS = {
    "silver_wire":          ((58, 64, 70), (88, 96, 102), (128, 137, 144), (185, 194, 200), (228, 235, 240)),
    "sterling_silver_wire": ((60, 66, 72), (92, 100, 108), (158, 166, 174), (206, 212, 218), (240, 245, 248)),
    "gold_wire":            ((72, 52, 12), (104, 78, 20), (176, 138, 40), (226, 186, 74), (250, 226, 150)),
    "electrum_wire":        ((74, 68, 38), (110, 100, 58), (170, 158, 96), (214, 204, 140), (240, 232, 180)),
    "rose_gold_wire":       ((82, 48, 38), (120, 72, 58), (182, 118, 98), (222, 158, 138), (244, 204, 188)),
}


def hammer():
    """Iron head, silver planishing face, stick handle, on the tool diagonal."""
    im = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    px = im.load()

    wood_dark = (94, 68, 40, 255)
    wood = (134, 100, 60, 255)
    wood_hi = (168, 128, 80, 255)
    iron_out = (52, 52, 56, 255)
    iron = (128, 128, 134, 255)
    iron_hi = (188, 188, 194, 255)
    silver = (196, 204, 212, 255)
    silver_hi = (234, 240, 246, 255)

    # Handle: a diagonal from the lower-left corner up to the head.
    for i in range(9):
        x, y = 2 + i, 13 - i
        px[x, y] = wood
        if x + 1 < 16:
            px[x + 1, y] = wood_dark
        if y - 1 >= 0:
            px[x, y - 1] = wood_hi
    # Grip wrap at the butt.
    for x, y in ((2, 13), (3, 12)):
        px[x, y] = wood_dark

    # Head: a chunky block sitting across the top of the handle, rows 2-6.
    for y in range(2, 7):
        for x in range(8, 15):
            px[x, y] = iron
    # Top light, bottom shadow, back edge outlined.
    for x in range(8, 15):
        px[x, 2] = iron_hi
        px[x, 6] = iron_out
    for y in range(2, 7):
        px[8, y] = iron_out
    # Silver planishing face on the striking end — the reason it's a jeweler's hammer.
    for y in range(3, 6):
        px[13, y] = silver
        px[14, y] = silver
    px[14, 3] = silver_hi
    px[13, 4] = silver_hi

    # Outline the whole silhouette.
    outline = (34, 30, 28, 255)
    for y in range(16):
        for x in range(16):
            if px[x, y][3] == 0:
                nb = [(x + dx, y + dy) for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1))]
                if any(0 <= nx < 16 and 0 <= ny < 16 and px[nx, ny][3] != 0 and px[nx, ny] != outline
                       for nx, ny in nb):
                    px[x, y] = outline
    im.save(f"{ITEM_DIR}/jewelers_hammer.png")


def wire(name, ramp):
    """A loose coil: three turns seen slightly from the side, plus a drawn tail."""
    outline, dark, mid, light, hi = (c + (255,) for c in ramp)
    im = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    px = im.load()

    # Three stacked ellipse turns.
    for turn, top in enumerate((4, 7, 10)):
        for x in range(4, 12):
            px[x, top] = light if turn == 0 else mid
            px[x, top + 1] = dark
        # Ends of each turn curve down a row.
        px[3, top + 1] = mid
        px[12, top] = mid
    # Highlight along the topmost turn.
    for x in range(6, 10):
        px[x, 4] = hi
    # Shadow under the bottom turn.
    for x in range(4, 12):
        px[x, 12] = dark

    # A drawn tail leaving the coil, the bit you'd bend into a shank.
    for x, y in ((12, 11), (13, 10), (13, 9)):
        px[x, y] = light

    # Outline.
    for y in range(16):
        for x in range(16):
            if px[x, y][3] == 0:
                nb = [(x + dx, y + dy) for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1))]
                if any(0 <= nx < 16 and 0 <= ny < 16 and px[nx, ny][3] != 0 and px[nx, ny] != outline
                       for nx, ny in nb):
                    px[x, y] = outline
    im.save(f"{ITEM_DIR}/{name}.png")


if __name__ == "__main__":
    hammer()
    for name, ramp in METALS.items():
        wire(name, ramp)
    print("bench textures written")
