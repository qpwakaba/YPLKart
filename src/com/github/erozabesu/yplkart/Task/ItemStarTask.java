package com.github.erozabesu.yplkart.Task;

import java.util.HashMap;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.Data.Settings;
import com.github.erozabesu.yplkart.Utils.Util;

public class ItemStarTask extends BukkitRunnable{
	Player p;
	int life = 0;
	int effectsecond;
	int hitdamage;
	private static HashMap<Player, ItemStarTask> boostTask = new HashMap<Player, ItemStarTask>();

	public ItemStarTask(Player p){
		this.p = p;
		this.effectsecond = (Settings.StarEffectSecond + RaceManager.getRace(p).getCharacter().getItemAdjustPositiveEffectSecond()) * 20;
		this.hitdamage = Settings.StarHitDamage + RaceManager.getRace(p).getCharacter().getItemAdjustAttackDamage();

		if(boostTask.get(p) != null){
			boostTask.get(p).cancel();
			boostTask.put(p, null);
		}

		boostTask.put(p, this);

		Util.setItemDecrease(p);
		RaceManager.getRace(p).setDeathPenaltyTask(null);
		RaceManager.getRace(p).setItemNegativeSpeedTask(null);
		p.setNoDamageTicks(effectsecond);
		p.setFireTicks(0);
		p.setWalkSpeed(Settings.StarWalkSpeed);
		p.removePotionEffect(PotionEffectType.SLOW);
		p.removePotionEffect(PotionEffectType.BLINDNESS);
		p.removePotionEffect(PotionEffectType.NIGHT_VISION);
		p.removePotionEffect(PotionEffectType.INVISIBILITY);
		p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, effectsecond, 1));
		p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, effectsecond, 1));

		p.playSound(p.getLocation(), Sound.LEVEL_UP, 0.5F, 2.0F);
	}

	public void die(){
		p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
		this.cancel();
	}

	@Override
	public void run(){
		if(RaceManager.getRace(p).getGoal()){
			p.setWalkSpeed(0.2F);
			die();
			return;
		}
		if(effectsecond < life){
			p.setWalkSpeed(RaceManager.getRace(p).getCharacter().getWalkSpeed());
			die();
			return;
		}
		life++;

		for(Player other : Util.getNearbyPlayers(this.p.getLocation(), 1.5)){
			Util.addDamage(other, this.p, this.hitdamage);
		}

		if(life % 20 == 0)
			p.playSound(p.getLocation(), Sound.LEVEL_UP, 0.5F, 2.0F);
	}
}
