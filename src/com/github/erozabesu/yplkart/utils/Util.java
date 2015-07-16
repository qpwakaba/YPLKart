package com.github.erozabesu.yplkart.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.EntityEffect;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.YPLKart;
import com.github.erozabesu.yplkart.data.Messages;
import com.github.erozabesu.yplkart.object.Circuit;
import com.github.erozabesu.yplkart.object.Race;
import com.github.erozabesu.yplkart.task.FlowerShowerTask;
import com.github.erozabesu.yplkart.task.SendBlinkingTitleTask;

public class Util extends ReflectionUtil {
    private static Class<?> nmsBlock = getNMSClass("Block");
    private static Class<?> nmsMaterial = getNMSClass("Material");

    private static Method static_nmsBlock_getById;
    private static Method block_getMaterial;
    private static Method material_isSolid;

    public Util() {
        try {
            static_nmsBlock_getById = nmsBlock.getMethod("getById", int.class);
            block_getMaterial = nmsBlock.getMethod("getMaterial");
            material_isSolid = nmsMaterial.getMethod("isSolid");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * value以下の乱数を生成する
     * 生成した乱数は50％の確率で負の値に変換される
     * @param value 乱数の上限・下限数値
     * @return 生成した乱数
     */
    public static int getRandom(int value) {
        int newvalue;
        Random random = new Random();
        newvalue = random.nextInt(10) < 5 ? random.nextInt(value) : -random.nextInt(value);
        return newvalue;
    }

    /**
     * textに含まれるChatColorを返す
     * 複数のChatColorが含まれていた場合文字列の最後に適応されているChatColorを返す
     * ChatColorが含まれていない場合はChatColor.WHITEを返す
     * @param text ChatColorを抽出する文字列
     * @return 抽出されたChatColor
     */
    public static ChatColor getChatColorFromText(String text) {
        String color = ChatColor.getLastColors(text);
        if (color == null)
            return ChatColor.WHITE;
        if (color.length() == 0)
            return ChatColor.WHITE;

        return ChatColor.getByChar(color.substring(1));
    }

    /**
     * Location fromからtoへのVectorを返す
     * @param from
     * @param to
     * @return Location fromからtoへのVector
     */
    public static Vector getVectorToLocation(Location from, Location to) {
        Vector vector = to.toVector().subtract(from.toVector());
        return vector.normalize();
    }

    /**
     * vectorから偏揺れ角Yawを算出し返す
     * 偏揺れ角はLocation型変数に用いられる水平方向の向きを表す方位角
     * @see org.bukkit.Location
     * @param vector ベクター
     * @return 偏揺れ角
     */
    public static float getYawFromVector(Vector vector) {
        double dx = vector.getX();
        double dz = vector.getZ();
        double yaw = 0;
        if (dx != 0) {
            if (dx < 0) {
                yaw = 1.5 * Math.PI;
            } else {
                yaw = 0.5 * Math.PI;
            }
            yaw -= Math.atan(dz / dx);
        } else if (dz < 0) {
            yaw = Math.PI;
        }
        return (float) (-yaw * 180 / Math.PI - 90);
    }

    /**
     * locationを基点に直下の最も近い固形Blockを返す
     * @param location 基点となる座標
     * @return Block
     */
    public static Block getGroundBlock(Location location) {
        for (int i = 0; i <= 5; i++) {
            if (isSolidBlock(location)) {
                return location.getBlock();
            }
            location.add(0, -1, 0);
        }
        return null;
    }

    /**
     * locationを基点に直下の最も近い固形BlockのIDを返す
     * @param location 基点となる座標
     * @return BlockのID、データのString
     */
    public static String getGroundBlockID(Location location) {
        Block block = getGroundBlock(location);
        if (block == null) {
            return "0:0";
        }

        return String.valueOf(block.getTypeId()) + ":" + String.valueOf(block.getData());
    }

    /**
     * locationを基点に直下の最も近い固形BlockのMaterialを返す
     * @param location 基点となる座標
     * @return BlockのMaterial
     */
    public static Material getGroundBlockMaterial(Location location) {
        Block block = getGroundBlock(location);
        if (block == null) {
            return Material.AIR;
        }

        return block.getType();
    }

    /**
     * locationを基点にoffsetの数値だけ前後に移動した座標を返す
     * offsetが正の数値であれば前方、負の数値であれば後方へ移動する
     * 前後の方角はlocationの偏揺れ角yawから算出している
     * @param location 基点となる座標
     * @param offset オフセット
     * @return offsetの数値だけ前後に移動した座標
     */
    public static Location getFrontBackLocationFromYaw(Location location, double offset) {
        Location adjustlocation = adjustBlockLocation(location);
        float yaw = adjustlocation.getYaw();
        double x = -Math.sin(Math.toRadians(yaw < 0 ? yaw + 360 : yaw));
        double z = Math.cos(Math.toRadians(yaw < 0 ? yaw + 360 : yaw));

        Location newlocation = new Location(adjustlocation.getWorld(),
                adjustlocation.getX() + x * offset,
                adjustlocation.getY(),
                adjustlocation.getZ() + z * offset,
                adjustlocation.getPitch(), yaw);
        return newlocation;
    }

    /**
     * locationを基点にoffsetの数値だけ左右に移動した座標を返す
     * offsetが正の数値であれば左方、負の数値であれば右方へ移動する
     * 左右の方角はlocationの偏揺れ角yawから算出している
     * @param location 基点となる座標
     * @param offset オフセット
     * @return offsetの数値だけ左右に移動した座標
     */
    public static Location getSideLocationFromYaw(Location location, double offset) {
        Location adjustlocation = adjustBlockLocation(location);
        float yaw = adjustlocation.getYaw();
        double x = Math.cos(Math.toRadians(yaw));
        double z = Math.sin(Math.toRadians(yaw));

        Location newlocation = new Location(adjustlocation.getWorld(),
                adjustlocation.getX() + x * offset,
                adjustlocation.getY(),
                adjustlocation.getZ() + z * offset,
                adjustlocation.getPitch(), yaw);
        return newlocation;
    }

    //TODO 負荷の原因になっている可能性あり
    public static ArrayList<Entity> getNearbyEntities(Location l, double radius) {
        ArrayList<Entity> entities = new ArrayList<Entity>();
        for (Entity entity : l.getWorld().getEntities()) {
            if (l.distanceSquared(entity.getLocation()) <= radius * radius) {
                entities.add(entity);
            }
        }
        return entities;
    }

    //TODO 負荷の原因になっている可能性あり
    public static ArrayList<LivingEntity> getNearbyLivingEntities(Location l, double radius) {
        ArrayList<Entity> entity = getNearbyEntities(l, radius);
        ArrayList<LivingEntity> livingentity = new ArrayList<LivingEntity>();
        for (Entity e : entity) {
            if (e instanceof LivingEntity)
                livingentity.add((LivingEntity) e);
        }
        return livingentity;
    }

    //TODO 負荷の原因になっている可能性あり
    public static ArrayList<Player> getNearbyPlayers(Location l, double radius) {
        ArrayList<Entity> entity = getNearbyEntities(l, radius);
        ArrayList<Player> livingentity = new ArrayList<Player>();
        for (Entity e : entity) {
            if (e instanceof Player)
                livingentity.add((Player) e);
        }
        return livingentity;
    }

    //TODO 負荷の原因になっている可能性あり
    public static Entity getNearestEntity(List<Entity> entities, Location l) {
        Iterator<Entity> i = entities.iterator();
        Entity e = null;
        Entity temp;
        while (i.hasNext()) {
            temp = i.next();
            if (e != null)
                if (e.getWorld().getName().equalsIgnoreCase(temp.getWorld().getName()))
                    if (e.getLocation().distance(l) < temp.getLocation().distance(l))
                        continue;
            e = temp;
        }

        return e;
    }

    //TODO 負荷の原因になっている可能性あり
    public static Player getNearestPlayer(ArrayList<Player> players, Location l) {
        if (players == null)
            return null;

        Iterator<Player> i = players.iterator();
        Player p = null;
        Player temp;
        while (i.hasNext()) {
            temp = i.next();
            if (p != null)
                if (p.getWorld().getName().equalsIgnoreCase(temp.getWorld().getName()))
                    if (p.getLocation().distance(l) < temp.getLocation().distance(l))
                        continue;
            p = temp;
        }

        return p;
    }

    /**
     * passengerが搭乗している全Entityを返す
     * passengerが搭乗しているEntityが更に別のEntityに搭乗している場合を指す
     * @param passenger
     * @return passengerが搭乗している全EntityのList
     */
    public static List<Entity> getAllVehicle(Entity passenger) {
        List<Entity> entitylist = new ArrayList<Entity>();

        Entity vehicle = passenger.getVehicle() != null ? passenger.getVehicle() : null;
        while (vehicle != null) {
            entitylist.add(vehicle);
            vehicle = vehicle.getVehicle() != null ? vehicle.getVehicle() : null;
        }

        return entitylist;
    }

    /**
     * vehicleに搭乗している全Entityを返す
     * vehicleに搭乗しているEntityが更に別のEntityに搭乗されている場合を指す
     * @param vehicle
     * @return vehicleに搭乗している全EntityのList
     */
    public static ArrayList<Entity> getAllPassenger(Entity vehicle) {
        ArrayList<Entity> entitylist = new ArrayList<Entity>();

        Entity passenger = vehicle.getPassenger() != null ? vehicle.getPassenger() : null;
        while (passenger != null) {
            entitylist.add(passenger);
            passenger = passenger.getPassenger() != null ? passenger.getPassenger() : null;
        }
        return entitylist;
    }

    //〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static void setPotionEffect(Player p, PotionEffectType effect, int second, int level) {
        p.removePotionEffect(effect);
        p.addPotionEffect(new PotionEffect(effect, second * 20, level));
    }

    public static void setItemDecrease(Player p) {
        int i = p.getItemInHand().getAmount();
        if (i == 1) {
            p.setItemInHand(null);
        } else if (i > 1) {
            p.getItemInHand().setAmount(i - 1);
        }
    }

    //〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static Boolean isOnline(String name) {
        return Bukkit.getPlayerExact(name) != null;
    }

