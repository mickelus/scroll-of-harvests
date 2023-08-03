package se.mickelus.harvests.gui;

import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;
import org.apache.commons.lang3.StringUtils;
import se.mickelus.harvests.HarvestsMod;
import se.mickelus.mutil.gui.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TierGui extends GuiElement {
    private static final ResourceLocation texture = new ResourceLocation(HarvestsMod.modId, "textures/gui/scroll.png");
    private final GuiString nameElement;
    private final List<Component> nameTooltip;

    public TierGui(int x, int y, Tier tier, int index, List<TieredItem> tieredItems) {
        super(x, y, 64, 142);

        Font font = Minecraft.getInstance().font;

        int tierNumber = index + 1;
        String tierLabel = I18n.get("harvests.tier." + tierNumber);
        addChild(new LargeStringGui(1, 4, tierLabel, 0)
                .setShadow(false)
                .setOpacity(0.35f)
                .setAttachment(GuiAttachment.topCenter));
        int tierWidth = font.width(tierLabel) * 2;

        // some mods seem to return null as getRepairIngredient even though it's annotated as NoNNull
        Optional.ofNullable(tier.getRepairIngredient())
                .map(Ingredient::getItems)
                .filter(items -> items.length > 0)
                .map(items -> items[0])
                .map(itemStack -> new GuiItem(0, 26)
                        .setItem(itemStack)
                        .setResetDepthTest(false)
                        .setAttachment(GuiAttachment.topCenter))
                .ifPresent(this::addChild);


        ResourceLocation tierIdentifier = TierSortingRegistry.getName(tier);
        String tierName = font.plainSubstrByWidth(StringUtils.capitalize(tierIdentifier.getPath().replace("_", " ")), 60);
        nameElement = new GuiString(0, 7, tierName, 0);
        nameElement.setShadow(false);
        nameElement.setAttachment(GuiAttachment.topCenter);
        addChild(nameElement);

        nameTooltip = ImmutableList.of(Component.literal(tierIdentifier.toString()),
                Component.translatable("harvests.level",
                        ChatFormatting.GRAY.toString() + tierNumber + ChatFormatting.RESET)
                        .withStyle(ChatFormatting.DARK_GRAY),
                Component.translatable("harvests.durability",
                        ChatFormatting.GRAY.toString() + tier.getUses() + ChatFormatting.RESET)
                        .withStyle(ChatFormatting.DARK_GRAY),
                Component.translatable("harvests.efficiency",
                        ChatFormatting.GRAY.toString() + tier.getSpeed() + ChatFormatting.RESET)
                        .withStyle(ChatFormatting.DARK_GRAY),
                Component.translatable("harvests.damage",
                        ChatFormatting.GRAY.toString() + tier.getAttackDamageBonus() + ChatFormatting.RESET)
                        .withStyle(ChatFormatting.DARK_GRAY),
                Component.translatable("harvests.enchantability",
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
                    .setResetDepthTest(false)
                    .setRenderDecoration(false));
        }

        if (toolOverflow) {
            List<Component> overflowToolNames = applicableTools.stream()
                    .skip(toolCount)
                    .map(Item::getDefaultInstance)
                    .map(ItemStack::getHoverName)
                    .collect(Collectors.toList());

            addChild(new OverflowCounterGui(43, 74, overflowToolNames));
        }

        List<Block> applicableBlocks = Optional.ofNullable(tier.getTag())
                .filter(ForgeRegistries.BLOCKS.tags()::isKnownTagName)
                .map(ForgeRegistries.BLOCKS.tags()::getTag)
                .stream()
                .flatMap(ITag::stream)
                .collect(Collectors.toList());
        boolean blockOverflow = applicableBlocks.size() > 6;
        int blockCount = Math.min(blockOverflow ? 5 : 6, applicableBlocks.size());

        for (int i = 0; i < blockCount; i++) {
            addChild(new GuiItem((i % 3) * 19 + 5, 102 + (i / 3) * 19)
                    .setItem(applicableBlocks.get(i).asItem().getDefaultInstance())
                    .setResetDepthTest(false)
                    .setRenderDecoration(false));
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
    }

    @Override
    public List<Component> getTooltipLines() {
        if (nameElement.hasFocus()) {
            return nameTooltip;
        }
        return super.getTooltipLines();
    }
}
