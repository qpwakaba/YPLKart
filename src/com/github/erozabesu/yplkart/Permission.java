package com.github.erozabesu.yplkart;

import org.bukkit.entity.Player;

import com.github.erozabesu.yplkart.data.ConfigEnum;
import com.github.erozabesu.yplkart.data.MessageEnum;

/**
 * パーミッションノード、及びconfig.ymlのパーミッション設定を管理するクラス
 * @author erozabesu
 */
public enum Permission {
    KART_RIDE(ConfigEnum.PERMISSION_KART_RIDE, ConfigEnum.PERMISSION_KART_RIDE),
    KART_DRIFT(ConfigEnum.PERMISSION_KART_DRIFT, ConfigEnum.PERMISSION_KART_DRIFT),
    OP_KART_REMOVE(ConfigEnum.OP_PERMISSION_KART_REMOVE, ConfigEnum.OP_PERMISSION_KART_REMOVE),

    CMD_KA(null, null),
    CMD_MENU(ConfigEnum.PERMISSION_CMD_MENU, ConfigEnum.PERMISSION_CMD_OTHER_MENU),
    CMD_ENTRY(ConfigEnum.PERMISSION_CMD_ENTRY, ConfigEnum.PERMISSION_CMD_OTHER_ENTRY),
    CMD_EXIT(ConfigEnum.PERMISSION_CMD_EXIT, ConfigEnum.PERMISSION_CMD_OTHER_EXIT),
    CMD_KART(ConfigEnum.PERMISSION_CMD_KART, ConfigEnum.PERMISSION_CMD_OTHER_KART),
    CMD_LEAVE(ConfigEnum.PERMISSION_CMD_LEAVE, ConfigEnum.PERMISSION_CMD_OTHER_LEAVE),
    CMD_CHARACTER(ConfigEnum.PERMISSION_CMD_CHARACTER
            , ConfigEnum.PERMISSION_CMD_OTHER_CHARACTER),
    CMD_CHARACTERRESET(ConfigEnum.PERMISSION_CMD_CHARACTERRESET
            , ConfigEnum.PERMISSION_CMD_OTHER_CHARACTERRESET),
    CMD_RANKING(ConfigEnum.PERMISSION_CMD_RANKING, ConfigEnum.PERMISSION_CMD_OTHER_RANKING),

    OP_CMD_CIRCUIT(ConfigEnum.OP_PERMISSION_CMD_CIRCUIT, ConfigEnum.OP_PERMISSION_CMD_CIRCUIT),
    OP_CMD_DISPLAY(ConfigEnum.OP_PERMISSION_CMD_DISPLAY, ConfigEnum.OP_PERMISSION_CMD_DISPLAY),
    OP_CMD_RELOAD(ConfigEnum.OP_PERMISSION_CMD_RELOAD, ConfigEnum.OP_PERMISSION_CMD_RELOAD),
    OP_CMD_ITEMBOXTOOL(ConfigEnum.OP_PERMISSION_CMD_ITEMBOXTOOL,
            ConfigEnum.OP_PERMISSION_CMD_ITEMBOXTOOL),

    ITEMCMD_MUSHROOM(ConfigEnum.PERMISSION_CMD_ITEM, ConfigEnum.PERMISSION_CMD_OTHER_ITEM),
    ITEMCMD_POWERFULLMUSHROOM(ConfigEnum.PERMISSION_CMD_ITEM
            , ConfigEnum.PERMISSION_CMD_OTHER_ITEM),
    ITEMCMD_BANANA(ConfigEnum.PERMISSION_CMD_ITEM, ConfigEnum.PERMISSION_CMD_OTHER_ITEM),
    ITEMCMD_FAKEITEMBOX(ConfigEnum.PERMISSION_CMD_ITEM, ConfigEnum.PERMISSION_CMD_OTHER_ITEM),
    ITEMCMD_THUNDER(ConfigEnum.PERMISSION_CMD_ITEM, ConfigEnum.PERMISSION_CMD_OTHER_ITEM),
    ITEMCMD_STAR(ConfigEnum.PERMISSION_CMD_ITEM, ConfigEnum.PERMISSION_CMD_OTHER_ITEM),
    ITEMCMD_TURTLE(ConfigEnum.PERMISSION_CMD_ITEM, ConfigEnum.PERMISSION_CMD_OTHER_ITEM),
    ITEMCMD_REDTURTLE(ConfigEnum.PERMISSION_CMD_ITEM, ConfigEnum.PERMISSION_CMD_OTHER_ITEM),
    ITEMCMD_THORNEDTURTLE(ConfigEnum.PERMISSION_CMD_ITEM
            , ConfigEnum.PERMISSION_CMD_OTHER_ITEM),
    ITEMCMD_TERESA(ConfigEnum.PERMISSION_CMD_ITEM, ConfigEnum.PERMISSION_CMD_OTHER_ITEM),
    ITEMCMD_GESSO(ConfigEnum.PERMISSION_CMD_ITEM, ConfigEnum.PERMISSION_CMD_OTHER_ITEM),
    ITEMCMD_KILLER(ConfigEnum.PERMISSION_CMD_ITEM, ConfigEnum.PERMISSION_CMD_OTHER_ITEM),

