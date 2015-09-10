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
import org.bukkit.Chunk;
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
import com.github.erozabesu.yplkart.object.MessageParts;
import com.github.erozabesu.yplkart.object.Racer;
import com.github.erozabesu.yplkart.reflection.Classes;
import com.github.erozabesu.yplkart.reflection.Constructors;
import com.github.erozabesu.yplkart.reflection.Fields;
import com.github.erozabesu.yplkart.reflection.Methods;
import com.github.erozabesu.yplkart.task.FlowerShowerTask;

public class Util extends ReflectionUtil {

    //〓 Player 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static boolean isPlayer(Object object) {
        return (object instanceof Player);
    }

    public static Boolean isOnline(String name) {
        return Bukkit.getPlayerExact(name) != null;
    }

    public static Boolean isOnline(UUID id) {
        return Bukkit.getPlayer(id) != null;
    }

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

    //〓 Entity 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 引数locationから半径radiusブロック以内のチャンクに存在するエンティティを返す。
     * @param location 基点となる座標
     * @param radius 半径
     * @return 引数locationから半径radiusブロック以内のチャンクに存在するエンティティ
     */
    public static List<Entity> getNearbyEntities(Location location, double radius) {
        List<Entity> entities = new ArrayList<Entity>();

        List<Chunk> nearbyChunks = getNearbyChunks(location, radius);
        if (nearbyChunks.isEmpty()) {
            return entities;
        }

        for (Chunk chunk : nearbyChunks) {
            for (Entity entity : chunk.getEntities()) {
                entities.add(entity);
            }
        }

        return entities;
    }

    /**
     * 引数locationから半径radiusブロック以内のチャンクに存在する生物エンティティを返す。
     * @param location 基点となる座標
     * @param radius 半径
     * @return 引数locationから半径radiusブロック以内のチャンクに存在する生物エンティティ
     */
    public static List<LivingEntity> getNearbyLivingEntities(Location location, double radius) {
        List<Entity> entities = getNearbyEntities(location, radius);
        List<LivingEntity> livingEntities = new ArrayList<LivingEntity>();
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity)
                livingEntities.add((LivingEntity) entity);
        }
        return livingEntities;
    }

    /**
     * 引数locationから半径radiusブロック以内のチャンクに存在するプレイヤーを返す。
     * @param location 基点となる座標
     * @param radius 半径
     * @return 引数locationから半径radiusブロック以内のチャンクに存在するプレイヤー
     */
    public static List<Player> getNearbyPlayers(Location location, double radius) {
        List<Entity> entities = getNearbyEntities(location, radius);
        List<Player> humanEntities = new ArrayList<Player>();
        for (Entity entity : entities) {
            if (entity instanceof Player) {
                humanEntities.add((Player) entity);
            }
        }
        return humanEntities;
    }

    /**
     * 引数entitiesの配列中のエンティティの内、最も引数locationとの直線距離が近いエンティティを返す。
     * @param entities エンティティリスト
     * @param location 基点となる座標
     * @return 引数entitiesの配列中のエンティティの内、最も引数locationとの直線距離が近いエンティティ
     */
    public static Entity getNearestEntity(List<Entity> entities, Location location) {
        Iterator<Entity> iterator = entities.iterator();
        Entity entity = null;
        Entity tempEntity;
        while (iterator.hasNext()) {
            tempEntity = iterator.next();
            if (entity != null) {
                if (entity.getWorld().getName().equalsIgnoreCase(tempEntity.getWorld().getName())) {
                    if (entity.getLocation().distance(location) < tempEntity.getLocation().distance(location)) {
                        continue;
                    }
                }
            }
            entity = tempEntity;
        }

        return entity;
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

    /**
     * 引数nmsEntityオブジェクトからBukkitEntityを取得し返す
     * @param nmsEntity BukkitEntityを取得するNmsEntity
     * @return BukkitEntity
     */
    public static Entity getBukkitEntityFromNmsEntity(Object nmsEntity) {
        return (Entity) invoke(Methods.nmsEntity_getBukkitEntity, nmsEntity);
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
            Racer racer = RaceManager.getRacer(player);

            //レース中でない場合は通常通りのダメージ処理
            if (!racer.isStillRacing()) {
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
                    Circuit circuit = racer.getCircuit();

                    //体力を最大値まで回復
                    player.setHealth(player.getMaxHealth());

                    //攻撃実行者が明確な場合、レース参加者に他殺のデスログを送信
                    MessageParts circuitParts = MessageParts.getMessageParts(circuit);
                    if (executor != null) {
                        MessageParts playerParts = MessageParts.getMessageParts((Player) damaged, (Player) executor);
                        circuit.sendMessageEntryPlayer(MessageEnum.racePlayerKill, circuitParts, playerParts);

                    //攻撃実行者が不明の場合、レース参加者に死亡したことのみを伝えるデスログを送信
                    } else {
                        MessageParts playerParts = MessageParts.getMessageParts((Player) damaged);
                        circuit.sendMessageEntryPlayer(MessageEnum.racePlayerDead, circuitParts, playerParts);
                    }

                    //プレイヤーにデスペナルティを適用
                    racer.applyDeathPenalty();
                }

                //連続してダメージを受けないようプレイヤーを少しの間無敵にする
                player.setNoDamageTicks(5);
            }

        //被ダメージエンティティがプレイヤー以外の場合
        } else {
            damagedLiving.damage(damage, executor);
        }
    }

    //〓 World 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * ブロック、非生物エンティティに影響しない爆発を生成する。
     * @param executor 爆発を引き起こしたプレイヤーとしてキルログに表示するプレイヤー
     * @param location 生成する座標
     * @param damage 爆発ダメージ
     * @param range 爆発の半径
     * @param soundVolume 爆発音声の音量
     * @param offset 爆発パーティクルを生成する際のXYZのオフセット
     * @param particles 生成するパーティクル
     */
    public static void createSafeExplosion(Player executor, Location location, int damage, int range, float soundVolume, float offset, Particle...particles) {
        for (Particle particle : particles) {
            PacketUtil.sendParticlePacket(null, particle, location, offset, offset, offset, 1.0F, 20, new int[]{});
        }

        location.getWorld().playSound(location, Sound.EXPLODE, soundVolume, 1.0F);

        List<LivingEntity> entities = Util.getNearbyLivingEntities(location, range);
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

            Racer racer = RaceManager.getRacer((Player) damaged);
            if (!racer.isStillRacing()) {
                continue;
            }

            Vector vector = Util.getVectorToLocation(damaged.getLocation(), location);
            vector.setX(vector.clone().multiply(-1).getX());
            vector.setY(0);
            vector.setZ(vector.clone().multiply(-1).getZ());
            damaged.setVelocity(vector);

            addDamage(damaged, executor, damage);
        }
    }

    //〓 Chunk 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 引数locationから半径radiusブロック以内のチャンクを返す。
     * @param location 基点となる座標
     * @param radius 半径
     * @return 引数locationから半径radiusブロック以内のチャンク
     */
    public static List<Chunk> getNearbyChunks(Location location, double radius) {
        List<Chunk> nearbyChunks = new ArrayList<Chunk>();

        location.add(radius, 0, radius);

        double round = (radius / 16) + 1;
        for (int x = 0; x < round; x++) {
            for (int z = 0; z < round; z++) {
                Chunk chunk = location.getWorld().getChunkAt(location.clone().add(-16 * x, 0, -16 * z));
                nearbyChunks.add(chunk);
            }
        }

        return nearbyChunks;
    }

    //〓 Location 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

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
        Location adjustlocation = adjustLocationToBlockCenter(location);
        float yaw = adjustlocation.getYaw();
        double x = Math.cos(Math.toRadians(yaw));
        double z = Math.sin(Math.toRadians(yaw));

        return new Location(adjustlocation.getWorld(),
                adjustlocation.getX() + x * offset,
                adjustlocation.getY(),
                adjustlocation.getZ() + z * offset,
                yaw, adjustlocation.getPitch());
    }

    public static Location getStandingLocationByBoudingBox(Entity entity) {
        Location location = entity.getLocation();
        Object boudingBox = ReflectionUtil.invoke(Methods.nmsEntity_getBoundingBox, entity);

        location.setX((Double) ReflectionUtil.getFieldValue(Fields.nmsEntity_locX, entity));
        location.setY((Double) ReflectionUtil.getFieldValue(Fields.nmsAxisAlignedBB_locYBottom, boudingBox));
        location.setZ((Double) ReflectionUtil.getFieldValue(Fields.nmsEntity_locZ, entity));

        return location;
    }

    /**
     * 引数locationに設置されたブロックの中心座標を返す。
     * @param location 基点となる座標
     * @return 引数locationに設置されたブロックの中心座標
     */
    public static Location adjustLocationToBlockCenter(Location location) {
        Location cloneLocation = location.clone();
        double x = cloneLocation.getBlockX() + 0.5D;
        double z = cloneLocation.getBlockZ() + 0.5D;

        return new Location(cloneLocation.getWorld(), x, cloneLocation.getY(), z, cloneLocation.getYaw(), cloneLocation.getPitch());
    }

    /**
     * 引数fromから引数targetの座標間に固形ブロックが存在しておらず、引数targetの座標を視認できるかどうかを返す。
     * @param from チェックする座標
     * @param target チェックする座標
     * @return 引数fromから引数targetの座標を視認できるかどうか
     */
    public static boolean canSeeLocation(Location from, Location target) {
        if (!from.getWorld().equals(target.getWorld())) {
            return false;
        }

        Object nmsWorld = ReflectionUtil.invoke(Methods.craftWorld_getHandle, from.getWorld());
        Object fromVec3D = ReflectionUtil.newInstance(Constructors.nmsVec3D, from.getX(), from.getY(), from.getZ());
        Object targetVec3D = ReflectionUtil.newInstance(Constructors.nmsVec3D, target.getX(), target.getY(), target.getZ());

        return ReflectionUtil.invoke(Methods.nmsWorld_rayTrace, nmsWorld, fromVec3D, targetVec3D) == null;
    }

    //〓 Block 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

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
     * 引数locationのブロックがよじ登ることができるブロックかどうかを判別する
     * @param location 座標
     * @return よじ登ることができるブロックかどうか
     */
    public static boolean isClimbableBlock(Location location) {
        Material blockMaterial = location.getBlock().getType();
        return (blockMaterial == Material.LADDER || blockMaterial == Material.VINE);
    }

    /**
     * 引数blockが下半分に設置された半ブロックかどうかを返す。<br>
     * 上半分に設置された半ブロックの場合、もしくは半ブロックではない場合はfalseを返す。
     * @param block チェックするブロック
     * @return 引数blockが下半分に設置された半ブロックかどうか
     */
    public static Boolean isBottomSlabBlock(Block block) {
        if (!isSlabBlock(block.getLocation())) {
            return false;
        }

        if (8 <= block.getData()) {
            return false;
        }

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

    /**
     * 引数locationに設置されているブロックが半ブロックかどうかを返す。
     * @param location ブロックをチェックする座標
     * @return 半ブロックかどうか
     */
    public static Boolean isSlabBlock(Location location) {
        int id = location.getBlock().getTypeId();
        if (id == 44 || id == 126 || id == 182)
            return true;
        return false;
    }

    //〓 Vector 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 引数fromが現在向いているYaw方向へのベクターを返す。
     * @param from 取得する座標
     * @return 引数fromが現在向いているYaw方向へのベクター
     */
    public static Vector getVectorByYaw(Location from){
        float yaw = from.getYaw();
        double x = -Math.sin(Math.toRadians(yaw < 0 ? yaw + 360 : yaw));
        double z = Math.cos(Math.toRadians(yaw < 0 ? yaw + 360 : yaw));

        Location to = new Location(from.getWorld(), from.getX()+x, from.getY(), from.getZ()+z);
        return getVectorToLocation(to, from);
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
     * 引数eyeLocationからの視界に引数targetの座標が含まれているかどうかを返す。
     * @param eyeLocation チェックする視点の座標
     * @param target 視界に入っているかチェックする座標
     * @param threshold 視野の広さ。0.0F～360.0Fの数値を指定する
     * @return 引数fromからの視界に引数toの座標が含まれているかどうか
     */
    public static boolean isLocationInSight(Location eyeLocation, Location target, float threshold) {
        // 360.0F以上を指定された場合は無条件でtrueを返す
        if (360.0F <= threshold) {
            return true;
        }

        // 座標の取得。playerがカートに搭乗している場合はカートの座標を格納。
        float eyeYaw = eyeLocation.getYaw();

        // マイナスの値になる場合があるため正の数に変換
        if (eyeYaw < 0) {
            eyeYaw += 360.0F;
        }

        // vectorYawと同じ逆時計周りに変更
        eyeYaw -= 360.0F;
        eyeYaw = Math.abs(eyeYaw);

        float vectorYaw = getYawFromVector(getVectorToLocation(eyeLocation, target));
        vectorYaw = Math.abs(vectorYaw) - 90.0F;

        return isInSight(eyeYaw, vectorYaw, threshold);
    }

    /**
     * 引数entityからの視界に引数targetの座標が含まれているかどうかを返す。
     * @param entity チェックするエンティティ
     * @param target 視界に入っているかチェックする座標
     * @param threshold 視野の広さ。0.0F～360.0Fの数値を指定する
     * @return 引数fromからの視界に引数toの座標が含まれているかどうか
     */
    public static boolean isLocationInSight(Entity entity, Location target, float threshold) {
        // 座標の取得。entityが乗り物に搭乗している場合は乗り物の座標を格納。
        Location eyeLocation = entity.getVehicle() == null ? entity.getLocation().clone() : entity.getVehicle().getLocation().clone();

        return isLocationInSight(eyeLocation, target, threshold);
    }

    private static boolean isInSight(float baseYaw, float targetYaw, float threshold) {
        // 360.0F以上を指定された場合は無条件でtrueを返す
        if (360.0F <= threshold) {
            return true;
        }

        float positiveThreshold = baseYaw + (threshold) / 2.0F;

        // 基準点に閾値を加算した結果が360未満の場合
        if (positiveThreshold <= 360) {
            if (baseYaw <= targetYaw && targetYaw <= positiveThreshold) {
                return true;
            }

        // 基準点に閾値を加算した結果が360を超えた場合
        } else {
            if (baseYaw <= targetYaw && targetYaw <= 360.0F) {
                return true;
            } else{
                positiveThreshold -= 360.0F;
                if (0.0F <= targetYaw && targetYaw <= positiveThreshold) {
                    return true;
                }
            }
        }

        float negativeThreshold = baseYaw - (threshold) / 2.0F;
        // 基準点に閾値を減算した結果が0以上の場合
        if (0 <= negativeThreshold) {
            if (negativeThreshold <= targetYaw && targetYaw <= baseYaw) {
                return true;
            }

        // 基準点に閾値を減算した結果が0未満の場合
        } else {
            if (0.0F <= targetYaw && targetYaw <= baseYaw) {
                return true;
            } else{
                negativeThreshold += 360.0F;
                if (negativeThreshold <= targetYaw && targetYaw <= 360.0F) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 引数yawを-180.0F～180.0Fの数値に換算した場合に、負の数値に換算されるかどうかを返す。
     * @param yaw チェックするYaw
     * @return 引数yawが負の数値に換算されるかどうか
     */
    private static boolean isNegativeRoundDegree(float yaw) {
        if ((0.0F <= yaw && yaw <= 180.0F) || yaw < -180.0F) {
            return false;
        }

        return true;
    }

    /**
     * 引数yawが180.0Fを超えていた場合、0～-180.0Fの間の数値に変換し返す。<br>
     * 180.0Fを超えていない場合は引数yawをそのまま返す。
     * @param yaw 変換するYaw
     * @return 変換したYaw
     */
    private static float calcYawToRoundDegree(float yaw) {
        if (180.0F <= yaw) {
            return (180.0F - (yaw - 180.0F)) * -1;
        } else if (yaw <= -180.0F) {
            return 180.0F + (yaw + 180.0F);
        }

        return yaw;
    }

    //〓 Nms Object 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static Object getBlockPosition(Location location) {
        double locX = Math.floor(location.getX());
        double locY = Math.floor(location.getY());
        double locZ = Math.floor(location.getZ());

        return ReflectionUtil.newInstance(Constructors.nmsBlockPosition, locX, locY, locZ);
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

    //〓 Particle 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

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

    //〓 Java Class Format 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

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

    //〓 Math 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

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
     * 引数valueと引数value2の符号を考慮した差を返す。<br>
     * お互いの符号が一致する場合は、双方の絶対値の差を返す。<br>
     * 符号が一致しない場合は、双方の絶対値の和を返す。
     * @param value 差を求める数値
     * @param value2 差を求める数値
     * @return 差
     */
    public static double getDoubleDifference(double value, double value2) {
        double absValue = Math.abs(value);
        double absValue2 = Math.abs(value2);

        // お互いの符号が一致している場合
        if ((Math.signum(value) == 1.0D && Math.signum(value2) == 1.0D)
                || (Math.signum(value) == -1.0D && Math.signum(value2) == -1.0D)) {

            return Math.abs(absValue - absValue2);

        // お互いの符号が一致していない場合
        } else {
            return absValue + absValue2;
        }
    }

    //〓 String 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 引数textの文字列から整数値を抽出し返す。<br>
     * 抽出できなかった場合は0を返す。
     * @param text 抽出する文字列
     * @return 抽出した整数値
     */
    public static int extractIntegerFromString(String text) {
        text = ChatColor.stripColor(text);
        text = text.replaceAll("[^0-9]", "");
        try {
            return Integer.valueOf(text);
        } catch(NumberFormatException ex) {
            return 0;
        }
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
}
