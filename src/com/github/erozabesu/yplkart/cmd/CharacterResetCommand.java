package com.github.erozabesu.yplkart.cmd;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.erozabesu.yplkart.Permission;
import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.data.MessageEnum;
import com.github.erozabesu.yplkart.data.SystemMessageEnum;
import com.github.erozabesu.yplkart.object.MessageParts;
import com.github.erozabesu.yplutillibrary.util.CommonUtil;

/**
 * /ka characterresetコマンドクラス。
 * @author King
 * @author erozabesu
 */
public class CharacterResetCommand extends Command {
    public CharacterResetCommand() {
        super("characterreset");
    }
    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length == 1 && sender instanceof Player) {
            if (!Permission.hasPermission(sender, Permission.CMD_CHARACTERRESET, false)) {
                return true;
            }

            Player player = (Player) sender;
            RaceManager.racerSetter_DeselectCharacter(player.getUniqueId());
            return true;
        } else if (args.length == 2) {
            if (!Permission.hasPermission(sender, Permission.CMDOTHER_CHARACTERRESET, false)) {
                return true;
            }

            if (args[1].equalsIgnoreCase("all")) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    RaceManager.racerSetter_DeselectCharacter(other.getUniqueId());
                }
                MessageEnum.cmdCharacterResetAll.sendConvertedMessage(sender);
                return true;
            } else {
                String playerName = args[1];
                if (!CommonUtil.isOnline(playerName)) {
                    MessageEnum.invalidPlayer.sendConvertedMessage(sender);
                    return true;
                }

                @SuppressWarnings("deprecation")
                Player other = Bukkit.getPlayer(playerName);
                RaceManager.racerSetter_DeselectCharacter(other.getUniqueId());

                MessageParts playerParts = MessageParts.getMessageParts(other);
                MessageEnum.cmdCharacterResetOther.sendConvertedMessage(sender, playerParts);
                return true;
            }
        } else {
            SystemMessageEnum.referenceCharacterReset.sendConvertedMessage(sender);
            SystemMessageEnum.referenceCharacterResetOther.sendConvertedMessage(sender);
            return true;
        }
    }
}