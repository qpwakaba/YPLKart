package com.github.erozabesu.yplkart.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.erozabesu.yplkart.YPLKart;
import com.github.erozabesu.yplkart.enumdata.EnumCharacter;
import com.github.erozabesu.yplkart.enumdata.EnumItem;
import com.github.erozabesu.yplkart.enumdata.EnumKarts;
import com.github.erozabesu.yplkart.enumdata.Permission;
import com.github.erozabesu.yplkart.utils.Util;

public final class Settings {
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
    public static boolean EnablePermissionCMDCharacterReset = true;
    public static boolean EnablePermissionCMDRide = true;
    public static boolean EnablePermissionCMDLeave = true;
    public static boolean EnablePermissionCMDRanking = true;
    public static boolean EnablePermissionCMDItem = true;

    public static boolean EnablePermissionCMDOtherMenu = true;
    public static boolean EnablePermissionCMDOtherEntry = true;
    public static boolean EnablePermissionCMDOtherExit = true;
    public static boolean EnablePermissionCMDOtherCharacter = true;
    public static boolean EnablePermissionCMDOtherCharacterReset = true;
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
    public static float StarWalkSpeed = 0.9F;
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

    public static double[] HumanClassSetting = { 0, 0, 0, 0, 0, 0, 0, 20, 0.6, 3, 0.1, 6 };
    public static double[] CreeperClassSetting = { 0, 0, 0, 0, 0, 0, 2, 14, 0.6, 3, 0.1, 6 };
    public static double[] ZombieClassSetting = { 0, 0, 0, 0, 2, 1, 2, 35, 0.6, 3, 0.1, 10 };
    public static double[] SkeletonClassSetting = { 1, 1, 0, 0, 0, 0, 0, 14, 0.6, 3, 0.1, 7 };
    public static double[] SpiderClassSetting = { 0, 0, 1, 1, 0, 0, -1, 4, 0.7, 3, 0.1, 7 };
    public static double[] EndermanClassSetting = { 1, 1, 1, 1, 0, 0, 15, 25, 0.5, 3, 0.1, 5 };
    public static double[] WitchClassSetting = { 0, 0, 1, 0, -1, 0, -2, 10, 0.6, 4, 0.1, 7 };
    public static double[] PigClassSetting = { -1, 0, 0, -1, 1, 1, -4, 6, 0.6, 2, 0.1, 10 };
    public static double[] SquidClassSetting = { 1, 1, 0, 0, 0, 0, -1, 6, 0.6, 4, 0.1, 6 };
    public static double[] VillagerClassSetting = { 0, 0, 0, 0, 1, 0, -4, 15, 0.6, 1, 0.1, 0 };

    public static String KartName1 = "Standard";
    public static String KartName2 = "Survival";
    public static String KartName3 = "Fast";
    public static String KartName4 = "Tricky";
    public static String KartName5 = "CustomKart";
    public static String KartName6 = "CustomKart";
    public static String KartName7 = "CustomKart";
    public static String KartName8 = "CustomKart";

    public static double[] KartSetting1 = { 1.0, 250.0, 1.0, 0.5, 5.0, 1.0, 5.5, 2.0 };
    public static double[] KartSetting2 = { 1.5, 220.0, 1.0, 0.5, 1.0, 1.0, 6.5, 2.0 };
    public static double[] KartSetting3 = { 0.8, 180.0, 4.0, 0.5, 5.0, 1.0, 5.5, 2.0 };
    public static double[] KartSetting4 = { 0.5, 300.0, 0.7, 0.5, 7.0, 1.5, 6.5, 2.0 };
    public static double[] KartSetting5 = { 1.0, 200.0, 1.5, 0.5, 5.0, 1.7, 6.5, 5.0 };
    public static double[] KartSetting6 = { 0.5, 300.0, 2.5, 0.5, 5.0, 1.0, 4.0, 2.5 };
    public static double[] KartSetting7 = { 4.0, 200.0, 0.7, 1.0, 3.0, 1.5, 5.5, 1.5 };
    public static double[] KartSetting8 = { 1.0, 200.0, 0.8, 0.5, 6.0, 1.0, 5.0, 2.0 };

    public static Boolean isEnable(World w) {
        if (EnableThisPlugin)
            if (!DisWorlds.contains(w.getName()))
                return true;
        return false;
    }

