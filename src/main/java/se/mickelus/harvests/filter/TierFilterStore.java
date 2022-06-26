package se.mickelus.harvests.filter;

import com.google.gson.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import se.mickelus.harvests.HarvestsMod;
import se.mickelus.harvests.api.TierFilter;
import se.mickelus.mutil.util.JsonOptional;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class TierFilterStore implements ResourceManagerReloadListener {
    private static final Logger logger = LogManager.getLogger();
    private static final Gson gson = new GsonBuilder().create();
    public static TierFilterStore instance;
    private Collection<Tier> tiersWithTools;
    private TierFilter[] filters;

    public TierFilterStore() {
        ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(this);

        instance = this;
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        filters = null;
    }

    public TierFilter[] getFilters() {
        if (filters == null) {
            prepareFilters();
        }
        return filters;
    }

    private void prepareFilters() {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        filters = Stream.concat(
                        resourceManager.listResources("filters/", rl -> rl.getPath().endsWith(".json")).entrySet().stream()
                                .filter(entry -> HarvestsMod.modId.equals(entry.getKey().getNamespace()))
                                .map(entry -> parseFilter(entry.getKey(), entry.getValue()))
                                .filter(Objects::nonNull),
                        TierFilter.getFilters().stream())
                .toArray(TierFilter[]::new);

        if (filters.length == 0) {
            filters = new TierFilter[]{new TierFilter("default", false, tier -> true)};
        }
    }

    @Nullable
    private TierFilter parseFilter(ResourceLocation resourceLocation, Resource resource) {
        try (BufferedReader reader = resource.openAsReader()) {
            return Optional.ofNullable(GsonHelper.fromJson(gson, reader, JsonElement.class))
                    .map(this::deserialize)
                    .orElse(null);
        } catch (IOException | JsonParseException e) {
            logger.warn("Failed to parse tier filter data from '{}': {}", resourceLocation, e);
        }

        return null;
    }

    private TierFilter deserialize(JsonElement json) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String key = JsonOptional.field(jsonObject, "key")
                .map(JsonElement::getAsString)
                .orElse(null);

        boolean collapseLevels = JsonOptional.field(jsonObject, "collapse_levels")
                .map(JsonElement::getAsBoolean)
                .orElse(false);


        Predicate<Tier> predicate = JsonOptional.field(jsonObject, "predicate")
                .map(JsonElement::getAsJsonObject)
                .map(this::getPredicate)
                .orElse(tier -> true);

        return new TierFilter(key, collapseLevels, predicate);
    }

    private Predicate<Tier> getPredicate(JsonObject json) {
        boolean requireTools = JsonOptional.field(json, "tools")
                .map(JsonElement::getAsBoolean)
                .orElse(false);
        boolean requireBlocks = JsonOptional.field(json, "blocks")
                .map(JsonElement::getAsBoolean)
                .orElse(false);
        boolean requireRepairs = JsonOptional.field(json, "repair_material")
                .map(JsonElement::getAsBoolean)
                .orElse(false);

        Collection<String> namespace = JsonOptional.field(json, "namespaces")
                .map(JsonElement::getAsJsonArray)
                .map(array -> StreamSupport.stream(array.spliterator(), false))
                .orElseGet(Stream::empty)
                .map(JsonElement::getAsString)
                .collect(Collectors.toSet());

        Collection<ResourceLocation> allowList = JsonOptional.field(json, "allow")
                .map(JsonElement::getAsJsonArray)
                .map(array -> StreamSupport.stream(array.spliterator(), false))
                .orElseGet(Stream::empty)
                .map(JsonElement::getAsString)
                .map(ResourceLocation::new)
                .collect(Collectors.toSet());

        Collection<ResourceLocation> rejectList = JsonOptional.field(json, "reject")
                .map(JsonElement::getAsJsonArray)
                .map(array -> StreamSupport.stream(array.spliterator(), false))
                .orElseGet(Stream::empty)
                .map(JsonElement::getAsString)
                .map(ResourceLocation::new)
                .collect(Collectors.toSet());

        return tier -> {
            ResourceLocation rl = TierSortingRegistry.getName(tier);

            if (requireTools && !getTiersWithTools().contains(tier)) {
                return false;
            }

            if (requireBlocks && (tier.getTag() == null
                    || !ForgeRegistries.BLOCKS.tags().isKnownTagName(tier.getTag())
                    || ForgeRegistries.BLOCKS.tags().getTag(tier.getTag()).isEmpty())) {
                return false;
            }

            if (requireRepairs && (tier.getRepairIngredient() == null || tier.getRepairIngredient().isEmpty())) {
                return false;
            }

            if (!namespace.isEmpty() && (rl == null || !namespace.contains(rl.getNamespace()))) {
                return false;
            }

            if (!allowList.isEmpty() && !allowList.contains(rl)) {
                return false;
            }

            if (!rejectList.isEmpty() && rejectList.contains(rl)) {
                return false;
            }

            return true;
        };
    }

    private Collection<Tier> getTiersWithTools() {
        if (tiersWithTools == null) {
            tiersWithTools = ForgeRegistries.ITEMS.getValues().stream()
                    .filter(item -> item instanceof TieredItem)
                    .map(item -> (TieredItem) item)
                    .map(TieredItem::getTier)
                    .collect(Collectors.toSet());
        }
        return tiersWithTools;
    }
}
