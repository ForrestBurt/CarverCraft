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

    // The filled arrow sprite lives to the right of the panel in the sheet.
    private static final int ARROW_U = 176;
    private static final int ARROW_V = 0;

    public RockTumblerScreen(RockTumblerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 190;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // One arrow per lane, revealed left-to-right by width. No trig, no overlap.
        for (int lane = 0; lane < RockTumblerMenu.LANES; lane++) {
            float fraction = menu.getLaneProgress(lane);
            if (fraction <= 0f) {
                continue;
            }
            int filled = Math.max(1, Math.round(RockTumblerMenu.ARROW_W * fraction));
            int arrowY = y + RockTumblerMenu.ROW_Y[lane] + 1;
            guiGraphics.blit(
                    TEXTURE,
                    x + RockTumblerMenu.ARROW_X,
                    arrowY,
                    ARROW_U,
                    ARROW_V,
                    filled,
                    RockTumblerMenu.ARROW_H);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
