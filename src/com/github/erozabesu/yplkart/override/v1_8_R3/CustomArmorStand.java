package com.github.erozabesu.yplkart.override.v1_8_R3;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.Vec3D;
import net.minecraft.server.v1_8_R3.Vector3f;
import net.minecraft.server.v1_8_R3.World;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.github.erozabesu.yplkart.Permission;
import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.YPLKart;
import com.github.erozabesu.yplkart.data.ConfigEnum;
import com.github.erozabesu.yplkart.data.DisplayKartConfig;
import com.github.erozabesu.yplkart.data.ItemEnum;
import com.github.erozabesu.yplkart.data.KartConfig;
import com.github.erozabesu.yplkart.object.Kart;
import com.github.erozabesu.yplkart.object.KartType;
import com.github.erozabesu.yplkart.object.Racer;
import com.github.erozabesu.yplkart.utils.PacketUtil;
import com.github.erozabesu.yplkart.utils.Particle;
import com.github.erozabesu.yplkart.utils.ReflectionUtil;
import com.github.erozabesu.yplkart.utils.Util;

public class CustomArmorStand extends EntityArmorStand {

    /** デフォルトヘッドポーズ */
    private static final Vector3f a = new Vector3f(0.0F, 0.0F, 0.0F);

    /** デフォルトボディポーズ */
    private static final Vector3f b = new Vector3f(0.0F, 0.0F, 0.0F);

    /** デフォルトレフトアームポーズ */
    private static final Vector3f c = new Vector3f(-27.0F, 1.00F, 0.0F);

    /** デフォルトライトアームポーズ */
    private static final Vector3f d = new Vector3f(-27.0F, 1.00F, 0.0F);

    /** デフォルトレフトレッグポーズ */
    private static final Vector3f e = new Vector3f(-1.0F, 0.0F, -1.0F);

    /** デフォルトライトレッグポーズ */
    private static final Vector3f f = new Vector3f(1.0F, 0.0F, 1.0F);

    private final ItemStack[] items;
    private boolean h;
    private long i;
    private int bi;

    /** マーカーアーマースタンドかどうか */
    private boolean bj;

    /** ヘッドポーズ */
    private Vector3f headPose;

    /** ボディポーズ */
    private Vector3f bodyPose;

    /** レフトアームポーズ */
    private Vector3f leftArmPose;

    /** ライトアームポーズ */
    private Vector3f rightArmPose;

    /** レフトレッグポーズ */
    private Vector3f leftLegPose;

    /**ライトレッグポーズ  */
    private Vector3f rightLegPose;

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

    /**
     * 1チック前のticksLivedの値を格納する
     * livingCheckTaskで利用する<br>
     * エンティティが自然消滅した場合、メンバ変数の値は変更されないまま消滅する<br>
     * そのため、deadフラグやisAlive()メソッドは生前の値のまま放置され、<br>
     * 消滅後でもdeadフラグはfalseであり、isAlive()メソッドはtrueを返す<br>
     * 同様に何チック生存したかを示すticksLivedも消滅後は値が変更されなくなるため、<br>
     * ticksLivedが1チック前と同じ値を示した場合、エンティティの消滅を確認することができる<br>
     * この変数はその確認を行うために宣言している
     */
    private int lastTicksLived = -2;

    //〓 Main 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public CustomArmorStand(World world, Kart kart, KartType kartType, Location location) {
        super(world);

        //生存チェックタスク
        runLivingCheckTask();

        //デフォルトイニシャライズ
        this.items = new ItemStack[5];
        this.headPose = a;
        this.bodyPose = b;
        this.leftArmPose = c;
        this.rightArmPose = d;
        this.leftLegPose = e;
        this.rightLegPose = f;
        this.b(true);

        //以下順序を変更しないこと

        //カートタイプセット
        setKartType(kartType);

        //カートタイプによって異なる項目を設定
        setYaw(this, location.getYaw());
        if (getKartType().equals(KartType.DisplayKart)) {
            setPosition(location.getX(), location.getY() - 0.5D, location.getZ());
            setPitch(this, -location.getPitch());
            setYawPitch(getYaw(this), -getPitch(this));
            setGravity(false);
        } else {
            setPosition(location.getX(), location.getY() + 1, location.getZ());
            setYawPitch(getYaw(this), 0F);
            setGravity(true);
        }

        //カートのパラメーター
        setParameter(kart);

        //外見とかコリジョンとか
        setArms(true);
        setInvisible(true);
        setMarker(false);//XXX: CraftBukkit Unstable
        setBasePlate(true);
        setNoclip(this, !hasGravity());

