package com.github.erozabesu.yplkart.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.material.MaterialData;

import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.YPLKart;
import com.github.erozabesu.yplkart.enumdata.EnumKarts;
import com.github.erozabesu.yplkart.utils.Util;

public final class DisplayKartData {
    public static YPLKart pl;

    private static String filename = "displaykart.yml";
    private static File datafolder;
    private static File configFile;
    private static FileConfiguration config;

    private static boolean EnableThisPlugin = true;
    public static boolean EnableScoreboard = true;
    public static List<String> DisWorlds;

    public DisplayKartData(YPLKart plugin) {
        pl = plugin;
        datafolder = pl.getDataFolder();

        configFile = new File(datafolder, filename);
        config = YamlConfiguration.loadConfiguration(configFile);

        if (!CreateConfig()) {
            return;
        }
    }

    /* <UUID>:
     *   type: enumkart material
     *   data: enumkart material bytedata
     *   world: world
     *   x: double
     *   y: double
     *   z: double
     *   yaw: double
     *   pitch: double
     * */

    public static void createData(String uuid, EnumKarts kart, Location l) {
        config.set(uuid + ".type", kart.getDisplayBlock().name());
        config.set(uuid + ".data", kart.getDisplayData());
        config.set(uuid + ".world", l.getWorld().getName());
        config.set(uuid + ".x", l.getX());
        config.set(uuid + ".y", l.getY());
        config.set(uuid + ".z", l.getZ());
        config.set(uuid + ".yaw", l.getYaw());
        config.set(uuid + ".pitch", l.getPitch());

        saveConfigFile();
    }

    public static void respawnKart(Chunk chunk) {
        for (String key : config.getKeys(false)) {
            if (!chunk.getWorld().getName().equalsIgnoreCase(getWorld(key)))
                continue;
            if (!chunk.toString().equalsIgnoreCase(getLocation(key).getChunk().toString()))
                continue;
            boolean flag = true;
            for (Entity e : chunk.getEntities()) {
                if (e.getCustomName() == null)
                    continue;
                if (ChatColor.stripColor(e.getCustomName()).equalsIgnoreCase(key)) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                Minecart cart = RaceManager.createDisplayMinecart(getLocation(key), EnumKarts.KART1, key);
                cart.setDisplayBlock(new MaterialData(getType(key), getData(key)));
            }
        }
    }

    public static void respawnKart(World w) {
        for (String key : config.getKeys(false)) {
            if (w.getName().equalsIgnoreCase(getWorld(key))) {
                boolean flag = true;
                for (Entity e : getLocation(key).getChunk().getEntities()) {
                    if (e.getCustomName() == null)
                        continue;
                    if (ChatColor.stripColor(e.getCustomName()).equalsIgnoreCase(key)) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    Minecart cart = RaceManager.createDisplayMinecart(getLocation(key), EnumKarts.KART1, key);
                    cart.setDisplayBlock(new MaterialData(getType(key), getData(key)));
                }
            }
        }
    }

    public static List<String> getList() {
        List<String> list = new ArrayList<String>();
        for (String key : config.getKeys(false)) {
            list.add(key);
        }
        return list;
    }

    public static Material getType(String uuid) {
        if (config.getString(uuid + ".type") == null)
            return Material.STONE;
        if (Material.getMaterial(config.getString(uuid + ".type")) == null)
            return Material.STONE;

        return Material.getMaterial(config.getString(uuid + ".type"));
    }

    public static byte getData(String uuid) {
        return (byte) config.getInt(uuid + ".data", (byte) 0);
    }

    public static String getWorld(String uuid) {
        if (!config.contains(uuid))
            return null;

        return config.getString(uuid + ".world");
    }

    public static Location getLocation(String uuid) {
        if (!config.contains(uuid))
            return null;

        return new Location(Bukkit.getWorld(config.getString(uuid + ".world"))
                , config.getDouble(uuid + ".x")
                , config.getDouble(uuid + ".y")
                , config.getDouble(uuid + ".z")
                , (float) config.getDouble(uuid + ".yaw")
                , (float) config.getDouble(uuid + ".pitch"));
    }

    public static List<Location> getListLocation() {
        List<Location> list = new ArrayList<Location>();
        for (String key : config.getKeys(false)) {
            list.add(getLocation(key));
        }
        return list;
    }

    public static boolean deleteData(String uuid) {
        for (String key : config.getKeys(false)) {
            if (key.equalsIgnoreCase(uuid)) {
                config.set(key, null);
                saveConfigFile();
                return true;
            }
        }
        return false;
    }

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
                Messages.sendAbsolute(null, "[" + YPLKart.PLUGIN_NAME + "] v."
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

    //〓〓	ファイル取得		〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓
    public static File getConfigFile() {
        return configFile;
    }

    //〓〓	コンフィグ取得		〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓
    public static FileConfiguration getConfig() {
        return config;
    }

    //〓〓	ファイル保存		〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓
    public static void saveConfigFile() {
        saveFile(configFile, config);
    }

    //〓〓	ファイル保存実行		〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓
    public static void saveFile(File file, FileConfiguration config) {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
