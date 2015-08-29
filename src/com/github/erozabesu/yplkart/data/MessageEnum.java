package com.github.erozabesu.yplkart.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.erozabesu.yplkart.ConfigManager;
import com.github.erozabesu.yplkart.YPLKart;
import com.github.erozabesu.yplkart.object.Character;
import com.github.erozabesu.yplkart.object.Circuit;
import com.github.erozabesu.yplkart.object.Kart;
import com.github.erozabesu.yplkart.object.PermissionObject;
import com.github.erozabesu.yplkart.object.RaceType;
import com.github.erozabesu.yplkart.utils.Util;

/**
 * プラグインから出力されるテキストメッセージを格納するクラス
 * enum要素名がコンフィグ取得キーなので注意
 * ユーザ側で要素数を変更できない静的なコンフィグを扱うためenumで管理する
 *
 * 一見冗長だがenumで管理することで呼び出す側の負担を軽減できる
 *
 * コマンドUsageのようなユーザ側で変更させたくないテキストメッセージは
 * SystemMessageEnumクラスで扱う
 * @author erozabesu
 */
public enum MessageEnum {
    messageVersion(),
    header(),
    circuitheader(),
    noPermission(),

    invalidPlayer(),
    invalidWorld(),
    invalidNumber(),
    invalidBoolean(),
    invalidCircuit(),
    invalidRaceType(),
    invalidCharacter(),
    invalidKart(),

    cmdCircuitCreate(),
    cmdCircuitAlreadyExist(),
    cmdCircuitDelete(),
    cmdCircuitList(),
    cmdCircuitEdit(),
    cmdCircuitRename(),
    cmdCircuitRankingNoScoreData(),
    cmdCircuitSetRaceType(),
    cmdCircuitSetPosition(),
    cmdCircuitSetLap(),
    cmdCircuitSetMinPlayer(),
    cmdCircuitOutOfMaxPlayer(),
    cmdCircuitSetMaxPlayer(),
    cmdCircuitOutOfMinPlayer(),
    cmdCircuitSetMatchingTime(),
    cmdCircuitSetMenuTime(),
    cmdCircuitSetLimitTime(),
    cmdCircuitSetBroadcastGoalMessage(),
    cmdMenuAll(),
    cmdDisplayCreate(),
    cmdDisplayDelete(),
    cmdMenuOther(),
    cmdEntryOther(),
    cmdEntryAll(),
    cmdExitAll(),
    cmdExitOther(),
    cmdCharacterAll(),
    cmdCharacterRandomAll(),
    cmdCharacterOther(),
    cmdCharacterResetAll(),
    cmdCharacterResetOther(),
    cmdRideAll(),
    cmdRideRandomAll(),
    cmdRideOther(),
    cmdLeaveAll(),
    cmdLeaveOther(),
    cmdRankingOther(),
    cmdRankingAll(),
    cmdItem(),
    cmdItemOther(),
    cmdItemAll(),
    cmdReload(),

    raceReady(),
    raceMatchingFailed(),
    raceAccept(),

    raceStart(),
    raceEnd(),
    raceTimeLimitAlert(),
    raceTimeLimitCountDown(),
    raceTimeUp(),

    raceGoal(),
    raceHighScore(),
    raceUpdateLap(),
    raceReverseRun(),
    racePlayerDead(),
    racePlayerKill(),

    raceEntry(),
    raceEntryAlready(),
    raceEntryAlreadyStart(),
    raceEntryFull(),
    raceExit(),

    raceCharacter(),
    raceKart(),
    raceCharacterReset(),
    raceLeave(),
    raceMustSelectCharacter(),
    raceMustSelectKart(),
    raceNotStarted(),

    raceInteractBanana(),
    raceInteractItemBox(),
    raceInteractItemBoxFailed(),
    raceInteractFakeItemBox(),

    itemRemoveItemBox(),
    itemRemoveCheckPoint(),
    itemNoPlayer(),
    itemNoHigherPlayer(),
    itemNoCheckpoint(),
    itemHighestPlayer(),
    itemAlreadyUsing(),
    itemTeresaNoItem(),
    itemTeresaRob(),
    itemTeresaRobbed(),

