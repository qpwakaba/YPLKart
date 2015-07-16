package com.github.erozabesu.yplkart.enumdata;

import org.bukkit.entity.Player;

import com.github.erozabesu.yplkart.YPLKart;
import com.github.erozabesu.yplkart.data.Messages;
import com.github.erozabesu.yplkart.data.Settings;

public enum Permission {
    KART_RIDE(
            YPLKart.PLUGIN_NAME + ".kart.ride",
            Settings.EnablePermissionKartRide,
            Settings.EnablePermissionKartRide),
    KART_DRIFT(
            YPLKart.PLUGIN_NAME + ".kart.drift",
            Settings.EnablePermissionKartDrift,
            Settings.EnablePermissionKartDrift),
    OP_KART_REMOVE(
            YPLKart.PLUGIN_NAME + ".op.kart.remove",
            Settings.EnableOPPermissionKartRemove,
            Settings.EnableOPPermissionKartRemove),

    CMD_KA(
            YPLKart.PLUGIN_NAME + ".cmd.ka",
            false,
            false),
    CMD_MENU(
            YPLKart.PLUGIN_NAME + ".cmd.menu",
            Settings.EnablePermissionCMDMenu,
            Settings.EnablePermissionCMDOtherMenu),
    CMD_ENTRY(
            YPLKart.PLUGIN_NAME + ".cmd.entry",
            Settings.EnablePermissionCMDEntry,
            Settings.EnablePermissionCMDOtherEntry),
    CMD_EXIT(
            YPLKart.PLUGIN_NAME + ".cmd.exit",
            Settings.EnablePermissionCMDExit,
            Settings.EnablePermissionCMDOtherExit),
    CMD_RIDE(
            YPLKart.PLUGIN_NAME + ".cmd.kart",
            Settings.EnablePermissionCMDRide,
            Settings.EnablePermissionCMDOtherRide),
    CMD_LEAVE(
            YPLKart.PLUGIN_NAME + ".cmd.leave",
            Settings.EnablePermissionCMDLeave,
            Settings.EnablePermissionCMDOtherLeave),
    CMD_CHARACTER(
            YPLKart.PLUGIN_NAME + ".cmd.character",
            Settings.EnablePermissionCMDCharacter,
            Settings.EnablePermissionCMDOtherCharacter),
    CMD_CHARACTERRESET(
            YPLKart.PLUGIN_NAME + ".cmd.characterreset",
            Settings.EnablePermissionCMDCharacterReset,
            Settings.EnablePermissionCMDOtherCharacterReset),
    CMD_RANKING(
            YPLKart.PLUGIN_NAME + ".cmd.ranking",
            Settings.EnablePermissionCMDRanking,
            Settings.EnablePermissionCMDOtherRanking),

    OP_CMD_CIRCUIT(
            YPLKart.PLUGIN_NAME + ".op.cmd.circuit",
            Settings.EnableOPPermissionCMDCircuit,
            Settings.EnableOPPermissionCMDCircuit),
    OP_CMD_DISPLAY(
            YPLKart.PLUGIN_NAME + ".op.cmd.display",
            Settings.EnableOPPermissionCMDDisplay,
            Settings.EnableOPPermissionCMDDisplay),
    OP_CMD_RELOAD(
            YPLKart.PLUGIN_NAME + ".op.cmd.reload",
            Settings.EnableOPPermissionCMDReload,
            Settings.EnableOPPermissionCMDReload),
    OP_CMD_ITEMBOX(
            YPLKart.PLUGIN_NAME + ".op.cmd.itemboxtool",
            Settings.EnableOPPermissionCMDItemBoxTool,
            Settings.EnableOPPermissionCMDItemBoxTool),

