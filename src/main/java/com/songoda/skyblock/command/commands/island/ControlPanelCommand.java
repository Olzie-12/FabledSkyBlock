package com.songoda.skyblock.command.commands.island;

import com.songoda.third_party.com.cryptomorin.xseries.XSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.menus.ControlPanel;
import com.songoda.skyblock.sound.SoundManager;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class ControlPanelCommand extends SubCommand {
    public ControlPanelCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        SoundManager soundManager = this.plugin.getSoundManager();

        FileManager.Config config = this.plugin.getFileManager().getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (this.plugin.getIslandManager().getIsland(player) == null) {
            this.plugin.getMessageManager().sendMessage(player, configLoad.getString("Command.Island.ControlPanel.Owner.Message"));
            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
        } else {
            ControlPanel.getInstance().open(player);
            soundManager.playSound(player, XSound.BLOCK_CHEST_OPEN);
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "controlpanel";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.ControlPanel.Info.Message";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"cp"};
    }

    @Override
    public String[] getArguments() {
        return new String[0];
    }
}
