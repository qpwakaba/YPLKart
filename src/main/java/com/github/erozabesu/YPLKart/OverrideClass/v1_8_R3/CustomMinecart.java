package main.java.com.github.erozabesu.YPLKart.OverrideClass.v1_8_R3;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;

import main.java.com.github.erozabesu.YPLKart.Data.DisplayKartData;
import main.java.com.github.erozabesu.YPLKart.Data.Settings;
import main.java.com.github.erozabesu.YPLKart.Enum.EnumKarts;
import main.java.com.github.erozabesu.YPLKart.Enum.Permission;
import main.java.com.github.erozabesu.YPLKart.Object.Race;
import main.java.com.github.erozabesu.YPLKart.Object.RaceManager;
import main.java.com.github.erozabesu.YPLKart.Utils.Particle;
import main.java.com.github.erozabesu.YPLKart.Utils.Util;
import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityMinecartAbstract;
import net.minecraft.server.v1_8_R3.EntityMinecartRideable;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.World;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

public class CustomMinecart extends EntityMinecartRideable{
	private boolean a;
	private String b;
	private static final int[][][] matrix = { { { 0, 0, -1 }, { 0, 0, 1 } }, { { -1 }, { 1 } }, { { -1, -1 }, { 1 } }, { { -1 }, { 1, -1 } }, { { 0, 0, -1 }, { 0, -1, 1 } }, { { 0, -1, -1 }, { 0, 0, 1 } }, { { 0, 0, 1 }, { 1 } }, { { 0, 0, 1 }, { -1 } }, { { 0, 0, -1 }, { -1 } }, { { 0, 0, -1 }, { 1 } } };
	private int d;
	private double e;
	private double f;
	private double g;
	private double h;
	private double i;
	public boolean slowWhenEmpty = true;
	private double derailedX = 0.85D;//ベクトルの減衰率、慣性
	private double derailedY = 0.85D;
	private double derailedZ = 0.85D;
	private double flyingX = 0.95D;
	private double flyingY = 0.95D;
	private double flyingZ = 0.95D;
	public double maxSpeed = 0.4D;//レール上のみ使用

	public double speedStack = 0;
	public double lastMotionSpeed;

	private EnumKarts kart;
	private boolean display;
	private double killerX = 0;
	private double killerY = 0;
	private double killerZ = 0;

	public CustomMinecart(World w, EnumKarts kart, float yaw){
		super(w);
		this.kart = kart;
		this.display = false;
//TODO CraftBukkit
		this.S = getClimbableHeight();
		this.yaw = yaw;
		setYawPitch(yaw + 90F, 0F);
	}

	public CustomMinecart(World w, EnumKarts kart, Location l){
		super(w);
		this.kart = kart;
		this.display = true;
		this.yaw = l.getYaw();
		this.pitch = -l.getPitch();
		setYawPitch(yaw + 90F, this.pitch);

	}

	public EnumKarts getKart(){
		return this.kart;
	}

	public double getMaxSpeedStack(){
		if(EnumKarts.getKartfromString(this.kart.getName()) == null)return 0;
		return EnumKarts.getKartfromString(this.kart.getName()).getMaxSpeed();
	}

	public double getAcceleration(){
		if(EnumKarts.getKartfromString(this.kart.getName()) == null)return 0;
		return EnumKarts.getKartfromString(this.kart.getName()).getAcceleration();
	}

	public double getSpeedOnDirt(){
		if(EnumKarts.getKartfromString(this.kart.getName()) == null)return 1;
		return EnumKarts.getKartfromString(this.kart.getName()).getSpeedOnDirt();
	}

	public float getClimbableHeight(){
		if(EnumKarts.getKartfromString(this.kart.getName()) == null)return 0;
		return EnumKarts.getKartfromString(this.kart.getName()).getClimbableHeight();
	}

	public float getDefaultCorneringPower(){
		if(EnumKarts.getKartfromString(this.kart.getName()) == null)return 0;
		return EnumKarts.getKartfromString(this.kart.getName()).getDefaultCorneringPower();
	}

	public float getDriftCorneringPower(){
		if(EnumKarts.getKartfromString(this.kart.getName()) == null)return 0;
		return EnumKarts.getKartfromString(this.kart.getName()).getDriftCorneringPower();
	}

	public double getDriftSpeedDecrease(){
		if(EnumKarts.getKartfromString(this.kart.getName()) == null)return 0;
		return EnumKarts.getKartfromString(this.kart.getName()).getDriftSpeedDecrease();
	}

