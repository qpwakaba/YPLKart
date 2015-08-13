package com.github.erozabesu.yplkart;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

import com.github.erozabesu.yplkart.data.CharacterConfig;
import com.github.erozabesu.yplkart.data.DisplayKartConfig;
import com.github.erozabesu.yplkart.data.KartConfig;
import com.github.erozabesu.yplkart.data.MessageEnum;
import com.github.erozabesu.yplkart.enumdata.EnumSelectMenu;
import com.github.erozabesu.yplkart.object.Character;
import com.github.erozabesu.yplkart.object.Circuit;
import com.github.erozabesu.yplkart.object.Kart;
import com.github.erozabesu.yplkart.object.KartType;
import com.github.erozabesu.yplkart.object.Racer;
import com.github.erozabesu.yplkart.reflection.Constructors;
import com.github.erozabesu.yplkart.reflection.Methods;
import com.github.erozabesu.yplkart.utils.PacketUtil;
import com.github.erozabesu.yplkart.utils.ReflectionUtil;
import com.github.erozabesu.yplkart.utils.Util;

public class RaceManager {
    public static int checkPointHeight = 8;
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

    public static void setMatchingCircuitData(UUID id) {
        Circuit c = getCircuit(id);
        Player p = Bukkit.getPlayer(id);
        if (p != null)
            p.playSound(p.getLocation(), Sound.CLICK, 1.0F, 1.0F);
        if (c == null) {
            return;
        } else if (!c.isMatching()) {
            return;
        } else if (isStandBy(id)) {
            return;
        } else {
            c.acceptMatching(id);
            MessageEnum.raceAccept.sendConvertedMessage(p, c);
        }
    }

    public static void clearMatchingCircuitData(UUID id) {
        Circuit c = getCircuit(id);
        Player p = Bukkit.getPlayer(id);
        if (p != null)
            p.playSound(p.getLocation(), Sound.CLICK, 1.0F, 0.9F);
        if (c == null) {
            return;
        } else if (!c.isMatching()) {
            return;
        } else if (isStandBy(id)) {
            return;
        } else {
            clearEntryRaceData(id);
        }
    }

