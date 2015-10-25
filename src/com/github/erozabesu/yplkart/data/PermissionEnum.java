package com.github.erozabesu.yplkart.data;

import com.github.erozabesu.yplkart.ConfigManager;

/**
 * permission.ymlのパーミッション設定を格納するクラス
 * ユーザ側で要素数を変更できない静的なコンフィグを扱うためenumで管理する
 * @author erozabesu
 */
public enum PermissionEnum {
    ENABLE_PERMISSION$KART_RIDE,
    ENABLE_PERMISSION$KART_DRIFT,
    ENABLE_PERMISSION$CMD_MENU,
    ENABLE_PERMISSION$CMD_ENTRY,
    ENABLE_PERMISSION$CMD_EXIT,
    ENABLE_PERMISSION$CMD_CHARACTER,
    ENABLE_PERMISSION$CMD_CHARACTERRESET,
    ENABLE_PERMISSION$CMD_KART,
    ENABLE_PERMISSION$CMD_LEAVE,
    ENABLE_PERMISSION$CMD_RANKING,
    ENABLE_PERMISSION$CMD_ITEM,
    ENABLE_PERMISSION$CMD_OTHER_MENU,
    ENABLE_PERMISSION$CMD_OTHER_ENTRY,
    ENABLE_PERMISSION$CMD_OTHER_EXIT,
    ENABLE_PERMISSION$CMD_OTHER_CHARACTER,
    ENABLE_PERMISSION$CMD_OTHER_CHARACTERRESET,
    ENABLE_PERMISSION$CMD_OTHER_KART,
    ENABLE_PERMISSION$CMD_OTHER_LEAVE,
    ENABLE_PERMISSION$CMD_OTHER_RANKING,
    ENABLE_PERMISSION$CMD_OTHER_ITEM,

    ENABLE_PERMISSION$USE_ITEM,
    ENABLE_PERMISSION$INTERACT_OBJECT,

    ENABLE_OP_PERMISSION$KART_REMOVE,
    ENABLE_OP_PERMISSION$CMD_CIRCUIT,
    ENABLE_OP_PERMISSION$CMD_DISPLAY,
    ENABLE_OP_PERMISSION$CMD_RELOAD,
    ENABLE_OP_PERMISSION$CMD_ITEMBOXTOOL;

    /** コンフィグキー */
    private String configKey;

    /** コンフィグバリュー */
    private Boolean configValue;

    //〓 Main 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * コンストラクタ
     * enumの静的データを格納する
     * 列挙型の性質上、値の代入は明示的にstatic.reload()メソッドから実行する
     * static.reload()はConfigManager.reload()から実行される
     */
    private PermissionEnum() {
        this.setKey(this.name().toLowerCase().replaceAll("\\$", "\\."));
    }

    private void loadValue() {
        this.setValue(ConfigManager.PERMISSION.getBoolean(this.getKey()));
    }

    //〓 Util 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** バリューを再読み込みする */
    public static void reload() {
        for (PermissionEnum configEnum : PermissionEnum.values()) {
            configEnum.loadValue();
        }
    }

    //〓 Getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @return コンフィグキー */
    public String getKey() {
        return this.configKey;
    }

    /** @return コンフィグバリュー */
    public Boolean getValue(){
        return this.configValue;
    }

    //〓 Setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @param コンフィグキー */
    public void setKey(String configKey) {
        this.configKey = configKey;
    }

    /** @param コンフィグバリュー */
    public void setValue(Boolean configValue){
        this.configValue = configValue;
    }
}