	//マインカートが搭乗可能な状態だった場合搭乗させる
	@Override
	public boolean e(EntityHuman human)
	{
		if(this.display)
			return false;
		if ((this.passenger != null) && ((this.passenger instanceof EntityHuman)) && (this.passenger != human)) {
			return true;
		}
		if ((this.passenger != null) && (this.passenger != human)) {
			return false;
		}
		if (!this.world.isClientSide) {
			human.mount(this);
		}

		return true;
	}

	@Override
	public void K(){
	}

	//LivingUpdate
	@Override
	public void t_()
	{
		if (this.world.isClientSide) {
			this.O();
			return;
		}

		double prevX = this.locX;
		double prevY = this.locY;
		double prevZ = this.locZ;
		float prevYaw = this.yaw;
		float prevPitch = this.pitch;

		if (getType() > 0) {
//TODO CraftBukkit
			j(getType() - 1);
		}

		if (getDamage() > 0.0F) {
			setDamage(getDamage() - 1.0F);
		}

		//ヴォイドに落下
		if (this.locY < -64.0D) {
//TODO CraftBukkit
			O();//Entity.O(){die();}
		}

		this.lastX = this.locX;
		this.lastY = this.locY;
		this.lastZ = this.locZ;

		if(this.display){
			this.motX = 0;
			this.motY = 0;
			this.motZ = 0;
			setYawPitch(this.yaw, this.pitch);
			return;
		}

		this.motY -= 0.03999999910593033D;
//TODO CraftBukkit
		n();
//TODO CraftBukkit
		checkBlockCollisions();


		//start:コリジョン
			Iterator iterator = this.world.getEntities(this, getBoundingBox().grow(0.2000000029802322D, 0.0D, 0.2000000029802322D)).iterator();
			while (iterator.hasNext()) {
				Entity entity = (Entity)iterator.next();
//TODO CraftBukkit
				if ((entity != this.passenger) && (entity.ae()) && ((entity instanceof EntityMinecartAbstract))) {
					entity.collide(this);
				}
			}
		//end:コリジョン

		//start:騎乗者死亡時に変数初期化
			if ((this.passenger != null) && (this.passenger.dead)) {
				if (this.passenger.vehicle == this) {
					this.passenger.vehicle = null;
				}

				this.passenger = null;
			}
		//end:騎乗者死亡時に変数初期化

			if (this.passenger != null) {
				if (this.passenger.vehicle == this) {
					((Player)this.passenger.getBukkitEntity()).playSound(this.passenger.getBukkitEntity().getLocation(), Sound.COW_WALK, 1.0F, 0.05F+((float)this.speedStack/200));
					((Player)this.passenger.getBukkitEntity()).playSound(this.passenger.getBukkitEntity().getLocation(), Sound.GHAST_FIREBALL, 0.01F+((float)this.speedStack/400), 1.0F);
					((Player)this.passenger.getBukkitEntity()).playSound(this.passenger.getBukkitEntity().getLocation(), Sound.FIZZ, 0.01F+((float)this.speedStack/400), 0.5F);
				}
			}

			W();
	}

	@Override
	protected void n() {
		if(this.passenger != null)
			if(this.passenger instanceof EntityHuman){
				setMotion((EntityHuman) this.passenger);
			}else{
//TODO CraftBukkit
				double d0 = m();

				this.motX = MathHelper.a(this.motX, -d0, d0);
				this.motZ = MathHelper.a(this.motZ, -d0, d0);
			}

		if (this.onGround)
		{
			this.motX *= this.derailedX;
			this.motY *= this.derailedY;
			this.motZ *= this.derailedZ;
		}

		move(this.motX, this.motY, this.motZ);
		if (!this.onGround)
		{
			this.motX *= this.flyingX;
			this.motY *= this.flyingY;
			this.motZ *= this.flyingZ;
		}
	}

