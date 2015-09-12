package com.github.erozabesu.yplkart.object;

import java.util.List;

import org.bukkit.Chunk;
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

        // 生成段階で予め最初のチェックポイントまでのモーションを格納しておく
        Location fromLocation = this.turtle.getLocation().clone();
        Location toLocation = this.lastCheckPoint.getLocation().clone().add(0.0D, -CheckPointUtil.checkPointHeight, 0.0D);
        Vector vectorToLocation = Util.getVectorToLocation(fromLocation, toLocation).multiply(2.0D);

        //算出したベクターのX、Y、Zモーションを格納
        this.motX = vectorToLocation.getX();
        this.motY = vectorToLocation.getY();
        this.motZ = vectorToLocation.getZ();

        Util.removeEntityCollision(this.turtle);
    }

    private void die() {
        if (!this.turtle.isDead()) {
            this.turtle.remove();
        }
        this.cancel();
    }

    @Override
    public void run() {
        Racer targerRacer = RaceManager.getRacer(target);

        // 生成から120秒以上経過した場合は自動消滅
        if (120 < this.turtle.getTicksLived() / 20) {
            this.die();
            return;
        }

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

        // ターゲットがオフライン、もしくはターゲットが別のワールドに移動している場合はタスクを終了
        if (!Util.isOnline(target.getName()) || !this.target.getLocation().getWorld().equals(this.turtle.getLocation().getWorld())) {
            die();
            return;
        }

        // 周囲のチャンクを強制ロード
        for (Chunk chunk : Util.getNearbyChunks(this.turtle.getLocation(), 20.0D)) {
            chunk.load();
        }

        this.move();
        this.rotate();
        if (this.createHitDamage()) {
            die();
            return;
        }
        this.createMovingDamage();

        // ターゲットに対するモーションの格納に成功した場合は何もしない
        if (this.updateTargetMotion()) {
            // Do nothing

        // ターゲットを補足できなかった場合はチェックポイントに対するモーションを格納
        } else {
            // 新たなチェックポイントの検出に成功した場合のみモーションの更新を行う
            if (this.updateCheckPoint()) {
                this.updateCheckPointMotion();
            }
        }
    }

    /** 格納されているモーション値を基にエンティティを移動させる。 */
    private void move() {
        this.turtle.setVelocity(new Vector(this.motX, this.motY, this.motZ));
    }

    /** Yawを少しずつずらしエンティティを回転させる */
    private void rotate() {
        Location turtleLocation = this.turtle.getLocation().clone();

        Object nmsTurtle = Util.getCraftEntity(this.turtle);
        ReflectionUtil.invoke(Methods.nmsEntity_setYawPitch, nmsTurtle, turtleLocation.getYaw() + 30.0F, 0.0F);
    }

    /**
     * shooter以外のプレイヤーとの距離が3ブロック以内の場合プレイヤーの座標に爆発を引き起こしtrueを返す。<br>
     * 距離が3ブロックを超えている場合はfalseを返す。
     * @return 爆発に成功したかどうか
     */
    private boolean createHitDamage() {
        // 周囲のエンティティを取得
        List<Entity> nearbyEntities = this.turtle.getNearbyEntities(3.0D, 3.0D, 3.0D);

        // shooterを除外
        nearbyEntities.remove(this.shooter);

        for (Entity nearbyEntity : nearbyEntities) {
            // エンティティがプレイヤーインスタンス
            if (nearbyEntity instanceof Player) {

                // 同一のサーキットにエントリーしている
                Racer racer = RaceManager.getRacer((Player) nearbyEntity);
                if (racer.getCircuit() != null && racer.getCircuit().getCircuitName().equalsIgnoreCase(this.circuitName)) {

                    // レース中
                    if (racer.isStillRacing()) {
                        Util.createSafeExplosion(this.shooter, nearbyEntity.getLocation(), hitDamage, 3, 0.4F, 2.0F, Particle.EXPLOSION_LARGE);
                        return true;
                    }
                }
            }
        }

        return false;
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
    private boolean updateCheckPoint() {
        // 前回通過したチェックポイントとの距離が5ブロック以内の場合、
        // チェックポイントの視点から最寄の視認可能なチェックポイントを新しく検出し変数に格納
        if (this.turtle.getLocation().distance(this.lastCheckPoint.getLocation().clone().add(0.0D, -CheckPointUtil.checkPointHeight, 0.0D)) <= 5) {

            // 逆走フラグがtrueの場合はYawに180.0Fを加算
            Location checkPointLocation = this.lastCheckPoint.getLocation().clone();
            if (this.isReverse) {
                checkPointLocation.setYaw(checkPointLocation.getYaw() - 180.0F);
            }

            Entity newCheckPoint = CheckPointUtil.getInSightNearestCheckpoint(this.circuitName, checkPointLocation, 180.0F, this.lastCheckPoint);

            // 新たなチェックポイントの検出に成功
            if (newCheckPoint != null) {
                this.lastCheckPoint = newCheckPoint;
                return true;

            // 検出できなかった場合は現在のモーションを保持するためfalseを返す
            } else {
                return false;
            }

        // 5ブロックを超える距離がある場合は現在のモーションを保持するためfalseを返す
        } else {
            return false;
        }
    }

    /** lastPassecCheckPointへ向けたモーションを格納する。 */
    private void updateCheckPointMotion() {
        Location fromLocation = this.turtle.getLocation().clone();
        Location toLocation = this.lastCheckPoint.getLocation().clone().add(0.0D, -CheckPointUtil.checkPointHeight, 0.0D);

        // fromLocationからtoLocationへ向けたベクターを算出
        Vector vectorToLocation = Util.getVectorToLocation(fromLocation, toLocation).multiply(2.0D);

        //算出したベクターのX、Y、Zモーションを格納
        this.motX = vectorToLocation.getX();
        this.motY = vectorToLocation.getY();
        this.motZ = vectorToLocation.getZ();
    }

    /**
     * ターゲットとの距離が20ブロック以内の場合はターゲットに向けたモーションを格納しtrueを返す。<br>
     * 距離が20ブロックを超える場合は何もせずfalseを返す。
     * @return ターゲットを発見しモーションを格納したかどうか
     */
    private boolean updateTargetMotion() {
        Location fromLocation = this.turtle.getLocation().clone();
        Location toLocation = this.target.getLocation().clone();

        // ターゲットとの距離が20ブロックを超える場合はfalseを返す
        if (400.0D < fromLocation.distanceSquared(toLocation)) {
            return false;
        }

        // fromLocationからtoLocationへ向けたベクターを算出
        Vector vectorToLocation = Util.getVectorToLocation(fromLocation, toLocation).multiply(4.0D);

        //算出したベクターのX、Y、Zモーションを格納
        this.motX = vectorToLocation.getX();
        this.motY = vectorToLocation.getY();
        this.motZ = vectorToLocation.getZ();

        return true;
    }
}
