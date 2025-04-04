package com.songoda.skyblock.command.commands.island;

import com.songoda.third_party.com.cryptomorin.xseries.XSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.challenge.FabledChallenge;
import com.songoda.skyblock.challenge.challenge.Challenge;
import com.songoda.skyblock.challenge.challenge.ChallengeCategory;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class ChallengeCommand extends SubCommand {
    public ChallengeCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        MessageManager messageManager = this.plugin.getMessageManager();
        SoundManager soundManager = this.plugin.getSoundManager();
        FileManager fileManager = this.plugin.getFileManager();
        FabledChallenge fabledChallenge = this.plugin.getFabledChallenge();
        IslandManager islandManager = this.plugin.getIslandManager();

        FileManager.Config langConfig = fileManager.getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration langConfigLoad = langConfig.getFileConfiguration();

        // Not loaded
        if (!this.plugin.getConfiguration().getBoolean("Island.Challenge.Enable")) {
            messageManager.sendMessage(player, langConfigLoad.getString("Command.Island.Challenge.Disabled.Message"));
            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
            return;
        }
        if (args.length == 0) {
            if (this.plugin.getConfiguration().getBoolean("Island.Challenge.PerIsland")) {
                if (islandManager.getIsland(player) == null) {
                    messageManager.sendMessage(player, langConfigLoad.getString("Command.Island.Challenge.NoIsland.Message"));
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                    return;
                }
            }

            // Open challenge inventory
            ChallengeCategory cc = fabledChallenge.getChallengeManager().getChallenge(1);
            if (cc == null) {
                messageManager.sendMessage(player, langConfigLoad.getString("Command.Island.Challenge.NotFound.Message"));
                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                return;
            }
            fabledChallenge.openChallengeInventory(player, fabledChallenge.getChallengeManager().getChallenge(1));
            return;
        }
        if (args.length == 2) {
            // Complete a challenge
            int ccId;
            int cId;
            try {
                ccId = Integer.parseInt(args[0]);
                cId = Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
                messageManager.sendMessage(player,
                        langConfigLoad.getString("Command.Island.Challenge.Invalid.Message"));
                soundManager.playSound(player, XSound.ENTITY_VILLAGER_HURT);
                return;
            }
            ChallengeCategory cc = fabledChallenge.getChallengeManager().getChallenge(ccId);
            if (cc == null) {
                messageManager.sendMessage(player,
                        langConfigLoad.getString("Command.Island.Challenge.CategoryNotFound.Message"));
                soundManager.playSound(player, XSound.ENTITY_VILLAGER_HURT);
                return;
            }
            Challenge c = cc.getChallenge(cId);
            if (c == null) {
                messageManager.sendMessage(player, langConfigLoad.getString("Command.Island.Challenge.ChallengeNotFound.Message"));
                soundManager.playSound(player, XSound.ENTITY_VILLAGER_HURT);
                return;
            }
            if (fabledChallenge.getPlayerManager().doChallenge(player, c)) {    // Ok
                soundManager.playSound(player, XSound.ENTITY_PLAYER_LEVELUP);
            } else {
                soundManager.playSound(player, XSound.BLOCK_GLASS_BREAK);
            }
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "challenge";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Challenge.Info.Message";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"c", "challenges"};
    }

    @Override
    public String[] getArguments() {
        return new String[0];
    }
}