    public Settings(YPLKart plugin) {
        pl = plugin;
        datafolder = pl.getDataFolder();

        configFile = new File(datafolder, filename);
        config = YamlConfiguration.loadConfiguration(configFile);

        if (CreateConfig()) {
            loadConfig();
        }
    }

    public static void loadConfig() {
        //〓メイン
        EnableThisPlugin = getBoolean("enable_this_plugin", EnableThisPlugin);
        EnableScoreboard = getBoolean("enable_scoreboard", EnableScoreboard);

        if (!config.contains("disabled_worlds")) {
            ArrayList<String> disworlds = new ArrayList<String>();
            disworlds.add("testworld");
            disworlds.add("testworld2");
            config.set("disabled_worlds", disworlds);
        }
        DisWorlds = config.getStringList("disabled_worlds");

        //〓Permission

        EnablePermissionKartRide = getBoolean("enable_permission.kart_ride", EnablePermissionKartRide);
        EnablePermissionKartDrift = getBoolean("enable_permission.kart_drift", EnablePermissionKartDrift);

        EnablePermissionCMDMenu = getBoolean("enable_permission.cmd_menu", EnablePermissionCMDMenu);
        EnablePermissionCMDEntry = getBoolean("enable_permission.cmd_entry", EnablePermissionCMDEntry);
        EnablePermissionCMDExit = getBoolean("enable_permission.cmd_exit", EnablePermissionCMDExit);
        EnablePermissionCMDCharacter = getBoolean("enable_permission.cmd_character", EnablePermissionCMDCharacter);
        EnablePermissionCMDCharacterReset = getBoolean("enable_permission.cmd_characterreset",
                EnablePermissionCMDCharacterReset);
        EnablePermissionCMDRide = getBoolean("enable_permission.cmd_kart", EnablePermissionCMDRide);
        EnablePermissionCMDLeave = getBoolean("enable_permission.cmd_leave", EnablePermissionCMDLeave);
        EnablePermissionCMDRanking = getBoolean("enable_permission.cmd_ranking", EnablePermissionCMDRanking);
        EnablePermissionCMDItem = getBoolean("enable_permission.cmd_item", EnablePermissionCMDItem);

        EnablePermissionCMDOtherMenu = getBoolean("enable_permission.cmd_other_menu", EnablePermissionCMDOtherMenu);
        EnablePermissionCMDOtherEntry = getBoolean("enable_permission.cmd_other_entry", EnablePermissionCMDOtherEntry);
        EnablePermissionCMDOtherExit = getBoolean("enable_permission.cmd_other_exit", EnablePermissionCMDOtherExit);
        EnablePermissionCMDOtherCharacter = getBoolean("enable_permission.cmd_other_character",
                EnablePermissionCMDOtherCharacter);
        EnablePermissionCMDOtherCharacterReset = getBoolean("enable_permission.cmd_other_characterreset",
                EnablePermissionCMDOtherCharacterReset);
        EnablePermissionCMDOtherRide = getBoolean("enable_permission.cmd_other_kart", EnablePermissionCMDOtherRide);
        EnablePermissionCMDOtherLeave = getBoolean("enable_permission.cmd_other_leave", EnablePermissionCMDOtherLeave);
        EnablePermissionCMDOtherRanking = getBoolean("enable_permission.cmd_other_ranking",
                EnablePermissionCMDOtherRanking);
        EnablePermissionCMDOtherItem = getBoolean("enable_permission.cmd_other_item", EnablePermissionCMDOtherItem);

        EnablePermissionUseItem = getBoolean("enable_permission.use_item", EnablePermissionUseItem);
        EnablePermissionInteractObject = getBoolean("enable_permission.interact_object", EnablePermissionInteractObject);

        EnableOPPermissionKartRemove = getBoolean("enable_op_permission.kart_remove", EnableOPPermissionKartRemove);
        EnableOPPermissionCMDCircuit = getBoolean("enable_op_permission.cmd_circuit", EnableOPPermissionCMDCircuit);
        EnableOPPermissionCMDDisplay = getBoolean("enable_op_permission.cmd_display", EnableOPPermissionCMDDisplay);
        EnableOPPermissionCMDReload = getBoolean("enable_op_permission.cmd_reload", EnableOPPermissionCMDReload);
        EnableOPPermissionCMDItemBoxTool = getBoolean("enable_op_permission.cmd_itemboxtool",
                EnableOPPermissionCMDItemBoxTool);

        //〓セッティング

        StartBlock = getString("settings.start_block_id", StartBlock);
        GoalBlock = getString("settings.goal_block_id", GoalBlock);
        DirtBlock = getString("settings.dirt_block_id", DirtBlock);
        ItemSlot = getInt("settings.item_slot", ItemSlot);

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

        KartName1 = getString("karts.custom_kart1.name", KartName1);
        KartName2 = getString("karts.custom_kart2.name", KartName2);
        KartName3 = getString("karts.custom_kart3.name", KartName3);
        KartName4 = getString("karts.custom_kart4.name", KartName4);
        KartName5 = getString("karts.custom_kart5.name", KartName5);
        KartName6 = getString("karts.custom_kart6.name", KartName6);
        KartName7 = getString("karts.custom_kart7.name", KartName7);
        KartName8 = getString("karts.custom_kart8.name", KartName8);

        //〓アイテム

        BoostRailEffectLevel = getInt("item.dash_board.effect_level", BoostRailEffectLevel);
        BoostRailEffectSecond = getInt("item.dash_board.effect_second", BoostRailEffectSecond);
        Tier1 = getInt("item.tier1", Tier1);
        Tier2 = getInt("item.tier2", Tier2);
        Tier3 = getInt("item.tier3", Tier3);

        //ダッシュきのこ
        MushroomTier = getInt("item.mushroom.tier", MushroomTier);
        MushroomMaxStackSize = getInt("item.mushroom.max_stack_size", MushroomMaxStackSize);
        MushroomEffectLevel = getInt("item.mushroom.effect_level", MushroomEffectLevel);
        MushroomEffectSecond = getInt("item.mushroom.effect_second", MushroomEffectSecond);
        MushroomMultipleTier = getInt("item.mushroom.multiple.tier", MushroomMultipleTier);
        MushroomMultipleDropAmount = getInt("item.mushroom.multiple.drop_amount", MushroomMultipleDropAmount);

        //パワフルダッシュきのこ
        PowerfullMushroomTier = getInt("item.powerfull_mushroom.tier", PowerfullMushroomTier);
        PowerfullMushroomMaxStackSize = getInt("item.powerfull_mushroom.max_stack_size", PowerfullMushroomMaxStackSize);
        PowerfullMushroomEffectLevel = getInt("item.powerfull_mushroom.effect_level", PowerfullMushroomEffectLevel);
        PowerfullMushroomEffectSecond = getInt("item.powerfull_mushroom.effect_second", PowerfullMushroomEffectSecond);
        PowerfullMushroomMultipleTier = getInt("item.powerfull_mushroom.multiple.tier", PowerfullMushroomMultipleTier);
        PowerfullMushroomMultipleDropAmount = getInt("item.powerfull_mushroom.multiple.drop_amount",
                PowerfullMushroomMultipleDropAmount);

        //バナナ
        BananaTier = getInt("item.banana.tier", BananaTier);
        BananaMaxStackSize = getInt("item.banana.max_stack_size", BananaMaxStackSize);
        BananaEffectLevel = getInt("item.banana.effect_level", BananaEffectLevel);
        BananaEffectSecond = getInt("item.banana.effect_second", BananaEffectSecond);
        BananaMultipleTier = getInt("item.banana.multiple.tier", BananaMultipleTier);
        BananaMultipleDropAmount = getInt("item.banana.multiple.drop_amount", BananaMultipleDropAmount);

        //にせアイテムボックス
        FakeItemBoxTier = getInt("item.fakeitembox.tier", FakeItemBoxTier);
        FakeItemBoxMaxStackSize = getInt("item.fakeitembox.max_stack_size", FakeItemBoxMaxStackSize);
        FakeItemBoxHitDamage = getInt("item.fakeitembox.hit_damage", FakeItemBoxHitDamage);
        FakeItemBoxMultipleTier = getInt("item.fakeitembox.multiple.tier", FakeItemBoxMultipleTier);
        FakeItemBoxMultipleDropAmount = getInt("item.fakeitembox.multiple.drop_amount", FakeItemBoxMultipleDropAmount);

        //ミドリこうら
        TurtleTier = getInt("item.turtle.tier", TurtleTier);
        TurtleMaxStackSize = getInt("item.turtle.max_stack_size", TurtleMaxStackSize);
        TurtleHitDamage = getInt("item.turtle.hit_damage", TurtleHitDamage);
        TurtleMultipleTier = getInt("item.turtle.multiple.tier", TurtleMultipleTier);
        TurtleMultipleDropAmount = getInt("item.turtle.multiple.drop_amount", TurtleMultipleDropAmount);

        //アカこうら
        RedTurtleTier = getInt("item.redturtle.tier", RedTurtleTier);
        RedTurtleMaxStackSize = getInt("item.redturtle.max_stack_size", RedTurtleMaxStackSize);
        RedTurtleHitDamage = getInt("item.redturtle.hit_damage", RedTurtleHitDamage);
        RedTurtleMultipleTier = getInt("item.redturtle.multiple.tier", RedTurtleMultipleTier);
        RedTurtleMultipleDropAmount = getInt("item.redturtle.multiple.drop_amount", RedTurtleMultipleDropAmount);

        //トゲゾーうら
        ThornedTurtleTier = getInt("item.thornedturtle.tier", ThornedTurtleTier);
        ThornedTurtleMaxStackSize = getInt("item.thornedturtle.max_stack_size", ThornedTurtleMaxStackSize);
        ThornedTurtleHitDamage = getInt("item.thornedturtle.hit_damage", ThornedTurtleHitDamage);
        ThornedTurtleMovingDamage = getInt("item.thornedturtle.moving_damage", ThornedTurtleMovingDamage);
        ThornedTurtleMultipleTier = getInt("item.thornedturtle.multiple.tier", ThornedTurtleMultipleTier);
        ThornedTurtleMultipleDropAmount = getInt("item.thornedturtle.multiple.drop_amount",
                ThornedTurtleMultipleDropAmount);

        //ゲッソー
        GessoTier = getInt("item.gesso.tier", GessoTier);
        GessoMaxStackSize = getInt("item.gesso.max_stack_size", GessoMaxStackSize);
        GessoEffectLevel = getInt("item.gesso.effect_level", GessoEffectLevel);
        GessoEffectSecond = getInt("item.gesso.effect_second", GessoEffectSecond);
        GessoMultipleTier = getInt("item.gesso.multiple.tier", GessoMultipleTier);
        GessoMultipleDropAmount = getInt("item.gesso.multiple.drop_amount", GessoMultipleDropAmount);

        //サンダー
        ThunderTier = getInt("item.thunder.tier", ThunderTier);
        ThunderMaxStackSize = getInt("item.thunder.max_stack_size", ThunderMaxStackSize);
        ThunderHitDamage = getInt("item.thunder.hit_damage", ThunderHitDamage);
        ThunderEffectLevel = getInt("item.thunder.effect_level", ThunderEffectLevel);
        ThunderEffectSecond = getInt("item.thunder.effect_second", ThunderEffectSecond);
        ThunderMultipleTier = getInt("item.thunder.multiple.tier", ThunderMultipleTier);
        ThunderMultipleDropAmount = getInt("item.thunder.multiple.drop_amount", ThunderMultipleDropAmount);

        //スーパースター
        StarTier = getInt("item.star.tier", StarTier);
        StarHitDamage = getInt("item.star.hit_damage", StarHitDamage);
        StarMaxStackSize = getInt("item.star.max_stack_size", StarMaxStackSize);
        StarWalkSpeed = (float) getDouble("item.star.walk_speed", StarWalkSpeed);
        StarEffectSecond = getInt("item.star.effect_second", StarEffectSecond);
        StarMultipleTier = getInt("item.star.multiple.tier", StarMultipleTier);
        StarMultipleDropAmount = getInt("item.star.multiple.drop_amount", StarMultipleDropAmount);

        //テレサ
        TeresaTier = getInt("item.teresa.tier", TeresaTier);
        TeresaMaxStackSize = getInt("item.teresa.max_stack_size", TeresaMaxStackSize);
        TeresaEffectSecond = getInt("item.teresa.effect_second", TeresaEffectSecond);
        TeresaMultipleTier = getInt("item.teresa.multiple.tier", TeresaMultipleTier);
        TeresaMultipleDropAmount = getInt("item.teresa.multiple.drop_amount", TeresaMultipleDropAmount);

        //キラー
        KillerTier = getInt("item.killer.tier", KillerTier);
        KillerMaxStackSize = getInt("item.killer.max_stack_size", KillerMaxStackSize);
        KillerEffectSecond = getInt("item.killer.effect_second", KillerEffectSecond);
        KillerMovingDamage = getInt("item.killer.moving_damage", KillerMovingDamage);
        KillerMultipleTier = getInt("item.killer.multiple.tier", KillerMultipleTier);
        KillerMultipleDropAmount = getInt("item.killer.multiple.drop_amount", KillerMultipleDropAmount);

        reloadPermissionMemver();
        reloadItemMember();
        reloadClassMemver();
        reloadKartMemver();

        saveConfigFile();
    }

