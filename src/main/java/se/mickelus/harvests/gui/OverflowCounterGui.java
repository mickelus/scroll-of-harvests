package se.mickelus.harvests.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import se.mickelus.mutil.gui.GuiAttachment;
import se.mickelus.mutil.gui.GuiElement;
import se.mickelus.mutil.gui.GuiString;

import java.util.List;
import java.util.stream.Collectors;

public class OverflowCounterGui extends GuiElement {
    private static final int tooltipCap = 20;
    private  int tooltipOffset = 0;
    private final List<Component> labels;
    private List<Component> tooltip;

    public OverflowCounterGui(int x, int y, List<Component> labels) {
        super(x, y, 16, 16);

        this.labels = labels;
        updateTooltip();

        addChild(new GuiString(0, 0, "+" + labels.size(), 0)
                .setShadow(false)
                .setOpacity(0.7f)
                .setAttachment(GuiAttachment.middleCenter));
    }

    private void updateTooltip() {
        if (labels.size() > tooltipCap) {
            tooltip = labels.stream()
                    .skip(Math.min(tooltipOffset, labels.size() - tooltipCap))
                    .limit(tooltipCap)
                    .collect(Collectors.toList());
            tooltip.add(new TextComponent(""));
            tooltip.add(new TranslatableComponent("harvests.overflow_scroll").withStyle(ChatFormatting.GRAY));
        } else {
            tooltip = labels;
        }
    }

    @Override
    public List<Component> getTooltipLines() {
        if (hasFocus()) {
            return tooltip;
        }
        return null;
    }

    @Override
    public boolean onMouseScroll(double mouseX, double mouseY, double distance) {
        if (hasFocus()) {
            tooltipOffset = Mth.clamp(tooltipOffset + (int) Math.signum(-distance), 0, labels.size() - tooltipCap);
            updateTooltip();
            return true;
        }

        return false;
    }
}
