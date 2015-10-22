package com.github.erozabesu.yplkart.utils;

import java.util.List;

import org.bukkit.Color;
import org.bukkit.EntityEffect;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.YPLKart;
import com.github.erozabesu.yplkart.data.MessageEnum;
import com.github.erozabesu.yplkart.object.Circuit;
import com.github.erozabesu.yplkart.object.MessageParts;
import com.github.erozabesu.yplkart.object.Racer;
import com.github.erozabesu.yplutillibrary.enumdata.Particle;
import com.github.erozabesu.yplutillibrary.util.CommonUtil;
import com.github.erozabesu.yplutillibrary.util.PacketUtil;
import com.github.erozabesu.yplutillibrary.util.ReflectionUtil;

public class YPLUtil extends ReflectionUtil {

    //〓 Player 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static void setPotionEffect(Player p, PotionEffectType effect, int second, int level) {
        p.removePotionEffect(effect);
        p.addPotionEffect(new PotionEffect(effect, second * 20, level));
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

        List<LivingEntity> entities = CommonUtil.getNearbyLivingEntities(location, range);
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

            Vector vector = CommonUtil.getVectorToLocation(damaged.getLocation(), location);
            vector.setX(vector.clone().multiply(-1).getX());
            vector.setY(0);
            vector.setZ(vector.clone().multiply(-1).getZ());
            damaged.setVelocity(vector);

            addDamage(damaged, executor, damage);
        }
    }

    //〓 Particle 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static void createFlowerShower(Player p, int maxlife) {
        new FlowerShowerTask(p, maxlife).runTaskTimer(YPLKart.getInstance(), 0, 1);
    }

    public static class FlowerShowerTask extends BukkitRunnable {
        int life = 0;
        int maxlife = 0;
        Player player;

        public FlowerShowerTask(Player p, int maxlife) {
            this.player = p;
            this.maxlife = maxlife * 20;
        }

        @Override
        public void run() {
            life++;

            if (maxlife < life || !player.isOnline()) {
                this.cancel();
                return;
            }

            Location location = player.getLocation();
            if (life % 4 == 0) {
                PacketUtil.sendParticlePacket(null, Particle.REDSTONE, location, 7.0F, 7.0F, 7.0F, 1, 100, new int[]{});
            }
        }
    }

    public static void createSignalFireworks(Location location) {
        World world = location.getWorld();
        FireworkEffect effect = FireworkEffect.builder().withColor(Color.GREEN).withFade(Color.LIME).with(Type.BALL_LARGE).build();
        FireworkEffect effect2 = FireworkEffect.builder().withColor(Color.YELLOW).withFade(Color.ORANGE).with(Type.STAR).build();
        FireworkEffect effect3 = FireworkEffect.builder().withColor(Color.RED).withFade(Color.PURPLE).with(Type.CREEPER).build();
        FireworkEffect effect4 = FireworkEffect.builder().withColor(Color.AQUA).withFade(Color.BLUE).with(Type.BURST).build();

        PacketUtil.playFireworksParticle(effect, location, 20, 5, 20);
        PacketUtil.playFireworksParticle(effect2, location, 20, 5, 20);
        PacketUtil.playFireworksParticle(effect3, location, 20, 5, 20);
        PacketUtil.playFireworksParticle(effect4, location, 20, 5, 20);
    }
}
