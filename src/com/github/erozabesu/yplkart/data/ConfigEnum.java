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
    HEADER("\n"
            + "------------------------- 概要 -------------------------\n"
            + "\n"
            + "プラグインの汎用的な機能の設定を変更できるコンフィグファイルです。\n"
            + "\n"
            + "!\n"
            + "------------------------- 汎用設定 -------------------------"),

    ENABLE_THIS_PLUGIN("当プラグインの機能を有効にします。"),
    ENABLE_SCOREBOARD("!\nスコアボードを有効にします。"),
    DISABLED_WORLDS("!\n当プラグインが動作しないワールド名を指定します。"),

    SETTINGS("!\nレース設定"),
    SETTINGS$START_BLOCK_ID("!\nスタートブロックのIDを変更します。"),
    SETTINGS$GOAL_BLOCK_ID("!\nゴールブロックのIDを変更します。"),
    SETTINGS$DIRT_BLOCK_ID("!\nダートブロックのIDを変更します。"),
    SETTINGS$ITEM_SLOT("!\nレース中使用できるアイテムスロットの数を変更します。"),

    ITEM("!\nアイテム設定"),
    ITEM$TIER1("!\ntierは、アイテムボックスに接触した際に順位に応じたアイテムを付与するための設定です。"
            + "\nアイテム毎にtierが設定されており、tier1～tier4の4つの階級があります。"
            + "\n階級が高いほど効果の強いアイテムが多くなります。"
            + "!\n"
            + "\nプレイヤーの上位「0％～tier1％」のプレイヤーには階級1のアイテムのみ与えられます。"
            + "\nデフォルトでは、上位0％～30％のプレイヤーに設定されています。"),
    ITEM$TIER2("!\nプレイヤーの上位「tier1％～tier2」％のプレイヤーには階級1～階級2のアイテムのみ与えられます。"
            + "\nデフォルトでは、上位30％～60％のプレイヤーに設定されています。"),
    ITEM$TIER3("!\nプレイヤーの上位「tier2％～tier3％」のプレイヤーには階級2～階級3のアイテムのみ与えられます。"
            + "\nデフォルトでは、上位60％～80％のプレイヤーに設定されています。"
            + "\n"
            + "\n残りの、上位tier3％～100％のプレイヤーには階級3～階級4のアイテムのみ与えられます。"
            + "\nデフォルトでは、上位80％～100％のプレイヤーに設定されています。"),

    ITEM$DASH_BOARD("!\nダッシュボード設定"),
    ITEM$DASH_BOARD$EFFECT_LEVEL("!\nスピードポーションエフェクトのLVを変更します。"),
    ITEM$DASH_BOARD$EFFECT_SECOND("!\nスピードポーションエフェクトの秒数を変更します。"),

    ITEM$DETECT_CHECKPOINT_RADIUS("!\nチェックポイントの検出範囲設定。"),
    ITEM$DETECT_CHECKPOINT_RADIUS$TIER1("!\nチェックポイントツールで設置したチェックポイントとの距離が、この項目で設定した数値(ブロック数)以内に差し掛かった場合、"
                                    + "\nチェックポイントを通過したと判定されます。"
                                    + "\n100を超える数値は実際の処理では100として扱われます。"),
    ITEM$DETECT_CHECKPOINT_RADIUS$TIER2("!\nチェックポイントツールTier2で設置したチェックポイントとの距離が、この項目で設定した数値(ブロック数)以内に差し掛かった場合、"
                                    + "\nチェックポイントを通過したと判定されます。"
                                    + "\n100を超える数値は実際の処理では100として扱われます。"),
    ITEM$DETECT_CHECKPOINT_RADIUS$TIER3("!\nチェックポイントツールTier3で設置したチェックポイントとの距離が、この項目で設定した数値(ブロック数)以内に差し掛かった場合、"
                                    + "\nチェックポイントを通過したと判定されます。"
                                    + "\n100を超える数値は実際の処理では100として扱われます。");
    /*
     * コンフィグ値の型を保証するため変数として別途宣言する。
     * 他クラスからインスタンスとして参照するためプリミティブ型は利用しない。
     */
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

    /** コンフィグキーに付加するコメント文 */
    private String configComment;

    /** コンフィグバリュー */
    private Object configValue;

    //〓 Main 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * コンストラクタ
     * enumの静的データを格納する
     * 列挙型の性質上、値の代入は明示的にstatic.reload()メソッドから実行する
     * static.reload()はConfigManager.reload()から実行される
     * @param configComment コンフィグキーに付加するコメント文
     */
    private ConfigEnum(String configComment) {
        this.setKey(this.name().toLowerCase().replaceAll("\\$", "\\."));
        this.setConfigComment(configComment);

        if (this.name().equalsIgnoreCase("HEADER")) {
            if (0 < configComment.length()) {
                ConfigManager.CONFIG_ENUM.getLocalConfig().setAltHeader(configComment);
            }
        } else {
            if (0 < configComment.length()) {
                ConfigManager.CONFIG_ENUM.getLocalConfig().setComment(this.getKey(), configComment);
            }
        }
    }

    private void loadValue() {
        Object value = ConfigManager.CONFIG_ENUM.getValue(this.getKey());
        if (value != null) {
            this.setValue(value);

            // 読み込まれる順序の都合上変数の宣言時に値を代入することは出来ないため、後付で代入を行う
            Field field = ReflectionUtil.getField(ConfigEnum.class, this.getKey().replaceAll("\\.", "\\$"));
            if (field != null) {
                ReflectionUtil.setFieldValue(field, null, value);
            }
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

    /** @return コンフィグキーに付加するコメント文 */
    public String getConfigComment() {
        return configComment;
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

    /** @param configComment コンフィグキーに付加するコメント文 */
    public void setConfigComment(String configComment) {
        this.configComment = configComment;
    }

    /** @param コンフィグバリュー */
    public void setValue(Object configValue){
        this.configValue = configValue;
    }
}
