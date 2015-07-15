package com.github.erozabesu.yplkart.Enum;

import org.bukkit.entity.Player;

import com.github.erozabesu.yplkart.YPLKart;
import com.github.erozabesu.yplkart.Data.Message;
import com.github.erozabesu.yplkart.Data.Settings;

public enum Permission {
    KART_RIDE(
            YPLKart.plname + ".kart.ride",
            Settings.EnablePermissionKartRide,
            Settings.EnablePermissionKartRide),
    KART_DRIFT(
            YPLKart.plname + ".kart.drift",
            Settings.EnablePermissionKartDrift,
            Settings.EnablePermissionKartDrift),
    OP_KART_REMOVE(
            YPLKart.plname + ".op.kart.remove",
            Settings.EnableOPPermissionKartRemove,
            Settings.EnableOPPermissionKartRemove),

    CMD_KA(
            YPLKart.plname + ".cmd.ka",
            false,
            false),
    CMD_MENU(
            YPLKart.plname + ".cmd.menu",
            Settings.EnablePermissionCMDMenu,
            Settings.EnablePermissionCMDOtherMenu),
    CMD_ENTRY(
            YPLKart.plname + ".cmd.entry",
            Settings.EnablePermissionCMDEntry,
            Settings.EnablePermissionCMDOtherEntry),
    CMD_EXIT(
            YPLKart.plname + ".cmd.exit",
            Settings.EnablePermissionCMDExit,
            Settings.EnablePermissionCMDOtherExit),
    CMD_RIDE(
            YPLKart.plname + ".cmd.kart",
            Settings.EnablePermissionCMDRide,
            Settings.EnablePermissionCMDOtherRide),
    CMD_LEAVE(
            YPLKart.plname + ".cmd.leave",
            Settings.EnablePermissionCMDLeave,
            Settings.EnablePermissionCMDOtherLeave),
    CMD_CHARACTER(
            YPLKart.plname + ".cmd.character",
            Settings.EnablePermissionCMDCharacter,
            Settings.EnablePermissionCMDOtherCharacter),
    CMD_CHARACTERRESET(
            YPLKart.plname + ".cmd.characterreset",
            Settings.EnablePermissionCMDCharacterReset,
            Settings.EnablePermissionCMDOtherCharacterReset),
    CMD_RANKING(
            YPLKart.plname + ".cmd.ranking",
            Settings.EnablePermissionCMDRanking,
            Settings.EnablePermissionCMDOtherRanking),

    OP_CMD_CIRCUIT(
            YPLKart.plname + ".op.cmd.circuit",
            Settings.EnableOPPermissionCMDCircuit,
            Settings.EnableOPPermissionCMDCircuit),
    OP_CMD_DISPLAY(
            YPLKart.plname + ".op.cmd.display",
            Settings.EnableOPPermissionCMDDisplay,
            Settings.EnableOPPermissionCMDDisplay),
    OP_CMD_RELOAD(
            YPLKart.plname + ".op.cmd.reload",
            Settings.EnableOPPermissionCMDReload,
            Settings.EnableOPPermissionCMDReload),
    OP_CMD_ITEMBOX(
            YPLKart.plname + ".op.cmd.itemboxtool",
            Settings.EnableOPPermissionCMDItemBoxTool,
            Settings.EnableOPPermissionCMDItemBoxTool),

