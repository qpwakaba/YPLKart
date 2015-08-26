package com.github.erozabesu.yplkart.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.YPLKart;
import com.github.erozabesu.yplkart.data.MessageEnum;
import com.github.erozabesu.yplkart.enumdata.Particle;
import com.github.erozabesu.yplkart.object.Circuit;
import com.github.erozabesu.yplkart.reflection.Classes;
import com.github.erozabesu.yplkart.reflection.Constructors;
import com.github.erozabesu.yplkart.reflection.Fields;
import com.github.erozabesu.yplkart.reflection.Methods;
import com.github.erozabesu.yplkart.task.FlowerShowerTask;

public class Util extends ReflectionUtil {

    //〓 Getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

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
     * 引数locationを基点に、引数heightの数値だけ下方の最も近い固形Blockを返す。
     * @param location 基点となる座標
     * @param height 高さ
     * @return 直下の固形ブロック。固形ブロックが存在しない場合は{@code null}を返す。
     */
    public static Block getGroundBlock(Location location, int height) {
        Location cloneLocation = location.clone();
        for (int i = 0; i <= height; i++) {
            if (isSolidBlock(cloneLocation)) {
                return cloneLocation.getBlock();
            }
            cloneLocation.add(0, -1, 0);
        }
        return null;
    }

    /**
     * locationを基点に、引数heightの数値だけ下方の最も近い固形BlockのIDを返す。
     * @param location 基点となる座標
     * @param height 高さ
     * @return BlockのID、データのString
     */
    public static String getGroundBlockID(Location location, int height) {
        Block block = getGroundBlock(location, height);
        if (block == null) {
            return "0:0";
        }

        return String.valueOf(block.getTypeId()) + ":" + String.valueOf(block.getData());
    }

