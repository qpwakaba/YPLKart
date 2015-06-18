package main.java.com.github.erozabesu.YPLKart.Data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import main.java.com.github.erozabesu.YPLKart.YPLKart;
import main.java.com.github.erozabesu.YPLKart.Enum.EnumCharacter;
import main.java.com.github.erozabesu.YPLKart.Enum.EnumItem;
import main.java.com.github.erozabesu.YPLKart.Enum.EnumKarts;
import main.java.com.github.erozabesu.YPLKart.Enum.Permission;
import main.java.com.github.erozabesu.YPLKart.Utils.Util;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public final class Settings{
	public static YPLKart pl;

	private static String filename = "config.yml";
	private static File datafolder;
	private static File configFile;
	private static FileConfiguration config;

	private static boolean EnableThisPlugin = true;
	public static boolean EnableScoreboard = true;
	public static List<String> DisWorlds;

	public static boolean EnablePermissionKartRide = true;
	public static boolean EnablePermissionKartDrift = true;
	public static boolean EnablePermissionCMDMenu = true;
	public static boolean EnablePermissionCMDEntry = true;
	public static boolean EnablePermissionCMDExit = true;
	public static boolean EnablePermissionCMDCharacter = true;
	//public static boolean EnablePermissionCMDCharacterReset = true;
	public static boolean EnablePermissionCMDRide = true;
	public static boolean EnablePermissionCMDLeave = true;
	public static boolean EnablePermissionCMDRanking = true;
	public static boolean EnablePermissionCMDItem = true;

	public static boolean EnablePermissionCMDOtherMenu = true;
	public static boolean EnablePermissionCMDOtherEntry = true;
	public static boolean EnablePermissionCMDOtherExit = true;
	public static boolean EnablePermissionCMDOtherCharacter = true;
	//public static boolean EnablePermissionCMDOtherCharacterReset = true;
	public static boolean EnablePermissionCMDOtherRide = true;
	public static boolean EnablePermissionCMDOtherLeave = true;
	public static boolean EnablePermissionCMDOtherItem = true;
	public static boolean EnablePermissionCMDOtherRanking = true;
	public static boolean EnablePermissionUseItem = true;

	public static boolean EnablePermissionInteractObject = true;

	public static boolean EnableOPPermissionKartRemove = true;

	public static boolean EnableOPPermissionCMDCircuit = true;
	public static boolean EnableOPPermissionCMDDisplay = true;
	public static boolean EnableOPPermissionCMDReload = true;
	//public static boolean EnableOPPermissionCMDCheckPointTool = true;
	public static boolean EnableOPPermissionCMDItemBoxTool = true;

	public static int NumberOfLaps = 3;
	public static String StartBlock = "41:0";
	public static String GoalBlock = "42:0";
	public static String DirtBlock = "12:0";
	public static int ItemSlot = 2;

	public static int BoostRailEffectLevel = 5;
	public static int BoostRailEffectSecond = 3;

	public static int Tier1 = 30;
	public static int Tier2 = 60;
	public static int Tier3 = 80;

	public static int MushroomTier = 1;
	public static int MushroomMaxStackSize = 3;
	public static int MushroomEffectLevel = 3;
	public static int MushroomEffectSecond = 3;
	public static int MushroomMultipleTier = 3;
	public static int MushroomMultipleDropAmount = 3;

	public static int PowerfullMushroomTier = 3;
	public static int PowerfullMushroomMaxStackSize = 1;
	public static int PowerfullMushroomEffectLevel = 4;
	public static int PowerfullMushroomEffectSecond = 4;
	public static int PowerfullMushroomMultipleTier = 0;
	public static int PowerfullMushroomMultipleDropAmount = 2;

	public static int BananaTier = 1;
	public static int BananaMaxStackSize = 5;
	public static int BananaEffectLevel = 2;
	public static int BananaEffectSecond = 3;
	public static int BananaMultipleTier = 2;
	public static int BananaMultipleDropAmount = 3;

	public static int FakeItemBoxTier = 1;
	public static int FakeItemBoxMaxStackSize = 1;
	public static int FakeItemBoxHitDamage = 6;
	public static int FakeItemBoxMultipleTier = 0;
	public static int FakeItemBoxMultipleDropAmount = 2;

	public static int TurtleTier = 1;
	public static int TurtleMaxStackSize = 3;
	public static int TurtleHitDamage = 1;
	public static int TurtleMultipleTier = 2;
	public static int TurtleMultipleDropAmount = 3;

	public static int RedTurtleTier = 2;
	public static int RedTurtleMaxStackSize = 3;
	public static int RedTurtleHitDamage = 6;
	public static int RedTurtleMultipleTier = 3;
	public static int RedTurtleMultipleDropAmount = 3;

	public static int ThornedTurtleTier = 4;
	public static int ThornedTurtleMaxStackSize = 1;
	public static int ThornedTurtleHitDamage = 20;
	public static int ThornedTurtleMovingDamage = 6;
	public static int ThornedTurtleMultipleTier = 0;
	public static int ThornedTurtleMultipleDropAmount = 2;

	public static int GessoTier = 3;
	public static int GessoMaxStackSize = 1;
	public static int GessoEffectLevel = 1;
	public static int GessoEffectSecond = 5;
	public static int GessoMultipleTier = 0;
	public static int GessoMultipleDropAmount = 1;

	public static int ThunderTier = 4;
	public static int ThunderHitDamage = 5;
	public static int ThunderMaxStackSize = 1;
	public static int ThunderEffectLevel = 2;
	public static int ThunderEffectSecond = 3;
	public static int ThunderMultipleTier = 0;
	public static int ThunderMultipleDropAmount = 2;

	public static int StarTier = 4;
	public static int StarHitDamage = 2;
	public static int StarMaxStackSize = 1;
	public static float StarWalkSpeed =0.9F;
	public static int StarEffectSecond = 6;
	public static int StarMultipleTier = 0;
	public static int StarMultipleDropAmount = 2;

	public static int TeresaTier = 2;
	public static int TeresaMaxStackSize = 1;
	public static int TeresaEffectSecond = 4;
	public static int TeresaMultipleTier = 0;
	public static int TeresaMultipleDropAmount = 2;

	public static int KillerTier = 4;
	public static int KillerMaxStackSize = 1;
	public static int KillerEffectSecond = 10;
	public static int KillerMovingDamage = 6;
	public static int KillerMultipleTier = 0;
	public static int KillerMultipleDropAmount = 2;

	public static double[] HumanClassSetting = 		{0,0,0,0,0,0,0,20,0.6,3,0.1,6};
	public static double[] CreeperClassSetting = 	{0,0,0,0,0,0,2,14,0.6,3,0.1,6};
	public static double[] ZombieClassSetting = 	{0,0,0,0,2,1,2,35,0.6,3,0.1,10};
	public static double[] SkeletonClassSetting = 	{1,1,0,0,0,0,0,14,0.6,3,0.1,7};
	public static double[] SpiderClassSetting = 	{0,0,1,1,0,0,-1,4,0.7,3,0.1,7};
	public static double[] EndermanClassSetting = 	{1,1,1,1,0,0,15,25,0.5,3,0.1,5};
	public static double[] WitchClassSetting = 		{0,0,1,0,-1,0,-2,10,0.6,4,0.1,7};
	public static double[] PigClassSetting = 		{-1,0,0,-1,1,1,-4,6,0.6,2,0.1,10};
	public static double[] SquidClassSetting = 		{1,1,0,0,0,0,-1,6,0.6,4,0.1,6};
	public static double[] VillagerClassSetting = 	{0,0,0,0,1,0,-4,15,0.6,1,0.1,0};

	public static String KartName1 = "Standard";
	public static String KartName2 = "Survival";
	public static String KartName3 = "Fast";
	public static String KartName4 = "Tricky";
	public static String KartName5 = "CustomKart";
	public static String KartName6 = "CustomKart";
	public static String KartName7 = "CustomKart";
	public static String KartName8 = "CustomKart";

	public static double[] KartSetting1 = 	{1.0,250.0,1.0,0.5,5.0,1.0,5.5,2.0};
	public static double[] KartSetting2 = 	{1.5,220.0,1.0,0.5,1.0,1.0,6.5,2.0};
	public static double[] KartSetting3 = 	{0.8,180.0,4.0,0.5,5.0,1.0,5.5,2.0};
	public static double[] KartSetting4 = 	{0.5,300.0,0.7,0.5,7.0,1.5,6.5,2.0};
	public static double[] KartSetting5 = 	{1.0,200.0,1.5,0.5,5.0,1.7,6.5,5.0};
	public static double[] KartSetting6 = 	{0.5,300.0,2.5,0.5,5.0,1.0,4.0,2.5};
	public static double[] KartSetting7 = 	{4.0,200.0,0.7,1.0,3.0,1.5,5.5,1.5};
	public static double[] KartSetting8 = 	{1.0,200.0,0.8,0.5,6.0,1.0,5.0,2.0};

	public static Boolean isEnable(World w){
		if(EnableThisPlugin)
			if(!DisWorlds.contains(w.getName()))
				return true;
		return false;
	}

	public Settings(YPLKart plugin){
		pl = plugin;
		datafolder = pl.getDataFolder();

		configFile = new File(datafolder, filename);
		config = YamlConfiguration.loadConfiguration(configFile);

		CreateConfig();
		loadConfig();
	}

	public static void loadConfig() {
//〓メイン
		if(!config.contains("enable_this_plugin"))config.set("enable_this_plugin", EnableThisPlugin);
		EnableThisPlugin = config.getBoolean("enable_this_plugin");

		if(!config.contains("enable_scoreboard"))config.set("enable_scoreboard", EnableScoreboard);
		EnableScoreboard = config.getBoolean("enable_scoreboard");

		if(!config.contains("disabled_worlds")){
			ArrayList<String> disworlds = new ArrayList<String>();
			disworlds.add("testworld");
			disworlds.add("testworld2");
			config.set("disabled_worlds", disworlds);
		}
		DisWorlds = config.getStringList("disabled_worlds");

//〓Permission

		if(!config.contains("enable_permission.kart_ride"))config.set("enable_permission.kart_ride", EnablePermissionKartRide);
		EnablePermissionKartRide = config.getBoolean("enable_permission.kart_ride");

		if(!config.contains("enable_permission.kart_drift"))config.set("enable_permission.kart_drift", EnablePermissionKartDrift);
		EnablePermissionKartDrift = config.getBoolean("enable_permission.kart_drift");

		if(!config.contains("enable_permission.cmd_menu"))config.set("enable_permission.cmd_menu", EnablePermissionCMDMenu);
		EnablePermissionCMDMenu = config.getBoolean("enable_permission.cmd_menu");

		if(!config.contains("enable_permission.cmd_entry"))config.set("enable_permission.cmd_entry", EnablePermissionCMDEntry);
		EnablePermissionCMDEntry = config.getBoolean("enable_permission.cmd_entry");

		if(!config.contains("enable_permission.cmd_exit"))config.set("enable_permission.cmd_exit", EnablePermissionCMDExit);
		EnablePermissionCMDExit = config.getBoolean("enable_permission.cmd_exit");

		if(!config.contains("enable_permission.cmd_character"))config.set("enable_permission.cmd_character", EnablePermissionCMDCharacter);
		EnablePermissionCMDCharacter = config.getBoolean("enable_permission.cmd_character");

		//if(!config.contains("enable_permission.cmd_characterreset"))config.set("enable_permission.cmd_characterreset", EnablePermissionCMDCharacterReset);
		//EnablePermissionCMDCharacterReset = config.getBoolean("enable_permission.cmd_characterreset");

		if(!config.contains("enable_permission.cmd_kart"))config.set("enable_permission.cmd_kart", EnablePermissionCMDRide);
		EnablePermissionCMDRide = config.getBoolean("enable_permission.cmd_kart");

		if(!config.contains("enable_permission.cmd_leave"))config.set("enable_permission.cmd_leave", EnablePermissionCMDLeave);
		EnablePermissionCMDLeave = config.getBoolean("enable_permission.cmd_leave");

		if(!config.contains("enable_permission.cmd_ranking"))config.set("enable_permission.cmd_ranking", EnablePermissionCMDRanking);
		EnablePermissionCMDRanking = config.getBoolean("enable_permission.cmd_ranking");

		if(!config.contains("enable_permission.cmd_item"))config.set("enable_permission.cmd_item", EnablePermissionCMDItem);
		EnablePermissionCMDItem = config.getBoolean("enable_permission.cmd_item");

//〓Other

		if(!config.contains("enable_permission.cmd_other_menu"))config.set("enable_permission.cmd_other_menu", EnablePermissionCMDOtherMenu);
		EnablePermissionCMDOtherMenu = config.getBoolean("enable_permission.cmd_other_menu");

		if(!config.contains("enable_permission.cmd_other_entry"))config.set("enable_permission.cmd_other_entry", EnablePermissionCMDOtherEntry);
		EnablePermissionCMDOtherEntry = config.getBoolean("enable_permission.cmd_other_entry");

		if(!config.contains("enable_permission.cmd_other_unentry"))config.set("enable_permission.cmd_other_unentry", EnablePermissionCMDOtherExit);
		EnablePermissionCMDOtherExit = config.getBoolean("enable_permission.cmd_other_unentry");

		if(!config.contains("enable_permission.cmd_other_character"))config.set("enable_permission.cmd_other_character", EnablePermissionCMDOtherCharacter);
		EnablePermissionCMDOtherCharacter = config.getBoolean("enable_permission.cmd_other_character");

		//if(!config.contains("enable_permission.cmd_other_characterreset"))config.set("enable_permission.cmd_other_characterreset", EnablePermissionCMDOtherCharacterReset);
		//EnablePermissionCMDOtherCharacterReset = config.getBoolean("enable_permission.cmd_other_characterreset");

		if(!config.contains("enable_permission.cmd_other_kart"))config.set("enable_permission.cmd_other_kart", EnablePermissionCMDOtherRide);
		EnablePermissionCMDOtherRide = config.getBoolean("enable_permission.cmd_other_kart");

		if(!config.contains("enable_permission.cmd_other_leave"))config.set("enable_permission.cmd_other_leave", EnablePermissionCMDOtherLeave);
		EnablePermissionCMDOtherLeave = config.getBoolean("enable_permission.cmd_other_leave");

		if(!config.contains("enable_permission.cmd_other_ranking"))config.set("enable_permission.cmd_other_ranking", EnablePermissionCMDOtherRanking);
		EnablePermissionCMDOtherRanking = config.getBoolean("enable_permission.cmd_other_ranking");

		if(!config.contains("enable_permission.cmd_other_item"))config.set("enable_permission.cmd_other_item", EnablePermissionCMDOtherItem);
		EnablePermissionCMDOtherItem = config.getBoolean("enable_permission.cmd_other_item");

//〓
		if(!config.contains("enable_permission.use_item"))config.set("enable_permission.use_item", EnablePermissionUseItem);
		EnablePermissionUseItem = config.getBoolean("enable_permission.use_item");

		if(!config.contains("enable_permission.interact_object"))config.set("enable_permission.interact_object", EnablePermissionInteractObject);
		EnablePermissionInteractObject = config.getBoolean("enable_permission.interact_object");

//〓OPPermission

		if(!config.contains("enable_op_permission.kart_remove"))config.set("enable_op_permission.kart_remove", EnableOPPermissionKartRemove);
		EnableOPPermissionKartRemove = config.getBoolean("enable_op_permission.kart_remove");

		if(!config.contains("enable_op_permission.cmd_circuit"))config.set("enable_op_permission.cmd_circuit", EnableOPPermissionCMDCircuit);
		EnableOPPermissionCMDCircuit = config.getBoolean("enable_op_permission.cmd_circuit");

		if(!config.contains("enable_op_permission.cmd_display"))config.set("enable_op_permission.cmd_display", EnableOPPermissionCMDDisplay);
		EnableOPPermissionCMDDisplay = config.getBoolean("enable_op_permission.cmd_display");

		if(!config.contains("enable_op_permission.cmd_reload"))config.set("enable_op_permission.cmd_reload", EnableOPPermissionCMDReload);
		EnableOPPermissionCMDReload = config.getBoolean("enable_op_permission.");

		//if(!config.contains("enable_op_permission.cmd_checkpointtool"))config.set("enable_op_permission.cmd_checkpointtool", EnableOPPermissionCMDCheckPointTool);
		//EnableOPPermissionCMDCheckPointTool = config.getBoolean("enable_op_permission.cmd_checkpointtool");

		if(!config.contains("enable_op_permission.cmd_itemboxtool"))config.set("enable_op_permission.cmd_itemboxtool", EnableOPPermissionCMDItemBoxTool);
		EnableOPPermissionCMDItemBoxTool = config.getBoolean("enable_op_permission.cmd_itemboxtool");

//〓セッティング
		if(!config.contains("settings.number_of_laps"))config.set("settings.number_of_laps", 3);
		NumberOfLaps = config.getInt("settings.number_of_laps") + 1;

		if(!config.contains("settings.start_block_id"))config.set("settings.start_block_id", StartBlock);
		StartBlock = config.getString("settings.start_block_id");

		if(!config.contains("settings.goal_block_id"))config.set("settings.goal_block_id", GoalBlock);
		GoalBlock = config.getString("settings.goal_block_id");

		if(!config.contains("settings.dirt_block_id"))config.set("settings.dirt_block_id", DirtBlock);
		DirtBlock = config.getString("settings.dirt_block_id");

		if(!config.contains("settings.item_slot"))config.set("settings.item_slot", ItemSlot);
		ItemSlot = config.getInt("settings.item_slot");

//〓ジョブ カート
		HumanClassSetting = getJobSetting(EnumCharacter.human, HumanClassSetting);
		CreeperClassSetting = getJobSetting(EnumCharacter.creeper, CreeperClassSetting);
		ZombieClassSetting = getJobSetting(EnumCharacter.zombie, ZombieClassSetting);
		SkeletonClassSetting = getJobSetting(EnumCharacter.skeleton, SkeletonClassSetting);
		SpiderClassSetting = getJobSetting(EnumCharacter.spider, SpiderClassSetting);
		EndermanClassSetting = getJobSetting(EnumCharacter.enderman, EndermanClassSetting);
		WitchClassSetting = getJobSetting(EnumCharacter.witch, WitchClassSetting);
		PigClassSetting = getJobSetting(EnumCharacter.pig, PigClassSetting);
		SquidClassSetting = getJobSetting(EnumCharacter.squid, SquidClassSetting);
		VillagerClassSetting = getJobSetting(EnumCharacter.villager, VillagerClassSetting);

		KartSetting1 = getKartSetting("custom_kart1", KartSetting1);
		KartSetting2 = getKartSetting("custom_kart2", KartSetting2);
		KartSetting3 = getKartSetting("custom_kart3", KartSetting3);
		KartSetting4 = getKartSetting("custom_kart4", KartSetting4);
		KartSetting5 = getKartSetting("custom_kart5", KartSetting5);
		KartSetting6 = getKartSetting("custom_kart6", KartSetting6);
		KartSetting7 = getKartSetting("custom_kart7", KartSetting7);
		KartSetting8 = getKartSetting("custom_kart8", KartSetting8);

		if(!config.contains("karts.custom_kart1.name"))config.set("karts.custom_kart1.name", KartName1);
		KartName1 = config.getString("karts.custom_kart1.name");
		if(!config.contains("karts.custom_kart2.name"))config.set("karts.custom_kart2.name", KartName2);
		KartName2 = config.getString("karts.custom_kart2.name");
		if(!config.contains("karts.custom_kart3.name"))config.set("karts.custom_kart3.name", KartName3);
		KartName3 = config.getString("karts.custom_kart3.name");
		if(!config.contains("karts.custom_kart4.name"))config.set("karts.custom_kart4.name", KartName4);
		KartName4 = config.getString("karts.custom_kart4.name");
		if(!config.contains("karts.custom_kart5.name"))config.set("karts.custom_kart5.name", KartName5);
		KartName5 = config.getString("karts.custom_kart5.name");
		if(!config.contains("karts.custom_kart6.name"))config.set("karts.custom_kart6.name", KartName6);
		KartName6 = config.getString("karts.custom_kart6.name");
		if(!config.contains("karts.custom_kart7.name"))config.set("karts.custom_kart7.name", KartName7);
		KartName7 = config.getString("karts.custom_kart7.name");
		if(!config.contains("karts.custom_kart8.name"))config.set("karts.custom_kart8.name", KartName8);
		KartName8 = config.getString("karts.custom_kart8.name");

//〓アイテム
		if(!config.contains("item.dash_board.effect_level"))config.set("item.dash_board.effect_level", BoostRailEffectLevel);
		BoostRailEffectLevel = config.getInt("item.dash_board.effect_level");

		if(!config.contains("item.dash_board.effect_second"))config.set("item.dash_board.effect_second", BoostRailEffectSecond);
		BoostRailEffectSecond = config.getInt("item.dash_board.effect_second");

		if(!config.contains("item.tier1"))config.set("item.tier1", Tier1);
		Tier1 = config.getInt("item.tier1");

		if(!config.contains("item.tier2"))config.set("item.tier2", Tier2);
		Tier2 = config.getInt("item.tier2");

		if(!config.contains("item.tier3"))config.set("item.tier3", Tier3);
		Tier3 = config.getInt("item.tier3");

		//ダッシュきのこ
		if(!config.contains("item.mushroom.tier"))config.set("item.mushroom.tier", MushroomTier);
		MushroomTier = config.getInt("item.mushroom.tier");

		if(!config.contains("item.mushroom.max_stack_size"))config.set("item.mushroom.max_stack_size", MushroomMaxStackSize);
		MushroomMaxStackSize = config.getInt("item.mushroom.max_stack_size");

		if(!config.contains("item.mushroom.effect_level"))config.set("item.mushroom.effect_level", MushroomEffectLevel);
		MushroomEffectLevel = config.getInt("item.mushroom.effect_level");

		if(!config.contains("item.mushroom.effect_second"))config.set("item.mushroom.effect_second", MushroomEffectSecond);
		MushroomEffectSecond = config.getInt("item.mushroom.effect_second");

		if(!config.contains("item.mushroom.multiple.tier"))config.set("item.mushroom.multiple.tier", MushroomMultipleTier);
		MushroomMultipleTier = config.getInt("item.mushroom.multiple.tier");

		if(!config.contains("item.mushroom.multiple.drop_amount"))config.set("item.mushroom.multiple.drop_amount", MushroomMultipleDropAmount);
		MushroomMultipleDropAmount = config.getInt("item.mushroom.multiple.drop_amount");

		//パワフルダッシュきのこ
		if(!config.contains("item.powerfull_mushroom.tier"))config.set("item.powerfull_mushroom.tier", PowerfullMushroomTier);
		PowerfullMushroomTier = config.getInt("item.powerfull_mushroom.tier");

		if(!config.contains("item.powerfull_mushroom.max_stack_size"))config.set("item.powerfull_mushroom.max_stack_size", PowerfullMushroomMaxStackSize);
		PowerfullMushroomMaxStackSize = config.getInt("item.powerfull_mushroom.max_stack_size");

		if(!config.contains("item.powerfull_mushroom.effect_level"))config.set("item.powerfull_mushroom.effect_level", MushroomEffectLevel);
		PowerfullMushroomEffectLevel = config.getInt("item.powerfull_mushroom.effect_level");

		if(!config.contains("item.powerfull_mushroom.effect_second"))config.set("item.powerfull_mushroom.effect_second", PowerfullMushroomEffectSecond);
		PowerfullMushroomEffectSecond = config.getInt("item.powerfull_mushroom.effect_second");

		if(!config.contains("item.powerfull_mushroom.multiple.tier"))config.set("item.powerfull_mushroom.multiple.tier", PowerfullMushroomMultipleTier);
		PowerfullMushroomMultipleTier = config.getInt("item.powerfull_mushroom.multiple.tier");

		if(!config.contains("item.powerfull_mushroom.multiple.drop_amount"))config.set("item.powerfull_mushroom.multiple.drop_amount", PowerfullMushroomMultipleDropAmount);
		PowerfullMushroomMultipleDropAmount = config.getInt("item.powerfull_mushroom.multiple.drop_amount");

		//バナナ
		if(!config.contains("item.banana.tier"))config.set("item.banana.tier", BananaTier);
		BananaTier = config.getInt("item.banana.tier");

		if(!config.contains("item.banana.max_stack_size"))config.set("item.banana.max_stack_size", BananaMaxStackSize);
		BananaMaxStackSize = config.getInt("item.banana.max_stack_size");

		if(!config.contains("item.banana.effect_level"))config.set("item.banana.effect_level", BananaEffectLevel);
		BananaEffectLevel = config.getInt("item.banana.effect_level");

		if(!config.contains("item.banana.effect_second"))config.set("item.banana.effect_second", BananaEffectSecond);
		BananaEffectSecond = config.getInt("item.banana.effect_second");

		if(!config.contains("item.banana.multiple.tier"))config.set("item.banana.multiple.tier", BananaMultipleTier);
		BananaMultipleTier = config.getInt("item.banana.multiple.tier");

		if(!config.contains("item.banana.multiple.drop_amount"))config.set("item.banana.multiple.drop_amount", BananaMultipleDropAmount);
		BananaMultipleDropAmount = config.getInt("item.banana.multiple.drop_amount");

		//にせアイテムボックス
		if(!config.contains("item.fakeitembox.tier"))config.set("item.fakeitembox.tier", FakeItemBoxTier);
		FakeItemBoxTier = config.getInt("item.fakeitembox.tier");

		if(!config.contains("item.fakeitembox.max_stack_size"))config.set("item.fakeitembox.max_stack_size", FakeItemBoxMaxStackSize);
		FakeItemBoxMaxStackSize = config.getInt("item.fakeitembox.max_stack_size");

		if(!config.contains("item.fakeitembox.hit_damage"))config.set("item.fakeitembox.hit_damage", FakeItemBoxHitDamage);
		FakeItemBoxHitDamage = config.getInt("item.fakeitembox.hit_damage");

		if(!config.contains("item.fakeitembox.multiple.tier"))config.set("item.fakeitembox.multiple.tier", FakeItemBoxMultipleTier);
		FakeItemBoxMultipleTier = config.getInt("item.fakeitembox.multiple.tier");

		if(!config.contains("item.fakeitembox.multiple.drop_amount"))config.set("item.fakeitembox.multiple.drop_amount", FakeItemBoxMultipleDropAmount);
		FakeItemBoxMultipleDropAmount = config.getInt("item.fakeitembox.multiple.drop_amount");

		//ミドリこうら
		if(!config.contains("item.turtle.tier"))config.set("item.turtle.tier", TurtleTier);
		TurtleTier = config.getInt("item.turtle.tier");

		if(!config.contains("item.turtle.max_stack_size"))config.set("item.turtle.max_stack_size", TurtleMaxStackSize);
		TurtleMaxStackSize = config.getInt("item.turtle.max_stack_size");

		if(!config.contains("item.turtle.hit_damage"))config.set("item.turtle.hit_damage", TurtleHitDamage);
		TurtleHitDamage = config.getInt("item.turtle.hit_damage");

		if(!config.contains("item.turtle.multiple.tier"))config.set("item.turtle.multiple.tier", TurtleMultipleTier);
		TurtleMultipleTier = config.getInt("item.turtle.multiple.tier");

		if(!config.contains("item.turtle.multiple.drop_amount"))config.set("item.turtle.multiple.drop_amount", TurtleMultipleDropAmount);
		TurtleMultipleDropAmount = config.getInt("item.turtle.multiple.drop_amount");

		//アカこうら
		if(!config.contains("item.redturtle.tier"))config.set("item.redturtle.tier", RedTurtleTier);
		RedTurtleTier = config.getInt("item.redturtle.tier");

		if(!config.contains("item.redturtle.max_stack_size"))config.set("item.redturtle.max_stack_size", RedTurtleMaxStackSize);
		RedTurtleMaxStackSize = config.getInt("item.redturtle.max_stack_size");

		if(!config.contains("item.redturtle.hit_damage"))config.set("item.redturtle.hit_damage", RedTurtleHitDamage);
		RedTurtleHitDamage = config.getInt("item.redturtle.hit_damage");

		if(!config.contains("item.redturtle.multiple.tier"))config.set("item.redturtle.multiple.tier", RedTurtleMultipleTier);
		RedTurtleMultipleTier = config.getInt("item.redturtle.multiple.tier");

		if(!config.contains("item.redturtle.multiple.drop_amount"))config.set("item.redturtle.multiple.drop_amount", RedTurtleMultipleDropAmount);
		RedTurtleMultipleDropAmount = config.getInt("item.redturtle.multiple.drop_amount");

		//トゲゾーうら
		if(!config.contains("item.thornedturtle.tier"))config.set("item.thornedturtle.tier", ThornedTurtleTier);
		ThornedTurtleTier = config.getInt("item.thornedturtle.tier");

		if(!config.contains("item.thornedturtle.max_stack_size"))config.set("item.thornedturtle.max_stack_size", ThornedTurtleMaxStackSize);
		ThornedTurtleMaxStackSize = config.getInt("item.thornedturtle.max_stack_size");

		if(!config.contains("item.thornedturtle.hit_damage"))config.set("item.thornedturtle.hit_damage", ThornedTurtleHitDamage);
		ThornedTurtleHitDamage = config.getInt("item.thornedturtle.hit_damage");

		if(!config.contains("item.thornedturtle.moving_damage"))config.set("item.thornedturtle.moving_damage", ThornedTurtleMovingDamage);
		ThornedTurtleMovingDamage = config.getInt("item.thornedturtle.moving_damage");

		if(!config.contains("item.thornedturtle.multiple.tier"))config.set("item.thornedturtle.multiple.tier", ThornedTurtleMultipleTier);
		ThornedTurtleMultipleTier = config.getInt("item.thornedturtle.multiple.tier");

		if(!config.contains("item.thornedturtle.multiple.drop_amount"))config.set("item.thornedturtle.multiple.drop_amount", ThornedTurtleMultipleDropAmount);
		ThornedTurtleMultipleDropAmount = config.getInt("item.thornedturtle.multiple.drop_amount");

		//ゲッソー
		if(!config.contains("item.gesso.tier"))config.set("item.gesso.tier", GessoTier);
		GessoTier = config.getInt("item.gesso.tier");

		if(!config.contains("item.gesso.max_stack_size"))config.set("item.gesso.max_stack_size", GessoMaxStackSize);
		GessoMaxStackSize = config.getInt("item.gesso.max_stack_size");

		if(!config.contains("item.gesso.effect_level"))config.set("item.gesso.effect_level", GessoEffectLevel);
		GessoEffectLevel = config.getInt("item.gesso.effect_level");

		if(!config.contains("item.gesso.effect_second"))config.set("item.gesso.effect_second", GessoEffectSecond);
		GessoEffectSecond = config.getInt("item.gesso.effect_second");

		if(!config.contains("item.gesso.multiple.tier"))config.set("item.gesso.multiple.tier", GessoMultipleTier);
		GessoMultipleTier = config.getInt("item.gesso.multiple.tier");

		if(!config.contains("item.gesso.multiple.drop_amount"))config.set("item.gesso.multiple.drop_amount", GessoMultipleDropAmount);
		GessoMultipleDropAmount = config.getInt("item.gesso.multiple.drop_amount");

		//サンダー
		if(!config.contains("item.thunder.tier"))config.set("item.thunder.tier", ThunderTier);
		ThunderTier = config.getInt("item.thunder.tier");

		if(!config.contains("item.thunder.max_stack_size"))config.set("item.thunder.max_stack_size", ThunderMaxStackSize);
		ThunderMaxStackSize = config.getInt("item.thunder.max_stack_size");

		if(!config.contains("item.thunder.hit_damage"))config.set("item.thunder.hit_damage", ThunderHitDamage);
		ThunderHitDamage = config.getInt("item.thunder.hit_damage");

		if(!config.contains("item.thunder.effect_level"))config.set("item.thunder.effect_level", ThunderEffectLevel);
		ThunderEffectLevel = config.getInt("item.thunder.effect_level");

		if(!config.contains("item.thunder.effect_second"))config.set("item.thunder.effect_second", ThunderEffectSecond);
		ThunderEffectSecond = config.getInt("item.thunder.effect_second");

		if(!config.contains("item.thunder.multiple.tier"))config.set("item.thunder.multiple.tier", ThunderMultipleTier);
		ThunderMultipleTier = config.getInt("item.thunder.multiple.tier");

		if(!config.contains("item.thunder.multiple.drop_amount"))config.set("item.thunder.multiple.drop_amount", ThunderMultipleDropAmount);
		ThunderMultipleDropAmount = config.getInt("item.thunder.multiple.drop_amount");

		//スーパースター
		if(!config.contains("item.star.tier"))config.set("item.star.tier", StarTier);
		StarTier = config.getInt("item.star.tier");

		if(!config.contains("item.star.hit_damage"))config.set("item.star.hit_damage", StarHitDamage);
		StarHitDamage = config.getInt("item.star.hit_damage");

		if(!config.contains("item.star.max_stack_size"))config.set("item.star.max_stack_size", StarMaxStackSize);
		StarMaxStackSize = config.getInt("item.star.max_stack_size");

		if(!config.contains("item.star.walk_speed"))config.set("item.star.walk_speed", StarWalkSpeed);
		StarWalkSpeed = (float) config.getDouble("item.star.walk_speed");

		if(!config.contains("item.star.effect_second"))config.set("item.star.effect_second", StarEffectSecond);
		StarEffectSecond = config.getInt("item.star.effect_second");

		if(!config.contains("item.star.multiple.tier"))config.set("item.star.multiple.tier", StarMultipleTier);
		StarMultipleTier = config.getInt("item.star.multiple.tier");

		if(!config.contains("item.star.multiple.drop_amount"))config.set("item.star.multiple.drop_amount", StarMultipleDropAmount);
		StarMultipleDropAmount = config.getInt("item.star.multiple.drop_amount");

		//テレサ
		if(!config.contains("item.teresa.tier"))config.set("item.teresa.tier", TeresaTier);
		TeresaTier = config.getInt("item.teresa.tier");

		if(!config.contains("item.teresa.max_stack_size"))config.set("item.teresa.max_stack_size", TeresaMaxStackSize);
		TeresaMaxStackSize = config.getInt("item.teresa.max_stack_size");

		if(!config.contains("item.teresa.effect_second"))config.set("item.teresa.effect_second", TeresaEffectSecond);
		TeresaEffectSecond = config.getInt("item.teresa.effect_second");

		if(!config.contains("item.teresa.multiple.tier"))config.set("item.teresa.multiple.tier", TeresaMultipleTier);
		TeresaMultipleTier = config.getInt("item.teresa.multiple.tier");

		if(!config.contains("item.teresa.multiple.drop_amount"))config.set("item.teresa.multiple.drop_amount", TeresaMultipleDropAmount);
		TeresaMultipleDropAmount = config.getInt("item.teresa.multiple.drop_amount");

		//キラー
		if(!config.contains("item.killer.tier"))config.set("item.killer.tier", KillerTier);
		KillerTier = config.getInt("item.killer.tier");

		if(!config.contains("item.killer.max_stack_size"))config.set("item.killer.max_stack_size", KillerMaxStackSize);
		KillerMaxStackSize = config.getInt("item.killer.max_stack_size");

		if(!config.contains("item.killer.effect_second"))config.set("item.killer.effect_second", KillerEffectSecond);
		KillerEffectSecond = config.getInt("item.killer.effect_second");

		if(!config.contains("item.killer.moving_damage"))config.set("item.killer.moving_damage", KillerMovingDamage);
		KillerMovingDamage = config.getInt("item.killer.moving_damage");

		if(!config.contains("item.killer.multiple.tier"))config.set("item.killer.multiple.tier", KillerMultipleTier);
		KillerMultipleTier = config.getInt("item.killer.multiple.tier");

		if(!config.contains("item.killer.multiple.drop_amount"))config.set("item.killer.multiple.drop_amount", KillerMultipleDropAmount);
		KillerMultipleDropAmount = config.getInt("item.killer.multiple.drop_amount");

		reloadPermissionMemver();
		reloadItemMember();
		reloadClassMemver();
		reloadKartMemver();

		saveConfigFile();
	}

	public static double[] getJobSetting(String job, double[] initdata){
		if(!config.contains("jobs." + job + ".item_adjust_max_slot"))config.set("jobs." + job + ".item_adjust_max_slot", (int)initdata[0]);
		if(!config.contains("jobs." + job + ".item_adjust_max_stack_size"))config.set("jobs." + job + ".item_adjust_max_stack_size", (int)initdata[1]);
		if(!config.contains("jobs." + job + ".item_adjust_positive_effect_second"))config.set("jobs." + job + ".item_adjust_positive_effect_second", (int)initdata[2]);
		if(!config.contains("jobs." + job + ".item_adjust_positive_effect_level"))config.set("jobs." + job + ".item_adjust_positive_effect_level", (int)initdata[3]);
		if(!config.contains("jobs." + job + ".item_adjust_negative_effect_second"))config.set("jobs." + job + ".item_adjust_negative_effect_second", (int)initdata[4]);
		if(!config.contains("jobs." + job + ".item_adjust_negative_effect_level"))config.set("jobs." + job + ".item_adjust_negative_effect_level", (int)initdata[5]);
		if(!config.contains("jobs." + job + ".item_adjust_attack_damage"))config.set("jobs." + job + ".item_adjust_attack_damage", (int)initdata[6]);
		if(!config.contains("jobs." + job + ".max_health"))config.set("jobs." + job + ".max_health", (int)initdata[7]);
		if(!config.contains("jobs." + job + ".walk_speed"))config.set("jobs." + job + ".walk_speed", initdata[8]);
		if(!config.contains("jobs." + job + ".death_penalty.anti_reskill_second"))config.set("jobs." + job + ".death_penalty.anti_reskill_second", (int)initdata[9]);
		if(!config.contains("jobs." + job + ".death_penalty.walk_speed"))config.set("jobs." + job + ".death_penalty.walk_speed", initdata[10]);
		if(!config.contains("jobs." + job + ".death_penalty.penalty_second"))config.set("jobs." + job + ".death_penalty.penalty_second", (int)initdata[11]);

		double[] jobsetting = {config.getDouble("jobs." + job + ".item_adjust_max_slot"),
								config.getDouble("jobs." + job + ".item_adjust_max_stack_size"),
								config.getDouble("jobs." + job + ".item_adjust_positive_effect_second"),
								config.getDouble("jobs." + job + ".item_adjust_positive_effect_level"),
								config.getDouble("jobs." + job + ".item_adjust_negative_effect_second"),
								config.getDouble("jobs." + job + ".item_adjust_negative_effect_level"),
								config.getDouble("jobs." + job + ".item_adjust_attack_damage"),
								config.getDouble("jobs." + job + ".max_health"),
								config.getDouble("jobs." + job + ".walk_speed"),
								config.getDouble("jobs." + job + ".death_penalty.anti_reskill_second"),
								config.getDouble("jobs." + job + ".death_penalty.walk_speed"),
								config.getDouble("jobs." + job + ".death_penalty.penalty_second")};

		return jobsetting;
	}

	public static double[] getKartSetting(String kart, double[] initdata){
		if(!config.contains("karts." + kart + ".weight"))config.set("karts." + kart + ".weight", initdata[0]);
		if(!config.contains("karts." + kart + ".max_speed"))config.set("karts." + kart + ".max_speed", initdata[1]);
		if(!config.contains("karts." + kart + ".acceleration"))config.set("karts." + kart + ".acceleration", initdata[2]);
		if(!config.contains("karts." + kart + ".speed_decrease_on_dirt"))config.set("karts." + kart + ".speed_decrease_on_dirt", initdata[3]);
		if(!config.contains("karts." + kart + ".climbable_height"))config.set("karts." + kart + ".climbable_height", initdata[4]);
		if(!config.contains("karts." + kart + ".default_cornering_power"))config.set("karts." + kart + ".default_cornering_power", initdata[5]);
		if(!config.contains("karts." + kart + ".on_drift_cornering_power"))config.set("karts." + kart + ".on_drift_cornering_power", initdata[6]);
		if(!config.contains("karts." + kart + ".on_drift_speed_decrease"))config.set("karts." + kart + ".on_drift_speed_decrease", initdata[7]);

		double[] kartsetting = {config.getDouble("karts." + kart + ".weight"),
								config.getDouble("karts." + kart + ".max_speed"),
								config.getDouble("karts." + kart + ".acceleration"),
								config.getDouble("karts." + kart + ".speed_decrease_on_dirt"),
								config.getDouble("karts." + kart + ".climbable_height"),
								config.getDouble("karts." + kart + ".default_cornering_power"),
								config.getDouble("karts." + kart + ".on_drift_cornering_power"),
								config.getDouble("karts." + kart + ".on_drift_speed_decrease")};

		return kartsetting;
	}

	//〓〓	ファイル生成		〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓
	public static void CreateConfig() {
		//pl.log.info(Strings.str_configloadstart);
		if(!(configFile.exists())){
			pl.saveResource(filename, true);
			configFile = new File(datafolder, filename);
			config = YamlConfiguration.loadConfiguration(configFile);
			Util.sendMessage(null, "config.ymlを生成しました");
		}
	}

	//〓〓	ファイル取得		〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓
	public static File getConfigFile(){
		return configFile;
	}

	//〓〓	コンフィグ取得		〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓
	public static FileConfiguration getConfig(){
		return config;
	}

	//〓〓	コンフィグ再取得		〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓
	public static void reloadConfig(){
		configFile = new File(datafolder, filename);
		config = YamlConfiguration.loadConfiguration(configFile);
		loadConfig();
	}

	private static void reloadClassMemver(){
		EnumCharacter.Human.reload(HumanClassSetting);
		EnumCharacter.Creeper.reload(CreeperClassSetting);
		EnumCharacter.Zombie.reload(ZombieClassSetting);
		EnumCharacter.Skeleton.reload(SkeletonClassSetting);
		EnumCharacter.Spider.reload(SpiderClassSetting);
		EnumCharacter.Enderman.reload(EndermanClassSetting);
		EnumCharacter.Witch.reload(WitchClassSetting);
		EnumCharacter.Pig.reload(PigClassSetting);
		EnumCharacter.Squid.reload(SquidClassSetting);
		EnumCharacter.Villager.reload(VillagerClassSetting);
	}

	private static void reloadKartMemver(){
		EnumKarts.Kart1.reload(KartName1, KartSetting1);
		EnumKarts.Kart2.reload(KartName2, KartSetting2);
		EnumKarts.Kart3.reload(KartName3, KartSetting3);
		EnumKarts.Kart4.reload(KartName4, KartSetting4);
		EnumKarts.Kart5.reload(KartName5, KartSetting5);
		EnumKarts.Kart6.reload(KartName6, KartSetting6);
		EnumKarts.Kart7.reload(KartName7, KartSetting7);
		EnumKarts.Kart8.reload(KartName8, KartSetting8);
	}

	private static void reloadPermissionMemver(){
		Permission.kart_ride.reload(Settings.EnablePermissionKartRide, Settings.EnablePermissionKartRide);
		Permission.kart_drift.reload(Settings.EnablePermissionKartDrift, Settings.EnablePermissionKartDrift);
		Permission.op_kart_remove.reload(Settings.EnableOPPermissionKartRemove, Settings.EnableOPPermissionKartRemove);

		Permission.cmd_ka.reload(false, false);
		Permission.cmd_entry.reload(Settings.EnablePermissionCMDEntry, Settings.EnablePermissionCMDOtherEntry);
		Permission.cmd_exit.reload(Settings.EnablePermissionCMDExit, Settings.EnablePermissionCMDOtherExit);
		Permission.cmd_character.reload(Settings.EnablePermissionCMDCharacter, Settings.EnablePermissionCMDOtherCharacter);
		//Permission.cmd_characterreset.reload(Settings.EnablePermissionCMDCharacterReset, Settings.EnablePermissionCMDOtherCharacterReset);
		Permission.cmd_ride.reload(Settings.EnablePermissionCMDRide, Settings.EnablePermissionCMDOtherRide);
		Permission.cmd_leave.reload(Settings.EnablePermissionCMDLeave, Settings.EnablePermissionCMDOtherLeave);
		Permission.cmd_ranking.reload(Settings.EnablePermissionCMDRanking, Settings.EnablePermissionCMDOtherRanking);

		Permission.op_cmd_reload.reload(Settings.EnableOPPermissionCMDReload, Settings.EnableOPPermissionCMDReload);
		//Permission.op_cmd_checkpointtool.reload(Settings.EnableOPPermissionCMDCheckPointTool, Settings.EnableOPPermissionCMDCheckPointTool);
		Permission.op_cmd_itemboxtool.reload(Settings.EnableOPPermissionCMDItemBoxTool, Settings.EnableOPPermissionCMDItemBoxTool);

		Permission.itemcmd_mushroom.reload(Settings.EnablePermissionCMDItem, Settings.EnablePermissionCMDOtherItem);
		Permission.itemcmd_powerfullmushroom.reload(Settings.EnablePermissionCMDItem, Settings.EnablePermissionCMDOtherItem);
		Permission.itemcmd_banana.reload(Settings.EnablePermissionCMDItem, Settings.EnablePermissionCMDOtherItem);
		Permission.itemcmd_fakeitembox.reload(Settings.EnablePermissionCMDItem, Settings.EnablePermissionCMDOtherItem);
		Permission.itemcmd_thunder.reload(Settings.EnablePermissionCMDItem, Settings.EnablePermissionCMDOtherItem);
		Permission.itemcmd_star.reload(Settings.EnablePermissionCMDItem, Settings.EnablePermissionCMDOtherItem);
		Permission.itemcmd_turtle.reload(Settings.EnablePermissionCMDItem, Settings.EnablePermissionCMDOtherItem);
		Permission.itemcmd_redturtle.reload(Settings.EnablePermissionCMDItem, Settings.EnablePermissionCMDOtherItem);
		Permission.itemcmd_thornedturtle.reload(Settings.EnablePermissionCMDItem, Settings.EnablePermissionCMDOtherItem);
		Permission.itemcmd_teresa.reload(Settings.EnablePermissionCMDItem, Settings.EnablePermissionCMDOtherItem);
		Permission.itemcmd_gesso.reload(Settings.EnablePermissionCMDItem, Settings.EnablePermissionCMDOtherItem);
		Permission.itemcmd_killer.reload(Settings.EnablePermissionCMDItem, Settings.EnablePermissionCMDOtherItem);

		Permission.use_mushroom.reload(Settings.EnablePermissionUseItem, Settings.EnablePermissionUseItem);
		Permission.use_powerfullmushroom.reload(Settings.EnablePermissionUseItem, Settings.EnablePermissionUseItem);
		Permission.use_banana.reload(Settings.EnablePermissionUseItem, Settings.EnablePermissionUseItem);
		Permission.use_fakeitembox.reload(Settings.EnablePermissionUseItem, Settings.EnablePermissionUseItem);
		Permission.use_thunder.reload(Settings.EnablePermissionUseItem, Settings.EnablePermissionUseItem);
		Permission.use_star.reload(Settings.EnablePermissionUseItem, Settings.EnablePermissionUseItem);
		Permission.use_turtle.reload(Settings.EnablePermissionUseItem, Settings.EnablePermissionUseItem);
		Permission.use_redturtle.reload(Settings.EnablePermissionUseItem, Settings.EnablePermissionUseItem);
		Permission.use_thornedturtle.reload(Settings.EnablePermissionUseItem, Settings.EnablePermissionUseItem);
		Permission.use_teresa.reload(Settings.EnablePermissionUseItem, Settings.EnablePermissionUseItem);
		Permission.use_gesso.reload(Settings.EnablePermissionUseItem, Settings.EnablePermissionUseItem);
		Permission.use_killer.reload(Settings.EnablePermissionUseItem, Settings.EnablePermissionUseItem);
		Permission.interact_boostrail.reload(Settings.EnablePermissionInteractObject, Settings.EnablePermissionInteractObject);
		Permission.interact_banana.reload(Settings.EnablePermissionInteractObject, Settings.EnablePermissionInteractObject);
		Permission.interact_itembox.reload(Settings.EnablePermissionInteractObject, Settings.EnablePermissionInteractObject);
		Permission.interact_fakeitembox.reload(Settings.EnablePermissionInteractObject, Settings.EnablePermissionInteractObject);
	}

	public static void reloadItemMember(){
		EnumItem.Star.reload(StarTier, 1, StarMaxStackSize);
		EnumItem.StarMultiple.reload(StarMultipleTier, StarMultipleDropAmount, StarMaxStackSize);
		EnumItem.Mushroom.reload(MushroomTier, 1, MushroomMaxStackSize);
		EnumItem.MushroomMultiple.reload(MushroomMultipleTier, MushroomMultipleDropAmount, MushroomMaxStackSize);
		EnumItem.PowerfullMushroom.reload(PowerfullMushroomTier, 1, PowerfullMushroomMaxStackSize);
		EnumItem.PowerfullMushroomMultiple.reload(PowerfullMushroomMultipleTier, PowerfullMushroomMultipleDropAmount, PowerfullMushroomMaxStackSize);
		EnumItem.Turtle.reload(TurtleTier, 1, TurtleMaxStackSize);
		EnumItem.Turtle.reload(TurtleMultipleTier, TurtleMultipleDropAmount, TurtleMaxStackSize);
		EnumItem.RedTurtle.reload(RedTurtleTier, 1, RedTurtleMaxStackSize);
		EnumItem.RedTurtleMultiple.reload(RedTurtleMultipleTier, RedTurtleMultipleDropAmount, RedTurtleMaxStackSize);
		EnumItem.ThornedTurtle.reload(ThornedTurtleTier, 1, ThornedTurtleMaxStackSize);
		EnumItem.ThornedTurtleMultiple.reload(ThornedTurtleMultipleTier, ThornedTurtleMultipleDropAmount, ThornedTurtleMaxStackSize);
		EnumItem.Banana.reload(BananaTier, 1, BananaMaxStackSize);
		EnumItem.BananaMultiple.reload(BananaMultipleTier, BananaMultipleDropAmount, BananaMaxStackSize);
		EnumItem.FakeItembox.reload(FakeItemBoxTier, 1, FakeItemBoxMaxStackSize);
		EnumItem.FakeItemboxMultiple.reload(FakeItemBoxMultipleTier, FakeItemBoxMultipleDropAmount, FakeItemBoxMaxStackSize);
		EnumItem.Thunder.reload(ThunderTier, 1, ThunderMaxStackSize);
		EnumItem.ThunderMultiple.reload(ThunderMultipleTier, ThunderMultipleDropAmount, ThunderMaxStackSize);
		EnumItem.Teresa.reload(TeresaTier, 1, TeresaMaxStackSize);
		EnumItem.TeresaMultiple.reload(TeresaMultipleTier, TeresaMultipleDropAmount, TeresaMaxStackSize);
		EnumItem.Gesso.reload(GessoTier, 1, GessoMaxStackSize);
		EnumItem.GessoMultiple.reload(GessoMultipleTier, GessoMultipleDropAmount, GessoMaxStackSize);
	}

	//〓〓	ファイル保存		〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓
	public static void saveConfigFile(){
		saveFile(configFile, config);
	}

	public static void saveAllFiles(){
		saveConfigFile();
	}

	//〓〓	ファイル保存実行		〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓
	public static void saveFile(File file, FileConfiguration config){
		try{
			config.save(file);
		} catch(IOException e){
			e.printStackTrace();
		}
	}

	public static boolean isStartBlock(Block b){
		if(StartBlock.equalsIgnoreCase(String.valueOf(b.getTypeId()) + ":" + String.valueOf(b.getData())))return true;
		if(StartBlock.contains(String.valueOf(b.getTypeId()) + ":*"))return true;

		return false;
	}
}
