package com.github.erozabesu.yplkart.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.github.erozabesu.yplkart.Permission;
import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.YPLKart;
import com.github.erozabesu.yplkart.data.ConfigEnum;
import com.github.erozabesu.yplkart.data.DisplayKartConfig;
import com.github.erozabesu.yplkart.data.ItemEnum;
import com.github.erozabesu.yplkart.enumdata.Particle;
import com.github.erozabesu.yplkart.object.Kart;
import com.github.erozabesu.yplkart.object.KartType;
import com.github.erozabesu.yplkart.object.Racer;
import com.github.erozabesu.yplkart.reflection.Classes;
import com.github.erozabesu.yplkart.reflection.Constructors;
import com.github.erozabesu.yplkart.reflection.Fields;
import com.github.erozabesu.yplkart.reflection.Methods;

public class KartUtil extends ReflectionUtil {

    //〓 Entity Management 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    @SuppressWarnings("unchecked")
    public static void livingUpdate(Object nmsEntityKart) {

        Object passenger = getFieldValue(Fields.nmsEntity_passenger, nmsEntityKart);
        Object nmsWorld = invoke(Methods.nmsEntity_getWorld, nmsEntityKart);

        //搭乗者が死亡していた場合passenger変数を初期化する
        //EntityMinecartのようなRidableなクラスで利用される
        if (passenger != null) {
            if ((Boolean) getFieldValue(Fields.nmsEntity_dead, passenger)) {
                if (getFieldValue(Fields.nmsEntity_vehicle, passenger) == nmsEntityKart) {
                    setFieldValue(Fields.nmsEntity_vehicle, passenger, null);
                }

                setFieldValue(Fields.nmsEntity_passenger, nmsEntityKart, null);
            }
        }

        //ディスプレイカートの場合何もしない
        if (invoke(Methods.Ypl_getKartType, nmsEntityKart).equals(KartType.DisplayKart)) {
            // Do nothing

        //ディスプレイカート以外
        } else {

            //BoundingBox内のEntityを検索し、当たり判定を発生させる
            Object boundingBox = invoke(Methods.nmsEntity_getBoundingBox, nmsEntityKart);
            boundingBox = invoke(Methods.nmsAxisAlignedBB_grow, boundingBox, 0.2000000029802322D, 0.0D, 0.2000000029802322D);
            List<Object> collideEntities = (List<Object>) invoke(Methods.nmsWorld_getEntities, nmsWorld, nmsEntityKart, boundingBox);
            Iterator iterator = collideEntities.iterator();
            while (iterator.hasNext()) {
                Object collideEntity = iterator.next();
                if (collideEntity != passenger) {
                    invoke(Methods.nmsEntity_collide, nmsEntityKart, collideEntity);
                }
            }

            //プレイヤーが搭乗時、当プラグイン独自のモーション値を適用する
            KartUtil.moveByKartMotion(nmsEntityKart);

            //モーション値に摩擦係数を割り当て減衰させる
            KartUtil.setFrictionMotion(nmsEntityKart);

            //よく分からない
            //EntityMinecartのようなRidableなクラスではこの位置で実行されている
            invoke(Methods.nmsEntity_checkBlockCollisions, nmsEntityKart);
        }
    }

    /**
     * エンティティが生存しているかどうかをチェックするタスク<br>
     * die()メソッドをOverrideしてもチャンクのアンロードによるデスポーンを検知できないため、タスクを起動して確認する
     */
    public static void runLivingCheckTask(final Object nmsEntityKart) {
        BukkitTask livingCheckTask =
            Bukkit.getScheduler().runTaskTimer(YPLKart.getInstance(), new Runnable() {
                public void run() {
                    int entityId = (Integer) ReflectionUtil.invoke(Methods.nmsEntity_getId, nmsEntityKart);
                    Object nmsWorld = ReflectionUtil.invoke(Methods.nmsEntity_getWorld, nmsEntityKart);

                    if (isDead(entityId, nmsWorld)) {
                        RaceManager.removeKartEntityIdMap(entityId);
                        ((BukkitTask) invoke(Methods.Ypl_getLivingCheckTask, nmsEntityKart)).cancel();
                    }
                }
            }, 0, 1);

        invoke(Methods.Ypl_setLivingCheckTask, nmsEntityKart, livingCheckTask);
    }

    //〓 Event 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * アーマースタンドを右クリックした場合<br>
     * 通常アーマースタンドにアイテムを装備させる、もしくはアイテムを剥ぎ取る操作を行うがキャンセルし、<br>
     * 搭乗可能な状態なら搭乗させる
     */
    public static boolean onRightClicked(Object nmsEntityKart, Object nmsEntityHuman) {

        Object passenger = getFieldValue(Fields.nmsEntity_passenger, nmsEntityKart);

        //既に搭乗者が居た場合
        //搭乗者がクリックしたプレイヤー以外
        if (passenger != null && passenger != nmsEntityHuman) {

            //搭乗者がプレイヤーエンティティ
            if (!instanceOf(passenger, Classes.nmsEntityPlayer)) {
                //何もせずリターン
                return false;

            //搭乗者がプレイヤーエンティティ以外のエンティティ
            } else {
                //カートから降ろす
                invoke(Methods.nmsEntity_mount, passenger, new Object[]{null});
            }
        }

        //レースカート以外のカートであれば搭乗させる
        Object nmsWorld = invoke(Methods.nmsEntity_getWorld, nmsEntityKart);
        if (!invoke(Methods.Ypl_getKartType, nmsEntityKart).equals(KartType.RacingKart)) {
            if (!(Boolean) ReflectionUtil.getFieldValue(Fields.nmsWorld_isClientSide, nmsWorld)) {
                invoke(Methods.nmsEntity_mount, nmsEntityHuman, nmsEntityKart);
            }
        }

        return false;
    }

