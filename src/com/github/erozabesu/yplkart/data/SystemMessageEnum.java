package com.github.erozabesu.yplkart.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.github.erozabesu.yplkart.YPLKart;

/**
 * プラグインから出力されるテキストメッセージ、
 * コマンドUsageのようなユーザ側で変更させたくないテキストメッセージを格納するクラス
 * ユーザ側で要素数を変更できない静的なコンフィグを扱うためenumで管理する
 *
 * 一見冗長だがenumで管理することで呼び出す側の負担を軽減できる
 * @author erozabesu
 */
public enum SystemMessageEnum {
    referenceCircuitIngame("<green>/ka circuit info {circuit name} :<br>"
            + "<green>/ka circuit create {circuit name} :<br>"
            + "<green>/ka circuit create {circuit name} {worldname} {x} {y} {z} {yaw} {pitch} :<br>"
            + "<green>/ka circuit delete {circuit name} :<br>"
            + "<green>/ka circuit edit {circuit name} :<br>"
            + "<green>/ka circuit setracetype {circuit name} {race type} :<br>"
            + "<green>/ka circuit setlap {circuit name} {number of laps} :<br>"
            + "<green>/ka circuit setminplayer {circuit name} {number of player} :<br>"
            + "<green>/ka circuit setmaxplayer {circuit name} {number of player} :<br>"
            + "<green>/ka circuit setmatchingtime {circuit name} {number of second} :<br>"
            + "<green>/ka circuit setmenutime {circuit name} {number of second} :<br>"
            + "<green>/ka circuit setlimittime {circuit name} {number of second} :<br>"
            + "<green>/ka circuit setposition {circuit name} :<br>"
            + "<green>/ka circuit setposition {circuit name} {worldname} {x} {y} {z} {yaw} {pitch} :<br>"
            + "<green>/ka circuit broadcastgoal {circuit name} {true or false} :<br>"
            + "<green>/ka circuit rename {circuit name} {new circuitname} :<br>"
            + "<green>/ka circuit list :<br>"
            + "<gold>Circuit List :<br><white><circuitlist>"),
    referenceCircuitOutgame("<green>/ka circuit create {circuit name} {worldname} {x} {y} {z} {yaw} {pitch} :<br>"
            + "<green>/ka circuit delete {circuit name} :<br>"
            + "<green>/ka circuit setracetype {circuit name} {race type} :<br>"
            + "<green>/ka circuit setposition {circuit name} {worldname} {x} {y} {z} {yaw} {pitch} :<br>"
            + "<green>/ka circuit setgoalposition {circuit name} {worldname} {x} {y} {z} {yaw} {pitch} :<br>"
            + "<green>/ka circuit rename {circuit name} {new circuitname} :<br>"
            + "<green>/ka circuit list :<br>"
            + "<gold>Circuit List :<br><white><circuitlist>"),
    referenceDisplayIngame("<green>/ka display {kart name} :<br>"
            + "<green>/ka display random :<br>"
            + "<green>/ka display {kart name} {worldname} {x} {y} {z} {yaw} {pitch} :<br>"
            + "<green>/ka display random {worldname} {x} {y} {z}  {yaw} {pitch} :<br>"
            + "<gold>Kart List :<br><white><kartlist>"),
    referenceDisplayOutgame("<green>/ka display {kart name} {worldname} {x} {y} {z} {yaw} {pitch} :<br>"
            + "<green>/ka display random {worldname} {x} {y} {z}  {yaw} {pitch} :<br>"
            + "<gold>Kart List :<br><white><kartlist>"),
    referenceEntry("<green>/ka entry {circuit name} :"),
    referenceEntryOther("<green>/ka entry {player name} {circuit name} :<br>"
            + "<green>/ka entry all {circuit name} :<br>"
            + "<gold>Circuit List :<br><white><circuitlist>"),
    referenceExit("<green>/ka exit :"),
    referenceExitOther("<green>/ka exit {player name} :<br>"
            + "<green>/ka exit all"),
    referenceMenu("<green>/ka menu :"),
    referenceMenuOther("<green>/ka menu {player name} :<br>"
            + "<green>/ka menu all :"),
    referenceCharacter("<green>/ka character {character name} :<br>"
            + "<green>/ka character random :"),
    referenceCharacterOther("<green>/ka character {player name} {character name} :<br>"
            + "<green>/ka character all {character name} :<br>"
            + "<green>/ka character {player name} random :<br>"
            + "<green>/ka character all random :<br>"
            + "<gold>Character List :<br><white><characterlist>"),
    referenceCharacterReset("<green>/ka characterreset :"),
    referenceCharacterResetOther("<green>/ka characterreset {player name} :<br>"
            + "<green>/ka characterreset all :"),
    referenceRide("<green>/ka kart {kart name} :<br>"
            + "<green>/ka kart random :"),
    referenceRideOther("<green>/ka kart {player name} {kart name} :<br>"
            + "<green>/ka kart all {kart name} :<br>"
            + "<green>/ka kart {player name} random :<br>"
            + "<green>/ka kart all random :<br>"
            + "<gold>Kart List :<br><white><kartlist>"),
    referenceLeave("<green>/ka leave :"),
    referenceLeaveOther("<green>/ka leave {player name} :<br>"
            + "<green>/ka leave all :"),
    referenceRanking("<green>/ka ranking {circuit name} :<br>"
            + "<green>/ka ranking list :"),
    referenceRankingOther("<green>/ka ranking {player name} {circuit name} :<br>"
            + "<green>/ka ranking all {circuit name} :<br>"
            + "<gold>Circuit List :<br><white><circuitlist>"),
    referenceAddItem("<green>/ka {item name} :<br>"
            + "<green>/ka {item name} {amount} :"),
    referenceAddItemOther("<green>/ka {item name} {player name} :<br>"
            + "<green>/ka {item name} {player name} {amount} :<br>"
            + "<green>/ka {item name} all :<br>"
            + "<green>/ka {item name} all {amount} :<br>"
            + "<gold>Item List :<br><white><itemlist>"),
    reference("<green>===========-[ <gold><plugin> Command List<green> ]-===========<br>"
            + "<green>/ka circuit :<br>"
            + "<green>/ka display :<br>"
            + "<green>/ka entry :<br>"
            + "<green>" + referenceExit.getMessage() + "<br>"
            + "<green>" + referenceExitOther.getMessage() + "<br>"
            + "<green>" + referenceMenu.getMessage() + "<br>"
            + "<green>" + referenceMenuOther.getMessage() + "<br>"
            + "<green>/ka character :<br>"
            + "<green>" + referenceCharacterReset.getMessage() + "<br>"
            + "<green>" + referenceCharacterResetOther.getMessage() + "<br>"
            + "<green>/ka ride :<br>"
            + "<green>" + referenceLeave.getMessage() + "<br>"
            + "<green>" + referenceLeaveOther.getMessage() + "<br>"
            + "<green>/ka ranking :<br>"
            + "<green>/ka item :<br>"
            + "<green>/ka reload :<br>"
            + "<green>===========================================<br>");

