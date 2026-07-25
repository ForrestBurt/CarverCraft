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

    // The rotating-arrow sprite (a circular progress ring) lives in the same texture sheet.
    private static final int ARROW_U = 176;
    private static final int ARROW_V = 0;
    private static final int ARROW_SIZE = 24;
    private static final int ARROW_X = 99;
    private static final int ARROW_Y = 34;

    public RockTumblerScreen(RockTumblerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        // Keep the title over the slots and the inventory label where players expect them.
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // Draw the progress ring, cropped to the current fraction (fills clockwise via height reveal).
        float fraction = menu.getProgressFraction();
        if (fraction > 0f) {
            int filled = Math.max(1, (int) (ARROW_SIZE * fraction));
            guiGraphics.blit(
                    TEXTURE,
                    x + ARROW_X,
                    y + ARROW_Y + (ARROW_SIZE - filled),
                    ARROW_U,
                    ARROW_V + (ARROW_SIZE - filled),
                    ARROW_SIZE,
                    filled);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
