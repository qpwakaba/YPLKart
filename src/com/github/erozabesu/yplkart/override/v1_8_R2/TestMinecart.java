package com.github.erozabesu.yplkart.override.v1_8_R2;

import java.util.Iterator;
import java.util.List;

import net.minecraft.server.v1_8_R2.AxisAlignedBB;
import net.minecraft.server.v1_8_R2.Block;
import net.minecraft.server.v1_8_R2.BlockCobbleWall;
import net.minecraft.server.v1_8_R2.BlockFence;
import net.minecraft.server.v1_8_R2.BlockFenceGate;
import net.minecraft.server.v1_8_R2.BlockMinecartTrackAbstract;
import net.minecraft.server.v1_8_R2.BlockPosition;
import net.minecraft.server.v1_8_R2.BlockPoweredRail;
import net.minecraft.server.v1_8_R2.Blocks;
import net.minecraft.server.v1_8_R2.CrashReport;
import net.minecraft.server.v1_8_R2.CrashReportSystemDetails;
import net.minecraft.server.v1_8_R2.Entity;
import net.minecraft.server.v1_8_R2.EntityHuman;
import net.minecraft.server.v1_8_R2.EntityIronGolem;
import net.minecraft.server.v1_8_R2.EntityLiving;
import net.minecraft.server.v1_8_R2.EntityMinecartAbstract;
import net.minecraft.server.v1_8_R2.EntityMinecartRideable;
import net.minecraft.server.v1_8_R2.IBlockData;
import net.minecraft.server.v1_8_R2.Material;
import net.minecraft.server.v1_8_R2.MathHelper;
import net.minecraft.server.v1_8_R2.MinecraftServer;
import net.minecraft.server.v1_8_R2.ReportedException;
import net.minecraft.server.v1_8_R2.Vec3D;
import net.minecraft.server.v1_8_R2.World;
import net.minecraft.server.v1_8_R2.WorldServer;

import org.bukkit.Location;

public class TestMinecart extends EntityMinecartRideable {

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

    //〓 メイン 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public TestMinecart(World w, Location location) {
        super(w);
        this.yaw = location.getYaw();
        setYawPitch(this.yaw + 90F, location.getPitch());
        setPosition(location.getX(), location.getY() + 1, location.getZ());

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

    //〓 Override 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * setPassenger
     * マインカートが搭乗可能な状態だった場合搭乗させる
     * プレイヤーに右クリックされた場合等に処理される
     * @return 既に搭乗者がいた場合、搭乗者がプレイヤーならtrue、プレイヤー以外のEntityならfalse
     */
    @Override
    public boolean e(EntityHuman entityhuman)
    {
        if (this.passenger != null && this.passenger instanceof EntityHuman && this.passenger != entityhuman) {
            return true;
        } else if (this.passenger != null && this.passenger != entityhuman) {
            return false;
        } else {
            if (!this.world.isClientSide) {
                entityhuman.mount(this);
            }

            return true;
        }
    }

