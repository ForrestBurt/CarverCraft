#!/usr/bin/env python3
"""Generate the Alloyer's programmer-art textures.

The GUI sheet is spliced from the trim saw's sheet so the panel, bevel, and player
inventory stay pixel-identical to the other machines; only the machine furniture
(two stacked inputs, one output, arrow, short energy gauge) is drawn fresh.
Block textures follow the machine family's palette but warm: a brick-bodied
crucible with a molten top when lit.

All sheets stay 256x256 — the default blit overload assumes it.
"""

from PIL import Image

GUI_DIR = "src/main/resources/assets/carvercraft/textures/gui"
BLOCK_DIR = "src/main/resources/assets/carvercraft/textures/block"

BORDER = (85, 85, 85, 255)        # 0x555555 panel outline / slot frames
BODY = (198, 198, 198, 255)       # 0xC6C6C6 panel body
SLOT_IN = (139, 139, 139, 255)    # 0x8B8B8B slot interior
GAUGE_IN = (70, 70, 74, 255)      # energy gauge window
ARROW_FILL = (235, 140, 50, 255)  # molten orange for the alloyer's arrow


def gui_sheet():
    src = Image.open(f"{GUI_DIR}/trim_saw.png").convert("RGBA")
    out = Image.new("RGBA", (256, 256), (0, 0, 0, 0))

    # Panel: top 72 rows, then the player-inventory block of the 190-tall sheet
    # (rows 96..190) pulled up to make a 166-tall furnace-style dialog.
    out.paste(src.crop((0, 0, 176, 72)), (0, 0))
    out.paste(src.crop((0, 96, 176, 190)), (0, 72))

    px = out.load()

    # Wipe the lapidary furniture out of the machine area, bevel untouched.
    for y in range(2, 72):
        for x in range(2, 174):
            px[x, y] = BODY

    # Slot frames: 18x18, 1px outline, gray interior. Item positions are +1.
    for fx, fy in ((48, 17), (48, 53), (116, 35)):
        for y in range(fy, fy + 18):
            for x in range(fx, fx + 18):
                edge = x in (fx, fx + 17) or y in (fy, fy + 17)
                px[x, y] = BORDER if edge else SLOT_IN

    # Arrow background: reuse the outline arrow drawn on the lapidary sheet.
    out.paste(src.crop((69, 17, 91, 33)), (76, 36))

    # Energy gauge: 1px frame around a 12x54 window (the lapidary gauge is 72
    # tall; this dialog is shorter).
    for y in range(16, 72):
        for x in range(11, 25):
            edge = x in (11, 24) or y in (16, 71)
            px[x, y] = BORDER if edge else GAUGE_IN

    # Sprite: filled arrow at (176,0), the lapidary arrow mask in molten orange.
    arrow = src.crop((176, 0, 198, 16))
    apx = arrow.load()
    for y in range(16):
        for x in range(22):
            if apx[x, y][3] != 0:
                px[176 + x, y] = ARROW_FILL

    # Sprite: energy fill at (176,16), 12x54 gradient matching the family style.
    top, bottom = (250, 200, 60), (190, 110, 30)
    for y in range(54):
        t = y / 53
        col = tuple(round(top[i] + (bottom[i] - top[i]) * t) for i in range(3)) + (255,)
        for x in range(12):
            px[176 + x, 16 + y] = col

    out.save(f"{GUI_DIR}/alloyer.png")


# ------------------------------------------------------------ block textures

FRAME = (66, 60, 56)      # warm dark border, same value range as 60,65,72
METAL = (118, 110, 102)   # warm gray body
METAL_HI = (170, 160, 148)
METAL_LO = (58, 52, 48)
BRICK = (146, 92, 74)
BRICK_HI = (168, 112, 90)
MORTAR = (126, 116, 108)
MOUTH = (38, 32, 28)


def side():
    im = Image.new("RGB", (16, 16), METAL)
    px = im.load()
    for i in range(16):
        px[i, 0] = FRAME
        px[i, 15] = FRAME
        px[0, i] = FRAME
        px[15, i] = FRAME
    # Riveted steel band across the top.
    for x in range(1, 15):
        px[x, 2] = METAL_HI if x % 3 else METAL_LO
    # Dark charging mouth, rows 4-6.
    for y in (4, 5, 6):
        for x in range(4, 12):
            px[x, y] = MOUTH
    for x in range(4, 12, 2):
        px[x, 6] = (52, 44, 38)
    # Brick courses from row 8 down — the crucible body the recipe builds.
    for y in range(8, 15):
        for x in range(1, 15):
            course = (y - 8) // 2
            if (y - 8) % 2 == 1:
                px[x, y] = MORTAR
            else:
                offset = 3 if course % 2 else 0
                px[x, y] = MORTAR if (x + offset) % 4 == 0 else BRICK
    # A few highlight glints on bricks.
    for x, y in ((3, 8), (9, 10), (6, 12), (12, 14)):
        if px[x, y] == BRICK:
            px[x, y] = BRICK_HI
    im.save(f"{BLOCK_DIR}/alloyer_side.png")


def _top_base():
    im = Image.new("RGB", (16, 16), METAL)
    px = im.load()
    for i in range(16):
        px[i, 0] = FRAME
        px[i, 15] = FRAME
        px[0, i] = FRAME
        px[15, i] = FRAME
    # Bolted rim.
    for x in range(2, 14, 3):
        px[x, 2] = METAL_LO
        px[x, 13] = METAL_LO
        px[2, x] = METAL_LO
        px[13, x] = METAL_LO
    return im, px


def top_cold():
    im, px = _top_base()
    # Cold crucible mouth: dark solidified slag with a dull sheen.
    for y in range(4, 12):
        for x in range(4, 12):
            px[x, y] = (64, 56, 50)
    for x, y in ((6, 6), (9, 8), (7, 10)):
        px[x, y] = (92, 82, 72)
    im.save(f"{BLOCK_DIR}/alloyer_top.png")


def top_lit():
    im, px = _top_base()
    cx = cy = 7.5
    for y in range(4, 12):
        for x in range(4, 12):
            d = ((x - cx) ** 2 + (y - cy) ** 2) ** 0.5 / 5.0
            d = min(d, 1.0)
            r = round(255 - (255 - 205) * d)
            g = round(215 - (215 - 85) * d)
            b = round(95 - (95 - 25) * d)
            px[x, y] = (r, g, b)
    # White-hot flecks.
    for x, y in ((7, 7), (8, 8), (6, 9)):
        px[x, y] = (255, 240, 180)
    im.save(f"{BLOCK_DIR}/alloyer_top_lit.png")


if __name__ == "__main__":
    gui_sheet()
    side()
    top_cold()
    top_lit()
    print("alloyer textures written")
