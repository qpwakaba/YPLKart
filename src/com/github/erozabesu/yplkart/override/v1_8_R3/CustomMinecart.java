package com.github.erozabesu.yplkart.override.v1_8_R3;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityMinecartAbstract;
import net.minecraft.server.v1_8_R3.EntityMinecartRideable;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.World;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import com.github.erozabesu.yplkart.Permission;
import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.data.ConfigEnum;
import com.github.erozabesu.yplkart.data.DisplayKartConfig;
import com.github.erozabesu.yplkart.data.ItemEnum;
import com.github.erozabesu.yplkart.data.KartConfig;
import com.github.erozabesu.yplkart.object.Kart;
import com.github.erozabesu.yplkart.object.KartType;
import com.github.erozabesu.yplkart.object.Racer;
import com.github.erozabesu.yplkart.utils.Particle;
import com.github.erozabesu.yplkart.utils.Util;

public class CustomMinecart extends EntityMinecartRideable {

    private boolean a;
    private String b;
    private static final int[][][] matrix = { { { 0, 0, -1 }, { 0, 0, 1 } }, { { -1 }, { 1 } }, { { -1, -1 }, { 1 } },
            { { -1 }, { 1, -1 } }, { { 0, 0, -1 }, { 0, -1, 1 } }, { { 0, -1, -1 }, { 0, 0, 1 } },
            { { 0, 0, 1 }, { 1 } }, { { 0, 0, 1 }, { -1 } }, { { 0, 0, -1 }, { -1 } }, { { 0, 0, -1 }, { 1 } } };
    private int d;
    private double e;
    private double f;
    private double g;
    private double h;
    private double i;
    public boolean slowWhenEmpty = true;

    /** Xモーションの地面での摩擦係数 */
    private double groundFrictionX = 0.85D;

    /** Yモーションの地面での摩擦係数 */
    private double groundFrictionY = 0.85D;

    /** Zモーションの地面での摩擦係数 */
    private double groundFrictionZ = 0.85D;

    /** Xモーションの空中での摩擦係数 */
    private double flyFrictionX = 0.95D;

    /** Yモーションの空中での摩擦係数 */
    private double flyFrictionY = 0.95D;

    /** Zモーションの空中での摩擦係数 */
    private double flyFrictionZ = 0.95D;

    /* レール上を移動する際のモーション値の上限
    public double maxSpeed = 0.4D;*/

    /** Kartオブジェクト */
    private Kart kart;

    /** KartType */
    private KartType kartType;

    /** スピードスタック */
    private double speedStack = 0;

    /** 1チック前のモーション値 */
    private double lastMotionSpeed;

    /** キラー使用中のX方向のモーション値 */
    private double killerX = 0;

    /** キラー使用中のY方向のモーション値 */
    private double killerY = 0;

    /** キラー使用中のZ方向のモーション値 */
    private double killerZ = 0;

    /** キラー使用中に通過したチェックポイントEntityのUUIDList */
    private List<String> killerPassedCheckPointList;

    /** キラー使用中、1チック前に通過したチェックポイントEntityのUUID */
    private org.bukkit.entity.Entity killerLastPassedCheckPoint;

    //〓 メイン 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public CustomMinecart(World w, Kart kart, KartType kartType, Location location) {
        super(w);
        setKartType(kartType);
        this.yaw = location.getYaw();

