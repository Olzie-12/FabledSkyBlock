package com.songoda.skyblock.command.commands.admin;

import com.songoda.third_party.com.cryptomorin.xseries.XSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class UpdateAllIslandsCommand extends SubCommand {
    public UpdateAllIslandsCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        processCommand(player, args);
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        processCommand(sender, args);
    }

    public void processCommand(CommandSender sender, String[] args) {
        MessageManager messageManager = this.plugin.getMessageManager();
        IslandManager islandManager = this.plugin.getIslandManager();
        FileManager fileManager = this.plugin.getFileManager();
        SoundManager soundManager = this.plugin.getSoundManager();

        FileManager.Config config = fileManager.getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (args.length < 1) {
            messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.UpdateAllIslands.NoArgs.Message"));
            soundManager.playSound(sender, XSound.BLOCK_ANVIL_LAND);
        } else {
            switch (args[0].toLowerCase()) {
                case "setsize":
                    if (args.length >= 2) {
                        int size;
                        try {
                            size = Integer.parseInt(args[1]);
                            messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.UpdateAllIslands.Start.Message"));
                            islandManager.setAllIslandsSize(Math.abs(size), () ->
                                    messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.UpdateAllIslands.Finished.Message")));
                        } catch (NumberFormatException ignored) {
                            messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.UpdateAllIslands.Unexpected.Message"));
                        }
                    } else {
                        messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.UpdateAllIslands.NotANumber.Message"));
                    }
                    return;
                case "adjustsize":
                    if (args.length >= 2) {
                        int size;
                        try {
                            size = Integer.parseInt(args[1]);
                            messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.UpdateAllIslands.Start.Message"));
                            islandManager.adjustAllIslandsSize(size, () ->
                                    messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.UpdateAllIslands.Finished.Message")));
                        } catch (NumberFormatException ignored) {
                            messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.UpdateAllIslands.NotANumber.Message"));
                        }
                    } else {
                        messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.UpdateAllIslands.Unexpected.Message"));
                    }
                    return;
                default:
                    messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.UpdateAllIslands.Unexpected.Message"));
                    soundManager.playSound(sender, XSound.BLOCK_ANVIL_LAND);
            }
        }
    }

    @Override
    public String getName() {
        return "updateallislands";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Admin.UpdateAllIslands.Info.Message";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String[] getArguments() {
        return new String[]{"SetSize", "AdjustSize"};
    }
}