    public static double[] getJobSetting(String job, double[] initdata) {
        if (!config.contains("jobs." + job + ".item_adjust_max_slot"))
            config.set("jobs." + job + ".item_adjust_max_slot", (int) initdata[0]);
        if (!config.contains("jobs." + job + ".item_adjust_max_stack_size"))
            config.set("jobs." + job + ".item_adjust_max_stack_size", (int) initdata[1]);
        if (!config.contains("jobs." + job + ".item_adjust_positive_effect_second"))
            config.set("jobs." + job + ".item_adjust_positive_effect_second", (int) initdata[2]);
        if (!config.contains("jobs." + job + ".item_adjust_positive_effect_level"))
            config.set("jobs." + job + ".item_adjust_positive_effect_level", (int) initdata[3]);
        if (!config.contains("jobs." + job + ".item_adjust_negative_effect_second"))
            config.set("jobs." + job + ".item_adjust_negative_effect_second", (int) initdata[4]);
        if (!config.contains("jobs." + job + ".item_adjust_negative_effect_level"))
            config.set("jobs." + job + ".item_adjust_negative_effect_level", (int) initdata[5]);
        if (!config.contains("jobs." + job + ".item_adjust_attack_damage"))
            config.set("jobs." + job + ".item_adjust_attack_damage", (int) initdata[6]);
        if (!config.contains("jobs." + job + ".max_health"))
            config.set("jobs." + job + ".max_health", (int) initdata[7]);
        if (!config.contains("jobs." + job + ".walk_speed"))
            config.set("jobs." + job + ".walk_speed", initdata[8]);
        if (!config.contains("jobs." + job + ".death_penalty.anti_reskill_second"))
            config.set("jobs." + job + ".death_penalty.anti_reskill_second", (int) initdata[9]);
        if (!config.contains("jobs." + job + ".death_penalty.walk_speed"))
            config.set("jobs." + job + ".death_penalty.walk_speed", initdata[10]);
        if (!config.contains("jobs." + job + ".death_penalty.penalty_second"))
            config.set("jobs." + job + ".death_penalty.penalty_second", (int) initdata[11]);

        double[] jobsetting = { config.getDouble("jobs." + job + ".item_adjust_max_slot"),
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
                config.getDouble("jobs." + job + ".death_penalty.penalty_second") };

        return jobsetting;
    }

