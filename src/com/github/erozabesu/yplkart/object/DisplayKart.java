package com.github.erozabesu.yplkart.object;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.github.erozabesu.yplkart.ConfigManager;

public class DisplayKart {

    /** コンフィグキー/UUID */
    private String configKey;

    /** パラメータを引き継ぐカートオブジェクトキー */
    private String kartObjectKey;

    /** ワールド名 */
    private String worldName;

    /** x座標 */
    private double locationX;

    /** y座標 */
    private double locationY;

    /** z座標 */
    private double locationZ;

    /** Pitch */
    private float locationPitch;

    /** Yaw */
    private float locationYaw;

    //〓 Main 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * コンストラクタ。<br>
     * 設定をメンバ変数へ格納する。<br>
     * ユーザ側で新規に追加するデータを扱うため、ConfigManager.getDefaultConfig()には何も記述されていない。<br>
     * そのため、ConfigManager.getXxxx(String configKey, Object defaultValue)メソッドのdefaultValueには手動で固定値を渡す。
     * @param configKey コンフィグキー
     */
    public DisplayKart(String configKey) {
        setConfigKey(configKey);

        ConfigManager configManager = ConfigManager.DISPLAY_KART_CONFIG;

        setKartObjectKey(configManager.getString(configKey + ".kart_type", "Standard"));
        setWorldName(configManager.getString(configKey + ".world", "world"));
        setLocationX(configManager.getDouble(configKey + ".x", 0.0D));
        setLocationY(configManager.getDouble(configKey + ".y", 0.0D));
        setLocationZ(configManager.getDouble(configKey + ".z", 0.0D));
        setLocationPitch(configManager.getFloat(configKey + ".pitch", 0.0F));
        setLocationYaw(configManager.getFloat(configKey + ".yaw", 0.0F));
    }

    public void createDisplayKart(String configKey, Kart kart, Location location) {
        setConfigKey(configKey);

        setKartObjectKey(kart.getKartName());
        setWorldName(location.getWorld().getName());
        setLocationX(location.getX());
        setLocationY(location.getY());
        setLocationZ(location.getZ());
        setLocationPitch(location.getPitch());
        setLocationYaw(location.getYaw());

        saveConfiguration();
    }

    //〓 File 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * メンバ変数の設定データをローカルコンフィグに新しい値として上書きし
     * ローカルファイルに保存する
     * ゲーム内から動的に変更される設定データを扱うため用意されている
     * コマンド等で設定データが変更された場合逐一上書きすること
     */
    public void saveConfiguration() {
        String configKey = getConfigKey();

        ConfigManager config = ConfigManager.DISPLAY_KART_CONFIG;

        //設定データに値を上書き
        config.setValue(configKey + ".kart_type", getKartObjectKey());
        config.setValue(configKey + ".world", getWorldName());
        config.setValue(configKey + ".x", getLocationX());
        config.setValue(configKey + ".y", getLocationY());
        config.setValue(configKey + ".z", getLocationZ());
        config.setValue(configKey + ".pitch", getLocationPitch());
        config.setValue(configKey + ".yaw", getLocationYaw());

        //設定データをローカルファイルに保存
        config.saveConfiguration();
    }

    /** ローカルコンフィグファイルから全データを削除する */
    public void deleteConfiguration() {
        ConfigManager config = ConfigManager.DISPLAY_KART_CONFIG;

        config.setValue(getConfigKey(), null);

        //設定データをローカルファイルに保存
        config.saveConfiguration();
    }

    //〓 Util 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @param location セットする座標 */
    public void setLocation(Location location) {
        setWorldName(location.getWorld().getName());
        setLocationX(location.getX());
        setLocationY(location.getY());
        setLocationZ(location.getZ());
        setLocationPitch(location.getPitch());
        setLocationYaw(location.getYaw());
    }

    /** @return 座標 */
    public Location getLocation() {
        World world = Bukkit.getWorld(getWorldName());

        if (world == null) {
            return null;
        }

        return new Location(world
                , getLocationX(), getLocationY(), getLocationZ()
                , getLocationYaw(), getLocationPitch());
    }

    //〓 Getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @return configKey コンフィグキー */
    public String getConfigKey() {
        return configKey;
    }

    /** @return kartObjectKey カートオブジェクト */
    public String getKartObjectKey() {
        return kartObjectKey;
    }

    /** @return worldName ワールド名 */
    public String getWorldName() {
        return worldName;
    }

    /** @return locationX x座標 */
    public double getLocationX() {
        return locationX;
    }

    /** @return locationY y座標 */
    public double getLocationY() {
        return locationY;
    }

    /** @return locationZ z座標 */
    public double getLocationZ() {
        return locationZ;
    }

    /** @return locationYaw Yaw */
    public float getLocationYaw() {
        return locationYaw;
    }

    /** @return locationPitch Pitch */
    public float getLocationPitch() {
        return locationPitch;
    }

    //〓 Setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @param configKey コンフィグキー */
    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    /** @param kartObjectKey カートオブジェクト */
    public void setKartObjectKey(String kartObjectKey) {
        this.kartObjectKey = kartObjectKey;
    }

    /** @param worldName ワールド名 */
    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    /** @param locationX x座標 */
    public void setLocationX(double locationX) {
        this.locationX = locationX;
    }

    /** @param locationY y座標 */
    public void setLocationY(double locationY) {
        this.locationY = locationY;
    }

    /** @param locationZ z座標 */
    public void setLocationZ(double locationZ) {
        this.locationZ = locationZ;
    }

    /** @param locationYaw Yaw */
    public void setLocationYaw(float locationYaw) {
        this.locationYaw = locationYaw;
    }

    /** @param locationPitch Pitch */
    public void setLocationPitch(float locationPitch) {
        this.locationPitch = locationPitch;
    }
}
