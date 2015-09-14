package com.github.erozabesu.yplkart.cmd;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.erozabesu.yplkart.Permission;
import com.github.erozabesu.yplkart.data.KartConfig;
import com.github.erozabesu.yplkart.data.MessageEnum;
import com.github.erozabesu.yplkart.data.SystemMessageEnum;
import com.github.erozabesu.yplkart.object.Kart;
import com.github.erozabesu.yplkart.object.MessageParts;
import com.github.erozabesu.yplkart.utils.KartUtil;
import com.github.erozabesu.yplkart.utils.Util;

/**
 * /ka displayコマンドクラス。
 * @author King
 * @author erozabesu
 */
public class DisplayCommand extends Command {
    public DisplayCommand() {
        super("display");
    }
    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!Permission.hasPermission(sender, Permission.OP_CMD_DISPLAY, false))
            return true;
        if (!(args.length == 8 || (Util.isPlayer(sender) && args.length == 2))) {
            SystemMessageEnum.referenceDisplayIngame.sendConvertedMessage(sender);
            return true;
        }

        Kart kart = null;
        Location location;

        String kartName = args[1];
        if (kartName.equalsIgnoreCase("random"))
            kart = KartConfig.getRandomKart();
        else
            kart = KartConfig.getKart(kartName);

        if (kart == null) {
            MessageEnum.invalidKart.sendConvertedMessage(sender);
            return true;
        }

        if (args.length == 2 && Util.isPlayer(sender)) {
            Player player = (Player) sender;
            location = player.getLocation();
        } else {
            String worldName = args[2];
            World world;
            double x, y, z;
            float pitch, yaw;
            world = Bukkit.getWorld(worldName);
            if(world == null) {
                MessageEnum.invalidWorld.sendConvertedMessage(sender);
                return true;
            }
            try {
                x = Double.parseDouble(args[3]);
                y = Double.parseDouble(args[4]);
                z = Double.parseDouble(args[5]);
                pitch = Float.parseFloat(args[6]);
                yaw = Float.parseFloat(args[7]);
            } catch (NumberFormatException ex) {
                MessageEnum.invalidNumber.sendConvertedMessage(sender);
                return true;
            }
            location = new Location(world, x, y, z, yaw, pitch);
        }
        KartUtil.createDisplayKart(location, kart, null);
        MessageEnum.cmdDisplayCreate.sendConvertedMessage(sender, MessageParts.getMessageParts(kart));
        return true;
    }
}