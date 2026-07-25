package com.forrestb.carvercraft.client.screen;

import com.forrestb.carvercraft.menu.LapidaryMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

/**
 * One screen for every lapidary machine. The background texture comes from the block
 * entity, so a new machine needs a texture rather than a new class.
 */
public class LapidaryScreen extends AbstractContainerScreen<LapidaryMenu> {

    // Filled progress arrow sprite, right of the panel in every machine's sheet.
    private static final int ARROW_U = 176;
    private static final int ARROW_V = 0;
    // Filled energy gauge sprite, below the arrow.
    private static final int ENERGY_U = 176;
    private static final int ENERGY_V = 16;

    public LapidaryScreen(LapidaryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 190;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    private ResourceLocation texture() {
        return menu.blockEntity.guiTexture();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        ResourceLocation tex = texture();
        guiGraphics.blit(tex, x, y, 0, 0, imageWidth, imageHeight);

        // Lane arrows: left-to-right width reveal.
        for (int lane = 0; lane < LapidaryMenu.LANES; lane++) {
            float fraction = menu.getLaneProgress(lane);
            if (fraction <= 0f) {
                continue;
            }
            int filled = Math.max(1, Math.round(LapidaryMenu.ARROW_W * fraction));
            guiGraphics.blit(tex,
                    x + LapidaryMenu.ARROW_X,
                    y + LapidaryMenu.ROW_Y[lane] + 1,
                    ARROW_U, ARROW_V,
                    filled, LapidaryMenu.ARROW_H);
        }

        // Energy gauge fills from the bottom up.
        if (menu.hasEnergy()) {
            float fraction = menu.getEnergyFraction();
            if (fraction > 0f) {
                int filled = Math.max(1, Math.round(LapidaryMenu.ENERGY_H * fraction));
                int top = LapidaryMenu.ENERGY_Y + (LapidaryMenu.ENERGY_H - filled);
                guiGraphics.blit(tex,
                        x + LapidaryMenu.ENERGY_X,
                        y + top,
                        ENERGY_U, ENERGY_V + (LapidaryMenu.ENERGY_H - filled),
                        LapidaryMenu.ENERGY_W, filled);
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);

        // Hovering the gauge reports the actual numbers.
        if (menu.hasEnergy()) {
            int x = (width - imageWidth) / 2 + LapidaryMenu.ENERGY_X;
            int y = (height - imageHeight) / 2 + LapidaryMenu.ENERGY_Y;
            if (mouseX >= x && mouseX < x + LapidaryMenu.ENERGY_W
                    && mouseY >= y && mouseY < y + LapidaryMenu.ENERGY_H) {
                guiGraphics.renderComponentTooltip(this.font,
                        List.of(Component.literal(menu.getEnergyStored() + " / " + menu.getEnergyCapacity() + " FE")),
                        mouseX, mouseY);
            }
        }
    }
}
