package com.github.erozabesu.yplkart;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.erozabesu.yplkart.data.CharacterConfig;
import com.github.erozabesu.yplkart.data.CircuitConfig;
import com.github.erozabesu.yplkart.data.ConfigEnum;
import com.github.erozabesu.yplkart.data.DisplayKartConfig;
import com.github.erozabesu.yplkart.data.ItemEnum;
import com.github.erozabesu.yplkart.data.KartConfig;
import com.github.erozabesu.yplkart.data.MessageEnum;
import com.github.erozabesu.yplutillibrary.util.CommentableYamlConfiguration;
import com.github.erozabesu.yplutillibrary.util.CommonUtil;

/**
 * 各コンフィグファイルのファイル管理を行うクラス
 * 設定データの取得はこのクラスからではなく
 * Enum、Configサフィックスの付いているクラスからstaticに取得すること
 *
 * Enumクラスは、ユーザ側で要素数を変更できない静的なコンフィグを扱う
 * コンフィグはEnumで管理する
 *
 * Configクラスは、ユーザ側で要素数を変更できる動的なコンフィグを扱う
 * コンフィグはオブジェクトで管理する
 *
 * @author erozabesu
 */
public enum ConfigManager {
    CONFIG_ENUM("config.yml"),
    ITEM_ENUM("item.yml"),
    MESSAGE_ENUM("message.yml"),

    CHARACTER_CONFIG("character.yml"),
    KART_CONFIG("kart.yml"),
    RACEDATA_CONFIG("racedata.yml"),
    DISPLAY_KART_CONFIG("displaykart.yml");

    /** コンフィグファイルを保存するローカルディレクトリ */
    private File dataDirectory = YPLKart.getInstance().getDataFolder();

    /** ローカルコンフィグファイル名 */
    private String localConfigFileName;

    /** ローカルコンフィグファイル */
    private File localConfigFile;

    /** ローカルコンフィグファイルの設定データ */
    private CommentableYamlConfiguration localConfig;

    /** .jar内のデフォルトコンフィグファイルの設定データ */
    private YamlConfiguration defaultConfig;

    //〓 Main 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * コンストラクタ
     * ローカルファイルの読み込みはstatic.reload()メソッドで明示的に行う
     * static.reload()はYPLKart.onEnable()から実行される
     * @param localConfigFileName ローカルコンフィグファイル名
     */
    private ConfigManager(String localConfigFileName) {
        this.setLocalConfigFileName(localConfigFileName);
        this.setLocalConfigFile(new File(YPLKart.getInstance().getDataFolder() + File.separator + localConfigFileName));
        this.setDefaultConfig(CommonUtil.getYamlConfigFromResource(YPLKart.getInstance(), "resources/" + this.getLocalConfigFileName()));
    }

