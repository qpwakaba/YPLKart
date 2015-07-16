package com.github.erozabesu.yplkart.Data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.YPLKart;
import com.github.erozabesu.yplkart.Enum.EnumItem;
import com.github.erozabesu.yplkart.Utils.Util;

public final class RaceData {
    public static YPLKart pl;

    private static String filename = "racedata.yml";
    private static File datafolder;
    private static File configFile;
    private static FileConfiguration config;

    private static int numberoflaps = 3;
    private static int defaultminplayer = 3;
    private static int defaultmaxplayer = 10;
    private static int matchingtime = 30;
    private static int menutime = 30;
    private static int limittime = 300;
    private static boolean broadcastboalmessage = false;

    public RaceData(YPLKart plugin) {
        pl = plugin;
        datafolder = pl.getDataFolder();

        configFile = new File(datafolder, filename);
        config = YamlConfiguration.loadConfiguration(configFile);

        if (!CreateConfig()) {
            return;
        }
    }

    /* <circuit name>:
     *   world: world
     *   x: double
     *   y: double
     *   z: double
     *   yaw: double
     *   pitch: double
     *   laptime:
     *     <lapcount>:
     *       <player name>: 123.456
     *       <player name>: 123.456
     *     <lapcount>:
     *       <player name>: 123.456
     *       <player name>: 123.456
     * */

    public static void createCircuit(Player p, String circuitname) {
        if (!getCircuitSet().contains(circuitname)) {
            Location l = p.getLocation();
            config.set(circuitname + ".world", l.getWorld().getName());
            config.set(circuitname + ".x", l.getX());
            config.set(circuitname + ".y", l.getY());
            config.set(circuitname + ".z", l.getZ());
            config.set(circuitname + ".yaw", l.getYaw());
            config.set(circuitname + ".pitch", l.getPitch());
            Message.cmdCircuitCreate.sendMessage(p, circuitname);
        } else {
            Message.cmdCircuitAlreadyExist.sendMessage(p, circuitname);
        }
        saveConfigFile();
    }

    public static void createCircuit(Player p, String circuitname, String worldname, double x, double y, double z,
            float yaw, float pitch) {
        if (!getCircuitSet().contains(circuitname)) {
            config.set(circuitname + ".world", worldname);
            config.set(circuitname + ".x", x);
            config.set(circuitname + ".y", y);
            config.set(circuitname + ".z", z);
            config.set(circuitname + ".yaw", yaw);
            config.set(circuitname + ".pitch", pitch);
            Message.cmdCircuitCreate.sendMessage(p, circuitname);
        } else {
            Message.cmdCircuitAlreadyExist.sendMessage(p, circuitname);
        }
        saveConfigFile();
    }

    public static void deleteCircuit(Player p, String circuitname) {
        for (String key : config.getKeys(false)) {
            if (key.equalsIgnoreCase(circuitname)) {
                config.set(key, null);
                for (World w : Bukkit.getWorlds()) {
                    for (Entity e : w.getEntities()) {
                        if (RaceManager.isCustomWitherSkull(e, circuitname))
                            e.remove();
                    }
                }
                saveConfigFile();
                Message.cmdCircuitDelete.sendMessage(p, circuitname);
                return;
            }
        }
        Message.invalidCircuit.sendMessage(p, circuitname);
    }

    public static void listCricuit(Player p) {
        Message.cmdCircuitList.sendMessage(p);
    }

    public static void editCircuit(Player p, String circuitname) {
        if (!getCircuitSet().contains(circuitname)) {
            Message.invalidCircuit.sendMessage(p, circuitname);
        } else {
            p.getInventory().addItem(EnumItem.getCheckPointTool(EnumItem.CHECKPOINT_TOOL, circuitname));
            Message.cmdCircuitEdit.sendMessage(p, circuitname);
        }
    }

    public static void renameCircuit(Player p, String circuitname, String newname) {
        if (!getCircuitSet().contains(circuitname)) {
            Message.invalidCircuit.sendMessage(p, circuitname);
        } else if (getCircuitSet().contains(newname)) {
            Message.cmdCircuitAlreadyExist.sendMessage(p, newname);
        } else {
            for (World w : Bukkit.getWorlds()) {
                for (Entity e : w.getEntities()) {
                    if (RaceManager.isCustomWitherSkull(e, circuitname))
                        e.setCustomName(e.getCustomName().replace(circuitname, newname));
                }
            }

            config.set(newname, config.get(circuitname));
            config.set(circuitname, null);
            saveConfigFile();

            Message.cmdCircuitRename.sendMessage(p, newname);
        }
    }

