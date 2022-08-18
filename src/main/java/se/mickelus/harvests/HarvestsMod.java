package se.mickelus.harvests;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import se.mickelus.harvests.filter.TierFilterStore;
import se.mickelus.harvests.gui.ScrollScreen;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
@Mod(HarvestsMod.modId)
public class HarvestsMod {

    public static final String modId = "harvests";

    public HarvestsMod() {
        ConfigHandler.setup();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

        new TierFilterStore();
    }

    public void clientSetup(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(ScrollScreen.class);
    }
}