    private static Logger log = Logger.getLogger("Minecraft");

    /** テキストメッセージ */
    private String message;

    //〓 main 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * コンストラクタ
     * enumの静的データを格納する
     */
    private SystemMessageEnum(String message) {
        setMessage(message);

        //タグを置換
        setMessage(replaceBasicTags(getMessage()));
        setMessage(replaceCommandReferenceParam(getMessage()));
        setMessage(replaceChatColor(getMessage()));
    }

    //〓 getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @return message テキストメッセージ */
    public String getMessage() {
        return this.message;
    }

    //〓 setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @param message セットするテキストメッセージ */
    public void setMessage(String message) {
        this.message = message;
    }

    //〓 public do 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public void sendConvertedMessage(Object adress) {
        sendMessage(adress, getMessage());
    }

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

    //〓 util 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 引数adressに対し引数messageのテキストメッセージを送信する
     * adressがnullの場合はログにプレーンメッセージを出力する
     * @param adress 送信対象(Player, UUID, null)
     * @param message テキストメッセージ
     * @see org.bukkit.entity.Player
     * @see java.util.UUID
     */
    private void sendMessage(Object adress, String message) {
        if (message == null)
            return;
        if (message.length() == 0)
            return;

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

    //〓 static 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 引数messageの汎用タグを置換する
     * 必ず全メンバ変数を格納後に使用すること
     * @param message 置換前のテキストメッセージ
     * @return 置換後のテキストメッセージ
     */
    private static String replaceBasicTags(String message) {
        message = message.replace("<header>", MessageEnum.header.getMessage());
        message = message.replace("<circuitheader>", MessageEnum.circuitheader.getMessage());
        message = message.replace("<plugin>", YPLKart.PLUGIN_NAME);
        if (ItemEnum.getItemListString() != null) {
            message = message.replace("<itemlist>", ItemEnum.getItemListString());
        }
        if (CircuitConfig.getCircuitListString() != null) {
            message = message.replace("<circuitlist>", CircuitConfig.getCircuitListString());
        }
        if (CharacterConfig.getCharacterListString() != null) {
            message = message.replace("<characterlist>", CharacterConfig.getCharacterListString());
        }
        if (KartConfig.getKartListString() != null) {
            message = message.replace("<kartlist>", KartConfig.getKartListString());
        }

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
     * 引数messageのコマンドリファレンス用テキストを見えやすいよう
     * カラータグを交えたテキストに置換する
     * @param message 置換前のテキストメッセージ
     * @return 置換後のテキストメッセージ
     */
    private static String replaceCommandReferenceParam(String message) {
        message = message.replace("{race type}", "<green>{<white>race type<green>}");
        message = message.replace("{circuit name}", "<green>{<white>circuit name<green>}");
        message = message.replace("{worldname}", "<green>{<white>worldname<green>}");
        message = message.replace("{x}", "<green>{<white>x<green>}");
        message = message.replace("{y}", "<green>{<white>y<green>}");
        message = message.replace("{z}", "<green>{<white>z<green>}");
        message = message.replace("{yaw}", "<green>{<white>yaw<green>}");
        message = message.replace("{pitch}", "<green>{<white>pitch<green>}");
        message = message.replace("{new circuitname}", "<green>{<white>new circuitname<green>}");
        message = message.replace("{player name}", "<green>{<white>player name<green>}");
        message = message.replace("{character name}", "<green>{<white>character name<green>}");
        message = message.replace("{kart name}", "<green>{<white>kart name<green>}");
        message = message.replace("{item name}", "<green>{<white>item name<green>}");
        message = message.replace("{amount}", "<green>{<white>amount<green>}");
        message = message.replace("{number of player}", "<green>{<white>number of player<green>}");
        message = message.replace("{number of second}", "<green>{<white>number of second<green>}");
        message = message.replace("{number of laps}", "<green>{<white>number of laps<green>}");
        message = message.replace("{true or false}", "<green>{<white>true <green>or <white>false<green>}");

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
}