    /**
     * アーマースタンドを左クリックした場合<br>
     * 通常ダメージを受けた場合の処理を行うが、プレイヤーの左クリック以外ではダメージを受けないよう変更している<br>
     * カートの破壊パーミッションを所有している場合のみカートエンティティを削除する
     */
    public static boolean onLeftClicked(Object nmsEntityKart, Object damageSource) {
        Object nmsWorld = invoke(Methods.nmsEntity_getWorld, nmsEntityKart);
        boolean isDead = (Boolean) getFieldValue(Fields.nmsEntity_dead, nmsEntityKart);

        if (!(Boolean) ReflectionUtil.getFieldValue(Fields.nmsWorld_isClientSide, nmsWorld) && !isDead) {
            Object nmsDamagerEntity = invoke(Methods.nmsDamageSource_getEntity, damageSource);

            //窒息等のダメージ要因がエンティティ以外の場合は除外
            if (nmsDamagerEntity == null) {
                return false;
            }

            if (!instanceOf(nmsDamagerEntity, Classes.nmsEntityPlayer)) {
                return false;
            }

            Player player = (Player) invoke(Methods.nmsEntity_getBukkitEntity, nmsDamagerEntity);
            if (!Permission.hasPermission(player, Permission.OP_KART_REMOVE, false)) {
                return false;
            }

            Object passenger = getFieldValue(Fields.nmsEntity_passenger, nmsEntityKart);
            if (passenger != null) {
                invoke(Methods.nmsEntity_mount, passenger, new Object[]{null});
            }

            if (invoke(Methods.Ypl_getKartType, nmsEntityKart).equals(KartType.DisplayKart)) {
                String customName = (String) invoke(Methods.nmsEntity_getCustomName, nmsEntityKart);
                DisplayKartConfig.deleteDisplayKart(player, customName);
            }

            invoke(Methods.nmsEntity_die, nmsEntityKart);
        }
        return true;
    }

    //〓 Move 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * プレイヤーが登場時のカートのモーションを当プラグイン独自の値に変換し適用する
     * @param nmsEntityKart Nmsカートエンティティ
     */
    public static void moveByKartMotion(Object nmsEntityKart) {
        Object passenger = getFieldValue(Fields.nmsEntity_passenger, nmsEntityKart);

        //EntityHumanが搭乗していない場合は何もしない
        if (passenger == null) {
            return;
        }
        if (!instanceOf(passenger, Classes.nmsEntityPlayer)) {
            return;
        }

        Entity entityKart = (Entity) invoke(Methods.nmsEntity_getBukkitEntity, nmsEntityKart);
        Player player = (Player) invoke(Methods.nmsEntity_getBukkitEntity, passenger);
        Kart kart = (Kart) invoke(Methods.Ypl_getKart, nmsEntityKart);
        Racer racer = RaceManager.getRacer(player);

        Location location = entityKart.getLocation();
        double speedStack = (Double) invoke(Methods.Ypl_getSpeedStack, nmsEntityKart);
        double motX = (Double) getFieldValue(Fields.nmsEntity_motX, nmsEntityKart);
        double motY = (Double) getFieldValue(Fields.nmsEntity_motY, nmsEntityKart);
        double motZ = (Double) getFieldValue(Fields.nmsEntity_motZ, nmsEntityKart);

        //モーション値を変換する前に現在のモーション値を残しておく
        //collide()で使用する
        invoke(Methods.Ypl_setLastMotionSpeed, nmsEntityKart, calcMotionSpeed(motX, motZ) * kart.getWeight());

        //キラー使用中
        boolean isKillerInitialized = (Boolean) invoke(Methods.Ypl_isKillerInitialized, nmsEntityKart);
        if (racer.getUsingKiller() != null) {

            //キラー利用後の初回チックの場合
            if (!isKillerInitialized) {

                //フラグをオンにする
                invoke(Methods.Ypl_setKillerInitialized, nmsEntityKart, true);

                //外見をキラーに変更する
                setDisplayMaterial(nmsEntityKart
                        , ItemEnum.KILLER.getDisplayBlockMaterial(), ItemEnum.KILLER.getDisplayBlockMaterialData());

                //よじ登れる高さを一時的に高くする
                setFieldValue(Fields.nmsEntity_climbableHeight, nmsEntityKart, kart.getClimbableHeight() + 5);
            }

            //スピードスタックを一時的に常に最大値に固定する
            invoke(Methods.Ypl_setSpeedStack, nmsEntityKart, kart.getMaxSpeed());

            //キラー専用モーションの適用
            setKillerMotion(nmsEntityKart, racer);

            //キラー用エフェクトの再生
            playKillerEffect(player, location);

            //周囲のプレイヤーへダメージ
            Util.createSafeExplosion(player, location, ItemEnum.KILLER.getMovingDamage()
                    + RaceManager.getRacer(player).getCharacter().getAdjustAttackDamage(), 6, 0.0F, true);
        } else {
            if (invoke(Methods.Ypl_getKartType, nmsEntityKart).equals(KartType.RacingKart)) {

                //キラーの効果が切れた場合
                if (isKillerInitialized) {

                    //フラグを元に戻す
                    invoke(Methods.Ypl_setKillerInitialized, nmsEntityKart, false);

                    //外見を本来のカートに戻す
                    setDisplayMaterial(nmsEntityKart, kart.getDisplayMaterial(), kart.getDisplayMaterialData());

                    //よじ登れる高さを元に戻す
                    setFieldValue(Fields.nmsEntity_climbableHeight, nmsEntityKart, kart.getClimbableHeight());
                }
            }

            //モーションの適用
            setNormalMotion(nmsEntityKart, passenger);

            //アイドリングエフェクトの再生
            playIdleEffect(player, location, speedStack);

            //ドリフトエフェクトの再生
            playDriftEffect(player, location, speedStack);

            //スピードメーター
            int motionSpeed = (int) (calcMotionSpeed(motX, motZ) * 564.0D);
            PacketUtil.sendActionBar(player, ChatColor.GOLD.toString() + "SPEED:" + ChatColor.WHITE.toString() + motionSpeed);
            //player.setLevel((int) (calcMotionSpeed(motX, motZ) * 564.0D));
        }

        //はしご、つたのようなよじ登れるブロックに立っている場合
        if (Util.isClimbableBlock(Util.getForwardLocationFromYaw(location, 0.5D))) {

            float f4 = 0.15F;
            motX = (Double) invoke(Methods.static_nmsMathHelper_a2, null, motX, -f4, f4) * 0.2;
            motZ = (Double) invoke(Methods.static_nmsMathHelper_a2, null, motZ, -f4, f4) * 0.2;

            //setFieldValue(Fields.nmsEntity_motX, nmsEntityKart, motX);
            //setFieldValue(Fields.nmsEntity_motZ, nmsEntityKart, motZ);
            setFieldValue(Fields.nmsEntity_fallDistance, nmsEntityKart, 0.0F);

            if (motY < -0.15D) {
                setFieldValue(Fields.nmsEntity_motY, nmsEntityKart, motY = -0.15D);
            }
        }

        invoke(Methods.nmsEntity_move, nmsEntityKart, motX, motY, motZ);

        if ((Boolean) getFieldValue(Fields.nmsEntity_positionChanged, nmsEntityKart)
                && Util.isClimbableBlock(Util.getForwardLocationFromYaw(location, 0.5D))) {
            setFieldValue(Fields.nmsEntity_motY, nmsEntityKart, motY = 0.2D + speedStack / 300);
        }

        boolean isLoadedChunk = location.getChunk().isLoaded();
        Object nmsWorld = invoke(Methods.nmsEntity_getWorld, nmsEntityKart);
        if ((Boolean) ReflectionUtil.getFieldValue(Fields.nmsWorld_isClientSide, nmsWorld) && !isLoadedChunk) {
            if ((Double) getFieldValue(Fields.nmsEntity_locY, nmsEntityKart) > 0.0D) {
                setFieldValue(Fields.nmsEntity_locY, nmsEntityKart, -0.1D);
            } else {
                setFieldValue(Fields.nmsEntity_locY, nmsEntityKart, 0.0D);
            }
        } else {
            setFieldValue(Fields.nmsEntity_motY, nmsEntityKart, motY -= 0.08D);
        }

        setFieldValue(Fields.nmsEntity_motY, nmsEntityKart, motY *= 0.9800000190734863D);
        //setMotionX(getMotionX() * groundFriction);
        //setMotionZ(getMotionZ() * groundFriction);

        /*
         * カートエンティティの描画位置をkart.ymlのmount_position_offsetの位置に強制描画させるため、
         * 現在座標のテレポートパケットを送信する
         * 描画位置の細かい計算は、パケットの送信をPlayerChannelHandlerがフックし行うためここでは送信するのみ
         */
        //PacketUtil.sendEntityTeleportPacket(null, entityKart, location);
    }