    public static void addRunningRaceLapTime(Player p, String circuitname, double laptime) {
        if (getCircuitSet().contains(circuitname)) {

            String path = circuitname + ".laptime." + getNumberOfLaps(circuitname) + "." + p.getUniqueId().toString();
            if (!config.contains(path)) {
                config.set(path, laptime);
                saveConfigFile();
                return;
            }

            if (laptime < config.getDouble(path, 0)) {
                Message.raceHighScore.sendMessage(p, new Object[] { circuitname,
                        new Object[] { config.getDouble(path), laptime } });
                config.set(path, laptime);
                saveConfigFile();
                return;
            }
        }
    }

    public static void addKartRaceLapTime(Player p, String circuitname, double laptime) {
        if (getCircuitSet().contains(circuitname)) {

            String path = circuitname + ".kartlaptime." + getNumberOfLaps(circuitname) + "."
                    + p.getUniqueId().toString();
            if (!config.contains(path)) {
                config.set(path, laptime);
                saveConfigFile();
                return;
            }

            if (laptime < config.getDouble(path, 0)) {
                Message.raceHighScore.sendMessage(p, new Object[] { circuitname,
                        new Object[] { config.getDouble(path), laptime } });
                config.set(path, laptime);
                saveConfigFile();
                return;
            }
        }
    }

    public static void sendRanking(UUID id, String circuitname) {
        String ranking = RaceData.getRanking(id, circuitname);
        String kartranking = RaceData.getKartRanking(id, circuitname);
        if (ranking == null && kartranking == null)
            Message.cmdCircuitRankingNoScoreData.sendMessage(id, circuitname);
        else {
            if (kartranking != null)
                Message.sendAbsolute(id, kartranking);
            if (ranking != null)
                Message.sendAbsolute(id, kartranking);
        }
    }

    public static void sendCircuitInformation(Object adress, String circuitname) {
        if (!getCircuitSet().contains(circuitname)) {
            Message.invalidCircuit.sendMessage(adress);
        }

        Location l = getPosition(circuitname);
        boolean flag = getBroadcastGoalMessage(circuitname);
        Number[] numberdata = { getNumberOfLaps(circuitname), getMinPlayer(circuitname), getMaxPlayer(circuitname),
                getLimitTime(circuitname), getMenuTime(circuitname), getMatchingTime(circuitname), l.getBlockX(),
                l.getBlockY(), l.getBlockZ(), l.getYaw(), l.getPitch() };

        Message.tableCircuitInformation.sendMessage(adress, new Object[] { circuitname, flag, numberdata });
    }

    // 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static int getNumberOfLaps(String circuitname) {
        if (!getCircuitSet().contains(circuitname))
            return numberoflaps;
        if (config.getInt(circuitname + ".numberoflaps") == 0)
            return numberoflaps;

