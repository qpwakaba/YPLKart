package com.github.erozabesu.yplkart;

import java.util.ArrayList;
import java.util.List;

import com.github.erozabesu.yplkart.data.ConfigEnum;
import com.github.erozabesu.yplutillibrary.config.LoaderAbstract;
import com.github.erozabesu.yplutillibrary.config.RawTextLoader;
import com.github.erozabesu.yplutillibrary.config.YamlLoader;

/**
 * 各言語ファイルのファイル管理を行うクラス
 *
 * @author erozabesu
 */
public class LanguageManager {
    private static List<LoaderAbstract> configList = new ArrayList<LoaderAbstract>();

    public static YamlLoader CONFIG_COMMENT;
    public static RawTextLoader CONFIG_HEADER;

    public static YamlLoader MESSAGE;
    public static RawTextLoader MESSAGE_HEADER;

    public static YamlLoader PERMISSION_COMMENT;
    public static RawTextLoader PERMISSION_HEADER;

    public static RawTextLoader CHARACTER_HEADER;
    public static RawTextLoader ITEM_HEADER;
    public static RawTextLoader KART_HEADER;

    //〓 Util 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** 全設定データをローカルコンフィグファイルに保存する */
    public static void saveAllFile() {
        for (LoaderAbstract loader : configList) {
            loader.saveLocal();
        }
    }

    /** ローカルの全コンフィグファイルからYamlConfigurationを再読み込みする */
    public static void reloadAllFile() {
        saveAllFile();

        CONFIG_COMMENT = new YamlLoader("config_comment.yml", ConfigEnum.language);
        configList.add(CONFIG_COMMENT);

        CONFIG_HEADER = new RawTextLoader("config_header.yml", ConfigEnum.language);
        configList.add(CONFIG_HEADER);

        MESSAGE = new YamlLoader("message.yml", ConfigEnum.language);
        configList.add(MESSAGE);

        MESSAGE_HEADER = new RawTextLoader("message_header.yml", ConfigEnum.language);
        configList.add(MESSAGE_HEADER);

        PERMISSION_COMMENT = new YamlLoader("permission_comment.yml", ConfigEnum.language);
        configList.add(PERMISSION_COMMENT);

        PERMISSION_HEADER = new RawTextLoader("permission_header.yml", ConfigEnum.language);
        configList.add(PERMISSION_HEADER);

        CHARACTER_HEADER = new RawTextLoader("character_header.yml", ConfigEnum.language);
        configList.add(CHARACTER_HEADER);

        ITEM_HEADER = new RawTextLoader("item_header.yml", ConfigEnum.language);
        configList.add(ITEM_HEADER);

        KART_HEADER = new RawTextLoader("kart_header.yml", ConfigEnum.language);
        configList.add(KART_HEADER);

        incertAllComments();
    }

    /** 全ローカルファイルにコメント、ヘッダーを挿入し保存する */
    public static void incertAllComments() {
        ConfigManager.CONFIG.incertComment(CONFIG_COMMENT.getLocalConfig());
        ConfigManager.CONFIG.incertHeader(CONFIG_HEADER.getLocalRawText());

        LanguageManager.MESSAGE.incertHeader(MESSAGE_HEADER.getLocalRawText());

        ConfigManager.PERMISSION.incertComment(PERMISSION_COMMENT.getLocalConfig());
        ConfigManager.PERMISSION.incertHeader(PERMISSION_HEADER.getLocalRawText());

        ConfigManager.CHARACTER.incertHeader(CHARACTER_HEADER.getLocalRawText());
        ConfigManager.ITEM.incertHeader(ITEM_HEADER.getLocalRawText());
        ConfigManager.KART.incertHeader(KART_HEADER.getLocalRawText());

        ConfigManager.saveAllFile();
    }
}