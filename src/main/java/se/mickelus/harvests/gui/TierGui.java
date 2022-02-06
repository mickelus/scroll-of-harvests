package se.mickelus.harvests.gui;

import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.TierSortingRegistry;
import org.apache.commons.lang3.StringUtils;
import se.mickelus.harvests.HarvestsMod;
import se.mickelus.mutil.gui.*;

import java.util.List;
import java.util.stream.Collectors;

public class TierGui extends GuiElement {
    private static final ResourceLocation texture = new ResourceLocation(HarvestsMod.modId, "textures/gui/scroll.png");
    private final GuiString nameElement;
    private final List<Component> nameTooltip;

    public TierGui(int x, int y, Tier tier, List<TieredItem> tieredItems) {
        super(x, y, 64, 142);

        Font font = Minecraft.getInstance().font;

        int tierNumber = TierSortingRegistry.getTiersLowerThan(tier).size() + 1;
        String tierLabel = I18n.get("harvests.tier." + tierNumber);
        addChild(new LargeStringGui(1, 4, tierLabel, 0)
                .setShadow(false)
                .setOpacity(0.15f)
                .setAttachment(GuiAttachment.topCenter));
        int tierWidth = font.width(tierLabel) * 2;

        ItemStack[] repairItems = tier.getRepairIngredient().getItems();
        if (repairItems.length > 0) {
            addChild(new GuiItem(0, 26)
                    .setItem(repairItems[0])
                    .setResetDepthTest(false)
                    .setAttachment(GuiAttachment.topCenter));
        }

        ResourceLocation tierIdentifier = TierSortingRegistry.getName(tier);
        String tierName = font.plainSubstrByWidth(StringUtils.capitalize(tierIdentifier.getPath().replace("_", " ")), 60);
        nameElement = new GuiString(0, 7, tierName, 0);
        nameElement.setShadow(false);
        nameElement.setAttachment(GuiAttachment.topCenter);
        addChild(nameElement);

        nameTooltip = ImmutableList.of(new TextComponent(tierIdentifier.toString()),
                new TranslatableComponent("harvests.level",
                        ChatFormatting.GRAY.toString() + tierNumber + ChatFormatting.RESET)
                        .withStyle(ChatFormatting.DARK_GRAY),
                new TranslatableComponent("harvests.durability",
                        ChatFormatting.GRAY.toString() + tier.getUses() + ChatFormatting.RESET)
                        .withStyle(ChatFormatting.DARK_GRAY),
                new TranslatableComponent("harvests.efficiency",
                        ChatFormatting.GRAY.toString() + tier.getSpeed() + ChatFormatting.RESET)
                        .withStyle(ChatFormatting.DARK_GRAY),
                new TranslatableComponent("harvests.damage",
                        ChatFormatting.GRAY.toString() + tier.getAttackDamageBonus() + ChatFormatting.RESET)
                        .withStyle(ChatFormatting.DARK_GRAY),
                new TranslatableComponent("harvests.enchantability",
                        ChatFormatting.GRAY.toString() + tier.getEnchantmentValue() + ChatFormatting.RESET)
                        .withStyle(ChatFormatting.DARK_GRAY));

        List<TieredItem> applicableTools = tieredItems.stream()
                .filter(item -> tier.equals(item.getTier()))
                .collect(Collectors.toList());
        boolean toolOverflow = applicableTools.size() > 6;
        int toolCount = Math.min(toolOverflow ? 5 : 6, applicableTools.size());

        for (int i = 0; i < toolCount; i++) {
            addChild(new GuiItem((i % 3) * 19 + 5, 54 + (i / 3) * 19)
                    .setItem(applicableTools.get(i).getDefaultInstance())
                    .setResetDepthTest(false));
        }

        if (toolOverflow) {
            List<Component> overflowToolNames = applicableTools.stream()
                    .skip(toolCount)
                    .map(Item::getDefaultInstance)
                    .map(ItemStack::getHoverName)
                    .collect(Collectors.toList());

            addChild(new OverflowCounterGui(43, 74, overflowToolNames));
        }

        List<Block> applicableBlocks = tier.getTag().getValues();
        boolean blockOverflow = applicableBlocks.size() > 6;
        int blockCount = Math.min(blockOverflow ? 5 : 6, applicableBlocks.size());

        for (int i = 0; i < blockCount; i++) {
            addChild(new GuiItem((i % 3) * 19 + 5, 102 + (i / 3) * 19)
                    .setItem(applicableBlocks.get(i).asItem().getDefaultInstance())
                    .setResetDepthTest(false));
        }

        if (blockOverflow) {
            List<Component> overflowToolNames = applicableBlocks.stream()
                    .skip(blockCount)
                    .map(Block::getName)
                    .collect(Collectors.toList());

            addChild(new OverflowCounterGui(43, 122, overflowToolNames));
        }

        GuiElement outlines = new GuiElement(0, 0, width, height);
        addChild(outlines);

        outlines.addChild(new GuiTexture(0, 0, 64, 13, 64, 160, texture));
        outlines.addChild(new GuiTexture(0, 21, 64, 27, 64, 187, texture));
        outlines.addChild(new GuiTexture(0, 47, 64, 95, 0, 160, texture));

        if (tierWidth < 50) {
            outlines.addChild(new GuiTexture(tierWidth / -2 - 6, 4, 6, 14, 66, 173, texture).setAttachmentAnchor(GuiAttachment.topCenter).setOpacity(0.5f));
            outlines.addChild(new GuiTexture(tierWidth / 2, 4, 6, 14, 72, 173, texture).setAttachmentAnchor(GuiAttachment.topCenter).setOpacity(0.5f));
        }

        outlines.getChildren(GuiTexture.class).forEach(tex -> tex.setUseDefaultBlending(false));
    }

    @Override
    public List<Component> getTooltipLines() {
        if (nameElement.hasFocus()) {
            return nameTooltip;
        }
        return super.getTooltipLines();
    }
}
