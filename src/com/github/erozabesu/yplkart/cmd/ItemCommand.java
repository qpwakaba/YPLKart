package com.github.erozabesu.yplkart.cmd;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.erozabesu.yplkart.Permission;
import com.github.erozabesu.yplkart.data.ItemEnum;
import com.github.erozabesu.yplkart.data.MessageEnum;
import com.github.erozabesu.yplkart.data.SystemMessageEnum;
import com.github.erozabesu.yplkart.utils.Util;

public class ItemCommand extends Command {
    public ItemCommand() {
        super("item");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        ItemEnum itemEnum;

        //コマンド引数がitem、もしくは一致するItemEnumが存在しない場合はリファレンスを表示
        if (args.length == 1 && args[0].equalsIgnoreCase("item")
                || (itemEnum = ItemEnum.getItemByCommandKey(args[0])) == null) {
            SystemMessageEnum.referenceAddItem.sendConvertedMessage(sender);
            SystemMessageEnum.referenceAddItemOther.sendConvertedMessage(sender);
            return true;
        }

        ItemStack itemStack = itemEnum.getItem();
        Permission permission = itemEnum.getCmdPermission();

        // ka [アイテム]
        if (args.length == 1) {
            if (!Permission.hasPermission(sender, permission, false)) {
                return true;
            }
            if (!this.addItem(sender, itemStack)) {
                SystemMessageEnum.referenceAddItemOther.sendConvertedMessage(sender);
            }

            return true;

        // ka [アイテム] [プレイヤー]
        // ka [アイテム] all|@a
        // ka [アイテム] [数量]
        } else if (args.length == 2) {

            // ka [アイテム] all|@a
            if (args[1].equalsIgnoreCase("all") || args[1].equalsIgnoreCase("@a")) {
                if (!Permission.hasPermission(sender, permission.getTargetOtherPermission(), false)) {
                    return true;
                }

                for (Player other : Bukkit.getOnlinePlayers()) {
                    this.addItem(other, itemStack);
                }

                MessageEnum.cmdItemAll.sendConvertedMessage(sender, itemStack);

                return true;

            // ka [アイテム] [数量]
            } else if (Util.isInteger(args[1])) {
                if (!Permission.hasPermission(sender, permission, false)) {
                    return true;
                }

                itemStack.setAmount(Integer.parseInt(args[1]));
                if (!this.addItem(sender, itemStack)) {
                    SystemMessageEnum.referenceAddItemOther.sendConvertedMessage(sender);
                }

                return true;

            // ka [アイテム] [プレイヤー]
            } else if(Bukkit.getPlayer(args[1]) != null) {
                if (!Permission.hasPermission(sender, permission.getTargetOtherPermission(), false)) {
                    return true;
                }

                Player otherPlayer = Bukkit.getPlayer(args[1]);
                if (otherPlayer == null) {
                    MessageEnum.invalidPlayer.sendConvertedMessage(sender);
                    return true;
                }

                this.addItem(otherPlayer, itemStack);
                MessageEnum.cmdItemOther.sendConvertedMessage(sender, otherPlayer, itemStack);

                return true;
            }

            if (sender instanceof Player) {
                SystemMessageEnum.referenceAddItem.sendConvertedMessage(sender);
            }
            SystemMessageEnum.referenceAddItemOther.sendConvertedMessage(sender);

        // ka [アイテム] all|@a [数量]
        // ka [アイテム] [プレイヤー] [数量]
        } else if (args.length == 3) {

            // 全て他プレイヤーを対象としたコマンドのため、予めパーミッションをチェックする。
            if (!Permission.hasPermission(sender, permission.getTargetOtherPermission(), false)) {
                return true;
            }

            // 全て数量を指定するコマンドのため、予めIntegerに変換できるかチェックする。
            try {
                itemStack.setAmount(Integer.parseInt(args[2]));
            } catch (NumberFormatException ex) {
                MessageEnum.invalidNumber.sendConvertedMessage(sender);
                return true;
            }

            // ka [アイテム] all|@a [数量]
            if (args[1].equalsIgnoreCase("all") || args[1].equalsIgnoreCase("@a")) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    this.addItem(other, itemStack);
                }
                MessageEnum.cmdItemAll.sendConvertedMessage(sender, itemStack);

            // ka [アイテム] [プレイヤー] [数量]
            } else {
                Player otherPlayer = Bukkit.getPlayer(args[1]);
                if (otherPlayer == null) {
                    MessageEnum.invalidPlayer.sendConvertedMessage(sender);
                    return true;
                }

                this.addItem(otherPlayer, itemStack);
                MessageEnum.cmdItemOther.sendConvertedMessage(sender, otherPlayer, itemStack);
            }
        } else {
            SystemMessageEnum.referenceAddItem.sendConvertedMessage(sender);
            SystemMessageEnum.referenceAddItemOther.sendConvertedMessage(sender);
        }

        return true;
    }

    /**
     * 引数senderがPlayerインスタンスであれば引数itemStackを付与し、trueを返す。<br>
     * Playerインスタンス以外の場合は何もせず、falseを返す。
     * @param sender アイテムを付与する対象
     * @param itemStack 付与するアイテムスタック
     * @return アイテムの付与に成功したかどうか
     */
    private boolean addItem(CommandSender sender, ItemStack itemStack) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.getInventory().addItem(itemStack);
            MessageEnum.cmdItem.sendConvertedMessage(sender, itemStack);
            return true;
        }

        return false;
    }
}