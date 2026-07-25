package com.forrestb.carvercraft.client.screen;

import com.forrestb.carvercraft.block.entity.AlloyerBlockEntity;
import com.forrestb.carvercraft.menu.AlloyerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

/** The crucible screen. Same visual language as the lapidary machines, one clock. */
public class AlloyerScreen extends AbstractContainerScreen<AlloyerMenu> {

    // Filled arrow sprite, right of the panel; filled energy column below it.
    private static final int ARROW_U = 176;
    private static final int ARROW_V = 0;
    private static final int ENERGY_U = 176;
    private static final int ENERGY_V = 16;

    public AlloyerScreen(AlloyerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.blit(AlloyerBlockEntity.GUI, x, y, 0, 0, imageWidth, imageHeight);

        float fraction = menu.getProgress();
        if (fraction > 0f) {
            int filled = Math.max(1, Math.round(AlloyerMenu.ARROW_W * fraction));
            guiGraphics.blit(AlloyerBlockEntity.GUI,
                    x + AlloyerMenu.ARROW_X,
                    y + AlloyerMenu.ARROW_Y,
                    ARROW_U, ARROW_V,
                    filled, AlloyerMenu.ARROW_H);
        }

        float energy = menu.getEnergyFraction();
        if (energy > 0f) {
            int filled = Math.max(1, Math.round(AlloyerMenu.ENERGY_H * energy));
            int top = AlloyerMenu.ENERGY_Y + (AlloyerMenu.ENERGY_H - filled);
            guiGraphics.blit(AlloyerBlockEntity.GUI,
                    x + AlloyerMenu.ENERGY_X,
                    y + top,
                    ENERGY_U, ENERGY_V + (AlloyerMenu.ENERGY_H - filled),
                    AlloyerMenu.ENERGY_W, filled);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);

        // Hovering the gauge reports the actual numbers.
        int x = (width - imageWidth) / 2 + AlloyerMenu.ENERGY_X;
        int y = (height - imageHeight) / 2 + AlloyerMenu.ENERGY_Y;
        if (mouseX >= x && mouseX < x + AlloyerMenu.ENERGY_W
                && mouseY >= y && mouseY < y + AlloyerMenu.ENERGY_H) {
            guiGraphics.renderComponentTooltip(this.font,
                    List.of(Component.literal(menu.getEnergyStored() + " / " + menu.getEnergyCapacity() + " FE")),
                    mouseX, mouseY);
        }
    }
}
