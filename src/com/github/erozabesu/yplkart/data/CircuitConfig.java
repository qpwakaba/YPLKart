package com.github.erozabesu.yplkart.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.erozabesu.yplkart.ConfigManager;
import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.object.Circuit;
import com.github.erozabesu.yplkart.object.CircuitData;
import com.github.erozabesu.yplkart.object.LapTime;

/**
 * レース設定を管理するクラス
 * ユーザ側で要素数を変更できる動的なコンフィグを扱うためオブジェクトで管理する
 * 更に、ゲーム内から動的に登録、変更、削除が行われるデータを扱うため
 * 他のクラスとは異なりstaticメソッドを多用している
 * @author erozabesu
 */
public class CircuitConfig {

    /** RaceDataオブジェクトを格納しているハッシュマップ */
    private HashMap<String, CircuitData> circuitDataMap = new HashMap<String, CircuitData>();

    //〓 main 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** このクラスのインスタンス */
    private static CircuitConfig instance;

    /**
     * このクラスのインスタンスを返す
     * @return CircuitConfig.classインスタンス
     */
    private static CircuitConfig getInstance() {
        return instance;
    }

    /**
     * コンストラクタ
     * コンフィグの読み込みはstatic.reload()メソッドで明示的に行う
     * static.reload()はConfigManager.reload()から実行される
     * ややこしくなるため他のConfigクラスと同様の手順を踏む
     */
    public CircuitConfig() {
        instance = this;
    }

    //〓 getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @return CircuitDataオブジェクトを格納しているハッシュマップ */
    public HashMap<String, CircuitData> getCircuitDataMap() {
        return this.circuitDataMap;
    }

    //〓 setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @param circuitDataMap CircuitDataオブジェクトを格納しているハッシュマップ */
    public void setCircuitDataMap(HashMap<String, CircuitData> circuitDataMap) {
        this.circuitDataMap = circuitDataMap;
    }

    //〓 static data edit 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 引数circuitDataNameに対応するCircuitDataオブジェクトを返す
     * @param circuitDataName サーキット名
     * @return CircuitDataオブジェクト
     */
    public static CircuitData getCircuitData(String circuitDataName) {
        for (String mapKey : getInstance().getCircuitDataMap().keySet()) {
            if (mapKey.equalsIgnoreCase(circuitDataName)) {
                return getInstance().getCircuitDataMap().get(mapKey);
            }
        }
        return null;
    }

    /**
     * 設定データを再読み込みする
     * 既存のCircuitDataオブジェクトを破棄し、ローカルファイルの設定データを基に
     * 新規にオブジェクトを生成しハッシュマップに格納する
     */
    public static void reload() {
        CircuitConfig instance = getInstance();
        instance.circuitDataMap.clear();

        for(String configKey : ConfigManager.RACEDATA_CONFIG.getLocalConfig().getKeys(false)) {
            instance.circuitDataMap.put(configKey, new CircuitData(configKey));
        }
    }

    /**
     * 新規のCircuitDataオブジェクトを作成しハッシュマップに格納する
     * また、ローカルコンフィグファイルに設定を保存する
     * @param adress ログの送信先
     * @param location レース開始座標
     * @param circuitDataName サーキット名
     */
    public static void createCircuit(Object adress, Location location, String circuitDataName) {
        CircuitData circuitData = getCircuitData(circuitDataName);
        if (circuitData == null) {
            circuitData = new CircuitData(circuitDataName);
            circuitData.init(location);
            circuitData.saveConfiguration();

            getInstance().getCircuitDataMap().put(circuitDataName, circuitData);

            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.cmdCircuitCreate.sendConvertedMessage(adress, circuit);
        } else {
            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.cmdCircuitAlreadyExist.sendConvertedMessage(adress, circuit);
        }
    }