    /**
     * 引数nmsEntityKartが引数nmsEntityOtherに衝突した際の接触モーション、及びダメージを適用する
     * @param nmsEntityKart 衝突したNmsEntity
     * @param nmsEntityOther 衝突されたNmsEntity
     */
    public static void moveByCollision(Object nmsEntityKart, Object nmsEntityOther) {
        Entity entityKart = (Entity) invoke(Methods.nmsEntity_getBukkitEntity, nmsEntityKart);
        Entity entityOther = (Entity) invoke(Methods.nmsEntity_getBukkitEntity, nmsEntityOther);
        Object nmsWorld = invoke(Methods.nmsEntity_getWorld, nmsEntityKart);

        //クライアントと同期不要な場合return
        if ((Boolean) getFieldValue(Fields.nmsWorld_isClientSide, nmsWorld)) {
            return;
        }

        //コリジョン消去フラグがtrueの場合return
        if ((Boolean) getFieldValue(Fields.nmsEntity_noclip, nmsEntityOther)) {
            return;
        }

        //接触対象が搭乗者の場合return
        if (nmsEntityOther == getFieldValue(Fields.nmsEntity_passenger, nmsEntityKart)) {
            return;
        }

        //接触対象のエンティティによって接触判定から除外しreturn
        if (entityOther instanceof EnderCrystal || entityOther instanceof WitherSkull) {
            return;
        }

        double crashMotionX = (Double) getFieldValue(Fields.nmsEntity_locX, nmsEntityOther)
                - (Double) getFieldValue(Fields.nmsEntity_locX, nmsEntityKart);
        double crashMotionZ = (Double) getFieldValue(Fields.nmsEntity_locZ, nmsEntityOther)
                - (Double) getFieldValue(Fields.nmsEntity_locZ, nmsEntityKart);
        double d2 = (Double) invoke(Methods.static_nmsMathHelper_a, null, crashMotionX, crashMotionZ);

        d2 = Math.sqrt(d2);
        crashMotionX /= d2;
        crashMotionZ /= d2;
        double d3 = 1.0D / d2;

        if (d3 > 1.0D) {
            d3 = 1.0D;
        }

        crashMotionX *= d3;
        crashMotionZ *= d3;

        crashMotionX *= 0.05000000074505806D;
        crashMotionZ *= 0.05000000074505806D;

        if (0.02000000029802322D <= d2) {

            //衝突者が無人カートの場合モーションを固定し、相手のみに反発モーションを適用する
            if (getFieldValue(Fields.nmsEntity_passenger, nmsEntityKart) == null) {
                setFieldValue(Fields.nmsEntity_motX, nmsEntityOther, crashMotionX);
                setFieldValue(Fields.nmsEntity_motY, nmsEntityOther, getFieldValue(Fields.nmsEntity_motY, nmsEntityOther));
                setFieldValue(Fields.nmsEntity_motZ, nmsEntityOther, crashMotionZ);

            //衝突者が有人カートの場合、nmsEntityKartとnmsEntityOtherに衝突モーションを適用する
            //衝突モーションはお互いのモーション値の差(速度の差)を基に算出される
            } else {

                //衝突対象が無人カートの場合、自身のみに反発モーションを適用しreturnする
                if (getFieldValue(Fields.nmsEntity_passenger, nmsEntityOther) == null) {
                    setFieldValue(Fields.nmsEntity_motX, nmsEntityKart, -crashMotionX);
                    setFieldValue(Fields.nmsEntity_motY, nmsEntityKart, getFieldValue(Fields.nmsEntity_motY, nmsEntityKart));
                    setFieldValue(Fields.nmsEntity_motZ, nmsEntityKart, -crashMotionZ);
                    return;
                }

                Kart kart = (Kart) invoke(Methods.Ypl_getKart, nmsEntityKart);
                Kart otherKart = RaceManager.getKartObjectByEntityMetaData(entityOther);

                //重量
                double kartWeight = kart.getWeight();
                double otherWeight;
                if (otherKart == null) {
                    //カートエンティティでない場合、width,lengthフィールドの値から重量を算出
                    float otherWidth = (Float) getFieldValue(Fields.nmsEntity_width, nmsEntityOther);
                    float otherLength = (Float) getFieldValue(Fields.nmsEntity_length, nmsEntityOther);
                    otherWeight = otherWidth * otherWidth * otherLength;
                } else {
                    otherWeight = otherKart.getWeight();
                }

                //モーション値
                final double kartMotionX = (Double) getFieldValue(Fields.nmsEntity_motX, nmsEntityKart);
                final double kartMotionZ = (Double) getFieldValue(Fields.nmsEntity_motZ, nmsEntityKart);
                final double otherMotionX = (Double) getFieldValue(Fields.nmsEntity_motX, nmsEntityOther);
                final double otherMotionZ = (Double) getFieldValue(Fields.nmsEntity_motZ, nmsEntityOther);
                double kartMotionSpeed = calcMotionSpeed(kartMotionX, kartMotionZ) * kartWeight;
                double otherMotionSpeed = calcMotionSpeed(otherMotionX, otherMotionZ) * otherWeight;

                //衝突対象のcollideメソッドと処理が重複するため、衝突者のモーション値が劣っている場合return
                if (kartMotionSpeed < otherMotionSpeed) {
                    return;
                }

                //お互いのモーション値の差から衝突の衝撃係数を算出
                double crashSpeed = (kartMotionSpeed - otherMotionSpeed);
                //数値の幅を後半大きく伸びる曲線状に
                crashSpeed *= crashSpeed;
                crashSpeed *= 1000.0D; // ノーマライズ

                //衝撃係数を基に、重量でダメージを付与し、ダメージ量に応じた演出を再生
                Entity kartPassenger = Util.getEndPassenger(entityKart);
                Entity otherPassenger = Util.getEndPassenger(entityOther);
                float soundVolume = (float) (0.5F + crashSpeed / 10.0D);
                if (4.0F < soundVolume) {
                    soundVolume = 4.0F;
                }
                if (kartPassenger instanceof LivingEntity) {
                    long ownDamage = Math.round(crashSpeed / 2.0D / kartWeight);
                    if (1 <= ownDamage) {
                        //Util.addDamage(kartPassenger, null, (int) ownDamage);
                        if (kartPassenger instanceof Player) {
                            Player kartPlayer = (Player) kartPassenger;
                            Location location = kartPlayer.getLocation();
                            kartPlayer.playSound(
                                    kartPassenger.getLocation(), Sound.AMBIENCE_THUNDER, soundVolume, 2.0F);
                            kartPlayer.playSound(
                                    kartPassenger.getLocation(), Sound.AMBIENCE_THUNDER, soundVolume, 0.5F);
                            kartPlayer.playSound(
                                    kartPassenger.getLocation(), Sound.IRONGOLEM_HIT, soundVolume, 0.5F);
                            PacketUtil.sendParticlePacket(null, Particle.CRIT, location, 0.0F, 0.0F, 0.0F, 1, 10, new int[]{});
                            PacketUtil.sendParticlePacket(null, Particle.CLOUD, location, 0.0F, 0.0F, 0.0F, 1, 10, new int[]{});
                        }
                    }
                }
                if (otherPassenger instanceof LivingEntity) {
                    long otherDamage = Math.round(crashSpeed / 2.0D / otherWeight);
                    if (1 <= otherDamage) {
                        Util.addDamage(otherPassenger, kartPassenger, (int) otherDamage);
                        if (otherPassenger instanceof Player) {
                            Player otherPlayer = (Player) otherPassenger;
                            Location location = otherPlayer.getLocation();
                            otherPlayer.playSound(
                                    otherPassenger.getLocation(), Sound.AMBIENCE_THUNDER, soundVolume, 2.0F);
                            otherPlayer.playSound(
                                    otherPassenger.getLocation(), Sound.AMBIENCE_THUNDER, soundVolume, 0.5F);
                            otherPlayer.playSound(
                                    otherPassenger.getLocation(), Sound.IRONGOLEM_HIT, soundVolume, 0.5F);
                            PacketUtil.sendParticlePacket(null, Particle.CRIT, location, 0.0F, 0.0F, 0.0F, 1, 10, new int[]{});
                            PacketUtil.sendParticlePacket(null, Particle.CLOUD, location, 0.0F, 0.0F, 0.0F, 1, 10, new int[]{});
                        }
                    }
                }

                //衝撃係数をスピードスタックの減衰率に変換
                //カートエンティティがトップスピードで静止したエンティティに衝突した際に、
                //8割程度のスピードスタックが消失するよう変換
                crashSpeed *= 3.5D;

                //衝突者のスピードスタックを重量を加味した上で減衰
                double nmsEntityKartSpeedStack = (Double) invoke(Methods.Ypl_getSpeedStack, nmsEntityKart);
                invoke(Methods.Ypl_setSpeedStack, nmsEntityKart, nmsEntityKartSpeedStack - (crashSpeed / kartWeight));

                //衝突対象のエンティティがカートエンティティだった場合、重量を加味した上でスピードスタックを減衰
                if (otherKart != null) {
                    double nmsEntityOtherSpeedStack = (Double) invoke(Methods.Ypl_getSpeedStack, nmsEntityOther);
                    invoke(Methods.Ypl_setSpeedStack, nmsEntityOther, nmsEntityOtherSpeedStack
                            - (crashSpeed / otherWeight));
                }

                //衝突者に衝突モーションの適用
                crashMotionX *= 2.0D;                crashMotionZ *= 2.0D;
                setFieldValue(Fields.nmsEntity_motX, nmsEntityKart, kartMotionX - crashMotionX);
                setFieldValue(Fields.nmsEntity_motZ, nmsEntityKart, kartMotionZ - crashMotionZ);

                //衝突対象に衝突モーションの適用
                crashMotionX *= 2.0D;
                crashMotionZ *= 2.0D;
                setFieldValue(Fields.nmsEntity_motX, nmsEntityOther, otherMotionX + crashMotionX);
                setFieldValue(Fields.nmsEntity_motZ, nmsEntityOther, otherMotionZ + crashMotionZ);
            }
        }
    }