    ITEMCMD_MUSHROOM(
            YPLKart.PLUGIN_NAME + ".itemcmd.mushroom",
            Settings.EnablePermissionCMDItem,
            Settings.EnablePermissionCMDOtherItem),
    ITEMCMD_POWERFULLMUSHROOM(
            YPLKart.PLUGIN_NAME + ".itemcmd.goldenmushroom",
            Settings.EnablePermissionCMDItem,
            Settings.EnablePermissionCMDOtherItem),
    ITEMCMD_BANANA(
            YPLKart.PLUGIN_NAME + ".itemcmd.banana",
            Settings.EnablePermissionCMDItem,
            Settings.EnablePermissionCMDOtherItem),
    ITEMCMD_FAKEITEMBOX(
            YPLKart.PLUGIN_NAME + ".itemcmd.fakeitembox",
            Settings.EnablePermissionCMDItem,
            Settings.EnablePermissionCMDOtherItem),
    ITEMCMD_THUNDER(
            YPLKart.PLUGIN_NAME + ".itemcmd.thunder",
            Settings.EnablePermissionCMDItem,
            Settings.EnablePermissionCMDOtherItem),
    ITEMCMD_STAR(
            YPLKart.PLUGIN_NAME + ".itemcmd.star",
            Settings.EnablePermissionCMDItem,
            Settings.EnablePermissionCMDOtherItem),
    ITEMCMD_TURTLE(
            YPLKart.PLUGIN_NAME + ".itemcmd.turtle",
            Settings.EnablePermissionCMDItem,
            Settings.EnablePermissionCMDOtherItem),
    ITEMCMD_REDTURTLE(
            YPLKart.PLUGIN_NAME + ".itemcmd.redturtle",
            Settings.EnablePermissionCMDItem,
            Settings.EnablePermissionCMDOtherItem),
    ITEMCMD_THORNEDTURTLE(
            YPLKart.PLUGIN_NAME + ".itemcmd.thornedturtle",
            Settings.EnablePermissionCMDItem,
            Settings.EnablePermissionCMDOtherItem),
    ITEMCMD_TERESA(
            YPLKart.PLUGIN_NAME + ".itemcmd.teresa",
            Settings.EnablePermissionCMDItem,
            Settings.EnablePermissionCMDOtherItem),
    ITEMCMD_GESSO(
            YPLKart.PLUGIN_NAME + ".itemcmd.gesso",
            Settings.EnablePermissionCMDItem,
            Settings.EnablePermissionCMDOtherItem),
    ITEMCMD_KILLER(
            YPLKart.PLUGIN_NAME + ".itemcmd.killer",
            Settings.EnablePermissionCMDItem,
            Settings.EnablePermissionCMDOtherItem),

    USE_MUSHROOM(
            YPLKart.PLUGIN_NAME + ".use.mushroom",
            Settings.EnablePermissionUseItem,
            Settings.EnablePermissionUseItem),
    USE_POWERFULLMUSHROOM(
            YPLKart.PLUGIN_NAME + ".use.powerfullmushroom",
            Settings.EnablePermissionUseItem,
            Settings.EnablePermissionUseItem),
    USE_BANANA(
            YPLKart.PLUGIN_NAME + ".use.banana",
            Settings.EnablePermissionUseItem,
            Settings.EnablePermissionUseItem),
    USE_TURTLE(
            YPLKart.PLUGIN_NAME + ".use.turtle",
            Settings.EnablePermissionUseItem,
            Settings.EnablePermissionUseItem),
    USE_REDTURTLE(
            YPLKart.PLUGIN_NAME + ".use.redturtle",
            Settings.EnablePermissionUseItem,
            Settings.EnablePermissionUseItem),
    USE_THORNEDTURTLE(
            YPLKart.PLUGIN_NAME + ".use.thornedturtle",
            Settings.EnablePermissionUseItem,
            Settings.EnablePermissionUseItem),
    USE_FAKEITEMBOX(
            YPLKart.PLUGIN_NAME + ".use.fakeitembox",
            Settings.EnablePermissionUseItem,
            Settings.EnablePermissionUseItem),
    USE_TERESA(
            YPLKart.PLUGIN_NAME + ".use.teresa",
            Settings.EnablePermissionUseItem,
            Settings.EnablePermissionUseItem),
    USE_GESSO(
            YPLKart.PLUGIN_NAME + ".use.gesso",
            Settings.EnablePermissionUseItem,
            Settings.EnablePermissionUseItem),
    USE_KILLER(
            YPLKart.PLUGIN_NAME + ".use.killer",
            Settings.EnablePermissionUseItem,
            Settings.EnablePermissionUseItem),
    USE_THUNDER(
            YPLKart.PLUGIN_NAME + ".use.thunder",
            Settings.EnablePermissionUseItem,
            Settings.EnablePermissionUseItem),
    USE_STAR(
            YPLKart.PLUGIN_NAME + ".use.star",
            Settings.EnablePermissionUseItem,
            Settings.EnablePermissionUseItem),

    INTERACT_DASHBOARD(
            YPLKart.PLUGIN_NAME + ".interact.dashboard",
            Settings.EnablePermissionInteractObject,
            Settings.EnablePermissionInteractObject),
    INTERACT_BANANA(
            YPLKart.PLUGIN_NAME + ".interact.banana",
            Settings.EnablePermissionInteractObject,
            Settings.EnablePermissionInteractObject),
    INTERACT_ITEMBOX(
            YPLKart.PLUGIN_NAME + ".interact.itembox",
            Settings.EnablePermissionInteractObject,
            Settings.EnablePermissionInteractObject),
    INTERACT_FAKEITEMBOX(
            YPLKart.PLUGIN_NAME + ".interact.fakeitembox",
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

        Messages.noPermission.sendMessage(p, perm.getPerm());
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
            Messages.noPermission.sendMessage(p, perm.getPerm());
        } else {
            if (perm.getPerm().contains("itemcmd")) {
                Messages.noPermission.sendMessage(p, perm.getPermItemCMDOther());
            } else
                Messages.noPermission.sendMessage(p, perm.getPermBasicCMDOther());
        }
        return false;
    }
}
