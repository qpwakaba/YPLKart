package com.github.erozabesu.yplkart.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.erozabesu.yplkart.ConfigManager;
import com.github.erozabesu.yplkart.object.Circuit;
import com.github.erozabesu.yplkart.object.CircuitData;
import com.github.erozabesu.yplkart.object.LapTime;
import com.github.erozabesu.yplkart.object.RaceType;
import com.github.erozabesu.yplkart.utils.CheckPointUtil;

/**
 * レース設定を管理するクラス
 * ユーザ側で要素数を変更できる動的なコンフィグを扱うためオブジェクトで管理する
 * 更に、ゲーム内から動的に登録、変更、削除が行われるデータを扱うため
 * 他のクラスとは異なりstaticメソッドを多用している
 * @author erozabesu
 */
public class CircuitConfig {

    /** RaceDataオブジェクトを格納しているハッシュマップ */
    private static HashMap<String, CircuitData> circuitDataMap = new HashMap<String, CircuitData>();

    //〓 main 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /*
     * コンストラクタは使用禁止
     * コンフィグの読み込みはstatic.reload()メソッドで明示的に行う
     * static.reload()はConfigManager.reload()から実行される
     */
    /*public CircuitConfig() {
    }*/

    //〓 Data Edit 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 設定データを再読み込みする
     * 既存のCircuitDataオブジェクトを破棄し、ローカルファイルの設定データを基に
     * 新規にオブジェクトを生成しハッシュマップに格納する
     */
    public static void reload() {
        clearCircuitData();

        for(String configKey : ConfigManager.RACEDATA_CONFIG.getLocalConfig().getKeys(false)) {
            putCircuitData(configKey, new CircuitData(configKey));
        }
    }

    /**
     * 新規のCircuitDataオブジェクトを作成しハッシュマップに格納する
     * また、ローカルコンフィグファイルに設定を保存する
     * @param address ログの送信先
     * @param location レース開始座標
     * @param circuitDataName サーキット名
     */
    public static void createCircuit(CommandSender address, String circuitDataName, Location location) {
        CircuitData circuitData = getCircuitData(circuitDataName);
        if (circuitData == null) {
            circuitData = new CircuitData(circuitDataName, location);
            circuitData.saveConfiguration();

            putCircuitData(circuitDataName, circuitData);

            //MessageEnumのメンバ変数を更新 : issue #154
            MessageEnum.reload();

            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.cmdCircuitCreate.sendConvertedMessage(address, circuit);
        } else {
            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.cmdCircuitAlreadyExist.sendConvertedMessage(address, circuit);
        }
    }

    /**
     * 引数circuitDataに対応するCircuitDataオブジェクトをハッシュマップから削除し、
     * 設定データをローカルコンフィグファイルから削除する
     * @param address ログの送信先
     * @param circuitDataName 削除するCircuitDataオブジェクト名
     */
    public static void deleteCircuit(CommandSender address, String circuitDataName) {
        CircuitData circuitData = getCircuitData(circuitDataName);
        if (circuitData != null) {

            //ハッシュマップから削除
            removeCircuitData(circuitDataName);

            //ローカルファイルから削除
            circuitData.deleteConfiguration();

            //MessageEnumのメンバ変数を更新 : issue #154
            MessageEnum.reload();

            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.cmdCircuitDelete.sendConvertedMessage(address, circuit);
        } else {
            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.invalidCircuit.sendConvertedMessage(address, circuit);
        }
    }