	public void setMotion(EntityHuman human){
		this.lastMotionSpeed = calcMotionSpeed(this.motX, this.motZ) * this.kart.getWeight();

		if(RaceManager.getRace((Player)human.getBukkitEntity()).getUsingKiller()){
			this.speedStack = getMaxSpeedStack();
			Player p = (Player)human.getBukkitEntity();
//TODO CraftBukkit
			//this.noclip = true;
			this.S = 5;
			Race r = RaceManager.getRace(p);

			p.playSound(p.getLocation(), Sound.GHAST_FIREBALL, 0.13F, 1.5F);
			p.playSound(p.getLocation(), Sound.FIZZ, 0.2F, 1.0F);

			for(org.bukkit.entity.Entity e : p.getNearbyEntities(2.5, 2, 2.5)){
				if(e.getUniqueId() != p.getUniqueId()){
					if(e instanceof Player){
						if(RaceManager.isEntry((Player)e));
							Util.createSafeExplosion(p, e.getLocation(), Settings.KillerMovingDamage + RaceManager.getRace(p).getCharacter().getItemAdjustAttackDamage(), 2);
					}
				}
			}

			if(this.killerX != 0 && this.killerZ != 0){
				this.motX = this.killerX;
				//this.motY = this.killerY;
				//this.motY = 0.04;
				this.motZ = this.killerZ;
			}
			setYawPitch(Util.getYawfromVector(new Vector(this.motX, this.motY, this.motZ))+180,0);
			Location current = this.bukkitEntity.getLocation().add(0,0.5,0);
			current.setYaw(current.getYaw() + 270);
			Location ll = Util.getLocationfromYaw(current, -15);

			for(int i = 0;i < 10;i++){
				Particle.sendToLocation("REDSTONE", ll.add(((double)Util.getRandom(10))/10, ((double)Util.getRandom(10))/10, ((double)Util.getRandom(10))/10), 0, 0, 0, 0, 10);
			}

			ArrayList<org.bukkit.entity.Entity> checkpoint = new ArrayList<org.bukkit.entity.Entity>();
			String lap = r.getLapCount() <= 0 ? "" : String.valueOf(r.getLapCount());
			ArrayList<org.bukkit.entity.Entity> templist = RaceManager.getNearbyCheckpoint(p.getLocation(), 30, r.getEntry());
			if(templist == null)return;

			for (org.bukkit.entity.Entity e : templist) {
				if(!r.getPassedCheckPoint().contains(lap + e.getUniqueId().toString())){
					checkpoint.add(e);
				}
			}

			if(checkpoint.isEmpty()){
				return;
			}

			org.bukkit.entity.Entity e = Util.getNearestEntity(checkpoint, this.getBukkitEntity().getLocation());
			Vector v = Util.getVectorLocationToLocation(e.getLocation().add(0,-RaceManager.checkPointHeight+1, 0), this.getBukkitEntity().getLocation()).multiply(1.8);
			this.killerX = v.getX();
			//this.killerY = v.getY();
			this.killerZ = v.getZ();
		}else{
//TODO CraftBukkit
			this.S = getClimbableHeight();
			float sideMotion = human.aZ * 0.0F;//横方向への移動速度(+-0.98固定)
			float forwardMotion = human.ba;//縦方向への移動速度(+-3.92固定)

			this.killerX = 0;
			this.killerZ = 0;
//TODO CraftBukkit
			this.noclip = false;
			calcSpeedStack(human);

			if(0 < forwardMotion){
				forwardMotion *= 0.1;
				forwardMotion += this.speedStack/400;
			}else if(forwardMotion < 0){
				if(isDirtBlock())
					forwardMotion *= getSpeedOnDirt() * 0.1;
				else
					forwardMotion *= 0.1;
			}

			//コーナリング性能
			if(Permission.hasPermission((Player)human.getBukkitEntity(), Permission.kart_drift, true)){
//TODO CraftBukkit
				this.yaw = human.isSneaking() ? this.yaw - human.aZ * getDriftCorneringPower() : this.yaw - human.aZ * getDefaultCorneringPower();
				if(human.isSneaking()){
					if(100 < this.speedStack){
						Location current = this.bukkitEntity.getLocation();
						current.setYaw(current.getYaw() + 270);
						Location ll = Util.getLocationfromYaw(current, -this.speedStack/60);

						Particle.sendToLocation("LAVA", ll, 0, 0, 0, 0, 5);
					}
				}
			}else{
//TODO CraftBukkit
				this.yaw = this.yaw - human.aZ * getDefaultCorneringPower();
			}

			Location current = this.bukkitEntity.getLocation().add(0,0.5,0);
			current.setYaw(current.getYaw() + 270);
			Location ll = Util.getLocationfromYaw(current, -1.0 - this.speedStack/30);

			Particle.sendToLocation("SPELL", ll.add(((double)Util.getRandom(4))/10, 0.5, ((double)Util.getRandom(4))/10), 0, 0, 0, 0, 5);

			setYawPitch(this.yaw, /*(float) (-this.speedStack/10)*/0);
//TODO CraftBukkit
			calcMotion(forwardMotion, sideMotion, /*f3*/human.bI()/2.0F);
		}
		//はしご、つたのようなよじ登れるブロックに立っている場合
		if (isClambableBlock()) {
				float f4 = 0.15F;
				this.motX = MathHelper.a(this.motX, -f4, f4);
				this.motZ = MathHelper.a(this.motZ, -f4, f4);
				this.fallDistance = 0.0F;
				if (this.motY < -0.15D) {
					this.motY = -0.15D;
				}
		}

		move(this.motX, this.motY, this.motZ);
		if ((this.positionChanged) && (isClambableBlock())) {
			this.motY = 0.2D+this.speedStack/300;
		}

//TODO CraftBukkit
		if ((this.world.isClientSide) && ((!this.world.isLoaded(new BlockPosition((int)this.locX, 0, (int)this.locZ))) || (!this.world.getChunkAtWorldCoords(new BlockPosition((int)this.locX, 0, (int)this.locZ)).o()))) {
			if (this.locY > 0.0D)
			this.motY = -0.1D;
			else
			this.motY = 0.0D;
		}
		else {
			this.motY -= 0.08D;
		}

		this.motY *= 0.9800000190734863D;
		//this.motX *= groundFriction;
		//this.motZ *= groundFriction;

		//スピードメーター
		if(!RaceManager.getRace((Player)human.getBukkitEntity()).getUsingKiller())
			((Player)human.getBukkitEntity()).setLevel(calcMotionSpeed(this.motX, this.motZ));
	}