        //その他
        setKillerInitialized(false);
    }

    //〓 Override 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * livingUpdate<br>
     * Entityの状態を毎チック更新する
     */
    @Override
    public void t_()//XXX: CraftBukkit Unstable
    {
        //搭乗者が死亡していた場合passenger変数を初期化する
        if (getPassenger(this) != null && isDead(getPassenger(this))) {
            if (getVehicle(getPassenger(this)) == this) {
                setVehicle(getPassenger(this), null);
            }

            setPassenger(this, null);
        }

        this.L = this.M;

        //1チック前の座標を記録
        setLastLocationX(this, getLocationX(this));
        setLastLocationY(this, getLocationY(this));
        setLastLocationZ(this, getLocationZ(this));

        //ディスプレイカートの場合何もしないモーションを0に固定
        if (getKartType().equals(KartType.DisplayKart)) {
            // Do nothing

        //ディスプレイカート以外
        } else {

            //BoundingBox内のEntityを検索し、Entityがアーマースタンドであれば当たり判定を発生させる
            AxisAlignedBB boundingBox = getPassenger(this) == null
                    ? getBoundingBox() : getPassenger(this).getBoundingBox();
            Iterator iterator = this.getWorld().getEntities(this,
                    getBoundingBox().grow(0.2000000029802322D, 0.0D, 0.2000000029802322D)).iterator();
            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();
                if (entity != getPassenger(this)) {
                    entity.collide(this);
                }
            }

            //YモーションをマイナスしEntityを自然落下させる
            setMotionY(this, getMotionY(this) - 0.03999999910593033D);

            //プレイヤーが搭乗時、当プラグイン独自のモーション値を適用する
            applyMotion();

            //モーション値に摩擦係数を割り当て減衰させる
            applyFriction();

            //よく分からない
            checkBlockCollisions();
        }

        //クライアントから読み込まれなくなった場合デスポーン
        if (isClientSide(this)) {
            removeEntity();
            return;
        }

        //奈落に落下した場合デスポーン
        if (getLocationY(this) < -64.0D) {
            removeEntity();
        }

        //水に接触している場合水没フラグをtrueにし、落下距離・延焼を初期化する
        applyWaterCollision();

        //よくわからない
        //EntityLivingクラスのこのメソッドで実行されている
        //superしていないため念のためやっておく
        this.justCreated = false;
        this.world.methodProfiler.b();

        //XXX: CraftBukkit Unstable 1.8.1以前ではMarker機能がないため、この行以降は存在しない
        //よく分からない
        boolean hasMarker = hasMarker();
        if (!this.bj && hasMarker) {
            removeBoundingBox(false);
        } else {
            if (!this.bj || hasMarker) {
                return;
            }

            removeBoundingBox(true);
        }

        this.bj = hasMarker;

        removeBoundingBox(true);
    }

    //エンティティ同士の衝突
    //衝突相手を吹き飛ばす代償として自身のスピードは相殺される
    //スピード50の相手にスピード200で衝突した場合
    //相手はスピード200-50=150の速度で吹き飛び、自身のスピードは200-150=50に減衰する
    //ここで言うスピードとは移動速度ではなく、移動速度に自身の重さを掛け合わせたもの
    //スピードの定義 : calcMotionSpeed() * Karts.getWeight()
    @Override
    public void collide(Entity entity) {
        if (!isClientSide(this) && entity != getPassenger(this)) {
            if (!RaceManager.isKartEntity(entity.getBukkitEntity()) && isNoclip(entity)) {
                return;
            }

            applyCollideMotion(this);

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

    /**
     * leftClicked<br>
     * アーマースタンドを左クリックした場合<br>
     * 通常ダメージを受けた場合の処理を行うが、プレイヤーの左クリック以外ではダメージを受けないよう変更している<br>
     * カートの破壊パーミッションを所有している場合のみカートエンティティを削除する
     */
    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (!isClientSide(this) && !this.dead) {
            if (!(damagesource.getEntity() instanceof EntityHuman)) {
                return false;
            }

            Player player = (Player) damagesource.getEntity().getBukkitEntity();
            if (!Permission.hasPermission(player, Permission.OP_KART_REMOVE, false)) {
                return false;
            }

            if (getPassenger(this) != null) {
                getPassenger(this).mount(null);
            }

            if (getKartType().equals(KartType.DisplayKart)) {
                DisplayKartConfig.deleteDisplayKart(player, this.getCustomName());
            }

            removeEntity();
        }
        return true;
    }

    /**
     * rightClicked<br>
     * アーマースタンドを右クリックした場合<br>
     * 通常アーマースタンドにアイテムを装備させる、もしくはアイテムを剥ぎ取る操作を行うがキャンセルし、
     * 搭乗可能な状態なら搭乗させる
     */
    @Override
    public boolean a(EntityHuman entityhuman, Vec3D vec3d) {

        //既に搭乗者が居た場合
        //搭乗者がクリックしたプレイヤー以外
        if (getPassenger(this) != null && getPassenger(this) != entityhuman) {

            //搭乗者がプレイヤーエンティティ
            if (!(getPassenger(this) instanceof EntityHuman)) {
                //何もせずリターン
                return false;

            //搭乗者がプレイヤーエンティティ以外のエンティティ
            } else {
                //カートから降ろす
                getPassenger(this).mount(null);
            }
        }

        //レースカート以外のカートであれば搭乗させる
        if (!getKartType().equals(KartType.RacingKart)) {
            if (!isClientSide(this)) {
                entityhuman.mount(this);
            }
        }

        return false;

        //アイテム操作はキャンセル
        //return super.a(entityhuman, vec3d);
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

    /** @return lastTicksLived 1チック前のticksLived */
    public int getLastTicksLived() {
        return lastTicksLived;
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

    /** @param lastTicksLived 1チック前のticksLived */
    public void setLastTicksLived(int lastTicksLived) {
        this.lastTicksLived = lastTicksLived;
    }

    //〓 Getter - CraftBukkit 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * @param entity 取得するEntity
     * @return クライアントとの同期が必要な状態かどうか
     */
    public boolean isClientSide(Entity entity) {
        return entity.getWorld().isClientSide;//XXX: CraftBukkit Unstable
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
        return entity.noclip;//XXX: CraftBukkit Unstable
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

    /** @return マーカーアーマースタンドに設定されているかどうか */
    public boolean hasMarker() {
        return s();//XXX: CraftBukkit Unstable
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
        return human.ba;//XXX: CraftBukkit Unstable
    }

    /**
     * 引数humanを操作しているクライアントの横方向の移動に関する入力係数を返す
     * @param human 取得するEntityHuman
     * @return 縦方向の入力係数
     */
    public float getSideMotionInput(EntityHuman human) {
        return human.aZ;//XXX: CraftBukkit Unstable
    }

    /**
     * 引数humanを操作しているクライアントの移動に関する入力の強さ
     * @param human 取得するEntityHuman
     * @return 入力の強さ
     */
    public float getMotionInputStrength(EntityHuman human) {
        return human.bI();//XXX: CraftBukkit Unstable
    }

    /** @return 摩擦係数（固定で0.4D） */
    public double getFrictionValue() {
        return 0.4D;
    }

    /** @return 当たり判定に基づいた現在のY座標 */
    public double getLocationYByBoundingBox() {
        return getBoundingBox().b;
    }

    //〓 Setter - CraftBukkit 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * @param entity 値を変更するEntity
     * @param isNoclip 物理判定が無効な状態かどうか
     */
    public void setNoclip(Entity entity, boolean isNoclip) {
        entity.noclip = isNoclip;//XXX: CraftBukkit Unstable
    }

    /**
     * BoundingBoxの基となるエンティティの横幅、縦幅を設定する
     * このメソッド実行と同時に、BoundingBoxの更新、当たり判定によるmove()が実行される
     * @param width エンティティの横幅
     * @param length エンティティの縦幅
     */
    public void setEntitySize(float width, float length) {
        setSize(width, length);//XXX: CraftBukkit Unstable
    }

    /**
     * マーカーアーマースタンドかどうかを変更する
     * @param hasMarker マーカーアーマースタンドかどうか
     */
    public void setMarker(boolean hasMarker) {
        byte b0 = this.datawatcher.getByte(10);

        if (hasMarker) {
            b0 = (byte) (b0 | 16);
        } else {
            b0 &= -17;
        }

        this.datawatcher.watch(10, Byte.valueOf(b0));
    }

    /** エンティティとの衝突判定を削除する */
    public void removeBoundingBox(boolean flag) {
        double d0 = getLocationX(this);
        double d1 = getLocationY(this);
        double d2 = getLocationZ(this);

        if (flag) {
            setEntitySize(0.5F, 1.975F);
        } else {
            setEntitySize(0.0F, 0.0F);
        }

        this.setPosition(d0, d1, d2);

        this.bj = flag;
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

    //〓 Util Get/Set 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

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
        int locationY = MathHelper.floor(getLocationYByBoundingBox());
        int locationZ = MathHelper.floor(getLocationZ(this));
        Block block = this.getWorld().getType(
                new BlockPosition(locationX, locationY, locationZ)).getBlock();

        return (block == Blocks.LADDER || block == Blocks.VINE);
    }

    /** @return ダートブロックかどうか */
    public boolean isDirtBlock() {
        int locationX = MathHelper.floor(getLocationX(this));
        int locationY = MathHelper.floor(getLocationYByBoundingBox());
        int locationZ = MathHelper.floor(getLocationZ(this));

        Location location =
                new Location(this.getBukkitEntity().getWorld(), locationX, locationY, locationZ);

        return Util.getGroundBlockID(location).equalsIgnoreCase(
                (String) ConfigEnum.DIRT_BLOCK_ID.getValue());
    }

    /**
     * パラメータを引数kartのオブジェクトから引き継ぐ
     * @param kart パラメータを引き継ぐKartオブジェクト
     */
    public void setParameter(Kart kart) {
        setKart(kart);
        setClimbableHeight(this, kart.getClimbableHeight());
        setDisplayMaterial(kart.getDisplayMaterial(), kart.getDisplayMaterialData());
    }

    /**
     * カートの外見を指定されたマテリアルに変更する
     * 正確には、アーマースタンドが手に持っているアイテムを変更する
     * @param displayMaterial 新たに表示するアイテムのマテリアル
     * @param displayMaterialData 新たに表示するアイテムのマテリアルデータ
     */
    public void setDisplayMaterial(org.bukkit.Material displayMaterial, byte displayMaterialData) {

        //Bukkitマテリアルからクラフトアイテムスタックへ変換
        org.bukkit.inventory.ItemStack bukkitItemStack =
                new org.bukkit.inventory.ItemStack(displayMaterial, 1, (short) 0,displayMaterialData);
        Object craftItemStack = ReflectionUtil.getCraftItemStack(bukkitItemStack);

        //右手のアイテムの変更
        setEquipment(0, (ItemStack) craftItemStack);

        //装備を変更した外見のパケットを送信
        //データ上手に持っているアイテムは書き換わっているが、見た目の更新は何故かされないため
        //明示的にパケットを送信する
        PacketUtil.sendEntityEquipmentPacket(null, getId(), 0, craftItemStack);

        //腕の角度を調整
        setRightArmPose(new Vector3f(-26.0F + getPitch(this), 1.00F, 0.0F));
    }

    /**
     * カートのモーションを適用する
     * キラー使用中のモーションを適用する場合はapplyKillerMotion(Racer r)を利用する
     * @param human 計算の基となるEntityHuman
     */
    public void setNormalMotion(EntityHuman human) {
        //キラー用変数の初期化
        this.setKillerPassedCheckPointList(null);
        this.setKillerLastPassedCheckPoint(null);
        this.setKillerX(0);
        this.setKillerY(0);
        this.setKillerZ(0);

        //横方向への移動入力値
        float sideInput = getSideMotionInput(human) * 0.0F;

        //縦方向への移動入力値
        float forwardInput = getForwardMotionInput(human);

        //スピードスタックの算出、格納
        setSpeedStack(calcSpeedStack(human));

        //スピードスタックを基に縦方向への移動入力値を変換
        forwardInput = calcForwardInput(forwardInput);

        //横方向への移動入力値を基にYawを変更
        Player player = (Player) human.getBukkitEntity();
        if (Permission.hasPermission(player, Permission.KART_DRIFT, true)) {
            if (RaceManager.getRace(player).isSneaking()) {
                setYaw(this, (float) (getYaw(this) - getSideMotionInput(human)
                        * getKart().getDriftCorneringPower()));
            } else {
                setYaw(this, (float) (getYaw(this) - getSideMotionInput(human)
                        * getKart().getDefaultCorneringPower()));
            }
        } else {
            setYaw(this, (float) (getYaw(this) - getSideMotionInput(human)
                    * getKart().getDefaultCorneringPower()));
        }
        setYawPitch(getYaw(this), 0);

        //移動入力値を基にモーションを算出し格納
        float normalizeMotion = forwardInput * forwardInput + sideInput * sideInput;

        if (normalizeMotion >= 1.0E-004F) {
            normalizeMotion = (float) Math.sqrt(normalizeMotion);
            if (normalizeMotion < 1.0F) {
                normalizeMotion = 1.0F;
            }

            normalizeMotion = (getMotionInputStrength(human) / 2.0F) / normalizeMotion;
            forwardInput *= normalizeMotion;
            sideInput *= normalizeMotion;

            float sin = MathHelper.sin(getYaw(this) * 3.141593F / 180.0F);
            float cos = MathHelper.cos(getYaw(this) * 3.141593F / 180.0F);

            setMotionX(this, getMotionX(this) - (forwardInput * sin - sideInput * cos));
            setMotionZ(this, getMotionZ(this) + (sideInput * sin + forwardInput * cos));
        }
    }

    /**
     * キラー使用中のモーションを適用する<br>
     * 最寄の未通過のチェックポイントに向けたモーションを算出し適用する<br>
     * <br>
     * 未通過のチェックポイントを検出するため、通過済みのチェックポイントをリストとして保管する<br>
     * ただし、Racerオブジェクトから取得できる通過済みリストとは異なり、別の変数として新規に宣言している<br>
     * これは、キラー使用中はコースアウトを防ぐため、チェックポイントとの平面距離が5ブロック以内になるまで次のチェックポイントへは移動しない、<br>
     * という処理を実現するためである<br>
     * Racerオブジェクトの通過済みリストは、周囲のEntityを取得し、検出された全チェックポイントを無差別に格納している<br>
     * この場合、チェックポイントとの距離が5ブロック以内に差し掛かった際に、既に隣接するチェックポイントは通過済みリストに含まれているため、<br>
     * 次の未通過のチェックポイントが検出不可能となる<br>
     * そこで、通過済みリストを別の変数として宣言し、キラー使用中に限り、<br>
     * 距離が5ブロック以内チェックポイントのみを通過済みリストに格納するよう処理している
     *
     * @param r
     */
    public void setKillerMotion(Racer r) {
        int checkPointHeihgt = RaceManager.checkPointHeight;
        Location location = getBukkitEntity().getLocation();

        //モーションの適用
        if (this.getKillerX() != 0 &&  getKillerZ() != 0) {
            setMotionX(this, getKillerX());
            setMotionZ(this, getKillerZ());
        }
        setYawPitch(Util.getYawFromVector(new Vector(getMotionX(this), getMotionY(this), getMotionZ(this))) + 90, 0);


        //初回起動時のみ
        if (getKillerPassedCheckPointList() == null) {
            //キラー使用者が通過済みのチェックポイントリストを引き継ぐ
            setKillerPassedCheckPointList(new ArrayList<String>(r.getPassedCheckPoint()));

            //キラー使用時に取得した、最寄の未通過のチェックポイントを格納
            setKillerLastPassedCheckPoint(r.getUsingKiller());

            //最寄の未通過のチェックポイントへ向けたベクターを算出
            Vector vector = Util.getVectorToLocation(
                    location,
                    getKillerLastPassedCheckPoint().getLocation().add(0, -checkPointHeihgt, 0)).multiply(1.5);

            //算出したベクターのX、Zモーションを格納
            //Yモーションは利用しない
            setKillerX(vector.getX());
            setKillerZ(vector.getZ());
        }

        //最寄の未通過のチェックポイントとの平面距離が5ブロック以内になるまでreturnする
        if (getKillerLastPassedCheckPoint() != null) {
            double distance = getKillerLastPassedCheckPoint().getLocation()
                    .distance(location.clone().add(0,checkPointHeihgt,0));
            if ( 5 < distance) {
                return;
            }
        }

        //周囲のチェックポイントを取得し、検出できなかった場合return
        ArrayList<org.bukkit.entity.Entity> chackPointList = RaceManager.getNearbyCheckpoint(location, 40, r.getEntry());
        if (chackPointList == null) {
            return;
        }

        //周囲のチェックポイントの内、未通過のチェックポイントを抽出
        ArrayList<org.bukkit.entity.Entity> unpassedCheckPointList = new ArrayList<org.bukkit.entity.Entity>();
        String lap = r.getLapCount() <= 0 ? "" : String.valueOf(r.getLapCount());
        for (org.bukkit.entity.Entity checkPoint : chackPointList) {
            if (!this.getKillerPassedCheckPointList().contains(lap + checkPoint.getUniqueId().toString())) {
                unpassedCheckPointList.add(checkPoint);
            }
        }

        //未通過のチェックポイントが検出できなかった場合return
        if (unpassedCheckPointList.isEmpty()) {
            return;
        }

        //最寄の未通過のチェックポイントを格納
        setKillerLastPassedCheckPoint(Util.getNearestEntity(unpassedCheckPointList, location));

        //最寄の未通過のチェックポイントへ向けたベクターを算出
        Vector v = Util.getVectorToLocation(
                location,
                getKillerLastPassedCheckPoint().getLocation().add(0, -RaceManager.checkPointHeight, 0)).multiply(1.5);

        //算出したベクターのX、Zモーションを格納
        //Yモーションは利用しない
        this.setKillerX(v.getX());
        this.setKillerZ(v.getZ());

        //チェックポイントを通過済みリストに格納
        getKillerPassedCheckPointList().add(lap + getKillerLastPassedCheckPoint().getUniqueId().toString());
    }

    //〓 Util Do 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * エンティティが生存しているかどうかをチェックするタスク<br>
     * O()、die()メソッドをOverrideしてもチャンクのアンロードによるデスポーンを検出不可能なため
     * タスクを起動して確認する
     */
    public void runLivingCheckTask() {
        setLivingCheckTask(
            Bukkit.getScheduler().runTaskTimer(YPLKart.getInstance(), new Runnable() {
                public void run() {
                    setLastTicksLived(getLastTicksLived() + 1);
                    try {
                        if (getLastTicksLived() == ticksLived) {
                            RaceManager.removeKartEntityIdMap(getId());
                            getLivingCheckTask().cancel();
                        }
                    } catch(Exception e) {
                        getLivingCheckTask().cancel();
                    }
                }
            }, 0, 1)
        );
    }

    /** 当クラスのEntityを死亡させる */
    public void removeEntity() {
        this.die();
    }

    public void applyCollideMotion(Entity entity) {
        double d0 = entity.locX - this.locX;
        double d1 = entity.locZ - this.locZ;
        double d2 = MathHelper.a(d0, d1);

        if (d2 >= 0.009999999776482582D) {
            d2 = (double) MathHelper.sqrt(d2);
            d0 /= d2;
            d1 /= d2;
            double d3 = 1.0D / d2;

            if (d3 > 1.0D) {
                d3 = 1.0D;
            }

            d0 *= d3;
            d1 *= d3;
            d0 *= 0.05000000074505806D;
            d1 *= 0.05000000074505806D;
            d0 *= (double) (1.0F - this.U);
            d1 *= (double) (1.0F - this.U);
            if (this.passenger == null) {
                this.g(-d0, 0.0D, -d1);
            }

            if (entity.passenger == null) {
                entity.g(d0, 0.0D, d1);
            }
        }
    }

    /** 現在のモーション値に摩擦係数を適用し徐々に減衰させる */
    public void applyFriction() {
        //搭乗者がいる場合
        if (getPassenger(this) != null) {

            //プレイヤーが搭乗している場合
            if (getPassenger(this) instanceof EntityHuman) {
                // Do nothing

            //プレイヤー以外が搭乗している場合
            } else {
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

    /** 水に接触している場合水没フラグをtrueにし、落下距離・延焼を初期化する */
    public void applyWaterCollision() {
        W();
    }

    /**
     * プレイヤーが登場時のカートのモーションを当プラグイン独自の値に変換し適用する
     * @param human カートに搭乗しているEntityHuman
     */
    public void applyMotion() {

        //EntityHumanが搭乗していない場合は何もしない
        if (getPassenger(this) == null) {
            return;
        }
        if (!(getPassenger(this) instanceof EntityHuman)) {
            return;
        }

        //モーション値を変換する前に現在のモーション値を残しておく
        //collide()で使用する
        setLastMotionSpeed(calcMotionSpeed(getMotionX(this), getMotionZ(this)) * this.getKart().getWeight());

        EntityHuman human = (EntityHuman) getPassenger(this);
        Player player = (Player) human.getBukkitEntity();
        Racer r = RaceManager.getRace(player);

        //キラー使用中
        if (r.getUsingKiller() != null) {

            //キラー利用後の初回チックの場合
            if (!isKillerInitialized()) {

                //フラグをオンにする
                setKillerInitialized(true);

                //外見をキラーに変更する
                setDisplayMaterial(ItemEnum.KILLER.getDisplayBlockMaterial(), ItemEnum.KILLER.getDisplayBlockMaterialData());

                //よじ登れる高さを一時的に高くする
                setClimbableHeight(this, getKart().getClimbableHeight() + 5);
            }

            //スピードスタックを一時的に常に最大値に固定する
            setSpeedStack(getKart().getMaxSpeed());

            //キラー専用モーションの適用
            setKillerMotion(r);

            //キラー用エフェクトの再生
            playKillerEffect(human);

            //周囲のプレイヤーへダメージ
            Util.createSafeExplosion(player, getBukkitEntity().getLocation()
                    , ItemEnum.KILLER.getMovingDamage()
                    + RaceManager.getRace(player).getCharacter().getAdjustAttackDamage(), 4);
        } else {
            //レースが開始されるまで動かない
            if (!RaceManager.isRacing(human.getUniqueID())) {
                return;
            }

            //キラーの効果が切れた場合
            if (isKillerInitialized()) {

                //フラグを元に戻す
                setKillerInitialized(false);

                //外見を本来のカートに戻す
                setDisplayMaterial(getKart().getDisplayMaterial(), getKart().getDisplayMaterialData());

                //よじ登れる高さを元に戻す
                setClimbableHeight(this, getKart().getClimbableHeight());
            }

            //モーションの適用
            setNormalMotion(human);

            //アイドリングエフェクトの再生
            playIdleEffect(human);

            //ドリフトエフェクトの再生
            playDriftEffect(human);

            //スピードメーター
            player.setLevel(calcMotionSpeed(getMotionX(this), getMotionZ(this)));
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
    }

    /** 地面のブロックに応じたパーティクルを発生させる */
    public void playGroundParticle() {
        Z();
    }

    /** エンジン音、排気口のパーティクルを生成する */
    public void playIdleEffect(EntityHuman human) {
        Location currentLocation = getBukkitEntity().getLocation().add(0, 0.5, 0);
        currentLocation.setYaw(currentLocation.getYaw());

        //排気口付近の座標を取得
        currentLocation =
                Util.getForwardLocationFromYaw(currentLocation, -1.5 - this.getSpeedStack() / 60);

        //座標にランダム性を持たせ、排気口付近にパーティクルを散らす
        double[] particleOffSet = new double[]{
                Double.valueOf(Util.getRandom(4)) / 10
                , 0.5
                , Double.valueOf(Util.getRandom(4)) / 10};
        currentLocation.add(particleOffSet[0], particleOffSet[1], particleOffSet[2]);
        Particle.sendToLocation("SPELL", currentLocation, 0, 0, 0, 0, 5);

        //音声を再生
        Player player = (Player) human.getBukkitEntity();
        player.playSound(player.getLocation()
                , Sound.COW_WALK, 0.2F, 0.05F + ((float) this.getSpeedStack() / 200));
        player.playSound(player.getLocation()
                , Sound.GHAST_FIREBALL, 0.01F + ((float) this.getSpeedStack() / 400), 1.0F);
        player.playSound(player.getLocation()
                , Sound.FIZZ, 0.01F + ((float) this.getSpeedStack() / 400), 0.5F);
    }

    /** ドリフト中の火花パーティクルを生成する */
    public void playDriftEffect(EntityHuman human) {
        Player player = (Player) human.getBukkitEntity();
        if (RaceManager.getRace(player).isSneaking()) {

            //スピードスタックが100を越える場合のみ火花のパーティクルを生成する
            if (100 < getSpeedStack()) {
                Location currentLocation = getBukkitEntity().getLocation();
                currentLocation.setYaw(currentLocation.getYaw());

                //後輪付近の座標を取得
                currentLocation = Util.getForwardLocationFromYaw(currentLocation, -this.getSpeedStack() / 60);

                //後輪付近に火花のパーティクルを散らす
                Particle.sendToLocation("LAVA", currentLocation, 0, 0, 0, 0, 5);
            }

            //音声を再生
            player.playSound(player.getLocation(), Sound.FIREWORK_BLAST, 1.0F, 7.0F);
        }
    }

    /** キラー使用中のエンジン音、排気口のパーティクルを生成する */
    public void playKillerEffect(EntityHuman human) {
        Location currentLocation = getBukkitEntity().getLocation().add(0, 0.5, 0);
        currentLocation.setYaw(currentLocation.getYaw());

        //キラーの排気口付近の座標を取得
        currentLocation = Util.getForwardLocationFromYaw(currentLocation, -15);

        //座標にランダム性を持たせ、排気口付近に赤いパーティクルを散らす
        double[] particleOffSet;
        for (int i = 0; i < 10; i++) {
            particleOffSet = new double[]{
                    Double.valueOf(Util.getRandom(10)) / 10
                    , Double.valueOf(Util.getRandom(10)) / 10
                    , Double.valueOf(Util.getRandom(10)) / 10};
            currentLocation.add(particleOffSet[0], particleOffSet[1], particleOffSet[2]);
            Particle.sendToLocation("REDSTONE", currentLocation, 0, 0, 0, 0, 10);
        }

        //音声を再生
        Player player = (Player) human.getBukkitEntity();
        player.playSound(player.getLocation(), Sound.GHAST_FIREBALL, 0.05F, 1.5F);
        player.playSound(player.getLocation(), Sound.FIZZ, 0.05F, 1.0F);
    }

    //〓 Util Other 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 引数humanの情報を基にスピードスタックを算出し返す
     * @param human 取得するHumanEntity
     * @return 引数humanの情報を基に算出したスピードスタック
     */
    private double calcSpeedStack(EntityHuman human) {
        Player player = (Player) human.getBukkitEntity();
        Racer race = RaceManager.getRace(player);

        double speedStack = 0;

        //ダッシュボード、ポーションの効果をスピードスタックに上乗せしreturnする
        //returnしなければ、後の処理で最大値・最小値を越えている場合、正常値にマージされてしまう
        //両効果が重複してしまうと爆発的なスピードが出てしまうため、
        //ダッシュボードに接触している場合はポーションの効果は無視する
        if (race.isStepDashBoard()) {

            //ダッシュボードに接触した場合、スピードスタックを最大値+αしreturnする
            return getKart().getMaxSpeed()
                    * (Integer) ConfigEnum.ITEM_DASH_BOARD_EFFECT_LEVEL.getValue()
                    + race.getCharacter().getAdjustPositiveEffectLevel() * 50;
        } else {

            //スピードに影響するポーション効果を保持している場合、スピードスタックを操作しreturnする
            for (PotionEffect potion : player.getActivePotionEffects()) {

                //スピードポーション効果を保持している場合、スピードスタックを最大値+αしreturnする
                if (potion.getType().getName().equalsIgnoreCase("SPEED")) {
                    return  getKart().getMaxSpeed() + potion.getAmplifier() * 10;
                }

                //スロウポーション効果を保持している場合、スピードスタックを急激に減衰しreturnする
                if (potion.getType().getName().equalsIgnoreCase("SLOW")) {
                    if (getSpeedStack() < potion.getAmplifier()) {
                        return 0;
                    } else {
                        return getSpeedStack() - potion.getAmplifier();
                    }
                }
            }
        }

        float forwardMotionInput = getForwardMotionInput(human);
        //前方へキーを入力している
        if (0 < forwardMotionInput) {
            if (!isDirtBlock()) {
                speedStack = getSpeedStack() + getKart().getAcceleration();
            } else {
                speedStack = getSpeedStack() - race.getKart().getSpeedDecreaseOnDirt();
            }
        //後方へキーを入力している
        } else if (forwardMotionInput < 0) {
            speedStack = getSpeedStack() - 10;

        //入力していない
        } else if (0 == forwardMotionInput) {
            speedStack = getSpeedStack() - 4;
        }

        //最大値・最小値を越えている場合、正常値にマージする
        if (getKart().getMaxSpeed() < getSpeedStack()) {
            speedStack = getKart().getMaxSpeed();
        } else if (getSpeedStack() < 0) {
            speedStack = 0;
        }

        //モーション値が限りなく0に近い場合はスピードスタックを0にする
        //壁に衝突した場合などの急激なモーションのストップに対する処理
        BigDecimal bd = new BigDecimal((getMotionX(this) * getMotionX(this)
                + getMotionZ(this) * getMotionZ(this)));
        double mot = bd.setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (mot == 0) {
            speedStack = 0;
        }

        return speedStack;
    }

    /**
     * スピードスタックを基に、縦方向の移動入力値を変換し返す
     * @param forwardInput 変換前の縦方向の移動入力値
     * @return 変換した縦方向の移動入力値
     */
    private float calcForwardInput(float forwardInput) {
        //前方へキーを入力している
        if (0 < forwardInput) {
            forwardInput *= 0.1;
            forwardInput += getSpeedStack() / 400;

        //後方へキーを入力している
        } else if (forwardInput < 0) {
            if (isDirtBlock()) {
                forwardInput *= getKart().getSpeedDecreaseOnDirt() * 0.1;
            } else {
                forwardInput *= 0.1;
            }

        //入力していない
        } else {
            // Do nothing
        }

        return forwardInput;
    }

    private int calcMotionSpeed(double x, double z) {
        BigDecimal bd = new BigDecimal((x * x + z * z));
        return (int) (bd.doubleValue() * 564);
    }
}