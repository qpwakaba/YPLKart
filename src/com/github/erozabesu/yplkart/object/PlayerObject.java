package com.github.erozabesu.yplkart.object;

import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.github.erozabesu.yplkart.YPLKart;

/**
 * プレイヤーの基本情報を格納するクラス
 * @author erozabesu
 */
public class PlayerObject {

    /** UUID */
    private UUID uuid;

    /** ゲームモード */
    private GameMode gameMode;

    /** 座標 */
    private Location location;

    /** 最大体力 */
    private double maxHealth;

    /** 体力 */
    private double health;

    /** 空腹度 */
    private int hunger;

    /** 歩行速度 */
    private float walkSpeed;

    /** 経験値LV */
    private int level;

    /** 経験値 */
    private float exp;

    /** インベントリ */
    private ArrayList<ItemStack> inventory;

    /** アーマーインベントリ */
    private ArrayList<ItemStack> armorContents;

    /**
     * プレイヤーがスニークしているかどうか
     * @see com.github.erozabesu.yplkart.override.PlayerChannelHandler#channelRead(ChannelHandlerContext , Object)
     */
    private boolean isSneaking;

    /**
     *  リスポーン時のプレイヤーの向き<br>
     *  issue #88 の変更で削除予定
     */
    private float lastYaw;

    //〓 Main 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public PlayerObject(UUID uuid) {
        setUUID(uuid);

        initializePlayerObject();