    public static Boolean isOnline(UUID id) {
        return Bukkit.getPlayer(id) != null;
    }

    public static boolean isNumber(String number) {
        try {
            Integer.parseInt(number);
            return true;
        } catch (NumberFormatException e) {
            try {
                Float.parseFloat(number);
                return true;
            } catch (NumberFormatException ee) {
                try {
                    Double.parseDouble(number);
                    return true;
                } catch (NumberFormatException eee) {
                    return false;
                }
            }
        }
    }

    public static boolean isBoolean(String flag) {
        if (flag.equalsIgnoreCase("true") || flag.equalsIgnoreCase("false"))
            return true;
        return false;
    }

    public static boolean isLoadedChunk(Location l) {
        Player player = getNearestPlayer(getNearbyPlayers(l, 100), l);
        if (player == null)
            return false;

        return true;
    }

    /**
     * 引数locationのBlockが固形Blockか判別する
     * @param location 判別するBlockの座標
     * @return 固形Blockかどうか
     */
    public static Boolean isSolidBlock(Location location) {
        try {
            return (Boolean) material_isSolid.invoke(
                    block_getMaterial.invoke(
                            static_nmsBlock_getById.invoke(null, location.getBlock().getTypeId())));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Boolean isSlabBlock(Location l) {
        int id = l.getBlock().getTypeId();
        if (id == 44 || id == 126 || id == 182)
            return true;
        return false;
    }

    //〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static void addDamage(Entity damaged, Entity executor, int damage) {
        if (!(damaged instanceof LivingEntity))
            return;
        if (damaged instanceof Player) {
            final Player p = (Player) damaged;
            if (0 < p.getNoDamageTicks())
                return;
            if (!RaceManager.isRacing(p.getUniqueId()))
                return;

            p.playEffect(EntityEffect.HURT);

            if (1 <= p.getHealth() - damage) {
                p.setHealth(p.getHealth() - damage);
            } else {
                Circuit c = RaceManager.getCircuit(p.getUniqueId());
                p.setHealth(p.getMaxHealth());
                if (executor != null)
                    c.sendMessageEntryPlayer(Messages.racePlayerKill, new Object[] { c,
                            new Player[] { (Player) damaged, (Player) executor } });
                else
                    c.sendMessageEntryPlayer(Messages.racePlayerDead, new Object[] { c, (Player) damaged });

                final Race r = RaceManager.getRace(p);

                p.setWalkSpeed(r.getCharacter().getDeathPenaltyWalkSpeed());
                p.setNoDamageTicks(r.getCharacter().getDeathPenaltyAntiReskillSecond() * 20);
                p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 0.5F);
                r.setDeathPenaltyTask(
                        Bukkit.getScheduler().runTaskLater(YPLKart.getInstance(), new Runnable() {
                            public void run() {
                                p.setWalkSpeed(r.getCharacter().getWalkSpeed());
                                p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
                                p.setSprinting(true);
                            }
                        }, r.getCharacter().getDeathPenaltySecond() * 20)
                        );
                r.setDeathPenaltyTitleSendTask(
                        new SendBlinkingTitleTask((Player) damaged, r.getCharacter().getDeathPenaltySecond(),
                                Messages.titleDeathPanalty.getMessage()).runTaskTimer(YPLKart.getInstance(), 0, 1)
                        );
            }
        } else {
            ((LivingEntity) damaged).damage(damage, executor);
        }
    }

