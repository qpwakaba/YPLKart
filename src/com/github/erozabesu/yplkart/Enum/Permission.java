package com.github.erozabesu.yplkart.Enum;

import org.bukkit.entity.Player;

import com.github.erozabesu.yplkart.YPLKart;
import com.github.erozabesu.yplkart.Data.Message;
import com.github.erozabesu.yplkart.Data.Settings;

public enum Permission {
    kart_ride(
            YPLKart.plname + ".kart.ride",
            Settings.EnablePermissionKartRide,
            Settings.EnablePermissionKartRide),
    kart_drift(
            YPLKart.plname + ".kart.drift",
            Settings.EnablePermissionKartDrift,
            Settings.EnablePermissionKartDrift),
    op_kart_remove(
            YPLKart.plname + ".op.kart.remove",
            Settings.EnableOPPermissionKartRemove,
            Settings.EnableOPPermissionKartRemove),

    cmd_ka(
            YPLKart.plname + ".cmd.ka",
            false,
            false),
    cmd_menu(
            YPLKart.plname + ".cmd.menu",
            Settings.EnablePermissionCMDMenu,
            Settings.EnablePermissionCMDOtherMenu),
    cmd_entry(
            YPLKart.plname + ".cmd.entry",
            Settings.EnablePermissionCMDEntry,
            Settings.EnablePermissionCMDOtherEntry),
    cmd_exit(
            YPLKart.plname + ".cmd.exit",
            Settings.EnablePermissionCMDExit,
            Settings.EnablePermissionCMDOtherExit),
    cmd_ride(
            YPLKart.plname + ".cmd.kart",
            Settings.EnablePermissionCMDRide,
            Settings.EnablePermissionCMDOtherRide),
    cmd_leave(
            YPLKart.plname + ".cmd.leave",
            Settings.EnablePermissionCMDLeave,
            Settings.EnablePermissionCMDOtherLeave),
    cmd_character(
            YPLKart.plname + ".cmd.character",
            Settings.EnablePermissionCMDCharacter,
            Settings.EnablePermissionCMDOtherCharacter),
    cmd_characterreset(
            YPLKart.plname + ".cmd.characterreset",
            Settings.EnablePermissionCMDCharacterReset,
            Settings.EnablePermissionCMDOtherCharacterReset),
    cmd_ranking(
            YPLKart.plname + ".cmd.ranking",
            Settings.EnablePermissionCMDRanking,
            Settings.EnablePermissionCMDOtherRanking),

    op_cmd_circuit(
            YPLKart.plname + ".op.cmd.circuit",
            Settings.EnableOPPermissionCMDCircuit,
            Settings.EnableOPPermissionCMDCircuit),
    op_cmd_display(
            YPLKart.plname + ".op.cmd.display",
            Settings.EnableOPPermissionCMDDisplay,
            Settings.EnableOPPermissionCMDDisplay),
    op_cmd_reload(
            YPLKart.plname + ".op.cmd.reload",
            Settings.EnableOPPermissionCMDReload,
            Settings.EnableOPPermissionCMDReload),
    op_cmd_itemboxtool(
            YPLKart.plname + ".op.cmd.itemboxtool",
            Settings.EnableOPPermissionCMDItemBoxTool,
            Settings.EnableOPPermissionCMDItemBoxTool),

    itemcmd_mushroom(
            YPLKart.plname + ".itemcmd.mushroom",
            Settings.EnablePermissionCMDItem,
            Settings.EnablePermissionCMDOtherItem),
    itemcmd_powerfullmushroom(
            YPLKart.plname + ".itemcmd.goldenmushroom",
            Settings.EnablePermissionCMDItem,
            Settings.EnablePermissionCMDOtherItem),
    itemcmd_banana(
            YPLKart.plname + ".itemcmd.banana",
            Settings.EnablePermissionCMDItem,
            Settings.EnablePermissionCMDOtherItem),
    itemcmd_fakeitembox(
            YPLKart.plname + ".itemcmd.fakeitembox",
            Settings.EnablePermissionCMDItem,
            Settings.EnablePermissionCMDOtherItem),
    itemcmd_thunder(
            YPLKart.plname + ".itemcmd.thunder",
            Settings.EnablePermissionCMDItem,
            Settings.EnablePermissionCMDOtherItem),
    itemcmd_star(
            YPLKart.plname + ".itemcmd.star",
            Settings.EnablePermissionCMDItem,
            Settings.EnablePermissionCMDOtherItem),
    itemcmd_turtle(
            YPLKart.plname + ".itemcmd.turtle",
            Settings.EnablePermissionCMDItem,
            Settings.EnablePermissionCMDOtherItem),
    itemcmd_redturtle(
            YPLKart.plname + ".itemcmd.redturtle",
            Settings.EnablePermissionCMDItem,
            Settings.EnablePermissionCMDOtherItem),
    itemcmd_thornedturtle(
            YPLKart.plname + ".itemcmd.thornedturtle",
            Settings.EnablePermissionCMDItem,
            Settings.EnablePermissionCMDOtherItem),
    itemcmd_teresa(
            YPLKart.plname + ".itemcmd.teresa",
            Settings.EnablePermissionCMDItem,
            Settings.EnablePermissionCMDOtherItem),
    itemcmd_gesso(
            YPLKart.plname + ".itemcmd.gesso",
            Settings.EnablePermissionCMDItem,
            Settings.EnablePermissionCMDOtherItem),
    itemcmd_killer(
            YPLKart.plname + ".itemcmd.killer",
            Settings.EnablePermissionCMDItem,
            Settings.EnablePermissionCMDOtherItem),