    //〓 Setter Motion 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * カートのモーションを適用する
     * キラー使用中のモーションを適用する場合はapplyKillerMotion(Racer r)を利用する
     * @param entityHuman 計算の基となるEntityHuman
     */
    public static void setNormalMotion(Object nmsEntityKart, Object entityHuman) {
        Player player = (Player) invoke(Methods.nmsEntity_getBukkitEntity, entityHuman);

        //キラー用変数の初期化
        invoke(Methods.Ypl_setKillerPassedCheckPointList, nmsEntityKart, new Object[]{null});
        invoke(Methods.Ypl_setKillerLastPassedCheckPoint, nmsEntityKart, new Object[]{null});
        invoke(Methods.Ypl_setKillerX, nmsEntityKart, 0);
        invoke(Methods.Ypl_setKillerY, nmsEntityKart, 0);
        invoke(Methods.Ypl_setKillerZ, nmsEntityKart, 0);

        //クライアントの移動入力値。レースカート、かつレースが開始されていない場合は除外
        float sideInput = (Float) getFieldValue(Fields.nmsEntityHuman_sideMotionInput, entityHuman) * 0.8F;
        float forwardInput = (Float) getFieldValue(Fields.nmsEntityHuman_forwardMotionInput, entityHuman) * 1.2F;
        if (invoke(Methods.Ypl_getKartType, nmsEntityKart).equals(KartType.RacingKart)) {
            if (!RaceManager.isStarted(player.getUniqueId())) {
                sideInput = 0.0F;
                forwardInput = 0.0F;
            }
        }

        //スピードスタックの算出、格納
        double speedStack = calcSpeedStack(nmsEntityKart, entityHuman);
        invoke(Methods.Ypl_setSpeedStack, nmsEntityKart, speedStack);

        //スピードスタックを基に縦方向への移動入力値を変換
        forwardInput = calcForwardInput(nmsEntityKart, forwardInput);

        //横方向への移動入力値を基にYawを変更
        Kart kart = (Kart) invoke(Methods.Ypl_getKart, nmsEntityKart);
        float yaw = (Float) getFieldValue(Fields.nmsEntity_yaw, nmsEntityKart);
        if (Permission.hasPermission(player, Permission.KART_DRIFT, true)) {
            if (RaceManager.getRacer(player).isSneaking()) {
                yaw -= sideInput * kart.getDriftCorneringPower();
            } else {
                yaw -= sideInput * kart.getDefaultCorneringPower();
            }
        } else {
            yaw -= sideInput * kart.getDefaultCorneringPower();
        }
        invoke(Methods.nmsEntity_setYawPitch, nmsEntityKart, yaw, 0);

        //横方向の入力をモーションに適用しないため0を代入
        sideInput = 0;

        //移動入力値を基にモーションを算出し格納
        float normalizeMotion = forwardInput * forwardInput + sideInput * sideInput;
        if (normalizeMotion >= 1.0E-004F) {
            normalizeMotion = (float) Math.sqrt(normalizeMotion);
            if (normalizeMotion < 1.0F) {
                normalizeMotion = 1.0F;
            }

            normalizeMotion = ((Float) invoke(Methods.nmsEntityHuman_getAttributesMovementSpeed, entityHuman) / 2.0F) / normalizeMotion;
            forwardInput *= normalizeMotion;
            sideInput *= normalizeMotion;

            float sin = (Float) invoke(Methods.static_nmsMathHelper_sin, null, yaw * 3.141593F / 180.0F);
            float cos = (Float) invoke(Methods.static_nmsMathHelper_cos, null, yaw * 3.141593F / 180.0F);

            double motX = (Double) getFieldValue(Fields.nmsEntity_motX, nmsEntityKart);
            double motZ = (Double) getFieldValue(Fields.nmsEntity_motZ, nmsEntityKart);

            motX -= forwardInput * sin + sideInput * cos;
            motZ -= sideInput * sin - forwardInput * cos;

            setFieldValue(Fields.nmsEntity_motX, nmsEntityKart, motX);
            setFieldValue(Fields.nmsEntity_motZ, nmsEntityKart, motZ);
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
     * 距離が5ブロック以内のチェックポイントのみを通過済みリストに格納するよう処理している<br>
     * <br>
     * 凡例:<br>
     * CheckPoint : CP
     *
     * @param racer
     */
    @SuppressWarnings("unchecked")
    public static void setKillerMotion(Object entityKart, Racer racer) {
        int cPHeihgt = RaceManager.checkPointHeight;
        Location kartLocation = ((Entity) invoke(Methods.nmsEntity_getBukkitEntity, entityKart)).getLocation();
        List<String> passedCPList = (List<String>) invoke(Methods.Ypl_getKillerPassedCheckPointList, entityKart);

        //キラー用モーションの適用
        double killerX = (Double) invoke(Methods.Ypl_getKillerX, entityKart);
        double killerZ = (Double) invoke(Methods.Ypl_getKillerZ, entityKart);
        if (killerX != 0 && killerZ != 0) {
            setFieldValue(Fields.nmsEntity_motX, entityKart, killerX);
            setFieldValue(Fields.nmsEntity_motZ, entityKart, killerZ);
        }

        //Yawを現在のモーションの方向へ変更
        double motX = (Double) getFieldValue(Fields.nmsEntity_motX, entityKart);
        double motY = (Double) getFieldValue(Fields.nmsEntity_motY, entityKart);
        double motZ = (Double) getFieldValue(Fields.nmsEntity_motZ, entityKart);
        invoke(Methods.nmsEntity_setYawPitch, entityKart
                , Util.getYawFromVector(new Vector(motX, motY, motZ)) + 90, 0);

        //初回起動時のみ
        if (passedCPList == null) {
            //キラー使用者が通過済みのチェックポイントリストを引き継ぐ
            invoke(Methods.Ypl_setKillerPassedCheckPointList, entityKart
                    , new ArrayList<String>(racer.getPassedCheckPointList()));

            //キラー使用時に取得した、最寄の未通過のチェックポイントを格納
            invoke(Methods.Ypl_setKillerLastPassedCheckPoint, entityKart, racer.getUsingKiller());

            //最寄の未通過のチェックポイントへ向けたベクターを算出
            Vector vector = Util.getVectorToLocation(kartLocation,
                    ((Entity) invoke(Methods.Ypl_getKillerLastPassedCheckPoint, entityKart))
                    .getLocation().add(0, -cPHeihgt, 0)).multiply(1.5);

            //算出したベクターのX、Zモーションを格納
            //Yモーションは利用しない
            invoke(Methods.Ypl_setKillerX, entityKart, vector.getX());
            invoke(Methods.Ypl_setKillerZ, entityKart, vector.getZ());
        }

        //最寄の未通過のチェックポイントとの平面距離が5ブロック以内になるまでreturnする
        Entity lastPassedCP = (Entity) invoke(Methods.Ypl_getKillerLastPassedCheckPoint, entityKart);
        if (lastPassedCP != null) {
            double distance = lastPassedCP.getLocation()
                    .distance(kartLocation.clone().add(0, cPHeihgt, 0));
            if ( 5 < distance) {
                return;
            }
        }

        //周囲のチェックポイントを取得し、検出できなかった場合return
        ArrayList<Entity> nearbyCPList = RaceManager.getNearbyCheckpoint(kartLocation, 40, racer.getCircuitName());
        if (nearbyCPList == null) {
            return;
        }

        //周囲のチェックポイントの内、未通過のチェックポイントを抽出
        ArrayList<Entity> unpassedCPList = new ArrayList<Entity>();
        String lap = racer.getCurrentLaps() <= 0 ? "" : String.valueOf(racer.getCurrentLaps());
        for (Entity nearbyCP : nearbyCPList) {
            if (!passedCPList.contains(lap + nearbyCP.getUniqueId().toString())) {
                unpassedCPList.add(nearbyCP);
            }
        }

        //未通過のチェックポイントが検出できなかった場合return
        if (unpassedCPList.isEmpty()) {
            return;
        }

        //最寄の未通過のチェックポイントを格納
        Entity unpassedClosestCP = Util.getNearestEntity(unpassedCPList, kartLocation);
        invoke(Methods.Ypl_setKillerLastPassedCheckPoint, entityKart, unpassedClosestCP);

        //最寄の未通過のチェックポイントへ向けたベクターを算出
        Vector v = Util.getVectorToLocation(kartLocation, unpassedClosestCP.getLocation().add(0, -cPHeihgt, 0)).multiply(1.5);

        //算出したベクターのX、Zモーションを格納
        //Yモーションは利用しない
        invoke(Methods.Ypl_setKillerX, entityKart, v.getX());
        invoke(Methods.Ypl_setKillerZ, entityKart, v.getZ());

        //チェックポイントを通過済みリストに格納

        passedCPList.add(lap + unpassedClosestCP.getUniqueId().toString());
        invoke(Methods.Ypl_setKillerPassedCheckPointList, entityKart
                , new ArrayList<String>(passedCPList));
    }

    /**
     * 現在のモーション値に摩擦係数を適用し徐々に減衰させる<br>
     * このメソッドを実行しなかった場合、氷の上で滑るような動きを再現できる
     */
    public static void setFrictionMotion(Object nmsEntityKart) {

        Object passenger = ReflectionUtil.getFieldValue(Fields.nmsEntity_passenger, nmsEntityKart);
        double motX = (Double) ReflectionUtil.getFieldValue(Fields.nmsEntity_motX, nmsEntityKart);
        double motY = (Double) ReflectionUtil.getFieldValue(Fields.nmsEntity_motY, nmsEntityKart);
        double motZ = (Double) ReflectionUtil.getFieldValue(Fields.nmsEntity_motZ, nmsEntityKart);

        //搭乗者がいる場合
        if (passenger != null) {

            //プレイヤーが搭乗している場合
            if (instanceOf(passenger, Classes.nmsEntityPlayer)) {
                // Do nothing

            //プレイヤー以外が搭乗している場合
            } else {
                double frictionValue = 0.4D;

                motX = motX < -frictionValue ? -frictionValue : (motX > frictionValue ? frictionValue : motX);
                motZ = motZ < -frictionValue ? -frictionValue : (motZ > frictionValue ? frictionValue : motZ);

                ReflectionUtil.setFieldValue(Fields.nmsEntity_motX, nmsEntityKart, motX);
                ReflectionUtil.setFieldValue(Fields.nmsEntity_motZ, nmsEntityKart, motZ);
            }
        }

        boolean isOnGround = (Boolean) ReflectionUtil.getFieldValue(Fields.nmsEntity_onGround, nmsEntityKart);

        //地面にいる場合は地面との摩擦係数を適用
        if (isOnGround) {
            motX *= (Double) invoke(Methods.Ypl_getGroundFrictionX, nmsEntityKart);
            motY *= (Double) invoke(Methods.Ypl_getGroundFrictionY, nmsEntityKart);
            motZ *= (Double) invoke(Methods.Ypl_getGroundFrictionZ, nmsEntityKart);
        }

        //移動
        invoke(Methods.nmsEntity_move, nmsEntityKart, motX, motY, motZ);

        //空中にいる場合は地面との摩擦係数を適用
        //なぜmoveの後に適用するのかは不明
        if (!isOnGround) {
            motX *= (Double) invoke(Methods.Ypl_getFlyFrictionX, nmsEntityKart);
            motY *= (Double) invoke(Methods.Ypl_getFlyFrictionY, nmsEntityKart);
            motZ *= (Double) invoke(Methods.Ypl_getFlyFrictionZ, nmsEntityKart);
        }

        setFieldValue(Fields.nmsEntity_motX, nmsEntityKart, motX);
        setFieldValue(Fields.nmsEntity_motY, nmsEntityKart, motY);
        setFieldValue(Fields.nmsEntity_motZ, nmsEntityKart, motZ);
    }

    //〓 Setter Parameter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * パラメータを引数kartのオブジェクトから引き継ぐ
     * @param kart パラメータを引き継ぐKartオブジェクト
     */
    public static void setParameter(Object kartEntity, Kart kart) {
        invoke(Methods.Ypl_setKart, kartEntity, kart);
        setFieldValue(Fields.nmsEntity_climbableHeight, kartEntity, kart.getClimbableHeight());
        setDisplayMaterial(kartEntity, kart.getDisplayMaterial(), kart.getDisplayMaterialData());
    }

    /**
     * カートの外見を指定されたマテリアルに変更する
     * 正確には、アーマースタンドが手に持っているアイテムを変更する
     * @param displayMaterial 新たに表示するアイテムのマテリアル
     * @param displayMaterialData 新たに表示するアイテムのマテリアルデータ
     */
    public static void setDisplayMaterial(Object kartEntity, Material displayMaterial, byte displayMaterialData) {

        //右手のアイテムの変更
        ItemStack itemStack = new ItemStack(displayMaterial, 1, (short) 0,displayMaterialData);
        ArmorStand armorStand = (ArmorStand) invoke(Methods.nmsEntity_getBukkitEntity, kartEntity);
        armorStand.setItemInHand(itemStack);

        //装備を変更した外見のパケットを送信
        //データ上手に持っているアイテムは書き換わっているが、見た目の更新は何故かされないため
        //明示的にパケットを送信する
        int entityId = (Integer) invoke(Methods.nmsEntity_getId, kartEntity);
        Object craftItemStack = invoke(Methods.static_craftItemStack_asNMSCopy, null, itemStack);
        PacketUtil.sendEntityEquipmentPacket(null, entityId, 0, craftItemStack);

        //腕の角度を調整
        float pitch = (Float) getFieldValue(Fields.nmsEntity_pitch, kartEntity);
        Object vector3f = newInstance(Constructors.nmsVector3f, -26.0F + pitch, 1.00F, 0.0F);
        invoke(Methods.nmsEntityArmorStand_setRightArmPose, kartEntity, vector3f);
    }

    //〓 Play Effect 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** エンジン音、排気口のパーティクルを生成する */
    public static void playIdleEffect(Player player, Location kartEntityLocation, double speedStack) {
        kartEntityLocation.add(0, 0.5, 0);

        /*
         * 排気口付近の座標を取得
         * 移動中は座標のズレからパーティクルの生成位置が通常より前に出てきてしまうため、速度に応じて調整する
         */
        kartEntityLocation = Util.getForwardLocationFromYaw(kartEntityLocation, -1.5D - speedStack / 80.0D);

        //パーティクルを生成する
        PacketUtil.sendParticlePacket(null, Particle.SPELL, kartEntityLocation, 0.1F, 0.1F, 0.2F, 1, 5, new int[]{});

        //音声を再生する
        player.playSound(player.getLocation()
                , Sound.COW_WALK, 0.2F, 0.05F + ((float) speedStack / 200));
        player.playSound(player.getLocation()
                , Sound.GHAST_FIREBALL, 0.01F + ((float) speedStack / 400), 1.0F);
        player.playSound(player.getLocation()
                , Sound.FIZZ, 0.01F + ((float) speedStack / 400), 0.5F);
    }

    /** ドリフト中の火花パーティクルを生成する */
    public static void playDriftEffect(Player player, Location kartEntityLocation, double speedStack) {
        if (RaceManager.getRacer(player).isSneaking()) {

            //スピードスタックが100を越える場合のみ火花のパーティクルを生成する
            if (100 < speedStack) {

                /*
                 * 後輪付近の座標を取得
                 * 移動中は座標のズレからパーティクルの生成位置が通常より前に出てきてしまうため、速度に応じて調整する
                 */
                kartEntityLocation = Util.getForwardLocationFromYaw(kartEntityLocation, -speedStack / 80.0D);

                //後輪付近に火花のパーティクルを散らす
                PacketUtil.sendParticlePacket(null, Particle.LAVA, kartEntityLocation, 0.0F, 0.0F, 0.0F, 1, 5, new int[]{});
            }

            //音声を再生する
            player.playSound(player.getLocation(), Sound.FIREWORK_BLAST, 1.0F, 7.0F);
        }
    }

    /** キラー使用中のエンジン音、排気口のパーティクルを生成する */
    public static void playKillerEffect(Player player, Location kartEntityLocation) {
        kartEntityLocation.add(0, 0.5, 0);

        //キラーの排気口付近の座標を取得
        Location particleLocation = Util.getForwardLocationFromYaw(kartEntityLocation, -5);
        Location particleLocation2 = Util.getForwardLocationFromYaw(kartEntityLocation, -10);

        //パーティクルを生成する
        PacketUtil.sendParticlePacket(null, Particle.REDSTONE, particleLocation, 0.5F, 0.5F, 0.5F, 1.0F, 20, new int[]{});
        PacketUtil.sendParticlePacket(null, Particle.SMOKE_LARGE, particleLocation2, 0.5F, 0.5F, 0.5F, 0.0F, 20, new int[]{});

        //音声を再生する
        player.playSound(player.getLocation(), Sound.GHAST_FIREBALL, 0.05F, 1.5F);
        player.playSound(player.getLocation(), Sound.FIZZ, 0.05F, 1.0F);
    }

    //〓 Calculation 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 引数humanの移動に関する入力係数を基にスピードスタックを算出し返す
     * @param kartEntity 引数humanが搭乗しているKartEntity
     * @param human 取得するEntityHuman
     * @return 算出したスピードスタック
     */
    public static double calcSpeedStack(Object kartEntity, Object human) {
        Player player = (Player) invoke(Methods.nmsEntity_getBukkitEntity, human);
        Racer race = RaceManager.getRacer(player);
        Kart kartObject = (Kart) invoke(Methods.Ypl_getKart, kartEntity);

        double speedStack = (Double) invoke(Methods.Ypl_getSpeedStack, kartEntity);

        /*
         * ダッシュボード、ポーションの効果をスピードスタックに上乗せしreturnする
         * returnしなければ、後の処理で最大値・最小値を越えている場合、正常値にマージされてしまう
         * 両効果が重複してしまうと爆発的なスピードが出てしまうため、
         * ダッシュボードに接触している場合はポーションの効果は無視する
         */

        //ダッシュボードに接触した場合、スピードスタックを最大値+αしreturnする
        if (race.isStepDashBoard()) {

            //最高速度 + ダッシュボードのエフェクトLV + キャラクター補正
            speedStack = kartObject.getMaxSpeed()
                    * (Integer) ConfigEnum.ITEM_DASH_BOARD_EFFECT_LEVEL.getValue()
                    + race.getCharacter().getAdjustPositiveEffectLevel() * 50;

            //アイテム使用中の最高速度以内の数値になるよう調整
            speedStack = speedStack < kartObject.getBoostedMaxSpeed()
                    ? speedStack : kartObject.getBoostedMaxSpeed();

            return speedStack;
        } else {

            //スピードに影響するポーション効果を保持している場合、スピードスタックを操作しreturnする
            for (PotionEffect potion : player.getActivePotionEffects()) {

                //スピードポーション効果を保持している場合、スピードスタックを最大値+αしreturnする
                if (potion.getType().getName().equalsIgnoreCase("SPEED")) {

                    //最高速度 + ポーションエフェクトLV + キャラクター補正
                    speedStack = kartObject.getMaxSpeed() + potion.getAmplifier() * 10
                            + race.getCharacter().getAdjustPositiveEffectLevel() * 10;

                    //アイテム使用中の最高速度以内の数値になるよう調整
                    speedStack = speedStack < kartObject.getBoostedMaxSpeed()
                            ? speedStack : kartObject.getBoostedMaxSpeed();

                    return  speedStack;
                }

                //スロウポーション効果を保持している場合、スピードスタックを急激に減衰しreturnする
                if (potion.getType().getName().equalsIgnoreCase("SLOW")) {
                    if (speedStack < potion.getAmplifier()) {
                        return 0;
                    } else {
                        return speedStack - potion.getAmplifier();
                    }
                }
            }
        }

        float forwardMotionInput =
                (Float) getFieldValue(Fields.nmsEntityHuman_forwardMotionInput, human);
        //前方へキーを入力している
        if (0 < forwardMotionInput) {
            if (!isDirtBlock(kartEntity)) {
                speedStack += kartObject.getAcceleration();
            } else {
                speedStack -= kartObject.getSpeedDecreaseOnDirt();
            }

        //後方へキーを入力している
        } else if (forwardMotionInput < 0) {
            speedStack -= 10;

        //入力していない
        } else if (0 == forwardMotionInput) {
            speedStack -= 4;
        }

        //最大値・最小値を越えている場合、正常値にマージする
        if (kartObject.getMaxSpeed() < speedStack) {
            speedStack = kartObject.getMaxSpeed();
        } else if (speedStack < 0) {
            speedStack = 0;
        }

        /*
         * モーション値が限りなく0に近い場合はスピードスタックを0にする
         * 壁に衝突した場合などの急激なモーションのストップに対する処理
         * ただしデスペナルティ中は除外する
         */
        if (race.getDeathPenaltyTask() == null) {
            double motX = (Double) getFieldValue(Fields.nmsEntity_motX, kartEntity);
            double motZ = (Double) getFieldValue(Fields.nmsEntity_motZ, kartEntity);
            BigDecimal bd = new BigDecimal(motX * motX + motZ * motZ);
            double mot = bd.setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue();
            if (mot == 0) {
                speedStack = 0;
            }
        }

        return speedStack;
    }

    /**
     * スピードスタックを基に、縦方向の移動入力値を変換し返す
     * @param forwardInput 変換前の縦方向の移動入力値
     * @return 変換した縦方向の移動入力値
     */
    public static float calcForwardInput(Object kartEntity, float forwardInput) {
        Kart kart = (Kart) invoke(Methods.Ypl_getKart, kartEntity);
        double speedStack = (Double) invoke(Methods.Ypl_getSpeedStack, kartEntity);

        //前方へキーを入力している
        if (0 < forwardInput) {
            forwardInput *= 0.1;
            forwardInput += speedStack / 400;

        //後方へキーを入力している
        } else if (forwardInput < 0) {
            if (isDirtBlock(kartEntity)) {
                forwardInput *= kart.getSpeedDecreaseOnDirt() * 0.1;
            } else {
                forwardInput *= 0.1;
            }

        //入力していない
        } else {
            // Do nothing
        }

        return forwardInput;
    }

    public static double calcMotionSpeed(double x, double z) {
        BigDecimal bd = new BigDecimal(x * x + z * z);
        return bd.doubleValue();
    }

    //〓 Util 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @return ダートブロックかどうか */
    public static boolean isDirtBlock(Object kartEntity) {
        Location location = ((Entity) invoke(Methods.nmsEntity_getBukkitEntity, kartEntity)).getLocation();

        return Util.getGroundBlockID(location).equalsIgnoreCase((String) ConfigEnum.DIRT_BLOCK_ID.getValue());
    }

    /**
     * 引数nmsEntityKartがデスポーンしているかどうかを返す
     * @param nmsEntityKart 判別するNmsEntity
     * @return 引数nmsEntityKartがデスポーンしているかどうか
     */
    public static boolean isDead(int entityId, Object nmsWorld) {
        Object nmsEntityKart = ReflectionUtil.invoke(Methods.nmsWorld_getNmsEntityById, nmsWorld, entityId);

        return nmsEntityKart == null;
    }

    /**
     * カートエンティティのY方向の描画位置をkart.ymlのmount_position_offsetの値に応じて調整した座標を返す
     * @return 調整後の座標
     */
    public static Location getMountPositionAdjustedLocation(Entity kartEntity) {
        Kart kartObject = RaceManager.getKartObjectByEntityMetaData(kartEntity);
        Location location = kartEntity.getLocation();

        /*
         * カートエンティティ直下の直近のソリッドブロック、及びその座標
         * カートと接触するBoundingBoxの面の正確な座標が必要なため、NmsBlockから取得したブロックの高さを
         * 座標から減算する
         */
        Block groundBlock = Util.getGroundBlock(location);
        Location groundBlockLocation;
        if (groundBlock == null) {
            groundBlockLocation = location;
        } else {
            Object nmsGroundBlock = ReflectionUtil.invoke(Methods.craftBlock_getNMSBlock, groundBlock);
            groundBlockLocation = groundBlock.getLocation();
            groundBlockLocation.add(
                    0.0D, -1.0D + (Double) ReflectionUtil.getFieldValue(Fields.nmsBlock_maxY, nmsGroundBlock), 0.0D);
        }

        //Y座標のオフセット
        double offsetY = -0.25D + kartObject.getMountPositionOffset();

        //オフセットを適用したY座標
        double newLocationY = location.getY() + offsetY;

        /*
         * この時点でカートエンティティのY座標と地面との距離は必ずoffsetYの値になるはずだが、
         * 半ブロック等を通過時にズレが生じるため、距離が必ずoffsetYの値になるように調整する
         */
        offsetY -= newLocationY - groundBlockLocation.getY();
        newLocationY += offsetY;

        location.setY(newLocationY);

        return location;
    }
}