    public static double[] getKartSetting(String kart, double[] initdata) {
        if (!config.contains("karts." + kart + ".weight"))
            config.set("karts." + kart + ".weight", initdata[0]);
        if (!config.contains("karts." + kart + ".max_speed"))
            config.set("karts." + kart + ".max_speed", initdata[1]);
        if (!config.contains("karts." + kart + ".acceleration"))
            config.set("karts." + kart + ".acceleration", initdata[2]);
        if (!config.contains("karts." + kart + ".speed_decrease_on_dirt"))
            config.set("karts." + kart + ".speed_decrease_on_dirt", initdata[3]);
        if (!config.contains("karts." + kart + ".climbable_height"))
            config.set("karts." + kart + ".climbable_height", initdata[4]);
        if (!config.contains("karts." + kart + ".default_cornering_power"))
            config.set("karts." + kart + ".default_cornering_power", initdata[5]);
        if (!config.contains("karts." + kart + ".on_drift_cornering_power"))
            config.set("karts." + kart + ".on_drift_cornering_power", initdata[6]);
        if (!config.contains("karts." + kart + ".on_drift_speed_decrease"))
            config.set("karts." + kart + ".on_drift_speed_decrease", initdata[7]);

        double[] kartsetting = { config.getDouble("karts." + kart + ".weight"),
                config.getDouble("karts." + kart + ".max_speed"),
                config.getDouble("karts." + kart + ".acceleration"),
                config.getDouble("karts." + kart + ".speed_decrease_on_dirt"),
                config.getDouble("karts." + kart + ".climbable_height"),
                config.getDouble("karts." + kart + ".default_cornering_power"),
                config.getDouble("karts." + kart + ".on_drift_cornering_power"),
                config.getDouble("karts." + kart + ".on_drift_speed_decrease") };

        return kartsetting;
    }

