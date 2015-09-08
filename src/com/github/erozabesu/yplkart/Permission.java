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
    KART_RIDE(ConfigEnum.PERMISSION_KART_RIDE),
    KART_DRIFT(ConfigEnum.PERMISSION_KART_DRIFT),
    OP_KART_REMOVE(ConfigEnum.OP_PERMISSION_KART_REMOVE),

    CMD_KA(null),
    CMD_MENU(ConfigEnum.PERMISSION_CMD_MENU),
    CMD_ENTRY(ConfigEnum.PERMISSION_CMD_ENTRY),
    CMD_FORCEENTRY(ConfigEnum.PERMISSION_CMD_FORCEENTRY),
    CMD_EXIT(ConfigEnum.PERMISSION_CMD_EXIT),
    CMD_KART(ConfigEnum.PERMISSION_CMD_KART),
    CMD_LEAVE(ConfigEnum.PERMISSION_CMD_LEAVE),
    CMD_CHARACTER(ConfigEnum.PERMISSION_CMD_CHARACTER),
    CMD_CHARACTERRESET(ConfigEnum.PERMISSION_CMD_CHARACTERRESET),
    CMD_RANKING(ConfigEnum.PERMISSION_CMD_RANKING),
    CMDOTHER_KA(null),
    CMDOTHER_MENU(ConfigEnum.PERMISSION_CMD_OTHER_MENU),
    CMDOTHER_ENTRY(ConfigEnum.PERMISSION_CMD_OTHER_ENTRY),
    CMDOTHER_FORCEENTRY(ConfigEnum.PERMISSION_CMD_OTHER_FORCEENTRY),
    CMDOTHER_EXIT(ConfigEnum.PERMISSION_CMD_OTHER_EXIT),
    CMDOTHER_KART(ConfigEnum.PERMISSION_CMD_OTHER_KART),
    CMDOTHER_LEAVE(ConfigEnum.PERMISSION_CMD_OTHER_LEAVE),
    CMDOTHER_CHARACTER(ConfigEnum.PERMISSION_CMD_OTHER_CHARACTER),
    CMDOTHER_CHARACTERRESET(ConfigEnum.PERMISSION_CMD_OTHER_CHARACTERRESET),
    CMDOTHER_RANKING(ConfigEnum.PERMISSION_CMD_OTHER_RANKING),

    ITEMCMD_MUSHROOM(ConfigEnum.PERMISSION_CMD_ITEM),
    ITEMCMD_POWERFULLMUSHROOM(ConfigEnum.PERMISSION_CMD_ITEM),
    ITEMCMD_BANANA(ConfigEnum.PERMISSION_CMD_ITEM),
    ITEMCMD_FAKEITEMBOX(ConfigEnum.PERMISSION_CMD_ITEM),
    ITEMCMD_THUNDER(ConfigEnum.PERMISSION_CMD_ITEM),
    ITEMCMD_STAR(ConfigEnum.PERMISSION_CMD_ITEM),
    ITEMCMD_TURTLE(ConfigEnum.PERMISSION_CMD_ITEM),
    ITEMCMD_REDTURTLE(ConfigEnum.PERMISSION_CMD_ITEM),
    ITEMCMD_THORNEDTURTLE(ConfigEnum.PERMISSION_CMD_ITEM),
    ITEMCMD_TERESA(ConfigEnum.PERMISSION_CMD_ITEM),
    ITEMCMD_GESSO(ConfigEnum.PERMISSION_CMD_ITEM),
    ITEMCMD_KILLER(ConfigEnum.PERMISSION_CMD_ITEM),
    ITEMCMDOTHER_MUSHROOM(ConfigEnum.PERMISSION_CMD_OTHER_ITEM),
    ITEMCMDOTHER_POWERFULLMUSHROOM(ConfigEnum.PERMISSION_CMD_OTHER_ITEM),
    ITEMCMDOTHER_BANANA(ConfigEnum.PERMISSION_CMD_OTHER_ITEM),
    ITEMCMDOTHER_FAKEITEMBOX(ConfigEnum.PERMISSION_CMD_OTHER_ITEM),
    ITEMCMDOTHER_THUNDER(ConfigEnum.PERMISSION_CMD_OTHER_ITEM),
    ITEMCMDOTHER_STAR(ConfigEnum.PERMISSION_CMD_OTHER_ITEM),
    ITEMCMDOTHER_TURTLE(ConfigEnum.PERMISSION_CMD_OTHER_ITEM),
    ITEMCMDOTHER_REDTURTLE(ConfigEnum.PERMISSION_CMD_OTHER_ITEM),
    ITEMCMDOTHER_THORNEDTURTLE(ConfigEnum.PERMISSION_CMD_OTHER_ITEM),
    ITEMCMDOTHER_TERESA(ConfigEnum.PERMISSION_CMD_OTHER_ITEM),
    ITEMCMDOTHER_GESSO(ConfigEnum.PERMISSION_CMD_OTHER_ITEM),
    ITEMCMDOTHER_KILLER(ConfigEnum.PERMISSION_CMD_OTHER_ITEM),

    OP_CMD_CIRCUIT(ConfigEnum.OP_PERMISSION_CMD_CIRCUIT),
    OP_CMD_DISPLAY(ConfigEnum.OP_PERMISSION_CMD_DISPLAY),
    OP_CMD_RELOAD(ConfigEnum.OP_PERMISSION_CMD_RELOAD),
    OP_CMD_ITEMBOXTOOL(ConfigEnum.OP_PERMISSION_CMD_ITEMBOXTOOL),

    USE_MUSHROOM(ConfigEnum.PERMISSION_USE_ITEM),
    USE_POWERFULLMUSHROOM(ConfigEnum.PERMISSION_USE_ITEM),
    USE_BANANA(ConfigEnum.PERMISSION_USE_ITEM),
    USE_TURTLE(ConfigEnum.PERMISSION_USE_ITEM),
    USE_REDTURTLE(ConfigEnum.PERMISSION_USE_ITEM),
    USE_THORNEDTURTLE(ConfigEnum.PERMISSION_USE_ITEM),
    USE_FAKEITEMBOX(ConfigEnum.PERMISSION_USE_ITEM),
    USE_TERESA(ConfigEnum.PERMISSION_USE_ITEM),
    USE_GESSO(ConfigEnum.PERMISSION_USE_ITEM),
    USE_KILLER(ConfigEnum.PERMISSION_USE_ITEM),
    USE_THUNDER(ConfigEnum.PERMISSION_USE_ITEM),
    USE_STAR(ConfigEnum.PERMISSION_USE_ITEM),

    INTERACT_DASHBOARD(ConfigEnum.PERMISSION_INTERACT_OBJECT),
    INTERACT_BANANA(ConfigEnum.PERMISSION_INTERACT_OBJECT),
    INTERACT_ITEMBOX(ConfigEnum.PERMISSION_INTERACT_OBJECT),
    INTERACT_FAKEITEMBOX(ConfigEnum.PERMISSION_INTERACT_OBJECT);

    /** パーミッションノード */
    private String permissionNode;

    /** config.ymlのパーミッション設定 */
    private ConfigEnum permissionConfig;

    /** config.ymlのパーミッション設定値 */
    private boolean permissionConfigValue;

    //〓 Main 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * コンストラクタ<br>
     * 要素名をベースにパーミッションノードを生成する<br>
     * また、config.ymlのパーミッション設定を格納する
     * @param permissionConfig config.ymlのパーミッション設定
     */
    private Permission(ConfigEnum permissionConfig) {
        String name = name().toLowerCase();
        this.setPermissionNode(YPLKart.PLUGIN_NAME + "." + name.replace('_', '.'));

        this.setPermissionConfig(permissionConfig);
        this.reloadPermissionConfigValue();
    }

    /** config.ymlのパーミッション設定を格納する */
    private void reloadPermissionConfigValue(){
        setPermissionConfigValue(getPermissionConfig() == null
                ? true : Boolean.valueOf(String.valueOf(getPermissionConfig().getValue())));
    }

    /** 全要素の、config.ymlの設定データに関するメンバ変数を再取得する */
    public static void reloadPermissionConfig(){
        for (Permission permission : values()) {
            permission.reloadPermissionConfigValue();
        }
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
        if (permission == null) {
            return true;
        }

        if (target.isOp()) {
            return true;
        }

        if (!permission.getPermissionConfigValue()) {
            return true;
        }

        if (target.hasPermission(permission.getPermissionNode())) {
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

    /** @return settingsNode config.ymlのパーミッション設定 */
    public ConfigEnum getPermissionConfig() {
        return permissionConfig;
    }

    /** @return config.ymlのパーミッションセッティング値 */
    private boolean getPermissionConfigValue() {
        return this.permissionConfigValue;
    }

    //〓 Setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @param permissionNode パーミッションノード */
    public void setPermissionNode(String permissionNode) {
        this.permissionNode = permissionNode;
    }

    /** @param settingsNode config.ymlのパーミッション設定 */
    public void setPermissionConfig(ConfigEnum settingsNode) {
        this.permissionConfig = settingsNode;
    }

    /** @param settingNodeValue config.ymlのパーミッションセッティング値 */
    public void setPermissionConfigValue(boolean settingNodeValue) {
        this.permissionConfigValue = settingNodeValue;
    }
}
