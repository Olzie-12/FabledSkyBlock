package com.songoda.skyblock.command.commands.island;

import com.songoda.third_party.com.cryptomorin.xseries.XSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.menus.Information;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.player.OfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class InformationCommand extends SubCommand {
    public InformationCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        PlayerDataManager playerDataManager = this.plugin.getPlayerDataManager();
        MessageManager messageManager = this.plugin.getMessageManager();
        IslandManager islandManager = this.plugin.getIslandManager();
        SoundManager soundManager = this.plugin.getSoundManager();

        if (playerDataManager.hasPlayerData(player)) {
            FileManager.Config config = this.plugin.getFileManager().getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
            FileConfiguration configLoad = config.getFileConfiguration();

            UUID islandOwnerUUID = null;

            if (args.length == 1) {
                Player targetPlayer = Bukkit.getPlayerExact(args[0]);

                if (targetPlayer == null) {
                    OfflinePlayer targetOfflinePlayer = new OfflinePlayer(args[0]);
                    islandOwnerUUID = targetOfflinePlayer.getOwner();
                } else {
                    islandOwnerUUID = playerDataManager.getPlayerData(targetPlayer).getOwner();
                }

                if (islandOwnerUUID == null) {
                    messageManager.sendMessage(player,
                            configLoad.getString("Command.Island.Information.Island.Message"));
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                    return;
                }
            } else if (args.length != 0) {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Information.Invalid.Message"));
                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                return;
            }

            PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(player);

            if (playerData.isPreview()) {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Information.Previewing.Message"));
                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                return;
            }

            if (islandOwnerUUID == null) {
                if (islandManager.getIsland(player) == null) {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Information.Owner.Message"));
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                    return;
                } else {
                    islandOwnerUUID = playerData.getOwner();
                }
            }

            playerData.setViewer(new Information.Viewer(islandOwnerUUID, Information.Viewer.Type.CATEGORIES));
            Information.getInstance().open(player);
            soundManager.playSound(player, XSound.BLOCK_CHEST_OPEN);
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "information";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Information.Info.Message";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"info"};
    }

    @Override
    public String[] getArguments() {
        return new String[0];
    }
}
