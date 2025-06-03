package com.songoda.skyblock.visit;

import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class VisitTask extends BukkitRunnable {
    private final PlayerDataManager playerDataManager;

    public VisitTask(PlayerDataManager playerManager) {
        this.playerDataManager = playerManager;
    }

    @Override
    public void run() {
        Map<UUID, PlayerData> playerDataStorage = this.playerDataManager.getPlayerData();
        synchronized (playerDataStorage) {
            for (PlayerData playerData : playerDataStorage.values()) {
                if (playerData.getIsland() != null) {
                    playerData.setVisitTime(playerData.getVisitTime() + 1);
                }
            }
        }
    }
}
