package com.github.erozabesu.yplkart.object;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.data.ItemEnum;
import com.github.erozabesu.yplkart.enumdata.Particle;
import com.github.erozabesu.yplkart.reflection.Methods;
import com.github.erozabesu.yplkart.utils.CheckPointUtil;
import com.github.erozabesu.yplkart.utils.ReflectionUtil;
import com.github.erozabesu.yplkart.utils.Util;

public class ItemDyedTurtle extends BukkitRunnable {
    ArmorStand turtle;
    Entity lastCheckPoint;
    String circuitName;
    Player shooter;
    Player target;
    ItemEnum itemEnum;
    boolean isReverse;

    int hitDamage = 0;
    int movingDamage = 0;

    double motX = 0.0D;
    double motY = 0.0D;
    double motZ = 0.0D;

    boolean isLoadedChunk = true;

    /**
     * コンストラクタ。
     * @param circuitName サーキット名
     * @param turtle タートルエンティティ
     * @param firstCheckPoint アイテム使用時に取得した最寄のチェックポイント
     * @param shooter アイテム使用者
     * @param target 攻撃対象のプレイヤー
     * @param itemEnum こうらアイテムのItemEnum。ItemEnum.RED_TURTLE、もしくはItemEnum.THORNED_TURTLEを指定する
     * @param isReverse コースの逆方向へ向けて移動するかどうか
     */
    public ItemDyedTurtle(String circuitName, ArmorStand turtle, Entity firstCheckPoint, Player shooter, Player target, ItemEnum itemEnum, boolean isReverse) {
        this.turtle = turtle;
        this.lastCheckPoint = firstCheckPoint;
        this.circuitName = circuitName;
        this.shooter = shooter;
        this.target = target;
        this.isReverse = isReverse;

        int adjustDamage = RaceManager.getRacer(shooter).getCharacter().getAdjustAttackDamage();
        if (itemEnum == ItemEnum.RED_TURTLE) {
            this.hitDamage = ItemEnum.RED_TURTLE.getHitDamage() + adjustDamage;
        } else {
            this.hitDamage = ItemEnum.THORNED_TURTLE.getHitDamage() + adjustDamage;
            this.movingDamage = ItemEnum.THORNED_TURTLE.getMovingDamage() + adjustDamage;
        }

        Util.removeEntityCollision(this.turtle);
    }

    private void die() {
        if (!this.turtle.isDead()) {
            //this.turtle.setHealth(0.0D);
        }
        this.cancel();
    }

    @Override
    public void run() {
        Racer targerRacer = RaceManager.getRacer(target);

        // 何らかの原因でエンティティがデスポーンしている場合はタスクを終了
        if (this.turtle.isDead()) {
            die();
            return;
        }

        // ターゲットが走行中でない場合はタスクを終了
        if (!RaceManager.getRacer(this.target).isStillRacing()) {
            die();
            return;
        }

        // ターゲットがオフライン、もしくは寿命が尽きた場合デスポーンしタスクを終了
        if (!Util.isOnline(target.getName())) {
            die();
            return;
        }

        this.move();
        this.rotate();
        if (this.createHitDamage()) {
            die();
            return;
        }
        this.createMovingDamage();

        this.updateCheckPoint();
        this.updateMotion();
    }

    /** 格納されているモーション値を基にエンティティを移動させる。 */
    private void move() {
        Location location = this.turtle.getLocation().clone();

        // 読み込まれているチャンクの場合はベクターを割り当てる
        if (this.isLoadedChunk) {
            this.turtle.setVelocity(new Vector(this.motX, this.motY, this.motZ));

        // 読み込まれていないチャンクの場合はテレポートで移動する。
        } else {
            this.turtle.teleport(location.clone().add(this.motX, this.motY, this.motZ));
        }
    }

    /** Yawを少しずつずらしエンティティを回転させる */
    private void rotate() {
        Location turtleLocation = this.turtle.getLocation().clone();

        Object nmsTurtle = Util.getCraftEntity(this.turtle);
        ReflectionUtil.invoke(Methods.nmsEntity_setYawPitch, nmsTurtle, turtleLocation.getYaw() + 30.0F, 0.0F);
    }

