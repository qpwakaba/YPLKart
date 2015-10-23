package com.github.erozabesu.yplkart;

import org.bukkit.command.CommandSender;

import com.github.erozabesu.yplkart.data.ConfigEnum;
import com.github.erozabesu.yplkart.data.MessageEnum;
import com.github.erozabesu.yplkart.object.MessageParts;

/**
 * パーミッションノード、及びconfig.ymlのパーミッション設定を管理するクラス
 * @author erozabesu
 */
public enum Permission {
    KART_RIDE(ConfigEnum.enable_permission$kart_ride),
    KART_DRIFT(ConfigEnum.enable_permission$kart_drift),
    OP_KART_REMOVE(ConfigEnum.enable_op_permission$kart_remove),

    CMD_KA(null),
    CMD_MENU(ConfigEnum.enable_permission$cmd_menu),
    CMD_ENTRY(ConfigEnum.enable_permission$cmd_entry),
    CMD_EXIT(ConfigEnum.enable_permission$cmd_exit),
    CMD_KART(ConfigEnum.enable_permission$cmd_kart),
    CMD_LEAVE(ConfigEnum.enable_permission$cmd_leave),
    CMD_CHARACTER(ConfigEnum.enable_permission$cmd_character),
    CMD_CHARACTERRESET(ConfigEnum.enable_permission$cmd_characterreset),
    CMD_RANKING(ConfigEnum.enable_permission$cmd_ranking),
    CMDOTHER_KA(null),
    CMDOTHER_MENU(ConfigEnum.enable_permission$cmd_other_menu),
    CMDOTHER_ENTRY(ConfigEnum.enable_permission$cmd_other_entry),
    CMDOTHER_EXIT(ConfigEnum.enable_permission$cmd_other_exit),
    CMDOTHER_KART(ConfigEnum.enable_permission$cmd_other_kart),
    CMDOTHER_LEAVE(ConfigEnum.enable_permission$cmd_other_leave),
    CMDOTHER_CHARACTER(ConfigEnum.enable_permission$cmd_other_character),
    CMDOTHER_CHARACTERRESET(ConfigEnum.enable_permission$cmd_other_characterreset),
    CMDOTHER_RANKING(ConfigEnum.enable_permission$cmd_other_ranking),

    ITEMCMD_MUSHROOM(ConfigEnum.enable_permission$cmd_item),
    ITEMCMD_POWERFULLMUSHROOM(ConfigEnum.enable_permission$cmd_item),
    ITEMCMD_BANANA(ConfigEnum.enable_permission$cmd_item),
    ITEMCMD_FAKEITEMBOX(ConfigEnum.enable_permission$cmd_item),
    ITEMCMD_THUNDER(ConfigEnum.enable_permission$cmd_item),
    ITEMCMD_STAR(ConfigEnum.enable_permission$cmd_item),
    ITEMCMD_TURTLE(ConfigEnum.enable_permission$cmd_item),
    ITEMCMD_REDTURTLE(ConfigEnum.enable_permission$cmd_item),
    ITEMCMD_THORNEDTURTLE(ConfigEnum.enable_permission$cmd_item),
    ITEMCMD_TERESA(ConfigEnum.enable_permission$cmd_item),
    ITEMCMD_GESSO(ConfigEnum.enable_permission$cmd_item),
    ITEMCMD_KILLER(ConfigEnum.enable_permission$cmd_item),
    ITEMCMDOTHER_MUSHROOM(ConfigEnum.enable_permission$cmd_other_item),
    ITEMCMDOTHER_POWERFULLMUSHROOM(ConfigEnum.enable_permission$cmd_other_item),
    ITEMCMDOTHER_BANANA(ConfigEnum.enable_permission$cmd_other_item),
    ITEMCMDOTHER_FAKEITEMBOX(ConfigEnum.enable_permission$cmd_other_item),
    ITEMCMDOTHER_THUNDER(ConfigEnum.enable_permission$cmd_other_item),
    ITEMCMDOTHER_STAR(ConfigEnum.enable_permission$cmd_other_item),
    ITEMCMDOTHER_TURTLE(ConfigEnum.enable_permission$cmd_other_item),
    ITEMCMDOTHER_REDTURTLE(ConfigEnum.enable_permission$cmd_other_item),
    ITEMCMDOTHER_THORNEDTURTLE(ConfigEnum.enable_permission$cmd_other_item),
    ITEMCMDOTHER_TERESA(ConfigEnum.enable_permission$cmd_other_item),
    ITEMCMDOTHER_GESSO(ConfigEnum.enable_permission$cmd_other_item),
    ITEMCMDOTHER_KILLER(ConfigEnum.enable_permission$cmd_other_item),