    titleDeathPanalty(),
    titleUsingKiller(),
    titleRacePrepared(),
    titleRaceMenu(),
    titleRaceStandby(),
    titleRaceStandbySub(),
    titleRaceLaps(),
    titleRaceLapsSub(),
    titleRaceTimeLimit(),
    titleRaceTimeLimitSub(),
    titleRaceReady(),
    titleRaceReadySub(),
    titleRaceStartCountDown(),
    titleRaceStartCountDownSub(),
    titleCountDown(),
    titleGoalRank(),

    tableCircuitInformation(),
    tableCharacterParameter(),
    tableKartParameter();

    private static Logger log = Logger.getLogger("Minecraft");

    /** コンフィグキー */
    private String configKey;

    /** テキストメッセージ */
    private String message;

    //〓 Main 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * コンストラクタ
     * enumの静的データを格納する
     * 動的データ（コンフィグ）の読み込みはstatic.reload()メソッドで明示的に行う
     * static.reload()はConfigManager.reload()から実行される
     */
    private MessageEnum() {
        setConfigKey(name());
    }

    /** ローカルコンフィグファイルの設定データを格納する */
    private void loadLocalConfig() {
        ConfigManager config = ConfigManager.MESSAGE_ENUM;

        String defaultValue = config.getDefaultConfig().getString(this.getConfigKey());
        this.setMessage(ConfigManager.MESSAGE_ENUM.getString(this.getConfigKey(), defaultValue));
    }

    /**
     * 全要素のメンバ変数を再取得し汎用タグ、カラータグを置換する
     */
    public static void reload(){

        //ローカルコンフィグの設定データをメンバ変数へ格納
        for (MessageEnum messageEnum : values()) {
            messageEnum.loadLocalConfig();
        }

        //強制的に変更したいメッセージがある場合パッチを当てる
        updatePatch();

        //ローカルへファイルの保存
        //デフォルトコンフィグから、ローカルコンフィグに未記載の項目を追記する
        ConfigManager.MESSAGE_ENUM.saveConfiguration();

        //タグを置換
        for (MessageEnum messageEnum : values()) {
            messageEnum.setMessage(replaceBasicTags(messageEnum.getMessage()));
            messageEnum.setMessage(replaceChatColor(messageEnum.getMessage()));
        }
    }

    /**
     * 新規の要素を追加した際は自動的に追記されるためパッチを当てる必要はない
     * 既存の要素を強制的に上書きする際に用いる(項目の追加、記述ミスの修正など)
     */
    private static void updatePatch() {
        String version = messageVersion.getMessage();
        ConfigManager configManager = ConfigManager.MESSAGE_ENUM;

        if (version.equalsIgnoreCase("1.0")) {
            configManager.setValue(messageVersion.getConfigKey(), "1.1");
            configManager.setValue(racePlayerKill.getConfigKey()
                    , configManager.getDefaultConfig().get(racePlayerKill.getConfigKey()));
        } else if (version.equalsIgnoreCase("1.1")) {
            configManager.setValue(messageVersion.getConfigKey(), "1.2");
            configManager.setValue(tableKartParameter.getConfigKey()
                    , configManager.getDefaultConfig().get(tableKartParameter.getConfigKey()));
        } else if (version.equalsIgnoreCase("1.2")) {
            configManager.setValue(messageVersion.getConfigKey(), "1.3");
            configManager.setValue(tableCircuitInformation.getConfigKey()
                    , configManager.getDefaultConfig().get(tableCircuitInformation.getConfigKey()));
        } else if (version.equalsIgnoreCase("1.3")) {
            configManager.setValue(messageVersion.getConfigKey(), "1.4");
            configManager.setValue(tableCircuitInformation.getConfigKey()
                    , configManager.getDefaultConfig().get(tableCircuitInformation.getConfigKey()));
        }
    }

