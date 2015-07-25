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

    //〓 main 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public DisplayKart(String configKey) {
        setConfigKey(configKey);

        ConfigManager configManager = ConfigManager.DISPLAY_KART_CONFIG;

        setKartObjectKey(configManager.getString(configKey + ".kart_type"));
        setWorldName(configManager.getString(configKey + ".world"));
        setLocationX(configManager.getDouble(configKey + ".x"));
        setLocationY(configManager.getDouble(configKey + ".y"));
        setLocationZ(configManager.getDouble(configKey + ".z"));
        setLocationPitch(configManager.getFloat(configKey + ".pitch"));
        setLocationYaw(configManager.getFloat(configKey + ".yaw"));
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

    //〓 getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

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

    //〓 setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @param configKey セットするコンフィグキー */
    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    /** @param kartObjectKey セットするカートオブジェクト */
    public void setKartObjectKey(String kartObjectKey) {
        this.kartObjectKey = kartObjectKey;
    }

    /** @param worldName セットするワールド名 */
    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    /** @param locationX セットするx座標 */
    public void setLocationX(double locationX) {
        this.locationX = locationX;
    }

    /** @param locationY セットするy座標 */
    public void setLocationY(double locationY) {
        this.locationY = locationY;
    }

    /** @param locationZ セットするz座標 */
    public void setLocationZ(double locationZ) {
        this.locationZ = locationZ;
    }

    /** @param locationYaw セットするYaw */
    public void setLocationYaw(float locationYaw) {
        this.locationYaw = locationYaw;
    }

    /** @param locationPitch セットするPitch */
    public void setLocationPitch(float locationPitch) {
        this.locationPitch = locationPitch;
    }

    //〓 file 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * メンバ変数の設定データをローカルコンフィグに新しい値として上書きし
     * ローカルファイルに保存する
     * ゲーム内から動的に変更される設定データを扱うため用意されている
     * コマンド等で設定データが変更された場合逐一上書きすること
     */
    public void saveConfiguration() {
        String configKey = getConfigKey();

        ConfigManager config = ConfigManager.RACEDATA_CONFIG;

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
        ConfigManager config = ConfigManager.RACEDATA_CONFIG;

        config.setValue(getConfigKey(), null);

        //設定データをローカルファイルに保存
        config.saveConfiguration();
    }

    //〓 util 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

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
}
