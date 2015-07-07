package com.github.erozabesu.yplkart.Enum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.Data.RaceData;
import com.github.erozabesu.yplkart.Data.Settings;

public enum EnumItem{
	CheckPoint(					Material.NETHER_STAR,	(byte)0,	0,										1, 												64, "チェックポイントツール", ""),
	MarioHat(					Material.REDSTONE_BLOCK,(byte)0,	0,										1, 												64, "赤い帽子", "ひげのおじさんになれる赤い帽子。"),
	ItemBox(					Material.INK_SACK,		(byte)0,	0,										1, 												64, "アイテムボックスツール", "アイテムボックスを設置します。"),
	ItemBoxTier2(				Material.INK_SACK,		(byte)0,	0,										1, 												64, "高級アイテムボックスツール", "1段階豪華なアイテムをゲットできるアイテムボックスを設置します。"),
	ItemBoxFake(				Material.INK_SACK,		(byte)0,	0,										1, 												64, "にせアイテムボックスツール", "にせアイテムボックスを設置します。当たっても復活します。"),
	Menu(						Material.GOLD_INGOT,	(byte)0,	0,										1, 												64, "メニューを開きます", ""),

	Star(						Material.NETHER_STAR,	(byte)0,	Settings.StarTier,						1, 												Settings.StarMaxStackSize, "スーパースター","一定時間無敵になり、ぶつかったプレイヤーがひどい目に遭います。さらにスピードも速くなります。"),
	StarMultiple(				Material.NETHER_STAR,	(byte)0,	Settings.StarMultipleTier,				Settings.StarMultipleDropAmount, 				Settings.StarMaxStackSize, "スーパースター","一定時間無敵になり、ぶつかったプレイヤーがひどい目に遭います。さらにスピードも速くなります。"),
	Mushroom(					Material.INK_SACK,		(byte)1,	Settings.MushroomTier,					1, 												Settings.MushroomMaxStackSize, "ダッシュキノコ", "一定時間プレイヤーのスピードが上がります。"),
	MushroomMultiple(			Material.INK_SACK,		(byte)1,	Settings.MushroomMultipleTier,			Settings.MushroomMultipleDropAmount, 			Settings.MushroomMaxStackSize, "ダッシュキノコ", "一定時間プレイヤーのスピードが上がります。"),
	PowerfullMushroom(			Material.INK_SACK,		(byte)2,	Settings.PowerfullMushroomTier,			1, 												Settings.PowerfullMushroomMaxStackSize, "パワフルダッシュキノコ", "一定時間プレイヤーのスピードが上がります。"),
	PowerfullMushroomMultiple(	Material.INK_SACK,		(byte)2,	Settings.PowerfullMushroomMultipleTier,	Settings.PowerfullMushroomMultipleDropAmount, 	Settings.PowerfullMushroomMaxStackSize, "パワフルダッシュキノコ", "一定時間プレイヤーのスピードが上がります。"),
	Turtle(						Material.INK_SACK,		(byte)3,	Settings.TurtleTier,					1, 												Settings.TurtleMaxStackSize, "ミドリこうら", "投げると直進し、当たったプレイヤーを爆破します。"),
	TurtleMultiple(				Material.INK_SACK,		(byte)3,	Settings.TurtleMultipleTier,			Settings.TurtleMultipleDropAmount, 				Settings.TurtleMaxStackSize, "ミドリこうら", "投げると直進し、当たったプレイヤーを爆破します。"),
	RedTurtle(					Material.INK_SACK,		(byte)4,	Settings.RedTurtleTier,					1, 												Settings.RedTurtleMaxStackSize, "アカこうら", "投げると前方のプレイヤーを追いかけ、当たったプレイヤーを爆破します。"),
	RedTurtleMultiple(			Material.INK_SACK,		(byte)4,	Settings.RedTurtleMultipleTier,			Settings.RedTurtleMultipleDropAmount, 			Settings.RedTurtleMaxStackSize, "アカこうら", "投げると前方のプレイヤーを追いかけ、当たったプレイヤーを爆破します。"),
	ThornedTurtle(				Material.INK_SACK,		(byte)5,	Settings.ThornedTurtleTier,				1, 												Settings.ThornedTurtleMaxStackSize, "トゲゾーこうら", "投げると先頭のプレイヤーを追いかけ、当たったプレイヤーを爆破します。先頭のプレイヤーに追いつくまで、他のプレイヤーに当たっても進み続けます。"),
	ThornedTurtleMultiple(		Material.INK_SACK,		(byte)5,	Settings.ThornedTurtleMultipleTier,		Settings.ThornedTurtleMultipleDropAmount, 		Settings.ThornedTurtleMaxStackSize, "トゲゾーこうら", "投げると先頭のプレイヤーを追いかけ、当たったプレイヤーを爆破します。先頭のプレイヤーに追いつくまで、他のプレイヤーに当たっても進み続けます。"),
	Banana(						Material.INK_SACK,		(byte)6,	Settings.BananaTier,					1, 												Settings.BananaMaxStackSize, "バナナ", "コースに設置でき、接触したプレイヤーを減速させます。"),
	BananaMultiple(				Material.INK_SACK,		(byte)6,	Settings.BananaMultipleTier,			Settings.BananaMultipleDropAmount, 				Settings.BananaMaxStackSize, "バナナ", "コースに設置でき、接触したプレイヤーを減速させます。"),
	FakeItembox(				Material.INK_SACK,		(byte)7,	Settings.FakeItemBoxTier,				1, 												Settings.FakeItemBoxMaxStackSize, "にせアイテムボックス","アイテムボックスにそっくりですが、触れると減速します。"),
	FakeItemboxMultiple(		Material.INK_SACK,		(byte)7,	Settings.FakeItemBoxMultipleTier,		Settings.FakeItemBoxMultipleDropAmount, 		Settings.FakeItemBoxMaxStackSize, "にせアイテムボックス","アイテムボックスにそっくりですが、触れると減速します。"),
	Thunder(					Material.INK_SACK,		(byte)8,	Settings.ThunderTier,					1, 												Settings.ThunderMaxStackSize, "サンダー","ライバルのプレイヤーが一定時間遅くなります。"),
	ThunderMultiple(			Material.INK_SACK,		(byte)8,	Settings.ThunderMultipleTier,			Settings.ThunderMultipleDropAmount, 			Settings.ThunderMaxStackSize, "サンダー","ライバルのプレイヤーが一定時間遅くなります。"),
	Teresa(						Material.INK_SACK,		(byte)9,	Settings.TeresaTier,					1, 												Settings.TeresaMaxStackSize, "テレサ", "一定時間透明になり、ライバルの攻撃を受けなくなります。さらにランダムでライバルからアイテムを奪います。"),
	TeresaMultiple(				Material.INK_SACK,		(byte)9,	Settings.TeresaMultipleTier,			Settings.TeresaMultipleDropAmount, 				Settings.TeresaMaxStackSize, "テレサ", "一定時間透明になり、ライバルの攻撃を受けなくなります。さらにランダムでライバルからアイテムを奪います。"),
	Gesso(						Material.INK_SACK,		(byte)10,	Settings.GessoTier,						1, 												Settings.GessoMaxStackSize, "ゲッソー", "自分より上位のプレイヤーの画面に墨を吐いて画面を見難くします。"),
	GessoMultiple(				Material.INK_SACK,		(byte)10,	Settings.GessoMultipleTier,				Settings.GessoMultipleDropAmount, 				Settings.GessoMaxStackSize, "ゲッソー", "自分より上位のプレイヤーの画面に墨を吐いて画面を見難くします。"),
	Killer(						Material.INK_SACK,		(byte)11,	Settings.KillerTier,					1, 												Settings.KillerMaxStackSize, "キラー", ""),
	KillerMultiple(				Material.INK_SACK,		(byte)11,	Settings.KillerMultipleTier,			Settings.KillerMultipleDropAmount, 				Settings.KillerMaxStackSize, "キラー", "")
	;

