package com.songoda.skyblock.command.commands.island;

import com.songoda.third_party.com.cryptomorin.xseries.XSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.invite.Invite;
import com.songoda.skyblock.invite.InviteManager;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class CancelCommand extends SubCommand {
    public CancelCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        MessageManager messageManager = this.plugin.getMessageManager();
        IslandManager islandManager = this.plugin.getIslandManager();
        InviteManager inviteManager = this.plugin.getInviteManager();
        SoundManager soundManager = this.plugin.getSoundManager();

        FileManager.Config config = this.plugin.getFileManager().getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (args.length == 1) {
            Island island = islandManager.getIsland(player);

            if (island == null) {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Cancel.Owner.Message"));
                soundManager.playSound(player, XSound.ENTITY_VILLAGER_NO);
            } else if (island.hasRole(IslandRole.OWNER, player.getUniqueId())
                    || island.hasRole(IslandRole.OPERATOR, player.getUniqueId())) {
                String playerName = args[0];
                Player targetPlayer = Bukkit.getPlayerExact(playerName);

                if (targetPlayer == null) {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Cancel.Offline.Message"));
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                } else if (island.hasRole(IslandRole.MEMBER, targetPlayer.getUniqueId())
                        || island.hasRole(IslandRole.OPERATOR, targetPlayer.getUniqueId())
                        || island.hasRole(IslandRole.OWNER, targetPlayer.getUniqueId())) {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Cancel.Member.Message"));
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                } else if (inviteManager.hasInvite(targetPlayer.getUniqueId())) {
                    Invite invite = inviteManager.getInvite(targetPlayer.getUniqueId());

                    if (invite.getOwnerUUID().equals(island.getOwnerUUID())) {
                        inviteManager.removeInvite(targetPlayer.getUniqueId());

                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Cancel.Cancelled.Message").replace("%player", targetPlayer.getName()));
                        soundManager.playSound(player, XSound.ENTITY_GENERIC_EXPLODE, 10, 10);
                    } else {
                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Cancel.Invited.Message"));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                    }
                } else {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Cancel.Invited.Message"));
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                }
            } else {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Cancel.Permission.Message"));
                soundManager.playSound(player, XSound.ENTITY_VILLAGER_NO);
            }
        } else {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Cancel.Invalid.Message"));
            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "cancel";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Cancel.Info.Message";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String[] getArguments() {
        return new String[0];
    }
}