        setSneaking(false);
        setLastYaw(0);
    }

    public void initializePlayerObject() {
        Player player = getPlayer();
        if (player == null) {
            return;
        }

        setGameMode(player.getGameMode());
        setLocation(player.getLocation());
        setMaxHealth(player.getMaxHealth());

        setHealth(player.getHealth());
        setHunger(player.getFoodLevel());
        setWalkSpeed(player.getWalkSpeed());
        setLevel(player.getLevel());
        setExp(player.getExp());
        setInventory(new ArrayList<ItemStack>(Arrays.asList(player.getInventory().getContents())));
        setArmorContents(new ArrayList<ItemStack>(Arrays.asList(player.getInventory().getArmorContents())));
    }

    //〓 Getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @return UUID */
    public UUID getUUID() {
        return this.uuid;
    }

    /** @return ゲームモード */
    public GameMode getGameMode() {
        return gameMode;
    }

    /** @return 座標 */
    public Location getLocation() {
        return this.location;
    }

    /** @return 最大体力 */
    public double getMaxHealth() {
        return this.maxHealth;
    }

    /** @return 体力 */
    public double getHealth() {
        return this.health;
    }

    /** @return 空腹度 */
    public int getHunger() {
        return this.hunger;
    }

    /** @return 歩行速度 */
    public float getWalkSpeed() {
        return this.walkSpeed;
    }

    /** @return 経験値LV */
    public int getLevel() {
        return this.level;
    }

    /** @return 経験値 */
    public float getExp() {
        return this.exp;
    }

    /** @return インベントリ */
    public ArrayList<ItemStack> getInventory() {
        return this.inventory;
    }

    /** @return アーマーインベントリ */
    public ArrayList<ItemStack> getArmorContents() {
        return this.armorContents;
    }

    /** @return スニークしているかどうか */
    public boolean isSneaking() {
        return this.isSneaking;
    }

    /** @return リスポーン時のプレイヤーの向き */
    public float getLastYaw() {
        return this.lastYaw;
    }

    //〓 Setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @param uuid UUID */
    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    /** @param gameMode ゲームモード */
    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    /** @param location 座標 */
    public void setLocation(Location location) {
        this.location = location;
    }

    /** @param maxHealth 最大体力 */
    public void setMaxHealth(double maxHealth) {
        this.maxHealth = maxHealth;
    }

    /** @param health 体力 */
    public void setHealth(double health) {
        this.health = health;
    }

    /** @param hunger 空腹度 */
    public void setHunger(int hunger) {
        this.hunger = hunger;
    }

    /** @param walkSpeed 歩行速度 */
    public void setWalkSpeed(float walkSpeed) {
        this.walkSpeed = walkSpeed;
    }

    /** @param level 経験値LV */
    public void setLevel(int level) {
        this.level = level;
    }

    /** @param exp 経験値 */
    public void setExp(float exp) {
        this.exp = exp;
    }

    /** @param inventory インベントリ */
    public void setInventory(ArrayList<ItemStack> inventory) {
        this.inventory = inventory;
    }

    /** @param armorContents アーマーインベントリ */
    public void setArmorContents(ArrayList<ItemStack> armorContents) {
        this.armorContents = armorContents;
    }

    /** @param スニークしているかどうか */
    public void setSneaking(boolean isSneaking) {
        this.isSneaking = isSneaking;
    }

    /** @param value リスポーン時のプレイヤーの向き */
    public void setLastYaw(float value) {
        this.lastYaw = value;
    }

    //〓 Util 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @return プレイヤー */
    public Player getPlayer() {
        return Bukkit.getPlayer(getUUID());
    }

    /** ゲームモードを復元する */
    public void recoveryGameMode() {
        Player player = getPlayer();
        if (player == null) {
            return;
        }
        player.setGameMode(getGameMode());
    }

    /** 座標へテレポートする */
    public void recoveryLocation() {
        Player player = getPlayer();
        if (player == null) {
            return;
        }
        player.teleport(getLocation());
    }

    /** フィジカルを復元する */
    public void recoveryPhysical() {
        final Player player = getPlayer();
        if (player == null) {
            return;
        }

        player.setMaxHealth(getMaxHealth());
        player.setFoodLevel(getHunger());
        player.setWalkSpeed(getWalkSpeed());

        //最大体力の変更は1チック以上遅延するため、体力の変更も遅延させる必要がある
        Bukkit.getScheduler().runTaskLater(YPLKart.getInstance(), new Runnable() {
            public void run() {
                player.setHealth(getHealth());
            }
        }, 5L);
    }

    /** 経験値を復元する */
    public void recoveryExp() {
        Player player = getPlayer();
        if (player == null) {
            return;
        }

        player.setLevel(getLevel());
        player.setExp(getExp());
    }

    /** インベントリを復元する */
    public void recoveryInventory() {
        Player player = getPlayer();
        if (player == null) {
            return;
        }

        player.getInventory().clear();

        if (!getInventory().isEmpty()) {
            PlayerInventory inv = player.getInventory();
            for (int i = 0; i < 36; i++) {
                inv.setItem(i, getInventory().get(i));
            }
        }

        if (!getArmorContents().isEmpty()) {
            PlayerInventory inv = player.getInventory();
            if (getArmorContents().get(0) != null) {
                inv.setHelmet(getArmorContents().get(3));
            }
            if (getArmorContents().get(1) != null) {
                inv.setChestplate(getArmorContents().get(2));
            }
            if (getArmorContents().get(2) != null) {
                inv.setLeggings(getArmorContents().get(1));
            }
            if (getArmorContents().get(3) != null){
                inv.setBoots(getArmorContents().get(0));
            }
        }
    }

    /** 全パラメータを復元する */
    public void recoveryAll() {
        if (getPlayer() == null) {
            return;
        }

        recoveryLocation();
        recoveryPhysical();
        recoveryExp();
        recoveryInventory();
        recoveryGameMode();
    }

    /** レース用の初期パラメータを適用する */
    public void applyRaceParameter() {
        Player player = this.getPlayer();
        if (player == null) {
            return;
        }

        //現プレイヤーデータの保存
        initializePlayerObject();

        player.setGameMode(GameMode.ADVENTURE);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setHealth(player.getMaxHealth());
        player.setLevel(0);
        player.setExp(0);
        player.setFoodLevel(20);
    }
}