    /**
     * locationを基点に、引数heightの数値だけ下方の最も近い固形BlockのMaterialを返す
     * @param location 基点となる座標
     * @param height 高さ
     * @return BlockのMaterial
     */
    public static Material getGroundBlockMaterial(Location location, int height) {
        Block block = getGroundBlock(location, height);
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
    public static Location getForwardLocationFromYaw(Location location, double offset) {
        Vector direction = location.getDirection();
        double x = direction.getX();
        double z = direction.getZ();

        return new Location(location.getWorld(),
                location.getX() + x * offset,
                location.getY(),
                location.getZ() + z * offset,
                location.getYaw(), location.getPitch());
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

        return new Location(adjustlocation.getWorld(),
                adjustlocation.getX() + x * offset,
                adjustlocation.getY(),
                adjustlocation.getZ() + z * offset,
                yaw, adjustlocation.getPitch());
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
     * @param entity
     * @return 引数entityが搭乗している一番下のエンティティ
     */
    public static Entity getEndVehicle(Entity entity) {
        Entity vehicle = entity.getVehicle();

        if (vehicle == null) {
            return entity;
        }

        while (vehicle.getVehicle() != null) {
            vehicle = vehicle.getVehicle();
        }

        return vehicle;
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
     * @param entity
     * @return 引数entityが搭乗している一番上のエンティティ
     */
    public static Entity getEndPassenger(Entity entity) {
        Entity passenger = entity.getPassenger();

        if (passenger == null) {
            return entity;
        }

        while (passenger.getPassenger() != null) {
            passenger = passenger.getPassenger();
        }

        return passenger;
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

    //〓 Reflection Getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static Location getStandingLocationByBoudingBox(Entity entity) {
        Location location = entity.getLocation();
        Object boudingBox = ReflectionUtil.invoke(Methods.nmsEntity_getBoundingBox, entity);

        location.setX((Double) ReflectionUtil.getFieldValue(Fields.nmsEntity_locX, entity));
        location.setY((Double) ReflectionUtil.getFieldValue(Fields.nmsAxisAlignedBB_locYBottom, boudingBox));
        location.setZ((Double) ReflectionUtil.getFieldValue(Fields.nmsEntity_locZ, entity));

        return location;
    }

    public static Object getBlockPosition(Location location) {
        double locX = Math.floor(location.getX());
        double locY = Math.floor(location.getY());
        double locZ = Math.floor(location.getZ());

        return ReflectionUtil.newInstance(Constructors.nmsBlockPosition, locX, locY, locZ);
    }

    /**
     * 引数locationのブロックがよじ登ることができるブロックかどうかを判別する
     * @param location 座標
     * @return よじ登ることができるブロックかどうか
     */
    public static boolean isClimbableBlock(Location location) {
        Material blockMaterial = location.getBlock().getType();
        return (blockMaterial == Material.LADDER || blockMaterial == Material.VINE);
    }

    public static Object getCraftEntity(Entity entity) {
        String className = entity.getClass().getSimpleName();
        Method getHandle = Methods.craftEntity_getHandle.get(className);

        if (getHandle == null) {
            getHandle = getMethod(entity.getClass(), "getHandle");
            Methods.craftEntity_getHandle.put(className, getHandle);
        }

        return invoke(getHandle, entity);
    }

    public static Object getNewCraftEntityFromClass(World world, Class<?> nmsEntityClass) {
        Constructor<?> constructor = Constructors.nmsEntity_Constructor.get(nmsEntityClass.getSimpleName());

        if (constructor == null) {
            constructor = getConstructor(nmsEntityClass, Classes.nmsWorld);
            Constructors.nmsEntity_Constructor.put(nmsEntityClass.getSimpleName(), constructor);
        }

        return newInstance(constructor, invoke(Methods.craftWorld_getHandle, world));
    }

    public static Object getNewCraftEntityFromClassName(World world, String classname) {
        Constructor<?> constructor = Constructors.nmsEntity_Constructor.get(classname);

        if (constructor == null) {
            constructor = getConstructor(getNMSClass(classname), Classes.nmsWorld);
            Constructors.nmsEntity_Constructor.put(classname, constructor);
        }

        return newInstance(constructor, invoke(Methods.craftWorld_getHandle, world));
    }

    /**
     * 引数nmsEntityオブジェクトからBukkitEntityを取得し返す
     * @param nmsEntity BukkitEntityを取得するNmsEntity
     * @return BukkitEntity
     */
    public static Entity getBukkitEntityFromNmsEntity(Object nmsEntity) {
        return (Entity) invoke(Methods.nmsEntity_getBukkitEntity, nmsEntity);
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

    @Deprecated
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

    /**
     * 引数valueが数値、booleanに一致しない文字列かどうかを返す。
     * @param value チェックする文字列
     * @return 引数valueが数値、booleanに一致しない文字列かどうかを返す。
     */
    public static boolean isString(String value) {
        return !isInteger(value) && !isFloat(value) && !isBoolean(value);
    }

    /**
     * 引数valueがIntegerに変換できるかどうかを返す
     * @param value 調べるString
     * @return 引数valueがIntegerに変換できるかどうか
     */
    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 引数valueがFloatに変換できるかどうかを返す。
     * @param value チェックする文字列
     * @return 引数valueがFloatに変換できるかどうか
     */
    public static boolean isFloat(String value) {
        try {
            Float.parseFloat(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 引数valueがBooleanに変換できるかどうかを返す。
     * @return 引数valueがBooleanに変換できるかどうか
     */
    public static boolean isBoolean(String value) {
        if (value.equals("true") || value.equals("false")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isPlayer(Object object) {
        return (object instanceof Player);
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
        Object nmsBlock = invoke(Methods.static_nmsBlock_getById, null, location.getBlock().getTypeId());
        Object nmsMaterial = invoke(Methods.nmsBlock_getMaterial, nmsBlock);
        return (Boolean) invoke(Methods.nmsMaterial_isSolid, nmsMaterial);
    }

    public static Boolean isSlabBlock(Location l) {
        int id = l.getBlock().getTypeId();
        if (id == 44 || id == 126 || id == 182)
            return true;
        return false;
    }

    //〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 引数damagedに引数damageの値だけダメージを与える。<br>
     * 引数damagedがプレイヤー、かつレース中の場合、デスポーンしないよう仮想的に死亡状態を演出する。
     * @param damaged ダメージを受けたエンティティ
     * @param executor ダメージを与えたエンティティ、もしくはnull
     * @param damage ダメージ値
     */
    public static void addDamage(Entity damaged, Entity executor, int damage) {
        /*
         * ダメージが0以下の場合return
         * キャラクターの妨害アイテム威力補正の影響でアイテムの威力が0以下になる場合があるため
         */
        if (damage <= 0) {
            return;
        }

        //非生物エンティティは除外
        if (!(damaged instanceof LivingEntity)) {
            return;
        }

        LivingEntity damagedLiving = ((LivingEntity) damaged);

        //無敵状態の場合は除外
        if (0 < damagedLiving.getNoDamageTicks()) {
            return;
        }

        //被ダメージエンティティがプレイヤーの場合
        if (damagedLiving instanceof Player) {
            Player player = (Player) damagedLiving;

            //レース中でない場合は通常通りのダメージ処理
            if (!RaceManager.isStillRacing(player.getUniqueId())) {
                player.damage(damage, executor);

            //レース中の場合、デスポーンしないよう仮想的に死亡した演出を行う
            } else {
                player.playEffect(EntityEffect.HURT);

                //体力がダメージを上回っている
                if (damage < player.getHealth()) {
                    double newHealth = player.getHealth() -  damage;
                    player.setHealth(newHealth);

                //ダメージが体力を上回っている
                } else {
                    Circuit circuit = RaceManager.getCircuit(player.getUniqueId());

                    //体力を最大値まで回復
                    player.setHealth(player.getMaxHealth());

                    //攻撃実行者が明確な場合、レース参加者に他殺のデスログを送信
                    if (executor != null) {
                        Player[] messagePartsPlayers = new Player[]{(Player) damaged, (Player) executor};
                        circuit.sendMessageEntryPlayer(MessageEnum.racePlayerKill, circuit, messagePartsPlayers);

                    //攻撃実行者が不明の場合、レース参加者に死亡したことのみを伝えるデスログを送信
                    } else {
                        circuit.sendMessageEntryPlayer(MessageEnum.racePlayerDead
                                , new Object[] { circuit, (Player) damaged });
                    }

                    //プレイヤーにデスペナルティを適用
                    RaceManager.getRacer(player).applyDeathPenalty();
                }

                //連続してダメージを受けないようプレイヤーを少しの間無敵にする
                player.setNoDamageTicks(5);
            }

        //被ダメージエンティティがプレイヤー以外の場合
        } else {
            damagedLiving.damage(damage, executor);
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

    public static Location adjustBlockLocation(Location location) {
        Location cloneLocation = location.clone();
        double x = cloneLocation.getBlockX() + 0.5D;
        double z = cloneLocation.getBlockZ() + 0.5D;

        return new Location(cloneLocation.getWorld(), x, cloneLocation.getY(), z, cloneLocation.getYaw(), cloneLocation.getPitch());
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
        if (string == null) {
            return null;
        }
        if (string == "") {
            return "";
        }
        if (string.length() == 1) {
            return string.toUpperCase();
        }
        String initial = string.substring(0, 1).toUpperCase();
        String other = string.substring(1, string.length()).toLowerCase();

        return initial + other;
    }

    //〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    //ブロック、非生物エンティティに影響しない爆発を発生
    public static void createSafeExplosion(Player executor, Location l, int damage, int range, float offset, boolean noSound, Particle...particles) {
        for (Particle particle : particles) {
            PacketUtil.sendParticlePacket(null, particle, l, offset, offset, offset, 1.0F, 20, new int[]{});
        }
        if (!noSound) {
            l.getWorld().playSound(l, Sound.EXPLODE, 0.2F, 1.0F);
        }
        ArrayList<LivingEntity> entities = Util.getNearbyLivingEntities(l, range);
        for (LivingEntity damaged : entities) {
            if (executor != null) {
                if (damaged.getUniqueId() == executor.getUniqueId()) {
                    continue;
                }
            }
            if (0 < damaged.getNoDamageTicks()) {
                continue;
            }
            if (damaged.isDead()) {
                continue;
            }
            if (!(damaged instanceof Player)) {
                continue;
            }
            if (!RaceManager.isStillRacing(((Player) damaged).getUniqueId())) {
                continue;
            }

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
}