    use_mushroom(
            YPLKart.plname + ".use.mushroom",
            Settings.EnablePermissionUseItem,
            Settings.EnablePermissionUseItem),
    use_powerfullmushroom(
            YPLKart.plname + ".use.powerfullmushroom",
            Settings.EnablePermissionUseItem,
            Settings.EnablePermissionUseItem),
    use_banana(
            YPLKart.plname + ".use.banana",
            Settings.EnablePermissionUseItem,
            Settings.EnablePermissionUseItem),
    use_turtle(
            YPLKart.plname + ".use.turtle",
            Settings.EnablePermissionUseItem,
            Settings.EnablePermissionUseItem),
    use_redturtle(
            YPLKart.plname + ".use.redturtle",
            Settings.EnablePermissionUseItem,
            Settings.EnablePermissionUseItem),
    use_thornedturtle(
            YPLKart.plname + ".use.thornedturtle",
            Settings.EnablePermissionUseItem,
            Settings.EnablePermissionUseItem),
    use_fakeitembox(
            YPLKart.plname + ".use.fakeitembox",
            Settings.EnablePermissionUseItem,
            Settings.EnablePermissionUseItem),
    use_teresa(
            YPLKart.plname + ".use.teresa",
            Settings.EnablePermissionUseItem,
            Settings.EnablePermissionUseItem),
    use_gesso(
            YPLKart.plname + ".use.gesso",
            Settings.EnablePermissionUseItem,
            Settings.EnablePermissionUseItem),
    use_killer(
            YPLKart.plname + ".use.killer",
            Settings.EnablePermissionUseItem,
            Settings.EnablePermissionUseItem),
    use_thunder(
            YPLKart.plname + ".use.thunder",
            Settings.EnablePermissionUseItem,
            Settings.EnablePermissionUseItem),
    use_star(
            YPLKart.plname + ".use.star",
            Settings.EnablePermissionUseItem,
            Settings.EnablePermissionUseItem),

    interact_boostrail(
            YPLKart.plname + ".interact.dashboard",
            Settings.EnablePermissionInteractObject,
            Settings.EnablePermissionInteractObject),
    interact_banana(
            YPLKart.plname + ".interact.banana",
            Settings.EnablePermissionInteractObject,
            Settings.EnablePermissionInteractObject),
    interact_itembox(
            YPLKart.plname + ".interact.itembox",
            Settings.EnablePermissionInteractObject,
            Settings.EnablePermissionInteractObject),
    interact_fakeitembox(
            YPLKart.plname + ".interact.fakeitembox",
            Settings.EnablePermissionInteractObject,
            Settings.EnablePermissionInteractObject);

    private String permission;
    private boolean setting;
    private boolean settingother;

    private Permission(String permission, boolean setting, boolean settingother) {
        this.permission = permission;
        this.setting = setting;
        this.settingother = settingother;
    }

    public void reload(boolean setting, boolean settingother) {
        this.setting = setting;
        this.settingother = settingother;
    }

    private String getPerm() {
        return this.permission;
    }

    //YPLKart.cmd.<>
    private String getPermBasicCMDOther() {
        return this.permission.substring(0, 11) + "other" + this.permission.substring(11, this.permission.length());
    }

    //YPLKart.itemcmd.<>
    private String getPermItemCMDOther() {
        return this.permission.substring(0, 15) + "other" + this.permission.substring(15, this.permission.length());
    }

    private boolean getSetting() {
        return this.setting;
    }

    private boolean getSettingOther() {
        return this.settingother;
    }

    public boolean isOpPerm() {
        if (getPerm().contains(".op."))
            return true;
        return false;
    }

    public static Boolean hasPermission(Player p, Permission perm, boolean nomessage) {
        if (!perm.getSetting())
            return true;
        if (p.hasPermission(perm.getPerm()))
            return true;

        if (nomessage)
            return false;

        Message.noPermission.sendMessage(p, perm.getPerm());
        return false;
    }

    public static Boolean hasCMDPermission(Player p, Permission perm, boolean targetother, boolean nomessage) {
        if (!targetother) {
            if (!perm.getSetting())
                return true;
            if (p.hasPermission(perm.getPerm()))
                return true;
        } else {
            if (!perm.getSettingOther())
                return true;
            if (perm.getPerm().contains("itemcmd")) {
                System.out.println(perm.getPermItemCMDOther());
                if (p.hasPermission(perm.getPermItemCMDOther()))
                    return true;
            } else {
                if (p.hasPermission(perm.getPermBasicCMDOther()))
                    return true;
            }
        }
        if (nomessage)
            return false;

        if (!targetother) {
            Message.noPermission.sendMessage(p, perm.getPerm());
        } else {
            if (perm.getPerm().contains("itemcmd")) {
                Message.noPermission.sendMessage(p, perm.getPermItemCMDOther());
            } else
                Message.noPermission.sendMessage(p, perm.getPermBasicCMDOther());
        }
        return false;
    }
}