    OP_CMD_CIRCUIT(ConfigEnum.enable_op_permission$cmd_circuit),
    OP_CMD_DISPLAY(ConfigEnum.enable_op_permission$cmd_display),
    OP_CMD_RELOAD(ConfigEnum.enable_op_permission$cmd_reload),
    OP_CMD_ITEMBOXTOOL(ConfigEnum.enable_op_permission$cmd_itemboxtool),

    USE_MUSHROOM(ConfigEnum.enable_permission$use_item),
    USE_POWERFULLMUSHROOM(ConfigEnum.enable_permission$use_item),
    USE_BANANA(ConfigEnum.enable_permission$use_item),
    USE_TURTLE(ConfigEnum.enable_permission$use_item),
    USE_REDTURTLE(ConfigEnum.enable_permission$use_item),
    USE_THORNEDTURTLE(ConfigEnum.enable_permission$use_item),
    USE_FAKEITEMBOX(ConfigEnum.enable_permission$use_item),
    USE_TERESA(ConfigEnum.enable_permission$use_item),
    USE_GESSO(ConfigEnum.enable_permission$use_item),
    USE_KILLER(ConfigEnum.enable_permission$use_item),
    USE_THUNDER(ConfigEnum.enable_permission$use_item),
    USE_STAR(ConfigEnum.enable_permission$use_item),

    INTERACT_DASHBOARD(ConfigEnum.enable_permission$interact_object),
    INTERACT_BANANA(ConfigEnum.enable_permission$interact_object),
    INTERACT_ITEMBOX(ConfigEnum.enable_permission$interact_object),
    INTERACT_FAKEITEMBOX(ConfigEnum.enable_permission$interact_object);

    /** パーミッションノード */
    private String permissionNode;

    /** config.ymlのパーミッション設定値 */
    private Boolean configValue;

    //〓 Main 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * コンストラクタ<br>
     * 要素名をベースにパーミッションノードを生成する<br>
     * また、config.ymlのパーミッション設定を格納する
     * @param configValue config.ymlのパーミッション設定
     */
    private Permission(Boolean configValue) {
        String name = name().toLowerCase();
        this.setPermissionNode(YPLKart.PLUGIN_NAME + "." + name.replace('_', '.'));
        this.setConfigValue(configValue);
    }

    //〓 Util 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * playerがパーミッションを所有しているかどうかを確認する
     * @param target パーミッションを確認する対象
     * @param permission 確認するパーミッション
     * @param noMessage playerにパーミッションエラーメッセージを送信するかどうか
     * @return 対象がパーミッションを所有しているかどうか
     */
    public static Boolean hasPermission(CommandSender target, Permission permission, boolean noMessage) {
        if (permission == null
                || target.isOp()
                || !permission.getConfigValue()
                || target.hasPermission(permission.getPermissionNode())) {
            return true;
        }

        if (noMessage) {
            return false;
        }

        MessageEnum.noPermission.sendConvertedMessage(target, MessageParts.getMessageParts(permission));
        return false;
    }

    /**
     * 引数permissionに対応する、他プレイヤーをターゲットにした場合のパーミッションを返す<br>
     * 対応するパーミッションがない場合は引数permissionを返す
     * @param permission PermissionManager
     * @return PermissionManager
     */
    public Permission getTargetOtherPermission() {
        String targetOtherPermissionName = this.name().replace("CMD", "CMDOTHER");
        Permission permissionOther = getPermissionByString(targetOtherPermissionName);

        return permissionOther == null ? this : permissionOther;
    }

    /**
     * 引数enumNameと一致する要素名を持つPermissionManagerを返す
     * @param enumName 要素名
     * @return PermissionManager
     */
    public static Permission getPermissionByString(String enumName) {
        for (Permission permission : Permission.values()) {
            if (permission.name().equalsIgnoreCase(enumName)) {
                return permission;
            }
        }
        return null;
    }

    //〓 Getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @return パーミッションノード */
    public String getPermissionNode() {
        return this.permissionNode;
    }

    /** @return config.ymlのパーミッションセッティング値 */
    private Boolean getConfigValue() {
        return this.configValue;
    }

    //〓 Setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @param permissionNode パーミッションノード */
    public void setPermissionNode(String permissionNode) {
        this.permissionNode = permissionNode;
    }

    /** @param configValue config.ymlのパーミッションセッティング値 */
    public void setConfigValue(Boolean configValue) {
        this.configValue = configValue;
    }
}