    /**
     * ターゲットプレイヤーとの距離が3ブロック以内の場合ターゲットプレイヤーの座標に爆発を引き起こしtrueを返す。<br>
     * 距離が3ブロックを超えている場合はfalseを返す。
     * @return 爆発に成功したかどうか
     */
    private boolean createHitDamage() {
        if (this.target.getLocation().distance(this.turtle.getLocation()) < 3.0D) {
            Util.createSafeExplosion(this.shooter, target.getLocation(), hitDamage, 3, 0.4F, 2.0F, Particle.EXPLOSION_LARGE);
            return true;
        } else {
            return false;
        }
    }

    /** movingDamageが0でない場合は5チックおきに周囲にダメージを発生させる。 */
    private void createMovingDamage() {
        if (this.movingDamage != 0) {
            if (this.turtle.getTicksLived() % 5 == 0) {
                Util.createSafeExplosion(this.shooter, this.turtle.getLocation(), this.movingDamage, 10, 0.0F, 0.0F, Particle.CRIT_MAGIC, Particle.PORTAL);
            }
        }
    }

    /**
     * 周囲のチェックポイントを取得しlastCheckPointを更新する。<br>
     * 更新できた場合、もしくはlastCheckPointとの距離が5ブロックを超える場合はtrueを返す。<br>
     * 距離が5ブロック以内、かつ更新に失敗した場合はfalseを返す。
     * @return 更新できたかどうか
     */
    private void updateCheckPoint() {
        // 前回通過したチェックポイントとの距離が5ブロック以内の場合、
        // チェックポイントの視点から最寄の視認可能なチェックポイントを新しく検出し変数に格納
        if (this.turtle.getLocation().distance(this.lastCheckPoint.getLocation().clone().add(0.0D, -CheckPointUtil.checkPointHeight, 0.0D)) <= 5) {

            // 逆走フラグがtrueの場合はYawに180.0Fを加算
            Location checkPointLocation = this.lastCheckPoint.getLocation().clone();
            if (this.isReverse) {
                checkPointLocation.setYaw(checkPointLocation.getYaw() + 180.0F);
            }

            Entity newCheckPoint = CheckPointUtil.getInSightNearestCheckpoint(this.circuitName, checkPointLocation, 180.0F);
            if (newCheckPoint != null) {
                this.lastCheckPoint = newCheckPoint;

            // 検出できなかった場合は身動きが取れなくなるためタスクを終了する
            } else {
                this.die();
                return;
            }

        // 5ブロックを超える距離がある場合は何もしない
        } else {
            // Do nothing
        }
    }

    /** lastPassecCheckPointへ向けたモーションを格納する。 */
    private void updateMotion() {
        Location fromLocation = this.turtle.getLocation().clone();
        Location toLocation;

        // ターゲットとの距離が20ブロック以内の場合はtoLocationにターゲットの座標を格納する
        if (this.target.getLocation().distance(fromLocation) <= 20.0D) {
            toLocation = this.target.getLocation();

        // そうでない場合はチェックポイントの座標を格納する
        } else {
            toLocation = this.lastCheckPoint.getLocation().clone().add(0.0D, -CheckPointUtil.checkPointHeight, 0.0D);
        }

        // fromLocationからtoLocationへ向けたベクターを算出
        Vector vectorToLocation = Util.getVectorToLocation(fromLocation, toLocation).multiply(2.0D);

        // 現在のモーション値と全く同じ値の場合、読み込まれていないチャンクのためフラグを立てる
        if (this.motX == vectorToLocation.getX() && this.motY == vectorToLocation.getY() && this.motZ == vectorToLocation.getZ()) {
            this.isLoadedChunk = false;
        } else {
            this.isLoadedChunk = true;
        }

        //算出したベクターのX、Y、Zモーションを格納
        this.motX = vectorToLocation.getX();
        this.motY = vectorToLocation.getY();
        this.motZ = vectorToLocation.getZ();
    }
}