    /**
     * entityの物理判定を無効にする
     * 無効に設定されたEntityはBlockへの接触判定が行われない
     * @param entity
     */
    public static void removeEntityCollision(Entity entity) {
        Object craftentity = getCraftEntity(entity);
        Field noclip = getField(craftentity, "noclip");
        if (noclip != null) {
            try {
                noclip.setBoolean(craftentity, true);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //TODO 負荷の原因になっている可能性あり
    public static void removeEntityVerticalMotion(Entity e) {
        try {
            Object craftentity = getCraftEntity(e);
            Field nmsField;
            for (Field f : craftentity.getClass().getFields()) {
                if (!f.getName().equalsIgnoreCase("motY"))
                    continue;
                nmsField = f;
                nmsField.setAccessible(true);
                nmsField.setDouble(craftentity, f.getDouble(craftentity) + 0.03999999910593033D);
                return;
            }
        } catch (Exception exception) {
        }
    }

    public static Location adjustBlockLocation(Location old) {
        Location value = old.getBlock().getLocation();
        return new Location(old.getWorld(), value.getX() + 0.5, value.getY(), value.getZ() + 0.5, old.getYaw(),
                old.getPitch());
    }

    //〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static String convertSignNumber(int number) {
        return 0 <= number ? "<gold>+" + String.valueOf(number) : "<blue>" + String.valueOf(number);
    }

    public static String convertSignNumber(float number) {
        return 0 <= number ? "<gold>+" + String.valueOf(number) : "<blue>" + String.valueOf(number);
    }

    public static String convertSignNumber(double number) {
        return 0 <= number ? "<gold>+" + String.valueOf(number) : "<blue>" + String.valueOf(number);
    }

    public static String convertSignNumberR(int number) {
        String text = "";
        if (number == 0)
            text = "<gold>+" + String.valueOf(number);
        else
            text = 0 < number ? "<blue>+" + String.valueOf(number) : "<gold>" + String.valueOf(number);
        return text;
    }

    public static String convertSignNumberR(float number) {
        String text = "";
        if (number == 0)
            text = "<gold>+" + String.valueOf(number);
        else
            text = 0 < number ? "<blue>+" + String.valueOf(number) : "<gold>" + String.valueOf(number);
        return text;
    }

    public static String convertSignNumberR(double number) {
        String text = "";
        if (number == 0)
            text = "<gold>+" + String.valueOf(number);
        else
            text = 0 < number ? "<blue>+" + String.valueOf(number) : "<gold>" + String.valueOf(number);
        return text;
    }

    public static String convertInitialUpperString(String string) {
        String initial = string.substring(0, 1).toUpperCase();
        String other = string.substring(1, string.length()).toLowerCase();

        return initial + other;
    }

    //〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    //ブロック、非生物エンティティに影響しない爆発を発生
    public static void createSafeExplosion(Player executor, Location l, int damage, int range) {
        Particle.sendToLocation("CLOUD", l, 0, 0, 0, 1, 10);
        Particle.sendToLocation("SMOKE_NORMAL", l, 0, 0, 0, 1, 10);
        l.getWorld().playSound(l, Sound.EXPLODE, 0.2F, 1.0F);
        ArrayList<LivingEntity> entities = Util.getNearbyLivingEntities(l, range);
        for (LivingEntity damaged : entities) {
            if (executor != null)
                if (damaged.getUniqueId() == executor.getUniqueId())
                    continue;
            if (0 < damaged.getNoDamageTicks())
                continue;
            if (damaged.isDead())
                continue;
            if (!(damaged instanceof Player))
                continue;
            if (!RaceManager.isRacing(((Player) damaged).getUniqueId()))
                continue;

            Vector v = Util.getVectorToLocation(damaged.getLocation(), l);
            v.setX(v.clone().multiply(-1).getX());
            v.setY(0);
            v.setZ(v.clone().multiply(-1).getZ());
            damaged.setVelocity(v);

            addDamage(damaged, executor, damage);
        }
    }

    public static void createFlowerShower(Player p, int maxlife) {
        new FlowerShowerTask(p, maxlife).runTaskTimer(YPLKart.getInstance(), 0, 1);
    }

    public static void createSignalFireworks(Location l) {
        World w = l.getWorld();
        FireworkEffect effect = FireworkEffect.builder().withColor(Color.GREEN).withFade(Color.LIME)
                .with(Type.BALL_LARGE).build();
        FireworkEffect effect2 = FireworkEffect.builder().withColor(Color.YELLOW).withFade(Color.ORANGE)
                .with(Type.STAR).build();
        FireworkEffect effect3 = FireworkEffect.builder().withColor(Color.RED).withFade(Color.PURPLE)
                .with(Type.CREEPER).build();
        FireworkEffect effect4 = FireworkEffect.builder().withColor(Color.AQUA).withFade(Color.BLUE).with(Type.BURST)
                .build();

        for (int i = 0; i <= 2; i++) {
            try {
                FireWorks.playFirework(w, l.clone().add(Util.getRandom(20), 5, Util.getRandom(20)), effect);
                FireWorks.playFirework(w, l.clone().add(Util.getRandom(20), 5, Util.getRandom(20)), effect2);
                FireWorks.playFirework(w, l.clone().add(Util.getRandom(20), 5, Util.getRandom(20)), effect3);
                FireWorks.playFirework(w, l.clone().add(Util.getRandom(20), 5, Util.getRandom(20)), effect4);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /*
     * 各OSのデフォルトの文字コードに変換しリソースを保管します
     */
    public static boolean copyResource(String filename) {
        String filepath = "resources/" + filename;
        File outputFile = new File(YPLKart.getInstance().getDataFolder() + File.separator + filename);
        InputStream input = null;
        FileOutputStream output = null;
        BufferedReader reader = null;
        BufferedWriter writer = null;

        File parent = outputFile.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }

        try {
            input = YPLKart.getInstance().getResource(filepath);

            if (input == null) {
                return false;
            }

            output = new FileOutputStream(outputFile);
            reader = new BufferedReader(new InputStreamReader(input, "Shift_JIS"));
            writer = new BufferedWriter(new OutputStreamWriter(output, Charset.defaultCharset()));

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
            writer.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    // Do nothing
                }
            }
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    // Do nothing
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    // Do nothing
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // Do nothing
                }
            }
        }
    }

