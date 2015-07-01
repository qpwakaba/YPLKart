package com.github.erozabesu.yplkart.Object;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.Scoreboards;
import com.github.erozabesu.yplkart.YPLKart;
import com.github.erozabesu.yplkart.Data.RaceData;
import com.github.erozabesu.yplkart.Data.Settings;
import com.github.erozabesu.yplkart.Enum.EnumCharacter;
import com.github.erozabesu.yplkart.Enum.EnumItem;
import com.github.erozabesu.yplkart.Enum.EnumKarts;
import com.github.erozabesu.yplkart.Task.SendExpandedTitleTask;
import com.github.erozabesu.yplkart.Utils.PacketUtil;
import com.github.erozabesu.yplkart.Utils.Util;

public class Race {
	private UUID id;
	private Location goalposition;

	private EnumCharacter character;
	private EnumKarts kart;

	private String entry;
	private boolean goal;
	private boolean start;

	private String laststepblock;
	private int lapcount;
	private boolean lapstepcool;

	private float lastyaw;

	private String lastpassedcheckpoint;
	private int point;
	private String firstpassedcheckpoint;
	private ArrayList<String> passedcheckpoint;

	private ArrayList<ItemStack> inventory;
	private ArrayList<ItemStack> armorcontents;
	private ArrayList<ItemStack> keyitem;
	private ArrayList<ItemStack> keyarmor;

	private BukkitTask deathpenaltytask;
	private BukkitTask itemPositiveSpeedTask;
	private BukkitTask itemNegativeSpeedTask;
	private BukkitTask playerLookingUpdateTask;

	/*
	 * キラーを使用した際に、周囲にある最寄の未通過のチェックポイントを格納する
	 */
	private Entity usingKiller;

	private boolean cmdForceLeave;

	private boolean stepDashBoard;

	public Race(String id){
		this.id = UUID.fromString(id);
		init();
	}

	public void init(){
		//this.character = EnumCharacter.Human;
		//this.kart = null;
		this.goalposition = getPlayer().getLocation().add(0,1,0);

		this.entry = "";
		this.goal = false;

		this.laststepblock = null;
		this.lapcount = 0;
		this.lapstepcool = false;

		this.lastyaw = 0;

		this.lastpassedcheckpoint = "";
		this.point = 0;
		this.firstpassedcheckpoint = "";
		this.passedcheckpoint = new ArrayList<String>();
		this.inventory = new ArrayList<ItemStack>();
		this.armorcontents = new ArrayList<ItemStack>();

		//if(this.deathpenaltytask != null)
		//	this.deathpenaltytask.cancel();
		this.deathpenaltytask = null;

		//if(this.itemPositiveSpeedTask != null)
		//	this.itemPositiveSpeedTask.cancel();
		this.itemPositiveSpeedTask = null;

		//if(this.itemNegativeSpeedTask != null)
		//	this.itemNegativeSpeedTask.cancel();
		this.itemNegativeSpeedTask = null;

		this.usingKiller = null;

		this.cmdForceLeave = false;

		this.stepDashBoard = false;
	}

	//〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓
	public Player getPlayer(){
		return Bukkit.getPlayer(this.id);
	}

	public String getEntry(){
		return this.entry;
	}

	public boolean getGoal(){
		return this.goal;
	}

	public boolean getStart(){
		return this.start;
	}

	public EnumCharacter getCharacter(){
		return this.character;
	}

	public EnumKarts getKart(){
		return this.kart;
	}

	public int getLapCount(){
		return this.lapcount;
	}

	public boolean getLapStepCool(){
		return this.lapstepcool;
	}

	public String getLastStepBlock(){
		if(this.laststepblock == null)return "";
		return this.laststepblock;
	}

	public float getLastYaw(){
		return this.lastyaw;
	}

	public int getPoint(){
		return this.point;
	}

	public String getFirstPassedCheckPoint(){
		if(this.firstpassedcheckpoint.equalsIgnoreCase(""))return "";
		return this.firstpassedcheckpoint;
	}

	public ArrayList<String> getPassedCheckPoint(){
		return this.passedcheckpoint;
	}

	public Entity getLastPassedCheckPoint(){
		for(Entity e : getPlayer().getWorld().getEntities()){
			if(e.getUniqueId().toString().equalsIgnoreCase(this.lastpassedcheckpoint))
				return e;
		}
		return null;
	}

	public BukkitTask getDeathPenaltyTask(){
		return this.deathpenaltytask;
	}

	public BukkitTask getItemPositiveSpeed(){
		return this.itemPositiveSpeedTask;
	}

	public BukkitTask getItemNegativeSpeed(){
		return this.itemNegativeSpeedTask;
	}

	public BukkitTask getPlayerLookingUpdateTask(){
		return this.playerLookingUpdateTask;
	}

	public Entity getUsingKiller(){
		return this.usingKiller;
	}

	public boolean getCMDFroceLeave(){
		return this.cmdForceLeave;
	}

