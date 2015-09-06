package com.github.erozabesu.yplkart.utils;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.YPLKart;
import com.github.erozabesu.yplkart.data.ConfigEnum;
import com.github.erozabesu.yplkart.data.ItemEnum;
import com.github.erozabesu.yplkart.data.MessageEnum;
import com.github.erozabesu.yplkart.enumdata.Particle;
import com.github.erozabesu.yplkart.object.Circuit;
import com.github.erozabesu.yplkart.object.Racer;
import com.github.erozabesu.yplkart.reflection.Constructors;
import com.github.erozabesu.yplkart.reflection.Methods;

/**
 * レース用エンティティの取得、操作を行うユーティリティクラス。<br>
 * また、org.bukkit.entity.Entity.remove()メソッドはロード済みのチャンク以外では動作しないため、
 * リスナークラスからChunkUnloadEventをキャンセルできるよう、レース中に妨害エンティティが設置されたチャンクの配列も管理する。<br>
 * @author erozabesu
 */
public class RaceEntityUtil {

    private static String ItemBoxName = ChatColor.GOLD + "ItemBox";
    private static String FakeItemBoxName = ItemBoxName + "！！";
    private static String DisposableFakeItemBoxName = ItemBoxName + "！";
    private static Set<Chunk> jammerEntityExistChunkArray = new HashSet<Chunk>();

    public static Set<Chunk> getJammerEntityExistChunkArray() {
        return jammerEntityExistChunkArray;
    }

    public static void addJammerEntityExistChunkArray(Chunk chunk) {
        getJammerEntityExistChunkArray().add(chunk);
    }

    public static void removeJammerEntityExistChunkArray(Chunk chunk) {
        getJammerEntityExistChunkArray().remove(chunk);
    }

    //〓 Edit Entity 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static ArmorStand createJammerArmorStand(Circuit circuit, Location location, String customName, ItemStack handItem) {
        ArmorStand armorStand = location.getWorld().spawn(location, ArmorStand.class);

        armorStand.setItemInHand(handItem);
        armorStand.setCustomName(customName);
        armorStand.setCustomNameVisible(false);
        armorStand.setVisible(false);
        armorStand.setBasePlate(false);
        armorStand.setRemoveWhenFarAway(false);
        armorStand.setGravity(true);

        Object nmsBanana = Util.getCraftEntity(armorStand);
        Object vector3f = ReflectionUtil.newInstance(Constructors.nmsVector3f, -26.0F + location.getPitch(), 1.00F, 0.0F);
        ReflectionUtil.invoke(Methods.nmsEntityArmorStand_setRightArmPose, nmsBanana, vector3f);

        circuit.addJammerEntity(armorStand);

