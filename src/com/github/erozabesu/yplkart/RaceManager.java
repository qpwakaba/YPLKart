package com.github.erozabesu.yplkart;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import com.github.erozabesu.yplkart.data.CharacterConfig;
import com.github.erozabesu.yplkart.data.CircuitConfig;
import com.github.erozabesu.yplkart.data.ConfigEnum;
import com.github.erozabesu.yplkart.data.DisplayKartConfig;
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
import com.github.erozabesu.yplkart.reflection.Constructors;
import com.github.erozabesu.yplkart.reflection.Methods;
import com.github.erozabesu.yplkart.utils.PacketUtil;
import com.github.erozabesu.yplkart.utils.ReflectionUtil;
import com.github.erozabesu.yplkart.utils.Util;

public class RaceManager {
    public static ItemStack checkPointDisplayItem = new ItemStack(Material.PRISMARINE_SHARD);
    public static ItemStack visibleCheckPointDisplayItem = new ItemStack(Material.PRISMARINE_CRYSTALS);
    public static int checkPointHeight = 6;
    public static int checkPointDetectRadius = 20;

    /** プレイヤーUUIDとRacerオブジェクトを格納する */
    private static HashMap<UUID, Racer> racerDataMap = new HashMap<UUID, Racer>();

    /** サーキット名とCircuitオブジェクトを格納する */
    private static HashMap<String, Circuit> circuit = new HashMap<String, Circuit>();

    /** 生成したカートエンティティのEntityIDとEntityを格納する */
    private static HashMap<Integer, Entity> kartEntityIdMap = new HashMap<Integer, Entity>();

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

    public static Circuit getCircuit(UUID id) {
        try {
            return circuit.get(getRace(id).getCircuitName());
        } catch (NullPointerException ex) {
            return null;
        }
    }

    public static Circuit getCircuit(String circuitname) {
        try {
            return circuit.get(circuitname);
        } catch (NullPointerException ex) {
            return null;
        }
    }

    /**
     * @param entityId 調べるエンティティのEntityID
     * @return 引数entityIdをEntityIDとして持つエンティティがカートエンティティかどうか
     */
    public static boolean isKartEntityByEntityId(int entityId) {

        return kartEntityIdMap.keySet().contains(entityId);
    }

    /**
     * @param entityId 調べるエンティティのEntityID
     * @return 引数entityIdをEntityIDとして持つカートエンティティを返す
     */
    public static Entity getKartEntityByEntityId(int entityId) {
        return kartEntityIdMap.get(entityId);
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
        if (circuit.get(circuitname) == null)
            circuit.put(circuitname, new Circuit(circuitname));

        return circuit.get(circuitname);
    }

    public static void clearCircuitData(String circuitname) {
        if (circuit.get(circuitname) != null) {
            circuit.get(circuitname).initialize();
            circuit.remove(circuitname);
        }
    }

    public static void endAllCircuit() {
        for (Circuit c : circuit.values()) {
            c.endRace();
        }
        circuit.clear();
    }

    /**
     * 引数entityをハッシュマップkartEntityIdMapに追加する
     * @param entity 追加するエンティティ
     */
    public static void putKartEntityIdMap(Entity entity) {
        kartEntityIdMap.put(entity.getEntityId(), entity);
    }

