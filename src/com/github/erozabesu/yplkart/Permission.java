package com.github.erozabesu.yplkart;

import org.bukkit.command.CommandSender;

import com.github.erozabesu.yplkart.data.MessageEnum;
import com.github.erozabesu.yplkart.data.PermissionEnum;
import com.github.erozabesu.yplkart.object.MessageParts;

/**
 * パーミッションノードを管理するクラス
 * @author erozabesu
 */
public enum Permission {
    KART_RIDE(PermissionEnum.ENABLE_PERMISSION$KART_RIDE),
    KART_DRIFT(PermissionEnum.ENABLE_PERMISSION$KART_DRIFT),
    OP_KART_REMOVE(PermissionEnum.ENABLE_OP_PERMISSION$KART_REMOVE),

    CMD_KA(null),
    CMD_MENU(PermissionEnum.ENABLE_PERMISSION$CMD_MENU),
    CMD_ENTRY(PermissionEnum.ENABLE_PERMISSION$CMD_ENTRY),
    CMD_EXIT(PermissionEnum.ENABLE_PERMISSION$CMD_EXIT),
    CMD_KART(PermissionEnum.ENABLE_PERMISSION$CMD_KART),
    CMD_LEAVE(PermissionEnum.ENABLE_PERMISSION$CMD_LEAVE),
    CMD_CHARACTER(PermissionEnum.ENABLE_PERMISSION$CMD_CHARACTER),
    CMD_CHARACTERRESET(PermissionEnum.ENABLE_PERMISSION$CMD_CHARACTERRESET),
    CMD_RANKING(PermissionEnum.ENABLE_PERMISSION$CMD_RANKING),
    CMDOTHER_KA(null),
    CMDOTHER_MENU(PermissionEnum.ENABLE_PERMISSION$CMD_OTHER_MENU),
    CMDOTHER_ENTRY(PermissionEnum.ENABLE_PERMISSION$CMD_OTHER_ENTRY),
    CMDOTHER_EXIT(PermissionEnum.ENABLE_PERMISSION$CMD_OTHER_EXIT),
    CMDOTHER_KART(PermissionEnum.ENABLE_PERMISSION$CMD_OTHER_KART),
    CMDOTHER_LEAVE(PermissionEnum.ENABLE_PERMISSION$CMD_OTHER_LEAVE),
    CMDOTHER_CHARACTER(PermissionEnum.ENABLE_PERMISSION$CMD_OTHER_CHARACTER),
    CMDOTHER_CHARACTERRESET(PermissionEnum.ENABLE_PERMISSION$CMD_OTHER_CHARACTERRESET),
    CMDOTHER_RANKING(PermissionEnum.ENABLE_PERMISSION$CMD_OTHER_RANKING),

    ITEMCMD_MUSHROOM(PermissionEnum.ENABLE_PERMISSION$CMD_ITEM),
    ITEMCMD_POWERFULLMUSHROOM(PermissionEnum.ENABLE_PERMISSION$CMD_ITEM),
    ITEMCMD_BANANA(PermissionEnum.ENABLE_PERMISSION$CMD_ITEM),
    ITEMCMD_FAKEITEMBOX(PermissionEnum.ENABLE_PERMISSION$CMD_ITEM),
    ITEMCMD_THUNDER(PermissionEnum.ENABLE_PERMISSION$CMD_ITEM),
    ITEMCMD_STAR(PermissionEnum.ENABLE_PERMISSION$CMD_ITEM),
    ITEMCMD_TURTLE(PermissionEnum.ENABLE_PERMISSION$CMD_ITEM),
    ITEMCMD_REDTURTLE(PermissionEnum.ENABLE_PERMISSION$CMD_ITEM),
    ITEMCMD_THORNEDTURTLE(PermissionEnum.ENABLE_PERMISSION$CMD_ITEM),
    ITEMCMD_TERESA(PermissionEnum.ENABLE_PERMISSION$CMD_ITEM),
    ITEMCMD_GESSO(PermissionEnum.ENABLE_PERMISSION$CMD_ITEM),
    ITEMCMD_KILLER(PermissionEnum.ENABLE_PERMISSION$CMD_ITEM),
    ITEMCMDOTHER_MUSHROOM(PermissionEnum.ENABLE_PERMISSION$CMD_OTHER_ITEM),
    ITEMCMDOTHER_POWERFULLMUSHROOM(PermissionEnum.ENABLE_PERMISSION$CMD_OTHER_ITEM),
    ITEMCMDOTHER_BANANA(PermissionEnum.ENABLE_PERMISSION$CMD_OTHER_ITEM),
    ITEMCMDOTHER_FAKEITEMBOX(PermissionEnum.ENABLE_PERMISSION$CMD_OTHER_ITEM),
    ITEMCMDOTHER_THUNDER(PermissionEnum.ENABLE_PERMISSION$CMD_OTHER_ITEM),
    ITEMCMDOTHER_STAR(PermissionEnum.ENABLE_PERMISSION$CMD_OTHER_ITEM),
    ITEMCMDOTHER_TURTLE(PermissionEnum.ENABLE_PERMISSION$CMD_OTHER_ITEM),
    ITEMCMDOTHER_REDTURTLE(PermissionEnum.ENABLE_PERMISSION$CMD_OTHER_ITEM),
    ITEMCMDOTHER_THORNEDTURTLE(PermissionEnum.ENABLE_PERMISSION$CMD_OTHER_ITEM),
    ITEMCMDOTHER_TERESA(PermissionEnum.ENABLE_PERMISSION$CMD_OTHER_ITEM),
    ITEMCMDOTHER_GESSO(PermissionEnum.ENABLE_PERMISSION$CMD_OTHER_ITEM),
    ITEMCMDOTHER_KILLER(PermissionEnum.ENABLE_PERMISSION$CMD_OTHER_ITEM),