	public void calcSpeedStack(EntityHuman human){
		Player p = (Player)human.getBukkitEntity();

		if(RaceManager.getRace(p).getStepDashBoard()){
			this.speedStack = getMaxSpeedStack() + 100 * Settings.BoostRailEffectLevel + RaceManager.getRace(p).getCharacter().getItemAdjustPositiveEffectLevel() * 50;
			return;
		}

		for(PotionEffect potion : p.getActivePotionEffects()){
			if(potion.getType().getName().equalsIgnoreCase("SPEED")){
				this.speedStack = getMaxSpeedStack() + potion.getAmplifier() * 15;
				return;
			}else if(potion.getType().getName().equalsIgnoreCase("SLOW")){
				if(this.speedStack < potion.getAmplifier())
					this.speedStack = 0;
				else
					this.speedStack -= potion.getAmplifier();
				return;
			}
		}
//TODO CraftBukkit
		if(0 < human.ba){//forward
			if(!isDirtBlock()){
				if(this.speedStack < getMaxSpeedStack()){
					this.speedStack += getAcceleration();
				}else{
					//キノコ等で急加速した場合一時的にmaxSpeedStackを超えるため
					this.speedStack = getMaxSpeedStack();
				}
			}else{
				this.speedStack -= RaceManager.getRace(p).getKart().getSpeedOnDirt();
			}

			if(human.isSneaking()){
				this.speedStack -= getDriftSpeedDecrease();
				((Player)human.getBukkitEntity()).playSound(human.getBukkitEntity().getLocation(), Sound.FIREWORK_BLAST, 1.0F, 7.0F);
			}
//TODO CraftBukkit
		}else if(0 == human.ba)//stop
			if(0 < this.speedStack)
				this.speedStack -= 4;
//TODO CraftBukkit
		if(human.ba < 0)//backward
			if(0 < this.speedStack){
				this.speedStack -= 10;
			}

		if(this.speedStack < 0)this.speedStack = 0;
		BigDecimal bd = new BigDecimal((this.motX * this.motX + this.motZ * this.motZ));
		double mot = bd.setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue();
		if(mot == 0)this.speedStack = 0;
	}

	public int calcMotionSpeed(double x, double z){
		BigDecimal bd = new BigDecimal((x * x + z * z));
		return (int)(bd.doubleValue()*564);
	}

	public void calcMotion(float forwardMotion, float sideMotion, float Friction) {
		float f3 = forwardMotion * forwardMotion + sideMotion * sideMotion;

		if (f3 >= 1.0E-004F) {
			f3 = (float) Math.sqrt(f3);
			if (f3 < 1.0F) {
				f3 = 1.0F;
			}

			f3 = Friction / f3;
			forwardMotion *= f3;
			sideMotion *= f3;

			float f4 = MathHelper.sin(this.yaw * 3.141593F / 180.0F);
			float f5 = MathHelper.cos(this.yaw * 3.141593F / 180.0F);

			this.motX += (forwardMotion * f5 - sideMotion * f4);
			this.motZ += (sideMotion * f5 + forwardMotion * f4);
		}
	}