        return config.getInt(circuitname + ".numberoflaps");
    }

    public static int getMinPlayer(String circuitname) {
        if (!getCircuitSet().contains(circuitname))
            return defaultminplayer;
        if (config.getInt(circuitname + ".minplayer") == 0)
            return defaultminplayer;

        return config.getInt(circuitname + ".minplayer");
    }

    public static int getMaxPlayer(String circuitname) {
        if (!getCircuitSet().contains(circuitname))
            return defaultmaxplayer;
        if (config.getInt(circuitname + ".maxplayer") == 0)
            return defaultmaxplayer;

        return config.getInt(circuitname + ".maxplayer");
    }

    public static int getMatchingTime(String circuitname) {
        if (!getCircuitSet().contains(circuitname))
            return matchingtime;
        if (config.getInt(circuitname + ".matchingtime") == 0)
            return matchingtime;

        return config.getInt(circuitname + ".matchingtime");
    }

    public static int getMenuTime(String circuitname) {
        if (!getCircuitSet().contains(circuitname))
            return menutime;
        if (config.getInt(circuitname + ".menutime") == 0)
            return menutime;

        return config.getInt(circuitname + ".menutime");
    }

    public static boolean getBroadcastGoalMessage(String circuitname) {
        if (!getCircuitSet().contains(circuitname))
            return false;

        return config.getBoolean(circuitname + ".broadcastgoalmessage", broadcastboalmessage);
    }

    public static int getLimitTime(String circuitname) {
        if (!getCircuitSet().contains(circuitname))
            return limittime;
        if (config.getInt(circuitname + ".limittime") == 0)
            return limittime;

        return config.getInt(circuitname + ".limittime");
    }

    public static Location getPosition(String circuitname) {
        if (!getCircuitSet().contains(circuitname))
            return null;
        if (Bukkit.getWorld(config.getString(circuitname + ".world")) == null)
            return null;

        return new Location(Bukkit.getWorld(config.getString(circuitname + ".world")), config.getDouble(circuitname
                + ".x"), config.getDouble(circuitname + ".y"), config.getDouble(circuitname + ".z"),
                (float) config.getDouble(circuitname + ".yaw"), (float) config.getDouble(circuitname + ".pitch"));
    }

    public static List<Location> getPositionList(String circuitname) {
        Location position = getPosition(circuitname);
        if (position == null)
            return null;

        List<Location> list = new ArrayList<Location>();
        while (list.size() < defaultmaxplayer) {
            if (!Util.isSolidBlock(position))
                list.add(position);
            if (!Util.isSolidBlock(Util.getSideLocationFromYaw(position, 4)))
                list.add(Util.getSideLocationFromYaw(position, 4));
            if (!Util.isSolidBlock(Util.getSideLocationFromYaw(position, -4)))
                list.add(Util.getSideLocationFromYaw(position, -4));

            position = Util.getFrontBackLocationFromYaw(position, 4);
        }

        return list;
    }

    public static String getRanking(UUID id, String circuitname) {
        if (!getCircuitSet().contains(circuitname))
            return null;
        try {
            //記憶されている周回数
            ArrayList<String> numberoflaps = new ArrayList<String>();
            for (String lap : config.getConfigurationSection(circuitname + ".laptime").getKeys(false)) {
                numberoflaps.add(lap);
            }

            if (numberoflaps.isEmpty()) {
                return null;
            }

            String ranking = "";

            //各週回数のデータ取得
            for (String lap : numberoflaps) {
                HashMap<UUID, Double> count = new HashMap<UUID, Double>();
                for (String path : config.getConfigurationSection(circuitname + ".laptime." + lap).getKeys(false)) {
                    count.put(UUID.fromString(path), config.getDouble(circuitname + ".laptime." + lap + "." + path));
                }

                if (count.size() < 1)
                    continue;

                //データを並び替える
                List<Map.Entry<UUID, Double>> entry = new ArrayList<Map.Entry<UUID, Double>>(count.entrySet());
                Collections.sort(entry, new Comparator<Map.Entry<UUID, Double>>() {
                    @Override
                    public int compare(Entry<UUID, Double> entry1, Entry<UUID, Double> entry2) {
                        return ((Double) entry2.getValue()).compareTo((Double) entry1.getValue());
                    }
                });

                ranking += "#DarkAqua====== " + "#Aqua" + circuitname.toUpperCase() + "#DarkAqua Running Race Ranking"
                        + " - #Aqua" + lap + " #DarkAquaLaps" + " #DarkAqua======\n";
                int ownrank = 0;
                double ownsec = 0;
                for (int rank = 1; rank <= entry.size(); rank++) {
                    if (rank <= 10) {
                        entry.get(entry.size() - rank);
                        ranking += "   #Yellow" + rank + ". #White"
                                + Bukkit.getOfflinePlayer(entry.get(entry.size() - rank).getKey()).getName() + " : "
                                + "#Yellow" + entry.get(entry.size() - rank).getValue() + " sec\n";
                    }
                    if (id.toString().equalsIgnoreCase(entry.get(entry.size() - rank).getKey().toString())) {
                        ownrank = rank;
                        ownsec = entry.get(entry.size() - rank).getValue();
                    }
                }

                if (ownrank != 0 && ownsec != 0)
                    ranking += "#White" + Bukkit.getOfflinePlayer(id).getName() + "#Greenさんの順位は#Yellow" + ownrank
                            + "位 : " + ownsec + " sec" + "#Greenです\n";
                else
                    ranking += "#White" + Bukkit.getOfflinePlayer(id).getName() + "#Greenさんのデータは存在しません";
            }

            return ranking;
        } catch (NullPointerException ex) {
        }
        return null;
    }

    public static String getKartRanking(UUID id, String circuitname) {
        if (!getCircuitSet().contains(circuitname))
            return null;
        try {
            //記憶されている周回数
            ArrayList<String> numberoflaps = new ArrayList<String>();
            for (String lap : config.getConfigurationSection(circuitname + ".kartlaptime").getKeys(false)) {
                numberoflaps.add(lap);
            }

            if (numberoflaps.isEmpty()) {
                return null;
            }

            String ranking = "";

            //各週回数のデータ取得
            for (String lap : numberoflaps) {
                HashMap<UUID, Double> count = new HashMap<UUID, Double>();
                for (String path : config.getConfigurationSection(circuitname + ".kartlaptime." + lap).getKeys(false)) {
                    count.put(UUID.fromString(path), config.getDouble(circuitname + ".kartlaptime." + lap + "." + path));
                }

                if (count.size() < 1)
                    continue;

                //データを並び替える
                List<Map.Entry<UUID, Double>> entry = new ArrayList<Map.Entry<UUID, Double>>(count.entrySet());
                Collections.sort(entry, new Comparator<Map.Entry<UUID, Double>>() {
                    @Override
                    public int compare(Entry<UUID, Double> entry1, Entry<UUID, Double> entry2) {
                        return ((Double) entry2.getValue()).compareTo((Double) entry1.getValue());
                    }
                });

                ranking += "#DarkAqua====== " + "#Aqua" + circuitname.toUpperCase() + "#DarkAqua Kart Race Ranking"
                        + " - #Aqua" + lap + " #DarkAquaLaps" + " #DarkAqua======\n";
                int ownrank = 0;
                double ownsec = 0;
                for (int rank = 1; rank <= entry.size(); rank++) {
                    if (rank <= 10) {
                        entry.get(entry.size() - rank);
                        ranking += "   #Yellow" + rank + ". #White"
                                + Bukkit.getOfflinePlayer(entry.get(entry.size() - rank).getKey()).getName() + " : "
                                + "#Yellow" + entry.get(entry.size() - rank).getValue() + " sec\n";
                    }
                    if (id.toString().equalsIgnoreCase(entry.get(entry.size() - rank).getKey().toString())) {
                        ownrank = rank;
                        ownsec = entry.get(entry.size() - rank).getValue();
                    }
                }
                if (ownrank != 0 && ownsec != 0)
                    ranking += "#White" + Bukkit.getOfflinePlayer(id).getName() + "#Greenさんの順位は#Yellow" + ownrank
                            + "位 : " + ownsec + " sec" + "#Greenです\n";
                else
                    ranking += "#White" + Bukkit.getOfflinePlayer(id).getName() + "#Greenさんのデータは存在しません";
            }

            return ranking;
        } catch (NullPointerException ex) {
        }
        return null;
    }

    public static Set<String> getCircuitSet() {
        return config.getKeys(false);
    }

    public static String getCircuitList() {
        String names = null;
        for (String circuitname : getCircuitSet()) {
            if (names == null)
                names = circuitname;
            else
                names += " , " + circuitname;
        }
        return names;
    }

    // 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static void setPosition(Player p, String circuitname) {
        if (!getCircuitSet().contains(circuitname)) {
            Message.invalidCircuit.sendMessage(p);
        } else {
            Location l = p.getLocation();
            config.set(circuitname + ".world", l.getWorld().getName());
            config.set(circuitname + ".x", l.getX());
            config.set(circuitname + ".y", l.getY());
            config.set(circuitname + ".z", l.getZ());
            config.set(circuitname + ".yaw", l.getYaw());
            config.set(circuitname + ".pitch", l.getPitch());
            Message.cmdCircuitSetPosition.sendMessage(p, circuitname);
            saveConfigFile();
        }
    }

    public static void setPosition(Player p, String circuitname, String worldname, double x, double y, double z,
            float yaw, float pitch) {
        if (!getCircuitSet().contains(circuitname)) {
            Message.invalidCircuit.sendMessage(p);
        } else {
            config.set(circuitname + ".world", worldname);
            config.set(circuitname + ".x", x);
            config.set(circuitname + ".y", y);
            config.set(circuitname + ".z", z);
            config.set(circuitname + ".yaw", yaw);
            config.set(circuitname + ".pitch", pitch);
            Message.cmdCircuitSetPosition.sendMessage(p, circuitname);
            saveConfigFile();
        }
    }

    public static void setNumberOfLaps(Player p, String circuitname, int amount) {
        if (!getCircuitSet().contains(circuitname)) {
            Message.invalidCircuit.sendMessage(p);
        } else {
            config.set(circuitname + ".numberoflaps", amount);
            Message.cmdCircuitSetLap.sendMessage(p, new Object[] { circuitname, amount });
            saveConfigFile();
        }
    }

    public static void setMinPlayer(Player p, String circuitname, int amount) {
        if (!getCircuitSet().contains(circuitname)) {
            Message.invalidCircuit.sendMessage(p);
        } else if (getMaxPlayer(circuitname) < amount) {
            Message.cmdCircuitOutOfMaxPlayer.sendMessage(p, new Object[] { circuitname, getMaxPlayer(circuitname) });
        } else {
            config.set(circuitname + ".minplayer", amount);
            Message.cmdCircuitSetMinPlayer.sendMessage(p, new Object[] { circuitname, amount });
            saveConfigFile();
        }
    }

    public static void setMaxPlayer(Player p, String circuitname, int amount) {
        if (!getCircuitSet().contains(circuitname)) {
            Message.invalidCircuit.sendMessage(p);
        } else if (amount < getMinPlayer(circuitname)) {
            Message.cmdCircuitOutOfMinPlayer.sendMessage(p, new Object[] { circuitname, getMinPlayer(circuitname) });
        } else {
            config.set(circuitname + ".maxplayer", amount);
            Message.cmdCircuitSetMaxPlayer.sendMessage(p, new Object[] { circuitname, amount });
            saveConfigFile();
        }
    }

    public static void setMatchingTime(Player p, String circuitname, int second) {
        if (!getCircuitSet().contains(circuitname)) {
            Message.invalidCircuit.sendMessage(p);
        } else {
            config.set(circuitname + ".matchingtime", second);
            Message.cmdCircuitSetMatchingTime.sendMessage(p, new Object[] { circuitname, second });
            saveConfigFile();
        }
    }

    public static void setMenuTime(Player p, String circuitname, int second) {
        if (!getCircuitSet().contains(circuitname)) {
            Message.invalidCircuit.sendMessage(p);
        } else {
            config.set(circuitname + ".menutime", second);
            Message.cmdCircuitSetMenuTime.sendMessage(p, new Object[] { circuitname, second });
            saveConfigFile();
        }
    }

    public static void setLimitTime(Player p, String circuitname, int second) {
        if (!getCircuitSet().contains(circuitname)) {
            Message.invalidCircuit.sendMessage(p);
        } else {
            config.set(circuitname + ".limittime", second);
            Message.cmdCircuitSetLimitTime.sendMessage(p, new Object[] { circuitname, second });
            saveConfigFile();
        }
    }

    public static void setBroadcastGoalMessage(Player p, String circuitname, boolean flag) {
        if (!getCircuitSet().contains(circuitname)) {
            Message.invalidCircuit.sendMessage(p);
        } else {
            config.set(circuitname + ".broadcastgoalmessage", flag);
            Message.cmdCircuitSetBroadcastGoalMessage.sendMessage(p, new Object[] { circuitname, flag });
            saveConfigFile();
        }
    }

    // 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static boolean isCircuit(String value) {
        for (String circuit : getCircuitSet()) {
            if (circuit.equalsIgnoreCase(value))
                return true;
        }
        return false;
    }

    // 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * Plugin.jarから/plugins/YPLKartディレクトリにコンフィグファイルをコピーする
     * ファイルが生成済みの場合は何もせずtrueを返す
     * ファイルが未生成の場合はファイルのコピーを試みる
     * @return ファイルの生成に成功したかどうか
     */
    public static boolean CreateConfig() {
        if (!(configFile.exists())) {
            //jarファイル内にコピー元のファイルが存在しない場合
            if (!Util.copyResource(filename)) {
                Message.sendAbsolute(null, "[" + YPLKart.PLUGIN_NAME + "] v."
                        + YPLKart.PLUGIN_VERSION + " "
                        + filename + " was not found in jar file");
                YPLKart.getInstance().onDisable();
                return false;
            }

            //jarファイル内からファイルのコピーに成功した場合
            configFile = new File(datafolder, filename);
            config = YamlConfiguration.loadConfiguration(configFile);
        }

        return true;
    }

    public static File getConfigFile() {
        return configFile;
    }

    public static FileConfiguration getConfig() {
        return config;
    }

    public static void saveConfigFile() {
        saveFile(configFile, config);
    }

    public static void saveFile(File file, FileConfiguration config) {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
