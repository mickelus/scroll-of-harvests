package se.mickelus.harvests.gui;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import se.mickelus.mutil.gui.GuiAttachment;
import se.mickelus.mutil.gui.GuiElement;
import se.mickelus.mutil.gui.GuiString;

import java.util.List;

public class RowLabelGui extends GuiElement {

    private final GuiString label;
    private final List<Component> tooltip;

    public RowLabelGui(int x, int y, int height, String labelKey) {
        super(x, y, 14, height);

        label = new GuiString(0, 0, I18n.get("harvests." + labelKey));
        label.setShadow(false);
        label.setAttachmentPoint(GuiAttachment.topCenter);
        label.setColor(0);
        label.setOpacity(0.5f);

        tooltip = ImmutableList.of(Component.translatable("harvests." + labelKey + ".tooltip"));
    }

    @Override
    protected void drawChildren(final GuiGraphics graphics, final int refX, final int refY, final int screenWidth, final int screenHeight,
            final int mouseX, final int mouseY, final float opacity) {
        super.drawChildren(graphics, refX, refY, screenWidth, screenHeight, mouseX, mouseY, opacity);
        graphics.pose().pushPose();
        graphics.pose().translate(refX + 3, refY, 0);
        graphics.pose().mulPose(Axis.ZP.rotationDegrees(-90.0F));
        graphics.pose().translate(- (height + label.getWidth()) / 2, 0, 0);
        label.draw(graphics,  0, 0, screenWidth, screenHeight, mouseX, mouseY, opacity);
        graphics.pose().popPose();

    }

    @Override
    public List<Component> getTooltipLines() {
        if (hasFocus()) {
            return tooltip;
        }
        return null;
    }
}
