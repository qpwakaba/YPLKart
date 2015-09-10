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
import com.github.erozabesu.yplkart.enumdata.SelectMenu;
import com.github.erozabesu.yplkart.enumdata.TagType;
import com.github.erozabesu.yplkart.object.Character;
import com.github.erozabesu.yplkart.object.Circuit;
import com.github.erozabesu.yplkart.object.Kart;
import com.github.erozabesu.yplkart.object.KartType;
import com.github.erozabesu.yplkart.object.MessageParts;
import com.github.erozabesu.yplkart.object.RaceType;
import com.github.erozabesu.yplkart.object.Racer;
import com.github.erozabesu.yplkart.reflection.Classes;
import com.github.erozabesu.yplkart.utils.KartUtil;
import com.github.erozabesu.yplkart.utils.PacketUtil;
import com.github.erozabesu.yplkart.utils.Util;

public class RaceManager {
    /** プレイヤーUUIDとRacerオブジェクトを格納する */
    private static HashMap<UUID, Racer> racerDataMap = new HashMap<UUID, Racer>();

    // 〓 Getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 引数uuidのプレイヤーのRacerインスタンスを返す。<br>
     * Racerインスタンスが存在しない場合は新規に生成し格納したインスタンスを返す。
     * @param uuid プレイヤーUUID
     * @return Racerインスタンス
     */
    public static Racer getRacer(UUID uuid) {
        if (racerDataMap.get(uuid) == null) {
            putRacer(uuid, null);
        }
        return racerDataMap.get(uuid);
    }

    /**
     * 引数uuidのプレイヤーのRacerインスタンスを返す。<br>
     * Racerインスタンスが存在しない場合は新規に生成し格納したインスタンスを返す。
     * @param player プレイヤー
     * @return Racerインスタンス
     */
    public static Racer getRacer(Player player) {
        return getRacer(player.getUniqueId());
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

    public static void endAllCircuit(boolean clearAll) {
        for (Circuit circuit : CircuitConfig.values()) {
            circuit.endRace(clearAll);
        }
    }

    // 〓 Circuit Setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static void circuitSetter_AcceptMatching(Racer racer) {
        Circuit circuit = racer.getCircuit();

        Player player = racer.getPlayer();
        if (player != null) {
            player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 1.0F);
        }

        if (circuit == null) {
            return;

        // サーキットがマッチングフェーズでない場合はreturn
        } else if (!circuit.isMatchingPhase()) {
            return;
        }

