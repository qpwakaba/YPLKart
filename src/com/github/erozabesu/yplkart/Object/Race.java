package com.github.erozabesu.yplkart.Object;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.Scoreboards;
import com.github.erozabesu.yplkart.YPLKart;
import com.github.erozabesu.yplkart.Data.Message;
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
	private double maxhealth;
	private double health;
	private int hunger;
	private float walkspeed;
	private int level;
	private float exp;
	private ArrayList<ItemStack> inventory;
	private ArrayList<ItemStack> armorcontents;

	private Location quitposition;
	private double quitmaxhealth;
	private double quithealth;
	private int quithunger;
	private float quitwalkspeed;
	private int quitlevel;
	private float quitexp;
	private ArrayList<ItemStack> quitinventory;
	private ArrayList<ItemStack> quitarmorcontents;

	private EnumCharacter character;
	private EnumKarts kart;

	private String entry;
	private boolean standby;
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

	private ArrayList<ItemStack> keyitem;
	private ArrayList<ItemStack> keyarmor;

	private BukkitTask deathpenaltytask;
	private BukkitTask deathpenaltytitlesendtask;
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
		Player p = getPlayer();
		if(p != null){
			this.goalposition = getPlayer().getLocation().add(0,1,0);
			this.maxhealth = p.getMaxHealth();
			this.health = p.getHealth();
			this.hunger = p.getFoodLevel();
			this.walkspeed = p.getWalkSpeed();
			this.level = p.getLevel();
			this.exp = p.getExp();
		}
		this.inventory = new ArrayList<ItemStack>();
		this.armorcontents = new ArrayList<ItemStack>();

		this.quitposition = this.goalposition;
		this.quitmaxhealth = this.maxhealth;
		this.quithealth = this.health;
		this.quithunger = this.hunger;
		this.quitwalkspeed = this.walkspeed;
		this.quitlevel = this.level;
		this.quitexp = this.exp;
		this.quitinventory = new ArrayList<ItemStack>();
		this.quitarmorcontents = new ArrayList<ItemStack>();

		this.entry = "";
		this.standby = false;
		this.start = false;
		this.goal = false;

		this.laststepblock = null;
		this.lapcount = 0;
		this.lapstepcool = false;

		this.lastyaw = 0;

		this.lastpassedcheckpoint = "";
		this.point = 0;
		this.firstpassedcheckpoint = "";
		this.passedcheckpoint = new ArrayList<String>();

		this.deathpenaltytask = null;
		this.deathpenaltytitlesendtask = null;
		this.itemPositiveSpeedTask = null;
		this.itemNegativeSpeedTask = null;
		this.usingKiller = null;

		this.cmdForceLeave = false;

		this.stepDashBoard = false;
	}

	//〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓
	public Player getPlayer(){
		return Bukkit.getPlayer(this.id);
	}

	public Location getGoalPosition(){
		return this.goalposition;
	}

	public Location getGoalPositionOnQuit(){
		return this.quitposition;
	}

	public String getEntry(){
		return this.entry;
	}

	public boolean getStandBy(){
		return this.standby;
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
		if(getPlayer() == null)return null;

		for(Entity e : getPlayer().getWorld().getEntities()){
			if(e.getUniqueId().toString().equalsIgnoreCase(this.lastpassedcheckpoint))
				return e;
		}
		return null;
	}

	public BukkitTask getDeathPenaltyTask(){
		return this.deathpenaltytask;
	}

	public BukkitTask getDeathPenaltySendTitleTask(){
		return this.deathpenaltytitlesendtask;
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

		new SendExpandedTitleTask(getPlayer(), 5, "GOAL!!!" + ChatColor.GOLD, "O", 1, false).runTaskTimer(YPLKart.getInstance(), 0, 1);
		String message = Message.titleGoalRank.getMessage(new Object[]{new Number[]{RaceManager.getGoalPlayer(entry).size(), (double)(currentmillisecond/1000)}});
		PacketUtil.sendTitle(getPlayer(), message, 10, 100, 10, true);
		setPoint(getPassedCheckPoint().size() + (RaceManager.getRacingPlayer(entry).size())*10);

		Circuit c = RaceManager.getCircuit(entry);
		if(RaceData.getBroadcastGoalMessage(entry)){
			for(Player p : Bukkit.getOnlinePlayers()){
				Message.raceGoal.sendMessage(p, new Object[]{getPlayer(), c, new Number[]{RaceManager.getGoalPlayer(entry).size(), (double)(currentmillisecond/1000)}});
			}
		}else{
			c.sendMessageEntryPlayer(Message.raceGoal, new Object[]{getPlayer(), c, new Number[]{RaceManager.getGoalPlayer(entry).size(), (double)(currentmillisecond/1000)}});
		}

		if(getKart() == null)
			RaceData.addRunningRaceLapTime(getPlayer(), entry, currentmillisecond/1000);
		else
			RaceData.addKartRaceLapTime(getPlayer(), entry, currentmillisecond/1000);

		//終了処理 順序に注意?
		setStart(false);
		RaceManager.clearCharacterRaceData(this.id);
		RaceManager.clearKartRaceData(this.id);
		RaceManager.leaveRacingKart(getPlayer());
		EnumItem.removeAllKeyItems(getPlayer());
		recoveryInventory();
		recoveryExp();
		getPlayer().teleport(goalposition);
	}

	public void setStandBy(boolean value) {
		this.standby = value;
	}

	public void setStart(boolean value){
		this.start = value;
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
		Scoreboards.setPoint(this.id);
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

	public void setDeathPenaltyTitleSendTask(BukkitTask newtask){
		if(this.deathpenaltytitlesendtask != null)
			this.deathpenaltytitlesendtask.cancel();

		this.deathpenaltytitlesendtask = newtask;
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

	//〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

	public void savePlayerData(){
		if(getPlayer() == null)return;
		Player p = getPlayer();

		this.goalposition = p.getLocation().add(0,1,0);
		this.maxhealth = p.getMaxHealth();
		this.health = p.getHealth();
		this.hunger = p.getFoodLevel();
		this.walkspeed = p.getWalkSpeed();
		this.level = p.getLevel();
		this.exp = p.getExp();

		saveInventory();
	}

	public void savePlayerDataOnQuit(){
		if(getPlayer() == null)return;
		Player p = getPlayer();

		this.quitposition = p.getLocation().add(0,1,0);
		this.quitmaxhealth = p.getMaxHealth();
		this.quithealth = p.getHealth();
		this.quithunger = p.getFoodLevel();
		this.quitwalkspeed = p.getWalkSpeed();
		this.quitlevel = p.getLevel();
		this.quitexp = p.getExp();
	}

	public void saveInventory(){
		if(getPlayer() == null)return;
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

	public void saveInventoryOnQuit(){
		if(getPlayer() == null)return;
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

		this.quitinventory = inventory;
		this.quitarmorcontents = armorcontents;
	}

	public void saveKeyItem(){
		if(getPlayer() == null)return;
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

	/*
	 * 既にカート搭乗中に再度別のカートに乗る場合はパラメータの再設定と見た目・名前の更新のみ行う。
	 * サーバーがリロードされていた場合NoSuchMethodExceptionが出力されるため、
	 * その場合は古いカートを撤去し新しいカートに搭乗させる。
	 */
	public void recoveryKart(){
		if(getPlayer() == null)return;
		if(getKart() == null)return;

		final Player p = getPlayer();
		if(p.getVehicle() != null){
			if(RaceManager.isRacingKart(p.getVehicle())){
				Object customkart = p.getVehicle().getMetadata(YPLKart.plname).get(0).value();
				try {
					Minecart cart = (Minecart) customkart.getClass().getMethod("getBukkitEntity").invoke(customkart);
					cart.setDisplayBlock(new MaterialData(kart.getDisplayBlock(), kart.getDisplayData()));
					cart.setCustomName(kart.getName());
					cart.setCustomNameVisible(false);

					customkart.getClass().getMethod("setParameter", EnumKarts.class).invoke(customkart, kart);
					return;
				}catch (NoSuchMethodException e) {
					RaceManager.leaveRacingKart(p);
				}catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}else{
				p.leaveVehicle();
			}
		}
		Minecart cart = RaceManager.createCustomMinecart(p.getLocation(), kart);
		cart.setPassenger(p);
		PacketUtil.sendOwnAttachEntityPacket(p);
	}

	private void recoveryExp(){
		Player p = getPlayer();
		if(p == null)return;

		p.setLevel(this.level);
		p.setExp(this.exp);
	}

	private void recoveryExpOnQuit(){
		Player p = getPlayer();
		if(p == null)return;

		p.setLevel(this.quitlevel);
		p.setExp(this.quitexp);
	}

	public void recoveryPhysical(){
		final Player p = getPlayer();
		if(p == null)return;

		p.setMaxHealth(this.maxhealth);
		p.setFoodLevel(this.hunger);
		p.setWalkSpeed(this.walkspeed);
		Bukkit.getScheduler().runTaskLater(YPLKart.getInstance(), new Runnable(){
			public void run(){
				if(getPlayer() != null)
					p.setHealth(health);
			}
		}, 5L);
	}

	public void recoveryPhysicalOnQuit(){
		final Player p = getPlayer();
		if(p == null)return;

		p.setMaxHealth(this.quitmaxhealth);
		p.setFoodLevel(this.quithunger);
		p.setWalkSpeed(this.quitwalkspeed);
		Bukkit.getScheduler().runTaskLater(YPLKart.getInstance(), new Runnable(){
			public void run(){
				if(getPlayer() != null)
					p.setHealth(quithealth);
			}
		}, 5L);
	}

	public void recoveryCharacterPhysical(){
		final Player p = getPlayer();
		if(p == null)return;

		p.setMaxHealth(this.character.getMaxHealth());
		p.setWalkSpeed(this.character.getWalkSpeed());
		Bukkit.getScheduler().runTaskLater(YPLKart.getInstance(), new Runnable(){
			public void run(){
				if(getPlayer() != null)
					p.setHealth(character.getMaxHealth());
			}
		}, 5L);
	}

	public void recoveryInventory(){
		if(getPlayer() == null)return;
		recoveryExp();
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

	public void recoveryInventoryOnQuit(){
		if(getPlayer() == null)return;
		recoveryExpOnQuit();
		if(!this.quitinventory.isEmpty()){
			PlayerInventory inv = getPlayer().getInventory();
			for(int i = 0;i < 36;i++){
				inv.setItem(i, quitinventory.get(i));
			}
		}

		if(!this.quitarmorcontents.isEmpty()){
			PlayerInventory inv = getPlayer().getInventory();
			if(this.quitarmorcontents.get(0) != null)inv.setHelmet(this.quitarmorcontents.get(0));
			if(this.quitarmorcontents.get(1) != null)inv.setChestplate(this.quitarmorcontents.get(1));
			if(this.quitarmorcontents.get(2) != null)inv.setLeggings(this.quitarmorcontents.get(2));
			if(this.quitarmorcontents.get(3) != null)inv.setBoots(this.quitarmorcontents.get(3));
		}

		this.quitinventory = new ArrayList<ItemStack>();
		this.quitarmorcontents = new ArrayList<ItemStack>();
	}

	public void recoveryKeyItem(){
		if(getPlayer() == null)return;
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
