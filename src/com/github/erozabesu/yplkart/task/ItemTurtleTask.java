package com.github.erozabesu.yplkart.task;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.data.ItemEnum;
import com.github.erozabesu.yplkart.enumdata.Particle;
import com.github.erozabesu.yplkart.utils.Util;

public class ItemTurtleTask extends BukkitRunnable {
    ArmorStand turtle;
    Vector vector;
    double x;
    double y;
    double z;
    Player shooter;
    int life;
    int maxlife;
    int hitdamage;
    final double verticalonhover = 0.00;
    final double verticalonwall = 0.5;

    public ItemTurtleTask(ArmorStand turtle, Player shooter) {
        this.turtle = turtle;
        this.shooter = shooter;
        this.hitdamage = ItemEnum.TURTLE.getHitDamage() + RaceManager.getRacer(shooter).getCharacter().getAdjustAttackDamage();

        Location fromLocation = Util.adjustLocationToBlockCenter(this.turtle.getLocation());
        Location toLocation = Util.getForwardLocationFromYaw(fromLocation, 3);
        this.vector = Util.getVectorToLocation(fromLocation, toLocation);
        this.x = this.vector.getX();
        this.z = this.vector.getZ();
        this.y = this.verticalonhover;
        this.turtle.setVelocity(this.vector);
        Util.removeEntityCollision(turtle);
        Util.removeEntityVerticalMotion(turtle);
    }

    @Override
    public void run() {
        for (Entity entity : this.turtle.getNearbyEntities(1.5, 2, 1.5)) {
            if (!(entity instanceof Player)) {
                continue;
            }

            //発射したプレイヤーに発射した瞬間当たらないようにeがshooterだった場合は発射後2秒経過するまではcontinueする
            if (this.turtle.getTicksLived() < 40) {
                if (entity.getUniqueId() == this.shooter.getUniqueId()) {
                    continue;
                }
            }

            //発射したプレイヤーにも跳ね返って自滅するよう1つ目の引数はnull
            Util.createSafeExplosion(null, this.turtle.getLocation(), this.hitdamage, 3, 0.4F, 2.0F, Particle.EXPLOSION_LARGE);

            this.cancel();
            this.turtle.remove();
            break;
        }

        setNextVector();
        turtle.setVelocity(new Vector(this.x, this.y, this.z).multiply(1.5).setY(this.y));
    }

    private boolean isInWall() {
        try {
            if (Util.isSolidBlock(this.turtle.getLocation()))
                return true;
        } catch (Exception e) {
        }
        return false;
    }

    //真下が空気の場合は優先して下方向へ行く
    //ただしyがx,zを超えてしまうと地面に接触してしまうため注意
    public void setNextVector() {
        boolean onair = false;
        //真下とその更に後ろが非ソリッドブロックなら下へ
        //1ブロックだけ非ソリッドの場合に下に降ろしてしまうと、直前のソリッドブロックの先端に接触してしまう
        if (!Util.isSolidBlock(this.turtle.getLocation().clone().add(0, -1, 0))) {
            if (!Util.isSolidBlock(this.turtle.getLocation().clone().add(-this.x, -1, -this.z))) {
                this.y = -0.8;
                onair = true;
            }
        }

        Location current = Util.adjustLocationToBlockCenter(this.turtle.getLocation());

        if (Util.isSolidBlock(current.clone().add(0, 0, -1)) && Util.isSolidBlock(current.clone().add(1, 0, 0))) {//北東
            if (Util.isSolidBlock(current.clone().add(0, 1, -1)) && Util.isSolidBlock(current.clone().add(1, 1, 0))) {
                Vector normal = Util.getVectorToLocation(current, current.clone().add(1, 0, -1));
                setReverseVector(normal);
                return;
            } else {
                this.y = this.verticalonwall;
                return;
            }
        } else if (Util.isSolidBlock(current.clone().add(0, 0, 1)) && Util.isSolidBlock(current.clone().add(1, 0, 0))) {//南東
            if (Util.isSolidBlock(current.clone().add(0, 1, 1)) && Util.isSolidBlock(current.clone().add(1, 1, 0))) {
                Vector normal = Util.getVectorToLocation(current, current.clone().add(1, 0, 1));
                setReverseVector(normal);
                return;
            } else {
                this.y = this.verticalonwall;
                return;
            }
        } else if (Util.isSolidBlock(current.clone().add(0, 0, 1)) && Util.isSolidBlock(current.clone().add(-1, 0, 0))) {//南西
            if (Util.isSolidBlock(current.clone().add(0, 1, 1)) && Util.isSolidBlock(current.clone().add(-1, 1, 0))) {
                Vector normal = Util.getVectorToLocation(current, current.clone().add(-1, 0, 1));
                setReverseVector(normal);
                return;
            } else {
                this.y = this.verticalonwall;
                return;
            }
        } else if (Util.isSolidBlock(current.clone().add(0, 0, -1)) && Util.isSolidBlock(current.clone().add(-1, 0, 0))) {//北西
            if (Util.isSolidBlock(current.clone().add(0, 1, -1)) && Util.isSolidBlock(current.clone().add(-1, 1, 0))) {
                Vector normal = Util.getVectorToLocation(current, current.clone().add(-1, 0, -1));
                setReverseVector(normal);
                return;
            } else {
                this.y = this.verticalonwall;
                return;
            }
        }

        if (Util.isSolidBlock(current.clone().add(0, 0, -1))) {//北
            if (Util.isSolidBlock(current.clone().add(0, 1, -1)))
                this.z = -this.z;
            else {
                this.y = this.verticalonwall;
                return;
            }
        } else if (Util.isSolidBlock(current.clone().add(0, 0, 1))) {//南
            if (Util.isSolidBlock(current.clone().add(0, 1, 1)))
                this.z = -this.z;
            else {
                this.y = this.verticalonwall;
                return;
            }
        } else if (Util.isSolidBlock(current.clone().add(1, 0, 0))) {//東
            if (Util.isSolidBlock(current.clone().add(1, 1, 0)))
                this.x = -this.x;
            else {
                this.y = this.verticalonwall;
                return;
            }
        } else if (Util.isSolidBlock(current.clone().add(-1, 0, 0))) {//西
            if (Util.isSolidBlock(current.clone().add(-1, 1, 0)))
                this.x = -this.x;
            else {
                this.y = this.verticalonwall;
                return;
            }
        }

        if (!onair)
            this.y = this.verticalonhover;
    }

    public Vector getVector() {
        return new Vector(this.x, this.y, this.z);
    }

    //normalは法線ベクトル、forwardは現在のベクトル
    public Vector getReverseVector(Vector n) {
        Vector f = getVector();
        double t = -(n.getX() * f.getX() + n.getY() * f.getY() + n.getZ() * f.getZ())
                / (n.getX() * n.getX() + n.getY() * n.getY() + n.getZ() * n.getZ());
        return new Vector(f.getX() + t * n.getX() * 2.0, f.getY() + t * n.getY() * 2.0, f.getZ() + t * n.getZ() * 2.0);
    }

    public void setReverseVector(Vector n) {
        Vector r = getReverseVector(n);
        this.x = r.getX();
        this.z = r.getZ();
        this.y = this.verticalonhover;
    }
}