        if (!racer.isMatchingAccepted()) {
            racer.setMatchingAccepted(true);
            MessageEnum.raceAccept.sendConvertedMessage(player, MessageParts.getMessageParts(circuit));
        }
    }

    public static void circuitSetter_DenyMatching(Racer racer) {
        Circuit circuit = racer.getCircuit();
        Player player = racer.getPlayer();

        if (player != null) {
            player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 0.9F);
        }

        if (circuit == null) {
            return;

        // サーキットがマッチングフェーズでない場合はreturn
        } else if (!circuit.isMatchingPhase()) {
            return;
        }

        racerSetter_UnEntry(racer);
    }

    // 〓 Racer Setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static void racerSetter_Entry(Racer racer, String circuitName, boolean forceEntry) {
        Player player = racer.getPlayer();
        Circuit circuit = CircuitConfig.get(circuitName);

        // 既にエントリーしている場合return
        if (racer.isEntry()) {
            MessageParts circuitParts = new MessageParts(TagType.CIRCUIT, Util.convertInitialUpperString(circuitName));
            MessageEnum.raceEntryAlready.sendConvertedMessage(player, circuitParts);
            return;

        // 指定したサーキットが存在しない場合return
        } else if (circuit == null) {
            MessageParts circuitParts = new MessageParts(TagType.CIRCUIT, Util.convertInitialUpperString(circuitName));
            MessageEnum.invalidCircuit.sendConvertedMessage(player, circuitParts);
            return;

        // エントリー処理
        } else {
            // forceEntryフラグがtrueの場合はプレイヤーの意思決定に関わらず強制的にエントリー
            if (forceEntry) {
                racer.setCircuit(circuit);
                racer.setMatchingAccepted(true);
                circuit.entryPlayer(racer);
                Scoreboards.entryCircuit(racer.getUUID());
                MessageEnum.raceEntryForce.sendConvertedMessage(player, MessageParts.getMessageParts(circuit));

                // 既にレースがスタンバイフェーズ以降であればレースに割り込ませる
                if (circuit.isAfterStandbyPhase()) {

                    // スタート位置を取得
                    int startLocationListSize = circuit.getOnlineEntryRacerList().size();
                    List<Location> startLocationList = circuit.getStartLocationList(startLocationListSize - 1);

                    // スタート位置にテレポート、プレイヤーの状態をレース用に初期化
                    circuit.setupRacer(racer, startLocationList.get(startLocationListSize));

                    // メニューアイテムを削除
                    ItemEnum.removeAllKeyItems(player);

                    // TODO:
                    // 割り込みメッセージの送信
                }

            // 通常のエントリー
            } else {
                if (circuit.isFillPlayer()) {
                    circuit.entryReservePlayer(racer);
                    MessageEnum.raceEntryFull.sendConvertedMessage(player, MessageParts.getMessageParts(circuit));
                } else {
                    racer.setCircuit(circuit);

                    // 既にレースがスタンバイフェーズ以降であれば、リザーブエントリーする
                    if (circuit.isAfterStandbyPhase()) {
                        circuit.entryReservePlayer(racer);
                        MessageEnum.raceEntryAlreadyStart.sendConvertedMessage(player, MessageParts.getMessageParts(circuit));
                    } else {
                        circuit.entryPlayer(racer);
                        Scoreboards.entryCircuit(racer.getUUID());
                        MessageEnum.raceEntry.sendConvertedMessage(player, MessageParts.getMessageParts(circuit));

                        // サーキットが既にマッチングフェーズの場合は自動的に参加に同意する
                        if (circuit.isMatchingPhase()) {
                            circuitSetter_AcceptMatching(racer);
                        }
                    }
                }
            }
        }
    }

    public static void racerSetter_UnEntry(Racer racer) {
        if (racer.isEntry()) {
            Scoreboards.exitCircuit(racer.getUUID());

            Circuit circuit = racer.getCircuit();
            if (circuit == null) {
                return;
            }

            circuit.exitPlayer(racer);

            Player player = racer.getPlayer();
            if (player != null) {
                if (!racer.isGoal()) {
                    racerSetter_DeselectCharacter(racer.getUUID());
                    racerSetter_DeselectKart(racer.getUUID());
                    leaveRacingKart(player);

                    if (racer.isAfterStandbyPhase()) {
                        //全パラメータを復元する
                        racer.recoveryAll();
                    }
                }

                MessageEnum.raceExit.sendConvertedMessage(player, MessageParts.getMessageParts(circuit));
            }

            racer.initializeRacer();
        }
    }

    /**
     * 引数uuidのプレイヤーのキャラクターを引数characterにセットする。<br>
     * プレイヤーの選択キャラクターの変更、キャラクターフィジカルの適用、外見の偽装を行う。
     * @param uuid セットするプレイヤーのUUID
     * @param character セットするキャラクター
     */
    public static void racerSetter_Character(UUID uuid, Character character) {
        Player player = Bukkit.getPlayer(uuid);

        //プレイヤーがオフラインの場合return
        if (player == null) {
            return;
        }

        Racer racer = getRacer(uuid);
        Circuit circuit = racer.getCircuit();

        // レースがスタンバイフェーズ以降、かつプレイヤーがゴールしていない状態でなければreturn
        if (circuit == null) {
            MessageEnum.raceNotStarted.sendConvertedMessage(player);
            return;
        }
        if (!racer.isStillInRace()) {
            MessageEnum.raceNotStarted.sendConvertedMessage(player);
            return;
        }

        // キャラクターのセット
        racer.setCharacter(character);

        // キャラクターフィジカルの適用
        racer.recoveryCharacter();

        // 外見の変更
        PacketUtil.disguiseLivingEntity(null, player, character.getNmsClass());

        // キャラクターセレクトサウンドの再生
        character.playMenuSelectSound(player);

        // メッセージの送信
        MessageParts circuitParts = MessageParts.getMessageParts(circuit);
        MessageParts characterParts = MessageParts.getMessageParts(character);
        MessageEnum.raceCharacter.sendConvertedMessage(player, circuitParts, characterParts);
    }

    public static void racerSetter_Kart(UUID uuid, Kart kart) {
        Player player = Bukkit.getPlayer(uuid);

        //プレイヤーがオフライン
        if (player == null) {
            return;
        }

        Racer racer = getRacer(uuid);
        Circuit circuit = racer.getCircuit();

        // レースがスタンバイフェーズ以降、かつプレイヤーがゴールしていない状態でなければreturn
        if (circuit == null) {
            MessageEnum.raceNotStarted.sendConvertedMessage(player);
            return;
        }
        if (!racer.isStillInRace()) {
            MessageEnum.raceNotStarted.sendConvertedMessage(player);
            return;
        }

        racer.saveKartEntityLocation();
        racer.setKart(kart);
        racer.recoveryKart();

        MessageParts kartParts = MessageParts.getMessageParts(kart);
        MessageParts circuitParts = MessageParts.getMessageParts(circuit);
        MessageEnum.raceKart.sendConvertedMessage(player, kartParts, circuitParts);
    }

    public static void racerSetter_DeselectCharacter(UUID uuid) {
        Racer racer = getRacer(uuid);
        Circuit circuit = racer.getCircuit();
        if (circuit == null) {
            return;
        }

        if (racer.getCharacter() == null) {
            return;
        }

        racer.setCharacter(null);
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            racer.recoveryPhysical();
            PacketUtil.disguiseLivingEntity(null, player, Classes.nmsEntityHuman);

            MessageParts circuitParts = MessageParts.getMessageParts(circuit);
            MessageEnum.raceCharacterReset.sendConvertedMessage(player, circuitParts);
        }
    }

    public static void racerSetter_DeselectKart(UUID uuid) {
        Racer racer = getRacer(uuid);
        Circuit circuit = racer.getCircuit();
        if (circuit == null) {
            return;
        }

        if (racer.getKart() == null) {
            return;
        }

        racer.setKart(null);
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            MessageParts circuitParts = MessageParts.getMessageParts(circuit);
            MessageEnum.raceLeave.sendConvertedMessage(player, circuitParts);
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

    // 〓 Util Get 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 引数circuitに参加しているプレイヤーのうち、順位が引数rankに一致しているプレイヤーを返す。
     * @param circuit 取得するサーキット
     * @param rank 取得するプレイヤーの順位
     * @return 順位の一致したプレイヤー
     */
    public static Player getPlayerfromRank(Circuit circuit, int rank) {
        for (Player player : circuit.getOnlineRacingPlayerList()) {
            if (getRank(player) == rank) {
                return player;
            }
        }

        return null;
    }

    /**
     * 引数playerが参加しているレースにおける引数playerの順位を返す。<br>
     * 順位の計算は、オンラインの走行中のプレイヤーのみ含まれる。
     * @param player 順位を取得するプレイヤー
     * @return 順位
     */
    public static Integer getRank(Player player) {
        HashMap<UUID, Integer> count = new HashMap<UUID, Integer>();

        Racer racer = getRacer(player);
        Circuit circuit = racer.getCircuit();
        if (circuit == null) {
            return 0;
        }

        for (Player entryplayer : circuit.getOnlineRacingPlayerList()) {
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
            if (ranking.getKey().equals(player.getUniqueId()))
                return rank;

            rank++;
        }

        return 0;
    }

    // 〓 Edit Entity 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** 全サーキットに設置されている妨害エンティティをデスポーンさせる。 */
    public static void removeAllJammerEntity() {
        for (Circuit circuit : CircuitConfig.values()) {
            circuit.removeAllJammerEntity();
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
            inv.setItem(inventorySlotAmount - 4, SelectMenu.CHARACTER_RANDOM.getMenuItem());
            Racer racer = getRacer(player);
            Circuit circuit = racer.getCircuit();
            if (circuit != null) {
                if (circuit.getRaceType().equals(RaceType.KART)) {
                    inv.setItem(inventorySlotAmount - 5, SelectMenu.CHARACTER_PREVIOUS.getMenuItem());
                    inv.setItem(inventorySlotAmount - 3, SelectMenu.CHARACTER_NEXT.getMenuItem());
                }
            }
        } else {
            inv.setItem(inventorySlotAmount - 4, SelectMenu.KART_RANDOM.getMenuItem());
            inv.setItem(inventorySlotAmount - 5, SelectMenu.KART_PREVIOUS.getMenuItem());
            inv.setItem(inventorySlotAmount - 3, SelectMenu.KART_NEXT.getMenuItem());
        }

        player.openInventory(inv);
    }
}