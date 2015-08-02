package com.github.erozabesu.yplkart.object;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.erozabesu.yplkart.ConfigManager;
import com.github.erozabesu.yplkart.data.MessageEnum;

/**
 * Kartの各設定を格納するオブジェクトクラス
 * @author erozabesu
 */
public class Kart {

    /** カート名 */
    private String kartName;

    /** プレイヤーの搭乗位置のY座標に対するオフセット */
    private double mountPositionOffset;

    /** ブロックマテリアル */
    private Material displayMaterial;

    /** ブロックマテリアルデータ */
    private byte displayMaterialData;

    /** 重量 */
    private double weight;

    /** 最高速度 */
    private double maxSpeed;

    /** 加速力 */
    private double acceleration;

    /** 乗り越えられるブロックの高さ */
    private float climbableHeight;

    /** ダート時の減速率 */
    private double speedDecreaseOnDirt;

    /** ドリフト時の減速率 */
    private double speedDecreaseOnDrift;

    /** コーナリング力 */
    private double defaultCorneringPower;

    /** ドリフト時のコーナリング力 */
    private double driftCorneringPower;

    //〓 main 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * コンストラクタ
     * 設定をメンバ変数へ格納する
     * @param key コンフィグキー
     */
    public Kart(String key) {
        setKartName(key);

        ConfigManager config = ConfigManager.KART_CONFIG;

        setMountPositionOffset(config.getDouble(key + ".mount_position_offset"));

        setDisplayMaterial(config.getMaterial(key + ".display_material"));
        setDisplayMaterialData(config.getByte(key + ".display_material_data"));

        setWeight(config.getDouble(key + ".weight"));
        setMaxSpeed(config.getDouble(key + ".max_speed"));
        setAcceleration(config.getDouble(key + ".acceleration"));
        setClimbableHeight((float) config.getDouble(key + ".climbable_height"));
        setSpeedDecreaseOnDirt(config.getDouble(key + ".speed_decrease_on_dirt"));
        setSpeedDecreaseOnDrift(config.getDouble(key + ".speed_decrease_on_drift"));
        setDefaultCorneringPower(config.getDouble(key + ".default_cornering_power"));
        setDriftCorneringPower(config.getDouble(key + ".drift_cornering_power"));
    }

    /**
     * コンフィグファイルの影響のないカートオブジェクトを生成するコンストラクタ<br>
     * 主にキラーのような、一時的にカードオブジェクトの見た目を変更するアイテム用
     * @param kartName カート名
     * @param displayMaterial ディスプレイするアイテムのマテリアル
     * @param displayMaterialData ディスプレイするアイテムのマテリアルデータ
     */
    public Kart(String kartName, Material displayMaterial, byte displayMaterialData) {
        setKartName(kartName);

        setMountPositionOffset(-1.4D);

        setDisplayMaterial(displayMaterial);
        setDisplayMaterialData(displayMaterialData);

        setWeight(1.0D);
        setMaxSpeed(250.0D);
        setAcceleration(1.5D);
        setClimbableHeight(0.5F);
        setSpeedDecreaseOnDirt(5.0D);
        setSpeedDecreaseOnDrift(2.0D);
        setDefaultCorneringPower(1.0D);
        setDriftCorneringPower(5.5D);
    }

    //〓 getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @return kartName カート名 */
    public String getKartName() {
        return kartName;
    }

    /** @return mountPositionOffset プレイヤーの搭乗位置のY座標に対するオフセット */
    public double getMountPositionOffset() {
        return mountPositionOffset;
    }

    /** @return displayMaterial ブロックマテリアル */
    public Material getDisplayMaterial() {
        return displayMaterial;
    }

    /** @return displayMaterialData ブロックマテリアルデータ */
    public byte getDisplayMaterialData() {
        return displayMaterialData;
    }

    /** @return weight 重量 */
    public double getWeight() {
        return weight;
    }

    /** @return maxSpeed 最高速度 */
    public double getMaxSpeed() {
        return maxSpeed;
    }

