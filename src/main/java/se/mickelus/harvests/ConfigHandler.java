package se.mickelus.harvests;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigHandler {
    public static Client client;
    static ForgeConfigSpec clientSpec;

    public static void setup() {
        if (FMLEnvironment.dist.isClient()) {
            setupClient();
            ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, clientSpec);
            FMLJavaModLoadingContext.get().getModEventBus().register(ConfigHandler.client);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void setupClient() {
        final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
        clientSpec = specPair.getRight();
        client = specPair.getLeft();
    }

    @OnlyIn(Dist.CLIENT)
    public static class Client {
        public ForgeConfigSpec.IntValue buttonX;
        public ForgeConfigSpec.IntValue buttonY;

        Client(ForgeConfigSpec.Builder builder) {
            buttonX = builder
                    .comment("Horizontal positioning for the scroll button in the player inventory")
                    .defineInRange("button_x", 125, -1000, 1000);
            buttonY = builder
                    .comment("Horizontal positioning for the scroll button in the player inventory")
                    .defineInRange("button_y", 61, -1000, 1000);
        }
    }
}
