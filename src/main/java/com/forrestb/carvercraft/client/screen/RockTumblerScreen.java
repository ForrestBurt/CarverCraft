package com.forrestb.carvercraft.client.screen;

import com.forrestb.carvercraft.CarverCraft;
import com.forrestb.carvercraft.menu.RockTumblerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class RockTumblerScreen extends AbstractContainerScreen<RockTumblerMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CarverCraft.MODID, "textures/gui/rock_tumbler.png");

    private static final int RING_SIZE = 24;
    // Where the ring sits on the panel.
    private static final int RING_X = 99;
    private static final int RING_Y = 34;

    // 16 pre-rendered fill frames baked into the sheet, laid out in a 3-wide grid.
    private static final int FRAMES = 16;
    private static final int FRAME_ORIGIN_U = 176;
    private static final int FRAME_ORIGIN_V = 24;
    private static final int FRAME_COLS = 3;

    public RockTumblerScreen(RockTumblerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        float fraction = menu.getProgressFraction();
        if (fraction > 0f) {
            // Pick the frame whose fill best matches the current fraction.
            int frame = Math.min(FRAMES - 1, Math.max(0, Math.round(fraction * FRAMES) - 1));
            int u = FRAME_ORIGIN_U + (frame % FRAME_COLS) * RING_SIZE;
            int v = FRAME_ORIGIN_V + (frame / FRAME_COLS) * RING_SIZE;
            guiGraphics.blit(TEXTURE, x + RING_X, y + RING_Y, u, v, RING_SIZE, RING_SIZE);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
