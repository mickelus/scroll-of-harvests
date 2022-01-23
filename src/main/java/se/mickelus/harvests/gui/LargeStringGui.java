package se.mickelus.harvests.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import se.mickelus.mutil.gui.GuiAttachment;
import se.mickelus.mutil.gui.GuiString;
import se.mickelus.mutil.gui.animation.KeyframeAnimation;

public class LargeStringGui extends GuiString {
    public LargeStringGui(int x, int y, String string) {
        super(x / 2, y / 2, string);
    }

    public LargeStringGui(int x, int y, String string, int color) {
        super(x, y, string, color);
    }

    public LargeStringGui(int x, int y, String string, GuiAttachment attachment) {
        super(x, y, string, attachment);
    }

    public LargeStringGui(int x, int y, String string, int color, GuiAttachment attachment) {
        super(x, y, string, color, attachment);
    }

    public void setX(int x) {
        super.setX(x);
    }

    public void setY(int y) {
        super.setY(y);
    }

    public int getX() {
        return super.getX();
    }

    public int getY() {
        return super.getY();
    }

    public int getWidth() {
        return this.width * 2;
    }

    public int getHeight() {
        return this.height * 2;
    }

    protected void calculateFocusState(int refX, int refY, int mouseX, int mouseY) {
        boolean gainFocus = mouseX >= this.getX() + refX && mouseX < this.getX() + refX + this.getWidth() && mouseY >= this.getY() + refY && mouseY < this.getY() + refY + this.getHeight();
        if (gainFocus != this.hasFocus) {
            this.hasFocus = gainFocus;
            if (this.hasFocus) {
                this.onFocus();
            } else {
                this.onBlur();
            }
        }

    }

    public void draw(PoseStack matrixStack, int refX, int refY, int screenWidth, int screenHeight, int mouseX, int mouseY, float opacity) {
        this.calculateFocusState(refX, refY, mouseX, mouseY);
        this.activeAnimations.removeIf((keyframeAnimation) -> !keyframeAnimation.isActive());
        this.activeAnimations.forEach(KeyframeAnimation::preDraw);
        matrixStack.pushPose();
        matrixStack.translate(refX + x, refY + y, 0);
        matrixStack.scale(2, 2, 2);
        drawString(matrixStack, string, 0, 0, color, opacity * getOpacity(), drawShadow);
        matrixStack.popPose();
    }
}
