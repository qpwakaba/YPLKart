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
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

import com.github.erozabesu.yplkart.data.CharacterConfig;
import com.github.erozabesu.yplkart.data.DisplayKartConfig;
import com.github.erozabesu.yplkart.data.ItemEnum;
import com.github.erozabesu.yplkart.data.KartConfig;
import com.github.erozabesu.yplkart.data.MessageEnum;
import com.github.erozabesu.yplkart.enumdata.EnumSelectMenu;
import com.github.erozabesu.yplkart.object.Character;
import com.github.erozabesu.yplkart.object.Circuit;
import com.github.erozabesu.yplkart.object.Kart;
import com.github.erozabesu.yplkart.object.KartType;
import com.github.erozabesu.yplkart.object.Racer;
import com.github.erozabesu.yplkart.utils.PacketUtil;
import com.github.erozabesu.yplkart.utils.ReflectionUtil;
import com.github.erozabesu.yplkart.utils.Util;

public class RaceManager {
    public static int checkPointHeight = 8;
    public static int checkPointDetectRadius = 20;
    private static HashMap<UUID, Racer> racedata = new HashMap<UUID, Racer>();
    private static HashMap<String, Circuit> circuit = new HashMap<String, Circuit>();

    // 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static Circuit setupCircuit(String circuitname) {
        if (circuit.get(circuitname) == null)
            circuit.put(circuitname, new Circuit(circuitname));