        if (getKartType().equals(KartType.DisplayKart)) {
            this.pitch = -location.getPitch();
            setYawPitch(this.yaw + 90F, this.pitch);
            return;
        }
        setYawPitch(this.yaw + 90F, 0F);
        setPosition(location.getX(), location.getY() + 1, location.getZ());
        setParameter(kart);
    }

    //〓 getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @return groundFrictionX Xモーションの地面での摩擦係数 */
    public double getGroundFrictionX() {
        return groundFrictionX;
    }

    /** @return groundFrictionY Yモーションの地面での摩擦係数 */
    public double getGroundFrictionY() {
        return groundFrictionY;
    }

    /** @return groundFrictionZ Zモーションの地面での摩擦係数 */
    public double getGroundFrictionZ() {
        return groundFrictionZ;
    }

    /** @return flyFrictionX Xモーションの空中での摩擦係数 */
    public double getFlyFrictionX() {
        return flyFrictionX;
    }

    /** @return flyFrictionY Yモーションの空中での摩擦係数 */
    public double getFlyFrictionY() {
        return flyFrictionY;
    }

    /** @return flyFrictionZ Zモーションの空中での摩擦係数 */
    public double getFlyFrictionZ() {
        return flyFrictionZ;
    }

    /** @return kart Kartオブジェクト */
    public Kart getKart() {
        return kart;
    }

    /** @return kartType KartType */
    public KartType getKartType() {
        return kartType;
    }

    /** @return speedStack スピードスタック */
    public double getSpeedStack() {
        return speedStack;
    }

    /** @return lastMotionSpeed 1チック前のモーション値 */
    public double getLastMotionSpeed() {
        return lastMotionSpeed;
    }

    /** @return killerX キラー使用中のX方向のモーション値 */
    public double getKillerX() {
        return killerX;
    }

    /** @return killerY キラー使用中のY方向のモーション値 */
    public double getKillerY() {
        return killerY;
    }

    /** @return killerZ キラー使用中のZ方向のモーション値 */
    public double getKillerZ() {
        return killerZ;
    }

    /** @return killerPassedCheckPointList キラー使用中に通過したチェックポイントEntityのUUIDList */
    public List<String> getKillerPassedCheckPointList() {
        return killerPassedCheckPointList;
    }

    /** @return killerLastPassedCheckPoint キラー使用中、1チック前に通過したチェックポイントEntity */
    public org.bukkit.entity.Entity getKillerLastPassedCheckPoint() {
        return killerLastPassedCheckPoint;
    }

    //〓 setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @param groundFrictionX Xモーションの地面での摩擦係数 */
    public void setGroundFrictionX(double groundFrictionX) {
        this.groundFrictionX = groundFrictionX;
    }

    /** @param groundFrictionY Yモーションの地面での摩擦係数 */
    public void setGroundFrictionY(double groundFrictionY) {
        this.groundFrictionY = groundFrictionY;
    }

    /** @param groundFrictionZ Zモーションの地面での摩擦係数 */
    public void setGroundFrictionZ(double groundFrictionZ) {
        this.groundFrictionZ = groundFrictionZ;
    }

    /** @param flyFrictionX Xモーションの空中での摩擦係数 */
    public void setFlyFrictionX(double flyFrictionX) {
        this.flyFrictionX = flyFrictionX;
    }

    /** @param flyFrictionY Yモーションの空中での摩擦係数 */
    public void setFlyFrictionY(double flyFrictionY) {
        this.flyFrictionY = flyFrictionY;
    }

    /** @param flyFrictionZ Zモーションの空中での摩擦係数 */
    public void setFlyFrictionZ(double flyFrictionZ) {
        this.flyFrictionZ = flyFrictionZ;
    }

    /** @param kart セットするKartオブジェクト */
    public void setKart(Kart kart) {
        this.kart = kart;
    }

    /** @param kartType セットするKartType */
    public void setKartType(KartType kartType) {
        this.kartType = kartType;
    }

    /** @param speedStack セットするスピードスタック */
    public void setSpeedStack(double speedStack) {
        this.speedStack = speedStack;
    }

    /** @param lastMotionSpeed セットする1チック前のモーション値 */
    public void setLastMotionSpeed(double lastMotionSpeed) {
        this.lastMotionSpeed = lastMotionSpeed;
    }

    /** @param killerX セットするキラー使用中のX方向のモーション値 */
    public void setKillerX(double killerX) {
        this.killerX = killerX;
    }

    /** @param killerY セットするキラー使用中のY方向のモーション値 */
    public void setKillerY(double killerY) {
        this.killerY = killerY;
    }

    /** @param killerZ セットするキラー使用中のZ方向のモーション値 */
    public void setKillerZ(double killerZ) {
        this.killerZ = killerZ;
    }

    /** @param killerPassedCheckPointList セットするキラー使用中に通過したチェックポイントEntityのUUIDList */
    public void setKillerPassedCheckPointList(List<String> killerPassedCheckPointList) {
        this.killerPassedCheckPointList = killerPassedCheckPointList;
    }

    /** @param killerLastPassedCheckPoint セットするキラー使用中、1チック前に通過したチェックポイントEntity */
    public void setKillerLastPassedCheckPoint(org.bukkit.entity.Entity killerLastPassedCheckPoint) {
        this.killerLastPassedCheckPoint = killerLastPassedCheckPoint;
    }

    //〓 getter - CraftBukkit 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * @param entity 取得するEntity
     * @return クライアントとの同期が必要な状態かどうか
     */
    public boolean isClientSide(Entity entity) {
        return entity.getWorld().isClientSide;
    }

    /**
     * @param entity 取得するEntity
     * @return 死亡状態かどうか
     */
    public boolean isDead(Entity entity) {
        return entity.dead;
    }

    /**
     * @param entity 取得するEntity
     * @return 物理判定が無効な状態かどうか
     */
    public boolean isNoclip(Entity entity) {
        return entity.noclip;
    }

    /**
     * @param entity 取得するEntity
     * @return 1チック前から座標の変化があったかどうか
     */
    public boolean isPositionChanged(Entity entity) {
        return entity.positionChanged;
    }

    /**
     * @param entity 取得するEntity
     * @return 地面に接しているかどうか
     */
    public boolean isOnGround(Entity entity) {
        return entity.onGround;
    }

    /**
     * @param entity 取得するEntity
     * @return X座標
     */
    public double getLocationX(Entity entity) {
        return entity.locX;
    }

    /**
     * @param entity 取得するEntity
     * @return Y座標
     */
    public double getLocationY(Entity entity) {
        return entity.locY;
    }

    /**
     * @param entity 取得するEntity
     * @return Z座標
     */
    public double getLocationZ(Entity entity) {
        return entity.locZ;
    }

    /**
     * @param entity 取得するEntity
     * @return 偏揺れ角
     */
    public float getYaw(Entity entity) {
        return entity.yaw;
    }

    /**
     * @param entity 取得するEntity
     * @return ピッチ角
     */
    public float getPitch(Entity entity) {
        return entity.pitch;
    }

    /**
     * @param entity 取得するEntity
     * @return 1チック前のX座標
     */
    public double getLastLocationX(Entity entity) {
        return entity.lastX;
    }

    /**
     * @param entity 取得するEntity
     * @return 1チック前のY座標
     */
    public double getLastLocationY(Entity entity) {
        return entity.lastY;
    }

    /**
     * @param entity 取得するEntity
     * @return 1チック前のZ座標
     */
    public double getLastLocationZ(Entity entity) {
        return entity.lastZ;
    }

    /**
     * @param entity 取得するEntity
     * @return 引数entityのXモーション
     */
    public double getMotionX(Entity entity) {
        return entity.motX;
    }

    /**
     * @param entity 取得するEntity
     * @return 引数entityのYモーション
     */
    public double getMotionY(Entity entity) {
        return entity.motY;
    }

    /**
     * @param entity 取得するEntity
     * @return 引数entityのZモーション
     */
    public double getMotionZ(Entity entity) {
        return entity.motZ;
    }

    /**
     * @param entity 取得するEntity
     * @return 搭乗者
     */
    public Entity getPassenger(Entity entity) {
        return entity.passenger;
    }

    /**
     * @param entity 取得するEntity
     * @return 乗り物
     */
    public Entity getVehicle(Entity entity) {
        return entity.vehicle;
    }

    /**
     * @param entity 取得するEntity
     * @return 乗り越えられるブロックの高さ
     */
    public float getClimbableHeight(Entity entity) {
        return entity.S;
    }

    /**
     * @param entity 取得するEntity
     * @return 落下距離
     */
    public float getFallDistance(Entity entity) {
        return entity.fallDistance;
    }

    /**
     * 引数humanを操作しているクライアントの縦方向の移動に関する入力係数を返す
     * @param human 取得するEntityHuman
     * @return 縦方向の入力係数
     */
    public float getForwardMotionInput(EntityHuman human) {
        return human.ba;
    }

    /**
     * 引数humanを操作しているクライアントの横方向の移動に関する入力係数を返す
     * @param human 取得するEntityHuman
     * @return 縦方向の入力係数
     */
    public float getSideMotionInput(EntityHuman human) {
        return human.aZ;
    }

    /**
     * 引数humanを操作しているクライアントの移動に関する入力の強さ
     * @param human 取得するEntityHuman
     * @return 入力の強さ
     */
    public float getMotionInputStrength(EntityHuman human) {
        return human.bI();
    }

    /** @return 摩擦係数（固定で0.4D） */
    public double getFrictionValue() {
        return this.m();
    }

    /** @return 当たり判定に基づいた現在のY座標 */
    public double getLocationYFromBoundingBox() {
        return getBoundingBox().b;
    }

    //〓 setter - CraftBukkit 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * @param entity 値を変更するEntity
     * @param isNoclip 物理判定が無効な状態かどうか
     */
    public void setNoclip(Entity entity, boolean isNoclip) {
        entity.noclip = isNoclip;
    }

    /**
     * @param entity 値を変更するEntity
     * @param locX X座標
     */
    public void setLocationX(Entity entity, double locX) {
        entity.locX = locX;
    }

    /**
     * @param entity 値を変更するEntity
     * @param locY Y座標
     */
    public void setLocationY(Entity entity, double locY) {
        entity.locY = locY;
    }

    /**
     * @param entity 値を変更するEntity
     * @param locZ Z座標
     */
    public void setLocationZ(Entity entity, double locZ) {
        entity.locZ = locZ;
    }

    /**
     * @param entity 値を変更するEntity
     * @param yaw 偏揺れ角
     */
    public void setYaw(Entity entity, float yaw) {
        entity.yaw = yaw;
    }

    /**
     * @param entity 値を変更するEntity
     * @param pitch ピッチ角
     */
    public void setPitch(Entity entity, float pitch) {
        entity.pitch = pitch;
    }

    /**
     * @param entity 値を変更するEntity
     * @param lastX X座標
     */
    public void setLastLocationX(Entity entity, double lastX) {
        entity.lastX = lastX;
    }

    /**
     * @param entity 値を変更するEntity
     * @param lastY Y座標
     */
    public void setLastLocationY(Entity entity, double lastY) {
        entity.lastY = lastY;
    }

    /**
     * @param entity 値を変更するEntity
     * @param lastZ Z座標
     */
    public void setLastLocationZ(Entity entity, double lastZ) {
        entity.lastZ = lastZ;
    }

    /**
     * @param entity 値を変更するEntity
     * @param motX Xモーション
     */
    public void setMotionX(Entity entity, double motX) {
        entity.motX = motX;
    }

    /**
     * @param entity 値を変更するEntity
     * @param motY Yモーション
     */
    public void setMotionY(Entity entity, double motY) {
        entity.motY = motY;
    }

    /**
     * @param entity 値を変更するEntity
     * @param motZ Zモーション
     */
    public void setMotionZ(Entity entity, double motZ) {
        entity.motZ = motZ;
    }

    /**
     * @param entity 値を変更するEntity
     * @param passenger 搭乗者
     */
    public void setPassenger(Entity entity, Entity passenger) {
        entity.passenger = passenger;
    }

    /**
     * @param entity 値を変更するEntity
     * @param 乗り物
     */
    public void setVehicle(Entity entity, Entity vehicle) {
        entity.vehicle = vehicle;
    }

    /**
     * @param entity 値を変更するEntity
     * @param climbableHeight 乗り越えられるブロックの高さ
     */
    public void setClimbableHeight(Entity entity, float climbableHeight) {
        entity.S = climbableHeight;
    }

    /**
     * @param entity 値を変更するEntity
     * @param fallDistance 落下距離
     */
    public void setFallDistance(Entity entity, float fallDistance) {
        entity.fallDistance = fallDistance;
    }

    //〓 do 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * Entityがダメージを受けた際の揺れる速度を時間経過で復元する<br>
     * マインカートや船のような一定時間攻撃し続けなければ破壊できないEntityにおける
     * ダメージを受けた際に左右に揺れる速度を指す<br>
     * <br>
     * EntityMinecartAbstract.getType()はMinecartのDataWatcherインデックス17番Shaking Powerを返す<br>
     * EntityMinecartAbstract.j(int i)は同DataWatcherの値を変更する<br>
     * @see <a href="http://wiki.vg/Entities#Entity_Metadata_Format">Entity_Metadata_Format</a>
     */
    public void recoveryShakingPower() {
        if (getType() > 0) {
            j(getType() - 1);
        }
    }

    /**
     * Entityの耐久値を時間経過で復元する<br>
     * 耐久値とは、マインカートや船のような一定時間攻撃し続けなければ破壊できないEntityにおける破壊の進捗度を指す<br>
     * 同時に、耐久値はダメージを受けた際の揺れ幅にも影響する<br>
     * <br>
     * EntityMinecartAbstract.getDamage()はMinecartのDataWatcherインデックス19番Damage Taken / Shaking Multiplierを返す<br>
     * EntityMinecartAbstract.setDamage(float f)は同DataWatcherの値を変更する<br>
     * @see <a href="http://wiki.vg/Entities#Entity_Metadata_Format">Entity_Metadata_Format</a>
     */
    public void recoveryDamage() {
        if (getDamage() > 0.0F) {
            setDamage(getDamage() - 1.0F);
        }
    }

    public void craftBukkit_die() {
        this.O();
    }

    public void craftBukkit_applyFriction() {
        n();
    }

    public void craftBukkit_applyWaterCollision() {
        W();
    }

    public void setParameter(Kart kart) {
        this.setKart(kart);
        setClimbableHeight(this, kart.getClimbableHeight());
    }

    /**
     * @param entity
     * @return 引数entityの現在座標のチャンクがロードされているかどうか
     */
    public boolean isLoadedChunk(Entity entity) {
        BlockPosition currentPosition = getCurrentBlockPosition(entity);
        return entity.getWorld().getChunkAtWorldCoords(currentPosition).o();
    }

    /** @return よじ登ることができるブロックかどうか */
    public boolean isClambableBlock() {
        int locationX = MathHelper.floor(getLocationX(this));
        int locationY = MathHelper.floor(getLocationYFromBoundingBox());
        int locationZ = MathHelper.floor(getLocationZ(this));
        Block block = this.getWorld().getType(
                new BlockPosition(locationX, locationY, locationZ)).getBlock();

        return (block == Blocks.LADDER || block == Blocks.VINE);
    }

    /** @return ダートブロックかどうか */
    public boolean isDirtBlock() {
        int locationX = MathHelper.floor(getLocationX(this));
        int locationY = MathHelper.floor(getLocationYFromBoundingBox());
        int locationZ = MathHelper.floor(getLocationZ(this));

        Location location =
                new Location(this.getBukkitEntity().getWorld(), locationX, locationY, locationZ);

        return Util.getGroundBlockID(location).equalsIgnoreCase(
                (String) ConfigEnum.DIRT_BLOCK_ID.getValue());
    }

    /**
     * @param entity
     * @return 現在座標のBlockPositionを返す
     */
    public BlockPosition getCurrentBlockPosition(Entity entity) {
        return new BlockPosition(
                (int) getLocationX(entity), getLocationY(entity), (int) getLocationZ(entity));
    }

    /**
     * @param entity
     * @return 引数entityが搭乗している一番下のエンティティ
     */
    public Entity getEndVehicle(Entity entity) {
        if (getVehicle(entity) == null)
            return entity;

        Entity vehicle = getVehicle(entity);
        if (vehicle != null) {
            while (getVehicle(vehicle) != null) {
                vehicle = getVehicle(vehicle);
            }
        }

        return vehicle;
    }

    /**
     * @param entity
     * @return 引数entityが搭乗している一番上のエンティティ
     */
    public Entity getEndPassenger(Entity entity) {
        if (getPassenger(entity) == null)
            return entity;

        Entity passenger = getPassenger(entity);
        if (passenger != null) {
            while (getPassenger(passenger) != null) {
                passenger = getPassenger(passenger);
            }
        }

        return passenger;
    }

    //〓 Override 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * setPassenger
     * マインカートが搭乗可能な状態だった場合搭乗させる
     * プレイヤーに右クリックされた場合等に処理される
     * @return 既に搭乗者がいた場合、搭乗者がプレイヤーならtrue、プレイヤー以外のEntityならfalse
     */
    @Override
    public boolean e(EntityHuman human)
    {
        if (getPassenger(this) != null && getPassenger(this) != human) {
            if (getPassenger(this) instanceof EntityHuman) {
                return true;
            }
            return false;
        }
        if (getKartType().equals(KartType.DisplayKart)) {
            human.mount(this);
        }
        if (!isClientSide(this)) {
            human.mount(this);
        }

        return true;
    }

    /**
     * livingUpdate
     * Entityの状態を毎チック更新する
     */
    @Override
    public void t_()
    {
        //被ダメージ時の揺れる速度を減衰する
        recoveryShakingPower();

        //被ダメージ時の耐久値の回復
        recoveryDamage();

        //クライアントから読み込まれなくなった場合デスポーン
        if (isClientSide(this)) {
            craftBukkit_die();
            return;
        }

        //奈落に落下した場合デスポーン
        if (getLocationY(this) < -64.0D) {
            craftBukkit_die();
        }

        //1チック前の座標を記録
        setLastLocationX(this, getLocationX(this));
        setLastLocationY(this, getLocationY(this));
        setLastLocationZ(this, getLocationZ(this));

        //展示用カートの場合モーションを0に固定しreturnする
        if (getKartType().equals(KartType.DisplayKart)) {
            setMotionX(this, 0);
            setMotionY(this, 0);
            setMotionZ(this, 0);
            setYawPitch(getYaw(this), getPitch(this));
            return;
        }

        //YモーションをマイナスしEntityを自然落下させる
        setMotionY(this, getMotionY(this) - 0.03999999910593033D);

        craftBukkit_applyFriction();

        checkBlockCollisions();

        Iterator iterator = this.getWorld().getEntities(this,
                getBoundingBox().grow(0.2000000029802322D, 0.0D, 0.2000000029802322D)).iterator();
        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();
            if ((entity != getPassenger(this)) && ((entity instanceof EntityMinecartAbstract))) {
                entity.collide(this);
            }
        }

        if (getPassenger(this) != null && isDead(getPassenger(this))) {
            if (getVehicle(getPassenger(this)) == this) {
                setVehicle(getPassenger(this), null);
            }

            setPassenger(this, null);
        }

        if (getPassenger(this) != null) {
            if (getVehicle(getPassenger(this)) == this) {
                Player player = (Player) getPassenger(this).getBukkitEntity();
                player.playSound(getPassenger(this).getBukkitEntity().getLocation()
                        , Sound.COW_WALK, 0.2F, 0.05F + ((float) this.getSpeedStack() / 200));
                player.playSound(getPassenger(this).getBukkitEntity().getLocation()
                        , Sound.GHAST_FIREBALL, 0.01F + ((float) this.getSpeedStack() / 400), 1.0F);
                player.playSound(getPassenger(this).getBukkitEntity().getLocation()
                        , Sound.FIZZ, 0.01F + ((float) this.getSpeedStack() / 400), 0.5F);
            }
        }

        craftBukkit_applyWaterCollision();
    }

    /**
     * applyFriction
     * 現在のモーション値に摩擦係数を適用し徐々に減衰させる
     */
    @Override
    protected void n() {
        //搭乗者がいる場合
        if (getPassenger(this) != null) {
            //プレイヤーが搭乗している場合
            if (getPassenger(this) instanceof EntityHuman) {
                setMotion((EntityHuman) getPassenger(this));
            } else {
                //プレイヤー以外が搭乗している場合
                double d0 = getFrictionValue();

                setMotionX(this, MathHelper.a(getMotionX(this), -d0, d0));
                setMotionZ(this, MathHelper.a(getMotionZ(this), -d0, d0));
            }
        }

        //地面にいる場合は地面との摩擦係数を適用
        if (isOnGround(this)) {
            setMotionX(this, getMotionX(this) * getGroundFrictionX());
            setMotionY(this, getMotionY(this) * getGroundFrictionY());
            setMotionZ(this, getMotionZ(this) * getGroundFrictionZ());
        }

        //移動
        move(getMotionX(this), getMotionY(this), getMotionZ(this));

        //空中にいる場合は地面との摩擦係数を適用
        //なぜmoveの後に適用するのかは不明
        if (!isOnGround(this)) {
            setMotionX(this, getMotionX(this) * getFlyFrictionX());
            setMotionY(this, getMotionY(this) * getFlyFrictionY());
            setMotionZ(this, getMotionZ(this) * getFlyFrictionZ());
        }
    }

    public void setMotion(EntityHuman human) {
        this.setLastMotionSpeed(calcMotionSpeed(getMotionX(this), getMotionZ(this)) * this.getKart().getWeight());

        /*
         * キラー発動中
         * 「最寄」の「一度も通過していない」チェックポイントに向け自動で移動する
         * コースアウトを防ぐため、最寄のチェックポイントとの平面距離が3ブロック以内になるまで
         * 次のチェックポイントへは移動しない
         */
        if (RaceManager.getRace(human.getUniqueID()).getUsingKiller() != null) {
            Player player = (Player) human.getBukkitEntity();
            Racer r = RaceManager.getRace(player);

            setNoclip(this, true);
            setSpeedStack(getKart().getMaxSpeed());

            //〓〓モーション初期化
            if (this.getKillerX() != 0 && this.getKillerY() != 0 && this.getKillerZ() != 0) {
                setMotionX(this, this.getKillerX());
                setMotionY(this, this.getKillerY());
                setMotionZ(this, this.getKillerZ());
            }
            setYawPitch(Util.getYawFromVector(new Vector(getMotionX(this), getMotionY(this), getMotionZ(this))) + 180, 0);

            //〓〓演出
            player.playSound(player.getLocation(), Sound.GHAST_FIREBALL, 0.05F, 1.5F);
            player.playSound(player.getLocation(), Sound.FIZZ, 0.05F, 1.0F);
            Util.createSafeExplosion(player, getBukkitEntity().getLocation()
                    , ItemEnum.KILLER.getMovingDamage()
                    + RaceManager.getRace(player).getCharacter().getAdjustAttackDamage(), 2);

            Location current = getBukkitEntity().getLocation().add(0, 0.5, 0);
            current.setYaw(current.getYaw() + 270);
            Location ll = Util.getFrontBackLocationFromYaw(current, -15);

            for (int i = 0; i < 10; i++) {
                Particle.sendToLocation(
                        "REDSTONE",
                        ll.add(((double) Util.getRandom(10)) / 10, ((double) Util.getRandom(10)) / 10,
                                ((double) Util.getRandom(10)) / 10), 0, 0, 0, 0, 10);
            }

            //〓〓チェックポイント
            //初回起動
            if (this.getKillerPassedCheckPointList() == null) {
                this.setKillerPassedCheckPointList(new ArrayList<String>(r.getPassedCheckPoint()));
                this.setKillerLastPassedCheckPoint(RaceManager.getRace((Player) human.getBukkitEntity()).getUsingKiller());
                Vector v = Util.getVectorToLocation(
                        getBukkitEntity().getLocation(),
                        getKillerLastPassedCheckPoint().getLocation().add(0, -RaceManager.checkPointHeight + 2, 0)).multiply(1.8);
                this.setKillerX(v.getX());
                this.setKillerY(v.getY());
                this.setKillerZ(v.getZ());
            }
            if (this.getKillerLastPassedCheckPoint() != null)
                if (RaceManager.checkPointHeight + 5 < this.getKillerLastPassedCheckPoint().getLocation().distance(
                        getBukkitEntity().getLocation()))
                    return;

            ArrayList<org.bukkit.entity.Entity> checkpoint = new ArrayList<org.bukkit.entity.Entity>();
            String lap = r.getLapCount() <= 0 ? "" : String.valueOf(r.getLapCount());
            ArrayList<org.bukkit.entity.Entity> templist = RaceManager.getNearbyCheckpoint(player.getLocation(), 40,
                    r.getEntry());
            if (templist == null)
                return;

            for (org.bukkit.entity.Entity e : templist) {
                if (!this.getKillerPassedCheckPointList().contains(lap + e.getUniqueId().toString())) {
                    checkpoint.add(e);
                }
            }

            if (checkpoint.isEmpty()) {
                return;
            }

            this.setKillerLastPassedCheckPoint(Util.getNearestEntity(checkpoint, this.getBukkitEntity().getLocation()));
            this.getKillerPassedCheckPointList().add(lap + this.getKillerLastPassedCheckPoint().getUniqueId().toString());
            Vector v = Util.getVectorToLocation(
                    this.getBukkitEntity().getLocation(),
                    this.getKillerLastPassedCheckPoint().getLocation().add(0, -RaceManager.checkPointHeight + 2, 0)).multiply(1.8);
            this.setKillerX(v.getX());
            this.setKillerY(v.getY());
            this.setKillerZ(v.getZ());
        } else {
            if (!RaceManager.isRacing(human.getUniqueID()))
                return;

            float sideMotion = getSideMotionInput(human) * 0.0F;//横方向への移動速度(+-0.98固定)
            float forwardMotion = getForwardMotionInput(human);//縦方向への移動速度(+-3.92固定)

            setNoclip(this, false);
            this.setKillerPassedCheckPointList(null);
            this.setKillerLastPassedCheckPoint(null);
            this.setKillerX(0);
            this.setKillerY(0);
            this.setKillerZ(0);

            calcSpeedStack(human);

            if (0 < forwardMotion) {
                forwardMotion *= 0.1;
                forwardMotion += this.getSpeedStack() / 400;
            } else if (forwardMotion < 0) {
                if (isDirtBlock())
                    forwardMotion *= getKart().getSpeedDecreaseOnDirt() * 0.1;
                else
                    forwardMotion *= 0.1;
            }

            //コーナリング性能
            if (Permission.hasPermission((Player) human.getBukkitEntity(), Permission.KART_DRIFT, true)) {
                if (human.isSneaking()) {
                    setYaw(this, (float) (getYaw(this) - getSideMotionInput(human)
                            * getKart().getDriftCorneringPower()));
                } else {
                    setYaw(this, (float) (getYaw(this) - getSideMotionInput(human)
                            * getKart().getDefaultCorneringPower()));
                }

                if (human.isSneaking()) {
                    if (100 < this.getSpeedStack()) {
                        Location current = getBukkitEntity().getLocation();
                        current.setYaw(current.getYaw() + 270);
                        Location ll = Util.getFrontBackLocationFromYaw(current, -this.getSpeedStack() / 60);

                        Particle.sendToLocation("LAVA", ll, 0, 0, 0, 0, 5);
                    }
                }
            } else {
                setYaw(this, (float) (getYaw(this) - getSideMotionInput(human)
                        * getKart().getDefaultCorneringPower()));
            }

            Location current = getBukkitEntity().getLocation().add(0, 0.5, 0);
            current.setYaw(current.getYaw() + 270);
            Location ll = Util.getFrontBackLocationFromYaw(current, -1.0 - this.getSpeedStack() / 30);

            Particle.sendToLocation("SPELL",
                    ll.add(((double) Util.getRandom(4)) / 10, 0.5, ((double) Util.getRandom(4)) / 10)
                    , 0, 0, 0, 0, 5);

            setYawPitch(getYaw(this), 0);
            calcMotion(forwardMotion, sideMotion, getMotionInputStrength(human) / 2.0F);
        }
        //はしご、つたのようなよじ登れるブロックに立っている場合
        if (isClambableBlock()) {
            float f4 = 0.15F;
            setMotionX(this, MathHelper.a(getMotionX(this), -f4, f4));
            setMotionZ(this, MathHelper.a(getMotionZ(this), -f4, f4));
            setFallDistance(this, 0.0F);
            if (getMotionY(this) < -0.15D) {
                setMotionY(this, -0.15D);
            }
        }

        move(getMotionX(this), getMotionY(this), getMotionZ(this));
        if (isPositionChanged(this) && isClambableBlock()) {
            setMotionY(this, 0.2D + this.getSpeedStack() / 300);
        }

        BlockPosition currentPositon = getCurrentBlockPosition(this);
        boolean isLoadedWorld = this.getWorld().isLoaded(currentPositon);
        boolean isLoadedChunk = isLoadedChunk(this);
        if (isClientSide(this) && (!isLoadedWorld || !isLoadedChunk)) {
            if (getLocationY(this) > 0.0D) {
                setMotionY(this, -0.1D);
            } else {
                setMotionY(this, 0.0D);
            }
        } else {
            setMotionY(this, getMotionY(this) - 0.08D);
        }

        setMotionY(this, getMotionY(this) * 0.9800000190734863D);
        //setMotionX(getMotionX() * groundFriction);
        //setMotionZ(getMotionZ() * groundFriction);

        //スピードメーター
        if (RaceManager.getRace((Player) human.getBukkitEntity()).getUsingKiller() == null) {
            ((Player) human.getBukkitEntity()).setLevel(calcMotionSpeed(getMotionX(this), getMotionZ(this)));
        }
    }

    public void calcSpeedStack(EntityHuman human) {
        Player player = (Player) human.getBukkitEntity();
        if (RaceManager.getRace(player).getStepDashBoard()) {
            this.setSpeedStack(getKart().getMaxSpeed()
                    * (Integer) ConfigEnum.ITEM_DASH_BOARD_EFFECT_LEVEL.getValue()
                    + RaceManager.getRace(player).getCharacter().getAdjustPositiveEffectLevel() * 50);
            return;
        }
        for (PotionEffect potion : player.getActivePotionEffects()) {
            if (potion.getType().getName().equalsIgnoreCase("SPEED")) {
                setSpeedStack(getKart().getMaxSpeed() + potion.getAmplifier() * 10);
                return;
            } else if (potion.getType().getName().equalsIgnoreCase("SLOW")) {
                if (this.getSpeedStack() < potion.getAmplifier())
                    this.setSpeedStack(0);
                else
                    this.setSpeedStack(this.getSpeedStack() - potion.getAmplifier());
                return;
            }
        }

        if (0 < getForwardMotionInput(human)) {
            if (!isDirtBlock()) {
                if (this.getSpeedStack() < getKart().getMaxSpeed()) {
                    this.setSpeedStack(this.getSpeedStack() + getKart().getAcceleration());
                } else {
                    //キノコ等で急加速した場合一時的にmaxSpeedStackを超えるため
                    this.setSpeedStack(getKart().getMaxSpeed());
                }
            } else {
                this.setSpeedStack(this.getSpeedStack()
                        - RaceManager.getRace(player).getKart().getSpeedDecreaseOnDirt());
            }

            if (human.isSneaking()) {
                this.setSpeedStack(this.getSpeedStack() - getKart().getSpeedDecreaseOnDrift());
                ((Player) human.getBukkitEntity()).playSound(human.getBukkitEntity().getLocation(),
                        Sound.FIREWORK_BLAST, 1.0F, 7.0F);
            }
        } else if (0 == getForwardMotionInput(human)) {
            //入力がなく停止している状態
            if (0 < this.getSpeedStack()) {
                this.setSpeedStack(this.getSpeedStack() - 4);
            }
        }
        if (getForwardMotionInput(human) < 0) {
            //後方へ入力されている状態
            if (0 < this.getSpeedStack()) {
                this.setSpeedStack(this.getSpeedStack() - 10);
            }
        }
        if (this.getSpeedStack() < 0) {
            this.setSpeedStack(0);
        }
        BigDecimal bd = new BigDecimal((getMotionX(this) * getMotionX(this)
                + getMotionZ(this) * getMotionZ(this)));
        double mot = bd.setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (mot == 0)
            this.setSpeedStack(0);
    }

    public int calcMotionSpeed(double x, double z) {
        BigDecimal bd = new BigDecimal((x * x + z * z));
        return (int) (bd.doubleValue() * 564);
    }

    /**
     * クライアントからの移動に関する入力をモーション値に変換し格納する
     * @param forwardMotion 縦方向の入力値
     * @param sideMotion 横方向の入力値
     * @param friction 摩擦係数
     */
    public void calcMotion(float forwardMotion, float sideMotion, float friction) {
        float f3 = forwardMotion * forwardMotion + sideMotion * sideMotion;

        if (f3 >= 1.0E-004F) {
            f3 = (float) Math.sqrt(f3);
            if (f3 < 1.0F) {
                f3 = 1.0F;
            }

            f3 = friction / f3;
            forwardMotion *= f3;
            sideMotion *= f3;

            float f4 = MathHelper.sin(getYaw(this) * 3.141593F / 180.0F);
            float f5 = MathHelper.cos(getYaw(this) * 3.141593F / 180.0F);

            setMotionX(this, getMotionX(this) + (forwardMotion * f5 - sideMotion * f4));
            setMotionZ(this, getMotionZ(this) + (sideMotion * f5 + forwardMotion * f4));
        }
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if ((!isClientSide(this)) && (!this.dead)) {
            if (!(damagesource.getEntity() instanceof EntityHuman))
                return false;

            Player player = (Player) damagesource.getEntity().getBukkitEntity();
            if (!Permission.hasPermission(player, Permission.OP_KART_REMOVE, false))
                return false;

            if (getPassenger(this) != null)
                getPassenger(this).mount(null);

            if (getKartType().equals(KartType.DisplayKart)) {
                DisplayKartConfig.deleteDisplayKart(player, this.getCustomName());
            }

            craftBukkit_die();
        }
        return true;
    }

    //エンティティ同士の衝突
    //衝突相手を吹き飛ばす代償として自身のスピードは相殺される
    //スピード50の相手にスピード200で衝突した場合
    //相手はスピード200-50=150の速度で吹き飛び、自身のスピードは200-150=50に減衰する
    //ここで言うスピードとは移動速度ではなく、移動速度に自身の重さを掛け合わせたもの
    //スピードの定義 : calcMotionSpeed() * Karts.getWeight()
    @Override
    public void collide(Entity entity) {
        if (!isClientSide(this) && !isNoclip(entity) && !isNoclip(this) && entity != getPassenger(this)) {
            //エントリーしていて、かつスタートしていないプレイヤーへの衝突は例外としてキャンセル
            org.bukkit.entity.Entity otherpassenger = getEndPassenger(entity).getBukkitEntity();
            if (otherpassenger instanceof Player)
                if (!RaceManager.isRacing(((Player) otherpassenger).getUniqueId()))
                    return;

            Entity other = getEndVehicle(entity);

            double otherspeed = 0;
            if (RaceManager.isSpecificKartType(other.getBukkitEntity(), KartType.RacingKart)) {
                otherspeed = calcMotionSpeed(getMotionX(other), getMotionZ(other))
                        * KartConfig.getKartFromEntity(other.getBukkitEntity()).getWeight();
            } else {
                otherspeed = calcMotionSpeed(getMotionX(other), getMotionZ(other));
            }

            if (this.getLastMotionSpeed() < otherspeed) {
                return;
            }

            double collisionpower = this.getLastMotionSpeed() - otherspeed;
            if (1 < (int) (collisionpower * 0.04 * 2)) {
                Vector v = Util.getVectorToLocation(this.getBukkitEntity().getLocation(),
                        other.getBukkitEntity().getLocation()).setY(0);

                this.setSpeedStack(this.getSpeedStack() - collisionpower < 0
                        ? 0 : this.getSpeedStack() - collisionpower);
                other.getBukkitEntity().setVelocity(
                        other.getBukkitEntity().getVelocity().add(v.multiply(collisionpower * 0.01)));

                org.bukkit.entity.Entity damager = getEndPassenger(this).getBukkitEntity();
                Util.addDamage(other.getBukkitEntity(), damager, (int) (collisionpower * 0.04 * 2));
                for (org.bukkit.entity.Entity damaged : Util.getAllPassenger(other.getBukkitEntity())) {
                    Util.addDamage(damaged, damager, (int) (collisionpower * 0.04 * 2));
                }
                for (org.bukkit.entity.Entity damaged : Util.getAllVehicle(other.getBukkitEntity())) {
                    Util.addDamage(damaged, damager, (int) (collisionpower * 0.04 * 2));
                }
            }
        }
    }
}