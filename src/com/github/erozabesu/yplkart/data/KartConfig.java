package com.github.erozabesu.yplkart.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;

import com.github.erozabesu.yplkart.ConfigManager;
import com.github.erozabesu.yplkart.object.Kart;

/**
 * カート設定を管理するクラス
 * ユーザ側で要素数を変更できる動的なコンフィグを扱うためオブジェクトで管理する
 * @author erozabesu
 */
public class KartConfig{

    /** Kartオブジェクトを格納しているハッシュマップ */
    private HashMap<String, Kart> kartObject = new HashMap<String, Kart>();

    //〓 main 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** このクラスのインスタンス */
    private static KartConfig instance;

    /**
     * このクラスのインスタンスを返す
     * @return CharacterConfig.classインスタンス
     */
    private static KartConfig getInstance() {
        return instance;
    }

    /**
     * コンストラクタ
     * ややこしくなるため他のConfigクラスと同様の手順で読み込みを行う
     * コンフィグの読み込みはstatic.reload()メソッドで明示的に行う
     * static.reload()はConfigManager.reload()から実行される
     */
    public KartConfig() {
        instance = this;
    }

    //〓 static file edit 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 設定データを再読み込みする
     * 既存のKartオブジェクトを破棄し、新規に生成しハッシュマップに格納する
     */
    public static void reload() {
        KartConfig characterConfig = getInstance();
        characterConfig.kartObject.clear();

        for(String key : ConfigManager.KART_CONFIG.getLocalConfig().getKeys(false)) {
            characterConfig.kartObject.put(key, new Kart(key));
        }
    }

    //〓 static util 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 全KartオブジェクトをHashMapで返す
     * @return HashMap型の全Kartオブジェクト
     */
    public static HashMap<String, Kart> getKartMap() {
        return getInstance().kartObject;
    }

    /**
     * 全KartオブジェクトをListで返す
     * @return List型の全Kartオブジェクト
     */
    public static ArrayList<Kart> getKartList() {
        return new ArrayList<Kart>(getKartMap().values());
    }

    /** カート名の一覧を返す */
    public static String getKartListString() {
        String kartListString = null;
        for (String kartName : getKartMap().keySet()) {
            if (kartListString == null)
                kartListString = kartName;
            else
                kartListString += ", " + kartName;
        }
        return kartListString;
    }

    /**
     * 文字列と一致するカート名のKartオブジェクトを返す
     * @param value カート名
     * @return Kartオブジェクト
     */
    public static Kart getKart(String value) {
        for (String mapKey : getKartMap().keySet()) {
            if (mapKey.equalsIgnoreCase(value)) {
                return getKartMap().get(mapKey);
            }
        }
        return null;
    }

    /**
     * entityのカスタムネームと一致するカート名のKartオブジェクトを返す
     * @param entity カートエンティティ
     * @return Kartオブジェクト
     */
    public static Kart getKartFromEntity(Entity entity) {
        if (entity == null) {
            return null;
        }
        if (entity.getCustomName() == null) {
            return null;
        }

        for (Kart kart : KartConfig.getKartList()) {
            if (kart.getKartName().equalsIgnoreCase(ChatColor.stripColor(entity.getCustomName()))) {
                return kart;
            }
        }
        return null;
    }

    /**
     * 全カートの中からランダムに抽出した1カートを返す
     * @return Kartオブジェクト
     */
    public static Kart getRandomKart() {
        int ram = getKartList().size();
        ram = new Random().nextInt(ram);

        return getKartList().get(ram);
    }

    /** @return アイテムキラー用のカートオブジェクト */
    public static Kart getKillerKart() {
        return new Kart("Killer"
                , ItemEnum.KILLER.getDisplayBlockMaterial(), ItemEnum.KILLER.getDisplayBlockMaterialData());
    }
}
