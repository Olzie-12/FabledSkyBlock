package com.songoda.skyblock.command.commands.island;

import com.songoda.third_party.com.cryptomorin.xseries.XSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.menus.Bans;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class BansCommand extends SubCommand {
    public BansCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        MessageManager messageManager = this.plugin.getMessageManager();
        SoundManager soundManager = this.plugin.getSoundManager();

        FileManager.Config config = this.plugin.getFileManager().getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        Island island = this.plugin.getIslandManager().getIsland(player);

        if (island == null) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Bans.Owner.Message"));
            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
        } else if ((island.hasRole(IslandRole.OPERATOR, player.getUniqueId())
                && this.plugin.getPermissionManager().hasPermission(island, "Unban", IslandRole.OPERATOR))
                || island.hasRole(IslandRole.OWNER, player.getUniqueId())) {
            if (island.getBan().getBans().isEmpty()) {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Bans.Bans.Message"));
                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
            } else {
                Bans.getInstance().open(player);
                soundManager.playSound(player, XSound.BLOCK_CHEST_OPEN);
            }
        } else {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Bans.Permission.Message"));
            soundManager.playSound(player, XSound.ENTITY_VILLAGER_NO);
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "bans";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Bans.Info.Message";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"banned"};
    }

    @Override
    public String[] getArguments() {
        return new String[0];
    }
}