    /*
     * 各OSのデフォルトの文字コードに変換し、コンフィグを最新の内容に上書き保存します
     */
    public static void saveConfiguration(File file, FileConfiguration config, List<String> ignorelist) {
        //コンフィグファイルが無い場合新規作成します
        if (!file.exists()) {
            if (!copyResource(file.getName()))
                return;
            file = new File(YPLKart.getInstance().getDataFolder(), file.getName());
        }
        List<String> continuetext = new ArrayList<String>();
        FileInputStream input = null;
        FileOutputStream output = null;
        BufferedReader reader = null;
        BufferedWriter writer = null;

        try {
            input = new FileInputStream(new File(YPLKart.getInstance().getDataFolder(), file.getName()));
            reader = new BufferedReader(new InputStreamReader(input, Charset.defaultCharset()));

            //新しいファイルにコメントアウトしている行のみ引き継ぐ
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#"))
                    continuetext.add(line);
            }

            file.delete();
            file.createNewFile();

            file.createNewFile();
            output = new FileOutputStream(file);
            writer = new BufferedWriter(new OutputStreamWriter(output, Charset.defaultCharset()));

            for (String continueline : continuetext) {
                writer.write(continueline);
                writer.newLine();
            }

            boolean continueflag = false;
            for (String configkey : config.getKeys(true)) {
                continueflag = false;
                for (String ignore : ignorelist) {
                    if (configkey.startsWith(ignore)) {
                        continueflag = true;
                        break;
                    }
                }
                if (continueflag)
                    continue;

                writer.write(configkey + " : \"" + config.get(configkey) + "\"");
                writer.newLine();
            }

            writer.flush();
        } catch (Exception e) {
            //e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    // Do nothing
                }
            }
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    // Do nothing
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    // Do nothing
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // Do nothing
                }
            }
        }
    }
}