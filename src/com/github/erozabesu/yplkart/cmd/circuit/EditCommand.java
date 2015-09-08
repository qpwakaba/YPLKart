package com.github.erozabesu.yplkart.cmd.circuit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.erozabesu.yplkart.Permission;
import com.github.erozabesu.yplkart.data.ItemEnum;
import com.github.erozabesu.yplkart.data.MessageEnum;
import com.github.erozabesu.yplkart.enumdata.TagType;
import com.github.erozabesu.yplkart.object.MessageParts;

public class EditCommand extends Command {
    public EditCommand() {
        super("edit");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!Permission.hasPermission(sender, Permission.OP_CMD_CIRCUIT, false)) {
            return true;
        }
        if(args.length != 3 || !(sender instanceof Player)) {
            //TODO: ここでコマンドの使い方を表示させられる。
            return false;
        }

        Player player = (Player)sender;
        String circuitName = args[2];
        MessageParts circuitParts = new MessageParts(TagType.CIRCUIT, circuitName);

        ItemStack[] checkPointTools = ItemEnum.getCheckPointTools(circuitName);
        if (checkPointTools == null) {
            MessageEnum.invalidCircuit.sendConvertedMessage(player, circuitParts);
            return true;
        }

        player.getInventory().addItem(checkPointTools);
        MessageEnum.cmdCircuitEdit.sendConvertedMessage(player, circuitParts);

        return true;
    }
}
