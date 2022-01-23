package se.mickelus.harvests.gui;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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

        tooltip = ImmutableList.of(new TranslatableComponent("harvests." + labelKey + ".tooltip"));
    }

    @Override
    protected void drawChildren(PoseStack matrixStack, int refX, int refY, int screenWidth, int screenHeight, int mouseX, int mouseY, float opacity) {
        super.drawChildren(matrixStack, refX, refY, screenWidth, screenHeight, mouseX, mouseY, opacity);
        matrixStack.pushPose();
        matrixStack.translate(refX + 3, refY, 0);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-90.0F));
        matrixStack.translate(- (height + label.getWidth()) / 2, 0, 0);
        label.draw(matrixStack,  0, 0,
                screenWidth, screenHeight, mouseX, mouseY, opacity);
        matrixStack.popPose();

    }

    @Override
    public List<Component> getTooltipLines() {
        if (hasFocus()) {
            return tooltip;
        }
        return null;
    }
}
