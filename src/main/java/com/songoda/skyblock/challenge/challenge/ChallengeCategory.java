package com.songoda.skyblock.challenge.challenge;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.third_party.com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ChallengeCategory {
    private final int id;
    private final String name;
    private final HashMap<Integer, Challenge> challenges;

    public ChallengeCategory(int id, String name, FileConfiguration config) {
        this.id = id;
        this.name = name;
        this.challenges = new HashMap<>();
        loadChallenges(config);
    }

    private void loadChallenges(FileConfiguration config) {
        ConfigurationSection section = config.getConfigurationSection("challenges." + this.id + ".challenges");
        if (section == null) {
            return; // No challenge here
        }
        Set<String> keys = section.getKeys(false);
        for (String k : keys) {
            String key = "challenges." + this.id + ".challenges." + k;
            int id = config.getInt(key + ".id");
            if (id == 0) {
                throw new IllegalArgumentException("Invalid id at category " + this.name + "(" + this.id
                        + ") at challenge " + this.name + "(" + id + ")");
            }
            String name = ChatColor.translateAlternateColorCodes('&', config.getString(key + ".name"));
            List<String> require = toColor(config.getStringList(key + ".require"));
            List<String> reward = toColor(config.getStringList(key + ".reward"));
            int maxTimes = 0;
            try {
                Integer.parseInt(config.getString(key + ".maxtimes", "unlimited"));
                maxTimes = config.getInt(key + ".maxtimes");
            } catch (NumberFormatException ignored) {
                if (config.getString(key + ".maxtimes", "unlimited").equalsIgnoreCase("unlimited")) {
                    maxTimes = Integer.MAX_VALUE;
                }
            }
            boolean showInChat = config.getBoolean(key + ".showInChat");
            // Item
            boolean show = config.getBoolean(key + ".item.show");
            int row = show ? config.getInt(key + ".item.row") : 0;
            int col = show ? config.getInt(key + ".item.col") : 0;
            String strItem = show ? config.getString(key + ".item.item") : "AIR";
            if (strItem == null) {
                strItem = "AIR";
            }
            int amount = show ? config.getInt(key + ".item.amount") : 0;
            List<String> lore = show ? toColor(config.getStringList(key + ".item.lore")) : new ArrayList<>();
            try {
                // If an Exception occurs, we don't handle it here but in parent class
                Optional<XMaterial> compatibleMaterial = CompatibleMaterial.getMaterial(strItem);
                if (!compatibleMaterial.isPresent()) {
                    throw new IllegalArgumentException("Item " + strItem + " isn't a correct material");
                }
                ItemChallenge ic = new ItemChallenge(show, row, col, compatibleMaterial.get(), amount, lore);
                Challenge c = new Challenge(this, id, name, maxTimes, showInChat, require, reward, ic);
                this.challenges.put(id, c);
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Exception at category " + this.name.replace("&", "") + "(" + this.id
                        + ") at challenge " + name.replace("&", "") + "(" + id + "): " + ex.getMessage());
            }
        }
        Bukkit.getConsoleSender().sendMessage("[FabledSkyBlock] " + ChatColor.GREEN + "Category " + this.name + ChatColor.GREEN
                + " loaded with " + ChatColor.GOLD + this.challenges.size() + ChatColor.GREEN + " challenges");
    }

    private List<String> toColor(List<String> list) {
        List<String> copy = new ArrayList<>();
        if (list == null) {
            return copy;
        }
        for (String str : list) {
            copy.add(ChatColor.translateAlternateColorCodes('&', str));
        }
        return copy;
    }

    // GETTERS

    public Challenge getChallenge(int id) {
        return this.challenges.get(id);
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public HashMap<Integer, Challenge> getChallenges() {
        return this.challenges;
    }
}