    /**
     * livingUpdate
     * Entityの状態を毎チック更新する
     */
    @Override
    public void t_()
    {
        if (this.getType() > 0) {
            this.j(this.getType() - 1);
            this.SetDisplayBlockOffset((int) this.getDamage());
        }

        if (this.getDamage() > 0.0F) {
            this.setDamage(this.getDamage() - 1.0F);
        }

        //System.out.println(this.isInvisible());
        //System.out.println("yaw:" + this.yaw + "    pitch:" + this.pitch);
        //System.out.println("typ:" + getType() + "   dam:" + getDamage() + "   dir:" + r() + "   off:" + getDisplayBlockOffset());

        if (this.locY < -64.0D) {
            this.O();
        }

        int i;

        if (!this.world.isClientSide && this.world instanceof WorldServer) {
            this.world.methodProfiler.a("portal");
            MinecraftServer minecraftserver = ((WorldServer) this.world).getMinecraftServer();

            i = this.L();
            if (this.ak) {
                if (minecraftserver.getAllowNether()) {
                    if (this.vehicle == null && this.al++ >= i) {
                        this.al = i;
                        this.portalCooldown = this.aq();
                        byte b0;

                        if (this.world.worldProvider.getDimension() == -1) {
                            b0 = 0;
                        } else {
                            b0 = -1;
                        }

                        this.c(b0);
                    }

                    this.ak = false;
                }
            } else {
                if (this.al > 0) {
                    this.al -= 4;
                }

                if (this.al < 0) {
                    this.al = 0;
                }
            }

            if (this.portalCooldown > 0) {
                --this.portalCooldown;
            }

            this.world.methodProfiler.b();
        }

        if (this.world.isClientSide) {
            if (this.d > 0) {
                double d0 = this.locX + (this.e - this.locX) / (double) this.d;
                double d1 = this.locY + (this.f - this.locY) / (double) this.d;
                double d2 = this.locZ + (this.g - this.locZ) / (double) this.d;
                double d3 = MathHelper.g(this.h - (double) this.yaw);

                this.yaw = (float) ((double) this.yaw + d3 / (double) this.d);
                this.pitch = (float) ((double) this.pitch + (this.i - (double) this.pitch) / (double) this.d);
                --this.d;
                this.setPosition(d0, d1, d2);
                this.setYawPitch(this.yaw, this.pitch);
            } else {
                this.setPosition(this.locX, this.locY, this.locZ);
                this.setYawPitch(this.yaw, this.pitch);
            }

        } else {
            this.lastX = this.locX;
            this.lastY = this.locY;
            this.lastZ = this.locZ;
            this.motY -= 0.03999999910593033D;
            int j = MathHelper.floor(this.locX);

            i = MathHelper.floor(this.locY);
            int k = MathHelper.floor(this.locZ);

            if (BlockMinecartTrackAbstract.e(this.world, new BlockPosition(j, i - 1, k))) {
                --i;
            }

            BlockPosition blockposition = new BlockPosition(j, i, k);
            IBlockData iblockdata = this.world.getType(blockposition);

            if (BlockMinecartTrackAbstract.d(iblockdata)) {
                this.a(blockposition, iblockdata);
                if (iblockdata.getBlock() == Blocks.ACTIVATOR_RAIL) {
                    this.a(j, i, k, ((Boolean) iblockdata.get(BlockPoweredRail.POWERED)).booleanValue());
                }
            } else {
                this.n();
            }

            this.checkBlockCollisions();
            this.pitch = 0.0F;
            double d4 = this.lastX - this.locX;
            double d5 = this.lastZ - this.locZ;

            if (d4 * d4 + d5 * d5 > 0.001D) {
                this.yaw = (float) (MathHelper.b(d5, d4) * 180.0D / 3.141592653589793D);
                if (this.a) {
                    this.yaw += 180.0F;
                }
            }

            double d6 = (double) MathHelper.g(this.yaw - this.lastYaw);

            if (d6 < -170.0D || d6 >= 170.0D) {
                this.yaw += 180.0F;
                this.a = !this.a;
            }

            this.setYawPitch(this.yaw, this.pitch);
            Iterator iterator = this.world.getEntities(this, this.getBoundingBox().grow(0.20000000298023224D, 0.0D, 0.20000000298023224D)).iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();

                if (entity != this.passenger && entity.ae() && entity instanceof EntityMinecartAbstract) {
                    entity.collide(this);
                }
            }

            if (this.passenger != null && this.passenger.dead) {
                if (this.passenger.vehicle == this) {
                    this.passenger.vehicle = null;
                }

                this.passenger = null;
            }

            this.W();
        }
    }

    /**
     * applyFriction
     * 現在のモーション値に摩擦係数を適用し徐々に減衰させる
     */
    @Override
    protected void n() {
        double d0 = this.m();

        this.motX = MathHelper.a(this.motX, -d0, d0);
        this.motZ = MathHelper.a(this.motZ, -d0, d0);
        if (this.onGround) {
            this.motX *= 0.5D;
            this.motY *= 0.5D;
            this.motZ *= 0.5D;
        }

        this.move(this.motX, this.motY, this.motZ);
        if (!this.onGround) {
            this.motX *= 0.949999988079071D;
            this.motY *= 0.949999988079071D;
            this.motZ *= 0.949999988079071D;
        }
    }

    //エンティティ同士の衝突
    //衝突相手を吹き飛ばす代償として自身のスピードは相殺される
    //スピード50の相手にスピード200で衝突した場合
    //相手はスピード200-50=150の速度で吹き飛び、自身のスピードは200-150=50に減衰する
    //ここで言うスピードとは移動速度ではなく、移動速度に自身の重さを掛け合わせたもの
    //スピードの定義 : calcMotionSpeed() * Karts.getWeight()
    @Override
    public void collide(Entity entity) {
        if (!this.world.isClientSide) {
            if (!entity.noclip && !this.noclip) {
                if (entity != this.passenger) {
                    if (entity instanceof EntityLiving && !(entity instanceof EntityHuman) && !(entity instanceof EntityIronGolem) && this.s() == EntityMinecartAbstract.EnumMinecartType.RIDEABLE && this.motX * this.motX + this.motZ * this.motZ > 0.01D && this.passenger == null && entity.vehicle == null) {
                        entity.mount(this);
                    }

                    double d0 = entity.locX - this.locX;
                    double d1 = entity.locZ - this.locZ;
                    double d2 = d0 * d0 + d1 * d1;

                    if (d2 >= 9.999999747378752E-5D) {
                        d2 = (double) MathHelper.sqrt(d2);
                        d0 /= d2;
                        d1 /= d2;
                        double d3 = 1.0D / d2;

                        if (d3 > 1.0D) {
                            d3 = 1.0D;
                        }

                        d0 *= d3;
                        d1 *= d3;
                        d0 *= 0.10000000149011612D;
                        d1 *= 0.10000000149011612D;
                        d0 *= (double) (1.0F - this.U);
                        d1 *= (double) (1.0F - this.U);
                        d0 *= 0.5D;
                        d1 *= 0.5D;
                        if (entity instanceof EntityMinecartAbstract) {
                            double d4 = entity.locX - this.locX;
                            double d5 = entity.locZ - this.locZ;
                            Vec3D vec3d = (new Vec3D(d4, 0.0D, d5)).a();
                            Vec3D vec3d1 = (new Vec3D((double) MathHelper.cos(this.yaw * 3.1415927F / 180.0F), 0.0D, (double) MathHelper.sin(this.yaw * 3.1415927F / 180.0F))).a();
                            double d6 = Math.abs(vec3d.b(vec3d1));

                            if (d6 < 0.800000011920929D) {
                                return;
                            }

                            double d7 = entity.motX + this.motX;
                            double d8 = entity.motZ + this.motZ;

                            if (((EntityMinecartAbstract) entity).s() == EntityMinecartAbstract.EnumMinecartType.FURNACE && this.s() != EntityMinecartAbstract.EnumMinecartType.FURNACE) {
                                this.motX *= 0.20000000298023224D;
                                this.motZ *= 0.20000000298023224D;
                                this.g(entity.motX - d0, 0.0D, entity.motZ - d1);
                                entity.motX *= 0.949999988079071D;
                                entity.motZ *= 0.949999988079071D;
                            } else if (((EntityMinecartAbstract) entity).s() != EntityMinecartAbstract.EnumMinecartType.FURNACE && this.s() == EntityMinecartAbstract.EnumMinecartType.FURNACE) {
                                entity.motX *= 0.20000000298023224D;
                                entity.motZ *= 0.20000000298023224D;
                                entity.g(this.motX + d0, 0.0D, this.motZ + d1);
                                this.motX *= 0.949999988079071D;
                                this.motZ *= 0.949999988079071D;
                            } else {
                                d7 /= 2.0D;
                                d8 /= 2.0D;
                                this.motX *= 0.20000000298023224D;
                                this.motZ *= 0.20000000298023224D;
                                this.g(d7 - d0, 0.0D, d8 - d1);
                                entity.motX *= 0.20000000298023224D;
                                entity.motZ *= 0.20000000298023224D;
                                entity.g(d7 + d0, 0.0D, d8 + d1);
                            }
                        } else {
                            this.g(-d0, 0.0D, -d1);
                            entity.g(d0 / 4.0D, 0.0D, d1 / 4.0D);
                        }
                    }

                }
            }
        }
    }

    public void setBoundingBox(AxisAlignedBB boundingBox) {
        this.a(boundingBox);
    }

    @Override
    public void move(double motionX, double motionY, double motionZ) {
        if (this.noclip) {
            setBoundingBox(this.getBoundingBox().c(motionX, motionY, motionZ));
            this.recalcPosition();
        } else {
            this.locY = this.locY + 2;

            this.world.methodProfiler.a("move");
            double locationX = this.locX;
            double locationY = this.locY;
            double locationZ = this.locZ;

            //水没？
            if (this.H) {
                this.H = false;
                motionX *= 0.25D;
                motionY *= 0.05000000074505806D;
                motionZ *= 0.25D;
                this.motX = 0.0D;
                this.motY = 0.0D;
                this.motZ = 0.0D;
            }

            double motionX2 = motionX;
            double motionY2 = motionY;
            double motionZ2 = motionZ;

            boolean flag = false;

            List list = this.world.getCubes(this, this.getBoundingBox().a(motionX, motionY, motionZ));
            AxisAlignedBB axisalignedbb = this.getBoundingBox();

            AxisAlignedBB axisalignedbb1;
            for (Iterator iterator = list.iterator(); iterator.hasNext(); motionY = axisalignedbb1.b(this.getBoundingBox(), motionY)) {
                axisalignedbb1 = (AxisAlignedBB) iterator.next();
            }

            this.a(this.getBoundingBox().c(0.0D, motionY, 0.0D));
            boolean flag1 = this.onGround || motionY2 != motionY && motionY2 < 0.0D;

            AxisAlignedBB axisalignedbb2;
            Iterator iterator1;

            for (iterator1 = list.iterator(); iterator1.hasNext(); motionX = axisalignedbb2.a(this.getBoundingBox(), motionX)) {
                axisalignedbb2 = (AxisAlignedBB) iterator1.next();
            }

            this.a(this.getBoundingBox().c(motionX, 0.0D, 0.0D));

            for (iterator1 = list.iterator(); iterator1.hasNext(); motionZ = axisalignedbb2.c(this.getBoundingBox(), motionZ)) {
                axisalignedbb2 = (AxisAlignedBB) iterator1.next();
            }

            this.a(this.getBoundingBox().c(0.0D, 0.0D, motionZ));
            if (this.S > 0.0F && flag1 && (motionX2 != motionX || motionZ2 != motionZ)) {
                double d10 = motionX;
                double d11 = motionY;
                double d12 = motionZ;
                AxisAlignedBB axisalignedbb3 = this.getBoundingBox();

                this.a(axisalignedbb);
                motionY = (double) this.S;
                List list1 = this.world.getCubes(this, this.getBoundingBox().a(motionX2, motionY, motionZ2));
                AxisAlignedBB axisalignedbb4 = this.getBoundingBox();
                AxisAlignedBB axisalignedbb5 = axisalignedbb4.a(motionX2, 0.0D, motionZ2);
                double d13 = motionY;

                AxisAlignedBB axisalignedbb6;

                for (Iterator iterator2 = list1.iterator(); iterator2.hasNext(); d13 = axisalignedbb6.b(axisalignedbb5, d13)) {
                    axisalignedbb6 = (AxisAlignedBB) iterator2.next();
                }

                axisalignedbb4 = axisalignedbb4.c(0.0D, d13, 0.0D);
                double d14 = motionX2;

                AxisAlignedBB axisalignedbb7;

                for (Iterator iterator3 = list1.iterator(); iterator3.hasNext(); d14 = axisalignedbb7.a(axisalignedbb4, d14)) {
                    axisalignedbb7 = (AxisAlignedBB) iterator3.next();
                }

                axisalignedbb4 = axisalignedbb4.c(d14, 0.0D, 0.0D);
                double d15 = motionZ2;

                AxisAlignedBB axisalignedbb8;

                for (Iterator iterator4 = list1.iterator(); iterator4.hasNext(); d15 = axisalignedbb8.c(axisalignedbb4, d15)) {
                    axisalignedbb8 = (AxisAlignedBB) iterator4.next();
                }

                axisalignedbb4 = axisalignedbb4.c(0.0D, 0.0D, d15);
                AxisAlignedBB axisalignedbb9 = this.getBoundingBox();
                double d16 = motionY;

                AxisAlignedBB axisalignedbb10;

                for (Iterator iterator5 = list1.iterator(); iterator5.hasNext(); d16 = axisalignedbb10.b(axisalignedbb9, d16)) {
                    axisalignedbb10 = (AxisAlignedBB) iterator5.next();
                }

                axisalignedbb9 = axisalignedbb9.c(0.0D, d16, 0.0D);
                double d17 = motionX2;

                AxisAlignedBB axisalignedbb11;

                for (Iterator iterator6 = list1.iterator(); iterator6.hasNext(); d17 = axisalignedbb11.a(axisalignedbb9, d17)) {
                    axisalignedbb11 = (AxisAlignedBB) iterator6.next();
                }

                axisalignedbb9 = axisalignedbb9.c(d17, 0.0D, 0.0D);
                double d18 = motionZ2;

                AxisAlignedBB axisalignedbb12;

                for (Iterator iterator7 = list1.iterator(); iterator7.hasNext(); d18 = axisalignedbb12.c(axisalignedbb9, d18)) {
                    axisalignedbb12 = (AxisAlignedBB) iterator7.next();
                }

                axisalignedbb9 = axisalignedbb9.c(0.0D, 0.0D, d18);
                double d19 = d14 * d14 + d15 * d15;
                double d20 = d17 * d17 + d18 * d18;

                if (d19 > d20) {
                    motionX = d14;
                    motionZ = d15;
                    motionY = -d13;
                    this.a(axisalignedbb4);
                } else {
                    motionX = d17;
                    motionZ = d18;
                    motionY = -d16;
                    this.a(axisalignedbb9);
                }

                AxisAlignedBB axisalignedbb13;

                for (Iterator iterator8 = list1.iterator(); iterator8.hasNext(); motionY = axisalignedbb13.b(this.getBoundingBox(), motionY)) {
                    axisalignedbb13 = (AxisAlignedBB) iterator8.next();
                }

                this.a(this.getBoundingBox().c(0.0D, motionY, 0.0D));
                if (d10 * d10 + d12 * d12 >= motionX * motionX + motionZ * motionZ) {
                    motionX = d10;
                    motionY = d11;
                    motionZ = d12;
                    this.a(axisalignedbb3);
                }
            }

            this.world.methodProfiler.b();
            this.world.methodProfiler.a("rest");
            this.recalcPosition();
            this.positionChanged = motionX2 != motionX || motionZ2 != motionZ;
            this.E = motionY2 != motionY;
            this.onGround = this.E && motionY2 < 0.0D;
            this.F = this.positionChanged || this.E;
            int i = MathHelper.floor(this.locX);
            int j = MathHelper.floor(this.locY - 0.20000000298023224D);
            int k = MathHelper.floor(this.locZ);
            BlockPosition blockposition = new BlockPosition(i, j, k);
            Block block = this.world.getType(blockposition).getBlock();

            if (block.getMaterial() == Material.AIR) {
                Block block1 = this.world.getType(blockposition.down()).getBlock();

                if (block1 instanceof BlockFence || block1 instanceof BlockCobbleWall || block1 instanceof BlockFenceGate) {
                    block = block1;
                    blockposition = blockposition.down();
                }
            }

            this.a(motionY, this.onGround, block, blockposition);
            if (motionX2 != motionX) {
                this.motX = 0.0D;
            }

            if (motionZ2 != motionZ) {
                this.motZ = 0.0D;
            }

            if (motionY2 != motionY) {
                block.a(this.world, this);
            }

            if (this.s_() && !flag && this.vehicle == null) {
                double d21 = this.locX - locationX;
                double d22 = this.locY - locationY;
                double d23 = this.locZ - locationZ;

                if (block != Blocks.LADDER) {
                    d22 = 0.0D;
                }

                if (block != null && this.onGround) {
                    block.a(this.world, blockposition, this);
                }

                this.M = (float) ((double) this.M + (double) MathHelper.sqrt(d21 * d21 + d23 * d23) * 0.6D);
                this.N = (float) ((double) this.N + (double) MathHelper.sqrt(d21 * d21 + d22 * d22 + d23 * d23) * 0.6D);
                if (this.N > (float) this.h && block.getMaterial() != Material.AIR) {
                    this.h = (int) this.N + 1;
                    if (this.V()) {
                        float f = MathHelper.sqrt(this.motX * this.motX * 0.20000000298023224D + this.motY * this.motY + this.motZ * this.motZ * 0.20000000298023224D) * 0.35F;

                        if (f > 1.0F) {
                            f = 1.0F;
                        }

                        this.makeSound(this.P(), f, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
                    }

                    this.a(blockposition, block);
                }
            }

            try {
                this.checkBlockCollisions();
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.a(throwable, "Checking entity block collision");
                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity being checked for collision");

                this.appendEntityCrashDetails(crashreportsystemdetails);
                throw new ReportedException(crashreport);
            }

            boolean flag2 = this.U();

            if (this.world.e(this.getBoundingBox().shrink(0.001D, 0.001D, 0.001D))) {
                this.burn(1);
                if (!flag2) {
                    ++this.fireTicks;
                    if (this.fireTicks == 0) {
                        this.setOnFire(8);
                    }
                }
            } else if (this.fireTicks <= 0) {
                this.fireTicks = -this.maxFireTicks;
            }

            if (flag2 && this.fireTicks > 0) {
                this.makeSound("random.fizz", 0.7F, 1.6F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
                this.fireTicks = -this.maxFireTicks;
            }

            this.world.methodProfiler.b();
        }
    }

    private void recalcPosition() {
        this.locX = (this.getBoundingBox().a + this.getBoundingBox().d) / 2.0D;
        this.locY = this.getBoundingBox().b;
        this.locZ = (this.getBoundingBox().c + this.getBoundingBox().f) / 2.0D;
    }
}