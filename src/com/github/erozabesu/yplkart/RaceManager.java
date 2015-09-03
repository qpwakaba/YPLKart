package com.github.erozabesu.yplkart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.github.erozabesu.yplkart.data.CharacterConfig;
import com.github.erozabesu.yplkart.data.CircuitConfig;
import com.github.erozabesu.yplkart.data.ItemEnum;
import com.github.erozabesu.yplkart.data.KartConfig;
import com.github.erozabesu.yplkart.data.MessageEnum;
import com.github.erozabesu.yplkart.enumdata.EnumSelectMenu;
import com.github.erozabesu.yplkart.object.Character;
import com.github.erozabesu.yplkart.object.Circuit;
import com.github.erozabesu.yplkart.object.CircuitData;
import com.github.erozabesu.yplkart.object.Kart;
import com.github.erozabesu.yplkart.object.KartType;
import com.github.erozabesu.yplkart.object.RaceType;
import com.github.erozabesu.yplkart.object.Racer;
import com.github.erozabesu.yplkart.reflection.Classes;
import com.github.erozabesu.yplkart.utils.KartUtil;
import com.github.erozabesu.yplkart.utils.PacketUtil;
import com.github.erozabesu.yplkart.utils.Util;

public class RaceManager {
    /** プレイヤーUUIDとRacerオブジェクトを格納する */
    private static HashMap<UUID, Racer> racerDataMap = new HashMap<UUID, Racer>();

    /** サーキット名とCircuitオブジェクトを格納する */
    private static HashMap<String, Circuit> circuitMap = new HashMap<String, Circuit>();

    // 〓 Getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 引数uuidを持つプレイヤーのRacerオブジェクトを返す
     * Racerオブジェクトがnullの場合、新規に生成し格納したオブジェクトを返す
     * @param uuid ハッシュマップキーとなるプレイヤーUUID
     * @return Racerオブジェクト
     */
    public static Racer getRace(UUID uuid) {
        if (racerDataMap.get(uuid) == null) {
            putRacer(uuid, null);
        }
        return racerDataMap.get(uuid);
    }

    /**
     * 引数playerのRacerオブジェクトを返す
     * Racerオブジェクトがnullの場合、新規に生成し格納したオブジェクトを返す
     * @param player ハッシュマップキーとなるプレイヤー
     * @return Racerオブジェクト
     */
    public static Racer getRacer(Player player) {
        return getRace(player.getUniqueId());
    }

    /**
     * 引数uuidのプレイヤーがエントリーしているサーキットを返す。<br>
     * サーキットが存在しない場合は{@code null}を返す。
     * @param uuid チェックするプレイヤーのUUID
     * @return サーキット
     */
    public static Circuit getCircuit(UUID uuid) {
        Circuit circuit = circuitMap.get(getRace(uuid).getCircuitName());

        return circuit == null ? null : circuit;
    }

    /**
     * 引数circuitnameに一致するサーキット名を持つサーキットを返す。<br>
     * サーキットが存在しない場合は{@code null}を返す。
     * @param uuid チェックするプレイヤーのUUID
     * @return サーキット
     */
    public static Circuit getCircuit(String circuitname) {
        Circuit circuit = circuitMap.get(circuitname);

        return circuit == null ? null : circuit;
    }

    // 〓 Setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 引数uuidをキー、引数racerをバリューとして、ハッシュマップracerDataMapに格納する<br>
     * 引数racerがnullの場合、Racerオブジェクトを新規に生成し格納する
     * @param uuid ハッシュマップキーとして格納するプレイヤーUUID
     * @param racer バリューとして格納するRacerオブジェクト
     */
    public static void putRacer(UUID uuid, Racer racer) {
        if (racer == null) {
            racerDataMap.put(uuid, new Racer(uuid));
        } else {
            racerDataMap.put(uuid, racer);
        }
    }

    public static Circuit setupCircuit(String circuitname) {
        if (circuitMap.get(circuitname) == null)
            circuitMap.put(circuitname, new Circuit(circuitname));

        return circuitMap.get(circuitname);
    }

    public static void clearCircuitData(String circuitname) {
        if (circuitMap.get(circuitname) != null) {
            circuitMap.get(circuitname).initialize();
            circuitMap.remove(circuitname);
        }
    }

    public static void endAllCircuit() {
        for (Circuit c : circuitMap.values()) {
            c.endRace();
        }
        circuitMap.clear();
    }