    USE_MUSHROOM(ConfigEnum.PERMISSION_USE_ITEM, ConfigEnum.PERMISSION_USE_ITEM),
    USE_POWERFULLMUSHROOM(ConfigEnum.PERMISSION_USE_ITEM, ConfigEnum.PERMISSION_USE_ITEM),
    USE_BANANA(ConfigEnum.PERMISSION_USE_ITEM, ConfigEnum.PERMISSION_USE_ITEM),
    USE_TURTLE(ConfigEnum.PERMISSION_USE_ITEM, ConfigEnum.PERMISSION_USE_ITEM),
    USE_REDTURTLE(ConfigEnum.PERMISSION_USE_ITEM, ConfigEnum.PERMISSION_USE_ITEM),
    USE_THORNEDTURTLE(ConfigEnum.PERMISSION_USE_ITEM, ConfigEnum.PERMISSION_USE_ITEM),
    USE_FAKEITEMBOX(ConfigEnum.PERMISSION_USE_ITEM, ConfigEnum.PERMISSION_USE_ITEM),
    USE_TERESA(ConfigEnum.PERMISSION_USE_ITEM, ConfigEnum.PERMISSION_USE_ITEM),
    USE_GESSO(ConfigEnum.PERMISSION_USE_ITEM, ConfigEnum.PERMISSION_USE_ITEM),
    USE_KILLER(ConfigEnum.PERMISSION_USE_ITEM, ConfigEnum.PERMISSION_USE_ITEM),
    USE_THUNDER(ConfigEnum.PERMISSION_USE_ITEM, ConfigEnum.PERMISSION_USE_ITEM),
    USE_STAR(ConfigEnum.PERMISSION_USE_ITEM, ConfigEnum.PERMISSION_USE_ITEM),

    INTERACT_DASHBOARD(ConfigEnum.PERMISSION_INTERACT_OBJECT,
            ConfigEnum.PERMISSION_INTERACT_OBJECT),
    INTERACT_BANANA(ConfigEnum.PERMISSION_INTERACT_OBJECT,
            ConfigEnum.PERMISSION_INTERACT_OBJECT),
    INTERACT_ITEMBOX(ConfigEnum.PERMISSION_INTERACT_OBJECT,
            ConfigEnum.PERMISSION_INTERACT_OBJECT),
    INTERACT_FAKEITEMBOX(ConfigEnum.PERMISSION_INTERACT_OBJECT,
            ConfigEnum.PERMISSION_INTERACT_OBJECT);

    /** パーミッションノード */
    private String permission;

    /** config.ymlのパーミッション設定 */
    private ConfigEnum settingsNode;

    /** config.ymlの他プレイヤーに対するパーミッション設定 */
    private ConfigEnum settingsNodeOther;

    /** config.ymlのパーミッション設定値 */
    private boolean settingNodeValue;

    /** config.ymlの他プレイヤーに対するパーミッション設定値 */
    private boolean settingNodeOtherValue;

    //〓 main 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 要素名をベースにパーミッションノードを生成する
     */
    private Permission(ConfigEnum settingsNode, ConfigEnum settingsNodeOther) {
        String name = name().toLowerCase();
        this.setPermission(YPLKart.PLUGIN_NAME + "." + name.replace('_', '.'));
        this.setSettingsNode(settingsNode);
        this.setSettingsNodeOther(settingsNodeOther);
        this.setConfigData();
    }

    /**
     * config.ymlのパーミッション設定を格納する
     */
    private void setConfigData(){
        setSettingNodeValue(getSettingsNode() == null
                ? true : Boolean.valueOf(String.valueOf(getSettingsNode().getValue())));
        setSettingNodeOtherValue(getSettingsNodeOther() == null
                ? true : Boolean.valueOf(String.valueOf(getSettingsNodeOther().getValue())));
    }

    //〓 getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @return パーミッションノード */
    private String getPermission() {
        return this.permission;
    }

    /** @return settingsNode config.ymlのパーミッション設定 */
    public ConfigEnum getSettingsNode() {
        return settingsNode;
    }

    /** @return settingsNodeOther config.ymlの他プレイヤーに対するパーミッション設定 */
    public ConfigEnum getSettingsNodeOther() {
        return settingsNodeOther;
    }

    /** @return config.ymlのパーミッションセッティング値 */
    private boolean getSettingNodeValue() {
        return this.settingNodeValue;
    }

