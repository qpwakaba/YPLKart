package com.github.erozabesu.yplkart.Task;

import java.util.ArrayList;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.github.erozabesu.yplkart.Data.Settings;
import com.github.erozabesu.yplkart.Object.Race;
import com.github.erozabesu.yplkart.Object.RaceManager;
import com.github.erozabesu.yplkart.Utils.Util;

public class ItemDyedTurtle extends BukkitRunnable{
	int life = 0;

	Entity projectile;
	Player shooter;
	Player target;

	double motX;
	double motY;
	double motZ;

	boolean isthornedturtle;
	boolean targetreverse;

	int adjustdamage;
	int lap;
	String lastStepBlock;

	//リアルタイムに取得するとこうらが逆走する場合がある
	ArrayList<String> shooterpassedcheckpoint = new ArrayList<String>();
	ArrayList<String> turtlepassedcheckpoint = new ArrayList<String>();

	public ItemDyedTurtle(Player shooter, Player target, Entity projectile, boolean isthornedturtle, boolean targetreverse){
		this.projectile = projectile;
		this.target = target;

		this.isthornedturtle = isthornedturtle;
		this.targetreverse = targetreverse;

		this.target = target;
		this.shooter = shooter;
		this.adjustdamage = RaceManager.getRace(shooter).getCharacter().getItemAdjustAttackDamage();
		this.lap = RaceManager.getRace(shooter).getLapCount();
		this.lastStepBlock = Util.getStepBlock(this.projectile.getLocation());

		shooterpassedcheckpoint = RaceManager.getRace(shooter).getPassedCheckPoint();

		Util.removeEntityCollision(this.projectile);
	}

	private void die(){
		this.projectile.remove();
		this.cancel();
	}

	@Override
	public void run(){
		life++;

		if(!Util.isOnline(target.getName()) || 60 < life/20){
			die();
			return;
		}

		if(isthornedturtle){
			Util.createSafeExplosion(this.shooter ,this.projectile.getLocation(), Settings.ThornedTurtleMovingDamage + this.adjustdamage, 5);
		}

		//モーション 読み込まれていないチャンクに居る場合はMotionの値ずつテレポートで移動させる
		if(this.motX != 0 && this.motY != 0 && this.motZ != 0){
			this.projectile.setVelocity(new Vector(this.motX, this.motY, this.motZ));

			if(!Util.isLoadedChunk(this.projectile.getLocation())){
				this.projectile.teleport(this.projectile.getLocation().add(this.motX, this.motY, this.motZ));
			}
		}

		//周回数の更新
		if(lastStepBlock.equalsIgnoreCase(Settings.StartBlock))
			if(Util.getStepBlock(this.projectile.getLocation()).equalsIgnoreCase(Settings.GoalBlock))
				lap++;
		this.lastStepBlock = Util.getStepBlock(this.projectile.getLocation());

		//targetを発見したら突撃return
		ArrayList<LivingEntity> livingentity = Util.getNearbyLivingEntities(this.projectile.getLocation(), 20);
		for(LivingEntity target : livingentity){
			if(this.target.getUniqueId().toString().equalsIgnoreCase(target.getUniqueId().toString())){
				Vector v = Util.getVectorLocationToLocation(target.getLocation(), this.projectile.getLocation()).multiply(3);
				this.motX = v.getX();
				this.motY = v.getY();
				this.motZ = v.getZ();

				if(target.getLocation().distance(this.projectile.getLocation()) < 3){
					Util.createSafeExplosion(this.shooter, target.getLocation(), Settings.RedTurtleHitDamage + this.adjustdamage, 3);
					die();
				}
				return;
			}
		}

		//チェックポイントの更新
		Race r = RaceManager.getRace(this.shooter);
		ArrayList<Entity> checkpointlist = new ArrayList<Entity>();

		//アカこうらを1位から2位に向け発射した場合
		if(this.targetreverse){
			ArrayList<Entity> templist = RaceManager.getNearbyCheckpoint(this.projectile.getLocation().add(-this.motX*3,0,-this.motZ*3), 30, r.getEntry());
			if(templist == null)return;

			for (Entity e : templist) {
				if(this.shooterpassedcheckpoint.contains(lap + e.getUniqueId().toString())){
					if(!RaceManager.getRace(this.target).getPassedCheckPoint().contains(lap + e.getUniqueId().toString()))
						if(!this.turtlepassedcheckpoint.contains(lap + e.getUniqueId().toString()))
							checkpointlist.add(e);
				}
			}
		//その他
		}else{
			ArrayList<Entity> templist = RaceManager.getNearbyCheckpoint(this.projectile.getLocation().add(this.motX*3,0,this.motZ*3), 30, r.getEntry());
			if(templist == null)return;

			for (Entity e : templist) {
				if(!this.shooterpassedcheckpoint.contains(lap + e.getUniqueId().toString())){
					if(!this.turtlepassedcheckpoint.contains(lap + e.getUniqueId().toString()))
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
		if(checkpointlist.isEmpty())
			return;

		Entity checkpoint = Util.getNearestEntity(checkpointlist, this.projectile.getLocation());
		this.turtlepassedcheckpoint.add(lap + checkpoint.getUniqueId().toString());
		Vector v = Util.getVectorLocationToLocation(checkpoint.getLocation().add(0,-RaceManager.checkPointHeight, 0), this.projectile.getLocation()).multiply(3);
		this.motX = v.getX();
		this.motY = v.getY();
		this.motZ = v.getZ();
	}
}