	public boolean isClambableBlock(){
		int i = MathHelper.floor(this.locX);
//TODO CraftBukkit
		int j = MathHelper.floor(getBoundingBox().b);
		int k = MathHelper.floor(this.locZ);
		Block block = this.world.getType(new BlockPosition(i, j, k)).getBlock();

		return ((block == Blocks.LADDER) || (block == Blocks.VINE));
	}

	public boolean isDirtBlock() {
		int i = MathHelper.floor(this.locX);
//TODO CraftBukkit
		int j = MathHelper.floor(getBoundingBox().b);
		int k = MathHelper.floor(this.locZ);

		Location l = new Location(this.getBukkitEntity().getWorld() ,i, j, k);

		return (Settings.DirtBlock.equalsIgnoreCase(Util.getStepBlock(l)));
	}

	public boolean damageEntity(DamageSource damagesource, float f) {
//TODO CraftBukkit
		if ((!this.world.isClientSide) && (!this.dead)) {
			if(!(damagesource.getEntity() instanceof EntityHuman))return false;

			Player p = (Player) damagesource.getEntity().getBukkitEntity();
			if(!Permission.hasPermission(p, Permission.op_kart_remove, false))return false;

			if (this.passenger != null)
				this.passenger.mount(null);

			if(this.display){
				DisplayKartData.deleteData(this.getCustomName());
				Util.sendMessage(p, "ディスプレイ専用カートを削除しました");
			}

			die();
		}
		return true;
	}

	//エンティティ同士の衝突
	//衝突相手を吹き飛ばす代償として自身のスピードは相殺される
	//スピード50の相手にスピード200で衝突した場合
	//相手はスピード200-50=150の速度で吹き飛び、自身のスピードは200-150=50に減衰する
	//ここで言うスピードとは移動速度ではなく、移動速度に自身の重さを掛け合わせたもの
	//スピードの定義 : calcMotionSpeed() * Karts.getWeight()
	@Override
	public void collide(Entity entity)
	{
//TODO CraftBukkit
		if ((!this.world.isClientSide) &&
			(!entity.noclip) && (!this.noclip) &&
			(entity != this.passenger))
		{
			Entity other = entity.vehicle == null ? entity : getVehicle(entity);

			double otherspeed = RaceManager.isCustomMinecart(other.getBukkitEntity()) ? calcMotionSpeed(other.motX, other.motZ) * EnumKarts.getKartfromEntity(other.getBukkitEntity()).getWeight() : calcMotionSpeed(other.motX, other.motZ);

			if(this.lastMotionSpeed < otherspeed)return;

			double collisionpower = this.lastMotionSpeed - otherspeed;
			if(1 < (int)(collisionpower*0.04*2)){
				Vector v = Util.getVectorLocationToLocation(other.getBukkitEntity().getLocation(), this.getBukkitEntity().getLocation()).setY(0);

				this.speedStack = this.speedStack - collisionpower < 0 ? 0 : this.speedStack - collisionpower;
				other.getBukkitEntity().setVelocity(other.getBukkitEntity().getVelocity().add(v.multiply(collisionpower*0.01)));

				org.bukkit.entity.Entity damager = getPassenger(this).getBukkitEntity();
				Util.addDamage(other.getBukkitEntity(), damager,(int)(collisionpower*0.04*2));
				for(org.bukkit.entity.Entity damaged : Util.getAllPassenger(other.getBukkitEntity())){
					Util.addDamage(damaged, damager,(int)(collisionpower*0.04*2));
				}
				for(org.bukkit.entity.Entity damaged : Util.getAllVehicle(other.getBukkitEntity())){
					Util.addDamage(damaged, damager,(int)(collisionpower*0.04*2));
				}
			}
		}
	}

	//搭乗している一番下のエンティティを返す
	public Entity getVehicle(Entity entity){
		if(entity.vehicle == null)return null;

		Entity vehicle = entity.vehicle;
		if(vehicle != null){
			while(vehicle.vehicle != null){
				vehicle = vehicle.vehicle;
			}
		}

		return vehicle;
	}

	//搭乗している一番上のエンティティを返す
	public Entity getPassenger(Entity entity){
		if(entity.passenger == null)return null;

		Entity passenger = entity.passenger;
		if(passenger != null){
			while(passenger.passenger != null){
				passenger = passenger.passenger;
			}
		}

		return passenger;
	}
}
