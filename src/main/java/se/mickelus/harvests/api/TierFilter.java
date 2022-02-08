package se.mickelus.harvests.api;

import net.minecraft.world.item.Tier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class TierFilter {
    private static List<TierFilter> filters = new ArrayList<>();

    public String key;
    public boolean collapseLevels;
    public Predicate<Tier> predicate;

    public TierFilter(String labelKey, boolean collapseLevels, Predicate<Tier> filter) {
        this.key = labelKey;
        this.collapseLevels = collapseLevels;
        this.predicate = filter;
    }

    public static void register(TierFilter filter) {
        filters.add(filter);
    }

    public static List<TierFilter> getFilters() {
        return filters;
    }
}
