package com.github.erozabesu.yplkart.task;

import java.util.HashMap;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.data.ItemEnum;
import com.github.erozabesu.yplkart.object.Racer;
import com.github.erozabesu.yplkart.utils.YPLUtil;
import com.github.erozabesu.yplutillibrary.util.CommonUtil;

public class ItemStarTask extends BukkitRunnable {
    Player p;
    int life = 0;
    int effectsecond;
    int hitdamage;
    private static HashMap<Player, ItemStarTask> boostTask = new HashMap<Player, ItemStarTask>();

    public ItemStarTask(Player p) {
        this.p = p;
        this.effectsecond = (ItemEnum.STAR.getEffectSecond()
                + RaceManager.getRacer(p).getCharacter().getAdjustPositiveEffectSecond()) * 20;
        this.hitdamage = ItemEnum.STAR.getHitDamage()
                + RaceManager.getRacer(p).getCharacter().getAdjustAttackDamage();

        if (boostTask.get(p) != null) {
            boostTask.get(p).cancel();
            boostTask.put(p, null);
        }

        boostTask.put(p, this);

        CommonUtil.decreaseItemInHand(p, 1);
        Racer r = RaceManager.getRacer(p);
        r.setDeathPenaltyTask(null);
        r.setDeathPenaltyTitleSendTask(null);
        r.setItemNegativeSpeedTask(null);
        p.setNoDamageTicks(effectsecond);
        p.setFireTicks(0);
        p.setWalkSpeed(ItemEnum.STAR.getWalkSpeed());
        p.removePotionEffect(PotionEffectType.SLOW);
        p.removePotionEffect(PotionEffectType.BLINDNESS);
        p.removePotionEffect(PotionEffectType.NIGHT_VISION);
        p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, effectsecond, 1));
        p.playSound(p.getLocation(), Sound.LEVEL_UP, 0.5F, 2.0F);
    }

    public void die() {
        p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
        this.cancel();
    }

    @Override
    public void run() {
        p.setNoDamageTicks(10);
        if (RaceManager.getRacer(p).isGoal()) {
            p.setWalkSpeed(0.2F);
            die();
            return;
        }
        if (effectsecond < life) {
            p.setWalkSpeed(RaceManager.getRacer(p).getCharacter().getWalkSpeed());
            die();
            return;
        }
        life++;

        if (life % 2 == 0)
            for (Player other : CommonUtil.getNearbyPlayers(this.p.getLocation(), 1.5)) {
                if (!other.getUniqueId().equals(this.p.getUniqueId()))
                    YPLUtil.addDamage(other, this.p, this.hitdamage);
            }

        if (life % 20 == 0)
            p.playSound(p.getLocation(), Sound.LEVEL_UP, 0.5F, 2.0F);
    }
}