	private Material material;
	private byte data;
	private int tier;
	private int amount;
	private int maxstack;
	private String name;
	private String lore;

	private EnumItem(Material material, byte data, int tier, int amount, int maxstack, String name, String lore){
		this.material = material;
		this.data = data;
		this.tier = tier;
		this.amount = amount;
		this.maxstack = maxstack;
		this.name = name;
		this.lore = lore;
	}

	public void reload(int tier, int amount, int maxstack){
		this.tier = tier;
		this.amount = amount;
		this.maxstack = maxstack;
	}

	public Material getType(){
		return this.material;
	}

	public byte getData(){
		return this.data;
	}

	public Integer getTier(){
		return this.tier;
	}

	public int getAmount(){
		return this.amount;
	}

	public int getMaxstackSize(){
		return this.maxstack;
	}

	public static int getMaxstackSize(ItemStack i){
		int maxstacksize = i.getMaxStackSize();
		for(EnumItem item : EnumItem.values()){
			if(item.isSimilar(i))
				maxstacksize = item.getMaxstackSize();
		}
		return maxstacksize;
	}

	public String getName(){
		return this.name;
	}

	public String getLore(){
		return this.lore;
	}

	public ItemStack getItem(){
		ItemStack i = new ItemStack(getType(), getAmount(), (short)0, getData());
		ItemMeta meta = i.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + getName());
		meta.setLore(Arrays.asList(getLore()));
		i.setItemMeta(meta);
		return i;
	}

	public Boolean isSimilar(ItemStack i){
		if(i == null)return false;
		if(getName().equalsIgnoreCase("チェックポイントツール")){
			if(i.getType().equals(getType()))
				if(i.getData().getData() == getData())
					if(i.getItemMeta().hasDisplayName() && i.getItemMeta().hasLore())
						if(i.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + getName()))
							if(RaceData.getCircuitSet().contains(i.getItemMeta().getLore().get(0)))
							return true;
		}else{
			if(i.getType().equals(getType()))
				if(i.getData().getData() == getData())
					if(i.getItemMeta().hasDisplayName() && i.getItemMeta().hasLore())
						if(i.getItemMeta().getLore().get(0).equalsIgnoreCase(getLore()) && i.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + getName()))
							return true;
		}
		return false;
	}

	public static ArrayList<ItemStack> getItemfromTier(int grade){
		ArrayList<ItemStack> item = new ArrayList<ItemStack>();
		for(EnumItem i : EnumItem.values()){
			if(i.getTier() != 0)
				if(i.getTier() <= grade)
					if(grade-1 <= i.getTier())
						item.add(i.getItem());
		}
		return item;
	}

	public static ItemStack getRandomItemfromTier(int grade){
		ArrayList<ItemStack> item = getItemfromTier(grade);
		return item.get(new Random().nextInt(item.size()));
	}

	public static Boolean isKeyItem(ItemStack i){
		for(EnumItem item : values()){
			if(item.isSimilar(i))
				return true;
		}
		return false;
	}

	public static void removeAllKeyItems(Player p){
		PlayerInventory inv = p.getInventory();

		for(int i = 0;i < 36;i++){
			if(isKeyItem(inv.getItem(i)))
				inv.setItem(i, null);
		}

		if(inv.getHelmet() != null)if(EnumItem.isKeyItem(inv.getHelmet()))inv.setHelmet(new ItemStack(Material.AIR));
		if(inv.getChestplate() != null)if(EnumItem.isKeyItem(inv.getChestplate()))inv.setChestplate(new ItemStack(Material.AIR));
		if(inv.getLeggings() != null)if(EnumItem.isKeyItem(inv.getLeggings()))inv.setLeggings(new ItemStack(Material.AIR));
		if(inv.getBoots() != null)if(EnumItem.isKeyItem(inv.getBoots()))inv.setBoots(new ItemStack(Material.AIR));
		p.updateInventory();
	}

	public static void removeUnuseslotKeyItems(Player p){
		PlayerInventory inv = p.getInventory();
		int i = Settings.ItemSlot;
		i += RaceManager.getRace(p).getCharacter() == null ? 0 : RaceManager.getRace(p).getCharacter().getItemAdjustMaxSlotSize();
		for(int j = i;j < 36;j++){
			if(isKeyItem(inv.getItem(j)))
				inv.setItem(j, null);
		}
		p.updateInventory();
	}

	public static void addItem(Player p, ItemStack item){
		Inventory inv = p.getInventory();
		inv.addItem(item);

		adjustInventoryfromMaxstacksize(p);
		removeUnuseslotKeyItems(p);
		p.updateInventory();
	}

	public static ItemStack getCheckPointTool(EnumItem item, String circuitname){
		ItemStack i = new ItemStack(item.getType(), item.getAmount(), (short)0, item.getData());
		ItemMeta meta = i.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + item.getName());
		meta.setLore(Arrays.asList(circuitname));
		i.setItemMeta(meta);
		return i;
	}

	private static void adjustInventoryfromMaxstacksize(Player p){
		Inventory inv = p.getInventory();
		ArrayList<ItemStack> over = new ArrayList<ItemStack>();

		ItemStack temp;
		ItemStack flow;
		int maxstacksize;

		for(int i = 0; i < 36; i++){
			if(inv.getItem(i) == null)continue;

			temp = inv.getItem(i);
			maxstacksize = EnumItem.getMaxstackSize(temp);
			maxstacksize += RaceManager.getRace(p).getCharacter() == null ? 0 : RaceManager.getRace(p).getCharacter().getItemAdjustMaxStackSize();

			if(maxstacksize < temp.getAmount()){
				flow = temp.clone();
				flow.setAmount(flow.getAmount() - maxstacksize);
				temp.setAmount(maxstacksize);
			}else
				continue;

			while(maxstacksize < flow.getAmount()){
				int flowsize = flow.getAmount() - maxstacksize;
				flow.setAmount(maxstacksize);
				over.add(flow.clone());
				flow.setAmount(flowsize);
			}
			over.add(flow.clone());
		}

		if(over.isEmpty())return;

		int overindex = 0;
		for(int j = 0; j < 36; j++){
			try{over.get(overindex);}
			catch(IndexOutOfBoundsException ex){break;}

			if(inv.getItem(j) != null){
				if(inv.getItem(j).isSimilar(over.get(overindex))){
					int tempmaxstacksize = EnumItem.getMaxstackSize(inv.getItem(j));
					tempmaxstacksize += RaceManager.getRace(p).getCharacter() == null ? 0 : RaceManager.getRace(p).getCharacter().getItemAdjustMaxStackSize();
					if(inv.getItem(j).getAmount() < tempmaxstacksize){
						int tempflow = (inv.getItem(j).getAmount() + over.get(overindex).getAmount()) - tempmaxstacksize;
						if(tempflow < 0){
							inv.getItem(j).setAmount(tempmaxstacksize);
							overindex++;
						}else{
							inv.getItem(j).setAmount(tempmaxstacksize);
							over.get(overindex).setAmount(tempflow);
						}
						continue;
					}
				}
			}else{
				inv.setItem(j, over.get(overindex));
				overindex++;
			}
		}
	}
}