    /**
     * 引数oldNameをキーに持つCircuitDataオブジェクトのキーをnewNameに変更する
     * ハッシュマップ、ローカルファイル、設置済みのチェックポイント全てのキーを置換する
     * @param address ログの送信先
     * @param oldName 変更前のキー
     * @param newName 変更後のキー
     */
    public static void renameCircuit(CommandSender address, String oldName, String newName) {
        CircuitData oldCircuitData = getCircuitData(oldName);
        CircuitData newCircuitData = getCircuitData(newName);

        //名称を変更するCircuitDataObjectが存在しない
        if (oldCircuitData == null) {
            Circuit circuit = new Circuit();
            circuit.setCircuitName(oldName);
            MessageEnum.invalidCircuit.sendConvertedMessage(address, circuit);

        //新たな名称のCircuitDataObjectが既に存在している
        } else if (newCircuitData != null) {
            Circuit circuit = new Circuit();
            circuit.setCircuitName(newName);
            MessageEnum.cmdCircuitAlreadyExist.sendConvertedMessage(address, circuit);

        } else {

            //既存のオブジェクトのメンバ変数を引き継いだ新たなオブジェクトを生成
            newCircuitData = oldCircuitData.cloneCircuitData();

            //新たなキーに名称を変更
            newCircuitData.setCircuitDataName(newName);

            //ローカルファイルへ保存
            newCircuitData.saveConfiguration();

            //ハッシュマップに追加
            putCircuitData(newName, newCircuitData);

            //古いCircuitDataオブジェクトをハッシュマップから削除
            removeCircuitData(oldName);

            //古いCircuitDataオブジェクトをローカルファイルから削除
            oldCircuitData.deleteConfiguration();

            //設置済みのチェックポイントの固有名をnewNameに変更する
            for (World world : Bukkit.getWorlds()) {
                for (Entity entity : world.getEntities()) {
                    if (CheckPointUtil.isSpecificCircuitCheckPointEntity(entity, oldName)) {
                        entity.setCustomName(entity.getCustomName().replace(oldName, newName));
                    }
                }
            }

            //MessageEnumのメンバ変数を更新 : issue #154
            MessageEnum.reload();

            Circuit circuit = new Circuit();
            circuit.setCircuitName(newName);
            MessageEnum.cmdCircuitRename.sendConvertedMessage(address, circuit);
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
     * @param address ログの送信先
     * @param circuitDataName CircuitDataオブジェクトキー
     * @param location 設定座標
     */
    public static void setStartPosition(CommandSender address, String circuitDataName, Location location) {
        CircuitData circuitData = CircuitConfig.getCircuitData(circuitDataName);

        if (circuitData == null) {
            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.invalidCircuit.sendConvertedMessage(address, circuit);
        } else {
            circuitData.setStartLocation(location);
            circuitData.saveConfiguration();

            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.cmdCircuitSetPosition.sendConvertedMessage(address, circuit);
        }
    }

    /**
     * 引数circuitDataNameをキーに持つCircuitDataオブジェクトの周回数を設定する
     * @param address ログの送信先
     * @param circuitDataName CircuitDataオブジェクトキー
     * @param newValue 周回数
     */
    public static void setNumberOfLaps(CommandSender address, String circuitDataName, int newValue) {
        CircuitData circuitData = CircuitConfig.getCircuitData(circuitDataName);
        if (circuitData == null) {
            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.invalidCircuit.sendConvertedMessage(address, circuit);
        } else {
            circuitData.setNumberOfLaps(newValue);
            circuitData.saveConfiguration();

            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.cmdCircuitSetLap.sendConvertedMessage(address, circuit, newValue);
        }
    }

    /**
     * 引数circuitDataNameをキーに持つCircuitDataオブジェクトの
     * 最小プレイ人数を設定する
     * @param address ログの送信先
     * @param circuitDataName CircuitDataオブジェクトキー
     * @param newValue 最小プレイ人数
     */
    public static void setMinPlayer(CommandSender address, String circuitDataName, int newValue) {
        CircuitData circuitData = CircuitConfig.getCircuitData(circuitDataName);
        if (circuitData == null) {
            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.invalidCircuit.sendConvertedMessage(address, circuit);

        //最大プレイ人数を越える値は設定できない
        } else if (circuitData.getMaxPlayer() < newValue) {
            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.cmdCircuitOutOfMaxPlayer.sendConvertedMessage(address, circuit, circuitData.getMaxPlayer());
        } else {
            circuitData.setMinPlayer(newValue);
            circuitData.saveConfiguration();

            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.cmdCircuitSetMinPlayer.sendConvertedMessage(address, circuit, newValue);
        }
    }

    /**
     * 引数circuitDataNameをキーに持つCircuitDataオブジェクトの
     * 最大プレイ人数を設定する
     * @param address ログの送信先
     * @param circuitDataName CircuitDataオブジェクトキー
     * @param newValue 最大プレイ人数
     */
    public static void setMaxPlayer(CommandSender address, String circuitDataName, int newValue) {
        CircuitData circuitData = CircuitConfig.getCircuitData(circuitDataName);
        if (circuitData == null) {
            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.invalidCircuit.sendConvertedMessage(address, circuit);

        //最小プレイ人数を下回る数値は設定できない
        } else if (newValue < circuitData.getMinPlayer()) {
            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.cmdCircuitOutOfMinPlayer.sendConvertedMessage(address, circuit, circuitData.getMinPlayer());
        } else {
            circuitData.setMaxPlayer(newValue);
            circuitData.saveConfiguration();

            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.cmdCircuitSetMaxPlayer.sendConvertedMessage(address, circuit, newValue);
        }
    }

    /**
     * 引数circuitDataNameをキーに持つCircuitDataオブジェクトの
     * マッチング猶予時間を設定する
     * @param address ログの送信先
     * @param circuitDataName CircuitDataオブジェクトキー
     * @param newValue マッチング猶予時間
     */
    public static void setMatchingTime(CommandSender address, String circuitDataName, int newValue) {
        CircuitData circuitData = CircuitConfig.getCircuitData(circuitDataName);
        if (circuitData == null) {
            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.invalidCircuit.sendConvertedMessage(address, circuit);
        } else {
            circuitData.setMatchingTime(newValue);
            circuitData.saveConfiguration();

            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.cmdCircuitSetMatchingTime.sendConvertedMessage(address, circuit, newValue);
        }
    }

    /**
     * 引数circuitDataNameをキーに持つCircuitDataオブジェクトの
     * メニュー選択猶予時間を設定する
     * @param address ログの送信先
     * @param circuitDataName CircuitDataオブジェクトキー
     * @param newValue メニュー選択猶予時間
     */
    public static void setMenuTime(CommandSender address, String circuitDataName, int newValue) {
        CircuitData circuitData = CircuitConfig.getCircuitData(circuitDataName);
        if (circuitData == null) {
            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.invalidCircuit.sendConvertedMessage(address, circuit);
        } else {
            circuitData.setMenuTime(newValue);
            circuitData.saveConfiguration();

            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.cmdCircuitSetMenuTime.sendConvertedMessage(address, circuit, newValue);
        }
    }

    /**
     * 引数circuitDataNameをキーに持つCircuitDataオブジェクトの
     * レース終了までの制限時間を設定する
     * @param address ログの送信先
     * @param circuitDataName CircuitDataオブジェクトキー
     * @param newValue レース終了までの制限時間
     */
    public static void setLimitTime(CommandSender address, String circuitDataName, int newValue) {
        CircuitData circuitData = CircuitConfig.getCircuitData(circuitDataName);
        if (circuitData == null) {
            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.invalidCircuit.sendConvertedMessage(address, circuit);
        } else {
            circuitData.setLimitTime(newValue);
            circuitData.saveConfiguration();

            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.cmdCircuitSetLimitTime.sendConvertedMessage(address, circuit, newValue);
        }
    }

    /**
     * 引数circuitDataNameをキーに持つCircuitDataオブジェクトの
     * ゴール時のサーバー全体通知をするかどうかを設定する
     * @param address ログの送信先
     * @param circuitDataName CircuitDataオブジェクトキー
     * @param newValue ゴール時のサーバー全体通知をするかどうか
     */
    public static void setBroadcastGoalMessage(CommandSender address, String circuitDataName, boolean newValue) {
        CircuitData circuitData = CircuitConfig.getCircuitData(circuitDataName);
        if (circuitData == null) {
            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.invalidCircuit.sendConvertedMessage(address, circuit);
        } else {
            circuitData.setBroadcastGoalMessage(newValue);
            circuitData.saveConfiguration();

            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.cmdCircuitSetBroadcastGoalMessage.sendConvertedMessage(address, circuit, newValue);
        }
    }

    /**
     * 引数circuitDataNameをキーに持つCircuitDataオブジェクトのレースタイプを設定する
     * @param address ログの送信先
     * @param circuitDataName CircuitDataオブジェクトキー
     * @param raceType レースタイプ
     */
    public static void setRaceType(CommandSender address, String circuitDataName, RaceType raceType) {
        CircuitData circuitData = CircuitConfig.getCircuitData(circuitDataName);
        if (circuitData == null) {
            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.invalidCircuit.sendConvertedMessage(address, circuit);
        } else {
            circuitData.setRaceType(raceType);
            circuitData.saveConfiguration();

            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.cmdCircuitSetRaceType.sendConvertedMessage(address, circuit, raceType);
        }
    }

    //〓 static util 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 引数circuitDataNameをキーに持つCircuitDataオブジェクトの
     * 走行記録をランキング形式で引数addressへ送信する
     * @param address ログの送信先
     * @param circuitDataName CircuitDataオブジェクトキー
     */
    public static void sendRanking(CommandSender address, String circuitDataName) {
        CircuitData circuitData = getCircuitData(circuitDataName);
        if (circuitData == null) {
            Circuit circuit = new Circuit();
            circuit.setCircuitName(circuitDataName);
            MessageEnum.invalidCircuit.sendConvertedMessage(address, circuit);
            return;
        }

        circuitData.sendRanking(address);
    }

    //〓 Getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @return CircuitDataオブジェクトを格納しているハッシュマップ */
    public static HashMap<String, CircuitData> getCircuitDataMap() {
        return circuitDataMap;
    }

    /**
     * 引数circuitDataNameに対応するCircuitDataオブジェクトを返す
     * @param circuitDataName 取得するCircuitDataオブジェクト名
     * @return CircuitDataオブジェクト
     */
    public static CircuitData getCircuitData(String circuitDataName) {
        return getCircuitDataMap().get(ChatColor.stripColor(circuitDataName));
    }

    /** @return CircuitDataオブジェクトを格納しているハッシュマップのKeySet */
    public static Set<String> getCircuitDataMapKeySet() {
        return getCircuitDataMap().keySet();
    }

    /** @return CircuitDataオブジェクトキーのList */
    public static List<String> getCircuitList() {
        return Arrays.asList(getCircuitDataMap().keySet().toArray(new String[0]));
    }

    /** @return CircuitDataオブジェクトキーのListString */
    public static String getCircuitListString() {
        String list = null;
        for (String key : getCircuitList()) {
            if (list == null) {
                list = key;
            } else {
                list += ", " + key;
            }
        }
        return list;
    }

    //〓 Setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @param circuitDataMap CircuitDataオブジェクトを格納しているハッシュマップ */
    public static void setCircuitDataMap(HashMap<String, CircuitData> circuitDataMap) {
        CircuitConfig.circuitDataMap = circuitDataMap;
    }

    /**
     * CircuitDataオブジェクトを格納しているハッシュマップに新たなCircuitDataオブジェクトを格納する
     * @param 格納するCircuitDataのサーキット名
     * @param 格納するCircuitDataオブジェクト
     * @return 格納に成功したかどうか
     */
    public static boolean putCircuitData(String circuitDataName, CircuitData circuitData) {
        if (!getCircuitDataMap().containsKey(circuitDataName)) {
            getCircuitDataMap().put(circuitDataName, circuitData);
            return true;
        }
        return false;
    }

    /**
     * CircuitDataオブジェクトを格納しているハッシュマップから引数circuitDataNameをキーに持つCircuitDataオブジェクトを削除する
     * @param circuitDataName 削除するCircuitDataオブジェクトキー
     * @return 削除に成功したかどうか
     */
    public static boolean removeCircuitData(String circuitDataName) {
        if (getCircuitDataMap().containsKey(circuitDataName)) {
            getCircuitDataMap().remove(circuitDataName);
            return true;
        }
        return false;
    }

    /** CircuitDataオブジェクトを格納しているハッシュマップを初期化する */
    public static void clearCircuitData() {
        getCircuitDataMap().clear();
    }
}
