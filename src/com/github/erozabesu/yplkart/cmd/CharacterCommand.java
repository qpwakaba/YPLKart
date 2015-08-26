package com.github.erozabesu.yplkart.cmd;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.erozabesu.yplkart.Permission;
import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.data.CharacterConfig;
import com.github.erozabesu.yplkart.data.MessageEnum;
import com.github.erozabesu.yplkart.data.SystemMessageEnum;
import com.github.erozabesu.yplkart.object.Character;
import com.github.erozabesu.yplkart.utils.Util;

public class CharacterCommand extends Command {
    protected CharacterCommand() {
		super("character");
	}

	@Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length == 2 && sender instanceof Player) {
            if (!Permission.hasPermission(sender, Permission.CMD_CHARACTER, false))
                return true;
            Player player = (Player) sender;
            Character character;
            if (args[1].equalsIgnoreCase("random")) {
                character =  CharacterConfig.getRandomCharacter();
                //ka character {character name}
            } else {
                character = CharacterConfig.getCharacter(args[1]);
                if (character == null) {
                    MessageEnum.invalidCharacter.sendConvertedMessage(sender);
                    return true;
                }
            }
            RaceManager.setCharacterRaceData(player.getUniqueId(), character);
            return true;
            //ka character {player} {character name}
            //ka character all {character name}
            //ka character {player} random
            //ka character all random
        } else if (args.length == 3) {
            if (!Permission.hasPermission(sender, Permission.CMD_CHARACTER.getTargetOtherPermission(), false))
                return true;

            Character character;
            if (args[2].equalsIgnoreCase("random")) {
                character = CharacterConfig.getRandomCharacter();
            } else {
                character = CharacterConfig.getCharacter(args[2]);
                if (character == null) {
                    MessageEnum.invalidCharacter.sendConvertedMessage(sender);
                    return true;
                }
            }

            if (args[1].equalsIgnoreCase("all")) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    RaceManager.setCharacterRaceData(other.getUniqueId(), character);
                }
                MessageEnum.cmdCharacterRandomAll.sendConvertedMessage(sender);
                return true;
            } else {
                String playerName = args[1];
                if (!Util.isOnline(playerName)) {
                    MessageEnum.invalidPlayer.sendConvertedMessage(sender);
                    return true;
                }
                @SuppressWarnings("deprecation")
                Player other = Bukkit.getPlayer(playerName);
                RaceManager.setCharacterRaceData(other.getUniqueId(), character);
                MessageEnum.cmdCharacterOther.sendConvertedMessage(sender, new Object[] { other, character });
                return true;
            }
        } else {
            SystemMessageEnum.referenceCharacter.sendConvertedMessage(sender);
            SystemMessageEnum.referenceCharacterOther.sendConvertedMessage(sender);
            return true;
        }
    }
}