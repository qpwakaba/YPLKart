package com.github.erozabesu.yplkart.data;

import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import com.github.erozabesu.yplkart.ConfigManager;
import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.object.DisplayKart;
import com.github.erozabesu.yplkart.object.Kart;

/**
 * ディスプレイカート設定を管理するクラス
 * ユーザ側で要素数を変更できる動的なコンフィグを扱うためオブジェクトで管理する
 * 更に、ゲーム内から動的に登録、変更、削除が行われるデータを扱うため
 * 他のクラスとは異なりstaticメソッドを多用している
 * @author erozabesu
 */
public class DisplayKartConfig {

    /** RaceDataオブジェクトを格納しているハッシュマップ */
    private HashMap<String, DisplayKart> displayKartMap = new HashMap<String, DisplayKart>();

    //〓 main 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** このクラスのインスタンス */
    private static DisplayKartConfig instance;

    /**
     * このクラスのインスタンスを返す
     * @return CircuitConfig.classインスタンス
     */
    private static DisplayKartConfig getInstance() {
        return instance;
    }

    /**
     * コンストラクタ
     * コンフィグの読み込みはstatic.reload()メソッドで明示的に行う
     * static.reload()はConfigManager.reload()から実行される
     * ややこしくなるため他のConfigクラスと同様の手順を踏む
     */
    public DisplayKartConfig() {
        instance = this;
    }

    //〓 getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @return DisplayKartオブジェクトを格納しているハッシュマップ */
    public HashMap<String, DisplayKart> getDisplayKartMap() {
        return this.displayKartMap;
    }

    //〓 setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @param displayKartMap DisplayKartオブジェクトを格納しているハッシュマップ */
    public void setDisplayKartMap(HashMap<String, DisplayKart> displayKartMap) {
        this.displayKartMap = displayKartMap;
    }

    //〓 static data edit 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 引数displayKartNameに対応するDisplayKartオブジェクトを返す
     * @param uuid ディスプレイカート名
     * @return DisplayKartオブジェクト
     */
    public static DisplayKart getDisplayKart(String uuid) {
        for (String mapKey : getInstance().getDisplayKartMap().keySet()) {
            if (mapKey.equalsIgnoreCase(uuid)) {
                return getInstance().getDisplayKartMap().get(mapKey);
            }
        }
        return null;
    }

    /**
     * 設定データを再読み込みする
     * 既存のDisplayKartオブジェクトを破棄し、ローカルファイルの設定データを基に
     * 新規にオブジェクトを生成しハッシュマップに格納する
     */
    public static void reload() {
        DisplayKartConfig instance = getInstance();
        instance.getDisplayKartMap().clear();

        for(String configKey : ConfigManager.DISPLAY_KART_CONFIG.getLocalConfig().getKeys(false)) {
            instance.getDisplayKartMap().put(configKey, new DisplayKart(configKey));
        }
    }

    /**
     * 新規のDisplayKartオブジェクトを作成しハッシュマップに格納する
     * また、ローカルコンフィグファイルに設定を保存する
     * @param adress ログの送信先
     * @param uuid ディスプレイカートEntityのUUID
     * @param kart ディスプレイするKartオブジェクト
     * @param location 設置座標
     */
    public static void createDisplayKart(String uuid, Kart kart, Location location) {
        DisplayKart displayKart = new DisplayKart(uuid);
        displayKart.createDisplayKart(uuid, kart, location);

        getInstance().getDisplayKartMap().put(uuid, displayKart);
    }

    /**
     * 引数uuidに対応するDisplayKartオブジェクトをハッシュマップから削除し、
     * 設定データをローカルコンフィグファイルから削除する
     * @param adress ログの送信先
     * @param uuid 削除するDisplayKartオブジェクト名
     */
    public static void deleteDisplayKart(Object adress, String uuid) {
        DisplayKart displayKart = getDisplayKart(uuid);
        if (displayKart != null) {

            //ハッシュマップから削除
            getInstance().getDisplayKartMap().remove(uuid);

            //ローカルファイルから削除
            displayKart.deleteConfiguration();

            MessageEnum.cmdDisplayDelete.sendConvertedMessage(adress, uuid);
        }
    }

    //〓 static util 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * ローカルコンフィグの設定データ上で引数chunkに存在している
     * DisplayKartオブジェクトのEntityを再生成する
     * カスタムクラスEntityはチャンクがアンロードされると同時にデスポーンするため
     * このような処理が必要になる
     * @param chunk DisplayKartオブジェクトEntityを再生成させるチャンク
     */
    public static void respawnKart(Chunk chunk) {
        Iterator<DisplayKart> iterator = getInstance().getDisplayKartMap().values().iterator();
        DisplayKart displayKart = null;

        //読み込まれていないチャンクであれば何もしない
        if (!chunk.isLoaded()) {
            return;
        }

        while (iterator.hasNext()) {
            displayKart = iterator.next();

            //Worldの不一致
            if (!chunk.getWorld().getName().equalsIgnoreCase(displayKart.getWorldName())) {
                continue;
            }
            //Chunkの不一致
            if (!chunk.toString().equalsIgnoreCase(displayKart.getLocation().getChunk().toString())) {
                continue;
            }
            //Kartオブジェクトが存在するかどうか
            Kart kart = KartConfig.getKart(displayKart.getKartObjectKey());
            if (kart == null) {
                //Kartオブジェクトが存在しない＝kart.ymlから削除されているため
                //displaykart.ymlからも削除する
                displayKart.deleteConfiguration();
                iterator.remove();
                continue;
            }
            //既に同名のEntityが居ないかチェック
            boolean flag = true;
            for (Entity e : chunk.getEntities()) {
                if (e.getCustomName() == null) {
                    continue;
                }
                if (ChatColor.stripColor(e.getCustomName())
                        .equalsIgnoreCase(displayKart.getConfigKey())) {
                    flag = false;
                    break;
                }
            }
            //Entityのスポーン
            if (flag) {
                RaceManager.createDisplayMinecart(displayKart.getLocation()
                        , kart, displayKart.getConfigKey());
            }
        }
    }

    /**
     * ローカルコンフィグの設定データ上で引数worldに存在している
     * DisplayKartオブジェクトのEntityを再生成する
     * カスタムクラスEntityはチャンクがアンロードされると同時にデスポーンするため
     * このような処理が必要になる
     * @param world DisplayKartオブジェクトEntityを再生成させるワールド
     */
    public static void respawnKart(World world) {
        for (Chunk chunk : world.getLoadedChunks()) {
            respawnKart(chunk);
        }
    }
}