    private void loadConfig() {
        this.CreateConfig();

        CommentableYamlConfiguration config = new CommentableYamlConfiguration();
        try {
            config.load(this.getLocalConfigFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }

        this.setLocalConfig(config);
        this.setDefaultConfig(CommonUtil.getYamlConfigFromResource(YPLKart.getInstance(), "resources/" + this.getLocalConfigFileName()));
    }

    /** 設定データをローカルコンフィグファイルに保存する */
    public void saveConfig() {
        try {
            this.getLocalConfig().save(this.getLocalConfigFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * コンフィグファイルの生成が必要な状態かどうかを判別し必要ならファイルを生成する
     * @return ファイルのコピーに成功したかどうか
     */
    public boolean CreateConfig() {
        //既にローカルファイルが存在している
        if (this.getLocalConfigFile().exists()) {
            return false;
        }

        /*
         * jarファイルからコンフィグファイルのコピーを試みる
         * コピー元のファイルが存在しない場合プラグインを停止する
         */
        if (!CommonUtil.copyResource(YPLKart.getInstance(), "resources/" + this.getLocalConfigFileName(), this.getLocalConfigFileName())) {
            MessageEnum.sendAbsolute(null, "[" + YPLKart.PLUGIN_NAME + "] v."
                    + YPLKart.PLUGIN_VERSION + " "
                    + getLocalConfigFileName() + " was not found in jar file");
            YPLKart.getInstance().onDisable();
            return false;
        }

        return true;
    }

    /** 全ローカルファイル及び設定データを再読み込みし、オブジェクトを再生成する */
    public static void reloadAllConfig() {

        // 以下順序を変更しないこと

        // コンフィグ値を読み込む
        reloadAllFile();

        // 基本設定を読み込む
        ConfigEnum.reload();

        // アイテム設定を読み込む
        ItemEnum.reload();

        // キャラクター設定を読み込む
        CharacterConfig.reload();

        // カート設定を読み込む
        KartConfig.reload();

        // ディスプレイカート設定を読み込む
        DisplayKartConfig.reload();

        // サーキット設定を読み込む
        CircuitConfig.reload();

        // テキストメッセージを読み込む
        // 他オブジェクトの名称等が含まれるため最後に処理する
        MessageEnum.reload();

        saveAllFile();
    }

    /** ローカルの全コンフィグファイルから設定データを再読み込みする */
    private static void reloadAllFile() {
        for (ConfigManager configManager : ConfigManager.values()) {
            configManager.loadConfig();
        }
    }

    /** 全設定データをローカルコンフィグファイルに保存する */
    public static void saveAllFile() {
        for (ConfigManager manager : ConfigManager.values()) {
            try {
                manager.getLocalConfig().save(manager.getLocalConfigFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //〓 Util 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * ローカルコンフィグの設定データから引数configKeyをパスに持つ値を返す。<br>
     * config.yml以外には利用しないこと。
     * @param configKey ローカルコンフィグ値のパス
     * @return 取得したローカルコンフィグの値
     */
    public <T> Object getValue(String configKey) {
        //ローカルコンフィグにキーが含まれていない場合書き込む
        if (!getLocalConfig().contains(configKey)) {
            Object defaultValue = getDefaultConfig().get(configKey);
            if (defaultValue != null) {
                getLocalConfig().set(configKey, defaultValue);
            }
        }

        return getLocalConfig().get(configKey);
    }

    /**
     * ローカルコンフィグの設定データから引数configKeyをパスに持つString値を返す。<br>
     * 一致する値が存在しない場合は引数defaultValueを保存し返す。
     * @param configKey ローカルコンフィグ値のパス
     * @return 取得したローカルコンフィグのString値
     */
    public String getString(String configKey, String defaultValue) {
        //ローカルコンフィグにキーが含まれていない場合引数defaultValueを書き込む
        if (!this.getLocalConfig().contains(configKey)) {
            if (defaultValue != null) {
                this.getLocalConfig().set(configKey, defaultValue);
            } else {
                this.getLocalConfig().set(configKey, "");
            }
        }

        return String.valueOf(this.getLocalConfig().get(configKey));
    }

    /**
     * ローカルコンフィグの設定データから引数configKeyキーをパスに持つint値を返す。<br>
     * 一致する値が存在しない場合は引数defaultValueを保存し返す。
     * @param configKey ローカルコンフィグ値のパス
     * @return 取得したローカルコンフィグのint値
     */
    public int getInteger(String configKey, Integer defaultValue) {
        //ローカルコンフィグにキーが含まれていない場合書き込む
        if (!getLocalConfig().contains(configKey)) {
            if (defaultValue != null) {
                getLocalConfig().set(configKey, defaultValue);
            } else {
                getLocalConfig().set(configKey, 0);
            }
        }

        try {
            return Integer.valueOf(String.valueOf(getLocalConfig().get(configKey)));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * ローカルコンフィグの設定データから引数configKeyキーをパスに持つfloat値を返す。<br>
     * 一致する値が存在しない場合は引数defaultValueを保存し返す。
     * @param configKey ローカルコンフィグ値のパス
     * @return 取得したローカルコンフィグのfloat値
     */
    public float getFloat(String configKey, Float defaultValue) {
        //ローカルコンフィグにキーが含まれていない場合書き込む
        if (!getLocalConfig().contains(configKey)) {
            if (defaultValue != null) {
                getLocalConfig().set(configKey, defaultValue);
            } else {
                getLocalConfig().set(configKey, 0.0F);
            }
        }

        try {
            return Float.valueOf(String.valueOf(getLocalConfig().get(configKey)));
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0F;
        }
    }

    /**
     * ローカルコンフィグの設定データから引数configKeyキーをパスに持つdouble値を返す。<br>
     * 一致する値が存在しない場合は引数defaultValueを保存し返す。
     * @param configKey ローカルコンフィグ値のパス
     * @return 取得したローカルコンフィグのdouble値
     */
    public double getDouble(String configKey, Double defaultValue) {
        //ローカルコンフィグにキーが含まれていない場合書き込む
        if (!getLocalConfig().contains(configKey)) {
            if (defaultValue != null) {
                getLocalConfig().set(configKey, defaultValue);
            } else {
                getLocalConfig().set(configKey, 0.0D);
            }
        }

        try {
            return Double.valueOf(String.valueOf(getLocalConfig().get(configKey)));
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0D;
        }
    }

    /**
     * ローカルコンフィグの設定データから引数configKeyキーをパスに持つbyte値を返す。<br>
     * 一致する値が存在しない場合は引数defaultValueを保存し返す。
     * @param configKey ローカルコンフィグ値のパス
     * @return 取得したローカルコンフィグのbyte値
     */
    public byte getByte(String configKey, Byte defaultValue) {
        //ローカルコンフィグにキーが含まれていない場合書き込む
        if (!getLocalConfig().contains(configKey)) {
            if (defaultValue != null) {
                getLocalConfig().set(configKey, defaultValue);
            } else {
                getLocalConfig().set(configKey, (byte) 0);
            }
        }

        try {
            return Byte.valueOf(String.valueOf(getLocalConfig().get(configKey)));
        } catch (Exception e) {
            e.printStackTrace();
            return (byte) 0;
        }
    }

    /**
     * ローカルコンフィグの設定データから引数configKeyキーをパスに持つboolean値を返す。<br>
     * 一致する値が存在しない場合は引数defaultValueを保存し返す。
     * @param configKey ローカルコンフィグ値のパス
     * @return 取得したローカルコンフィグのboolean値
     */
    public boolean getBoolean(String configKey, Boolean defaultValue) {
        //ローカルコンフィグにキーが含まれていない場合書き込む
        if (!getLocalConfig().contains(configKey)) {
            if (defaultValue != null) {
                getLocalConfig().set(configKey, defaultValue);
            } else {
                getLocalConfig().set(configKey, false);
            }
        }

        try {
            return Boolean.valueOf(String.valueOf(getLocalConfig().get(configKey)));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * ローカルコンフィグの設定データから引数configKeyキーをパスに持つMaterial値を返す。<br>
     * 一致する値が存在しない場合は引数defaultValueを保存し返す。
     * @param configKey ローカルコンフィグ値のパス
     * @return 取得したローカルコンフィグのMaterial値
     */
    public Material getMaterial(String configKey, Material defaultValue) {
        //ローカルコンフィグにキーが含まれていない場合書き込む
        if (!getLocalConfig().contains(configKey)) {
            if (defaultValue != null) {
                getLocalConfig().set(configKey, defaultValue.name());
            } else {
                getLocalConfig().set(configKey, "STONE");
            }
        }

        try {
            return Material.getMaterial(String.valueOf(getLocalConfig().get(configKey)).toUpperCase());
        } catch (Exception e) {
            e.printStackTrace();
            return Material.STONE;
        }
    }

    /**
     * ローカルコンフィグの設定データから引数configKeyキーをパスに持つSound値を返す。<br>
     * 一致する値が存在しない場合は引数defaultValueを保存し返す。
     * @param configKey ローカルコンフィグ値のパス
     * @return 取得したローカルコンフィグのSound値
     */
    public Sound getSound(String configKey, Sound defaultValue) {
        //ローカルコンフィグにキーが含まれていない場合書き込む
        if (!getLocalConfig().contains(configKey)) {
            if (defaultValue != null) {
                getLocalConfig().set(configKey, defaultValue);
            } else {
                getLocalConfig().set(configKey, "CLICK");
            }
        }

        try {
            return Sound.valueOf(String.valueOf(getLocalConfig().get(configKey)).toUpperCase());
        } catch (Exception e) {
            return Sound.CLICK;
        }
    }

    /**
     * ローカルコンフィグの設定データを引数newValueで上書きする
     * @param configKey コンフィグキー
     * @param newValue 新しい値
     */
    public void setValue(String configKey, Object newValue) {
        this.getLocalConfig().set(configKey, newValue);
    }

    //〓 Getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @return コンフィグファイルを保存するローカルディレクトリ */
    public File getDataDirectory() {
        return this.dataDirectory;
    }

    /** @return ローカルコンフィグファイル名 */
    public String getLocalConfigFileName() {
        return this.localConfigFileName;
    }

    /** @return ローカルコンフィグファイル */
    public File getLocalConfigFile() {
        return this.localConfigFile;
    }

    /** @return ローカルコンフィグファイルの設定データ */
    public CommentableYamlConfiguration getLocalConfig() {
        return this.localConfig;
    }

    /** @return デフォルトコンフィグファイルの設定データ */
    public YamlConfiguration getDefaultConfig() {
        return this.defaultConfig;
    }

    //〓 Setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @param dataDirectory セットするローカルディレクトリ */
    private void setDataDirectory(File dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    /** @param localConfigFileName セットするローカルコンフィグファイル名 */
    private void setLocalConfigFileName(String localConfigFileName) {
        this.localConfigFileName = localConfigFileName;
    }

    /** @param localConfigFile セットするローカルコンフィグファイル */
    private void setLocalConfigFile(File localConfigFile) {
        this.localConfigFile = localConfigFile;
    }

    /** @param localConfig セットするローカルコンフィグ設定データ */
    private void setLocalConfig(CommentableYamlConfiguration localConfig) {
        this.localConfig = localConfig;
    }

    /** @param defaultConfig セットするデフォルトコンフィグ設定データ */
    private void setDefaultConfig(YamlConfiguration defaultConfig) {
        this.defaultConfig = defaultConfig;
    }
}