    /**
     * 引数circuitDataに対応するCircuitDataオブジェクトをハッシュマップから削除し、
     * 設定データをローカルコンフィグファイルから削除する
     * @param adress ログの送信先
     * @param circuitDataName 削除するCircuitDataオブジェクト名
     */
    public static void deleteCircuit(Object adress, String circuitDataName) {
        CircuitData circuitData = getCircuitData(circuitDataName);
        if (circuitData != null) {

            //ハッシュマップから削除
            getInstance().getCircuitDataMap().remove(circuitDataName);

            //ローカルファイルから削除
            circuitData.deleteConfiguration();

            //設置済みのチェックポイントEntityを削除
            for (World world : Bukkit.getWorlds()) {
                for (Entity entity : world.getEntities()) {
                    if (RaceManager.isCustomWitherSkull(entity, circuitDataName))
                        entity.remove();
                }
            }

            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.cmdCircuitDelete.sendConvertedMessage(adress, circuit);
        } else {
            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.invalidCircuit.sendConvertedMessage(adress, circuit);
        }
    }

    /**
     * 引数oldNameをキーに持つCircuitDataオブジェクトのキーをnewNameに変更する
     * ハッシュマップ、ローカルファイル、設置済みのチェックポイント全てのキーを置換する
     * @param adress ログの送信先
     * @param oldName 変更前のキー
     * @param newName 変更後のキー
     */
    public static void renameCircuit(Object adress, String oldName, String newName) {
        CircuitData oldCircuitData = getCircuitData(oldName);
        CircuitData newCircuitData = getCircuitData(newName);

        //名称を変更するCircuitDataObjectが存在しない
        if (oldCircuitData == null) {
            Circuit circuit = new Circuit();
            circuit.setCircuitName(oldName);
            MessageEnum.invalidCircuit.sendConvertedMessage(adress, circuit);

        //新たな名称のCircuitDataObjectが既に存在している
        } else if (newCircuitData != null) {
            Circuit circuit = new Circuit();
            circuit.setCircuitName(newName);
            MessageEnum.cmdCircuitAlreadyExist.sendConvertedMessage(adress, circuit);

        } else {

            //既存のオブジェクトのメンバ変数を引き継いだ新たなオブジェクトを生成
            newCircuitData = oldCircuitData.cloneCircuitData();

            //新たなキーに名称を変更
            newCircuitData.setCircuitDataName(newName);

            //ローカルファイルへ保存
            newCircuitData.saveConfiguration();

            //ハッシュマップに追加
            getInstance().getCircuitDataMap().put(newName, newCircuitData);

            //古いCircuitDataオブジェクトをハッシュマップから削除
            getInstance().getCircuitDataMap().remove(oldName);

            //古いCircuitDataオブジェクトをローカルファイルから削除
            oldCircuitData.deleteConfiguration();

            //設置済みのチェックポイントの固有名をnewNameに変更する
            for (World world : Bukkit.getWorlds()) {
                for (Entity entity : world.getEntities()) {
                    if (RaceManager.isCustomWitherSkull(entity, oldName))
                        entity.setCustomName(entity.getCustomName().replace(oldName, newName));
                }
            }

            Circuit circuit = new Circuit();
            circuit.setCircuitName(newName);
            MessageEnum.cmdCircuitRename.sendConvertedMessage(adress, circuit);
        }
    }

    /**
     * 引数playerの新しい走行記録を保存する
     * 古い記録が存在する場合、速い(数値が低い)方の記録がマージされる
     * @param player 走行時間を記録するプレイヤー
     * @param circuitDataName CircuitDataオブジェクトキー
     * @param lapTime 走行時間
     * @param isKartRace カートレースかどうか
     */
    public static void addRaceLapTime(Player player, String circuitDataName
            , double lapTime, boolean isKartRace) {

        CircuitData circuitData = getCircuitData(circuitDataName);

        if (circuitData == null) {
            return;
        }

        UUID uuid = player.getUniqueId();
        LapTime newLapTimeObject = new LapTime(circuitData.getNumberOfLaps(), uuid, lapTime);
        LapTime oldLapTimeObject = isKartRace
                ? circuitData.getKartLapTime(uuid) : circuitData.getRunLapTime(uuid);

        //プレイヤーの記録が既に存在している
        if (oldLapTimeObject != null) {

            //新旧の記録を比較し、新しい記録の方が早い場合記録を上書き保存する
            if (lapTime < oldLapTimeObject.getLapTime()) {
                //古い記録を削除
                if (isKartRace) {
                    circuitData.removeKartLapTime(uuid);
                } else {
                    circuitData.removeRunLapTime(uuid);
                }

                //テキストメッセージの送信
                Number[] messagePartsLapTime = new Number[] { oldLapTimeObject.getLapTime(), lapTime };
                Circuit circuit = new Circuit();
                circuit.setCircuitName(circuitDataName);
                MessageEnum.raceHighScore.sendConvertedMessage(player, circuit, messagePartsLapTime);
            } else {
                //記録の更新がない場合は何もしない
                return;
            }
        }

        //新しい記録を格納
        if (isKartRace) {
            circuitData.addKartLapTimeList(newLapTimeObject);
        } else {
            circuitData.addRunLapTimeList(newLapTimeObject);
        }

        //ローカルファイルへ保存
        circuitData.saveConfiguration();
    }