    /**
     * 引数entityIdをハッシュマップkartEntityIdMapから削除する
     * @param entityId 削除するEntityID
     */
    public static void removeKartEntityIdMap(int entityId) {
        if (isKartEntityByEntityId(entityId)) {
            kartEntityIdMap.remove(entityId);
        }
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
            if (isSpecificKartType(vehicle, KartType.RacingKart)) {
                player.leaveVehicle();
                removeKartEntityIdMap(vehicle.getEntityId());
                vehicle.remove();
            }
        }
    }

    // 〓 Util Getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static List<Player> getEntryPlayer(String circuitname) {
        if (circuit.get(circuitname) == null)
            return null;
        return circuit.get(circuitname).getOnlineEntryPlayerList();
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

    /**
     * 引数entityのMetadataからCustomMinecartObjectを取得し返す
     * @param entity CustomMinecartObjectを取得するEntity
     * @return 取得したCustomMinecartObject
     */
    public static Object getCustomMinecartObjectByEntityMetaData(Entity entity) {
        List<MetadataValue> metaDataList = entity.getMetadata(YPLKart.PLUGIN_NAME);
        if (metaDataList.size() != 0) {
            MetadataValue metaData = metaDataList.get(0);
            if (metaData != null) {
                Object metaDataValue = metaData.value();
                if (metaDataValue.getClass().isArray()) {
                    //MetaDataが配列の場合
                    for (Object values : (Object[]) metaDataValue) {
                        if (values.getClass().getSimpleName().contains("CustomArmorStand")) {
                            return values;
                        }
                    }
                } else {
                    //MetaDataが単数の場合
                    if (metaDataValue.getClass().getSimpleName().contains("CustomArmorStand")) {
                        return metaDataValue;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 引数entityのMetadataからKartオブジェクトを取得し返す
     * @param entity Kartオブジェクトを取得するEntity
     * @return 取得したKartオブジェクト
     */
    public static Kart getKartObjectByEntityMetaData(Entity entity) {
        List<MetadataValue> metaDataList = entity.getMetadata(YPLKart.PLUGIN_NAME);
        if (metaDataList.size() != 0) {
            MetadataValue metaData = metaDataList.get(0);
            if (metaData != null) {
                Object metaDataValue = metaData.value();
                if (metaDataValue.getClass().isArray()) {
                    //MetaDataが配列の場合
                    for (Object values : (Object[]) metaDataValue) {
                        if (values instanceof Kart) {
                            return (Kart) values;
                        }
                    }
                } else {
                    //MetaDataが単数の場合
                    if (metaDataValue instanceof Kart) {
                        return (Kart) metaDataValue;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 引数entityのカスタムネームのChatColorから、対応するチェックポイントTierを返す。<br>
     * 対応するTierが存在しない場合は{@code null}を返す。
     * @param entity 取得するエンティティ
     * @return チェックポイントのTier数。対応するTierが存在しない場合は{@code null}を返す。
     */
    public static Integer getCheckPointEntityTier(Entity entity) {
        String nameColor = ChatColor.getLastColors(entity.getCustomName());

        if (nameColor.contains("a")) {
            return 1;
        } else if (nameColor.contains("9")) {
            return 2;
        } else if (nameColor.contains("c")) {
            return 3;
        }

        return null;
    }

    /**
     * 引数entityが引数circuitNameが名称のサーキットに設置されたチェックポイントエンティティの場合、対応する階級からチェックポイントの検出距離を返す。<br>
     * チェックポイントエンティティでない場合は{@code null}を返す。
     * @param entity 取得するエンティティ
     * @return チェックポイントの検出距離
     */
    public static Integer getDetectCheckPointRadiusByCheckPointEntity(Entity entity, String circuitName) {
        if (!isSpecificCircuitCheckPointEntity(entity, circuitName)) {
            return null;
        }

        Integer tier = getCheckPointEntityTier(entity);
        if (tier == null) {
            return null;
        }

        switch(tier) {
            case 1:
                return (Integer) ConfigEnum.ITEM_DETECT_CHECKPOINT_RADIUS_TIER1.getValue();

            case 2:
                return (Integer) ConfigEnum.ITEM_DETECT_CHECKPOINT_RADIUS_TIER2.getValue();

            case 3:
                return (Integer) ConfigEnum.ITEM_DETECT_CHECKPOINT_RADIUS_TIER3.getValue();

            default:
                return null;
        }
    }

    // 〓

    /**
     * 引数circuitNameが名称のサーキットに設置されたチェックポイントのうち、引数locationを基点に半径radiusブロック以内に設置されたチェックポイント全てを配列で返す。<br>
     * チェックポイントが検出されなかった場合は{@code null}を返す。
     * @param circuitName サーキット名
     * @param location 基点となる座標
     * @param detectRadius チェックポイントを検出する範囲の半径
     * @return チェックポイントの配列
     */
    public static List<Entity> getNearbyCheckPoints(Racer racer, Location location, double detectRadius) {
        String circuitName = racer.getCircuitName();
        List<Entity> entityList = Util.getNearbyEntities(location.clone().add(0, checkPointHeight, 0), detectRadius);

        Iterator<Entity> iterator = entityList.iterator();
        Entity tempEntity;
        while (iterator.hasNext()) {
            tempEntity = iterator.next();

            //引数circuitNameのサーキットに設置されたチェックポイントではないエンティティを配列から削除
            if (!isSpecificCircuitCheckPointEntity(tempEntity, circuitName)) {
                iterator.remove();
            }
        }

        if (entityList == null || entityList.isEmpty()) {
            return null;
        }

        return entityList;
    }

    /**
     * 引数racerが参加中のサーキットに設置されたチェックポイントのうち、引数locationを基点に半径radiusブロック以内に設置された未通過のチェックポイント全てを配列で返す。<br>
     * チェックポイントが検出されなかった場合は{@code null}を返す。
     * @param racer 参加中のプレイヤーのRacerインスタンス
     * @param location 基点となる座標
     * @param radius 半径
     * @return チェックポイントの配列
     */
    public static List<Entity> getNearbyUnpassedCheckPoints(Racer racer, Location location, double radius) {
        String circuitName = racer.getCircuitName();
        if (circuitName == null || circuitName.equalsIgnoreCase("")) {
            return null;
        }

        String currentLaps = racer.getCurrentLaps() <= 0 ? "" : String.valueOf(racer.getCurrentLaps());
        List<Entity> entityList = Util.getNearbyEntities(location.clone().add(0, checkPointHeight, 0), radius);

        Iterator<Entity> iterator = entityList.iterator();
        Entity tempEntity;
        while (iterator.hasNext()) {
            tempEntity = iterator.next();

            // 引数circuitNameのサーキットに設置されたチェックポイントではないエンティティを配列から削除
            if (!isSpecificCircuitCheckPointEntity(tempEntity, circuitName)) {
                iterator.remove();
            }

            // 通過済みのチェックポイントを配列から削除
            if (racer.getPassedCheckPointList().contains(currentLaps + tempEntity.getUniqueId().toString())) {
                iterator.remove();
            }
        }

        if (entityList == null || entityList.isEmpty()) {
            return null;
        }

        return entityList;
    }

    /**
     * 引数circuitNameが名称のサーキットに設置されたチェックポイントのうち、引数locationを基点に半径detectRadiusブロック以内に設置されたチェックポイント、かつ、
     * 引数racerインスタンスのプレイヤーから視認できるチェックポイントを配列で返す。<br>
     * チェックポイントが検出されなかった場合は{@code null}を返す。
     * @param circuitName サーキット名
     * @param location 基点となる座標
     * @param detectRadius チェックポイントを検出する範囲の半径
     * @param sightThreshold 視野
     * @return チェックポイントの配列
     */
    public static List<Entity> getInSightAndVisibleNearbyCheckPoints(Racer racer, Location location, double detectRadius, float sightThreshold) {
        String circuitName = racer.getCircuitName();
        Player player = racer.getPlayer();
        List<Entity> entityList = Util.getNearbyEntities(location.clone().add(0, checkPointHeight, 0), detectRadius);

        Iterator<Entity> iterator = entityList.iterator();
        Entity tempEntity;
        while (iterator.hasNext()) {
            tempEntity = iterator.next();

            //引数circuitNameのサーキットに設置されたチェックポイントではないエンティティを配列から削除
            if (!isSpecificCircuitCheckPointEntity(tempEntity, circuitName)) {
                iterator.remove();
                continue;
            }

            // 視野に含まれていない場合配列から削除
            if (!Util.isLocationInSight(player, tempEntity.getLocation(), sightThreshold)) {
                iterator.remove();
                continue;
            }

            // 視線とチェックポイントの座標間に固形ブロックが存在する場合配列から削除
            if (!isVisibleCheckPointEntity(tempEntity)) {
                if (!Util.canSeeLocation(player.getEyeLocation(), tempEntity.getLocation())) {
                    iterator.remove();
                    continue;
                }
            }
        }

        if (entityList == null || entityList.isEmpty()) {
            return null;
        }

        return entityList;
    }

    /**
     * 引数racerが参加中のサーキットに設置されたチェックポイントのうち、引数locationを基点に半径radiusブロック以内に設置された未通過のチェックポイント、かつ、
     * 引数racerインスタンスのプレイヤーから視認できるチェックポイントを配列で返す。<br>
     * チェックポイントが検出されなかった場合は{@code null}を返す。
     * @param racer 参加中のプレイヤーのRacerインスタンス
     * @param location 基点となる座標
     * @param detectRadius チェックポイントを検出する範囲の半径
     * @param sightThreshold 視野
     * @return チェックポイントの配列
     */
    public static List<Entity> getInSightAndVisibleNearbyUnpassedCheckPoints(Racer racer, Location location, double detectRadius, float sightThreshold) {
        String circuitName = racer.getCircuitName();
        Player player = racer.getPlayer();
        if (circuitName == null || circuitName.equalsIgnoreCase("")) {
            return null;
        }

        String currentLaps = racer.getCurrentLaps() <= 0 ? "" : String.valueOf(racer.getCurrentLaps());
        List<Entity> entityList = Util.getNearbyEntities(location.clone().add(0, checkPointHeight, 0), detectRadius);

        Iterator<Entity> iterator = entityList.iterator();
        Entity tempEntity;
        while (iterator.hasNext()) {
            tempEntity = iterator.next();

            // 引数circuitNameのサーキットに設置されたチェックポイントではないエンティティを配列から削除
            if (!isSpecificCircuitCheckPointEntity(tempEntity, circuitName)) {
                iterator.remove();
                continue;
            }

            // 通過済みのチェックポイントを配列から削除
            if (racer.getPassedCheckPointList().contains(currentLaps + tempEntity.getUniqueId().toString())) {
                iterator.remove();
                continue;
            }

            // 視野に含まれていない場合配列から削除
            if (!Util.isLocationInSight(player, tempEntity.getLocation(), sightThreshold)) {
                iterator.remove();
                continue;
            }

            if (!isVisibleCheckPointEntity(tempEntity)) {
                if (!Util.canSeeLocation(player.getEyeLocation(), tempEntity.getLocation())) {
                    iterator.remove();
                    continue;
                }
            }
        }

        if (entityList == null || entityList.isEmpty()) {
            return null;
        }

        return entityList;
    }

    /**
     * 引数racerが参加中のサーキットに設置されたチェックポイントのうち、引数locationを基点に半径radiusブロック以内に設置された最寄のチェックポイントを返す。<br>
     * チェックポイントが検出されなかった場合は{@code null}を返す。
     * @param racer 参加中のプレイヤーのRacerインスタンス
     * @param location 基点となる座標
     * @param detectRadius チェックポイントを検出する範囲の半径
     * @return チェックポイントエンティティ
     */
    public static Entity getNearestCheckpoint(Racer racer, Location location, double detectRadius) {
        List<Entity> checkPointList = getNearbyCheckPoints(racer, location, detectRadius);
        if (checkPointList == null || checkPointList.isEmpty()) {
            return null;
        }

        return Util.getNearestEntity(checkPointList, location);
    }

    /**
     * 引数racerが参加中のサーキットに設置されたチェックポイントのうち、引数locationを基点に半径radiusブロック以内に設置された最寄の未通過のチェックポイントを返す。<br>
     * チェックポイントが検出されなかった場合は{@code null}を返す。
     * @param racer 参加中のプレイヤーのRacerインスタンス
     * @param location 基点となる座標
     * @param detectRadius チェックポイントを検出する範囲の半径
     * @return チェックポイントエンティティ
     */
    public static Entity getNearestUnpassedCheckpoint(Racer racer, Location location, double detectRadius) {
        List<Entity> checkPointList = getNearbyUnpassedCheckPoints(racer, location, detectRadius);
        if (checkPointList == null || checkPointList.isEmpty()) {
            return null;
        }

        return Util.getNearestEntity(checkPointList, location);
    }

    /**
     * 引数racerが参加中のサーキットに設置されたチェックポイントのうち、引数locationを基点に半径radiusブロック以内に設置された最寄のチェックポイント、かつ、
     * 引数racerインスタンスのプレイヤーから視認できるチェックポイントを返す。<br>
     * チェックポイントが検出されなかった場合は{@code null}を返す。
     * @param racer 参加中のプレイヤーのRacerインスタンス
     * @param location 基点となる座標
     * @param detectRadius チェックポイントを検出する範囲の半径
     * @return チェックポイントエンティティ
     */
    public static Entity getInSightAndVisibleNearestCheckpoint(Racer racer, Location location, double detectRadius, float sightThreshold) {
        List<Entity> checkPointList = getInSightAndVisibleNearbyCheckPoints(racer, location, detectRadius, sightThreshold);
        if (checkPointList == null || checkPointList.isEmpty()) {
            return null;
        }

        return Util.getNearestEntity(checkPointList, location);
    }

    /**
     * 引数racerが参加中のサーキットに設置されたチェックポイントのうち、引数locationを基点に半径radiusブロック以内に設置された最寄の未通過のチェックポイント、かつ、
     * 引数racerインスタンスのプレイヤーから視認できるチェックポイントを返す。<br>
     * チェックポイントが検出されなかった場合は{@code null}を返す。
     * @param racer 参加中のプレイヤーのRacerインスタンス
     * @param location 基点となる座標
     * @param detectRadius チェックポイントを検出する範囲の半径
     * @param sightThreshold
     * @return チェックポイントエンティティ
     */
    public static Entity getInSightAndVisibleNearestUnpassedCheckpoint(Racer racer, Location location, double detectRadius, float sightThreshold) {
        List<Entity> checkPointList = getInSightAndVisibleNearbyUnpassedCheckPoints(racer, location, detectRadius, sightThreshold);
        if (checkPointList == null || checkPointList.isEmpty()) {
            return null;
        }

        return Util.getNearestEntity(checkPointList, location);
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

    /**
     * 引数entityが引数kartTypeのエンティティかどうか判別する
     * @param entity 判別するエンティティ
     * @param kartType 判別するKartType
     * @return 引数kartTypeのエンティティかどうか
     */
    public static boolean isSpecificKartType(Entity entity, KartType kartType) {
        if (entity == null) {
            return false;
        }
        if (entity instanceof ArmorStand) {
            List<MetadataValue> metaDataList = entity.getMetadata(YPLKart.PLUGIN_NAME);
            if (metaDataList.size() != 0) {
                MetadataValue metaData = metaDataList.get(0);
                if (metaData != null) {
                    Object metaDataValue = metaData.value();
                    if (metaDataValue.getClass().isArray()) {
                        //MetaDataが配列の場合
                        for (Object values : (Object[]) metaDataValue) {
                            if (values instanceof KartType) {
                                if (values.equals(kartType)) {
                                    return true;
                                }
                            }
                        }
                    } else {
                        //MetaDataが単数の場合
                        if (metaDataValue instanceof KartType) {
                            if (metaDataValue.equals(kartType)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 引数entityカートエンティティかどうか判別する
     * @param entity 判別するエンティティ
     * @return カートエンティティかどうか
     */
    public static boolean isKartEntity(Entity entity) {
        if (entity == null) {
            return false;
        }
        if (entity instanceof ArmorStand) {
            List<MetadataValue> metaDataList = entity.getMetadata(YPLKart.PLUGIN_NAME);
            if (metaDataList.size() != 0) {
                MetadataValue metaData = metaDataList.get(0);
                if (metaData != null) {
                    Object metaDataValue = metaData.value();
                    if (metaDataValue.getClass().isArray()) {
                        //MetaDataが配列の場合
                        for (Object values : (Object[]) metaDataValue) {
                            if (values.getClass().getSimpleName().equalsIgnoreCase("KartType")) {
                                return true;
                            }
                        }
                    } else {
                        //MetaDataが単数の場合
                        if (metaDataValue.getClass().getSimpleName().equalsIgnoreCase("KartType")) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 引数entityがチェックポイントエンティティかどうかを返す。<br>
     * ウィザースカル、もしくはアーマースタンドエンティティであり、かつ存在するサーキットと同名のカスタムネームを所有しているエンティティの場合trueを返す。
     * @param entity チェックするエンティティ
     * @return 引数entityがチェックポイントエンティティかどうか
     */
    public static boolean isCheckPointEntity(Entity entity) {
        if (!(entity instanceof WitherSkull) && !(entity instanceof ArmorStand)) {
            return false;
        }
        if (entity.getCustomName() == null) {
            return false;
        }

        String entityName = ChatColor.stripColor(entity.getCustomName());
        for (String circuitName : CircuitConfig.getCircuitList()) {
            if (entityName.equalsIgnoreCase(circuitName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 引数entityが引数circuitNameが名称のサーキットに設置されたチェックポイントエンティティかどうかを返す。<br>
     * ウィザースカル、もしくはアーマースタンドエンティティであり、かつ引数circuitNameと同名のカスタムネームを所有しているエンティティの場合trueを返す。
     * @param entity チェックするエンティティ
     * @param circuitName サーキットの名称
     * @return 引数entityが引数circuitNameが名称のサーキットに設置されたチェックポイントエンティティかどうか
     */
    public static boolean isSpecificCircuitCheckPointEntity(Entity entity, String circuitName) {
        if (!(entity instanceof WitherSkull) && !(entity instanceof ArmorStand)) {
            return false;
        }
        if (entity.getCustomName() == null) {
            return false;
        }
        if (!ChatColor.stripColor(entity.getCustomName()).equalsIgnoreCase(ChatColor.stripColor(circuitName))) {
            return false;
        }
        return true;
    }

    /**
     * 引数entityが引数circuitNameが名称のサーキットに設置されたチェックポイントエンティティであり、かつTierが引数tierに一致しているかどうかを返す。<br>
     * ウィザースカル、もしくはアーマースタンドエンティティであり、かつ引数circuitNameと同名のカスタムネームを所有しているエンティティであり、<br>
     * かつTierが引数tierに一致している場合trueを返す。
     * @param entity チェックするエンティティ
     * @param circuitName サーキットの名称
     * @param tier チェックポイントエンティティの階級
     * @return 引数entityが引数circuitNameが名称のサーキットに設置されたチェックポイントエンティティであり、かつTierが引数tierに一致しているかどうか
     */
    public static boolean isCheckPointEntity(Entity entity, String circuitName, int tier) {
        if (!isSpecificCircuitCheckPointEntity(entity, circuitName)) {
            return false;
        }
        if (tier < 1 && 3 < tier) {
            return false;
        }

        String nameColor = ChatColor.getLastColors(entity.getCustomName());
        Integer entityTier = getCheckPointEntityTier(entity);
        if (entityTier == null) {
            return false;
        }

        if (entityTier != tier) {
            return false;
        }

        return true;
    }

    /**
     * 引数entityが透過チェックポイントエンティティかどうかを返す。<br>
     * アーマースタンドエンティティではなく、旧チェックポイントであるウィザースカルエンティティの場合はfalseを返す。
     * @param entity チェックするエンティティ
     * @return 引数entityが透過チェックポイントエンティティかどうか
     */
    public static boolean isVisibleCheckPointEntity(Entity entity) {
        if (!isCheckPointEntity(entity)) {
            return false;
        }

        if (!(entity instanceof ArmorStand)) {
            return false;
        }

        ItemStack itemInHand = ((ArmorStand) entity).getItemInHand();
        if (itemInHand == null) {
            return false;
        }

        if (itemInHand.isSimilar(checkPointDisplayItem)) {
            return false;
        }

        return true;
    }

    // 〓 Edit Entity 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** 全サーキットに設置されている妨害エンティティをデスポーンさせる。 */
    public static void removeAllJammerEntity() {
        for (Circuit cir : circuit.values()) {
            cir.removeAllJammerEntity();
        }
    }

    /**
     * カートエンティティをデスポーンさせる。<br>
     * 登録されているデータも削除する必要があるため、カートエンティティをデスポーンさせる場合は<br>
     * 必ずこのメソッドを経由すること。
     * @param entity デスポーンさせるカートエンティティ
     */
    public static void removeKartEntity(Entity entity) {
        removeKartEntityIdMap(entity.getEntityId());
        entity.remove();
    }

    public static Entity createRacingKart(Location location, Kart kart) {
        Entity entity = createCustomKart(location, kart, KartType.RacingKart);

        entity.setCustomName(kart.getKartName());

        return entity;
    }

    /**
     * 引数uuidをカスタムネームに持つディスプレイ用カートを引数locationに生成する
     * @param location 生成する座標
     * @param kart Kartオブジェクト。ディスプレイブロックの設定を引き継ぐ
     * @param uuid カスタムネーム。displaykart.ymlのコンフィグキーでもある
     * @return 生成したMinecartエンティティ
     */
    public static Entity createDisplayKart(Location location, Kart kart, String uuid) {
        Entity entity = createCustomKart(location, kart, KartType.DisplayKart);

        if (uuid == null) {
            entity.setCustomName(entity.getUniqueId().toString());
            DisplayKartConfig.createDisplayKart(entity.getUniqueId().toString(), kart, location);
        } else {
            entity.setCustomName(uuid);
        }

        return entity;
    }

    public static Entity createDriveKart(Location location, Kart kart) {
        Entity entity = createCustomKart(location, kart, KartType.DriveKart);

        entity.setCustomName(kart.getKartName());

        return entity;
    }

    /**
     * カスタムMinecartEntityを生成する
     * @param location 生成する座標
     * @param kart パラメータを引き継ぐKartObject
     * @param kartType カートの種類
     * @return 生成したMinecartEntity
     */
    private static Entity createCustomKart(Location location, Kart kart, KartType kartType) {
        Entity entity = null;
        try {
            Object craftWorld = ReflectionUtil.invoke(Methods.craftWorld_getHandle, location.getWorld());
            Object customKart = Constructors.constructor_yplCustomKart
                    .newInstance(craftWorld, kart, kartType, location);

            //個別情報の格納
            entity = (Entity) Methods.nmsEntity_getBukkitEntity.invoke(customKart);
            entity.setCustomNameVisible(false);
            entity.setMetadata(YPLKart.PLUGIN_NAME, new FixedMetadataValue(
                    YPLKart.getInstance(), new Object[]{kart, kartType, customKart}));

            //EntityIDの格納
            putKartEntityIdMap(entity);

            //エンティティのスポーン
            //必ずEntityIDの格納後に行う
            Methods.nmsWorld_addEntity.invoke(craftWorld, customKart);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return entity;
    }

    public static Entity createCheckPointEntity(Location l, String circuitname, boolean isVisible) {
        ArmorStand armorStand = l.getWorld().spawn(l.add(0, checkPointHeight, 0), ArmorStand.class);

        armorStand.getLocation().setYaw(l.getYaw());
        armorStand.getLocation().setPitch(l.getPitch());
        armorStand.setCustomName(circuitname);
        armorStand.setCustomNameVisible(true);
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setBasePlate(false);

        if (isVisible) {
            armorStand.setItemInHand(visibleCheckPointDisplayItem);
        } else {
            armorStand.setItemInHand(checkPointDisplayItem);
        }

        Object kartEntity = Util.getCraftEntity(armorStand);
        Object vector3f = ReflectionUtil.newInstance(Constructors.nmsVector3f, -26.0F + l.getPitch(), 1.00F, 0.0F);
        ReflectionUtil.invoke(Methods.nmsEntityArmorStand_setRightArmPose, kartEntity, vector3f);

        return armorStand;
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