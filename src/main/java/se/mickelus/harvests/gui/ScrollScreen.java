package se.mickelus.harvests.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TieredItem;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import se.mickelus.harvests.HarvestsMod;
import se.mickelus.mutil.gui.*;
import se.mickelus.mutil.gui.impl.GuiHorizontalLayoutGroup;
import se.mickelus.mutil.gui.impl.GuiHorizontalScrollable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ScrollScreen extends Screen {
    private static final ResourceLocation scrollButtonTexture = new ResourceLocation(HarvestsMod.modId, "textures/gui/scroll_button.png");
    private static final ResourceLocation scrollTexture = new ResourceLocation(HarvestsMod.modId, "textures/gui/scroll.png");

    private GuiElement defaultGui;

    protected ScrollScreen() {
        super(new TextComponent("harvests:gui_title"));

        defaultGui = new GuiElement(0, 0, 420, 240);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onScreenInit(ScreenEvent.InitScreenEvent.Post event) {
        if (event.getScreen() instanceof InventoryScreen screen) {
            Component tooltip = new TranslatableComponent("harvests.scroll_button.tooltip");
            event.addListener(new ImageButton(screen.getGuiLeft() + 125, screen.height / 2 - 22, 18, 18, 0,
                    0, 19, scrollButtonTexture, 256, 256,
                    button -> Minecraft.getInstance().setScreen(new ScrollScreen()),
                    new Button.OnTooltip() {
                        public void onTooltip(Button button, PoseStack poseStack, int mouseX, int mouseY) {
                            screen.renderTooltip(poseStack, tooltip, mouseX, mouseY);
                        }

                        public void narrateTooltip(Consumer<Component> narrationCallback) {
                            narrationCallback.accept(tooltip);
                        }
                    },
                    tooltip));
        }
    }

    @Override
    protected void init() {
        super.init();

        defaultGui.clearChildren();

        List<TieredItem> tieredItems = ForgeRegistries.ITEMS.getValues().stream()
                .filter(item -> item instanceof TieredItem)
                .map(item -> (TieredItem) item)
                .collect(Collectors.toList());

        defaultGui.addChild(new GuiTexture(0, 46, 46, 154, 0, 0, scrollTexture));
        defaultGui.addChild(new GuiTexture(46, 46, 164, 154, 46, 0, scrollTexture));
        defaultGui.addChild(new GuiTexture(210, 46, 164, 154, 46, 0, scrollTexture));
        defaultGui.addChild(new GuiTexture(374, 46, 46, 154, 210, 0, scrollTexture));

        ClipRectGui clipRect = new ClipRectGui(17, 52, 394, 142);
        defaultGui.addChild(clipRect);

        GuiHorizontalScrollable scrollable = new GuiHorizontalScrollable(0, 0, 394, 142);
        clipRect.addChild(scrollable);

        GuiHorizontalLayoutGroup horizontalLayout = new GuiHorizontalLayoutGroup(0, 0, 142, 2);
        scrollable.addChild(horizontalLayout);

        TierSortingRegistry.getSortedTiers().stream()
                .map(tier -> new TierGui(0, 0, tier, tieredItems))
                .forEach(horizontalLayout::addChild);

        defaultGui.addChild(new ScrollBarGui(0, -25, 180, 3, scrollable, true).setAttachment(GuiAttachment.bottomCenter));

        defaultGui.addChild(new GuiTexture(11, 52, 5, 49, 128, 160, scrollTexture));
        defaultGui.addChild(new GuiTexture(11, 102, 5, 93, 137, 160, scrollTexture));
        defaultGui.addChild(new GuiTexture(-3, 52, 5, 49, 132, 160, scrollTexture).setAttachment(GuiAttachment.topRight));
        defaultGui.addChild(new GuiTexture(-3, 102, 5, 93, 141, 160, scrollTexture).setAttachment(GuiAttachment.topRight));

        defaultGui.addChild(new RowLabelGui(1, 52, 20, "tier"));
        defaultGui.addChild(new RowLabelGui(1, 52 + 21, 27, "material"));
        defaultGui.addChild(new RowLabelGui(1, 52 + 49, 43, "tools"));
        defaultGui.addChild(new RowLabelGui(1, 52 + 98, 44, "blocks"));

        defaultGui.addChild(new GuiTexture(-2, 48, 19, 12, 235, 154, scrollTexture).setAttachment(GuiAttachment.topRight));
        defaultGui.addChild(new GuiTexture(-2, 188, 19, 10, 235, 166, scrollTexture).setAttachment(GuiAttachment.topRight));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);

        defaultGui.updateFocusState((width - defaultGui.getWidth()) / 2, (height - defaultGui.getHeight()) / 2, mouseX, mouseY);
        defaultGui.draw(poseStack, (width - defaultGui.getWidth()) / 2, (height - defaultGui.getHeight()) / 2,
                width, height, mouseX, mouseY, 1);

        renderHoveredToolTip(poseStack, mouseX, mouseY);
    }

    protected void renderHoveredToolTip(PoseStack matrixStack, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        matrixStack.pushPose();
        matrixStack.translate(0.0D, 0.0D, 200.0D);
        List<Component> tooltipLines = defaultGui.getTooltipLines();
        if (tooltipLines != null) {
            renderTooltip(matrixStack, tooltipLines, Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (defaultGui.onMouseClick((int) x, (int) y, button)) {
            return true;
        }

        return super.mouseClicked(x, y, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double distance) {
        if (defaultGui.onMouseScroll(mouseX, mouseY, distance)) {
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, distance);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (defaultGui.onKeyPress(keyCode, scanCode, modifiers)) {
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (defaultGui.onKeyRelease(keyCode, scanCode, modifiers)) {
            return true;
        }

        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char typedChar, int keyCode) {
        if (defaultGui.onCharType(typedChar, keyCode)) {
            return true;
        }

        return false;
    }
}
