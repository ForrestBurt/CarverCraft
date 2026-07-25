#!/usr/bin/env python3
"""Textures for the shop-supply consumables, and the consumable slot stamped onto
the tumbler and cabbing machine GUI sheets (the saw and faceting machine take no
consumable, so their sheets are untouched).

Sheets stay 256x256. The slot frame lands at (133,51) — item position (134,52),
matching LapidaryMenu.CONSUMABLE_X/Y — with a small color dot above it hinting
what belongs there.
"""

from PIL import Image

ITEM_DIR = "src/main/resources/assets/carvercraft/textures/item"
GUI_DIR = "src/main/resources/assets/carvercraft/textures/gui"

BORDER = (85, 85, 85, 255)
SLOT_IN = (139, 139, 139, 255)


def grit():
    """A heap of silicon carbide: near-black angular grains with an iridescent glint."""
    im = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    px = im.load()
    outline = (28, 30, 34, 255)
    base = (52, 54, 58, 255)
    dark = (38, 40, 44, 255)
    glint = (150, 160, 170, 255)
    irid_b = (90, 110, 140, 255)
    irid_p = (110, 88, 124, 255)

    heap = {
        6: range(7, 10),
        7: range(5, 11),
        8: range(4, 12),
        9: range(3, 13),
        10: range(3, 13),
        11: range(2, 14),
        12: range(2, 14),
    }
    for y, xs in heap.items():
        for x in xs:
            px[x, y] = base
    # Grain shadows.
    for x, y in ((6, 8), (9, 9), (5, 10), (11, 10), (4, 11), (8, 11), (12, 12), (6, 12)):
        px[x, y] = dark
    # Sparkle.
    for x, y in ((8, 7), (5, 9), (10, 10), (7, 12)):
        px[x, y] = glint
    px[11, 9] = irid_b
    px[9, 11] = irid_p
    # Outline the silhouette.
    for y in range(16):
        for x in range(16):
            if px[x, y][3] == 0:
                neighbours = [(x + dx, y + dy) for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1))]
                if any(0 <= nx < 16 and 0 <= ny < 16 and px[nx, ny][3] != 0
                       and px[nx, ny] != outline for nx, ny in neighbours):
                    px[x, y] = outline
    im.save(f"{ITEM_DIR}/silicon_carbide_grit.png")


def rouge():
    """A wrapped bar of jeweler's rouge — red iron oxide in a paper band."""
    im = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    px = im.load()
    outline = (70, 26, 20, 255)
    base = (150, 58, 44, 255)
    dark = (116, 42, 33, 255)
    hi = (192, 94, 72, 255)
    paper = (208, 192, 168, 255)
    paper_dark = (176, 158, 132, 255)

    for y in range(5, 13):
        for x in range(3, 14):
            px[x, y] = base
    # Rounded corners off.
    for x, y in ((3, 5), (13, 5), (3, 12), (13, 12)):
        px[x, y] = (0, 0, 0, 0)
    # Bottom/right shading, top/left light.
    for x in range(4, 13):
        px[x, 12] = dark
        px[x, 5] = hi
    for y in range(6, 12):
        px[13, y] = dark
        px[3, y] = hi
    # Paper wrap band around the middle.
    for y in range(5, 13):
        for x in (7, 8, 9):
            if px[x, y][3] != 0:
                px[x, y] = paper
    for x in (7, 8, 9):
        px[x, 12] = paper_dark
    # Outline.
    for y in range(16):
        for x in range(16):
            if px[x, y][3] == 0:
                neighbours = [(x + dx, y + dy) for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1))]
                if any(0 <= nx < 16 and 0 <= ny < 16 and px[nx, ny][3] != 0
                       and px[nx, ny] != outline for nx, ny in neighbours):
                    px[x, y] = outline
    im.save(f"{ITEM_DIR}/polishing_compound.png")


def stamp_slot(sheet, dot):
    path = f"{GUI_DIR}/{sheet}.png"
    im = Image.open(path).convert("RGBA")
    px = im.load()
    fx, fy = 133, 51
    for y in range(fy, fy + 18):
        for x in range(fx, fx + 18):
            edge = x in (fx, fx + 17) or y in (fy, fy + 17)
            px[x, y] = BORDER if edge else SLOT_IN
    # Hint dot: a 3px diamond above the slot in the consumable's color.
    cx, cy = fx + 8, fy - 4
    for dx, dy in ((0, 0), (1, 0), (-1, 0), (0, 1), (0, -1)):
        px[cx + dx, cy + dy] = dot
    im.save(path)


if __name__ == "__main__":
    grit()
    rouge()
    stamp_slot("rock_tumbler", (60, 62, 68, 255))      # grit-dark
    stamp_slot("cabbing_machine", (150, 58, 44, 255))  # rouge-red
    print("consumable textures written")
