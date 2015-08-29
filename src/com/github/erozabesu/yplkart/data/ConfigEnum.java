package com.github.erozabesu.yplkart.data;

import com.github.erozabesu.yplkart.ConfigManager;

/**
 * プラグインの基本設定を格納するクラス
 * ユーザ側で要素数を変更できない静的なコンフィグを扱うためenumで管理する
 * @author erozabesu
 */
public enum ConfigEnum {
    ENABLE_THIS_PLUGIN("enable_this_plugin"),

    PERMISSION_KART_RIDE("enable_permission.kart_ride"),
    PERMISSION_KART_DRIFT("enable_permission.kart_drift"),
    PERMISSION_CMD_MENU("enable_permission.cmd_menu"),
    PERMISSION_CMD_ENTRY("enable_permission.cmd_entry"),
    PERMISSION_CMD_FORCEENTRY("enable_permission.cmd_forceentry"),
    PERMISSION_CMD_EXIT("enable_permission.cmd_exit"),
    PERMISSION_CMD_CHARACTER("enable_permission.cmd_character"),
    PERMISSION_CMD_CHARACTERRESET("enable_permission.cmd_characterreset"),
    PERMISSION_CMD_KART("enable_permission.cmd_kart"),
    PERMISSION_CMD_LEAVE("enable_permission.cmd_leave"),
    PERMISSION_CMD_RANKING("enable_permission.cmd_ranking"),
    PERMISSION_CMD_ITEM("enable_permission.cmd_item"),
    PERMISSION_CMD_OTHER_MENU("enable_permission.cmd_other_menu"),
    PERMISSION_CMD_OTHER_ENTRY("enable_permission.cmd_other_entry"),
    PERMISSION_CMD_OTHER_FORCEENTRY("enable_permission.cmd_other_forceentry"),
    PERMISSION_CMD_OTHER_EXIT("enable_permission.cmd_other_exit"),
    PERMISSION_CMD_OTHER_CHARACTER("enable_permission.cmd_other_character"),
    PERMISSION_CMD_OTHER_CHARACTERRESET("enable_permission.cmd_other_characterreset"),
    PERMISSION_CMD_OTHER_KART("enable_permission.cmd_other_kart"),
    PERMISSION_CMD_OTHER_LEAVE("enable_permission.cmd_other_leave"),
    PERMISSION_CMD_OTHER_RANKING("enable_permission.cmd_other_ranking"),
    PERMISSION_CMD_OTHER_ITEM("enable_permission.cmd_other_item"),

    PERMISSION_USE_ITEM("enable_permission.use_item"),
    PERMISSION_INTERACT_OBJECT("enable_permission.interact_object"),

    OP_PERMISSION_KART_REMOVE("enable_op_permission.kart_remove"),
    OP_PERMISSION_CMD_CIRCUIT("enable_op_permission.cmd_circuit"),
    OP_PERMISSION_CMD_DISPLAY("enable_op_permission.cmd_display"),
    OP_PERMISSION_CMD_RELOAD("enable_op_permission.cmd_reload"),
    OP_PERMISSION_CMD_ITEMBOXTOOL("enable_op_permission.cmd_itemboxtool"),

    ENABLE_SCOREBOARD("enable_scoreboard"),
    DISABLED_WORLDS("disabled_worlds"),
    START_BLOCK_ID("settings.start_block_id"),
    GOAL_BLOCK_ID("settings.goal_block_id"),
    DIRT_BLOCK_ID("settings.dirt_block_id"),
    ITEM_SLOT("settings.item_slot"),

    ITEM_TIER1("item.tier1"),
    ITEM_TIER2("item.tier2"),
    ITEM_TIER3("item.tier3"),
    ITEM_DASH_BOARD_EFFECT_LEVEL("item.dash_board.effect_level"),
    ITEM_DASH_BOARD_EFFECT_SECOND("item.dash_board.effect_second"),

    ITEM_DETECT_CHECKPOINT_RADIUS_TIER1("item.detect_checkpoint_radius.tier1"),
    ITEM_DETECT_CHECKPOINT_RADIUS_TIER2("item.detect_checkpoint_radius.tier2"),
    ITEM_DETECT_CHECKPOINT_RADIUS_TIER3("item.detect_checkpoint_radius.tier3");

    /** コンフィグキー */
    private String configKey;

    /** コンフィグバリュー */
    private Object configValue;

    //〓 main 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * コンストラクタ
     * enumの静的データを格納する
     * 動的データ（コンフィグ）の読み込みはstatic.reload()メソッドで明示的に行う
     * static.reload()はConfigManager.reload()から実行される
     * @param configKey コンフィグキー
     */
    private ConfigEnum(String configKey) {
        this.configKey = configKey;
    }

    /** バリューを再読み込みする */
    public static void reload() {
        for (ConfigEnum configEnum : ConfigEnum.values()) {
            configEnum.setValue(ConfigManager.CONFIG_ENUM.getValue(configEnum.getKey()));
        }
    }

    //〓 getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @return コンフィグキー */
    public String getKey() {
        return this.configKey;
    }

    /** @return コンフィグバリュー */
    public <T> Object getValue(){
        return this.configValue;
    }

    //〓 getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @param コンフィグキー */
    public void getKey(String configKey) {
        this.configKey = configKey;
    }

    /** @param コンフィグバリュー */
    public void setValue(Object configValue){
        this.configValue = configValue;
    }
}
