package com.github.erozabesu.yplkart.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.github.erozabesu.yplkart.LanguageManager;
import com.github.erozabesu.yplkart.YPLKart;
import com.github.erozabesu.yplkart.enumdata.RaceType;
import com.github.erozabesu.yplkart.enumdata.TagType;
import com.github.erozabesu.yplkart.object.MessageParts;
import com.github.erozabesu.yplutillibrary.util.CommentableYamlConfiguration;

/**
 * プラグインから出力されるテキストメッセージを格納するクラス
 * enum要素名がコンフィグ取得キーなので注意
 * ユーザ側で要素数を変更できない静的なコンフィグを扱うためenumで管理する
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
    cmdEntryForceOther(),
    cmdEntryForceAll(),
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
    raceEntryForce(),
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
        this.setMessage(LanguageManager.MESSAGE.getString(this.getConfigKey()));
    }

    /**
     * 全要素のメンバ変数を再取得し汎用タグ、カラータグを置換する
     */
    public static void reload() {

        //ローカルコンフィグの設定データをメンバ変数へ格納
        for (MessageEnum messageEnum : values()) {
            messageEnum.loadLocalConfig();
        }

        //強制的に変更したいメッセージがある場合パッチを当てる
        updatePatch();

        //ローカルへファイルの保存
        //デフォルトコンフィグから、ローカルコンフィグに未記載の項目を追記する
        LanguageManager.MESSAGE.saveLocal();

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
        CommentableYamlConfiguration localConfig = LanguageManager.MESSAGE.getLocalConfig();
        YamlConfiguration defaultConfig = LanguageManager.MESSAGE.getDefaultConfig();

        if (version.equalsIgnoreCase("1.0")) {
            localConfig.set(messageVersion.getConfigKey(), "1.1");
            localConfig.set(racePlayerKill.getConfigKey(), defaultConfig.getString(racePlayerKill.getConfigKey()));
        } else if (version.equalsIgnoreCase("1.1")) {
            localConfig.set(messageVersion.getConfigKey(), "1.2");
            localConfig.set(tableKartParameter.getConfigKey(), defaultConfig.getString(tableKartParameter.getConfigKey()));
        } else if (version.equalsIgnoreCase("1.2")) {
            localConfig.set(messageVersion.getConfigKey(), "1.3");
            localConfig.set(tableCircuitInformation.getConfigKey(), defaultConfig.getString(tableCircuitInformation.getConfigKey()));
        } else if (version.equalsIgnoreCase("1.3")) {
            localConfig.set(messageVersion.getConfigKey(), "1.4");
            localConfig.set(tableCircuitInformation.getConfigKey(), defaultConfig.getString(tableCircuitInformation.getConfigKey()));
        }
    }

    //〓 Message 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public String getConvertedMessage(MessageParts... messageParts) {
        String message = this.getMessage();
        for (int i = 0; i < messageParts.length; i++) {
            message = replaceLimitedTags(message, messageParts[i]);
        }
        return message;
    }

    public void sendConvertedMessage(CommandSender address, MessageParts... messageParts) {
        sendMessage(address, getConvertedMessage(messageParts));
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
     * 引数baseMessageに含まれる動的なメッセージタグを引数messagePartsに含まれるデータに置換し返す。<br>
     * 動的データを扱うためメッセージの送信段階でのみ利用する。
     * @param basemessage 置換する文字列
     * @param messageParts 置換するタグ、対応する文字列が格納されたMessagePartsインスタンス
     * @return 置換後の文字列
     */
    private static String replaceLimitedTags(String baseMessage, MessageParts messageParts) {
        TagType tagType = messageParts.getTagType();
        String[] partsText = messageParts.getMessage();

        if (tagType.isArray()) {
            for (int i = 0; i < partsText.length; i++) {
                int tagnumber = i + 1;
                baseMessage = baseMessage.replace("<" + tagType.getTagText() + tagnumber + ">", partsText[i]);
            }
        } else {
            baseMessage = baseMessage.replace("<" + tagType.getTagText() + ">", partsText[0]);
        }

        return baseMessage;
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
