package com.github.erozabesu.yplkart;

import java.util.ArrayList;
import java.util.List;

import com.github.erozabesu.yplutillibrary.config.LoaderAbstract;
import com.github.erozabesu.yplutillibrary.config.YamlLoader;

/**
 * 各コンフィグファイルのファイル管理を行うクラス
 * 設定データの取得はこのクラスからではなく
 * Enum、Configサフィックスの付いているクラスからstaticに取得すること
 *
 * Enumクラスは、ユーザ側で要素数を変更できない静的なコンフィグを扱う
 * コンフィグはEnumで管理する
 *
 * Configクラスは、ユーザ側で要素数を変更できる動的なコンフィグを扱う
 * コンフィグはオブジェクトで管理する
 *
 * @author erozabesu
 */
public class ConfigManager {
    private static List<LoaderAbstract> configList = new ArrayList<LoaderAbstract>();

    public static YamlLoader CONFIG;
    public static YamlLoader PERMISSION;
    public static YamlLoader ITEM;
    public static YamlLoader CHARACTER;
    public static YamlLoader KART;
    public static YamlLoader RACEDATA;
    public static YamlLoader DISPLAY;

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

        CONFIG = new YamlLoader("config.yml");
        configList.add(CONFIG);

        PERMISSION = new YamlLoader("permission.yml");
        configList.add(PERMISSION);

        ITEM = new YamlLoader("item.yml");
        configList.add(ITEM);

        CHARACTER = new YamlLoader("character.yml");
        configList.add(CHARACTER);

        KART = new YamlLoader("kart.yml");
        configList.add(KART);

        RACEDATA = new YamlLoader("racedata.yml");
        configList.add(RACEDATA);

        DISPLAY = new YamlLoader("displaykart.yml");
        configList.add(DISPLAY);
    }
}
