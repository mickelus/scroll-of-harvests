package se.mickelus.harvests.gui;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import se.mickelus.harvests.api.TierFilter;
import se.mickelus.mutil.gui.GuiAttachment;
import se.mickelus.mutil.gui.GuiClickable;
import se.mickelus.mutil.gui.GuiString;
import se.mickelus.mutil.gui.impl.GuiColors;

import java.util.List;
import java.util.function.Consumer;

public class FilterTabGui extends GuiClickable {
    private final List<Component> tooltip;
    private final TierFilter filter;
    private final GuiString label;
    private boolean isSelected;

    public FilterTabGui(int x, int y, TierFilter filter, Consumer<TierFilter> onClick) {
        super(x, y, 0, 13, () -> onClick.accept(filter));

        this.filter = filter;

        label = new GuiString(0, 4, I18n.get("harvests.filter." + filter.key));
        label.setAttachment(GuiAttachment.topCenter);
        addChild(label);

        setWidth(label.getWidth() + 10);

        tooltip = ImmutableList.of(new TranslatableComponent("harvests.filter." + filter.key + ".tooltip"));
    }

    public void updateSelectedFilter(TierFilter filter) {
        isSelected = this.filter.equals(filter);

        updateState(hasFocus());
    }

    private void updateState(boolean hasFocus) {
        setOpacity(isSelected ? 1 : hasFocus ? 0.8f : 0.4f);

        label.setColor(hasFocus ? GuiColors.hover : GuiColors.normal);
    }

    @Override
    public List<Component> getTooltipLines() {
        if (hasFocus()) {
            return tooltip;
        }
        return null;
    }

    @Override
    protected void onFocus() {
        super.onFocus();
        updateState(true);
    }

    @Override
    protected void onBlur() {
        super.onBlur();
        updateState(false);
    }
}
