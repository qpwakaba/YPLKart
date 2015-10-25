package com.github.erozabesu.yplkart.object;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.github.erozabesu.yplkart.ConfigManager;
import com.github.erozabesu.yplutillibrary.config.YamlLoader;
import com.github.erozabesu.yplutillibrary.util.CommentableYamlConfiguration;

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
        this.setConfigKey(configKey);

        YamlLoader configManager = ConfigManager.DISPLAY;

        this.setKartObjectKey(configManager.getString(configKey + ".kart_type", "Standard"));
        this.setWorldName(configManager.getString(configKey + ".world", "world"));
        this.setLocationX(configManager.getDouble(configKey + ".x", 0.0D));
        this.setLocationY(configManager.getDouble(configKey + ".y", 0.0D));
        this.setLocationZ(configManager.getDouble(configKey + ".z", 0.0D));
        this.setLocationPitch(configManager.getFloat(configKey + ".pitch", 0.0F));
        this.setLocationYaw(configManager.getFloat(configKey + ".yaw", 0.0F));
    }

    public void createDisplayKart(String configKey, Kart kart, Location location) {
        this.setConfigKey(configKey);

        this.setKartObjectKey(kart.getKartName());
        this.setWorldName(location.getWorld().getName());
        this.setLocationX(location.getX());
        this.setLocationY(location.getY());
        this.setLocationZ(location.getZ());
        this.setLocationPitch(location.getPitch());
        this.setLocationYaw(location.getYaw());

        this.saveConfiguration();
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

        CommentableYamlConfiguration config = ConfigManager.DISPLAY.getLocalConfig();

        //設定データに値を上書き
        config.set(configKey + ".kart_type", getKartObjectKey());
        config.set(configKey + ".world", getWorldName());
        config.set(configKey + ".x", getLocationX());
        config.set(configKey + ".y", getLocationY());
        config.set(configKey + ".z", getLocationZ());
        config.set(configKey + ".pitch", getLocationPitch());
        config.set(configKey + ".yaw", getLocationYaw());

        //設定データをローカルファイルに保存
        ConfigManager.DISPLAY.saveLocal();
    }

    /** ローカルコンフィグファイルから全データを削除する */
    public void deleteConfiguration() {
        ConfigManager.DISPLAY.getLocalConfig().set(getConfigKey(), null);

        //設定データをローカルファイルに保存
        ConfigManager.DISPLAY.saveLocal();
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
