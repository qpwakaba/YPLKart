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
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import com.github.erozabesu.yplkart.Data.DisplayKartData;
import com.github.erozabesu.yplkart.Data.Message;
import com.github.erozabesu.yplkart.Enum.EnumCharacter;
import com.github.erozabesu.yplkart.Enum.EnumItem;
import com.github.erozabesu.yplkart.Enum.EnumKarts;
import com.github.erozabesu.yplkart.Enum.EnumSelectMenu;
import com.github.erozabesu.yplkart.Enum.Permission;
import com.github.erozabesu.yplkart.Object.Circuit;
import com.github.erozabesu.yplkart.Object.Race;
import com.github.erozabesu.yplkart.Utils.PacketUtil;
import com.github.erozabesu.yplkart.Utils.ReflectionUtil;
import com.github.erozabesu.yplkart.Utils.Util;

public class RaceManager {
    public static int checkPointHeight = 8;
    public static int checkPointDetectRadius = 20;
    private static HashMap<UUID, Race> racedata = new HashMap<UUID, Race>();
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
            Message.raceAccept.sendMessage(p, c);
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
            Message.raceEntryAlready.sendMessage(id, oldcircuitname);
        } else {
            Circuit c = setupCircuit(circuitname);
            if (c.isFillPlayer()) {
                c.entryReservePlayer(id);
                Message.raceEntryFull.sendMessage(id, c);
            } else {
                getRace(id).setEntry(circuitname);

                if (c.isStarted()) {
                    c.entryReservePlayer(id);
                    Message.raceEntryAlreadyStart.sendMessage(id, c);
                } else {
                    c.entryPlayer(id);
                    Scoreboards.entryCircuit(id);

                    Message.raceEntry.sendMessage(id, c);

                    if (c.isMatching())
                        setMatchingCircuitData(id);
                }
            }
        }
    }

    public static void setCharacterRaceData(UUID id, EnumCharacter character) {
        if (!isStandBy(id)) {
            Message.raceNotStarted.sendMessage(id, getCircuit(id));
            return;
        }
        if (Bukkit.getPlayer(id) == null) {
            Message.invalidPlayer.sendMessage(null, id);
            return;
        }

        final Player p = Bukkit.getPlayer(id);
        Race r = getRace(id);

        r.setCharacter(character);
        r.recoveryCharacterPhysical();
        //TODO : issue #46
        p.getInventory().setHelmet(EnumItem.MARIO_HAT.getItem());

        PacketUtil.disguise(p, null, character);
        EnumCharacter.playCharacterVoice(Bukkit.getPlayer(id), character);
        Message.raceCharacter.sendMessage(id, new Object[] { character, getCircuit(r.getEntry()) });
    }

    public static void setKartRaceData(UUID id, EnumKarts kart) {
        if (!isStandBy(id)) {
            Message.raceNotStarted.sendMessage(id, getCircuit(id));
            return;
        }
        if (Bukkit.getPlayer(id) == null) {
            Message.invalidPlayer.sendMessage(null, id);
            return;
        }

        Race r = getRace(id);
        r.setKart(kart);
        r.recoveryKart();

        Message.raceKart.sendMessage(id, new Object[] { kart, getCircuit(r.getEntry()) });
    }

    // 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static void clearEntryRaceData(UUID id) {
        if (isEntry(id)) {
            Scoreboards.exitCircuit(id);

            Race r = getRace(id);
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
                Message.raceExit.sendMessage(id, c);
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
            Message.raceCharacterReset.sendMessage(id, getCircuit(id));
        }
    }

    public static void clearKartRaceData(UUID id) {
        if (getRace(id).getKart() == null)
            return;

        if (Bukkit.getPlayer(id) != null)
            Message.raceLeave.sendMessage(id, getCircuit(id));
        getRace(id).setKart(null);
    }

    public static void leaveRacingKart(Player p) {
        if (p.getVehicle() != null)
            if (isRacingKart(p.getVehicle())) {
                getRace(p).setCMDForceLeave(true);
                Entity vehicle = p.getVehicle();
                p.leaveVehicle();
                vehicle.remove();
                getRace(p).setCMDForceLeave(false);
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

    public static Race getRace(Player p) {
        if (racedata.get(p.getUniqueId()) == null) {
            racedata.put(p.getUniqueId(), new Race(p.getUniqueId().toString()));
        }
        return racedata.get(p.getUniqueId());
    }

    public static Race getRace(UUID id) {
        if (racedata.get(id) == null) {
            racedata.put(id, new Race(id.toString()));
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

    public static List<Entity> getNearbyUnpassedCheckpoint(Location l, double radius, Race r) {
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

    public static Entity getNearestUnpassedCheckpoint(Location l, double radius, Race r) {
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

    public static boolean isRacingKart(Entity e) {
        if (e instanceof Minecart)
            if (e.getCustomName() != null)
                if (EnumKarts.getKartArrayList().contains(ChatColor.stripColor(e.getCustomName()).toString()))
                    if (e.getMetadata(YPLKart.plname).get(0) != null)
                        return true;
        return false;
    }

    public static boolean isDisplayKart(Entity e) {
        if (e instanceof Minecart)
            if (e.getCustomName() != null)
                if (DisplayKartData.getList().contains(ChatColor.stripColor(e.getCustomName()).toString()))
                    return true;
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

    public static Minecart createCustomMinecart(Location l, EnumKarts kart) {
        try {
            Object craftWorld = ReflectionUtil.getCraftWorld(l.getWorld());
            Class<?> customClass = ReflectionUtil.getYPLKartClass("CustomMinecart");
            Object customCart = customClass.getConstructor(ReflectionUtil.getBukkitClass("World"), EnumKarts.class,
                    Location.class, boolean.class).newInstance(craftWorld, kart, l, false);
            Minecart cart = (Minecart) customCart.getClass().getMethod("getBukkitEntity").invoke(customCart);

            customClass.getMethod("setPosition", double.class, double.class, double.class).invoke(customCart, l.getX(),
                    l.getY() + 1, l.getZ());
            craftWorld.getClass().getMethod("addEntity", ReflectionUtil.getBukkitClass("Entity"))
                    .invoke(craftWorld, customCart);

            cart.setDisplayBlock(new MaterialData(kart.getDisplayBlock(), kart.getDisplayData()));
            cart.setCustomName(kart.getName());
            cart.setCustomNameVisible(false);

            cart.setMetadata(YPLKart.plname, new FixedMetadataValue(YPLKart.getInstance(), customCart));

            return cart;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Minecart createDisplayMinecart(Location l, EnumKarts kart, String id) {
        try {
            Object craftWorld = ReflectionUtil.getCraftWorld(l.getWorld());
            Class<?> customClass = ReflectionUtil.getYPLKartClass("CustomMinecart");
            Object customCart = customClass.getConstructor(ReflectionUtil.getBukkitClass("World"), EnumKarts.class,
                    Location.class, boolean.class).newInstance(craftWorld, kart, l, true);
            final Minecart cart = (Minecart) customCart.getClass().getMethod("getBukkitEntity").invoke(customCart);

            customClass.getMethod("setPosition", double.class, double.class, double.class).invoke(customCart, l.getX(),
                    l.getY() + 1, l.getZ());
            craftWorld.getClass().getMethod("addEntity", ReflectionUtil.getBukkitClass("Entity"))
                    .invoke(craftWorld, customCart);

            cart.setDisplayBlock(new MaterialData(kart.getDisplayBlock(), kart.getDisplayData()));

            if (id == null) {
                cart.setCustomName(cart.getUniqueId().toString());
                DisplayKartData.createData(cart.getUniqueId().toString(), kart, l);
            } else {
                cart.setCustomName(id);
            }
            cart.setCustomNameVisible(false);

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

    public static void showCharacterSelectMenu(Player p) {
        Inventory inv = Bukkit.createInventory(null, 36, "Character Select Menu");
        //inv.setItem(8, EnumSelectMenu.CharacterCancel.getMenuItem());
        inv.setItem(11, EnumCharacter.HUMAN.getMenuItem());
        inv.setItem(12, EnumCharacter.ZOMBIE.getMenuItem());
        inv.setItem(13, EnumCharacter.CREEPER.getMenuItem());
        inv.setItem(14, EnumCharacter.SKELETON.getMenuItem());
        inv.setItem(15, EnumCharacter.SPIDER.getMenuItem());
        inv.setItem(20, EnumCharacter.ENDERMAN.getMenuItem());
        inv.setItem(21, EnumCharacter.WITCH.getMenuItem());
        inv.setItem(22, EnumCharacter.PIG.getMenuItem());
        inv.setItem(23, EnumCharacter.SQUID.getMenuItem());
        inv.setItem(24, EnumCharacter.VILLAGER.getMenuItem());
        inv.setItem(31, EnumSelectMenu.CHARACTER_RANDOM.getMenuItem());

        if (Permission.hasPermission(p, Permission.KART_RIDE, true)) {
            inv.setItem(30, EnumSelectMenu.CHARACTER_PREVIOUS.getMenuItem());
            inv.setItem(32, EnumSelectMenu.CHARACTER_NEXT.getMenuItem());
        }
        p.openInventory(inv);
    }

    public static void showKartSelectMenu(Player p) {
        Inventory inv = Bukkit.createInventory(null, 36, "Kart Select Menu");
        //inv.setItem(8, EnumSelectMenu.KartCancel.getMenuItem());
        inv.setItem(9, EnumKarts.KART1.getMenuItem());
        inv.setItem(11, EnumKarts.KART2.getMenuItem());
        inv.setItem(13, EnumKarts.KART3.getMenuItem());
        inv.setItem(15, EnumKarts.KART4.getMenuItem());
        inv.setItem(19, EnumKarts.KART5.getMenuItem());
        inv.setItem(21, EnumKarts.KART6.getMenuItem());
        inv.setItem(23, EnumKarts.KART7.getMenuItem());
        inv.setItem(25, EnumKarts.KART8.getMenuItem());
        inv.setItem(31, EnumSelectMenu.KART_RANDOM.getMenuItem());
        inv.setItem(30, EnumSelectMenu.KART_PREVIOUS.getMenuItem());
        inv.setItem(32, EnumSelectMenu.KART_NEXT.getMenuItem());
        p.openInventory(inv);
    }
}