	public boolean getStepDashBoard(){
		return this.stepDashBoard;
	}

	//〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

	public void setCharacter(EnumCharacter clazz){
		this.character = clazz;
	}

	public void setKart(EnumKarts kart){
		this.kart = kart;
	}

	public void setEntry(String circuitname) {
		this.entry = circuitname;
	}

	public void setGoal(){
		this.goal = true;
		final String entry = getEntry();

		Util.createSignalFireworks(getPlayer().getLocation());
		Util.createFlowerShower(getPlayer(), 20);

		double currentmillisecond = RaceManager.getCircuit(entry).getLapMilliSeconds();

		new SendExpandedTitleTask(getPlayer(), 5, "GOAL!!!", "O", 1, ChatColor.GOLD, false).runTaskTimer(YPLKart.getInstance(), 0, 1);
		PacketUtil.sendTitle(getPlayer(), RaceManager.getGoalPlayer(entry).size() + "位  " + currentmillisecond/1000 + "秒", 10, 100, 10, ChatColor.GREEN, true);
		Util.broadcastMessage(getPlayer().getName() + "さん#Yellow" + String.valueOf(RaceManager.getGoalPlayer(entry).size()) + "位#Greenでゴール！ #WhiteTime : #Yellow" + currentmillisecond/1000 + "#White秒");
		setPoint(getPassedCheckPoint().size() + (RaceManager.getEntryPlayer(entry).size())*10);


		if(getKart() == null)
			RaceData.addRunningRaceLapTime(getPlayer(), entry, currentmillisecond/1000);
		else
			RaceData.addKartRaceLapTime(getPlayer(), entry, currentmillisecond/1000);

		getPlayer().setWalkSpeed(0.2F);
		getPlayer().setMaxHealth(20);
		getPlayer().setHealth(20);
		setStart(false);
		this.character = null;
		this.kart = null;
		RaceManager.leave(getPlayer());

		EnumItem.removeAllKeyItems(getPlayer());
		this.recoveryInventory();
		RaceManager.removeCustomMinecart(getPlayer());

		Bukkit.getScheduler().runTaskLater(YPLKart.getInstance(), new Runnable(){
			public void run(){
				if(RaceData.getGoalPosition(entry) != null){
					getPlayer().teleport(RaceData.getGoalPosition(entry));
				}else{
					getPlayer().teleport(goalposition);
				}
			}
		}, 10);
	}

	public void setStart(boolean value){
		this.start = value;
		RaceManager.setupCircuit(this.entry, this.id);
	}

	public void setStart(boolean value, Location from, Location to){
		setStart(value);
		EnumItem.removeAllKeyItems(getPlayer());

		Vector v = Util.getVectorLocationToLocation(to, from);
		Location l = getPlayer().getLocation().add(v.getX()*5,0,v.getZ()*5);

		ArrayList<Entity> checkpoint = RaceManager.getNearbyCheckpoint(l, 20, getEntry());

		if(checkpoint != null){
			setFirstPassedCheckPoint(checkpoint.get(0).getUniqueId().toString());
		}

		Util.createSignalFireworks(getPlayer().getLocation());
		Util.createFlowerShower(getPlayer(), 5);

		new SendExpandedTitleTask(getPlayer(), 1, "START!!!", "A", 2, ChatColor.GOLD, false).runTaskTimer(YPLKart.getInstance(), 0, 1);
		RaceManager.getCircuit(this.entry).setStart(true);
	}

	public void setLapStepCool(boolean value){
		this.lapstepcool = value;
	}

	public void setLastStepBlock(String value){
		this.laststepblock = value;
	}

	public void setLapCount(int value){
		this.lapcount = value;
	}

	/*public void setPassedCheckPoint(Player p, ArrayList<String> value){
		passedcheckpoint.put(p.getUniqueId(), value);
	}*/

	public void addPassedCheckPoint(String value){
		if(getPassedCheckPoint().contains(value))return;
		getPassedCheckPoint().add(value);
		setPoint(getPassedCheckPoint().size());
		Scoreboards.setPoint(getPlayer());
	}

	public void setPoint(int value){
		this.point = value;
	}

	public void setFirstPassedCheckPoint(String value){
		this.firstpassedcheckpoint = value;
	}

	public void setLastPassedCheckPoint(String value){
		this.lastpassedcheckpoint = value;
	}

	public void setLastYaw(float value){
		this.lastyaw = value;
	}

	//スタートブロックを１チックで２度踏んでしまわないようにインターバルを作る
	public void setCool(){
		setLapStepCool(true);
		YPLKart.getInstance().getServer().getScheduler().runTaskLater(YPLKart.getInstance(), new Runnable(){
			public void run(){
				setLapStepCool(false);
			}
		}, 5L);
	}

	public void setDeathPenaltyTask(BukkitTask newtask){
		if(this.deathpenaltytask != null)
			this.deathpenaltytask.cancel();

		this.deathpenaltytask = newtask;
	}