    // 〓 Circuit Setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static void circuitSetter_AcceptMatching(UUID uuid) {
        Circuit circuit = getCircuit(uuid);
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 1.0F);
        }

        if (circuit == null) {
            return;
        } else if (!circuit.isMatching()) {
            return;
        } else if (isStandby(uuid)) {
            return;
        }

        if (circuit.acceptMatching(uuid)) {
            MessageEnum.raceAccept.sendConvertedMessage(player, circuit);
        }
    }

    public static void circuitSetter_DenyMatching(UUID uuid) {
        Circuit circuit = getCircuit(uuid);
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 0.9F);
        }

        if (circuit == null) {
            return;
        } else if (!circuit.isMatching()) {
            return;
        } else if (isStandby(uuid)) {
            return;
        } else {
            racerSetter_UnEntry(uuid);
        }
    }

    // 〓 Racer Setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static void racerSetter_Entry(UUID uuid, String circuitName, boolean forceEntry) {
        Player player = Bukkit.getPlayer(uuid);
        Racer racer = getRace(uuid);
        if (isEntry(uuid)) {
            String oldcircuitname = Util.convertInitialUpperString(racer.getCircuitName());
            MessageEnum.raceEntryAlready.sendConvertedMessage(player, oldcircuitname);
        } else {
            Circuit circuit = setupCircuit(circuitName);

            // プレイヤーの意思決定に関わらず強制的にエントリー
            if (forceEntry) {
                racer.setCircuitName(circuitName);
                circuit.entryPlayer(uuid);
                Scoreboards.entryCircuit(uuid);
                MessageEnum.raceEntryForce.sendConvertedMessage(player, circuit);
                circuit.acceptMatching(uuid);

                // 既にレースがスタートしている場合はレースに割り込ませる
                if (circuit.isStandby() || circuit.isStarted()) {

                    // スタート位置を取得
                    int startLocationListSize = circuit.getEntryPlayerList().size();
                    List<Location> startLocationList =
                            CircuitConfig.getCircuitData(circuit.getCircuitName()).getStartLocationList(startLocationListSize - 1);

                    // スタート位置にテレポート、プレイヤーの状態をレース用に初期化
                    circuit.setupRacer(uuid, startLocationList.get(startLocationListSize));

                    // メニューアイテムを削除
                    ItemEnum.removeAllKeyItems(player);

                    // TODO:
                    // 割り込みメッセージの送信
                }

            // 通常のエントリー
            } else {
                if (circuit.isFillPlayer()) {
                    circuit.entryReservePlayer(uuid);
                    MessageEnum.raceEntryFull.sendConvertedMessage(player, circuit);
                } else {
                    racer.setCircuitName(circuitName);

                    if (circuit.isStandby() || circuit.isStarted()) {
                        circuit.entryReservePlayer(uuid);
                        MessageEnum.raceEntryAlreadyStart.sendConvertedMessage(player, circuit);
                    } else {
                        circuit.entryPlayer(uuid);
                        Scoreboards.entryCircuit(uuid);
                        MessageEnum.raceEntry.sendConvertedMessage(player, circuit);

                        // サーキットが既にマッチングフェーズの場合は自動的に参加に同意する
                        if (circuit.isMatching()) {
                            circuitSetter_AcceptMatching(uuid);
                        }
                    }
                }
            }
        }
    }

    public static void racerSetter_Character(UUID uuid, Character character) {
        //レース開始前はなにもしない
        Player player = Bukkit.getPlayer(uuid);
        //プレイヤーがオフライン
        if (player == null) {
            return;
        }

        if (!isStandby(uuid)) {
            MessageEnum.raceNotStarted.sendConvertedMessage(player, getCircuit(uuid));
            return;
        }

        Racer racer = getRace(uuid);

        racer.setCharacter(character);
        racer.recoveryCharacter();

        PacketUtil.disguiseLivingEntity(null, player, character.getNmsClass());
        character.playMenuSelectSound(player);
        MessageEnum.raceCharacter.sendConvertedMessage(player, new Object[] { character, getCircuit(racer.getCircuitName()) });
    }

    public static void racerSetter_Kart(UUID uuid, Kart kart) {
        Player player = Bukkit.getPlayer(uuid);
        //プレイヤーがオフライン
        if (player == null) {
            return;
        }

        if (!isStandby(uuid)) {
            MessageEnum.raceNotStarted.sendConvertedMessage(player, getCircuit(uuid));
            return;
        }

        Racer racer = getRace(uuid);
        racer.saveKartEntityLocation();
        racer.setKart(kart);
        racer.recoveryKart();

        MessageEnum.raceKart.sendConvertedMessage(player, new Object[] { kart, getCircuit(racer.getCircuitName()) });
    }

    public static void racerSetter_UnEntry(UUID uuid) {
        if (isEntry(uuid)) {
            Scoreboards.exitCircuit(uuid);

            Racer racer = getRace(uuid);
            Circuit circuit = getCircuit(uuid);
            circuit.exitPlayer(uuid);

            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                if (!racer.isGoal()) {
                    racerSetter_DeselectCharacter(uuid);
                    racerSetter_DeselectKart(uuid);
                    leaveRacingKart(player);
                    if (isStandby(uuid)) {
                        //全パラメータを復元する
                        racer.recoveryAll();
                    }
                }
                MessageEnum.raceExit.sendConvertedMessage(player, circuit);
            }

            racer.initializeRacer();
        }
    }

    public static void racerSetter_DeselectCharacter(UUID id) {
        Racer racer = getRace(id);

        if (racer.getCharacter() == null) {
            return;
        }

        racer.setCharacter(null);
        Player player = Bukkit.getPlayer(id);
        if (player != null) {
            racer.recoveryPhysical();
            PacketUtil.disguiseLivingEntity(null, player, Classes.nmsEntityHuman);
            Circuit circuit = new Circuit();
            circuit.setCircuitName(racer.getCircuitName());
            MessageEnum.raceCharacterReset.sendConvertedMessage(player, circuit);
        }
    }

    public static void racerSetter_DeselectKart(UUID uuid) {
        Racer racer = getRace(uuid);
        if (racer.getKart() == null) {
            return;
        }

        racer.setKart(null);
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            Circuit circuit = new Circuit();
            circuit.setCircuitName(racer.getCircuitName());
            MessageEnum.raceLeave.sendConvertedMessage(player, circuit);
        }
    }

    /**
     * 引数playerが搭乗中のエンティティがレーシングカートエンティティだった場合、搭乗を解除し、カートエンティティをデスポーンさせる。
     * @param player
     */
    public static void leaveRacingKart(Player player) {
        Entity vehicle = player.getVehicle();
        if (vehicle != null) {
            if (KartUtil.isSpecificKartType(vehicle, KartType.RacingKart)) {
                player.leaveVehicle();
                KartUtil.removeKartEntity(vehicle);
            }
        }
    }

    // 〓 Util Getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static List<Player> getEntryPlayer(String circuitname) {
        if (circuitMap.get(circuitname) == null)
            return null;
        return circuitMap.get(circuitname).getOnlineEntryPlayerList();
    }

    public static List<Player> getGoalPlayer(String circuitname) {
        ArrayList<Player> goalplayer = new ArrayList<Player>();
        for (Player p : getEntryPlayer(circuitname)) {
            if (getRacer(p).isGoal())
                goalplayer.add(p);
        }
        return goalplayer;
    }

    public static List<Player> getRacingPlayer(String circuitname) {
        ArrayList<Player> list = new ArrayList<Player>();
        for (Player p : getEntryPlayer(circuitname)) {
            if (!getRacer(p).isGoal())
                list.add(p);
        }
        return list;
    }

    public static Player getPlayerfromRank(String circuitname, int rank) {
        for (Player p : getRacingPlayer(circuitname)) {
            if (getRank(p) == rank)
                return p;
        }
        return null;
    }

    // レース走行中(CPポイントカウント中)の順位
    public static Integer getRank(Player p) {
        HashMap<UUID, Integer> count = new HashMap<UUID, Integer>();

        for (Player entryplayer : getRacingPlayer(getRacer(p).getCircuitName())) {
            count.put(entryplayer.getUniqueId(), getRacer(entryplayer).getPassedCheckPointList().size());
        }

        List<Map.Entry<UUID, Integer>> entry = new ArrayList<Map.Entry<UUID, Integer>>(count.entrySet());
        Collections.sort(entry, new Comparator<Map.Entry<UUID, Integer>>() {
            @Override
            public int compare(Entry<UUID, Integer> entry1, Entry<UUID, Integer> entry2) {
                return entry2.getValue().compareTo(entry1.getValue());
            }
        });

        int rank = 1;
        for (Entry<UUID, Integer> ranking : entry) {
            if (ranking.getKey().equals(p.getUniqueId()))
                return rank;

            rank++;
        }

        return 0;
    }

    // 〓 Util Is 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * @param uuid チェックするレース参加プレイヤーのUUID
     * @return レースに参加申請し、規定人数が揃うまで待機している状態かどうか
     */
    public static Boolean isEntry(UUID uuid) {
        if (getRace(uuid).getCircuitName() != "")
            return true;
        return false;
    }

    /**
     * 申請していたレースが規定人数を満たし参加者が召集された状態かどうか<br>
     * メニュー選択をしている状態
     * @param uuid チェックするレース参加プレイヤーのUUID
     * @return 申請していたレースが規定人数を満たし参加者が召集された状態かどうか
     */
    public static Boolean isStandby(UUID uuid) {
        if (isEntry(uuid)) {
            if (getRace(uuid).isStandby()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param uuid チェックするレース参加プレイヤーのUUID
     * @return 申請していたレースのメニュー選択が終了し、スタートした状態かどうか
     */
    public static Boolean isStarted(UUID uuid) {
        if (isEntry(uuid)) {
            if (isStandby(uuid)) {
                Circuit circuit = getCircuit(uuid);
                if (circuit != null) {
                    if (circuit.isStarted()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * @param uuid チェックするレース参加プレイヤーのUUID
     * @return レースがスタートしており、かつまだゴールしていない状態かどうか
     */
    public static Boolean isStillRacing(UUID uuid) {
        if (isEntry(uuid)) {
            if (isStandby(uuid)) {
                Circuit circuit = getCircuit(uuid);
                if (circuit != null) {
                    if (circuit.isStarted()) {
                        if (!getRace(uuid).isGoal()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    // 〓 Edit Entity 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** 全サーキットに設置されている妨害エンティティをデスポーンさせる。 */
    public static void removeAllJammerEntity() {
        for (Circuit cir : circuitMap.values()) {
            cir.removeAllJammerEntity();
        }
    }

    // 〓 Edit Player 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 選択メニュー（仮想インベントリ）を引数playerに表示する<br>
     * 選択メニューのアイテムは下記のように配置される<br>
     * □ = 空白<br>
     * ■ = オブジェクトアイテム<br>
     * ▲ = メニュー操作用アイテム<br>
     * レイアウト：<br>
     * □□□□□□□□□<br>
     * □■■■■■■■□<br>
     * □■■■■■■■□<br>
     * ...<br>
     * □□□▲▲▲□□□
     * @param player 選択メニューを表示するプレイヤー
     * @param isCharacterMenu キャラクター選択メニューか、カート選択メニューかどうか
     */
    public static void showSelectMenu(Player player, boolean isCharacterMenu) {

        ArrayList<?> objectList;
        objectList = isCharacterMenu ? CharacterConfig.getCharacterList() : KartConfig.getKartList();
        Collections.reverse(objectList);
        int objectSize = objectList.size();

        //オブジェクト数が0以下の場合はなにもしない
        if (objectSize <= 0) {
            return;
        }

        //インベントリスロット数
        //空白行が1行（9スロット）、メニューボタンの行が1行（9スロット）で最低18スロット必要
        int inventorySlotAmount = 17;

        //オブジェクト数に応じてスロット数を拡張する
        while (0 < objectSize) {
            objectSize -= 7;
            inventorySlotAmount += 9;
        }

        //仮想インベントリの作成
        String inventoryName = isCharacterMenu ? "Character Select Menu" : "Kart Select Menu";
        Inventory inv = Bukkit.createInventory(null, inventorySlotAmount + 1, inventoryName);

        //スロットにオブジェクトアイテムを配置する
        objectSize = objectList.size();
        for (int i = 0; 0 < objectSize; i++) {
            //最初の1行(0～8)は空白
            if (i <= 8) {
                continue;
            }
            //9の倍数、9の倍数-1のスロットは空白
            if (i % 9 == 0 || i % 9 == 8) {
                continue;
            }

            //キャラクター選択メニューとカート選択メニューで処理が異なる
            if (isCharacterMenu) {
                inv.setItem(i, new ItemStack(((Character)objectList.get(0)).getMenuItem()));
            } else {
                inv.setItem(i, new ItemStack(((Kart)objectList.get(0)).getMenuItem()));
            }

            objectList.remove(0);
            objectSize--;
        }

        //メニュー操作用アイテムを配置する
        //キャラクター選択メニューとカート選択メニューで処理が異なる
        if (isCharacterMenu) {
            inv.setItem(inventorySlotAmount - 4, EnumSelectMenu.CHARACTER_RANDOM.getMenuItem());
            Racer racer = getRacer(player);
            if (!racer.getCircuitName().equalsIgnoreCase("")) {
                CircuitData circuitData = CircuitConfig.getCircuitData(racer.getCircuitName());
                if (circuitData != null) {
                    if (circuitData.getRaceType().equals(RaceType.KART)) {
                        inv.setItem(inventorySlotAmount - 5, EnumSelectMenu.CHARACTER_PREVIOUS.getMenuItem());
                        inv.setItem(inventorySlotAmount - 3, EnumSelectMenu.CHARACTER_NEXT.getMenuItem());
                    }
                }
            }
        } else {
            inv.setItem(inventorySlotAmount - 4, EnumSelectMenu.KART_RANDOM.getMenuItem());
            inv.setItem(inventorySlotAmount - 5, EnumSelectMenu.KART_PREVIOUS.getMenuItem());
            inv.setItem(inventorySlotAmount - 3, EnumSelectMenu.KART_NEXT.getMenuItem());
        }

        player.openInventory(inv);
    }
}