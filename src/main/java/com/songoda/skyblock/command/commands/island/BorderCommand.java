package com.songoda.skyblock.command.commands.island;

import com.songoda.third_party.com.cryptomorin.xseries.XSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.menus.Border;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.PermissionManager;
import com.songoda.skyblock.sound.SoundManager;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class BorderCommand extends SubCommand {
    public BorderCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        MessageManager messageManager = this.plugin.getMessageManager();
        IslandManager islandManager = this.plugin.getIslandManager();
        PermissionManager permissionManager = this.plugin.getPermissionManager();
        SoundManager soundManager = this.plugin.getSoundManager();
        FileManager fileManager = this.plugin.getFileManager();

        FileManager.Config config = fileManager.getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        Island island = islandManager.getIsland(player);

        if (island == null) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Border.Owner.Message"));
            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
        } else if ((island.hasRole(IslandRole.OPERATOR, player.getUniqueId())
                && permissionManager.hasPermission(island, "Border", IslandRole.OPERATOR))
                || island.hasRole(IslandRole.OWNER, player.getUniqueId())) {
            if (this.plugin.getConfiguration()
                    .getBoolean("Island.WorldBorder.Enable")) {
                Border.getInstance().open(player);
                soundManager.playSound(player, XSound.BLOCK_CHEST_OPEN);
            } else {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Border.Disabled.Message"));
                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
            }
        } else {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Border.Permission.Message"));
            soundManager.playSound(player, XSound.ENTITY_VILLAGER_NO);
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "border";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Border.Info.Message";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"worldborder", "wb"};
    }

    @Override
    public String[] getArguments() {
        return new String[0];
    }
}
