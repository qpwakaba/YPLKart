package com.github.erozabesu.yplkart.data;

import java.util.Arrays;
import java.util.Collection;
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
import com.github.erozabesu.yplkart.enumdata.TagType;
import com.github.erozabesu.yplkart.object.Circuit;
import com.github.erozabesu.yplkart.object.LapTime;
import com.github.erozabesu.yplkart.object.MessageParts;
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
    private static HashMap<String, Circuit> circuitDataMap = new HashMap<String, Circuit>();

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
        clear();

        for(String configKey : ConfigManager.RACEDATA_CONFIG.getLocalConfig().getKeys(false)) {
            put(configKey, new Circuit(configKey));
        }
    }

    /**
     * 引数circuitNameが名称の新規のCircuitインスタンスを作成しハッシュマップに格納し、ローカルコンフィグファイルに設定を保存する。<br>
     * @param address ログの送信先
     * @param circuitName サーキット名
     * @param location レース開始座標
     */
    public static void createCircuit(CommandSender address, String circuitName, Location location) {
        Circuit circuit = get(circuitName);
        if (circuit == null) {
            circuit = new Circuit(circuitName, location);
            circuit.saveConfiguration();

            put(circuitName, circuit);

            // MessageEnumのメンバ変数を更新 : issue #154
            MessageEnum.reload();

            MessageEnum.cmdCircuitCreate.sendConvertedMessage(address, MessageParts.getMessageParts(circuit));
        } else {
            MessageEnum.cmdCircuitAlreadyExist.sendConvertedMessage(address, new MessageParts(TagType.CIRCUIT, circuitName));
        }
    }

    /**
     * 引数circuitDataに対応するCircuitDataオブジェクトをハッシュマップから削除し、
     * 設定データをローカルコンフィグファイルから削除する
     * @param address ログの送信先
     * @param circuitName 削除するCircuitDataオブジェクト名
     */
    public static void deleteCircuit(CommandSender address, String circuitName) {
        Circuit circuit = get(circuitName);
        if (circuit != null) {

            // サーキットのレースを終了
            circuit.endRace(false);

            //ハッシュマップから削除
            remove(circuitName);

            //ローカルファイルから削除
            circuit.deleteConfiguration();

            //MessageEnumのメンバ変数を更新 : issue #154
            MessageEnum.reload();

            MessageEnum.cmdCircuitDelete.sendConvertedMessage(address, new MessageParts(TagType.CIRCUIT, circuitName));
        } else {
            MessageEnum.invalidCircuit.sendConvertedMessage(address, new MessageParts(TagType.CIRCUIT, circuitName));
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
        Circuit oldCircuit = get(oldName);
        Circuit newCircuit = get(newName);

        //名称を変更するCircuitObjectが存在しない
        if (oldCircuit == null) {
            MessageEnum.invalidCircuit.sendConvertedMessage(address, new MessageParts(TagType.CIRCUIT, oldName));

        //新たな名称のCircuitDataObjectが既に存在している
        } else if (newCircuit != null) {
            MessageEnum.cmdCircuitAlreadyExist.sendConvertedMessage(address, new MessageParts(TagType.CIRCUIT, newName));

        } else {

            // 古いサーキットのレースを強制終了
            oldCircuit.endRace(false);

            // 現在の状態を保存
            oldCircuit.saveConfiguration();

            // 古いサーキットの設定を引き継ぐため、古い名称のサーキットインスタンスを新規生成
            newCircuit = new Circuit(oldCircuit.getCircuitName());

            // 新たな名称に変更
            newCircuit.setCircuitName(newName);

            //ローカルファイルへ保存
            newCircuit.saveConfiguration();

            //ハッシュマップに追加
            put(newName, newCircuit);

            //古いCircuitDataオブジェクトをハッシュマップから削除
            remove(oldName);

            //古いCircuitDataオブジェクトをローカルファイルから削除
            oldCircuit.deleteConfiguration();

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

            MessageEnum.cmdCircuitRename.sendConvertedMessage(address, new MessageParts(TagType.CIRCUIT, newName));
        }
    }

    /**
     * 引数playerの新しい走行記録を保存する
     * 古い記録が存在する場合、速い(数値が低い)方の記録がマージされる
     * @param player 走行時間を記録するプレイヤー
     * @param circuitName CircuitDataオブジェクトキー
     * @param lapTime 走行時間
     * @param isKartRace カートレースかどうか
     */
    public static void addRaceLapTime(Player player, String circuitName
            , double lapTime, boolean isKartRace) {

        Circuit circuit = get(circuitName);

        if (circuit == null) {
            return;
        }

        UUID uuid = player.getUniqueId();
        LapTime newLapTimeObject = new LapTime(circuit.getNumberOfLaps(), uuid, lapTime);
        LapTime oldLapTimeObject = isKartRace ? circuit.getKartLapTime(uuid) : circuit.getRunLapTime(uuid);

        //プレイヤーの記録が既に存在している
        if (oldLapTimeObject != null) {

            //新旧の記録を比較し、新しい記録の方が早い場合記録を上書き保存する
            if (lapTime < oldLapTimeObject.getLapTime()) {
                //古い記録を削除
                if (isKartRace) {
                    circuit.removeKartLapTime(uuid);
                } else {
                    circuit.removeRunLapTime(uuid);
                }

                //テキストメッセージの送信
                MessageParts numberParts = MessageParts.getMessageParts(oldLapTimeObject.getLapTime(), lapTime);
                MessageEnum.raceHighScore.sendConvertedMessage(player, new MessageParts(TagType.CIRCUIT, circuitName), numberParts);
            } else {
                //記録の更新がない場合は何もしない
                return;
            }
        }

        //新しい記録を格納
        if (isKartRace) {
            circuit.addKartLapTimeList(newLapTimeObject);
        } else {
            circuit.addRunLapTimeList(newLapTimeObject);
        }

        //ローカルファイルへ保存
        circuit.saveConfiguration();
    }

    //〓 static config edit 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 引数circuitNameをキーに持つCircuitオブジェクトのレース開始座標を
     * 引数locationの座標に設定する
     * @param address ログの送信先
     * @param circuitName CircuitDataオブジェクトキー
     * @param location 設定座標
     */
    public static void setStartPosition(CommandSender address, String circuitName, Location location) {
        Circuit circuit = get(circuitName);
        MessageParts circuitParts = new MessageParts(TagType.CIRCUIT, circuitName);

        if (circuit == null) {
            MessageEnum.invalidCircuit.sendConvertedMessage(address, circuitParts);
        } else {
            circuit.setStartLocation(location);
            circuit.saveConfiguration();

            MessageEnum.cmdCircuitSetPosition.sendConvertedMessage(address, circuitParts);
        }
    }

    /**
     * 引数circuitNameをキーに持つCircuitオブジェクトの周回数を設定する
     * @param address ログの送信先
     * @param circuitName CircuitDataオブジェクトキー
     * @param newValue 周回数
     */
    public static void setNumberOfLaps(CommandSender address, String circuitName, int newValue) {
        Circuit circuit = get(circuitName);
        MessageParts circuitParts = new MessageParts(TagType.CIRCUIT, circuitName);

        if (circuit == null) {
            MessageEnum.invalidCircuit.sendConvertedMessage(address, circuitParts);
        } else {
            circuit.setNumberOfLaps(newValue);
            circuit.saveConfiguration();

            MessageEnum.cmdCircuitSetLap.sendConvertedMessage(address, circuitParts, MessageParts.getMessageParts(newValue));
        }
    }

    /**
     * 引数circuitNameをキーに持つCircuitオブジェクトの最小プレイ人数を設定する
     * @param address ログの送信先
     * @param circuitName CircuitDataオブジェクトキー
     * @param newValue 最小プレイ人数
     */
    public static void setMinPlayer(CommandSender address, String circuitName, int newValue) {
        Circuit circuit = get(circuitName);
        MessageParts circuitParts = new MessageParts(TagType.CIRCUIT, circuitName);

        if (circuit == null) {
            MessageEnum.invalidCircuit.sendConvertedMessage(address, circuitParts);

        //最大プレイ人数を越える値は設定できない
        } else if (circuit.getMaxPlayer() < newValue) {
            MessageEnum.cmdCircuitOutOfMaxPlayer.sendConvertedMessage(address, circuitParts, MessageParts.getMessageParts(circuit.getMaxPlayer()));
        } else {
            circuit.setMinPlayer(newValue);
            circuit.saveConfiguration();

            MessageEnum.cmdCircuitSetMinPlayer.sendConvertedMessage(address, circuitParts, MessageParts.getMessageParts(newValue));
        }
    }

    /**
     * 引数circuitNameをキーに持つCircuitオブジェクトの最大プレイ人数を設定する
     * @param address ログの送信先
     * @param circuitName CircuitDataオブジェクトキー
     * @param newValue 最大プレイ人数
     */
    public static void setMaxPlayer(CommandSender address, String circuitName, int newValue) {
        Circuit circuit = get(circuitName);
        MessageParts circuitParts = new MessageParts(TagType.CIRCUIT, circuitName);

        if (circuit == null) {
            MessageEnum.invalidCircuit.sendConvertedMessage(address, circuitParts);

        //最小プレイ人数を下回る数値は設定できない
        } else if (newValue < circuit.getMinPlayer()) {
            MessageEnum.cmdCircuitOutOfMinPlayer.sendConvertedMessage(address, circuitParts, MessageParts.getMessageParts(circuit.getMinPlayer()));
        } else {
            circuit.setMaxPlayer(newValue);
            circuit.saveConfiguration();

            MessageEnum.cmdCircuitSetMaxPlayer.sendConvertedMessage(address, circuitParts, MessageParts.getMessageParts(newValue));
        }
    }

    /**
     * 引数circuitNameをキーに持つCircuitオブジェクトのマッチング猶予時間を設定する
     * @param address ログの送信先
     * @param circuitName CircuitDataオブジェクトキー
     * @param newValue マッチング猶予時間
     */
    public static void setMatchingTime(CommandSender address, String circuitName, int newValue) {
        Circuit circuit = get(circuitName);
        MessageParts circuitParts = new MessageParts(TagType.CIRCUIT, circuitName);

        if (circuit == null) {
            MessageEnum.invalidCircuit.sendConvertedMessage(address, circuitParts);
        } else {
            circuit.setMatchingTime(newValue);
            circuit.saveConfiguration();

            MessageEnum.cmdCircuitSetMatchingTime.sendConvertedMessage(address, circuitParts, MessageParts.getMessageParts(newValue));
        }
    }

    /**
     * 引数circuitNameをキーに持つCircuitオブジェクトのメニュー選択猶予時間を設定する
     * @param address ログの送信先
     * @param circuitName CircuitDataオブジェクトキー
     * @param newValue メニュー選択猶予時間
     */
    public static void setMenuTime(CommandSender address, String circuitName, int newValue) {
        Circuit circuit = get(circuitName);
        MessageParts circuitParts = new MessageParts(TagType.CIRCUIT, circuitName);

        if (circuit == null) {
            MessageEnum.invalidCircuit.sendConvertedMessage(address, circuitParts);
        } else {
            circuit.setMenuTime(newValue);
            circuit.saveConfiguration();

            MessageEnum.cmdCircuitSetMenuTime.sendConvertedMessage(address, circuitParts, MessageParts.getMessageParts(newValue));
        }
    }

    /**
     * 引数circuitNameをキーに持つCircuitオブジェクトのレース終了までの制限時間を設定する
     * @param address ログの送信先
     * @param circuitName CircuitDataオブジェクトキー
     * @param newValue レース終了までの制限時間
     */
    public static void setLimitTime(CommandSender address, String circuitName, int newValue) {
        Circuit circuit = get(circuitName);
        MessageParts circuitParts = new MessageParts(TagType.CIRCUIT, circuitName);

        if (circuit == null) {
            MessageEnum.invalidCircuit.sendConvertedMessage(address, circuitParts);
        } else {
            circuit.setLimitTime(newValue);
            circuit.saveConfiguration();

            MessageEnum.cmdCircuitSetLimitTime.sendConvertedMessage(address, circuitParts, MessageParts.getMessageParts(newValue));
        }
    }

    /**
     * 引数circuitNameをキーに持つCircuitオブジェクトのゴール時のサーバー全体通知をするかどうかを設定する
     * @param address ログの送信先
     * @param circuitName CircuitDataオブジェクトキー
     * @param newValue ゴール時のサーバー全体通知をするかどうか
     */
    public static void setBroadcastGoalMessage(CommandSender address, String circuitName, boolean newValue) {
        Circuit circuit = get(circuitName);
        MessageParts circuitParts = new MessageParts(TagType.CIRCUIT, circuitName);

        if (circuit == null) {
            MessageEnum.invalidCircuit.sendConvertedMessage(address, circuitParts);
        } else {
            circuit.setBroadcastGoalMessage(newValue);
            circuit.saveConfiguration();

            MessageEnum.cmdCircuitSetBroadcastGoalMessage.sendConvertedMessage(address, circuitParts, MessageParts.getMessageParts(newValue));
        }
    }

    /**
     * 引数circuitNameをキーに持つCircuitオブジェクトのレースタイプを設定する
     * @param address ログの送信先
     * @param circuitName CircuitDataオブジェクトキー
     * @param raceType レースタイプ
     */
    public static void setRaceType(CommandSender address, String circuitName, RaceType raceType) {
        Circuit circuit = get(circuitName);
        MessageParts circuitParts = new MessageParts(TagType.CIRCUIT, circuitName);

        if (circuit == null) {
            MessageEnum.invalidCircuit.sendConvertedMessage(address, circuitParts);
        } else {
            circuit.setRaceType(raceType);
            circuit.saveConfiguration();

            MessageEnum.cmdCircuitSetRaceType.sendConvertedMessage(address, circuitParts, MessageParts.getMessageParts(raceType));
        }
    }

    //〓 static util 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 引数circuitDataNameをキーに持つCircuitDataオブジェクトの
     * 走行記録をランキング形式で引数addressへ送信する
     * @param address ログの送信先
     * @param circuitName CircuitDataオブジェクトキー
     */
    public static void sendRanking(CommandSender address, String circuitName) {
        Circuit circuit = get(circuitName);
        if (circuit == null) {
            MessageParts circuitParts = new MessageParts(TagType.CIRCUIT, circuitName);
            MessageEnum.invalidCircuit.sendConvertedMessage(address, circuitParts);
            return;
        }

        circuit.sendRanking(address);
    }

    //〓 Getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @return CircuitDataオブジェクトを格納しているハッシュマップ */
    public static HashMap<String, Circuit> getMap() {
        return circuitDataMap;
    }

    /**
     * 引数circuitNameに対応するCircuitオブジェクトを返す
     * @param circuitName 取得するCircuitDataオブジェクト名
     * @return CircuitDataオブジェクト
     */
    public static Circuit get(String circuitName) {
        return getMap().get(ChatColor.stripColor(circuitName));
    }

    /** @return ハッシュマップのキーSet */
    public static Set<String> keySet() {
        return getMap().keySet();
    }

    /** @return ハッシュマップのバリューCollection */
    public static Collection<Circuit> values() {
        return getMap().values();
    }

    /** @return CircuitDataオブジェクトキーのList */
    public static List<String> keyToArray() {
        return Arrays.asList(getMap().keySet().toArray(new String[0]));
    }

    /** @return CircuitDataオブジェクトキーのListString */
    public static String getCircuitListString() {
        String list = null;
        for (String key : keyToArray()) {
            if (list == null) {
                list = key;
            } else {
                list += ", " + key;
            }
        }
        return list;
    }

    //〓 Setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @param circuitMap CircuitDataオブジェクトを格納しているハッシュマップ */
    public static void setMap(HashMap<String, Circuit> circuitMap) {
        circuitDataMap = circuitMap;
    }

    /**
     * CircuitDataオブジェクトを格納しているハッシュマップに新たなCircuitDataオブジェクトを格納する
     * @param 格納するCircuitDataのサーキット名
     * @param 格納するCircuitDataオブジェクト
     * @return 格納に成功したかどうか
     */
    public static boolean put(String circuitName, Circuit circuit) {
        if (!getMap().containsKey(circuitName)) {
            getMap().put(circuitName, circuit);
            return true;
        }
        return false;
    }

    /**
     * CircuitDataオブジェクトを格納しているハッシュマップから引数circuitDataNameをキーに持つCircuitDataオブジェクトを削除する
     * @param circuitName 削除するCircuitDataオブジェクトキー
     * @return 削除に成功したかどうか
     */
    public static boolean remove(String circuitName) {
        if (getMap().containsKey(circuitName)) {
            getMap().remove(circuitName);
            return true;
        }
        return false;
    }

    /** CircuitDataオブジェクトを格納しているハッシュマップを初期化する */
    public static void clear() {
        getMap().clear();
    }
}