    //〓 Message 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public String getConvertedMessage(Object... object) {
        String message = getMessage();
        for (int i = 0; i < object.length; i++) {
            message = replaceLimitedTags(message, object[i]);
        }
        return message;
    }

    public void sendConvertedMessage(CommandSender address, Object... object) {
        sendMessage(address, getConvertedMessage(object));
    }

    //サーキットのランキング表のような、タグ置換ではどうしようもないStringを送信する際に用います
    //極力使用しません。ある意味挫折の結果です
    public static void sendAbsolute(Object adress, String message) {
        Player p = null;
        if (adress instanceof Player)
            p = (Player) adress;
        else if (adress instanceof UUID)
            p = Bukkit.getPlayer((UUID) adress);

        for (String line : replaceLine(message)) {
            if (p != null)
                p.sendMessage(line);
            else
                log.log(Level.INFO, ChatColor.stripColor(line));
        }
    }

    /**
     * 引数addressに対し引数messageのテキストメッセージを送信する
     * addressがnullの場合はログにプレーンメッセージを出力する
     * @param address 送信対象(CommandSender, null)
     * @param message テキストメッセージ
     * @see org.bukkit.command.CommandSender
     */
    private void sendMessage(CommandSender address, String message) {
        if (message == null)
            return;
        if (message.isEmpty())
            return;

        for (String line : replaceLine(message)) {
            if (address != null)
                address.sendMessage(line);
            else
                log.log(Level.INFO, ChatColor.stripColor(line));
        }
    }

    /**
     * 引数addressに対し引数messageのテキストメッセージを送信する
     * addressがnullの場合はログにプレーンメッセージを出力する
     * @param address 送信対象(Player, UUID, null)
     * @param message テキストメッセージ
     * @see java.util.UUID
     */
    private void sendMessage(UUID address, String message) {
        this.sendMessage(Bukkit.getPlayer((UUID) address), message);
    }

    //〓 Tags 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 引数messageの汎用タグを置換する
     * 必ず全メンバ変数を格納後に使用すること
     * @param message 置換前のテキストメッセージ
     * @return 置換後のテキストメッセージ
     */
    private static String replaceBasicTags(String message) {
        message = message.replace("<header>", header.getMessage());
        message = message.replace("<circuitheader>", circuitheader.getMessage());
        message = message.replace("<plugin>", YPLKart.PLUGIN_NAME);

        String itemList = ItemEnum.getItemListString();
        message = message.replace("<itemlist>", itemList == null ? "" : itemList);

        String circuitList = CircuitConfig.getCircuitListString();
        message = message.replace("<circuitlist>", circuitList == null ? "" : circuitList);

        String raceTypeList = RaceType.getRaceTypeListString();
        message = message.replace("<racetypelist>", raceTypeList);

        String characterList = CharacterConfig.getCharacterListString();
        message = message.replace("<characterlist>", characterList == null ? "" : characterList);

        String kartList = KartConfig.getKartListString();
        message = message.replace("<kartlist>", kartList == null ? "" : kartList);

        return message;
    }

    /**
     * 引数messageのカラータグを置換する
     * 必ずreplaceBasicTags()で汎用タグを置換後に使用すること
     * @param message 置換前のテキストメッセージ
     * @return 置換後のテキストメッセージ
     */
    public static String replaceChatColor(String message) {
        message = message.replace("<black>", ChatColor.BLACK.toString());
        message = message.replace("<white>", ChatColor.WHITE.toString());
        message = message.replace("<gray>", ChatColor.GRAY.toString());
        message = message.replace("<darkgray>", ChatColor.DARK_GRAY.toString());
        message = message.replace("<red>", ChatColor.RED.toString());
        message = message.replace("<darkRed>", ChatColor.DARK_RED.toString());
        message = message.replace("<green>", ChatColor.GREEN.toString());
        message = message.replace("<darkgreen>", ChatColor.DARK_GREEN.toString());
        message = message.replace("<blue>", ChatColor.BLUE.toString());
        message = message.replace("<darkblue>", ChatColor.DARK_BLUE.toString());
        message = message.replace("<yellow>", ChatColor.YELLOW.toString());
        message = message.replace("<gold>", ChatColor.GOLD.toString());
        message = message.replace("<lightpurple>", ChatColor.LIGHT_PURPLE.toString());
        message = message.replace("<darkpurple>", ChatColor.DARK_PURPLE.toString());
        message = message.replace("<aqua>", ChatColor.AQUA.toString());
        message = message.replace("<darkaqua>", ChatColor.DARK_AQUA.toString());
        message = message.replace("<magic>", ChatColor.MAGIC.toString());
        message = message.replace("<bold>", ChatColor.BOLD.toString());
        message = message.replace("<italic>", ChatColor.ITALIC.toString());
        message = message.replace("<reset>", ChatColor.RESET.toString());
        message = message.replace("<strikethrough>", ChatColor.STRIKETHROUGH.toString());
        message = message.replace("<underline>", ChatColor.UNDERLINE.toString());

        return message;
    }

    /**
     * 引数messageの改行タグを置換する
     * メッセージの送信段階でのみ利用する
     * @param message 置換前のテキストメッセージ
     * @return 置換後のテキストメッセージList
     */
    public static List<String> replaceLine(String message) {
        List<String> newtext = new ArrayList<String>();
        for (String line : message.split("<br>")) {
            newtext.add(line);
        }
        return newtext;
    }

    /**
     * 動的なメッセージタグをobjectに含まれるデータに置換する
     * 動的データを扱うためメッセージの送信段階でのみ利用する
     * @param basemessage
     * @param object
     * @return
     */
    private static String replaceLimitedTags(String basemessage, Object object) {
        if (object == null) {
            return basemessage;
        }

        if (object instanceof Number) {
            basemessage = basemessage.replace("<number>", String.valueOf((Number) object));
        } else if (object instanceof Number[]) {
            Number[] num = (Number[]) object;
            for (int i = 0; i < num.length; i++) {
                int tagnumber = i + 1;
                basemessage = basemessage.replace("<number" + tagnumber + ">", String.valueOf(num[i]));
            }
        } else if (object instanceof String[]) {
            String[] text = (String[]) object;
            for (int i = 0; i < text.length; i++) {
                int tagnumber = i + 1;
                basemessage = basemessage.replace("<text" + tagnumber + ">", text[i]);
            }
        } else if (object instanceof Boolean) {
            basemessage = basemessage.replace("<flag>", String.valueOf(object));
        } else if (object instanceof Player) {
            basemessage = basemessage.replace("<player>", ((Player) object).getName());
        } else if (object instanceof Player[]) {
            for (int i = 0; i < ((Player[]) object).length; i++) {
                int tagnumber = i + 1;
                basemessage = basemessage.replace("<player" + tagnumber + ">",
                        String.valueOf(((Player[]) object)[i].getName()));
            }
        } else if (object instanceof PermissionObject) {
            if (basemessage.contains("<perm>")) {
                basemessage = basemessage.replace("<perm>", ((PermissionObject) object).getPermissionNode());
            }
        } else if (object instanceof Circuit) {
            basemessage = basemessage.replace("<circuitname>",
                    Util.convertInitialUpperString(((Circuit) object).getCircuitName()));
        } else if (object instanceof RaceType) {
            basemessage = basemessage.replace("<racetype>", ((RaceType) object).name());
        } else if (object instanceof Character) {
            basemessage = basemessage.replace("<character>", ((Character) object).getCharacterName());
        } else if (object instanceof Kart) {
            basemessage = basemessage.replace("<kart>", ((Kart) object).getKartName());
        } else if (object instanceof ItemStack) {
            basemessage = basemessage.replace("<item>",
                    ChatColor.stripColor(((ItemStack) object).getItemMeta().getDisplayName()));
        }

        return basemessage;
    }

    //〓 Getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @return configKey コンフィグキー */
    public String getConfigKey() {
        return this.configKey;
    }

    /** @return message テキストメッセージ */
    public String getMessage() {
        return this.message;
    }

    //〓 Setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @param configKey セットするコンフィグキー */
    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    /** @param message セットするテキストメッセージ */
    public void setMessage(String message) {
        this.message = message;
    }
}