    /**
     * Plugin.jarから/plugins/YPLKartディレクトリにコンフィグファイルをコピーする
     * ファイルが生成済みの場合は何もせずtrueを返す
     * ファイルが未生成の場合はファイルのコピーを試みる
     * @return ファイルの生成に成功したかどうか
     */
    public static boolean CreateConfig() {
        if (!(configFile.exists())) {
            //jarファイル内にコピー元のファイルが存在しない場合
            if (!Util.copyResource(filename)) {
                Messages.sendAbsolute(null, "[" + YPLKart.PLUGIN_NAME + "] v."
                        + YPLKart.PLUGIN_VERSION + " "
                        + filename + " was not found in jar file");
                YPLKart.getInstance().onDisable();
                return false;
            }

            //jarファイル内からファイルのコピーに成功した場合
            configFile = new File(datafolder, filename);
            config = YamlConfiguration.loadConfiguration(configFile);
        }

        return true;
    }

    //〓〓	ファイル取得		〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓
    public static File getConfigFile() {
        return configFile;
    }

    //〓〓	コンフィグ取得		〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓
    public static FileConfiguration getConfig() {
        return config;
    }

    //〓〓	コンフィグ再取得		〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓
    public static void reloadConfig() {
        configFile = new File(datafolder, filename);
        config = YamlConfiguration.loadConfiguration(configFile);
        loadConfig();
    }

