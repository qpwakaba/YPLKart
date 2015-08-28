package com.github.erozabesu.yplkart.object;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
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

    /** 速度上昇アイテム使用中の最高速度 */
    private double boostedMaxSpeed;

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

    //〓 Main 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * コンストラクタ。<br>
     * 設定をメンバ変数へ格納する。<br>
     * デフォルト値はConfigManager.getDefaultConfig()からコンフィグキー「Standard.xxxxxx」の値を取得する。
     * @param key コンフィグキー
     */
    public Kart(String key) {
        setKartName(key);

        ConfigManager config = ConfigManager.KART_CONFIG;
        String defaultKey = "Standard";
        YamlConfiguration defaultConfig = config.getDefaultConfig();

        double defaultMountPositionOffset = defaultConfig.getDouble(defaultKey + ".mount_position_offset");
        setMountPositionOffset(config.getDouble(key + ".mount_position_offset", defaultMountPositionOffset));

        Material defaultDisplayMaterial = Material.getMaterial(defaultConfig.getString(defaultKey + ".display_material"));
        setDisplayMaterial(config.getMaterial(key + ".display_material", defaultDisplayMaterial));

        int defaultDisplayMaterialData = defaultConfig.getInt(defaultKey + ".display_material_data");
        setDisplayMaterialData(config.getByte(key + ".display_material_data", (byte) defaultDisplayMaterialData));

        double defaultWeight = defaultConfig.getDouble(defaultKey + ".weight");
        setWeight(config.getDouble(key + ".weight", defaultWeight));

        double defaultMaxSpeed = defaultConfig.getDouble(defaultKey + ".max_speed");
        setMaxSpeed(config.getDouble(key + ".max_speed", defaultMaxSpeed));

        double defaultBoostedMaxSpeed = defaultConfig.getDouble(defaultKey + ".boosted_max_speed");
        setBoostedMaxSpeed(config.getDouble(key + ".boosted_max_speed", defaultBoostedMaxSpeed));

        double defaultAcceleration = defaultConfig.getDouble(defaultKey + ".acceleration");
        setAcceleration(config.getDouble(key + ".acceleration", defaultAcceleration));

        double defaultClimbableHeight = defaultConfig.getDouble(defaultKey + ".climbable_height");
        setClimbableHeight((float) config.getDouble(key + ".climbable_height", defaultClimbableHeight));

        double defaultSpeedDecreaseOnDirt = defaultConfig.getDouble(defaultKey + ".speed_decrease_on_dirt");
        setSpeedDecreaseOnDirt(config.getDouble(key + ".speed_decrease_on_dirt", defaultSpeedDecreaseOnDirt));

        double defaultSpeedDecreaseOnDrift = defaultConfig.getDouble(defaultKey + ".speed_decrease_on_drift");
        setSpeedDecreaseOnDrift(config.getDouble(key + ".speed_decrease_on_drift", defaultSpeedDecreaseOnDrift));

        double defaultCorneringPower = defaultConfig.getDouble(defaultKey + ".default_cornering_power");
        setDefaultCorneringPower(config.getDouble(key + ".default_cornering_power", defaultCorneringPower));

        double defaultDriftCorneringPower = defaultConfig.getDouble(defaultKey + ".drift_cornering_power");
        setDriftCorneringPower(config.getDouble(key + ".drift_cornering_power", defaultDriftCorneringPower));
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
        setBoostedMaxSpeed(400.0D);
        setAcceleration(1.5D);
        setClimbableHeight(0.5F);
        setSpeedDecreaseOnDirt(5.0D);
        setSpeedDecreaseOnDrift(2.0D);
        setDefaultCorneringPower(1.0D);
        setDriftCorneringPower(5.5D);
    }

    //〓 Util 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

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
                String.valueOf(getBoostedMaxSpeed()),
                String.valueOf(getAcceleration()),
                String.valueOf(getDefaultCorneringPower()),
                String.valueOf(getDriftCorneringPower()),
                String.valueOf(getSpeedDecreaseOnDrift()),
                String.valueOf(getSpeedDecreaseOnDirt()),
                String.valueOf(getClimbableHeight())
        };

        return MessageEnum.tableKartParameter.getConvertedMessage(new Object[] {parameter});
    }

    //〓 Getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

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

    /** @return 速度上昇アイテム使用中の最高速度 */
    public double getBoostedMaxSpeed() {
        return boostedMaxSpeed;
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

    //〓 Setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @param kartName カート名 */
    private void setKartName(String kartName) {
        this.kartName = kartName;
    }

    /** @param mountPositionOffset プレイヤーの搭乗位置のY座標に対するオフセット */
    public void setMountPositionOffset(double mountPositionOffset) {
        this.mountPositionOffset = mountPositionOffset;
    }

    /** @param displayMaterial ブロックマテリアル */
    private void setDisplayMaterial(Material displayMaterial) {
        this.displayMaterial = displayMaterial;
    }

    /** @param displayMaterialData ブロックマテリアルデータ */
    private void setDisplayMaterialData(byte displayMaterialData) {
        this.displayMaterialData = displayMaterialData;
    }

    /** @param weight 重量 */
    private void setWeight(double weight) {
        this.weight = weight;
    }

    /** @param maxSpeed 最高速度 */
    private void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    /** @param boostedMaxSpeed 速度上昇アイテム使用中の最高速度 */
    public void setBoostedMaxSpeed(double boostedMaxSpeed) {
        this.boostedMaxSpeed = boostedMaxSpeed;
    }

    /** @param acceleration 加速力 */
    private void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }

    /** @param climbableHeight 乗り越えられるブロックの高さ */
    private void setClimbableHeight(float climbableHeight) {
        this.climbableHeight = climbableHeight;
    }

    /** @param speedDecreaseOnDirt ダート時の減速率 */
    private void setSpeedDecreaseOnDirt(double speedDecreaseOnDirt) {
        this.speedDecreaseOnDirt = speedDecreaseOnDirt;
    }

    /** @param speedDecreaseOnDrift ドリフト時の減速率 */
    private void setSpeedDecreaseOnDrift(double speedDecreaseOnDrift) {
        this.speedDecreaseOnDrift = speedDecreaseOnDrift;
    }

    /** @param defaultCorneringPower コーナリング力 */
    private void setDefaultCorneringPower(double defaultCorneringPower) {
        this.defaultCorneringPower = defaultCorneringPower;
    }

    /** @param driftCorneringPower ドリフト時のコーナリング力 */
    private void setDriftCorneringPower(double driftCorneringPower) {
        this.driftCorneringPower = driftCorneringPower;
    }
}
