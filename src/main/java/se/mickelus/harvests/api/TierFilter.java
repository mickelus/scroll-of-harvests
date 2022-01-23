package se.mickelus.harvests.api;

import net.minecraft.world.item.Tier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class TierFilter {

    private static List<TierFilter> filters = new ArrayList<>();

    String labelKey;
    boolean useRegistryLevels = true;
    Predicate<Tier> filter;

    public TierFilter(String labelKey, boolean useRegistryLevels, Predicate<Tier> filter) {
        this.labelKey = labelKey;
        this.useRegistryLevels = useRegistryLevels;
        this.filter = filter;
    }

    public static void register(TierFilter filter) {
        filters.add(filter);
    }

    public static List<TierFilter> getFilters() {
        return filters;
    }
}