    private static void reloadClassMemver() {
        EnumCharacter.HUMAN.reload(HumanClassSetting);
        EnumCharacter.CREEPER.reload(CreeperClassSetting);
        EnumCharacter.ZOMBIE.reload(ZombieClassSetting);
        EnumCharacter.SKELETON.reload(SkeletonClassSetting);
        EnumCharacter.SPIDER.reload(SpiderClassSetting);
        EnumCharacter.ENDERMAN.reload(EndermanClassSetting);
        EnumCharacter.WITCH.reload(WitchClassSetting);
        EnumCharacter.PIG.reload(PigClassSetting);
        EnumCharacter.SQUID.reload(SquidClassSetting);
        EnumCharacter.VILLAGER.reload(VillagerClassSetting);
    }

    private static void reloadKartMemver() {
        EnumKarts.KART1.reload(KartName1, KartSetting1);
        EnumKarts.KART2.reload(KartName2, KartSetting2);
        EnumKarts.KART3.reload(KartName3, KartSetting3);
        EnumKarts.KART4.reload(KartName4, KartSetting4);
        EnumKarts.KART5.reload(KartName5, KartSetting5);
        EnumKarts.KART6.reload(KartName6, KartSetting6);
        EnumKarts.KART7.reload(KartName7, KartSetting7);
        EnumKarts.KART8.reload(KartName8, KartSetting8);
    }

