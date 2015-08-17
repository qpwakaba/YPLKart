package com.github.erozabesu.yplkart;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.erozabesu.yplkart.data.CharacterConfig;
import com.github.erozabesu.yplkart.data.CircuitConfig;
import com.github.erozabesu.yplkart.data.ConfigEnum;
import com.github.erozabesu.yplkart.data.DisplayKartConfig;
import com.github.erozabesu.yplkart.data.ItemEnum;
import com.github.erozabesu.yplkart.data.KartConfig;
import com.github.erozabesu.yplkart.data.MessageEnum;

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
    private YamlConfiguration localConfig;

    /** .jar内のデフォルトコンフィグファイルの設定データ */
    private YamlConfiguration defaultConfig;

    /**
     * コンストラクタ
     * ローカルファイルの読み込みはstatic.reload()メソッドで明示的に行う
     * static.reload()はYPLKart.onEnable()から実行される
     * @param localConfigFileName ローカルコンフィグファイル名
     */
    private ConfigManager(String localConfigFileName) {
        setLocalConfigFileName(localConfigFileName);
        setDefaultConfig(getYamlConfigFromResource());
    }

    //〓 getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

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
    public FileConfiguration getLocalConfig() {
        return this.localConfig;
    }

    /** @return デフォルトコンフィグファイルの設定データ */
    public YamlConfiguration getDefaultConfig() {
        return this.defaultConfig;
    }

    //〓 setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

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
    private void setLocalConfig(YamlConfiguration localConfig) {
        this.localConfig = localConfig;
    }

    /** @param defaultConfig セットするデフォルトコンフィグ設定データ */
    private void setDefaultConfig(YamlConfiguration defaultConfig) {
        this.defaultConfig = defaultConfig;
    }

    //〓 do 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * ローカルコンフィグの設定データからpathキーの値を返す
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
     * ローカルコンフィグの設定データからpathキーのString値を返す
     * @param configKey ローカルコンフィグ値のパス
     * @return 取得したローカルコンフィグのString値
     */
    public String getString(String configKey) {
        //ローカルコンフィグにキーが含まれていない場合書き込む
        if (!getLocalConfig().contains(configKey)) {
            Object defaultValue = getDefaultConfig().get(configKey);
            if (defaultValue != null) {
                getLocalConfig().set(configKey, defaultValue);
            } else {
                getLocalConfig().set(configKey, "");
            }
        }

        return String.valueOf(getLocalConfig().get(configKey));
    }

    /**
     * ローカルコンフィグの設定データからpathキーのint値を返す
     * @param configKey ローカルコンフィグ値のパス
     * @return 取得したローカルコンフィグのint値
     */
    public int getInteger(String configKey) {
        //ローカルコンフィグにキーが含まれていない場合書き込む
        if (!getLocalConfig().contains(configKey)) {
            Object defaultValue = getDefaultConfig().get(configKey);
            if (defaultValue != null) {
                getLocalConfig().set(configKey, defaultValue);
            } else {
                getLocalConfig().set(configKey, 0);
            }
        }

        try {
            return Integer.valueOf(String.valueOf(getLocalConfig().get(configKey)));
        } catch (ClassCastException e) {
            return 0;
        }
    }

    /**
     * ローカルコンフィグの設定データからpathキーのfloat値を返す
     * @param configKey ローカルコンフィグ値のパス
     * @return 取得したローカルコンフィグのfloat値
     */
    public float getFloat(String configKey) {
        //ローカルコンフィグにキーが含まれていない場合書き込む
        if (!getLocalConfig().contains(configKey)) {
            Object defaultValue = getDefaultConfig().get(configKey);
            if (defaultValue != null) {
                getLocalConfig().set(configKey, defaultValue);
            } else {
                getLocalConfig().set(configKey, 0.6F);
            }
        }

        try {
            return Float.valueOf(String.valueOf(getLocalConfig().get(configKey)));
        } catch (ClassCastException e) {
            return 0.6F;
        }
    }

    /**
     * ローカルコンフィグの設定データからpathキーのdouble値を返す
     * @param configKey ローカルコンフィグ値のパス
     * @return 取得したローカルコンフィグのdouble値
     */
    public double getDouble(String configKey) {
        //ローカルコンフィグにキーが含まれていない場合書き込む
        if (!getLocalConfig().contains(configKey)) {
            Object defaultValue = getDefaultConfig().get(configKey);
            if (defaultValue != null) {
                getLocalConfig().set(configKey, defaultValue);
            } else {
                getLocalConfig().set(configKey, 1.0D);
            }
        }

        try {
            return Double.valueOf(String.valueOf(getLocalConfig().get(configKey)));
        } catch (ClassCastException e) {
            return 1.0D;
        }
    }

    /**
     * ローカルコンフィグの設定データからpathキーのbyte値を返す
     * @param configKey ローカルコンフィグ値のパス
     * @return 取得したローカルコンフィグのbyte値
     */
    public byte getByte(String configKey) {
        //ローカルコンフィグにキーが含まれていない場合書き込む
        if (!getLocalConfig().contains(configKey)) {
            Object defaultValue = getDefaultConfig().get(configKey);
            if (defaultValue != null) {
                getLocalConfig().set(configKey, defaultValue);
            } else {
                getLocalConfig().set(configKey, 0);
            }
        }

        try {
            return Byte.valueOf(String.valueOf(getLocalConfig().get(configKey)));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * ローカルコンフィグの設定データからpathキーのboolean値を返す
     * @param configKey ローカルコンフィグ値のパス
     * @return 取得したローカルコンフィグのboolean値
     */
    public boolean getBoolean(String configKey) {
        //ローカルコンフィグにキーが含まれていない場合書き込む
        if (!getLocalConfig().contains(configKey)) {
            Object defaultValue = getDefaultConfig().get(configKey);
            if (defaultValue != null) {
                getLocalConfig().set(configKey, defaultValue);
            } else {
                getLocalConfig().set(configKey, false);
            }
        }

        return Boolean.valueOf(String.valueOf(getLocalConfig().get(configKey)));
    }

    /**
     * ローカルコンフィグの設定データからpathキーのMaterial値を返す
     * @param configKey ローカルコンフィグ値のパス
     * @return 取得したローカルコンフィグのMaterial値
     */
    public Material getMaterial(String configKey) {
        //ローカルコンフィグにキーが含まれていない場合書き込む
        if (!getLocalConfig().contains(configKey)) {
            Object defaultValue = getDefaultConfig().get(configKey);
            if (defaultValue != null) {
                getLocalConfig().set(configKey, defaultValue);
            } else {
                getLocalConfig().set(configKey, "stone");
            }
        }

        try {
            return Material.getMaterial(String.valueOf(getLocalConfig().get(configKey)).toUpperCase());
        } catch (NullPointerException e) {
            return Material.STONE;
        }
    }

    /**
     * ローカルコンフィグの設定データからpathキーのSound値を返す
     * @param configKey ローカルコンフィグ値のパス
     * @return 取得したローカルコンフィグのSound値
     */
    public Sound getSound(String configKey) {
        //ローカルコンフィグにキーが含まれていない場合書き込む
        if (!getLocalConfig().contains(configKey)) {
            Object defaultValue = getDefaultConfig().get(configKey);
            if (defaultValue != null) {
                getLocalConfig().set(configKey, defaultValue);
            } else {
                getLocalConfig().set(configKey, "click");
            }
        }

        try {
            return Sound.valueOf(String.valueOf(getLocalConfig().get(configKey)).toUpperCase());
        } catch (NullPointerException e) {
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

    //〓 file edit 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * ローカルのコンフィグファイルから設定データを読み込む<br>
     * YamlConfiguration.loadConfiguration(File file)では、<br>
     * 2バイト文字を含む文字コードUTF-8で記述されたテキストファイルは正常に読み取れないため、<br>
     * YamlConfiguration.loadConfiguration(Reader reader)を利用する
     */
    public void loadLocalConfiguration() {
        File localFile = new File(YPLKart.getInstance().getDataFolder() + File.separator + this.getLocalConfigFileName());
        this.setLocalConfigFile(localFile);
        if (!localFile.exists()) {
            return;
        }

        InputStream input = null;
        BufferedReader reader = null;

        File parent = localFile.getParentFile();
        if (!parent.exists()) {
            return;
        }

        try {
            input = new FileInputStream(localFile);
            reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));

            this.setLocalConfig(YamlConfiguration.loadConfiguration(reader));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    // Do nothing
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // Do nothing
                }
            }
        }
    }

    /**
     * コンフィグファイルの生成が必要な状態かどうかを判別し必要ならファイルを生成する
     * @return ファイルのコピーに成功したかどうか
     */
    public boolean CreateConfig() {
        //既にローカルファイルが存在している
        if (getLocalConfigFile().exists()) {
            return false;
        }

        /*
         * jarファイルからコンフィグファイルのコピーを試みる
         * コピー元のファイルが存在しない場合プラグインを停止する
         */
        if (!copyResource()) {
            MessageEnum.sendAbsolute(null, "[" + YPLKart.PLUGIN_NAME + "] v."
                    + YPLKart.PLUGIN_VERSION + " "
                    + getLocalConfigFileName() + " was not found in jar file");
            YPLKart.getInstance().onDisable();
            return false;
        }

        return true;
    }

    /**
     * .jarからファイルをローカルディレクトリにコピーする
     * @return ファイルのコピーに成功したかどうか
     */
    public boolean copyResource() {
        String filepath = "resources/" + getLocalConfigFileName();
        File outputFile = new File(YPLKart.getInstance().getDataFolder() + File.separator + getLocalConfigFileName());
        InputStream input = null;
        FileOutputStream output = null;
        BufferedReader reader = null;
        BufferedWriter writer = null;

        File parent = outputFile.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }

        try {
            input = YPLKart.getInstance().getResource(filepath);

            if (input == null) {
                return false;
            }

            output = new FileOutputStream(outputFile);
            reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
            writer = new BufferedWriter(new OutputStreamWriter(output, "UTF-8"));

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
            writer.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    // Do nothing
                }
            }
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    // Do nothing
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    // Do nothing
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // Do nothing
                }
            }
        }
    }

    /** 設定データをローカルに保存する */
    public void saveConfiguration() {
        File file = getLocalConfigFile();
        FileConfiguration config = this.getLocalConfig();

        //コンフィグファイルが無い場合新規作成する
        if (!file.exists()) {
            if (!copyResource()) {
                return;
            }
            file = new File(YPLKart.getInstance().getDataFolder(), file.getName());
        }

        FileInputStream input = null;
        FileOutputStream output = null;
        BufferedReader reader = null;
        BufferedWriter writer = null;

        ArrayList<String> newLineList = new ArrayList<String>();

        //引き継がない行の値を一時的に書き換える際の文字列
        Date tempDate = new Date();
        String tempRemoveString = tempDate.toString() + "remove";

        try {
            input = new FileInputStream(new File(YPLKart.getInstance().getDataFolder(), file.getName()));
            reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));

            //新しいファイルに引き継ぐ行の文字列を格納
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) {
                    newLineList.add(line);
                } else if (line.matches(".+\\#.+")) {
                    newLineList.add(line);
                } else if (line.length() == 0) {
                    newLineList.add(line);
                } else {
                    newLineList.add(tempRemoveString);
                }
            }

            //設定データの格納
            for (String configkey : config.getKeys(true)) {
                String newLine = "";

                //キーに"."を含む場合、"."以前の文字列を削除しスペース2文字のインデントに置換する
                //そうでない場合はキーをそのままnewLineに代入する
                if (configkey.contains(".")) {

                    //正規表現"."で文字列を区切り、配列の最後の文字列を新たなキーとして抽出

                    String[] splitConfigKey = configkey.split("\\.");

                    newLine = splitConfigKey[splitConfigKey.length-1];

                    //抽出したキーの先頭に配列の数だけインデント"  "を追記
                    for (int i = 0; i < splitConfigKey.length-1; i++) {
                        newLine = "  " + newLine;
                    }
                } else {
                    newLine = configkey;
                }

                //valueに"MemorySection"を含む行はvalueを書き出さない
                //そうでない場合はvalueをそのままnewLineに追記する
                if (config.get(configkey).toString().contains("MemorySection")) {
                    newLine += " : ";
                } else {
                    if (config.get(configkey) instanceof Number) {
                        newLine += " : " + config.get(configkey);
                    } else if (String.valueOf(config.get(configkey)).equalsIgnoreCase("true")
                            || String.valueOf(config.get(configkey)).equalsIgnoreCase("false")) {
                        newLine += " : " + config.get(configkey);
                    } else {
                        newLine += " : \"" + config.get(configkey) + "\"";
                    }
                }

                int newLineListIndex = 0;
                for (String newLines : newLineList) {
                    if (newLines.equalsIgnoreCase(tempRemoveString)) {
                        newLineList.set(newLineListIndex, newLine);
                        break;
                    }
                    newLineListIndex++;
                }
            }

            //ファイルを再生成しファイルの内容を初期化
            file.delete();
            file.createNewFile();

            output = new FileOutputStream(file);
            writer = new BufferedWriter(new OutputStreamWriter(output, "UTF-8"));

            for (String newLines : newLineList) {
                writer.write(newLines);
                writer.newLine();
            }

            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    // Do nothing
                }
            }
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    // Do nothing
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    // Do nothing
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // Do nothing
                }
            }
        }
    }

    /**
     * .jar内のファイルからYamlConfigurationを取得する
     * @return 取得されたYamlConfiguration
     */
    public YamlConfiguration getYamlConfigFromResource() {
        String filepath = "resources/" + getLocalConfigFileName();
        InputStream input = null;
        BufferedReader reader = null;

        try {
            input = YPLKart.getInstance().getResource(filepath);

            if (input == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));

            return YamlConfiguration.loadConfiguration(reader);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    // Do nothing
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // Do nothing
                }
            }
        }
    }

    //〓 static 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** 全ローカルファイル及び設定データを再読み込みし、オブジェクトを再生成する */
    public static void reloadAllConfig() {

        //以下順序を変更しないこと

        //ファイルを読み込む
        reloadFile();

        //基本設定を読み込む
        ConfigEnum.reload();

        //アイテム設定を読み込む
        try {
            ItemEnum.reload();
        } catch (ExceptionInInitializerError e) {
            e.printStackTrace();
        }


        //キャラクター設定を読み込む
        new CharacterConfig();
        CharacterConfig.reload();

        //カート設定を読み込む
        new KartConfig();
        KartConfig.reload();

        //ディスプレイカート設定を読み込む
        new DisplayKartConfig();
        DisplayKartConfig.reload();

        //サーキット設定を読み込む
        new CircuitConfig();
        CircuitConfig.reload();

        //テキストメッセージを読み込む
        //他オブジェクトの名称等が含まれるため最後に処理する
        MessageEnum.reload();

        saveAllFile();
    }

    /** ローカルの全コンフィグファイルから設定データを再読み込みする */
    private static void reloadFile() {
        for (ConfigManager configManager : ConfigManager.values()) {

            //ローカルコンフィグの読み込み
            configManager.loadLocalConfiguration();

            //ローカルコンフィグを新規作成した場合、ローカルコンフィグを再読み込みする
            if (configManager.CreateConfig()) {
                configManager.loadLocalConfiguration();
            }
        }
    }

    /** 全設定データをローカルコンフィグファイルに保存する */
    private static void saveAllFile() {
        for (ConfigManager configManager : ConfigManager.values()) {
            //設定データをローカルコンフィグファイルへ保存
            configManager.saveConfiguration();
        }
    }
}
