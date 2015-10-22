package com.github.erozabesu.yplkart.cmd.circuit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.erozabesu.yplkart.Permission;
import com.github.erozabesu.yplkart.data.CircuitConfig;
import com.github.erozabesu.yplkart.data.MessageEnum;
import com.github.erozabesu.yplutillibrary.util.CommonUtil;

/**
 * /ka circuit setpositionコマンドクラス。
 * @author King
 * @author erozabesu
 */
public class SetPositionCommand extends Command {
    public SetPositionCommand() {
        super("setposition");
    }

    // 0:circuit 1:create 2:circuitname 3:worldname 4:x 5:y 6:z 7:pitch 8:yaw
    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!Permission.hasPermission(sender, Permission.OP_CMD_CIRCUIT, false)) {
            return true;
        }

        // 引数が9個指定されたときか、プレイヤーが3つ指定したとき
        if (args.length != 9 && !(args.length == 3 && CommonUtil.isPlayer(sender))) {
            // TODO: ここでコマンドの説明を表示させられる。
            return false;
        }

        String circuitName = args[2];
        Location location;

        if (args.length == 9) {
            String worldName = args[3];
            World world;
            double x, y, z;
            float pitch, yaw;
            world = Bukkit.getWorld(worldName);
            if (world == null) {
                MessageEnum.invalidWorld.sendConvertedMessage(sender);
                return true;
            }
            try {
                x = Double.parseDouble(args[4]);
                y = Double.parseDouble(args[5]);
                z = Double.parseDouble(args[6]);
                pitch = Float.parseFloat(args[7]);
                yaw = Float.parseFloat(args[8]);
            } catch (NumberFormatException ex) {
                MessageEnum.invalidNumber.sendConvertedMessage(sender);
                return true;
            }
            location = new Location(world, x, y, z, yaw, pitch);
        } else {
            Player player = (Player) sender;
            location = player.getLocation();
        }
        CircuitConfig.setStartPosition(sender, circuitName, location);
        return true;
    }
}
