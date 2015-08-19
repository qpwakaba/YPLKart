package com.github.erozabesu.yplkart.override.v1_8_R2;

import java.util.List;

import net.minecraft.server.v1_8_R2.DamageSource;
import net.minecraft.server.v1_8_R2.Entity;
import net.minecraft.server.v1_8_R2.EntityArmorStand;
import net.minecraft.server.v1_8_R2.EntityHuman;
import net.minecraft.server.v1_8_R2.Vec3D;
import net.minecraft.server.v1_8_R2.World;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftEntity;
import org.bukkit.scheduler.BukkitTask;

import com.github.erozabesu.yplkart.object.Kart;
import com.github.erozabesu.yplkart.object.KartType;
import com.github.erozabesu.yplkart.reflection.Fields;
import com.github.erozabesu.yplkart.reflection.Methods;
import com.github.erozabesu.yplkart.utils.KartUtil;
import com.github.erozabesu.yplkart.utils.ReflectionUtil;

/**
 * カートエンティティとしてスポーンさせるアーマースタンドエンティティクラス
 * @author erozabesu
 */
public class CustomArmorStand extends EntityArmorStand {

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

    /** Kartオブジェクト */
    private Kart kart;

    /** KartType */
    private KartType kartType;

    /** スピードスタック */
    private double speedStack = 0;

    /** 1チック前のモーション値 */
    private double lastMotionSpeed;

    /**
     * キラー使用後の初回動作かどうかを確認するためのフラグ
     * カートの外見を毎チック変更する必要がないように宣言している
     */
    private boolean killerInitialized;

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

    /**
     * エンティティが生存しているかどうかをチェックするタスク<br>
     * die()メソッドをOverrideしてもチャンクのアンロードによるデスポーンを検出不可能なため
     * タスクを起動して確認する
     */
    private BukkitTask livingCheckTask;

    //〓 Main 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public CustomArmorStand(World world, Kart kart, KartType kartType, Location location) {
        super(world);

        //生存チェックタスク
        KartUtil.runLivingCheckTask(this);

        //以下順序を変更しないこと

        //カートタイプセット
        setKartType(kartType);

        //カートタイプによって異なる項目を設定
        this.yaw = location.getYaw();
        if (getKartType().equals(KartType.DisplayKart)) {
            setPosition(location.getX(), location.getY(), location.getZ());
            this.pitch = location.getPitch();
            setYawPitch(this.yaw, this.pitch);
            setGravity(false);
        } else {
            setPosition(location.getX(), location.getY() + 1, location.getZ());
            setYawPitch(this.yaw, 0F);
            setGravity(true);
        }

        //カートのパラメーター
        KartUtil.setParameter(this, kart);

        //外見とかコリジョンとか
        setArms(true);
        setInvisible(true);
        setBasePlate(true);
        ReflectionUtil.setFieldValue(Fields.nmsEntity_noclip, this, !hasGravity());

        //その他
        setKillerInitialized(false);

        //BoundingBox(当たり判定)の変更
        ReflectionUtil.invoke(Methods.nmsEntity_setSize, this, 2.0F, 1.0F);

        //BoudingBoxの変更に伴い位置がズレるためテレポート
        this.getBukkitEntity().teleport(location);
    }

    //〓 Override 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * このクラスのBukkitEntityを返す<br>
     * イベントリスナのVehicle系イベントをフックできるようVehicleクラスを継承した、<br>
     * カスタムCraftEntityクラスのインスタンスを返す
     * @return Vehicleクラスを継承したカスタムCraftEntityクラスのインスタンス
     */
    @Override
    public CraftEntity getBukkitEntity() {
        if (this.bukkitEntity == null ) {
            this.bukkitEntity = new CustomCraftArmorStand(this.world.getServer(), this);
        }
        return this.bukkitEntity;
    }

    /** Entityの状態を毎チック更新する */
    @Override
    public void t_() {//XXX: CraftBukkit Unstable
        super.t_();
        KartUtil.livingUpdate(this);
    }

    /**
     * 水流に接触しているかどうかを判別し、フラグ、及びモーションの適用<br>
     * ディスプレイカートの場合、水流で動いてしまうため何もせずreturn falseする
     * @return 水に接触しているかどうか
     */
    @Override
    public boolean W() {
        if (this.getKartType().equals(KartType.DisplayKart)) {
            return false;
        } else {
            return super.W();
        }
    }

    /**
     * 引数fireTicsのチック数だけNmsEntityを延焼させる<br>
     * 延焼してしまうのを防ぐため何もせずreturnする
     * @param fireTics
     */
    public void setOnFire(int fireTics) {
        super.setOnFire(0);
    }

    /** エンティティ同士の衝突 */
    @Override
    public void collide(Entity entity) {
        KartUtil.moveByCollision(this, entity);
    }

    /** アーマースタンドを左クリックした場合 */
    @Override
    public boolean damageEntity(DamageSource damageSource, float f) {
        return KartUtil.onLeftClicked(this, damageSource);
    }

    /** アーマースタンドを右クリックした場合 */
    @Override
    public boolean a(EntityHuman entityHuman, Vec3D vec3d) {
        return KartUtil.onRightClicked(this, entityHuman);
    }

    //〓 Getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

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

    /** @return killerInitialized キラー使用後の初回動作かどうか */
    public boolean isKillerInitialized() {
        return killerInitialized;
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

    /** @return livingCheckTask 生存チェックタスク */
    public BukkitTask getLivingCheckTask() {
        return livingCheckTask;
    }

    //〓 Setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

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

    /** @param kart Kartオブジェクト */
    public void setKart(Kart kart) {
        this.kart = kart;
    }

    /** @param kartType KartType */
    public void setKartType(KartType kartType) {
        this.kartType = kartType;
    }

    /** @param speedStack スピードスタック */
    public void setSpeedStack(double speedStack) {
        this.speedStack = speedStack;
    }

    /** @param lastMotionSpeed 1チック前のモーション値 */
    public void setLastMotionSpeed(double lastMotionSpeed) {
        this.lastMotionSpeed = lastMotionSpeed;
    }

    /** @param killerInitialized キラー使用後の初回動作かどうか */
    public void setKillerInitialized(boolean killerInitialized) {
        this.killerInitialized = killerInitialized;
    }

    /** @param killerX キラー使用中のX方向のモーション値 */
    public void setKillerX(double killerX) {
        this.killerX = killerX;
    }

    /** @param killerY キラー使用中のY方向のモーション値 */
    public void setKillerY(double killerY) {
        this.killerY = killerY;
    }

    /** @param killerZ キラー使用中のZ方向のモーション値 */
    public void setKillerZ(double killerZ) {
        this.killerZ = killerZ;
    }

    /** @param killerPassedCheckPointList キラー使用中に通過したチェックポイントEntityのUUIDList */
    public void setKillerPassedCheckPointList(List<String> killerPassedCheckPointList) {
        this.killerPassedCheckPointList = killerPassedCheckPointList;
    }

    /** @param killerLastPassedCheckPoint キラー使用中、1チック前に通過したチェックポイントEntity */
    public void setKillerLastPassedCheckPoint(org.bukkit.entity.Entity killerLastPassedCheckPoint) {
        this.killerLastPassedCheckPoint = killerLastPassedCheckPoint;
    }

    /** @param livingCheckTask 生存チェックタスク */
    public void setLivingCheckTask(BukkitTask livingCheckTask) {
        this.livingCheckTask = livingCheckTask;
    }
}