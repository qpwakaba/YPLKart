package com.github.erozabesu.yplkart.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.erozabesu.yplkart.YPLKart;
import com.github.erozabesu.yplkart.Enum.EnumCharacter;
import com.github.erozabesu.yplkart.Enum.EnumItem;
import com.github.erozabesu.yplkart.Enum.EnumKarts;
import com.github.erozabesu.yplkart.Object.Circuit;
import com.github.erozabesu.yplkart.Utils.Util;

public enum Message{
	messageVersion("1.0"),
	//コンフィグファイルからメッセージが取得できなかった際に読み込まれるデフォルトメッセージ郡です
	header(						"<yellow>[<plugin>] "),
	circuitheader(				"<darkaqua>[<aqua><circuitname><darkaqua>] "),
	noPermission(				"<header><red>権限を所有していません - <perm>"),

	//systemLoadPlugin("<header>v." + YPLKart.getInstance().getDescription().getVersion() + " Plugin has been Enabled"),
	//systemLoadFile("<header>v." + YPLKart.getInstance().getDescription().getVersion() + " Loaded <file> File"),
	//systemCreateFile("<header>v." + YPLKart.getInstance().getDescription().getVersion() + " Created <file> File"),

	referenceCircuitIngame(		"<green>/ka circuit info {circuit name} :<br>"
								+ "<green>/ka circuit create {circuit name} :<br>"
								+ "<green>/ka circuit create {circuit name} {worldname} {x} {y} {z} {yaw} {pitch} :<br>"
								+ "<green>/ka circuit delete {circuit name} :<br>"
								+ "<green>/ka circuit edit {circuit name} :<br>"
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
	referenceCircuitOutgame(	"<green>/ka circuit create {circuit name} {worldname} {x} {y} {z} {yaw} {pitch} :<br>"
								+ "<green>/ka circuit delete {circuit name} :<br>"
								+ "<green>/ka circuit setposition {circuit name} {worldname} {x} {y} {z} {yaw} {pitch} :<br>"
								+ "<green>/ka circuit setgoalposition {circuit name} {worldname} {x} {y} {z} {yaw} {pitch} :<br>"
								+ "<green>/ka circuit rename {circuit name} {new circuitname} :<br>"
								+ "<green>/ka circuit list :<br>"
								+ "<gold>Circuit List :<br><white><circuitlist>"),
	referenceDisplayIngame(		"<green>/ka display {kart name} :<br>"
								+ "<green>/ka display random :<br>"
								+ "<green>/ka display {kart name} {worldname} {x} {y} {z} {yaw} {pitch} :<br>"
								+ "<green>/ka display random {worldname} {x} {y} {z}  {yaw} {pitch} :<br>"
								+ "<gold>Kart List :<br><white><kartlist>"),
	referenceDisplayOutgame(	"<green>/ka display {kart name} {worldname} {x} {y} {z} {yaw} {pitch} :<br>"
								+ "<green>/ka display random {worldname} {x} {y} {z}  {yaw} {pitch} :<br>"
								+ "<gold>Kart List :<br><white><kartlist>"),
	referenceEntry(				"<green>/ka entry {circuit name} :"),
	referenceEntryOther(		"<green>/ka entry {player name} {circuit name} :<br>"
								+ "<green>/ka entry all {circuit name} :<br>"
								+ "<gold>Circuit List :<br><white><circuitlist>"),
	referenceExit(				"<green>/ka exit :"),
	referenceExitOther(			"<green>/ka exit {player name} :<br>"
								+ "<green>/ka exit all"),
	referenceMenu(				"<green>/ka menu :"),
	referenceMenuOther(			"<green>/ka menu {player name} :<br>"
								+ "<green>/ka menu all :"),
	referenceCharacter(			"<green>/ka character {character name} :<br>"
								+ "<green>/ka character random :"),
	referenceCharacterOther(	"<green>/ka character {player name} {character name} :<br>"
								+ "<green>/ka character all {character name} :<br>"
								+ "<green>/ka character {player name} random :<br>"
								+ "<green>/ka character all random :<br>"
								+ "<gold>Character List :<br><white><characterlist>"),
	referenceCharacterReset(	"<green>/ka characterreset :"),
	referenceCharacterResetOther("<green>/ka characterreset {player name} :<br>"
								+ "<green>/ka characterreset all :"),
	referenceRide(				"<green>/ka kart {kart name} :<br>"
								+ "<green>/ka kart random :"),
	referenceRideOther(			"<green>/ka kart {player name} {kart name} :<br>"
								+ "<green>/ka kart all {kart name} :<br>"
								+ "<green>/ka kart {player name} random :<br>"
								+ "<green>/ka kart all random :<br>"
								+ "<gold>Kart List :<br><white><kartlist>"),
	referenceLeave(				"<green>/ka leave :"),
	referenceLeaveOther(		"<green>/ka leave {player name} :<br>"
								+ "<green>/ka leave all :"),
	referenceRanking(			"<green>/ka ranking {circuit name} :<br>"
								+ "<green>/ka ranking list :"),
	referenceRankingOther(		"<green>/ka ranking {player name} {circuit name} :<br>"
								+ "<green>/ka ranking all {circuit name} :<br>"
								+ "<gold>Circuit List :<br><white><circuitlist>"),
	referenceAddItem(			"<green>/ka {item name} :<br>"
								+ "<green>/ka {item name} {amount} :"),
	referenceAddItemOther(		"<green>/ka {item name} {player name} :<br>"
								+ "<green>/ka {item name} {player name} {amount} :<br>"
								+ "<green>/ka {item name} all :<br>"
								+ "<green>/ka {item name} all {amount} :<br>"
								+ "<gold>Item List :<br><white><itemlist>"),
	reference(					"<green>===========-[ <gold><plugin> Command List<green> ]-===========<br>"
								+ "<green>/ka circuit :<br>"
								+ "<green>/ka display :<br>"
								+ "<green>/ka entry :<br>"
								+ "<green>" + referenceExit.getDefaultMessage() + "<br>"
								+ "<green>" + referenceExitOther.getDefaultMessage() + "<br>"
								+ "<green>" + referenceMenu.getDefaultMessage() + "<br>"
								+ "<green>" + referenceMenuOther.getDefaultMessage() + "<br>"
								+ "<green>/ka character :<br>"
								+ "<green>" + referenceCharacterReset.getDefaultMessage() + "<br>"
								+ "<green>" + referenceCharacterResetOther.getDefaultMessage() + "<br>"
								+ "<green>/ka ride :<br>"
								+ "<green>" + referenceLeave.getDefaultMessage() + "<br>"
								+ "<green>" + referenceLeaveOther.getDefaultMessage() + "<br>"
								+ "<green>/ka ranking :<br>"
								+ "<green>/ka item :<br>"
								+ "<green>/ka reload :<br>"
								+ "<green>===========================================<br>"),

	invalidPlayer(		"<header><red>指定したプレイヤーはログインしていません"),
	invalidWorld(		"<header><red>指定したワールドは存在しません"),
	invalidNumber(		"<header><red>数値を入力して下さい"),
	invalidBoolean(		"<header><white>true<red>、もしくは<white>false<red>と入力して下さい"),
	invalidCircuit(		"<header><red>サーキット：<gold><circuitname><red>は存在しません"),
	invalidCharacter(	"<header><red>キャラクター名が正しくありません<br><white><characterlist>"),
	invalidKart(		"<header><red>カート名が正しくありません<br><white><kartlist>"),

	cmdCircuitCreate(					"<circuitheader><green>サーキットを作成しました"),
	cmdCircuitAlreadyExist(				"<circuitheader><red>既に作成済みです"),
	cmdCircuitDelete(					"<circuitheader><green>サーキットを削除しました"),
	cmdCircuitList(						"<header><gold>Circuit List：<br><circuitlist>"),
	cmdCircuitEdit(						"<circuitheader>チェックポイントツールを配布しました"),
	cmdCircuitRename(					"<circuitheader>サーキット名を変更しました"),
	cmdCircuitRankingNoScoreData(		"<circuitheader><red>レースの記録がありません"),
	cmdCircuitSetPosition(				"<circuitheader><green>現在位置を開始座標として設定しました"),
	cmdCircuitSetLap(					"<circuitheader><green>周回数を<white><number>周<green>に設定しました"),
	cmdCircuitSetMinPlayer(				"<circuitheader><green>最小プレイ人数を<white><number>人<green>に設定しました"),
	cmdCircuitOutOfMaxPlayer(			"<circuitheader><red>最大プレイ人数を上回る数値は設定できません。現在の最大プレイ人数は<white><number>人<red>に設定されています"),
	cmdCircuitSetMaxPlayer(				"<circuitheader><green>最大プレイ人数を<white><number>人<green>に設定しました"),
	cmdCircuitOutOfMinPlayer(			"<circuitheader><red>最小プレイ人数を下回る数値は設定できません。現在の最小プレイ人数は<white><number>人<red>に設定されています"),
	cmdCircuitSetMatchingTime(			"<circuitheader><green>マッチング時間を<white><number>秒<green>に設定しました"),
	cmdCircuitSetMenuTime(				"<circuitheader><green>メニュー選択時間を<white><number>秒<green>に設定しました"),
	cmdCircuitSetLimitTime(				"<circuitheader><green>レース終了までの制限時間を<white><number>秒<green>に設定しました"),
	cmdCircuitSetBroadcastGoalMessage(	"<circuitheader><green>順位・ラップタイムのサーバー全体通知を<white><flag><green>に設定しました"),
	cmdMenuAll(							"<header><green>全プレイヤーにメニューを表示しました"),
	cmdDisplayCreate(					"<header><green>ディスプレイ専用<gold><kart><green>カートを設置しました"),
	cmdDisplayDelete(					"<header><green>ディスプレイ専用カートを削除しました"),
	cmdMenuOther(						"<header><white><player><green>さんにメニューを表示しました"),
	cmdEntryOther(						"<header><white><player><green>さんをサーキット：<gold><circuitname><green>にエントリーしました"),
	cmdEntryAll(						"<header><green>全プレイヤーをサーキット：<gold><circuitname><green>にエントリーしました"),
	cmdExitAll(							"<header><green>全プレイヤーのエントリーを取り消しました"),
	cmdExitOther(						"<header><white><player><green>さんのエントリーを取り消しました"),
	cmdCharacterAll(					"<header><green>全プレイヤーのキャラクターを<white><character><green>にセットしました"),
	cmdCharacterRandomAll(				"<header><green>全プレイヤーのキャラクターを<white>ランダム<green>にセットしました"),
	cmdCharacterOther(					"<header><white><player><green>さんのキャラクターを<white><character><green>にセットしました"),
	cmdCharacterResetAll(				"<header><green>全プレイヤーのキャラクター選択を取り消しました"),
	cmdCharacterResetOther(				"<header><white><player><green>さんのキャラクター選択を取り消しました"),
	cmdRideAll(							"<header><green>全プレイヤーを<white><kart>カート<green>に搭乗させました"),
	cmdRideRandomAll(					"<header><green>全プレイヤーを<white>ランダムカート<green>に搭乗させました"),
	cmdRideOther(						"<header><white><player><green>さんを<white><kart>カート<green>に搭乗させました"),
	cmdLeaveAll(						"<header><green>全プレイヤーの搭乗を解除しました"),
	cmdLeaveOther(						"<header><white><player><green>さんの搭乗を解除しました"),
	cmdRankingOther(					"<header><white><player><green>さんにサーキット：<gold><circuitname><green>のランキングを表示しました"),
	cmdRankingAll(						"<header><green>全プレイヤーにサーキット：<gold><circuitname><green>のランキングを表示しました"),
	cmdItem(							"<header><white><item><green>を配布しました"),
	cmdItemOther(						"<header><white><player><green>さんに<white><item><green>を配布しました"),
	cmdItemAll(							"<header><green>全プレイヤーに<white><item><green>を配布しました"),
	cmdReload(							"<header><green>コンフィグをリロードしました"),

	raceStart(					"<circuitheader><green>レースを開始します"),
	raceEnd(					"<circuitheader><green>レースが終了しました"),
	raceReady(					"<circuitheader><green>レースを開始する準備が整いました<br><circuitheader><green>↓↓↓のチャットをクリックして参加を確定して下さい"),
	raceMatchingFailed(			"<circuitheader><green>辞退者が出たため再マッチングを行います。規定人数が揃うまでお待ち下さい"),
	raceAccept(					"<circuitheader><green>レース参加を承認しました。準備が整うまでお待ち下さい"),
	raceTimeLimitAlert(			"<circuitheader><red>-------- <aqua>残り時間<white><number>秒<aqua>！ <red>--------"),
	raceTimeLimitCountDown(		"<circuitheader><red>--------        <aqua><white><number><aqua>！       <red>--------"),
	raceTimeUp(					"<circuitheader><red>-------- <aqua>タイムアップ！  <red>--------"),

	raceGoal(					"<circuitheader><white><player><green>さん<yellow><number1>位<green>でゴール！ <white>Time : <yellow><number2><white>秒"),
	raceHighScore(				"<circuitheader><green>記録更新！<yellow><number1><green>秒 --> <yellow><number2><green>秒"),
	raceUpdateLap(				"<circuitheader><white><number><green>周目突入！"),
	raceReverseRun(				"<circuitheader><red>逆走"),
	racePlayerDead(				"<circuitheader><white><player> is dead"),
	racePlayerKill(				"<circuitheader><white><player> is dead"),

	raceEntry(					"<circuitheader><green>エントリーしました"),
	raceEntryAlready(			"<circuitheader><red>既にエントリーしています。他のレースにエントリーしたい場合は現在のエントリーを取り消して下さい"),
	raceEntryAlreadyStart(		"<circuitheader><green>エントリーしました。既にレースが開始されているため、次回開催されるレースにエントリーされました"),
	raceEntryFull(				"<circuitheader><green>エントリーしました。既に規定人数が満たされているため、次回開催されるレースにエントリーされました"),
	raceExit(					"<circuitheader><green>エントリーを取り消しました"),

	raceCharacter(				"<circuitheader><green>キャラクター<gold><character><green>を選択しました"),
	raceKart(					"<circuitheader><white><kart>カート<green>に搭乗しました"),
	raceCharacterReset(			"<circuitheader><green>キャラクター選択を取り消しました"),
	raceLeave(					"<circuitheader><green>搭乗を解除しました"),
	raceMustSelectCharacter(	"<circuitheader><red>キャラクターを選択して下さい"),
	raceMustSelectKart(			"<circuitheader><red>カートを選択して下さい"),
	raceNotStarted(				"<circuitheader><red>レースが開始されていないため利用できません"),

	raceInteractBanana(			"<circuitheader><red>バナナに引っ掛かった！"),
	raceInteractItemBox(		"<circuitheader><white><item><green>ゲット！"),
	raceInteractItemBoxFailed(	"<circuitheader><red>抽選対象の全アイテムにおいてパーミッションを所有していなかったため、アイテムを獲得できませんでした"),
	raceInteractFakeItemBox(	"<circuitheader><red>偽アイテムブロックだ！"),

	itemRemoveItemBox(			"<header><green>アイテムボックスを削除しました"),
	itemRemoveCheckPoint(		"<header><green>チェックポイントを削除しました"),
	itemNoPlayer(				"<circuitheader><red>レース参加者が居ないため使用できません"),
	itemNoHigherPlayer(			"<circuitheader><red>自分より上位のプレイヤーが居ないため使用できません"),
	itemNoCheckpoint(			"<circuitheader><red>周囲に未通過のチェックポイントがないため使用できません"),
	itemHighestPlayer(			"<circuitheader><red>1位のプレイヤーは<white><item><red>を使えません"),
	itemAlreadyUsing(			"<circuitheader><red>既に使用中です"),
	itemTeresaNoItem(			"<circuitheader><red>アイテムを持っていなかった！"),
	itemTeresaRob(				"<circuitheader><white><item><green>を盗んだ！"),
	itemTeresaRobbed(			"<circuitheader><white><item><red>を盗まれた！"),

	titleDeathPanalty(			"<red>DEATH PENALTY"),
	titleUsingKiller(			"<red>AUTO CONTROL"),
	titleRacePrepared(			"<white>レースの準備が整いました"),
	titleRaceMenu(				"<aqua>メニュー選択時間"),
	titleRaceStandby(			"<red>Standby"),
	titleRaceStandbySub(		""),
	titleRaceLaps(				"<red><number>"),
	titleRaceLapsSub(			"<gold>Laps"),
	titleRaceTimeLimit(			"<red><number>"),
	titleRaceTimeLimitSub(		"<gold>Time Limit"),
	titleRaceReady(				"<red>Ready"),
	titleRaceReadySub(			""),
	titleRaceStartCountDown(	"<red><number>"),
	titleRaceStartCountDownSub(	""),
	titleCountDown(				"<yellow>残り時間 : <number>秒"),
	titleGoalRank(				"<green><number1>位  <number2>秒"),

	tableCircuitInformation(
			"<aqua>==========-[ <gold>Circuit Information <aqua>]-==========<br>" +
			"    " + "<green>サーキット名 ： <white><circuitname><br>" +
			"    " + "<green>周回数 ： <white><number1><br>" +
			"    " + "<green>最小プレイ人数 ： <white><number2><br>" +
			"    " + "<green>最大プレイ人数 ： <white><number3><br>" +
			"    " + "<green>レースが自動終了するまでの時間 ： <white><number4><green> (秒)<br>" +
			"    " + "<green>キャラクター・カートを選択できる猶予時間 ： <white><number5><green> (秒)<br>" +
			"    " + "<green>レースへの参加・辞退を決定できる猶予時間 ： <white><number6><green> (秒)<br>" +
			"    " + "<green>順位・ラップタイムのサーバー全体通知 ： <white><flag><br>" +
			"    " + "<green>レース開始座標 ： " + "<br>" +
			"            " + "<green>x <white><number7><green> / y <white><number8><green> / z <white><number9><br>" +
			"            " + "<green>yaw <white><number10><green> / pitch <white><number11><br>"),
	tableCharacterParameter(
			"<darkaqua><green> 体力 : <yellow><text1><br>" +
			"<darkaqua><green> 速さ : <yellow><text2><br>" +
			"<darkaqua><green> アイテムスロット : <yellow><text3><br>" +
			"<darkaqua><green> 最大スタック数 : <yellow><text4><br>" +
			"<darkaqua><green> 妨害アイテム威力補正 : <yellow><text5><br>" +
			"<darkaqua><green> リスキル耐性 : <yellow><text6><br>" +
			"<darkaqua><green> デスペナルティの長さ : <blue><text7><br>" +
			"<darkaqua><green> デスペナルティ時の速さ : <blue><text8><br>" +
			"<darkaqua><green> 速度強化アイテム補正(LV) : <yellow><text9><br>" +
			"<darkaqua><green> 速度強化アイテム補正(秒) : <yellow><text10><br>" +
			"<darkaqua><green> 速度低下アイテム補正(LV) : <yellow><text11><br>" +
			"<darkaqua><green> 速度低下アイテム補正(秒) : <yellow><text12>"),
	tableKartParameter(
			"<darkaqua><green> 重量 : <yellow><text1><br>" +
			"<darkaqua><green> 最高速度 : <yellow><text2><br>" +
			"<darkaqua><green> 加速 : <yellow><text3><br>" +
			"<darkaqua><green> カーブ性能 : <yellow><text4><br>" +
			"<darkaqua><green> ドリフト時のカーブ性能 : <yellow><text5><br>" +
			"<darkaqua><green> ドリフト時の減速率 : <yellow><text6><br>" +
			"<darkaqua><green> ダート走行時の減速率 : <yellow><text7><br>" +
			"<darkaqua><green> 乗り越えられるブロックの高さ : <yellow><text8><br>");

	private static Logger log = Logger.getLogger("Minecraft");
	private static String filename = "message.yml";
	private static File datafolder;
	private static File configFile;
	private static FileConfiguration config;
	private static List<String> ignorelist =  Arrays.asList("system", "reference");

	private String defaultmessage;
	private String message;

	private Message(String defaultmessage){
		this.defaultmessage = defaultmessage;
	}

	// 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

	public String getMessage(){
		return this.message;
	}

	private String getDefaultMessage(){
		return this.defaultmessage;
	}

	private void setMessage(String newmessage){
		this.message = newmessage;
	}

	private static String getData(String path, String defaultmessage){
		if(!config.contains(path))config.set(path, defaultmessage);
		return config.getString(path);
	}

	// 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

	public void sendMessage(Object adress){
		sendMessage(adress, getMessage());
	}

	public void sendMessage(Object adress, Object object){
		sendMessage(adress, getMessage(object));
	}

	public void sendMessage(Object adress, Object[] object){
		sendMessage(adress, getMessage(object));
	}

	private void sendMessage(Object adress, String message){
		if(message == null)return;
		if(message.length() == 0)return;

		Player p = null;
		if(adress instanceof Player)
			p = (Player) adress;
		else if(adress instanceof UUID)
			p = Bukkit.getPlayer((UUID) adress);

		for (String line : replaceLine(message)) {
			if (p != null)
				p.sendMessage(line);
			else
				log.log(Level.INFO, ChatColor.stripColor(line));
		}
	}

	public String getMessage(Object object){
		return replaceLimitedTags(getMessage(), object);
	}

	public String getMessage(Object[] object){
		String message = getMessage();
		for(int i = 0; i < object.length; i++){
			message = replaceLimitedTags(message, object[i]);
		}
		return message;
	}

	//サーキットのランキング表のような、タグ置換ではどうしようもないStringを送信する際に用います
	//極力使用しません。ある意味挫折の結果です
	public static void sendAbsolute(Object adress, String message){
		Player p = null;
		if(adress instanceof Player)
			p = (Player) adress;
		else if(adress instanceof UUID)
			p = Bukkit.getPlayer((UUID) adress);

		for (String line : replaceLine(message)) {
			if (p != null)
				p.sendMessage(line);
			else
				log.log(Level.INFO, ChatColor.stripColor(line));
		}
	}

	// 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

	public static List<String> replaceLine(String message){
		List<String> newtext = new ArrayList<String>();
		for (String line : message.split("<br>")){
			newtext.add(line);
		}
		return newtext;
	}

	public static String replaceChatColor(String text){
		text = text.replace("<black>", ChatColor.BLACK.toString());
		text = text.replace("<white>", ChatColor.WHITE.toString());
		text = text.replace("<gray>", ChatColor.GRAY.toString());
		text = text.replace("<darkgray>", ChatColor.DARK_GRAY.toString());
		text = text.replace("<red>", ChatColor.RED.toString());
		text = text.replace("<darkRed>", ChatColor.DARK_RED.toString());
		text = text.replace("<green>", ChatColor.GREEN.toString());
		text = text.replace("<darkgreen>", ChatColor.DARK_GREEN.toString());
		text = text.replace("<blue>", ChatColor.BLUE.toString());
		text = text.replace("<darkblue>", ChatColor.DARK_BLUE.toString());
		text = text.replace("<yellow>", ChatColor.YELLOW.toString());
		text = text.replace("<gold>", ChatColor.GOLD.toString());
		text = text.replace("<lightpurple>", ChatColor.LIGHT_PURPLE.toString());
		text = text.replace("<darkpurple>", ChatColor.DARK_PURPLE.toString());
		text = text.replace("<aqua>", ChatColor.AQUA.toString());
		text = text.replace("<darkaqua>", ChatColor.DARK_AQUA.toString());
		text = text.replace("<magic>", ChatColor.MAGIC.toString());
		return text;
	}

	private static String replaceBasicTags(String message){
		message = message.replace("<header>", getData(header.name(), header.getDefaultMessage()));
		message = message.replace("<circuitheader>", getData(circuitheader.name(), circuitheader.getDefaultMessage()));
		message = message.replace("<plugin>", YPLKart.plname);
		message = message.replace("<itemlist>", EnumItem.getItemList().toLowerCase());
		message = message.replace("<circuitlist>", RaceData.getCircuitList());
		message = message.replace("<characterlist>", EnumCharacter.getCharacterList());
		message = message.replace("<kartlist>", EnumKarts.getKartList());
		replaceChatColor(message);

		return message;
	}

	private static String replaceLimitedTags(String basemessage, Object object){
		if(object == null)return basemessage;

		if(object instanceof Number){
			basemessage = basemessage.replace("<number>", String.valueOf((Number)object));
		}else if(object instanceof Number[]){
			Number[] num = (Number[]) object;
			for(int i = 0;i < num.length;i++){
				int tagnumber = i + 1;
				basemessage = basemessage.replace("<number" + tagnumber + ">", String.valueOf(num[i]));
			}
		}else if(object instanceof String){
			if(basemessage.contains("<circuitname>")){
				if(RaceData.getCircuitSet().contains((String)object))
					basemessage = basemessage.replace("<circuitname>", Util.convertInitialUpperString((String)object));
			}
			if(basemessage.contains("<perm>")){
				basemessage = basemessage.replace("<perm>", ((String)object));
			}
		}else if(object instanceof String[]){
			String[] text = (String[]) object;
			for(int i = 0;i < text.length;i++){
				int tagnumber = i + 1;
				basemessage = basemessage.replace("<text" + tagnumber + ">", text[i]);
			}
		}else if(object instanceof Boolean){
			basemessage = basemessage.replace("<flag>", String.valueOf((boolean)object));
		}else if(object instanceof Player){
			basemessage = basemessage.replace("<player>", ((Player)object).getName());
		}else if(object instanceof Player[]){
			for(int i = 0;i < ((Player[])object).length;i++){
				int tagnumber = i + 1;
				basemessage = basemessage.replace("<player" + tagnumber + ">", String.valueOf(((Player[])object)[i].getName()));
			}
		}else if(object instanceof Circuit){
			basemessage = basemessage.replace("<circuitname>", Util.convertInitialUpperString(((Circuit)object).getName()));
		}else if(object instanceof EnumCharacter){
			basemessage = basemessage.replace("<character>", ((EnumCharacter)object).getName());
		}else if(object instanceof EnumKarts){
			basemessage = basemessage.replace("<kart>", ((EnumKarts)object).getName());
		}else if(object instanceof ItemStack){
			basemessage = basemessage.replace("<item>", ChatColor.stripColor(((ItemStack)object).getItemMeta().getDisplayName()));
		}else if(object instanceof File){
			basemessage = basemessage.replace("<file>", ((File)object).getName());
		}

		return basemessage;
	}

	private static String replaceCommandReferenceParam(String text){
		text = text.replace("{race type}", 			"<green>{<white>race type<green>}");
		text = text.replace("{circuit name}", 		"<green>{<white>circuit name<green>}");
		text = text.replace("{worldname}", 			"<green>{<white>worldname<green>}");
		text = text.replace("{x}", 					"<green>{<white>x<green>}");
		text = text.replace("{y}", 					"<green>{<white>y<green>}");
		text = text.replace("{z}", 					"<green>{<white>z<green>}");
		text = text.replace("{yaw}", 				"<green>{<white>yaw<green>}");
		text = text.replace("{pitch}", 				"<green>{<white>pitch<green>}");
		text = text.replace("{new circuitname}", 	"<green>{<white>new circuitname<green>}");
		text = text.replace("{player name}", 		"<green>{<white>player name<green>}");
		text = text.replace("{character name}", 	"<green>{<white>character name<green>}");
		text = text.replace("{kart name}", 			"<green>{<white>kart name<green>}");
		text = text.replace("{item name}", 			"<green>{<white>item name<green>}");
		text = text.replace("{amount}", 			"<green>{<white>amount<green>}");
		text = text.replace("{number of player}", 	"<green>{<white>number of player<green>}");
		text = text.replace("{number of second}", 	"<green>{<white>number of second<green>}");
		text = text.replace("{number of laps}", 	"<green>{<white>number of laps<green>}");
		text = text.replace("{true or false}", 		"<green>{<white>true <green>or <white>false<green>}");

		return text;
	}

	// 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

	private static void loadConfig() {
		datafolder = YPLKart.getInstance().getDataFolder();

		configFile = new File(datafolder, filename);
		config = YamlConfiguration.loadConfiguration(configFile);

		createConfig();

		for(Message message : values()){
			String newmessage = getData(message.name(), message.getDefaultMessage());

			newmessage = replaceBasicTags(newmessage);
			newmessage = replaceCommandReferenceParam(newmessage);
			newmessage = replaceChatColor(newmessage);

			message.setMessage(newmessage);
		}

		saveConfigFile();
	}

	private static void createConfig() {
		if(!(configFile.exists())){
			Util.copyResource(filename);
			configFile = new File(datafolder, filename);
			config = YamlConfiguration.loadConfiguration(configFile);
		}
	}

	private static void saveConfigFile(){
		Util.saveConfiguration(configFile, config, ignorelist);
	}

	public static void reloadConfig(){
		configFile = new File(datafolder, filename);
		config = YamlConfiguration.loadConfiguration(configFile);
		loadConfig();
	}
}