        return circuit.get(circuitname);
    }

    public static void clearCircuitData(String circuitname) {
        if (circuit.get(circuitname) != null) {
            circuit.get(circuitname).init();
            circuit.remove(circuitname);
        }
    }

    public static void endAllCircuit() {
        for (Circuit c : circuit.values()) {
            c.endRace();
        }
        circuit.clear();
    }

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

    // 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static void setEntryRaceData(UUID id, String circuitname) {
        if (isEntry(id)) {
            String oldcircuitname = Util.convertInitialUpperString(getRace(id).getEntry());
            MessageEnum.raceEntryAlready.sendConvertedMessage(id, oldcircuitname);
        } else {
            Circuit c = setupCircuit(circuitname);
            if (c.isFillPlayer()) {
                c.entryReservePlayer(id);
                MessageEnum.raceEntryFull.sendConvertedMessage(id, c);
            } else {
                getRace(id).setEntry(circuitname);

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

    public static void setCharacterRaceData(UUID id, Character character) {
        //レース開始前はなにもしない
        if (!isStandBy(id)) {
            MessageEnum.raceNotStarted.sendConvertedMessage(id, getCircuit(id));
            return;
        }
        //プレイヤーがオフライン
        if (Bukkit.getPlayer(id) == null) {
            MessageEnum.invalidPlayer.sendConvertedMessage(null, id);
            return;
        }

        final Player p = Bukkit.getPlayer(id);
        Racer r = getRace(id);

        r.setCharacter(character);
        r.recoveryCharacterPhysical();
        //TODO : issue #46
        p.getInventory().setHelmet(ItemEnum.MARIO_HAT.getItem());

        PacketUtil.disguise(p, null, character);
        character.playMenuSelectSound(p);
        MessageEnum.raceCharacter.sendConvertedMessage(id, new Object[] { character, getCircuit(r.getEntry()) });
    }

    public static void setKartRaceData(UUID id, Kart kart) {
        if (!isStandBy(id)) {
            MessageEnum.raceNotStarted.sendConvertedMessage(id, getCircuit(id));
            return;
        }
        if (Bukkit.getPlayer(id) == null) {
            MessageEnum.invalidPlayer.sendConvertedMessage(null, id);
            return;
        }

        Racer r = getRace(id);
        r.setKart(kart);
        r.recoveryKart();

        MessageEnum.raceKart.sendConvertedMessage(id, new Object[] { kart, getCircuit(r.getEntry()) });
    }

    // 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static void clearEntryRaceData(UUID id) {
        if (isEntry(id)) {
            Scoreboards.exitCircuit(id);

            Racer r = getRace(id);
            Circuit c = getCircuit(id);
            c.exitPlayer(id);

            Player p = Bukkit.getPlayer(id);
            if (p != null) {
                if (!r.getGoal()) {
                    clearCharacterRaceData(id);
                    clearKartRaceData(id);
                    leaveRacingKart(p);
                    if (isStandBy(id)) {
                        r.recoveryInventory();
                        r.recoveryPhysical();
                        p.teleport(r.getGoalPosition());
                    }
                }
                MessageEnum.raceExit.sendConvertedMessage(id, c);
            }

            r.init();
        }
    }

    public static void clearCharacterRaceData(UUID id) {
        if (getRace(id).getCharacter() == null)
            return;

        getRace(id).setCharacter(null);
        Player p = Bukkit.getPlayer(id);
        if (p != null) {
            getRace(id).recoveryPhysical();
            PacketUtil.returnPlayer(p);
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
                getRace(player).setCMDForceLeave(true);
                player.leaveVehicle();
                vehicle.remove();
                getRace(player).setCMDForceLeave(false);
            }
        }
    }

    // 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static Circuit getCircuit(UUID id) {
        try {
            return circuit.get(getRace(id).getEntry());
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

    public static Racer getRace(Player p) {
        if (racedata.get(p.getUniqueId()) == null) {
            racedata.put(p.getUniqueId(), new Racer(p.getUniqueId().toString()));
        }
        return racedata.get(p.getUniqueId());
    }

    public static Racer getRace(UUID id) {
        if (racedata.get(id) == null) {
            racedata.put(id, new Racer(id.toString()));
        }
        return racedata.get(id);
    }

    public static List<Player> getEntryPlayer(String circuitname) {
        if (circuit.get(circuitname) == null)
            return null;
        return circuit.get(circuitname).getEntryPlayer();
    }

    public static List<Player> getGoalPlayer(String circuitname) {
        ArrayList<Player> goalplayer = new ArrayList<Player>();
        for (Player p : getEntryPlayer(circuitname)) {
            if (getRace(p).getGoal())
                goalplayer.add(p);
        }
        return goalplayer;
    }

    public static List<Player> getRacingPlayer(String circuitname) {
        ArrayList<Player> list = new ArrayList<Player>();
        for (Player p : getEntryPlayer(circuitname)) {
            if (!getRace(p).getGoal())
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

        for (Player entryplayer : getRacingPlayer(getRace(p).getEntry())) {
            count.put(entryplayer.getUniqueId(), getRace(entryplayer).getPassedCheckPoint().size());
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
        String lap = r.getLapCount() <= 0 ? "" : String.valueOf(r.getLapCount());
        List<Entity> entityList = Util.getNearbyEntities(l.clone().add(0, checkPointHeight, 0), radius);

        List<Entity> nearbycheckpoint = new ArrayList<Entity>();
        for (Entity e : entityList) {
            //プレイヤーとの高低差が一定以上のチェックポイントはスルー
            if (Math.abs(e.getLocation().getY() - l.getY()) < checkPointHeight + 5)
                if (isCustomWitherSkull(e, r.getEntry()))
                    if (ChatColor.stripColor(e.getCustomName()).equalsIgnoreCase(r.getEntry()))
                        if (!r.getPassedCheckPoint().contains(lap + e.getUniqueId().toString()))
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
    public static Object getCustomMinecartObjectFromEntityMetaData(Entity entity) {
        List<MetadataValue> metaDataList = entity.getMetadata(YPLKart.PLUGIN_NAME);
        if (metaDataList != null) {
            MetadataValue metaData = metaDataList.get(0);
            if (metaData != null) {
                Object metaDataValue = metaData.value();
                if (metaDataValue.getClass().isArray()) {
                    //MetaDataが配列の場合
                    for (Object values : (Object[]) metaDataValue) {
                        if (values.getClass().getSimpleName().contains("CustomMinecart")) {
                            return values;
                        }
                    }
                } else {
                    //MetaDataが単数の場合
                    if (metaDataValue.getClass().getSimpleName().contains("CustomMinecart")) {
                        return metaDataValue;
                    }
                }
            }
        }
        return null;
    }

    // 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /*
     * レースに参加申請し、開始されるまで待機している状態です
     * 行動の制限等は掛かりません
     */
    public static Boolean isEntry(UUID id) {
        if (getRace(id).getEntry() != "")
            return true;
        return false;
    }

    /*
     * 申請していたレースが規定人数を満たし参加者が召集された状態です
     * まだレースは開始されていません
     * レースの終了までインベントリの操作等が出来ない代わりに
     * 専用のアイテムが利用でき、キャラクター選択やレース専用カートへの搭乗が可能となります
     */
    public static Boolean isStandBy(UUID id) {
        if (isEntry(id))
            if (getRace(id).getStandBy())
                return true;
        return false;
    }

    /*
     * 申請していたレースが開始された状態です
     */
    public static Boolean isRacing(UUID id) {
        if (isEntry(id))
            if (isStandBy(id))
                if (getCircuit(id).isStarted())
                    return true;
        return false;
    }

    /**
     * 引数entityが引数kartTypeのエンティティかどうか判別する
     * @param entity 判別するエンティティ
     * @param kartType 判別するKartType
     * @return 引数kartTypeのエンティティかどうか
     */
    public static boolean isSpecificKartType(Entity entity, KartType kartType) {
        if (entity instanceof Minecart) {
            List<MetadataValue> metaDataList = entity.getMetadata(YPLKart.PLUGIN_NAME);
            if (metaDataList != null) {
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

    public static boolean isCustomWitherSkull(Entity e, String circuitname) {
        if (!(e instanceof WitherSkull))
            return false;
        if (e.getCustomName() == null)
            return false;
        if (!ChatColor.stripColor(e.getCustomName()).equalsIgnoreCase(circuitname))
            return false;
        return true;
    }

    // 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static void removeAllJammerEntity() {
        for (Circuit cir : circuit.values()) {
            cir.removeAllJammerEntity();
        }
    }

    public static Minecart createRacingMinecart(Location l, Kart kart) {
        try {
            Object craftWorld = ReflectionUtil.getCraftWorld(l.getWorld());
            Class<?> customClass = ReflectionUtil.getYPLKartClass("CustomMinecart");
            Object customKart = customClass.getConstructor(
                    ReflectionUtil.getNMSClass("World"), Kart.class, KartType.class, Location.class)
                    .newInstance(craftWorld, kart, KartType.RacingKart, l);
            Minecart cart = (Minecart) customKart.getClass().getMethod("getBukkitEntity").invoke(customKart);

            customClass.getMethod("setPosition", double.class, double.class, double.class).invoke(customKart, l.getX(),
                    l.getY() + 1, l.getZ());
            craftWorld.getClass().getMethod("addEntity", ReflectionUtil.getNMSClass("Entity"))
                    .invoke(craftWorld, customKart);

            cart.setDisplayBlock(new MaterialData(kart.getDisplayMaterial(), kart.getDisplayMaterialData()));
            cart.setCustomName(kart.getKartName());
            cart.setCustomNameVisible(false);

            cart.setMetadata(YPLKart.PLUGIN_NAME, new FixedMetadataValue(
                    YPLKart.getInstance(), new Object[]{KartType.RacingKart, customKart}));

            return cart;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Minecart createDisplayMinecart(Location l, Kart kart, String id) {
        try {
            Object craftWorld = ReflectionUtil.getCraftWorld(l.getWorld());
            Class<?> customClass = ReflectionUtil.getYPLKartClass("CustomMinecart");
            Object customKart = customClass.getConstructor(
                    ReflectionUtil.getNMSClass("World"), Kart.class, KartType.class, Location.class)
                    .newInstance(craftWorld, kart, KartType.DisplayKart, l);
            Minecart cart = (Minecart) customKart.getClass().getMethod("getBukkitEntity").invoke(customKart);

            customClass.getMethod("setPosition", double.class, double.class, double.class).invoke(customKart, l.getX(),
                    l.getY() + 1, l.getZ());
            craftWorld.getClass().getMethod("addEntity", ReflectionUtil.getNMSClass("Entity"))
                    .invoke(craftWorld, customKart);

            cart.setDisplayBlock(new MaterialData(kart.getDisplayMaterial(), kart.getDisplayMaterialData()));

            if (id == null) {
                cart.setCustomName(cart.getUniqueId().toString());
                DisplayKartConfig.createDisplayKart(cart.getUniqueId().toString(), kart, l);
            } else {
                cart.setCustomName(id);
            }
            cart.setCustomNameVisible(false);

            cart.setMetadata(YPLKart.PLUGIN_NAME, new FixedMetadataValue(
                    YPLKart.getInstance(), new Object[]{KartType.DisplayKart, customKart}));

            return cart;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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

    // 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

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