    ITEMCMD_MUSHROOM(
            YPLKart.plname + ".itemcmd.mushroom",
            Settings.EnablePermissionCMDItem,
            Settings.EnablePermissionCMDOtherItem),
    ITEMCMD_POWERFULLMUSHROOM(
            YPLKart.plname + ".itemcmd.goldenmushroom",
            Settings.EnablePermissionCMDItem,
            Settings.EnablePermissionCMDOtherItem),
    ITEMCMD_BANANA(
            YPLKart.plname + ".itemcmd.banana",
            Settings.EnablePermissionCMDItem,
            Settings.EnablePermissionCMDOtherItem),
    ITEMCMD_FAKEITEMBOX(
            YPLKart.plname + ".itemcmd.fakeitembox",
            Settings.EnablePermissionCMDItem,
            Settings.EnablePermissionCMDOtherItem),
    ITEMCMD_THUNDER(
            YPLKart.plname + ".itemcmd.thunder",
            Settings.EnablePermissionCMDItem,
            Settings.EnablePermissionCMDOtherItem),
    ITEMCMD_STAR(
            YPLKart.plname + ".itemcmd.star",
            Settings.EnablePermissionCMDItem,
            Settings.EnablePermissionCMDOtherItem),
    ITEMCMD_TURTLE(
            YPLKart.plname + ".itemcmd.turtle",
            Settings.EnablePermissionCMDItem,
            Settings.EnablePermissionCMDOtherItem),
    ITEMCMD_REDTURTLE(
            YPLKart.plname + ".itemcmd.redturtle",
            Settings.EnablePermissionCMDItem,
            Settings.EnablePermissionCMDOtherItem),
    ITEMCMD_THORNEDTURTLE(
            YPLKart.plname + ".itemcmd.thornedturtle",
            Settings.EnablePermissionCMDItem,
            Settings.EnablePermissionCMDOtherItem),
    ITEMCMD_TERESA(
            YPLKart.plname + ".itemcmd.teresa",
            Settings.EnablePermissionCMDItem,
            Settings.EnablePermissionCMDOtherItem),
    ITEMCMD_GESSO(
            YPLKart.plname + ".itemcmd.gesso",
            Settings.EnablePermissionCMDItem,
            Settings.EnablePermissionCMDOtherItem),
    ITEMCMD_KILLER(
            YPLKart.plname + ".itemcmd.killer",
            Settings.EnablePermissionCMDItem,
            Settings.EnablePermissionCMDOtherItem),

    USE_MUSHROOM(
            YPLKart.plname + ".use.mushroom",
            Settings.EnablePermissionUseItem,
            Settings.EnablePermissionUseItem),
    USE_POWERFULLMUSHROOM(
            YPLKart.plname + ".use.powerfullmushroom",
            Settings.EnablePermissionUseItem,
            Settings.EnablePermissionUseItem),
    USE_BANANA(
            YPLKart.plname + ".use.banana",
            Settings.EnablePermissionUseItem,
            Settings.EnablePermissionUseItem),
    USE_TURTLE(
            YPLKart.plname + ".use.turtle",
            Settings.EnablePermissionUseItem,
            Settings.EnablePermissionUseItem),
    USE_REDTURTLE(
            YPLKart.plname + ".use.redturtle",
            Settings.EnablePermissionUseItem,
            Settings.EnablePermissionUseItem),
    USE_THORNEDTURTLE(
            YPLKart.plname + ".use.thornedturtle",
            Settings.EnablePermissionUseItem,
            Settings.EnablePermissionUseItem),
    USE_FAKEITEMBOX(
            YPLKart.plname + ".use.fakeitembox",
            Settings.EnablePermissionUseItem,
            Settings.EnablePermissionUseItem),
    USE_TERESA(
            YPLKart.plname + ".use.teresa",
            Settings.EnablePermissionUseItem,
            Settings.EnablePermissionUseItem),
    USE_GESSO(
            YPLKart.plname + ".use.gesso",
            Settings.EnablePermissionUseItem,
            Settings.EnablePermissionUseItem),
    USE_KILLER(
            YPLKart.plname + ".use.killer",
            Settings.EnablePermissionUseItem,
            Settings.EnablePermissionUseItem),
    USE_THUNDER(
            YPLKart.plname + ".use.thunder",
            Settings.EnablePermissionUseItem,
            Settings.EnablePermissionUseItem),
    USE_STAR(
            YPLKart.plname + ".use.star",
            Settings.EnablePermissionUseItem,
            Settings.EnablePermissionUseItem),

    INTERACT_DASHBOARD(
            YPLKart.plname + ".interact.dashboard",
            Settings.EnablePermissionInteractObject,
            Settings.EnablePermissionInteractObject),
    INTERACT_BANANA(
            YPLKart.plname + ".interact.banana",
            Settings.EnablePermissionInteractObject,
            Settings.EnablePermissionInteractObject),
    INTERACT_ITEMBOX(
            YPLKart.plname + ".interact.itembox",
            Settings.EnablePermissionInteractObject,
            Settings.EnablePermissionInteractObject),
    INTERACT_FAKEITEMBOX(
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