    private static void reloadPermissionMemver() {
        Permission.KART_RIDE.reload(EnablePermissionKartRide, EnablePermissionKartRide);
        Permission.KART_DRIFT.reload(EnablePermissionKartDrift, EnablePermissionKartDrift);
        Permission.OP_KART_REMOVE.reload(EnableOPPermissionKartRemove, EnableOPPermissionKartRemove);

        Permission.CMD_KA.reload(false, false);
        Permission.CMD_ENTRY.reload(EnablePermissionCMDEntry, EnablePermissionCMDOtherEntry);
        Permission.CMD_EXIT.reload(EnablePermissionCMDExit, EnablePermissionCMDOtherExit);
        Permission.CMD_CHARACTER.reload(EnablePermissionCMDCharacter, EnablePermissionCMDOtherCharacter);
        Permission.CMD_CHARACTERRESET.reload(EnablePermissionCMDCharacterReset, EnablePermissionCMDOtherCharacterReset);
        Permission.CMD_RIDE.reload(EnablePermissionCMDRide, EnablePermissionCMDOtherRide);
        Permission.CMD_LEAVE.reload(EnablePermissionCMDLeave, EnablePermissionCMDOtherLeave);
        Permission.CMD_RANKING.reload(EnablePermissionCMDRanking, EnablePermissionCMDOtherRanking);

        Permission.OP_CMD_RELOAD.reload(EnableOPPermissionCMDReload, EnableOPPermissionCMDReload);
        Permission.OP_CMD_ITEMBOX.reload(EnableOPPermissionCMDItemBoxTool, EnableOPPermissionCMDItemBoxTool);

        Permission.ITEMCMD_MUSHROOM.reload(EnablePermissionCMDItem, EnablePermissionCMDOtherItem);
        Permission.ITEMCMD_POWERFULLMUSHROOM.reload(EnablePermissionCMDItem, EnablePermissionCMDOtherItem);
        Permission.ITEMCMD_BANANA.reload(EnablePermissionCMDItem, EnablePermissionCMDOtherItem);
        Permission.ITEMCMD_FAKEITEMBOX.reload(EnablePermissionCMDItem, EnablePermissionCMDOtherItem);
        Permission.ITEMCMD_THUNDER.reload(EnablePermissionCMDItem, EnablePermissionCMDOtherItem);
        Permission.ITEMCMD_STAR.reload(EnablePermissionCMDItem, EnablePermissionCMDOtherItem);
        Permission.ITEMCMD_TURTLE.reload(EnablePermissionCMDItem, EnablePermissionCMDOtherItem);
        Permission.ITEMCMD_REDTURTLE.reload(EnablePermissionCMDItem, EnablePermissionCMDOtherItem);
        Permission.ITEMCMD_THORNEDTURTLE.reload(EnablePermissionCMDItem, EnablePermissionCMDOtherItem);
        Permission.ITEMCMD_TERESA.reload(EnablePermissionCMDItem, EnablePermissionCMDOtherItem);
        Permission.ITEMCMD_GESSO.reload(EnablePermissionCMDItem, EnablePermissionCMDOtherItem);
        Permission.ITEMCMD_KILLER.reload(EnablePermissionCMDItem, EnablePermissionCMDOtherItem);

        Permission.USE_MUSHROOM.reload(EnablePermissionUseItem, EnablePermissionUseItem);
        Permission.USE_POWERFULLMUSHROOM.reload(EnablePermissionUseItem, EnablePermissionUseItem);
        Permission.USE_BANANA.reload(EnablePermissionUseItem, EnablePermissionUseItem);
        Permission.USE_FAKEITEMBOX.reload(EnablePermissionUseItem, EnablePermissionUseItem);
        Permission.USE_THUNDER.reload(EnablePermissionUseItem, EnablePermissionUseItem);
        Permission.USE_STAR.reload(EnablePermissionUseItem, EnablePermissionUseItem);
        Permission.USE_TURTLE.reload(EnablePermissionUseItem, EnablePermissionUseItem);
        Permission.USE_REDTURTLE.reload(EnablePermissionUseItem, EnablePermissionUseItem);
        Permission.USE_THORNEDTURTLE.reload(EnablePermissionUseItem, EnablePermissionUseItem);
        Permission.USE_TERESA.reload(EnablePermissionUseItem, EnablePermissionUseItem);
        Permission.USE_GESSO.reload(EnablePermissionUseItem, EnablePermissionUseItem);
        Permission.USE_KILLER.reload(EnablePermissionUseItem, EnablePermissionUseItem);
        Permission.INTERACT_DASHBOARD.reload(EnablePermissionInteractObject, EnablePermissionInteractObject);
        Permission.INTERACT_BANANA.reload(EnablePermissionInteractObject, EnablePermissionInteractObject);
        Permission.INTERACT_ITEMBOX.reload(EnablePermissionInteractObject, EnablePermissionInteractObject);
        Permission.INTERACT_FAKEITEMBOX.reload(EnablePermissionInteractObject, EnablePermissionInteractObject);
    }

