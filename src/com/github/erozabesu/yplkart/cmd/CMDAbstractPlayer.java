package com.github.erozabesu.yplkart.cmd;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.erozabesu.yplkart.ConfigManager;
import com.github.erozabesu.yplkart.Permission;
import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.data.CharacterConfig;
import com.github.erozabesu.yplkart.data.CircuitConfig;
import com.github.erozabesu.yplkart.data.DisplayKartConfig;
import com.github.erozabesu.yplkart.data.ItemEnum;
import com.github.erozabesu.yplkart.data.KartConfig;
import com.github.erozabesu.yplkart.data.MessageEnum;
import com.github.erozabesu.yplkart.data.SystemMessageEnum;
import com.github.erozabesu.yplkart.enumdata.Particle;
import com.github.erozabesu.yplkart.object.Character;
import com.github.erozabesu.yplkart.object.CircuitData;
import com.github.erozabesu.yplkart.object.Kart;
import com.github.erozabesu.yplkart.utils.PacketUtil;
import com.github.erozabesu.yplkart.utils.Util;

public class CMDAbstractPlayer extends CMDAbstract {

    /** コマンド実行プレイヤー */
    private Player player;

    /** コマンド実行プレイヤーのUUID */
    private UUID uuid;

    /** コマンドキー配列 */
    private String[] args;

    /** コマンドキー配列の長さ */
    private int length;

