package com.songoda.skyblock.command.commands.island;

import com.songoda.third_party.com.cryptomorin.xseries.XSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.gui.permissions.GuiPermissionsSelector;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.PermissionManager;
import com.songoda.skyblock.sound.SoundManager;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class SettingsCommand extends SubCommand {
    public SettingsCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        MessageManager messageManager = this.plugin.getMessageManager();
        IslandManager islandManager = this.plugin.getIslandManager();
        SoundManager soundManager = this.plugin.getSoundManager();
        PermissionManager permissionManager = this.plugin.getPermissionManager();

        FileManager.Config config = this.plugin.getFileManager().getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        Island island = islandManager.getIsland(player);

        if (island == null) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Settings.Owner.Message"));
            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
        } else if (island.hasRole(IslandRole.OPERATOR, player.getUniqueId())
                || island.hasRole(IslandRole.OWNER, player.getUniqueId())) {
            if ((island.hasRole(IslandRole.OPERATOR, player.getUniqueId())
                    && (permissionManager.hasPermission(island, "Visitor", IslandRole.OPERATOR)
                    || permissionManager.hasPermission(island, "Member", IslandRole.OPERATOR)))
                    || island.hasRole(IslandRole.OWNER, player.getUniqueId())) {
                this.plugin.getGuiManager().showGUI(player, new GuiPermissionsSelector(this.plugin, player, island, null));
                soundManager.playSound(player, XSound.BLOCK_CHEST_OPEN);
            } else {
                messageManager.sendMessage(player,
                        configLoad.getString("Command.Island.Settings.Permission.Default.Message"));
                soundManager.playSound(player, XSound.ENTITY_VILLAGER_NO);
            }
        } else {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Settings.Role.Message"));
            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "settings";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Settings.Info.Message";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"permissions", "perms", "p"};
    }

    @Override
    public String[] getArguments() {
        return new String[0];
    }
}