        return armorStand;
    }

    public static ArmorStand createBanana(Circuit circuit, Location location) {
        if (circuit == null) {
            return null;
        }

        location.setYaw(new Random().nextInt(360));
        location.setPitch(0.0F);

        ItemStack handItem = new ItemStack(ItemEnum.BANANA.getDisplayBlockMaterial(), 1, (short) 0, ItemEnum.BANANA.getDisplayBlockMaterialData());
        ArmorStand banana = createJammerArmorStand(circuit, location, ItemEnum.BANANA.getDisplayName(), handItem);

        addJammerEntityExistChunkArray(banana.getLocation().getChunk());

        return banana;
    }

    public static ArmorStand createTurtle(Circuit circuit, Location location, ItemEnum itemEnum) {
        if (circuit == null) {
            return null;
        }
        if (!itemEnum.equals(ItemEnum.TURTLE)
                && !itemEnum.equals(ItemEnum.RED_TURTLE)
                && !itemEnum.equals(ItemEnum.THORNED_TURTLE)) {
            return null;
        }

        location.setPitch(0.0F);

        ItemStack handItem = new ItemStack(itemEnum.getDisplayBlockMaterial(), 1, (short) 0, itemEnum.getDisplayBlockMaterialData());
        ArmorStand turtle = createJammerArmorStand(circuit, location, itemEnum.getDisplayName(), handItem);

        return turtle;
    }

    public static EnderCrystal createItemBox(Location location, int tier) {
        Location blockLocation = Util.adjustLocationToBlockCenter(location.getBlock().getLocation());
        EnderCrystal endercrystal = blockLocation.getWorld().spawn(blockLocation, EnderCrystal.class);
        if (tier <= 1) {
            endercrystal.setCustomName(ItemBoxName);
            endercrystal.setCustomNameVisible(false);
        } else {
            endercrystal.setCustomName(ItemBoxName + "Tier" + tier);
            endercrystal.setCustomNameVisible(true);
        }

        return endercrystal;
    }

    public static EnderCrystal createFakeItemBox(Location location) {
        Location blockLocation = Util.adjustLocationToBlockCenter(location.getBlock().getLocation());
        EnderCrystal endercrystal = blockLocation.getWorld().spawn(blockLocation, EnderCrystal.class);

        endercrystal.setCustomName(FakeItemBoxName);
        endercrystal.setCustomNameVisible(true);

        return endercrystal;
    }

    public static EnderCrystal createDesposableFakeItemBox(Location location, Circuit circuit) {
        Location blockLocation = Util.adjustLocationToBlockCenter(location.getBlock().getLocation());
        EnderCrystal endercrystal = blockLocation.getWorld().spawn(blockLocation, EnderCrystal.class);

        endercrystal.setCustomName(DisposableFakeItemBoxName);
        endercrystal.setCustomNameVisible(true);

        circuit.addJammerEntity(endercrystal);
        addJammerEntityExistChunkArray(endercrystal.getLocation().getChunk());

        return endercrystal;
    }

    //〓 Util - Get 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 引数entityが高級アイテムボックスの場合階級の整数値を返す。<br>
     * 通常のアイテムボックスの場合は1を返す。<br>
     * アイテムボックスでない場合は0を返す。
     * @param entity チェックするエンティティ
     * @return アイテムボックスの階級
     */
    public static int getTierByItemBoxEntity(Entity entity) {
        if (isHighGradeItemBoxEntity(entity)) {
            String customName = entity.getCustomName();
            int tier = Util.extractIntegerFromString(customName);

            if (tier <= 1) {
                return 1;
            } else {
                return tier;
            }
        } else {
            if (isNormalItemBoxEntity(entity)) {
                return 1;
            }
        }

        return 0;
    }

    /**
     * 引数entityが通常のアイテムボックスエンティティかどうかを返す。<br>
     * 高級アイテムボックス、にせアイテムボックスの場合はfalseを返す。
     * @param entity チェックするエンティティ
     * @return アイテムボックスエンティティかどうか
     */
    public static boolean isNormalItemBoxEntity(Entity entity) {
        if (!(entity instanceof EnderCrystal)) {
            return false;
        }

        String customName = entity.getCustomName();
        if (customName == null || customName.equalsIgnoreCase("")) {
            return false;
        }

        if (!customName.matches("^(§.)*ItemBox$")) {
            return false;
        }

        return true;
    }

    /**
     * 引数entityが高級アイテムボックスエンティティかどうかを返す。<br>
     * 通常のアイテムボックス、にせアイテムボックスの場合はfalseを返す。
     * @param entity チェックするエンティティ
     * @return アイテムボックスエンティティかどうか
     */
    public static boolean isHighGradeItemBoxEntity(Entity entity) {
        if (!(entity instanceof EnderCrystal)) {
            return false;
        }

        String customName = entity.getCustomName();
        if (customName == null || customName.equalsIgnoreCase("")) {
            return false;
        }

        if (!customName.matches("^(§.)*ItemBoxTier[2-9]$")) {
            return false;
        }

        return true;
    }

    /**
     * 引数entityが通常のにせアイテムボックスエンティティかどうかを返す。<br>
     * 使い捨てのにせアイテムボックスの場合はfalseを返す。
     * @param entity チェックするエンティティ
     * @return アイテムボックスエンティティかどうか
     */
    public static boolean isNormalFakeItemBox(Entity entity) {
        if (!(entity instanceof EnderCrystal)) {
            return false;
        }

        String customName = entity.getCustomName();
        if (customName == null || customName.equalsIgnoreCase("")) {
            return false;
        }

        if (!customName.matches("^(§.)*ItemBox(!|！){2}$")) {
            return false;
        }

        return true;
    }

    /**
     * 引数entityが使い捨てのにせアイテムボックスエンティティかどうかを返す。<br>
     * 通常のにせアイテムボックスの場合はfalseを返す。
     * @param entity チェックするエンティティ
     * @return アイテムボックスエンティティかどうか
     */
    public static boolean isDisposableFakeItemBox(Entity entity) {
        if (!(entity instanceof EnderCrystal)) {
            return false;
        }

        String customName = entity.getCustomName();
        if (customName == null || customName.equalsIgnoreCase("")) {
            return false;
        }

        if (!customName.matches("^(§.)*ItemBox(!|！)$")) {
            return false;
        }

        return true;
    }

    public static boolean isBananaEntity(Entity entity) {
        if (!(entity instanceof ArmorStand)) {
            return false;
        }

        String customName = entity.getCustomName();
        if (customName == null || customName.equalsIgnoreCase("")) {
            return false;
        }

        if (!customName.matches("^(§.)*" +  ItemEnum.BANANA.getDisplayName() + "$")) {
            return false;
        }

        return true;
    }

    public static boolean isRedTurtleEntity(Entity entity) {
        if (!(entity instanceof ArmorStand)) {
            return false;
        }

        String customName = entity.getCustomName();
        if (customName == null || customName.equalsIgnoreCase("")) {
            return false;
        }

        if (!customName.matches("^(§.)*" +  ItemEnum.RED_TURTLE.getDisplayName() + "$")) {
            return false;
        }

        return true;
    }

    /**
     * 引数chunkがバナナ、使い捨てのにせアイテムボックスを含んでいるかどうかを返す。
     * @param chunk チェックするチャンク
     * @return 妨害エンティティが設置されているチャンクかどうか
     */
    public static boolean containsJammerEntity(Chunk chunk) {
        for (Entity entity : chunk.getEntities()) {
            if (isBananaEntity(entity) || isDisposableFakeItemBox(entity)) {
                return true;
            }
        }

        return false;
    }

    //〓 Util - Collide 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static void collideBanana(Player player, Entity banana) {
        Racer racer = RaceManager.getRacer(player);
        banana.remove();

        if (player.getNoDamageTicks() == 0) {
            Circuit circuit = new Circuit();
            circuit.setCircuitName(racer.getCircuitName());
            MessageEnum.raceInteractBanana.sendConvertedMessage(player, circuit);
            racer.runNegativeItemSpeedTask(ItemEnum.BANANA.getEffectSecond(), ItemEnum.BANANA.getEffectLevel(), Sound.SLIME_WALK);
        }
    }

    public static void collideDisposableFakeItemBox(Player player, Entity fakeItemBox) {
        MessageEnum.raceInteractFakeItemBox.sendConvertedMessage(player, RaceManager.getCircuit(player.getUniqueId()));
        Util.createSafeExplosion(null, player.getLocation(), ItemEnum.FAKE_ITEMBOX.getHitDamage(), 1, 0.4F, 2.0F, Particle.EXPLOSION_LARGE);

        fakeItemBox.remove();
    }

    public static void collideNormalFakeItemBox(Player player, Entity fakeItemBox) {
        final Location itemBoxLocation = fakeItemBox.getLocation();

        collideDisposableFakeItemBox(player, fakeItemBox);
        Bukkit.getServer().getScheduler().runTaskLater(YPLKart.getInstance(), new Runnable() {
            public void run() {
                createFakeItemBox(itemBoxLocation);
            }
        }, 2 * 20L + 10L);
    }

    public static void collideItemBox(Player player, Entity itemBox) {
        // アイテムボックスの階級を取得
        final int itemBoxTier = getTierByItemBoxEntity(itemBox);

        // 階級が0以下の場合アイテムボックスエンティティではないためreturn
        if (itemBoxTier <= 0) {
            return;
        }

        Racer racer = RaceManager.getRacer(player);

        // アイテムボックスに接触して間もない場合はreturn
        if (racer.isItemBoxCooling()) {
            return;
        }

        // アイテムボックスに接触して間もない状態としてフラグを立てる
        racer.runItemBoxCoolingTask();

        // アイテムボックスを再生成するタスクの起動
        final Location itemBoxLocation = itemBox.getLocation();
        Bukkit.getScheduler().runTaskLater(YPLKart.getInstance(), new Runnable() {
            public void run() {
                createItemBox(itemBoxLocation, itemBoxTier);
            }
        }, 2 * 20L + 10L);

        itemBox.remove();

        /*
         * アイテムの付与
         */

        int denominator = RaceManager.getRacingPlayer(racer.getCircuitName()).size();
        if (denominator == 0) {
            denominator = 1;
        }

        int rank = RaceManager.getRank(player);
        if (rank == 0) {
            rank = 1;
        }

        int percent = 100 / (denominator / rank);
        int itemTier = 0;
        int settingsTier1 = (Integer) ConfigEnum.ITEM_TIER1.getValue();
        int settingsTier2 = (Integer) ConfigEnum.ITEM_TIER2.getValue();
        int settingsTier3 = (Integer) ConfigEnum.ITEM_TIER3.getValue();

        if (percent < settingsTier1) {
            itemTier = 1;
        } else if (settingsTier1 <= percent && percent < settingsTier2) {
            itemTier = 2;
        } else if (settingsTier2 <= percent && percent < settingsTier3) {
            itemTier = 3;
        } else {
            itemTier = 4;
        }

        // 接触したアイテムボックスが高級アイテムボックスの場合、アイテムのグレードにアイテムボックスのグレードを加算する
        if (1 < itemBoxTier) {
            itemTier += itemBoxTier;
            if (4 < itemTier) {
                itemTier = 4;
            }
        }

        ItemEnum.addRandomItemFromTier(player, itemTier);
    }
}
