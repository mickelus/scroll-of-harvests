package se.mickelus.harvests.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import se.mickelus.mutil.gui.GuiElement;
import se.mickelus.mutil.gui.impl.GuiHorizontalScrollable;

public class ScrollBarGui extends GuiElement {
    private final GuiHorizontalScrollable scrollable;

    private final boolean unscrollableHidden;

    public ScrollBarGui(int x, int y, int width, GuiHorizontalScrollable scrollable) {
        this(x, y, width, scrollable, false);
    }

    public ScrollBarGui(int x, int y, int width, GuiHorizontalScrollable scrollable, boolean unscrollableHidden) {
        super(x, y, width, 5);

        this.scrollable = scrollable;
        this.unscrollableHidden = unscrollableHidden;
    }

    private boolean isActive() {
        return !unscrollableHidden || scrollable.getOffsetMax() > 0;
    }

    @Override
    public void draw(PoseStack matrixStack, int refX, int refY, int screenWidth, int screenHeight, int mouseX, int mouseY, float opacity) {
        if (isActive()) {
            super.draw(matrixStack, refX, refY, screenWidth, screenHeight, mouseX, mouseY, opacity);

            drawRect(matrixStack, refX + x, refY + y + 1, refX + x + width, refY + y + 3, 0xffffff, opacity * 0.2f);
            int contentWidth = scrollable.getOffsetMax() + scrollable.getWidth();

            int handleWidth = Math.max(3, (int) (scrollable.getWidth() * 1f / contentWidth * width) + 1);
            int handleOffset = (int) (scrollable.getOffset() / contentWidth * width);

            drawRect(matrixStack, refX + x + handleOffset, refY + y + 1, refX + x + handleWidth + handleOffset, refY + y + 3, 0xffffff, opacity * 0.5f);
        }
    }
}