    //〓 main 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public CMDAbstractPlayer(Player player, String[] args) {
        setPlayer(player);
        setUUID(player.getUniqueId());
        setArgs(args);
        setLength(args.length);
    }

    //〓 getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @return player コマンド実行プレイヤー */
    public Player getPlayer() {
        return this.player;
    }

    /** @return uuid コマンド実行プレイヤーのUUID */
    public UUID getUUID() {
        return this.uuid;
    }

    /** @return args コマンドキー配列 */
    public String[] getArgs() {
        return this.args;
    }

    /** @return length コマンドキー配列の長さ */
    public int getLength() {
        return this.length;
    }

    //〓 setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @param player セットするコマンド実行プレイヤー */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /** @param uuid セットするコマンド実行プレイヤーのUUID */
    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    /** @param args セットするコマンドキー配列 */
    public void setArgs(String[] args) {
        this.args = args;
    }

    /** @param length セットするコマンドキー配列の長さ */
    public void setLength(int length) {
        this.length = length;
    }

    //〓 do 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    @Override
    void ka() {
        if (!Permission.hasCMDPermission(this.getPlayer(), Permission.CMD_KA, false, false))
            return;

        SystemMessageEnum.reference.sendConvertedMessage(this.getPlayer());
    }

    //ka circuit info {circuit name}
    //ka circuit create {circuit name}
    //ka circuit create {circuit name} {world} {x} {y} {z} {yaw} {pitch}
    //ka circuit delete {circuit name}
    //ka circuit edit {circuit name}
    //ka circuit broadcastgoal {circuit name} {true or false}
    //ka circuit setlap {circuit name} {number of laps}
    //ka circuit setminplayer {circuit name} {number of player}
    //ka circuit setmaxplayer {circuit name} {number of player}
    //ka circuit setmatchingtime {circuit name} {number of second}
    //ka circuit setmenutime {circuit name} {number of second}
    //ka circuit setposition {circuit name}
    //ka circuit setposition {circuit name} {worldname} {x} {y} {z} {yaw} {pitch}
    //ka circuit rename {circuit name} {new circuitname}
    //ka circuit list

    //ka circuit accept
    //ka circuit deny
    @Override
    void circuit() {
        if (this.getLength() == 2) {
            if (getArgs()[1].equalsIgnoreCase("accept")) {
                RaceManager.setMatchingCircuitData(this.getUUID());
                return;
            } else if (getArgs()[1].equalsIgnoreCase("deny")) {
                RaceManager.clearMatchingCircuitData(this.getUUID());
                return;
            }
        }

        if (!Permission.hasCMDPermission(this.getPlayer(), Permission.OP_CMD_CIRCUIT, false, false))
            return;
        if (this.getLength() == 2) {
            if (getArgs()[1].equalsIgnoreCase("list")) {
                MessageEnum.cmdCircuitList.sendConvertedMessage(this.getPlayer());
                return;
            }
        } else if (this.getLength() == 3) {
            if (getArgs()[1].equalsIgnoreCase("info")) {
                CircuitData circuitData = CircuitConfig.getCircuitData(getArgs()[2]);
                if (circuitData != null) {
                    circuitData.sendInformation(this.getPlayer());
                }
                return;
            } else if (getArgs()[1].equalsIgnoreCase("create")) {
                CircuitConfig.createCircuit(this.getPlayer(), this.getPlayer().getLocation(), getArgs()[2]);
                return;
            } else if (getArgs()[1].equalsIgnoreCase("delete")) {
                CircuitConfig.deleteCircuit(this.getPlayer(), getArgs()[2]);
                return;
            } else if (getArgs()[1].equalsIgnoreCase("edit")) {
                ItemEnum.addCheckPointTool(this.getPlayer(), getArgs()[2]);
                return;
            } else if (getArgs()[1].equalsIgnoreCase("setposition")) {
                CircuitConfig.setStartPosition(this.getPlayer(), getArgs()[2], this.getPlayer().getLocation());
                return;
            }
        } else if (this.getLength() == 4) {
            if (getArgs()[1].equalsIgnoreCase("rename")) {
                CircuitConfig.renameCircuit(this.getPlayer(), getArgs()[2], getArgs()[3]);
                return;
            } else if (getArgs()[1].equalsIgnoreCase("setminplayer")) {
                if (!Util.isNumber(getArgs()[3])) {
                    MessageEnum.invalidNumber.sendConvertedMessage(this.getPlayer());
                    return;
                }
                CircuitConfig.setMinPlayer(this.getPlayer(), getArgs()[2], Integer.valueOf(getArgs()[3]));
                return;
            } else if (getArgs()[1].equalsIgnoreCase("setmaxplayer")) {
                if (!Util.isNumber(getArgs()[3])) {
                    MessageEnum.invalidNumber.sendConvertedMessage(this.getPlayer());
                    return;
                }
                CircuitConfig.setMaxPlayer(this.getPlayer(), getArgs()[2], Integer.valueOf(getArgs()[3]));
                return;
            } else if (getArgs()[1].equalsIgnoreCase("setmatchingtime")) {
                if (!Util.isNumber(getArgs()[3])) {
                    MessageEnum.invalidNumber.sendConvertedMessage(this.getPlayer());
                    return;
                }
                CircuitConfig.setMatchingTime(this.getPlayer(), getArgs()[2], Integer.valueOf(getArgs()[3]));
                return;
            } else if (getArgs()[1].equalsIgnoreCase("setmenutime")) {
                if (!Util.isNumber(getArgs()[3])) {
                    MessageEnum.invalidNumber.sendConvertedMessage(this.getPlayer());
                    return;
                }
                CircuitConfig.setMenuTime(this.getPlayer(), getArgs()[2], Integer.valueOf(getArgs()[3]));
                return;
            } else if (getArgs()[1].equalsIgnoreCase("setlimittime")) {
                if (!Util.isNumber(getArgs()[3])) {
                    MessageEnum.invalidNumber.sendConvertedMessage(this.getPlayer());
                    return;
                }
                CircuitConfig.setLimitTime(this.getPlayer(), getArgs()[2], Integer.valueOf(getArgs()[3]));
                return;
            } else if (getArgs()[1].equalsIgnoreCase("setlap")) {
                if (!Util.isNumber(getArgs()[3])) {
                    MessageEnum.invalidNumber.sendConvertedMessage(this.getPlayer());
                    return;
                }
                CircuitConfig.setNumberOfLaps(this.getPlayer(), getArgs()[2], Integer.valueOf(getArgs()[3]));
                return;
            } else if (getArgs()[1].equalsIgnoreCase("broadcastgoal")) {
                if (!Util.isBoolean(getArgs()[3])) {
                    MessageEnum.invalidBoolean.sendConvertedMessage(this.getPlayer());
                    return;
                }
                CircuitConfig.setBroadcastGoalMessage(this.getPlayer(), getArgs()[2], Boolean.valueOf(getArgs()[3]));
                return;
            }
        } else if (this.getLength() == 9) {
            if (Bukkit.getWorld(getArgs()[3]) == null) {
                MessageEnum.invalidWorld.sendConvertedMessage(this.getPlayer());
                return;
            }
            if (!Util.isNumber(getArgs()[4]) || !Util.isNumber(getArgs()[5]) || !Util.isNumber(getArgs()[6])
                    || !Util.isNumber(getArgs()[7]) || !Util.isNumber(getArgs()[8])) {
                MessageEnum.invalidNumber.sendConvertedMessage(this.getPlayer());
                return;
            }
            //0:circuit 1:create 2:circuitname 3:worldname 4:x 5:y 6:z 7:pitch 8:yaw
            if (getArgs()[1].equalsIgnoreCase("create")) {
                World world = Bukkit.getWorld(getArgs()[3]);
                if (world == null) {
                    MessageEnum.invalidWorld.sendConvertedMessage(this.getPlayer());
                }
                Location location = new Location(world, Double.valueOf(getArgs()[4]), Double.valueOf(getArgs()[5])
                        ,Double.valueOf(getArgs()[6]), Float.valueOf(getArgs()[7]), Float.valueOf(getArgs()[8]));
                CircuitConfig.createCircuit(this.getPlayer(), location, getArgs()[2]);
                return;

            //0:circuit 1:setposition 2:circuitname 3:worldname 4:x 5:y 6:z 7:pitch 8:yaw
            } else if (getArgs()[1].equalsIgnoreCase("setposition")) {
                World world = Bukkit.getWorld(getArgs()[3]);
                if (world == null) {
                    MessageEnum.invalidWorld.sendConvertedMessage(this.getPlayer());
                }
                Location location = new Location(world, Double.valueOf(getArgs()[4]), Double.valueOf(getArgs()[5])
                        ,Double.valueOf(getArgs()[6]), Float.valueOf(getArgs()[7]), Float.valueOf(getArgs()[8]));
                CircuitConfig.setStartPosition(this.getPlayer(), getArgs()[2], location);
                return;
            }
        }
        SystemMessageEnum.referenceCircuitIngame.sendConvertedMessage(this.getPlayer());
    }

    //ka display {kart name}
    //ka display random
    //ka display {kart name} {worldname} {x} {y} {z} {yaw} {pitch}
    //ka display random {worldname} {x} {y} {z}  {yaw} {pitch}
    @Override
    void display() {
        if (getLength() == 2) {
            if (!Permission.hasCMDPermission(this.getPlayer(), Permission.OP_CMD_DISPLAY, false, false))
                return;
            Kart kart = null;
            if (getArgs()[1].equalsIgnoreCase("random"))
                kart = KartConfig.getRandomKart();
            else
                kart = KartConfig.getKart(getArgs()[1]);
            if (kart == null) {
                MessageEnum.invalidKart.sendConvertedMessage(this.getPlayer());
                return;
            }

            RaceManager.createDisplayKart(this.getPlayer().getLocation(), kart, null);
            MessageEnum.cmdDisplayCreate.sendConvertedMessage(this.getPlayer(), kart);
        } else if (getLength() == 8) {
            if (!Permission.hasCMDPermission(this.getPlayer(), Permission.OP_CMD_DISPLAY, false, false))
                return;
            if (Bukkit.getWorld(getArgs()[2]) == null) {
                MessageEnum.invalidWorld.sendConvertedMessage(this.getPlayer());
                return;
            }
            if (!Util.isNumber(getArgs()[3]) || !Util.isNumber(getArgs()[4]) || !Util.isNumber(getArgs()[5])
                    || !Util.isNumber(getArgs()[6]) || !Util.isNumber(getArgs()[7])) {
                MessageEnum.invalidNumber.sendConvertedMessage(this.getPlayer());
                return;
            }
            Kart kart = null;
            if (getArgs()[1].equalsIgnoreCase("random"))
                kart = KartConfig.getRandomKart();
            else
                kart = KartConfig.getKart(getArgs()[1]);
            if (kart == null) {
                MessageEnum.invalidKart.sendConvertedMessage(this.getPlayer());
                return;
            }

            RaceManager.createDisplayKart(
                    new Location(Bukkit.getWorld(getArgs()[2]), Double.valueOf(getArgs()[3]), Double.valueOf(getArgs()[4]), Double
                            .valueOf(getArgs()[5]), Float.valueOf(getArgs()[6]), Float.valueOf(getArgs()[7])), kart, null);
            MessageEnum.cmdDisplayCreate.sendConvertedMessage(this.getPlayer(), kart);
        } else {
            SystemMessageEnum.referenceDisplayIngame.sendConvertedMessage(this.getPlayer());
        }
    }

    //ka menu
    //ka menu {player}
    //ka menu all
    @Override
    void menu() {
        if (this.getLength() == 1) {
            if (!Permission.hasCMDPermission(this.getPlayer(), Permission.CMD_MENU, false, false))
                return;
            RaceManager.showSelectMenu(this.getPlayer(), true);
        } else if (getLength() == 2) {
            if (!Permission.hasCMDPermission(this.getPlayer(), Permission.CMD_MENU, true, false))
                return;

            if (getArgs()[1].equalsIgnoreCase("all")) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    RaceManager.showSelectMenu(other, true);
                }
                MessageEnum.cmdMenuAll.sendConvertedMessage(this.getPlayer());
            } else {
                if (!Util.isOnline(getArgs()[1])) {
                    MessageEnum.invalidPlayer.sendConvertedMessage(this.getPlayer());
                    return;
                }

                Player other = Bukkit.getPlayer(getArgs()[1]);
                RaceManager.showSelectMenu(other, true);
                MessageEnum.cmdMenuOther.sendConvertedMessage(this.getPlayer(), other);
            }
        } else {
            SystemMessageEnum.referenceMenu.sendConvertedMessage(this.getPlayer());
            SystemMessageEnum.referenceMenuOther.sendConvertedMessage(this.getPlayer());
        }
    }

    //ka entry {circuit name}
    //ka entry {player name} {circuit name}
    //ka entry all {circuit name}
    @Override
    void entry() {
        //ka entry {circuit name}
        if (this.getLength() == 2) {
            if (!Permission.hasCMDPermission(this.getPlayer(), Permission.CMD_ENTRY, false, false))
                return;
            if (CircuitConfig.getCircuitData(getArgs()[1]) == null) {
                MessageEnum.invalidCircuit.sendConvertedMessage(this.getPlayer(), getArgs()[1]);
                return;
            }

            RaceManager.setEntryRaceData(this.getUUID(), getArgs()[1]);
            //ka entry {player name} {circuit name}
            //ka entry all {circuit name}
        } else if (this.getLength() == 3) {
            if (!Permission.hasCMDPermission(this.getPlayer(), Permission.CMD_ENTRY, true, false))
                return;
            if (CircuitConfig.getCircuitData(getArgs()[2]) == null) {
                MessageEnum.invalidCircuit.sendConvertedMessage(this.getPlayer(), getArgs()[1]);
                return;
            }
            if (getArgs()[1].equalsIgnoreCase("all")) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    RaceManager.setEntryRaceData(other.getUniqueId(), getArgs()[2]);
                }
                MessageEnum.cmdEntryAll.sendConvertedMessage(this.getPlayer(), getArgs()[2]);
            } else {
                if (!Util.isOnline(getArgs()[1])) {
                    MessageEnum.invalidPlayer.sendConvertedMessage(this.getPlayer());
                    return;
                }
                Player other = Bukkit.getPlayer(getArgs()[1]);
                MessageEnum.cmdEntryOther.sendConvertedMessage(this.getPlayer(), new Object[] { other, getArgs()[2] });
                RaceManager.setEntryRaceData(other.getUniqueId(), getArgs()[2]);
            }
        } else {
            SystemMessageEnum.referenceEntry.sendConvertedMessage(this.getPlayer());
            SystemMessageEnum.referenceEntryOther.sendConvertedMessage(this.getPlayer());
        }
    }

    //ka exit
    //ka exit {player}
    //ka exit all
    @Override
    void exit() {
        if (this.getLength() == 1) {
            if (!Permission.hasCMDPermission(this.getPlayer(), Permission.CMD_EXIT, false, false))
                return;
            RaceManager.clearEntryRaceData(this.getUUID());
        } else if (getLength() == 2) {
            if (!Permission.hasCMDPermission(this.getPlayer(), Permission.CMD_EXIT, true, false))
                return;

            if (getArgs()[1].equalsIgnoreCase("all")) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    RaceManager.clearEntryRaceData(other.getUniqueId());
                }
                MessageEnum.cmdExitAll.sendConvertedMessage(this.getPlayer());
            } else {
                if (!Util.isOnline(getArgs()[1])) {
                    MessageEnum.invalidPlayer.sendConvertedMessage(this.getPlayer());
                    return;
                }

                Player other = Bukkit.getPlayer(getArgs()[1]);
                RaceManager.clearEntryRaceData(other.getUniqueId());
                MessageEnum.cmdExitOther.sendConvertedMessage(this.getPlayer(), other);
            }
        } else {
            SystemMessageEnum.referenceExit.sendConvertedMessage(this.getPlayer());
            SystemMessageEnum.referenceExitOther.sendConvertedMessage(this.getPlayer());
        }
    }

    //ka character {character name}
    //ka character random
    //ka character {player} {character name}
    //ka character all {character name}
    //ka character {player} random
    //ka character all random
    @Override
    void character() {
        if (this.getLength() == 2) {
            if (!Permission.hasCMDPermission(this.getPlayer(), Permission.CMD_CHARACTER, false, false))
                return;
            if (getArgs()[1].equalsIgnoreCase("random")) {
                RaceManager.setCharacterRaceData(this.getUUID(), CharacterConfig.getRandomCharacter());
                //ka character {character name}
            } else {
                Character character = CharacterConfig.getCharacter(getArgs()[1]);
                if (character == null) {
                    MessageEnum.invalidCharacter.sendConvertedMessage(this.getPlayer());
                    return;
                }
                RaceManager.setCharacterRaceData(this.getUUID(), character);
            }
            //ka character {player} {character name}
            //ka character all {character name}
            //ka character {player} random
            //ka character all random
        } else if (this.getLength() == 3) {
            if (!Permission.hasCMDPermission(this.getPlayer(), Permission.CMD_CHARACTER, true, false))
                return;
            if (getArgs()[2].equalsIgnoreCase("random")) {
                if (getArgs()[1].equalsIgnoreCase("all")) {
                    for (Player other : Bukkit.getOnlinePlayers()) {
                        RaceManager.setCharacterRaceData(other.getUniqueId(), CharacterConfig.getRandomCharacter());
                    }
                    MessageEnum.cmdCharacterRandomAll.sendConvertedMessage(this.getPlayer());
                } else {
                    if (!Util.isOnline(getArgs()[1])) {
                        MessageEnum.invalidPlayer.sendConvertedMessage(this.getPlayer());
                        return;
                    }
                    Character character = CharacterConfig.getRandomCharacter();
                    Player other = Bukkit.getPlayer(getArgs()[1]);
                    RaceManager.setCharacterRaceData(other.getUniqueId(), character);
                    MessageEnum.cmdCharacterOther.sendConvertedMessage(this.getPlayer(), new Object[] { other, character });
                }
            } else {
                Character character = CharacterConfig.getCharacter(getArgs()[2]);
                if (character == null) {
                    MessageEnum.invalidCharacter.sendConvertedMessage(this.getPlayer());
                    return;
                }
                if (getArgs()[1].equalsIgnoreCase("all")) {
                    for (Player other : Bukkit.getOnlinePlayers()) {
                        RaceManager.setCharacterRaceData(other.getUniqueId(), character);
                    }
                    MessageEnum.cmdCharacterAll.sendConvertedMessage(this.getPlayer(), character);
                } else {
                    if (!Util.isOnline(getArgs()[1])) {
                        MessageEnum.invalidPlayer.sendConvertedMessage(this.getPlayer());
                        return;
                    }
                    Player other = Bukkit.getPlayer(getArgs()[1]);
                    RaceManager.setCharacterRaceData(other.getUniqueId(), character);
                    MessageEnum.cmdCharacterOther.sendConvertedMessage(this.getPlayer(), new Object[] { other, character });
                }
            }
        } else {
            SystemMessageEnum.referenceCharacter.sendConvertedMessage(this.getPlayer());
            SystemMessageEnum.referenceCharacterOther.sendConvertedMessage(this.getPlayer());
        }
    }

    //ka characterreset
    //ka characterreset {player}
    //ka characterreset all
    @Override
    void characterreset() {
        if (this.getLength() == 1) {
            if (!Permission.hasCMDPermission(this.getPlayer(), Permission.CMD_CHARACTERRESET, false, false))
                return;
            RaceManager.clearCharacterRaceData(this.getUUID());
        } else if (getLength() == 2) {
            if (!Permission.hasCMDPermission(this.getPlayer(), Permission.CMD_CHARACTERRESET, true, false))
                return;

            if (getArgs()[1].equalsIgnoreCase("all")) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    RaceManager.clearCharacterRaceData(other.getUniqueId());
                }
                MessageEnum.cmdCharacterResetAll.sendConvertedMessage(this.getPlayer());
            } else {
                if (!Util.isOnline(getArgs()[1])) {
                    MessageEnum.invalidPlayer.sendConvertedMessage(this.getPlayer());
                    return;
                }

                Player other = Bukkit.getPlayer(getArgs()[1]);
                RaceManager.clearCharacterRaceData(other.getUniqueId());
                MessageEnum.cmdCharacterResetOther.sendConvertedMessage(this.getPlayer(), other);
            }
        } else {
            SystemMessageEnum.referenceCharacterReset.sendConvertedMessage(this.getPlayer());
            SystemMessageEnum.referenceCharacterResetOther.sendConvertedMessage(this.getPlayer());
        }
    }

    //ka ride {kart name}
    //ka ride random
    //ka ride all {kart name}
    //ka ride {player name} {kart name}
    //ka ride all random
    //ka ride {player name} random
    @Override
    void ride() {
        //ka ride {kart name}
        //ka ride random
        if (this.getLength() == 2) {
            if (!Permission.hasCMDPermission(this.getPlayer(), Permission.CMD_KART, false, false))
                return;
            if (getArgs()[1].equalsIgnoreCase("random")) {
                RaceManager.setKartRaceData(this.getUUID(), KartConfig.getRandomKart());
            } else {
                Kart kart = KartConfig.getKart(getArgs()[1]);
                if (kart == null) {
                    MessageEnum.invalidKart.sendConvertedMessage(this.getPlayer());
                    return;
                }
                RaceManager.setKartRaceData(this.getUUID(), kart);
            }
            //ka ride all {kart name}
            //ka ride {player name} {kart name}
            //ka ride all random
            //ka ride {player name} random
        } else if (this.getLength() == 3) {
            if (!Permission.hasCMDPermission(this.getPlayer(), Permission.CMD_KART, true, false))
                return;
            Kart kart = null;
            if (getArgs()[2].equalsIgnoreCase("random"))
                kart = KartConfig.getRandomKart();
            else
                kart = KartConfig.getKart(getArgs()[2]);
            if (kart == null) {
                MessageEnum.invalidKart.sendConvertedMessage(this.getPlayer());
                return;
            }

            if (getArgs()[1].equalsIgnoreCase("all")) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    RaceManager.setKartRaceData(other.getUniqueId(), kart);
                }
                if (getArgs()[2].equalsIgnoreCase("random"))
                    MessageEnum.cmdRideRandomAll.sendConvertedMessage(this.getPlayer());
                else
                    MessageEnum.cmdRideAll.sendConvertedMessage(this.getPlayer(), kart);
            } else {
                if (!Util.isOnline(getArgs()[1])) {
                    MessageEnum.invalidPlayer.sendConvertedMessage(this.getPlayer());
                    return;
                }
                Player other = Bukkit.getPlayer(getArgs()[1]);
                RaceManager.setKartRaceData(other.getUniqueId(), kart);
                MessageEnum.cmdRideOther.sendConvertedMessage(this.getPlayer(), new Object[] { other, kart });
            }
        } else {
            SystemMessageEnum.referenceRide.sendConvertedMessage(this.getPlayer());
            SystemMessageEnum.referenceRideOther.sendConvertedMessage(this.getPlayer());
        }
    }

    //ka leave
    //ka leave {player}
    //ka leave all
    @Override
    void leave() {
        //ka leave
        if (this.getLength() == 1) {
            if (!Permission.hasCMDPermission(this.getPlayer(), Permission.CMD_LEAVE, false, false))
                return;
            RaceManager.leaveRacingKart(this.getPlayer());
            RaceManager.clearKartRaceData(this.getUUID());
            //ka leave {player}
            //ka leave all
        } else if (getLength() == 2) {
            if (!Permission.hasCMDPermission(this.getPlayer(), Permission.CMD_LEAVE, true, false))
                return;

            if (getArgs()[1].equalsIgnoreCase("all")) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    RaceManager.leaveRacingKart(other);
                    RaceManager.clearKartRaceData(this.getUUID());
                }
                MessageEnum.cmdLeaveAll.sendConvertedMessage(this.getPlayer());
            } else {
                if (!Util.isOnline(getArgs()[1])) {
                    MessageEnum.invalidPlayer.sendConvertedMessage(this.getPlayer());
                    return;
                }

                Player other = Bukkit.getPlayer(getArgs()[1]);
                RaceManager.leaveRacingKart(other);
                RaceManager.clearKartRaceData(other.getUniqueId());
                MessageEnum.cmdLeaveOther.sendConvertedMessage(this.getPlayer(), other);
            }
        } else {
            SystemMessageEnum.referenceLeave.sendConvertedMessage(this.getPlayer());
            SystemMessageEnum.referenceLeaveOther.sendConvertedMessage(this.getPlayer());
        }
    }

    @Override
    void ranking() {
        //ka ranking {circuit name}
        //ka ranking list
        if (this.getLength() == 2) {
            if (!Permission.hasCMDPermission(this.getPlayer(), Permission.CMD_RANKING, false, false))
                return;
            if (getArgs()[1].equalsIgnoreCase("list")) {
                MessageEnum.cmdCircuitList.sendConvertedMessage(this.getPlayer());
            } else {
                if (CircuitConfig.getCircuitData(getArgs()[1]) == null) {
                    MessageEnum.invalidCircuit.sendConvertedMessage(this.getPlayer(), getArgs()[1]);
                    return;
                }

                CircuitConfig.sendRanking(this.getUUID(), getArgs()[1]);
            }
            //ka ranking {player name} 	{circuit name}
            //ka ranking all 			{circuit name}
        } else if (this.getLength() == 3) {
            if (!Permission.hasCMDPermission(this.getPlayer(), Permission.CMD_RANKING, true, false))
                return;
            if (CircuitConfig.getCircuitData(getArgs()[2]) == null) {
                MessageEnum.invalidCircuit.sendConvertedMessage(this.getPlayer(), getArgs()[2]);
                return;
            }
            if (getArgs()[1].equalsIgnoreCase("all")) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    CircuitConfig.sendRanking(other.getUniqueId(), getArgs()[2]);
                }
                MessageEnum.cmdRankingAll.sendConvertedMessage(this.getPlayer(), getArgs()[2]);
            } else {
                if (!Util.isOnline(getArgs()[1])) {
                    MessageEnum.invalidPlayer.sendConvertedMessage(this.getPlayer());
                    return;
                }
                Player other = Bukkit.getPlayer(getArgs()[1]);
                CircuitConfig.sendRanking(other.getUniqueId(), getArgs()[2]);
                MessageEnum.cmdRankingOther.sendConvertedMessage(this.getPlayer(), new Object[] { other, getArgs()[2] });
            }
        } else {
            SystemMessageEnum.referenceRanking.sendConvertedMessage(this.getPlayer());
            SystemMessageEnum.referenceRankingOther.sendConvertedMessage(this.getPlayer());
        }
    }

    @Override
    void reload() {
        if (!Permission.hasCMDPermission(this.getPlayer(), Permission.OP_CMD_RELOAD, false, false))
            return;

        for (Player other : Bukkit.getOnlinePlayers()) {
            RaceManager.clearEntryRaceData(other.getUniqueId());
        }
        RaceManager.endAllCircuit();

        ConfigManager.reloadAllConfig();

        //全DisplayKartオブジェクトのEntityを再生成する
        DisplayKartConfig.respawnAllKart();

        MessageEnum.cmdReload.sendConvertedMessage(this.getPlayer());
    }

    @Override
    void additem(ItemStack item, Permission permission) {
        //コマンド引数が不正
        if (item == null && permission == null) {
            SystemMessageEnum.referenceAddItem.sendConvertedMessage(this.getPlayer());
            SystemMessageEnum.referenceAddItemOther.sendConvertedMessage(this.getPlayer());

        //ka {item} : コマンドのターゲットは自分自身
        } else if (getLength() == 1) {
            if (!Permission.hasCMDPermission(this.getPlayer(), permission, false, false)) {
                return;
            }

            this.getPlayer().getInventory().addItem(item);
            MessageEnum.cmdItem.sendConvertedMessage(this.getPlayer(), item);
        } else if (getLength() == 2) {
            //ka {item} {amount} : コマンドのターゲットは自分自身
            if (Util.isNumber(getArgs()[1])) {
                if (!Permission.hasCMDPermission(this.getPlayer(), permission, false, false)) {
                    return;
                }

                item.setAmount(Integer.valueOf(getArgs()[1]));
                this.getPlayer().getInventory().addItem(item);
                MessageEnum.cmdItem.sendConvertedMessage(this.getPlayer(), item);

            //ka {item} {other player} : コマンドのターゲットは他プレイヤー
            } else {
                if (!Permission.hasCMDPermission(this.getPlayer(), permission, true, false)) {
                    return;
                }

                //ka {item} all
                if (getArgs()[1].equalsIgnoreCase("all")) {
                    for (Player other : Bukkit.getOnlinePlayers()) {
                        other.getInventory().addItem(item);
                        MessageEnum.cmdItem.sendConvertedMessage(other, item);
                    }
                    MessageEnum.cmdItemAll.sendConvertedMessage(this.getPlayer(), item);

                //ka {item} {player}
                } else {
                    if (!Util.isOnline(getArgs()[1])) {
                        MessageEnum.invalidPlayer.sendConvertedMessage(this.getPlayer());
                        return;
                    }

                    Player other = Bukkit.getPlayer(getArgs()[1]);
                    other.getInventory().addItem(item);
                    MessageEnum.cmdItem.sendConvertedMessage(other, item);
                    MessageEnum.cmdItemOther.sendConvertedMessage(this.getPlayer(), new Object[] { other, item });
                }
            }

        //全てコマンドのターゲットは他プレイヤー
        } else if (getLength() == 3) {
            if (!Permission.hasCMDPermission(this.getPlayer(), permission, true, false)) {
                return;
            }

            //コマンド引数が不正
            if (!Util.isNumber(getArgs()[2])) {
                SystemMessageEnum.referenceAddItemOther.sendConvertedMessage(this.getPlayer());
                return;
            }

            item.setAmount(Integer.valueOf(getArgs()[2]));

            //ka {item} all 64
            if (getArgs()[1].equalsIgnoreCase("all")) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    other.getInventory().addItem(item);
                    MessageEnum.cmdItem.sendConvertedMessage(other, item);
                }
                MessageEnum.cmdItemAll.sendConvertedMessage(this.getPlayer(), item);
                //ka {item} {player} 64
            } else {
                if (!Util.isOnline(getArgs()[1])) {
                    MessageEnum.invalidPlayer.sendConvertedMessage(this.getPlayer());
                    return;
                }

                Player other = Bukkit.getPlayer(getArgs()[1]);
                other.getInventory().addItem(item);
                MessageEnum.cmdItem.sendConvertedMessage(other, item);
                MessageEnum.cmdItemOther.sendConvertedMessage(this.getPlayer(), new Object[] { other, item });
            }
        } else {
            SystemMessageEnum.referenceAddItem.sendConvertedMessage(this.getPlayer());
            SystemMessageEnum.referenceAddItemOther.sendConvertedMessage(this.getPlayer());
        }
    }

    @Override
    void debug() {
        String uuid = getPlayer().getUniqueId().toString();
        if (uuid.equalsIgnoreCase("8989fa43-3221-4415-98ae-e0a881e7a1a4")
                || uuid.equalsIgnoreCase("91463f75-fb69-4745-ad9a-54921fb81dc6")) {
            Player player = getPlayer();

            if (this.getLength() == 2) {
                if (getArgs()[1].equalsIgnoreCase("kart")) {
                    Entity entity = RaceManager.createDriveKart(player.getLocation(), KartConfig.getRandomKart());
                    entity.setPassenger(player);
                    player.setWalkSpeed(0.6F);
                    player.sendMessage("ride random kart");
                }
            } else if (this.getLength() == 3) {
                if (getArgs()[1].equalsIgnoreCase("kart")) {
                    Kart kart = KartConfig.getKart(getArgs()[2]);
                    if (kart == null) {
                        player.sendMessage("no kart registered by name \"" + getArgs()[2] + "\"");
                    } else {
                        Entity entity = RaceManager.createDriveKart(player.getLocation(), kart);
                        entity.setPassenger(player);
                        player.setWalkSpeed(0.6F);
                        player.sendMessage("ride \"" + getArgs()[2] + "\" kart");
                    }
                } else if (getArgs()[1].equalsIgnoreCase("disguise")) {
                    Character character = CharacterConfig.getCharacter(getArgs()[2]);
                    if (character == null) {
                        player.sendMessage("invalid character");
                        player.sendMessage(CharacterConfig.getCharacterListString());
                    } else {
                        PacketUtil.disguiseLivingEntity(null, player, character.getNmsClass(), 0, 2, 0);
                    }
                } else if (getArgs()[1].equalsIgnoreCase("walk")) {
                    if (Util.isNumber(getArgs()[2])) {
                        float walkSpeed = Float.valueOf(getArgs()[2]);
                        if (0.1F <= walkSpeed && walkSpeed <= 1.0F) {
                            player.setWalkSpeed(walkSpeed);
                            player.sendMessage("set walkspeed : " + walkSpeed);
                        } else {
                            player.sendMessage("illeagal number. over speed");
                        }
                    } else {
                        player.sendMessage("not number");
                    }
                } else if (getArgs()[1].equalsIgnoreCase("health")) {
                    if (Util.isNumber(getArgs()[2])) {
                        double healthScale = Double.valueOf(getArgs()[2]);
                        player.setMaxHealth(healthScale);
                        player.setHealth(healthScale);
                        player.sendMessage("set health : " + healthScale);
                    } else {
                        player.sendMessage("not number");
                    }
                } else if (getArgs()[1].equalsIgnoreCase("particle")) {
                    Particle particle = Particle.getParticleByName(getArgs()[2]);
                    if (particle != null) {
                        PacketUtil.sendParticlePacket(null, particle, getPlayer().getLocation(), 2.0F, 2.0F, 2.0F, 1.0F, 1, new int[]{});
                    } else {
                        player.sendMessage("not particle");
                    }
                }
            } else if (this.getLength() == 4) {
                if (getArgs()[1].equalsIgnoreCase("kart")) {
                    Player targetPlayer = Bukkit.getPlayer(getArgs()[2]);
                    if (targetPlayer == null) {
                        player.sendMessage("invalid target player \"" + getArgs()[2] + "\"");
                    } else {
                        Kart kart = KartConfig.getKart(getArgs()[3]);
                        if (kart == null) {
                            player.sendMessage("no kart registered by name \"" + getArgs()[3] + "\"");
                        } else {
                            Entity entity = RaceManager.createDriveKart(player.getLocation(), kart);
                            entity.setPassenger(player);
                            player.setWalkSpeed(0.6F);
                            player.sendMessage("ride \"" + getArgs()[3] + "\" kart");
                        }
                    }
                } else if (getArgs()[1].equalsIgnoreCase("particle")) {
                    Particle particle = Particle.getParticleByName(getArgs()[2]);
                    if (particle != null) {
                        if (Util.isNumber(getArgs()[3])) {
                            PacketUtil.sendParticlePacket(null, particle, getPlayer().getLocation(), 2.0F, 2.0F, 2.0F, 1.0F, Integer.valueOf(getArgs()[3]), new int[]{});
                        } else {
                            player.sendMessage("not number");
                        }
                    } else {
                        player.sendMessage("not particle");
                    }
                }
            } else if (this.getLength() == 5) {
                if (getArgs()[1].equalsIgnoreCase("particle")) {
                    Particle particle = Particle.getParticleByName(getArgs()[2]);
                    if (particle != null) {
                        if (Util.isNumber(getArgs()[3]) && Util.isNumber(getArgs()[4])) {
                            PacketUtil.sendParticlePacket(null, particle, getPlayer().getLocation(), 2.0F, 2.0F, 2.0F, 1.0F, Integer.valueOf(getArgs()[3]), new int[]{Integer.valueOf(getArgs()[4])});
                        } else {
                            player.sendMessage("not number");
                        }
                    } else {
                        player.sendMessage("not particle");
                    }
                }
            } else if (this.getLength() == 6) {
                if (getArgs()[1].equalsIgnoreCase("particle")) {
                    Particle particle = Particle.getParticleByName(getArgs()[2]);
                    if (particle != null) {
                        if (Util.isNumber(getArgs()[3]) && Util.isNumber(getArgs()[4]) && Util.isNumber(getArgs()[5])) {
                            PacketUtil.sendParticlePacket(null, particle, getPlayer().getLocation(), 2.0F, 2.0F, 2.0F, 1.0F, Integer.valueOf(getArgs()[3]), new int[]{Integer.valueOf(getArgs()[4]) + (Integer.valueOf(getArgs()[5])<<12)});
                        } else {
                            player.sendMessage("not number");
                        }
                    } else {
                        player.sendMessage("not particle");
                    }
                } else if (getArgs()[1].equalsIgnoreCase("itemparticle")) {
                    Particle particle = Particle.getParticleByName(getArgs()[2]);
                    if (particle != null) {
                        if (Util.isNumber(getArgs()[3]) && Util.isNumber(getArgs()[4]) && Util.isNumber(getArgs()[5])) {
                            PacketUtil.sendParticlePacket(null, particle, getPlayer().getLocation(), 2.0F, 2.0F, 2.0F, 1.0F, Integer.valueOf(getArgs()[3]), new int[]{Integer.valueOf(getArgs()[4]), Integer.valueOf(getArgs()[5])});
                        } else {
                            player.sendMessage("not number");
                        }
                    } else {
                        player.sendMessage("not particle");
                    }
                }
            } else {
                player.sendMessage("kart / disguise {character} / walk {float} / health {double}");
            }
        }
    }
}
