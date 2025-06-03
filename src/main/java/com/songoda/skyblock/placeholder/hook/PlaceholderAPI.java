package com.songoda.skyblock.placeholder.hook;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.placeholder.PlaceholderProcessor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPI extends PlaceholderExpansion {
    private final SkyBlock plugin;
    private final PlaceholderProcessor placeholderProcessor;

    public PlaceholderAPI(SkyBlock plugin) {
        this.plugin = plugin;
        this.placeholderProcessor = new PlaceholderProcessor();
    }

    public @NotNull String getIdentifier() {
        return "fabledskyblock";
    }

    public @NotNull String getAuthor() {
        return this.plugin.getDescription().getAuthors().get(0);
    }

    public @NotNull String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    public boolean persist() {
        return true;
    }

    public String onPlaceholderRequest(Player player, @NotNull String params) {
        return this.placeholderProcessor.processPlaceholder(player, "fabledskyblock_" + params);
    }
}
