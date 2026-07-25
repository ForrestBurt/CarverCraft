#!/usr/bin/env python3
"""Split the gold-tier ring textures into a tintable band layer and a stone layer.

Only the nine faceted-stone rings can be set in electrum or rose gold, so only they
need the treatment. Each ring's band pixels are drawn from one known four-colour gold
ramp; those move to layer0, normalised against the gold tint so that multiplying
layer0 by gold reproduces today's texture EXACTLY (verified below) and multiplying by
the electrum or rose gold tint gives the same ring in that metal. Everything else —
the stone, its glint — moves to layer1 untinted.
"""

from PIL import Image

ITEM_DIR = "src/main/resources/assets/carvercraft/textures/item"

# The gold band ramp, shared by every faceted ring.
GOLD_RAMP = [(104, 78, 20), (176, 138, 40), (226, 186, 74), (250, 226, 150)]
# Tint applied to layer0 for the default (gold) band. This is the ramp's BRIGHTEST
# entry on purpose: normalise against anything dimmer and the highlight clamps at
# white and can't come back. With this base every ramp entry round-trips exactly.
GOLD_TINT = (250, 226, 150)

RINGS = [
    "peridot_ring", "garnet_ring", "topaz_ring", "ruby_ring", "sapphire_ring",
    "star_garnet_ring", "diamond_ring", "emerald_ring", "amethyst_ring",
]


def split(name):
    im = Image.open(f"{ITEM_DIR}/{name}.png").convert("RGBA")
    px = im.load()
    band = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    stone = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    bpx, spx = band.load(), stone.load()

    for y in range(16):
        for x in range(16):
            r, g, b, a = px[x, y]
            if a == 0:
                continue
            if (r, g, b) in GOLD_RAMP:
                # Normalise so band * GOLD_TINT == original.
                bpx[x, y] = (
                    min(255, round(r * 255 / GOLD_TINT[0])),
                    min(255, round(g * 255 / GOLD_TINT[1])),
                    min(255, round(b * 255 / GOLD_TINT[2])),
                    a,
                )
            else:
                spx[x, y] = (r, g, b, a)

    band.save(f"{ITEM_DIR}/{name}_band.png")
    stone.save(f"{ITEM_DIR}/{name}_stone.png")

    # Prove the round trip: recomposite band*gold over stone and diff against source.
    worst = 0
    for y in range(16):
        for x in range(16):
            br, bg, bb, ba = bpx[x, y]
            if ba == 0:
                continue
            back = (round(br * GOLD_TINT[0] / 255),
                    round(bg * GOLD_TINT[1] / 255),
                    round(bb * GOLD_TINT[2] / 255))
            worst = max(worst, max(abs(back[i] - px[x, y][i]) for i in range(3)))
    return worst


if __name__ == "__main__":
    worst = 0
    for name in RINGS:
        worst = max(worst, split(name))
    print(f"split {len(RINGS)} rings; worst gold round-trip error: {worst}/255")
