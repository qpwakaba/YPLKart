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
        ItemEnum itemEnum = null;
        if ((itemEnum = ItemEnum.getItemByCommandKey(args[0])) == null) {
            // TODO: ここにコマンドの使い方を表示させられる。
            return false;
        }
        ItemStack item = itemEnum.getItem();
        Permission permission = itemEnum.getCmdPermission();
        // コマンド引数が不正
        if (item == null || permission == null) {
            SystemMessageEnum.referenceAddItem.sendConvertedMessage(sender);
            SystemMessageEnum.referenceAddItemOther.sendConvertedMessage(sender);
            return true;
            // ka {item} : コマンドのターゲットは自分自身
        } else if (args.length == 1 && sender instanceof Player) {
            if (!Permission.hasPermission(sender, permission, false)) {
                return true;
            }
            Player player = (Player) sender;
            player.getInventory().addItem(item);
            MessageEnum.cmdItem.sendConvertedMessage(sender, item);
            return true;
        } else if (args.length == 2) {
            // ka {item} {amount} : コマンドのターゲットは自分自身
            if (Util.isInteger(args[1]) && sender instanceof Player) {
                if (!Permission.hasPermission(sender, permission, false)) {
                    return true;
                }
                Player player = (Player) sender;
                item.setAmount(Integer.parseInt(args[1]));
                player.getInventory().addItem(item);
                MessageEnum.cmdItem.sendConvertedMessage(sender, item);
                return true;
                // ka {item} {other player} : コマンドのターゲットは他プレイヤー
            } else {
                if (!Permission.hasPermission(sender, permission.getTargetOtherPermission(), false)) {
                    return true;
                }

                // ka {item} all
                if (args[1].equalsIgnoreCase("all")) {
                    for (Player other : Bukkit.getOnlinePlayers()) {
                        other.getInventory().addItem(item);
                        MessageEnum.cmdItem.sendConvertedMessage(other, item);
                    }
                    MessageEnum.cmdItemAll.sendConvertedMessage(sender, item);

                    // ka {item} {player}
                } else {
                    String playerName = args[1];
                    if (!Util.isOnline(playerName)) {
                        MessageEnum.invalidPlayer.sendConvertedMessage(sender);
                        return true;
                    }

                    @SuppressWarnings("deprecation")
                    Player other = Bukkit.getPlayer(playerName);
                    other.getInventory().addItem(item);
                    MessageEnum.cmdItem.sendConvertedMessage(other, item);
                    MessageEnum.cmdItemOther.sendConvertedMessage(sender, other, item);
                }
                return true;
            }

            // 全てコマンドのターゲットは他プレイヤー
        } else if (args.length == 3) {
            if (!Permission.hasPermission(sender, permission.getTargetOtherPermission(), false)) {
                return true;
            }

            // コマンド引数が不正
            if (!Util.isInteger(args[2])) {
                SystemMessageEnum.referenceAddItemOther.sendConvertedMessage(sender);
                return true;
            }

            item.setAmount(Integer.parseInt(args[2]));

            // ka {item} all 64
            if (args[1].equalsIgnoreCase("all")) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    other.getInventory().addItem(item);
                    MessageEnum.cmdItem.sendConvertedMessage(other, item);
                }
                MessageEnum.cmdItemAll.sendConvertedMessage(sender, item);
                // ka {item} {player} 64
            } else {
                String playerName = args[1];
                if (!Util.isOnline(playerName)) {
                    MessageEnum.invalidPlayer.sendConvertedMessage(sender);
                    return true;
                }

                @SuppressWarnings("deprecation")
                Player other = Bukkit.getPlayer(playerName);
                other.getInventory().addItem(item);
                MessageEnum.cmdItem.sendConvertedMessage(other, item);
                MessageEnum.cmdItemOther.sendConvertedMessage(sender, other, item);
            }
            return true;
        } else {
            SystemMessageEnum.referenceAddItem.sendConvertedMessage(sender);
            SystemMessageEnum.referenceAddItemOther.sendConvertedMessage(sender);
            return true;
        }
    }
}