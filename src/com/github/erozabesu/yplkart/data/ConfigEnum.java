package com.github.erozabesu.yplkart.data;

import java.lang.reflect.Field;
import java.util.List;

import com.github.erozabesu.yplkart.ConfigManager;
import com.github.erozabesu.yplutillibrary.util.ReflectionUtil;

/**
 * プラグインの基本設定を格納するクラス
 * ユーザ側で要素数を変更できない静的なコンフィグを扱うためenumで管理する
 * @author erozabesu
 */
public enum ConfigEnum {
    LANGUAGE,
    ENABLE_THIS_PLUGIN,
    ENABLE_SCOREBOARD,
    DISABLED_WORLDS,

    SETTINGS,
    SETTINGS$START_BLOCK_ID,
    SETTINGS$GOAL_BLOCK_ID,
    SETTINGS$DIRT_BLOCK_ID,
    SETTINGS$ITEM_SLOT,

    ITEM,
    ITEM$TIER1,
    ITEM$TIER2,
    ITEM$TIER3,

    ITEM$DASH_BOARD,
    ITEM$DASH_BOARD$EFFECT_LEVEL,
    ITEM$DASH_BOARD$EFFECT_SECOND,

    ITEM$DETECT_CHECKPOINT_RADIUS,
    ITEM$DETECT_CHECKPOINT_RADIUS$TIER1,
    ITEM$DETECT_CHECKPOINT_RADIUS$TIER2,
    ITEM$DETECT_CHECKPOINT_RADIUS$TIER3;

    /*
     * コンフィグ値の型を保証するため変数として別途宣言する。
     * 他クラスからインスタンスとして参照するためプリミティブ型は利用しない。
     */
    public static String language;

    public static Boolean enable_this_plugin;

    public static Boolean enable_scoreboard;
    public static List<String> disabled_worlds;

    public static String settings$start_block_id;
    public static String settings$goal_block_id;
    public static String settings$dirt_block_id;
    public static Integer settings$item_slot;

    public static Integer item$tier1;
    public static Integer item$tier2;
    public static Integer item$tier3;

    public static Integer item$dash_board$effect_level;
    public static Integer item$dash_board$effect_second;

    public static Integer item$detect_checkpoint_radius$tier1;
    public static Integer item$detect_checkpoint_radius$tier2;
    public static Integer item$detect_checkpoint_radius$tier3;

    /** コンフィグキー */
    private String configKey;

    /** コンフィグバリュー */
    private Object configValue;

    //〓 Main 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * コンストラクタ
     * enumの静的データを格納する
     * 列挙型の性質上、値の代入は明示的にstatic.reload()メソッドから実行する
     * static.reload()はConfigManager.reload()から実行される
     */
    private ConfigEnum() {
        this.setKey(this.name().toLowerCase().replaceAll("\\$", "\\."));
    }

    private void loadValue() {
        Object value = ConfigManager.CONFIG.getLocalConfig().get(this.getKey());
        this.setValue(value);

        // 読み込まれる順序の都合上変数の宣言時に値を代入することは出来ないため、後付で代入を行う
        Field field = ReflectionUtil.getField(ConfigEnum.class, this.getKey().replaceAll("\\.", "\\$"));
        if (field != null) {
            ReflectionUtil.setFieldValue(field, null, value);
        }
    }

    //〓 Util 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** バリューを再読み込みする */
    public static void reload() {
        for (ConfigEnum configEnum : ConfigEnum.values()) {
            configEnum.loadValue();
        }
    }

    //〓 Getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @return コンフィグキー */
    public String getKey() {
        return this.configKey;
    }

    /** @return コンフィグバリュー */
    public Object getValue(){
        return this.configValue;
    }

    //〓 Setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @param コンフィグキー */
    public void setKey(String configKey) {
        this.configKey = configKey;
    }

    /** @param コンフィグバリュー */
    public void setValue(Object configValue){
        this.configValue = configValue;
    }
}