    //〓 static config edit 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 引数circuitDataNameをキーに持つCircuitDataオブジェクトのレース開始座標を
     * 引数locationの座標に設定する
     * @param adress ログの送信先
     * @param circuitDataName CircuitDataオブジェクトキー
     * @param location 設定座標
     */
    public static void setStartPosition(Object adress, String circuitDataName, Location location) {
        CircuitData circuitData = CircuitConfig.getCircuitData(circuitDataName);

        if (circuitData == null) {
            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.invalidCircuit.sendConvertedMessage(adress, circuit);
        } else {
            circuitData.setStartLocation(location);
            circuitData.saveConfiguration();

            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.cmdCircuitSetPosition.sendConvertedMessage(adress, circuit);
        }
    }

    /**
     * 引数circuitDataNameをキーに持つCircuitDataオブジェクトの周回数を設定する
     * @param adress ログの送信先
     * @param circuitDataName CircuitDataオブジェクトキー
     * @param newValue 周回数
     */
    public static void setNumberOfLaps(Object adress, String circuitDataName, int newValue) {
        CircuitData circuitData = CircuitConfig.getCircuitData(circuitDataName);
        if (circuitData == null) {
            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.invalidCircuit.sendConvertedMessage(adress, circuit);
        } else {
            circuitData.setNumberOfLaps(newValue);
            circuitData.saveConfiguration();

            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.cmdCircuitSetLap.sendConvertedMessage(adress, circuit, newValue);
        }
    }

    /**
     * 引数circuitDataNameをキーに持つCircuitDataオブジェクトの
     * 最小プレイ人数を設定する
     * @param adress ログの送信先
     * @param circuitDataName CircuitDataオブジェクトキー
     * @param newValue 最小プレイ人数
     */
    public static void setMinPlayer(Object adress, String circuitDataName, int newValue) {
        CircuitData circuitData = CircuitConfig.getCircuitData(circuitDataName);
        if (circuitData == null) {
            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.invalidCircuit.sendConvertedMessage(adress, circuit);

        //最大プレイ人数を越える値は設定できない
        } else if (circuitData.getMaxPlayer() < newValue) {
            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.cmdCircuitOutOfMaxPlayer.sendConvertedMessage(adress, circuit, circuitData.getMaxPlayer());
        } else {
            circuitData.setMinPlayer(newValue);
            circuitData.saveConfiguration();

            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.cmdCircuitSetMinPlayer.sendConvertedMessage(adress, circuit, newValue);
        }
    }

    /**
     * 引数circuitDataNameをキーに持つCircuitDataオブジェクトの
     * 最大プレイ人数を設定する
     * @param adress ログの送信先
     * @param circuitDataName CircuitDataオブジェクトキー
     * @param newValue 最大プレイ人数
     */
    public static void setMaxPlayer(Object adress, String circuitDataName, int newValue) {
        CircuitData circuitData = CircuitConfig.getCircuitData(circuitDataName);
        if (circuitData == null) {
            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.invalidCircuit.sendConvertedMessage(adress, circuit);

        //最小プレイ人数を下回る数値は設定できない
        } else if (newValue < circuitData.getMinPlayer()) {
            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.cmdCircuitOutOfMinPlayer.sendConvertedMessage(adress, circuit, circuitData.getMinPlayer());
        } else {
            circuitData.setMaxPlayer(newValue);
            circuitData.saveConfiguration();

            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.cmdCircuitSetMaxPlayer.sendConvertedMessage(adress, circuit, newValue);
        }
    }

