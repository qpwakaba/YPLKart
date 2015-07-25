package com.github.erozabesu.yplkart.cmd;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.erozabesu.yplkart.data.ItemEnum;

public class CMD implements CommandExecutor {
    CMDAbstract cmd;

    public CMD() {
        // Do nothing
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("ka")) {
            for (int i = 0; i < args.length; i++) {
                args[i] = args[i].toLowerCase();
            }

            if (sender instanceof Player)
                this.cmd = new CMDAbstractPlayer((Player) sender, args);
            else if (sender instanceof BlockCommandSender)
                this.cmd = new CMDAbstractBlock(args);
            else
                this.cmd = new CMDAbstractConsole(args);

            if (args.length == 0)
                this.cmd.ka();
            else if (args[0].equalsIgnoreCase("circuit"))
                this.cmd.circuit();
            else if (args[0].equalsIgnoreCase("display"))
                this.cmd.display();
            else if (args[0].equalsIgnoreCase("menu"))
                this.cmd.menu();
            else if (args[0].equalsIgnoreCase("entry"))
                this.cmd.entry();
            else if (args[0].equalsIgnoreCase("exit"))
                this.cmd.exit();
            else if (args[0].equalsIgnoreCase("character"))
                this.cmd.character();
            else if (args[0].equalsIgnoreCase("characterreset"))
                this.cmd.characterreset();
            else if (args[0].equalsIgnoreCase("kart"))
                this.cmd.ride();
            else if (args[0].equalsIgnoreCase("leave"))
                this.cmd.leave();
            else if (args[0].equalsIgnoreCase("ranking"))
                this.cmd.ranking();
            else if (args[0].equalsIgnoreCase("reload"))
                this.cmd.reload();
            else if (args[0].equalsIgnoreCase("item"))
                this.cmd.additem(null, null);
            else {
                ItemEnum item = null;
                if ((item = ItemEnum.getItemFromCommandKey(args[0])) != null)
                    this.cmd.additem(item.getItem(), item.getPermission());
            }
            return true;
        }
        return false;
    }
}