    /** @return acceleration 加速力 */
    public double getAcceleration() {
        return acceleration;
    }

    /** @return climbableHeight 乗り越えられるブロックの高さ */
    public float getClimbableHeight() {
        return climbableHeight;
    }

    /** @return speedDecreaseOnDirt ダート時の減速率 */
    public double getSpeedDecreaseOnDirt() {
        return speedDecreaseOnDirt;
    }

    /** @return speedDecreaseOnDrift ドリフト時の減速率 */
    public double getSpeedDecreaseOnDrift() {
        return speedDecreaseOnDrift;
    }

    /** @return defaultCorneringPower コーナリング力 */
    public double getDefaultCorneringPower() {
        return defaultCorneringPower;
    }

    /** @return driftCorneringPower ドリフト時のコーナリング力 */
    public double getDriftCorneringPower() {
        return driftCorneringPower;
    }

    //〓 setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @param kartName セットするカート名 */
    private void setKartName(String kartName) {
        this.kartName = kartName;
    }

    /** @param mountPositionOffset プレイヤーの搭乗位置のY座標に対するオフセット */
    public void setMountPositionOffset(double mountPositionOffset) {
        this.mountPositionOffset = mountPositionOffset;
    }

    /** @param displayMaterial セットするブロックマテリアル */
    private void setDisplayMaterial(Material displayMaterial) {
        this.displayMaterial = displayMaterial;
    }

    /** @param displayMaterialData セットするブロックマテリアルデータ */
    private void setDisplayMaterialData(byte displayMaterialData) {
        this.displayMaterialData = displayMaterialData;
    }

    /** @param weight セットする重量 */
    private void setWeight(double weight) {
        this.weight = weight;
    }

    /** @param maxSpeed セットする最高速度 */
    private void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    /** @param acceleration セットする加速力 */
    private void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }

    /** @param climbableHeight セットする乗り越えられるブロックの高さ */
    private void setClimbableHeight(float climbableHeight) {
        this.climbableHeight = climbableHeight;
    }

    /** @param speedDecreaseOnDirt セットするダート時の減速率 */
    private void setSpeedDecreaseOnDirt(double speedDecreaseOnDirt) {
        this.speedDecreaseOnDirt = speedDecreaseOnDirt;
    }

    /** @param speedDecreaseOnDrift セットするドリフト時の減速率 */
    private void setSpeedDecreaseOnDrift(double speedDecreaseOnDrift) {
        this.speedDecreaseOnDrift = speedDecreaseOnDrift;
    }

    /** @param defaultCorneringPower セットするコーナリング力 */
    private void setDefaultCorneringPower(double defaultCorneringPower) {
        this.defaultCorneringPower = defaultCorneringPower;
    }

    /** @param driftCorneringPower セットするドリフト時のコーナリング力 */
    private void setDriftCorneringPower(double driftCorneringPower) {
        this.driftCorneringPower = driftCorneringPower;
    }

    //〓 do 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @return メニュー用アイテム */
    public ItemStack getMenuItem() {
        ItemStack item = new ItemStack(getDisplayMaterial(), 1, (short) 10, getDisplayMaterialData());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(getKartName());
        meta.setLore(MessageEnum.replaceLine(MessageEnum.replaceChatColor(getParameter())));

        item.setItemMeta(meta);

        return item;
    }

    /** @return カートのパラメーター一覧 */
    public String getParameter() {
        String[] parameter = new String[] {
                String.valueOf(getWeight()),
                String.valueOf(getMaxSpeed()),
                String.valueOf(getAcceleration()),
                String.valueOf(getDefaultCorneringPower()),
                String.valueOf(getDriftCorneringPower()),
                String.valueOf(getSpeedDecreaseOnDrift()),
                String.valueOf(getSpeedDecreaseOnDirt()),
                String.valueOf(getClimbableHeight())
        };

        return MessageEnum.tableKartParameter.getConvertedMessage(new Object[] {parameter});
    }
}