    // 〓 Racer Setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static void setEntryRaceData(UUID id, String circuitname) {
        if (isEntry(id)) {
            String oldcircuitname = Util.convertInitialUpperString(getRace(id).getCircuitName());
            MessageEnum.raceEntryAlready.sendConvertedMessage(id, oldcircuitname);
        } else {
            Circuit c = setupCircuit(circuitname);
            if (c.isFillPlayer()) {
                c.entryReservePlayer(id);
                MessageEnum.raceEntryFull.sendConvertedMessage(id, c);
            } else {
                getRace(id).setCircuitName(circuitname);

                if (c.isStarted()) {
                    c.entryReservePlayer(id);
                    MessageEnum.raceEntryAlreadyStart.sendConvertedMessage(id, c);
                } else {
                    c.entryPlayer(id);
                    Scoreboards.entryCircuit(id);

                    MessageEnum.raceEntry.sendConvertedMessage(id, c);

                    if (c.isMatching())
                        setMatchingCircuitData(id);
                }
            }
        }
    }

    public static void setCharacterRaceData(UUID uuid, Character character) {
        //レース開始前はなにもしない
        if (!isStandBy(uuid)) {
            MessageEnum.raceNotStarted.sendConvertedMessage(uuid, getCircuit(uuid));
            return;
        }

        Player player = Bukkit.getPlayer(uuid);

        //プレイヤーがオフライン
        if (player == null) {
            return;
        }

        Racer racer = getRace(uuid);

        racer.setCharacter(character);
        racer.recoveryCharacter();

        PacketUtil.disguiseLivingEntity(null, player, character.getNmsClass(), 0, 0, 0);
        character.playMenuSelectSound(player);
        MessageEnum.raceCharacter.sendConvertedMessage(uuid, new Object[] { character, getCircuit(racer.getCircuitName()) });
    }

    public static void setKartRaceData(UUID uuid, Kart kart) {
        if (!isStandBy(uuid)) {
            MessageEnum.raceNotStarted.sendConvertedMessage(uuid, getCircuit(uuid));
            return;
        }

        //プレイヤーがオフライン
        if (Bukkit.getPlayer(uuid) == null) {
            return;
        }

        Racer racer = getRace(uuid);
        racer.saveKartEntityLocation();
        racer.setKart(kart);
        racer.recoveryKart();

        MessageEnum.raceKart.sendConvertedMessage(uuid, new Object[] { kart, getCircuit(racer.getCircuitName()) });
    }

    public static void clearEntryRaceData(UUID uuid) {
        if (isEntry(uuid)) {
            Scoreboards.exitCircuit(uuid);

            Racer racer = getRace(uuid);
            Circuit circuit = getCircuit(uuid);
            circuit.exitPlayer(uuid);

            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                if (!racer.isGoal()) {
                    clearCharacterRaceData(uuid);
                    clearKartRaceData(uuid);
                    leaveRacingKart(player);
                    if (isStandBy(uuid)) {
                        //全パラメータを復元する
                        racer.recoveryAll();
                    }
                }
                MessageEnum.raceExit.sendConvertedMessage(uuid, circuit);
            }

            racer.initializeRacer();
        }
    }

    public static void clearCharacterRaceData(UUID id) {
        if (getRace(id).getCharacter() == null)
            return;

        getRace(id).setCharacter(null);
        Player p = Bukkit.getPlayer(id);
        if (p != null) {
            getRace(id).recoveryPhysical();
            PacketUtil.returnOriginalPlayer(p);
            MessageEnum.raceCharacterReset.sendConvertedMessage(id, getCircuit(id));
        }
    }

    public static void clearKartRaceData(UUID id) {
        if (getRace(id).getKart() == null)
            return;

        if (Bukkit.getPlayer(id) != null)
            MessageEnum.raceLeave.sendConvertedMessage(id, getCircuit(id));
        getRace(id).setKart(null);
    }

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
        return circuit.get(circuitname).getEntryPlayer();
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

    public static ArrayList<Entity> getNearbyCheckpoint(Location l, double radius, String circuitname) {
        List<Entity> entityList = Util.getNearbyEntities(l.clone().add(0, checkPointHeight, 0), radius);

        ArrayList<Entity> nearbycheckpoint = new ArrayList<Entity>();
        for (Entity e : entityList) {
            //プレイヤーとの高低差が一定以上のチェックポイントはスルー
            if (Math.abs(e.getLocation().getY() - l.getY()) < checkPointHeight + 5)
                if (isCustomWitherSkull(e, circuitname))
                    if (ChatColor.stripColor(e.getCustomName()).equalsIgnoreCase(circuitname))
                        nearbycheckpoint.add(e);
        }

        if (nearbycheckpoint.isEmpty())
            return null;
        return nearbycheckpoint;
    }

    public static List<Entity> getNearbyUnpassedCheckpoint(Location l, double radius, Racer r) {
        String lap = r.getCurrentLaps() <= 0 ? "" : String.valueOf(r.getCurrentLaps());
        List<Entity> entityList = Util.getNearbyEntities(l.clone().add(0, checkPointHeight, 0), radius);

        List<Entity> nearbycheckpoint = new ArrayList<Entity>();
        for (Entity e : entityList) {
            //プレイヤーとの高低差が一定以上のチェックポイントはスルー
            if (Math.abs(e.getLocation().getY() - l.getY()) < checkPointHeight + 5)
                if (isCustomWitherSkull(e, r.getCircuitName()))
                    if (ChatColor.stripColor(e.getCustomName()).equalsIgnoreCase(r.getCircuitName()))
                        if (!r.getPassedCheckPointList().contains(lap + e.getUniqueId().toString()))
                            nearbycheckpoint.add(e);
        }

        if (nearbycheckpoint.isEmpty())
            return null;
        return nearbycheckpoint;
    }

    public static Entity getNearestUnpassedCheckpoint(Location l, double radius, Racer r) {
        List<Entity> checkpoint = getNearbyUnpassedCheckpoint(l, radius, r);
        if (checkpoint == null)
            return null;

        return Util.getNearestEntity(checkpoint, l);
    }

    public static ArrayList<String> getNearbyCheckpointID(Location l, double radius, String circuitname) {
        List<Entity> entityList = Util.getNearbyEntities(l.clone().add(0, checkPointHeight, 0), radius);

        ArrayList<String> nearbycheckpoint = new ArrayList<String>();
        for (Entity e : entityList) {
            //プレイヤーとの高低差が一定以上のチェックポイントはスルー
            if (Math.abs(e.getLocation().getY() - l.getY()) < checkPointHeight + 5)
                if (isCustomWitherSkull(e, circuitname))
                    if (ChatColor.stripColor(e.getCustomName()).equalsIgnoreCase(circuitname))
                        nearbycheckpoint.add(e.getUniqueId().toString());
        }

        if (nearbycheckpoint.isEmpty())
            return null;

        return nearbycheckpoint;
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
    public static Boolean isStandBy(UUID uuid) {
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
            if (isStandBy(uuid)) {
                if (getCircuit(uuid).isStarted()) {
                    return true;
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
            if (isStandBy(uuid)) {
                if (getCircuit(uuid).isStarted()) {
                    if (!getRace(uuid).isGoal()) {
                        return true;
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

    public static boolean isCustomWitherSkull(Entity e, String circuitname) {
        if (!(e instanceof WitherSkull))
            return false;
        if (e.getCustomName() == null)
            return false;
        if (!ChatColor.stripColor(e.getCustomName()).equalsIgnoreCase(circuitname))
            return false;
        return true;
    }

    // 〓 Edit Entity 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

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

    public static Entity createCustomWitherSkull(Location l, String circuitname) throws Exception {
        WitherSkull skull = l.getWorld().spawn(l.add(0, checkPointHeight, 0), WitherSkull.class);
        skull.setDirection(new Vector(0, 0, 0));
        skull.setVelocity(new Vector(0, 0, 0));
        skull.getLocation().setYaw(0);
        skull.getLocation().setPitch(0);
        skull.setCustomName(ChatColor.GREEN + circuitname);
        skull.setCustomNameVisible(true);

        return skull;
    }

    // 〓 Edit Player 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 選択メニュー（仮想インベントリ）を引数playerに表示する
     * 選択メニューのアイテムは下記のように配置される
     * □ = 空白
     * ■ = オブジェクトアイテム
     * ▲ = メニュー操作用アイテム
     * レイアウト：
     * □□□□□□□□□
     * □■■■■■■■□
     * □■■■■■■■□
     * ...
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
            if (Permission.hasPermission(player, Permission.KART_RIDE, true)) {
                inv.setItem(inventorySlotAmount - 5, EnumSelectMenu.CHARACTER_PREVIOUS.getMenuItem());
                inv.setItem(inventorySlotAmount - 3, EnumSelectMenu.CHARACTER_NEXT.getMenuItem());
            }
        } else {
            inv.setItem(inventorySlotAmount - 4, EnumSelectMenu.KART_RANDOM.getMenuItem());
            inv.setItem(inventorySlotAmount - 5, EnumSelectMenu.KART_PREVIOUS.getMenuItem());
            inv.setItem(inventorySlotAmount - 3, EnumSelectMenu.KART_NEXT.getMenuItem());
        }

        player.openInventory(inv);
    }
}