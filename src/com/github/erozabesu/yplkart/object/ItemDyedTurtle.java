package com.github.erozabesu.yplkart.object;

import org.bukkit.Location;
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
    int life = 0;
    int maxLife = 500;

    Entity turtle;
    Entity lastCheckPoint;
    String circuitName;
    Player shooter;
    Player target;
    ItemEnum itemEnum;

    int hitDamage = 0;
    int movingDamage = 0;

    double motX = 0.0D;
    double motY = 0.0D;
    double motZ = 0.0D;


    public ItemDyedTurtle(String circuitName, Entity turtle, Entity firstCheckPoint, Player shooter, Player target, ItemEnum itemEnum) {
        this.turtle = turtle;
        this.lastCheckPoint = firstCheckPoint;
        this.circuitName = circuitName;
        this.shooter = shooter;
        this.target = target;

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
        this.turtle.remove();
        this.cancel();
    }

    @Override
    public void run() {
        // 寿命の加算
        life++;

        Racer targerRacer = RaceManager.getRacer(target);

        // 何らかの原因でエンティティがデスポーンしている場合はタスクを終了
        if (this.turtle.isDead()) {
            die();
            return;
        }

        // ターゲットがゴールしている場合デスポーンしタスクを終了
        if (targerRacer != null) {
            if (targerRacer.isGoal()) {
                die();
                return;
            }
        }

        // ターゲットがオフライン、もしくは寿命が尽きた場合デスポーンしタスクを終了
        if (!Util.isOnline(target.getName()) || this.maxLife < life / 20) {
            die();
            return;
        }

        this.move();
        this.rotate();
        this.createMovingDamage();

        this.updateCheckPoint();
        this.updateMotion();
    }

    /** 格納されているモーション値を基にエンティティを移動させる。 */
    private void move() {
        Location location = this.turtle.getLocation().clone();

        // モーションの値だけ座標を移動する。
        this.turtle.setVelocity(new Vector(this.motX, this.motY, this.motZ));

        // 読み込まれていないチャンクにいる場合はテレポート
        if (!location.getChunk().isLoaded()) {
            this.turtle.teleport(location.clone().add(this.motX, this.motY, this.motZ));
        }
    }

    /** Yawを少しずつずらしエンティティを回転させる */
    private void rotate() {
        Location turtleLocation = this.turtle.getLocation().clone();

        Object nmsTurtle = Util.getCraftEntity(this.turtle);
        ReflectionUtil.invoke(Methods.nmsEntity_setYawPitch, nmsTurtle, turtleLocation.getYaw() + 30.0F, 0.0F);
    }

    /** movingDamageが0でない場合は5チックおきに周囲にダメージを発生させる。 */
    private void createMovingDamage() {
        if (this.movingDamage != 0) {
            if (this.life % 5 == 0) {
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
            this.lastCheckPoint.getLocation().setYaw(this.lastCheckPoint.getLocation().getYaw());
            Entity newCheckPoint = CheckPointUtil.getInSightAndVisibleNearestCheckpoint(this.circuitName, this.lastCheckPoint, 180.0F);
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
        Location turtleLocation = this.turtle.getLocation().clone();
        Location checkPointLocation = this.lastCheckPoint.getLocation().clone().add(0.0D, -CheckPointUtil.checkPointHeight, 0.0D);

        // 現在の座標からチェックポイントへ向けたベクターを算出
        Vector vectorToLocation = Util.getVectorToLocation(turtleLocation, checkPointLocation).multiply(2.0D);

        //算出したベクターのX、Y、Zモーションを格納
        this.motX = vectorToLocation.getX();
        this.motY = vectorToLocation.getY();
        this.motZ = vectorToLocation.getZ();
    }

    /*@Override
    public void run() {
        //周回数の更新
        if (lastStepBlock.equalsIgnoreCase(
                (String) ConfigEnum.START_BLOCK_ID.getValue())) {
            if (Util.getGroundBlockID(this.projectile.getLocation(), 5).equalsIgnoreCase(
                    (String) ConfigEnum.GOAL_BLOCK_ID.getValue())) {
                lap++;
            }
        }
        this.lastStepBlock = Util.getGroundBlockID(this.projectile.getLocation(), 5);

        //targetを発見したら突撃return
        List<LivingEntity> livingentity = Util.getNearbyLivingEntities(this.projectile.getLocation(), 20);
        for (LivingEntity target : livingentity) {
            if (this.target.getUniqueId().toString().equalsIgnoreCase(target.getUniqueId().toString())) {
                Vector v = Util.getVectorToLocation(this.projectile.getLocation(), target.getLocation())
                        .multiply(3);
                this.motX = v.getX();
                this.motY = v.getY();
                this.motZ = v.getZ();

                if (target.getLocation().distance(this.projectile.getLocation()) < 3) {
                    Util.createSafeExplosion(this.shooter, target.getLocation()
                            , ItemEnum.RED_TURTLE.getHitDamage() + this.adjustdamage, 3, 0.4F, 2.0F, Particle.EXPLOSION_LARGE);
                    die();
                }
                return;
            }
        }

        //チェックポイントの更新
        Racer racer = RaceManager.getRacer(this.shooter);
        ArrayList<Entity> checkpointlist = new ArrayList<Entity>();

        //アカこうらを1位から2位に向け発射した場合
        if (this.targetreverse) {
            List<Entity> templist = CheckPointUtil.getNearbyCheckPoints(
                    racer, this.projectile.getLocation().clone().add(-this.motX * 3, 0, -this.motZ * 3), 30);
            if (templist == null)
                return;

            for (Entity e : templist) {
                if (this.shooterpassedcheckpoint.contains(lap + e.getUniqueId().toString())) {
                    if (!targerRacer.getPassedCheckPointList()
                            .contains(lap + e.getUniqueId().toString()))
                        if (!this.turtlepassedcheckpoint.contains(lap + e.getUniqueId().toString()))
                            checkpointlist.add(e);
                }
            }
            //その他
        } else {
            List<Entity> templist = CheckPointUtil.getNearbyCheckPoints(
                    racer, this.projectile.getLocation().clone().add(this.motX * 3, 0, this.motZ * 3), 30);
            if (templist == null)
                return;

            for (Entity e : templist) {
                if (!this.shooterpassedcheckpoint.contains(lap + e.getUniqueId().toString())) {
                    if (!this.turtlepassedcheckpoint.contains(lap + e.getUniqueId().toString()))
                        checkpointlist.add(e);
                }
            }
        }

        /*if(checkpoint.isEmpty()){
        	for (org.bukkit.entity.Entity e : templist) {
        		if(!r.getFirstPassedCheckPoint().equalsIgnoreCase(e.getUniqueId().toString())){
        			checkpoint.add(e);
        		}
        	}
        }*/
        /*if (checkpointlist.isEmpty())
            return;

        Entity checkpoint = Util.getNearestEntity(checkpointlist, this.projectile.getLocation());
        this.turtlepassedcheckpoint.add(lap + checkpoint.getUniqueId().toString());
        Vector v = Util.getVectorToLocation(this.projectile.getLocation(),
                checkpoint.getLocation().clone().add(0, -CheckPointUtil.checkPointHeight, 0)).multiply(3);
        this.motX = v.getX();
        this.motY = v.getY();
        this.motZ = v.getZ();
    }*/
}