    /**
     * 引数circuitDataNameをキーに持つCircuitDataオブジェクトの
     * マッチング猶予時間を設定する
     * @param adress ログの送信先
     * @param circuitDataName CircuitDataオブジェクトキー
     * @param newValue マッチング猶予時間
     */
    public static void setMatchingTime(Object adress, String circuitDataName, int newValue) {
        CircuitData circuitData = CircuitConfig.getCircuitData(circuitDataName);
        if (circuitData == null) {
            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.invalidCircuit.sendConvertedMessage(adress, circuit);
        } else {
            circuitData.setMatchingTime(newValue);
            circuitData.saveConfiguration();

            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.cmdCircuitSetMatchingTime.sendConvertedMessage(adress, circuit, newValue);
        }
    }

    /**
     * 引数circuitDataNameをキーに持つCircuitDataオブジェクトの
     * メニュー選択猶予時間を設定する
     * @param adress ログの送信先
     * @param circuitDataName CircuitDataオブジェクトキー
     * @param newValue メニュー選択猶予時間
     */
    public static void setMenuTime(Object adress, String circuitDataName, int newValue) {
        CircuitData circuitData = CircuitConfig.getCircuitData(circuitDataName);
        if (circuitData == null) {
            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.invalidCircuit.sendConvertedMessage(adress, circuit);
        } else {
            circuitData.setMenuTime(newValue);
            circuitData.saveConfiguration();

            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.cmdCircuitSetMenuTime.sendConvertedMessage(adress, circuit, newValue);
        }
    }

    /**
     * 引数circuitDataNameをキーに持つCircuitDataオブジェクトの
     * レース終了までの制限時間を設定する
     * @param adress ログの送信先
     * @param circuitDataName CircuitDataオブジェクトキー
     * @param newValue レース終了までの制限時間
     */
    public static void setLimitTime(Player adress, String circuitDataName, int newValue) {
        CircuitData circuitData = CircuitConfig.getCircuitData(circuitDataName);
        if (circuitData == null) {
            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.invalidCircuit.sendConvertedMessage(adress, circuit);
        } else {
            circuitData.setLimitTime(newValue);
            circuitData.saveConfiguration();

            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.cmdCircuitSetLimitTime.sendConvertedMessage(adress, circuit, newValue);
        }
    }

    /**
     * 引数circuitDataNameをキーに持つCircuitDataオブジェクトの
     * ゴール時のサーバー全体通知をするかどうかを設定する
     * @param adress ログの送信先
     * @param circuitDataName CircuitDataオブジェクトキー
     * @param newValue ゴール時のサーバー全体通知をするかどうか
     */
    public static void setBroadcastGoalMessage(Player adress, String circuitDataName, boolean newValue) {
        CircuitData circuitData = CircuitConfig.getCircuitData(circuitDataName);
        if (circuitData == null) {
            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.invalidCircuit.sendConvertedMessage(adress, circuit);
        } else {
            circuitData.setBroadcastGoalMessage(newValue);
            circuitData.saveConfiguration();

            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.cmdCircuitSetBroadcastGoalMessage.sendConvertedMessage(adress, circuit, newValue);
        }
    }

    //〓 static util 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @return CircuitDataオブジェクトキーのList */
    public static List<String> getCircuitList() {
        List<String> list = new ArrayList<String>();
        for (String key : getInstance().getCircuitDataMap().keySet()) {
            list.add(key);
        }
        return list;
    }

    /** @return CircuitDataオブジェクトキーのListString */
    public static String getCircuitListString() {
        String list = null;
        for (String key : getInstance().getCircuitDataMap().keySet()) {
            if (list == null) {
                list = key;
            } else {
                list += ", " + key;
            }
        }
        return list;
    }

    /**
     * 引数circuitDataNameをキーに持つCircuitDataオブジェクトの
     * 走行記録をランキング形式で引数adressへ送信する
     * @param adress ログの送信先
     * @param circuitDataName CircuitDataオブジェクトキー
     */
    public static void sendRanking(Object adress, String circuitDataName) {
        CircuitData circuitData = getCircuitData(circuitDataName);
        if (circuitData == null) {
            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.invalidCircuit.sendConvertedMessage(adress, circuit);
            return;
        }

        circuitData.sendRanking(adress);
    }
}