	public void setItemPositiveSpeedTask(BukkitTask newtask){
		if(this.itemPositiveSpeedTask != null)
			this.itemPositiveSpeedTask.cancel();

		this.itemPositiveSpeedTask = newtask;
	}

	public void setItemNegativeSpeedTask(BukkitTask newtask){
		if(this.itemNegativeSpeedTask != null)
			this.itemNegativeSpeedTask.cancel();

		this.itemNegativeSpeedTask = newtask;
	}

	public void setPlayerLookingUpdateTask(BukkitTask newtask){
		if(this.playerLookingUpdateTask != null)
			this.playerLookingUpdateTask.cancel();

		this.playerLookingUpdateTask = newtask;
	}

	public void setUsingKiller(int life, Entity nearestunpassedcheckpoint){
		this.usingKiller = nearestunpassedcheckpoint;
		if(this.usingKiller != null){
			Bukkit.getScheduler().runTaskLater(YPLKart.getInstance(), new Runnable(){
				public void run(){
					usingKiller = null;
				}
			}, life * 20);
		}
	}

	public void setCMDForceLeave(boolean value){
		this.cmdForceLeave = value;
	}

	public void setStepDashBoard(){
		this.stepDashBoard = true;
		Bukkit.getScheduler().runTaskLater(YPLKart.getInstance(), new Runnable(){
			public void run(){
				stepDashBoard = false;
			}
		}, (Settings.BoostRailEffectSecond + RaceManager.getRace(getPlayer()).getCharacter().getItemAdjustPositiveEffectSecond()) * 20);
	}

	public void saveInventory(){
		PlayerInventory inv = getPlayer().getInventory();
		ArrayList<ItemStack> inventory = new ArrayList<ItemStack>();
		ArrayList<ItemStack> armorcontents = new ArrayList<ItemStack>();

		for(ItemStack slot : inv.getContents()){
			inventory.add(slot);
		}
		armorcontents.add(inv.getHelmet());
		armorcontents.add(inv.getChestplate());
		armorcontents.add(inv.getLeggings());
		armorcontents.add(inv.getBoots());

		this.inventory = inventory;
		this.armorcontents = armorcontents;
	}

	public void saveKeyItem(){
		PlayerInventory inv = getPlayer().getInventory();
		ArrayList<ItemStack> contents = new ArrayList<ItemStack>();
		ArrayList<ItemStack> armor = new ArrayList<ItemStack>();
		for(ItemStack slot : inv.getContents()){
			if(slot == null)contents.add(null);
			else
				if(EnumItem.isKeyItem(slot))
					contents.add(slot);
				else
					contents.add(null);
		}

		if(EnumItem.isKeyItem(inv.getHelmet()))armor.add(inv.getHelmet());
		else armor.add(null);

		if(EnumItem.isKeyItem(inv.getChestplate()))armor.add(inv.getChestplate());
		else armor.add(null);

		if(EnumItem.isKeyItem(inv.getLeggings()))armor.add(inv.getLeggings());
		else armor.add(null);

		if(EnumItem.isKeyItem(inv.getBoots()))armor.add(inv.getBoots());
		else armor.add(null);

		this.keyitem = contents;
		this.keyarmor = armor;
	}

	public void recoveryInventory(){
		if(!this.inventory.isEmpty()){
			PlayerInventory inv = getPlayer().getInventory();
			for(int i = 0;i < 36;i++){
				inv.setItem(i, inventory.get(i));
			}
		}

		if(!this.armorcontents.isEmpty()){
			PlayerInventory inv = getPlayer().getInventory();
			if(this.armorcontents.get(0) != null)inv.setHelmet(this.armorcontents.get(0));
			if(this.armorcontents.get(1) != null)inv.setChestplate(this.armorcontents.get(1));
			if(this.armorcontents.get(2) != null)inv.setLeggings(this.armorcontents.get(2));
			if(this.armorcontents.get(3) != null)inv.setBoots(this.armorcontents.get(3));
		}

		this.inventory = new ArrayList<ItemStack>();
		this.armorcontents = new ArrayList<ItemStack>();
	}

	public void recoveryKeyItem(){
		if(!this.inventory.isEmpty()){
			PlayerInventory inv = getPlayer().getInventory();
			for(int i = 0;i < 36;i++){
				inv.setItem(i, inventory.get(i));
			}
		}

		if(!this.armorcontents.isEmpty()){
			PlayerInventory inv = getPlayer().getInventory();
			if(this.armorcontents.get(0) != null)inv.setHelmet(this.armorcontents.get(0));
			if(this.armorcontents.get(1) != null)inv.setChestplate(this.armorcontents.get(1));
			if(this.armorcontents.get(2) != null)inv.setLeggings(this.armorcontents.get(2));
			if(this.armorcontents.get(3) != null)inv.setBoots(this.armorcontents.get(3));
		}

		this.keyitem = new ArrayList<ItemStack>();
		this.keyarmor = new ArrayList<ItemStack>();
	}
}