    OP_CMD_CIRCUIT(PermissionEnum.ENABLE_OP_PERMISSION$CMD_CIRCUIT),
    OP_CMD_DISPLAY(PermissionEnum.ENABLE_OP_PERMISSION$CMD_DISPLAY),
    OP_CMD_RELOAD(PermissionEnum.ENABLE_OP_PERMISSION$CMD_RELOAD),
    OP_CMD_ITEMBOXTOOL(PermissionEnum.ENABLE_OP_PERMISSION$CMD_ITEMBOXTOOL),

    USE_MUSHROOM(PermissionEnum.ENABLE_PERMISSION$USE_ITEM),
    USE_POWERFULLMUSHROOM(PermissionEnum.ENABLE_PERMISSION$USE_ITEM),
    USE_BANANA(PermissionEnum.ENABLE_PERMISSION$USE_ITEM),
    USE_TURTLE(PermissionEnum.ENABLE_PERMISSION$USE_ITEM),
    USE_REDTURTLE(PermissionEnum.ENABLE_PERMISSION$USE_ITEM),
    USE_THORNEDTURTLE(PermissionEnum.ENABLE_PERMISSION$USE_ITEM),
    USE_FAKEITEMBOX(PermissionEnum.ENABLE_PERMISSION$USE_ITEM),
    USE_TERESA(PermissionEnum.ENABLE_PERMISSION$USE_ITEM),
    USE_GESSO(PermissionEnum.ENABLE_PERMISSION$USE_ITEM),
    USE_KILLER(PermissionEnum.ENABLE_PERMISSION$USE_ITEM),
    USE_THUNDER(PermissionEnum.ENABLE_PERMISSION$USE_ITEM),
    USE_STAR(PermissionEnum.ENABLE_PERMISSION$USE_ITEM),

    INTERACT_DASHBOARD(PermissionEnum.ENABLE_PERMISSION$INTERACT_OBJECT),
    INTERACT_BANANA(PermissionEnum.ENABLE_PERMISSION$INTERACT_OBJECT),
    INTERACT_ITEMBOX(PermissionEnum.ENABLE_PERMISSION$INTERACT_OBJECT),
    INTERACT_FAKEITEMBOX(PermissionEnum.ENABLE_PERMISSION$INTERACT_OBJECT);

    /** パーミッションノード */
    private String permissionNode;

    /** config.ymlのパーミッション設定 */
    private PermissionEnum configValue;

    //〓 Main 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * コンストラクタ<br>
     * 要素名をベースにパーミッションノードを生成する<br>
     * また、config.ymlのパーミッション設定を格納する
     * @param configValue config.ymlのパーミッション設定
     */
    private Permission(PermissionEnum configValue) {
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
                || !permission.getConfigValue().getValue()
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

    /** @return config.ymlのパーミッションセッティング */
    private PermissionEnum getConfigValue() {
        return this.configValue;
    }

    //〓 Setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @param permissionNode パーミッションノード */
    public void setPermissionNode(String permissionNode) {
        this.permissionNode = permissionNode;
    }

    /** @param configValue config.ymlのパーミッションセッティング */
    public void setConfigValue(PermissionEnum configValue) {
        this.configValue = configValue;
    }
}