    public static void reloadItemMember() {
        EnumItem.STAR.reload(StarTier, 1, StarMaxStackSize);
        EnumItem.STAR_MULTIPLE.reload(StarMultipleTier, StarMultipleDropAmount, StarMaxStackSize);
        EnumItem.MUSHROOM.reload(MushroomTier, 1, MushroomMaxStackSize);
        EnumItem.MUSHROOM_MULTIPLE.reload(MushroomMultipleTier, MushroomMultipleDropAmount, MushroomMaxStackSize);
        EnumItem.POWERFULL_MUSHROOM.reload(PowerfullMushroomTier, 1, PowerfullMushroomMaxStackSize);
        EnumItem.POWERFULL_MUSHROOM_MULTIPLE.reload(PowerfullMushroomMultipleTier, PowerfullMushroomMultipleDropAmount,
                PowerfullMushroomMaxStackSize);
        EnumItem.TURTLE.reload(TurtleTier, 1, TurtleMaxStackSize);
        EnumItem.TURTLE.reload(TurtleMultipleTier, TurtleMultipleDropAmount, TurtleMaxStackSize);
        EnumItem.RED_TURTLE.reload(RedTurtleTier, 1, RedTurtleMaxStackSize);
        EnumItem.RED_TURTLE_MULTIPLE.reload(RedTurtleMultipleTier, RedTurtleMultipleDropAmount, RedTurtleMaxStackSize);
        EnumItem.THORNED_TURTLE.reload(ThornedTurtleTier, 1, ThornedTurtleMaxStackSize);
        EnumItem.THORNED_TURTLE_MULTIPLE.reload(ThornedTurtleMultipleTier, ThornedTurtleMultipleDropAmount,
                ThornedTurtleMaxStackSize);
        EnumItem.BANANA.reload(BananaTier, 1, BananaMaxStackSize);
        EnumItem.BANANA_MULTIPLE.reload(BananaMultipleTier, BananaMultipleDropAmount, BananaMaxStackSize);
        EnumItem.FAKE_ITEMBOX.reload(FakeItemBoxTier, 1, FakeItemBoxMaxStackSize);
        EnumItem.FAKE_ITEMBOX_MULTIPLE.reload(FakeItemBoxMultipleTier, FakeItemBoxMultipleDropAmount,
                FakeItemBoxMaxStackSize);
        EnumItem.THUNDER.reload(ThunderTier, 1, ThunderMaxStackSize);
        EnumItem.THUNDER_MULTIPLE.reload(ThunderMultipleTier, ThunderMultipleDropAmount, ThunderMaxStackSize);
        EnumItem.TERESA.reload(TeresaTier, 1, TeresaMaxStackSize);
        EnumItem.TERESA_MULTIPLE.reload(TeresaMultipleTier, TeresaMultipleDropAmount, TeresaMaxStackSize);
        EnumItem.GESSO.reload(GessoTier, 1, GessoMaxStackSize);
        EnumItem.GESSO_MULTIPLE.reload(GessoMultipleTier, GessoMultipleDropAmount, GessoMaxStackSize);
    }

    //〓〓	ファイル保存		〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓
    public static void saveConfigFile() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean getBoolean(String path, Boolean defaultvalue) {
        if (!config.contains(path))
            config.set(path, defaultvalue);
        return config.getBoolean(path);
    }

    public static int getInt(String path, int defaultvalue) {
        if (!config.contains(path))
            config.set(path, defaultvalue);
        return config.getInt(path);
    }

    public static double getDouble(String path, double defaultvalue) {
        if (!config.contains(path))
            config.set(path, defaultvalue);
        return config.getDouble(path);
    }

    public static String getString(String path, String defaultvalue) {
        if (!config.contains(path))
            config.set(path, defaultvalue);
        return config.getString(path);
    }

    public static boolean isStartBlock(Block b) {
        if (StartBlock.equalsIgnoreCase(String.valueOf(b.getTypeId()) + ":" + String.valueOf(b.getData())))
            return true;
        if (StartBlock.contains(String.valueOf(b.getTypeId()) + ":*"))
            return true;

        return false;
    }
}