    /** @return config.ymlの他プレイヤーに対するパーミッション設定値 */
    private boolean getSettingNodeOtherValue() {
        return this.settingNodeOtherValue;
    }

    //〓 setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @param permission セットするパーミッションノード */
    public void setPermission(String permission) {
        this.permission = permission;
    }

    /** @param settingsNode セットするconfig.ymlのパーミッション設定 */
    public void setSettingsNode(ConfigEnum settingsNode) {
        this.settingsNode = settingsNode;
    }

    /** @param settingsNodeOther セットするconfig.ymlの他プレイヤーに対するパーミッション設定 */
    public void setSettingsNodeOther(ConfigEnum settingsNodeOther) {
        this.settingsNodeOther = settingsNodeOther;
    }

    /** @param settingNodeValue セットするconfig.ymlのパーミッションセッティング値 */
    public void setSettingNodeValue(boolean settingNodeValue) {
        this.settingNodeValue = settingNodeValue;
    }

    /** @param settingNodeOtherValue セットするconfig.ymlの他プレイヤーに対するパーミッション設定値 */
    public void setSettingNodeOtherValue(boolean settingNodeOtherValue) {
        this.settingNodeOtherValue = settingNodeOtherValue;
    }

    //〓 do 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 他プレイヤーをターゲットにしたコマンドパーミッションノードを返す
     * @return パーミッションノード
     */
    private String getPermBasicCMDOther() {
        return getPermission().substring(0, 11) + "other"
                + getPermission().substring(11, getPermission().length());
    }

    /**
     * 他プレイヤーをターゲットにしたアイテムコマンドパーミッションノードを返す
     * @return パーミッションノード
     */
    private String getPermItemCMDOther() {
        return getPermission().substring(0, 15) + "other"
                + getPermission().substring(15, getPermission().length());
    }

    /**
     * パーミッションノードがOPユーザ向けパーミッションかどうか判別する
     * @return OPユーザ向けパーミッションかどうか
     */
    public boolean isOpPerm() {
        if (getPermission().contains(".op."))
            return true;
        return false;
    }

    /**
     * playerがパーミッションを所有しているかどうかを確認する
     * コマンドパーミッションの確認する場合はPermission.hasCMDPermission()を利用すること
     * @param player パーミッションを確認するプレイヤー
     * @param permission 確認するパーミッション
     * @param noMessage playerにパーミッションエラーメッセージを送信するかどうか
     * @return playerがパーミッションを所有しているかどうか
     */
    public static Boolean hasPermission(Player player, Permission permission, boolean noMessage) {
        if (permission == null) {
            return true;
        }

        if (!permission.getSettingNodeValue()) {
            return true;
        }

        if (player.hasPermission(permission.getPermission())) {
            return true;
        }

        if (noMessage) {
            return false;
        }

        MessageEnum.noPermission.sendConvertedMessage(player, permission.getPermission());
        return false;
    }

    /**
     * playerがコマンドパーミッションを所有しているかどうかを確認する
     * コマンドの場合、自分がターゲットのコマンド、他プレイヤーがターゲットのコマンド、
     * の2タイプにコマンドが分類されるため、それぞれでパーミッションノードが異なる
     * どちらのタイプなのかは引数targetOtherで指定する
     * @param player パーミッションを確認するプレイヤー
     * @param permission 確認するパーミッション
     * @param targetOther コマンドのターゲットが他プレイヤーかどうか
     * @param noMessage playerにパーミッションエラーメッセージを送信するかどうか
     * @return playerがパーミッションを所有しているかどうか
     */
    public static Boolean hasCMDPermission(Player player, Permission permission, boolean targetOther, boolean noMessage) {
        if (!targetOther) {
            if (!permission.getSettingNodeValue())
                return true;
            if (player.hasPermission(permission.getPermission()))
                return true;
        } else {
            if (!permission.getSettingNodeOtherValue())
                return true;
            if (permission.getPermission().contains("itemcmd")) {
                System.out.println(permission.getPermItemCMDOther());
                if (player.hasPermission(permission.getPermItemCMDOther()))
                    return true;
            } else {
                if (player.hasPermission(permission.getPermBasicCMDOther()))
                    return true;
            }
        }
        if (noMessage)
            return false;

        if (!targetOther) {
            MessageEnum.noPermission.sendConvertedMessage(player, permission.getPermission());
        } else {
            if (permission.getPermission().contains("itemcmd")) {
                MessageEnum.noPermission.sendConvertedMessage(player, permission.getPermItemCMDOther());
            } else
                MessageEnum.noPermission.sendConvertedMessage(player, permission.getPermBasicCMDOther());
        }
        return false;
    }

    /**
     * 全要素のメンバ変数を再取得する
     */
    public static void reload(){
        for (Permission permission : values()) {
            permission.setConfigData();
        }
    }
}
