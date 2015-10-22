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
 * /ka circuit createコマンドクラス。
 * @author King
 * @author erozabesu
 */
public class CreateCommand extends Command {
    public CreateCommand() {
        super("create");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!Permission.hasPermission(sender, Permission.OP_CMD_CIRCUIT, false)) {
            return true;
        }

        if (CommonUtil.isPlayer(sender)) {
            // プレイヤーが引数3個、9個以外のコマンドを実行した場合return falseする
            if (args.length != 3 && args.length != 9) {
                // TODO: ここでコマンドの説明を表示させられる。
                return false;
            }
        } else {
            // プレイヤー以外が引数9個以外のコマンドを実行した場合return falseする
            if (args.length != 9) {
                // TODO: ここでコマンドの説明を表示させられる。
                return false;
            }
        }

        String circuitName = args[2];
        Location location;

        // 0:circuit 1:create 2:circuitname 3:worldname 4:x 5:y 6:z 7:pitch 8:yaw
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

        CircuitConfig.createCircuit(sender, circuitName, location);

        return true;
    }
}
