package com.github.erozabesu.yplkart.cmd;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.erozabesu.yplkart.ConfigManager;
import com.github.erozabesu.yplkart.Permission;
import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.data.CharacterConfig;
import com.github.erozabesu.yplkart.data.CircuitConfig;
import com.github.erozabesu.yplkart.data.DisplayKartConfig;
import com.github.erozabesu.yplkart.data.KartConfig;
import com.github.erozabesu.yplkart.data.MessageEnum;
import com.github.erozabesu.yplkart.data.SystemMessageEnum;
import com.github.erozabesu.yplkart.object.Character;
import com.github.erozabesu.yplkart.object.Circuit;
import com.github.erozabesu.yplkart.object.CircuitData;
import com.github.erozabesu.yplkart.object.Kart;
import com.github.erozabesu.yplkart.utils.Util;

public class CMDAbstractConsole extends CMDAbstract {
    String[] args;
    int length;

    public CMDAbstractConsole(String[] args) {
        this.args = args;
        this.length = args.length;
    }

    @Override
    void ka() {
        SystemMessageEnum.reference.sendConvertedMessage(null);
    }

    @Override
    void circuit() {
        if (this.length == 2) {
            if (args[1].equalsIgnoreCase("list")) {
                MessageEnum.cmdCircuitList.sendConvertedMessage(null);
                return;
            }
        } else if (this.length == 3) {
            if (args[1].equalsIgnoreCase("info")) {
                CircuitData circuitData = CircuitConfig.getCircuitData(args[2]);
                if (circuitData != null) {
                    circuitData.sendInformation(null);
                }
                return;
            } else if (args[1].equalsIgnoreCase("delete")) {
                CircuitConfig.deleteCircuit(null, args[2]);
                return;
            }
        } else if (this.length == 4) {
            if (args[1].equalsIgnoreCase("rename")) {
                CircuitConfig.renameCircuit(null, args[2], args[3]);
                return;
            } else if (args[1].equalsIgnoreCase("setminplayer")) {
                if (!Util.isNumber(args[3])) {
                    MessageEnum.invalidNumber.sendConvertedMessage(null);
                    return;
                }
                CircuitConfig.setMinPlayer(null, args[2], Integer.valueOf(args[3]));
                return;
            } else if (args[1].equalsIgnoreCase("setmaxplayer")) {
                if (!Util.isNumber(args[3])) {
                    MessageEnum.invalidNumber.sendConvertedMessage(null);
                    return;
                }
                CircuitConfig.setMaxPlayer(null, args[2], Integer.valueOf(args[3]));
                return;
            } else if (args[1].equalsIgnoreCase("setmatchingtime")) {
                if (!Util.isNumber(args[3])) {
                    MessageEnum.invalidNumber.sendConvertedMessage(null);
                    return;
                }
                CircuitConfig.setMatchingTime(null, args[2], Integer.valueOf(args[3]));
                return;
            } else if (args[1].equalsIgnoreCase("setmenutime")) {
                if (!Util.isNumber(args[3])) {
                    MessageEnum.invalidNumber.sendConvertedMessage(null);
                    return;
                }
                CircuitConfig.setMenuTime(null, args[2], Integer.valueOf(args[3]));
                return;
            } else if (args[1].equalsIgnoreCase("setlimittime")) {
                if (!Util.isNumber(args[3])) {
                    MessageEnum.invalidNumber.sendConvertedMessage(null);
                    return;
                }
                CircuitConfig.setLimitTime(null, args[2], Integer.valueOf(args[3]));
                return;
            } else if (args[1].equalsIgnoreCase("setlap")) {
                if (!Util.isNumber(args[3])) {
                    MessageEnum.invalidNumber.sendConvertedMessage(null);
                    return;
                }
                CircuitConfig.setNumberOfLaps(null, args[2], Integer.valueOf(args[3]));
                return;
            } else if (args[1].equalsIgnoreCase("broadcastgoal")) {
                if (!Util.isBoolean(args[3])) {
                    MessageEnum.invalidBoolean.sendConvertedMessage(null);
                    return;
                }
                CircuitConfig.setBroadcastGoalMessage(null, args[2], Boolean.valueOf(args[3]));
                return;
            }
        } else if (this.length == 9) {
            if (Bukkit.getWorld(args[3]) == null) {
                MessageEnum.invalidWorld.sendConvertedMessage(null);
                return;
            }
            if (!Util.isNumber(args[4]) || !Util.isNumber(args[5]) || !Util.isNumber(args[6])
                    || !Util.isNumber(args[7]) || !Util.isNumber(args[8])) {
                MessageEnum.invalidNumber.sendConvertedMessage(null);
                return;
            }
            //0:circuit 1:create 2:circuitname 3:worldname 4:x 5:y 6:z 7:pitch 8:yaw
            if (args[1].equalsIgnoreCase("create")) {
                World world = Bukkit.getWorld(args[3]);
                if (world == null) {
                    MessageEnum.invalidWorld.sendConvertedMessage(null);
                }
                Location location = new Location(world, Double.valueOf(args[4]), Double.valueOf(args[5])
                        ,Double.valueOf(args[6]), Float.valueOf(args[7]), Float.valueOf(args[8]));
                CircuitConfig.createCircuit(null, location, args[2]);

                return;
            //0:circuit 1:setposition 2:circuitname 3:worldname 4:x 5:y 6:z 7:pitch 8:yaw
            } else if (args[1].equalsIgnoreCase("setposition")) {
                World world = Bukkit.getWorld(args[3]);
                if (world == null) {
                    MessageEnum.invalidWorld.sendConvertedMessage(null);
                }
                Location location = new Location(world, Double.valueOf(args[4]), Double.valueOf(args[5])
                        ,Double.valueOf(args[6]), Float.valueOf(args[7]), Float.valueOf(args[8]));
                CircuitConfig.setStartPosition(null, args[2], location);

                return;
            }
        }
        SystemMessageEnum.referenceCircuitOutgame.sendConvertedMessage(null);
    }

    //ka display {kart name} {worldname} {x} {y} {z} {yaw} {pitch}
    //ka display random {worldname} {x} {y} {z}  {yaw} {pitch}
    @Override
    void display() {
        if (length == 8) {
            if (Bukkit.getWorld(args[2]) == null) {
                MessageEnum.invalidWorld.sendConvertedMessage(null);
                return;
            }
            if (!Util.isNumber(args[3]) || !Util.isNumber(args[4]) || !Util.isNumber(args[5])
                    || !Util.isNumber(args[6]) || !Util.isNumber(args[7])) {
                MessageEnum.invalidNumber.sendConvertedMessage(null);
                return;
            }
            Kart kart = null;
            if (args[1].equalsIgnoreCase("random"))
                kart = KartConfig.getRandomKart();
            else
                kart = KartConfig.getKart(args[1]);
            if (kart == null) {
                MessageEnum.invalidKart.sendConvertedMessage(null);
                return;
            }

            RaceManager.createDisplayKart(
                    new Location(Bukkit.getWorld(args[2]), Double.valueOf(args[3]), Double.valueOf(args[4]), Double
                            .valueOf(args[5]), Float.valueOf(args[6]), Float.valueOf(args[7])), kart, null);
            MessageEnum.cmdDisplayCreate.sendConvertedMessage(null, new Object[] { kart });
        } else {
            SystemMessageEnum.referenceDisplayOutgame.sendConvertedMessage(null);
        }
    }

    //ka menu {player}
    //ka menu all
    @Override
    void menu() {
        if (length == 2) {
            if (args[1].equalsIgnoreCase("all")) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    RaceManager.showSelectMenu(other, true);
                }
                MessageEnum.cmdMenuAll.sendConvertedMessage(null);
            } else {
                if (!Util.isOnline(args[1])) {
                    MessageEnum.invalidPlayer.sendConvertedMessage(null);
                    return;
                }

                Player other = Bukkit.getPlayer(args[1]);
                RaceManager.showSelectMenu(other, true);
                MessageEnum.cmdMenuOther.sendConvertedMessage(null, new Object[] { other });
            }
        } else {
            SystemMessageEnum.referenceMenuOther.sendConvertedMessage(null);
        }
    }

    //ka entry {player name} {circuit name}
    //ka entry all {circuit name}
    @Override
    void entry() {
        if (this.length == 3) {
            if (CircuitConfig.getCircuitData(args[2]) == null) {
                Circuit circuit = new Circuit();
                circuit.setCircuitName(args[2]);
                MessageEnum.invalidCircuit.sendConvertedMessage(null, circuit);
                return;
            }
            if (args[1].equalsIgnoreCase("all")) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    RaceManager.setEntryRaceData(other.getUniqueId(), args[2]);
                }
                MessageEnum.cmdEntryAll.sendConvertedMessage(null, args[2]);
            } else {
                Player other = Bukkit.getPlayer(args[1]);
                if (other == null) {
                    MessageEnum.invalidPlayer.sendConvertedMessage(null);
                    return;
                }

                Circuit circuit = new Circuit();
                circuit.setCircuitName(args[2]);
                MessageEnum.cmdEntryOther.sendConvertedMessage(null, other, circuit);
                RaceManager.setEntryRaceData(other.getUniqueId(), args[2]);
            }
        } else {
            SystemMessageEnum.referenceEntryOther.sendConvertedMessage(null);
        }
    }

    //ka exit {player}
    //ka exit all
    @Override
    void exit() {
        if (length == 2) {
            if (args[1].equalsIgnoreCase("all")) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    RaceManager.clearEntryRaceData(other.getUniqueId());
                }
                MessageEnum.cmdExitAll.sendConvertedMessage(null);
            } else {
                if (!Util.isOnline(args[1])) {
                    MessageEnum.invalidPlayer.sendConvertedMessage(null);
                    return;
                }

                Player other = Bukkit.getPlayer(args[1]);
                RaceManager.clearEntryRaceData(other.getUniqueId());
                MessageEnum.cmdExitOther.sendConvertedMessage(null, other);
            }
        } else {
            SystemMessageEnum.referenceExitOther.sendConvertedMessage(null);
        }
    }

    //ka character {player} {character name}
    //ka character all {character name}
    //ka character {player} random
    //ka character all random
    @Override
    void character() {
        if (this.length == 3) {
            if (args[2].equalsIgnoreCase("random")) {
                if (args[1].equalsIgnoreCase("all")) {
                    for (Player other : Bukkit.getOnlinePlayers()) {
                        RaceManager.setCharacterRaceData(other.getUniqueId(), CharacterConfig.getRandomCharacter());
                    }
                    MessageEnum.cmdCharacterRandomAll.sendConvertedMessage(null);
                } else {
                    if (!Util.isOnline(args[1])) {
                        MessageEnum.invalidPlayer.sendConvertedMessage(null);
                        return;
                    }
                    Character character = CharacterConfig.getRandomCharacter();
                    Player other = Bukkit.getPlayer(args[1]);
                    RaceManager.setCharacterRaceData(other.getUniqueId(), character);
                    MessageEnum.cmdCharacterOther.sendConvertedMessage(null, new Object[] { other, character });
                }
            } else {
                Character character = CharacterConfig.getCharacter(args[2]);
                if (character == null) {
                    MessageEnum.invalidCharacter.sendConvertedMessage(null);
                    return;
                }
                if (args[1].equalsIgnoreCase("all")) {
                    for (Player other : Bukkit.getOnlinePlayers()) {
                        RaceManager.setCharacterRaceData(other.getUniqueId(), character);
                    }
                    MessageEnum.cmdCharacterAll.sendConvertedMessage(null, character);
                } else {
                    if (!Util.isOnline(args[1])) {
                        MessageEnum.invalidPlayer.sendConvertedMessage(null);
                        return;
                    }
                    Player other = Bukkit.getPlayer(args[1]);
                    RaceManager.setCharacterRaceData(other.getUniqueId(), character);
                    MessageEnum.cmdCharacterOther.sendConvertedMessage(null, new Object[] { other, character });
                }
            }
        } else {
            SystemMessageEnum.referenceCharacterOther.sendConvertedMessage(null);
        }
    }

    //ka characterreset {player}
    //ka characterreset all
    @Override
    void characterreset() {
        if (length == 2) {
            if (args[1].equalsIgnoreCase("all")) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    RaceManager.clearCharacterRaceData(other.getUniqueId());
                }
                MessageEnum.cmdCharacterResetAll.sendConvertedMessage(null);
            } else {
                if (!Util.isOnline(args[1])) {
                    MessageEnum.invalidPlayer.sendConvertedMessage(null);
                    return;
                }

                Player other = Bukkit.getPlayer(args[1]);
                RaceManager.clearCharacterRaceData(other.getUniqueId());
                MessageEnum.cmdCharacterResetOther.sendConvertedMessage(null, other);
            }
        } else {
            SystemMessageEnum.referenceCharacterResetOther.sendConvertedMessage(null);
        }
    }

    //ka ride all {kart name}
    //ka ride {player name} {kart name}
    //ka ride all random
    //ka ride {player name} random
    @Override
    void ride() {
        if (this.length == 3) {
            Kart kart = null;
            if (args[2].equalsIgnoreCase("random"))
                kart = KartConfig.getRandomKart();
            else
                kart = KartConfig.getKart(args[2]);
            if (kart == null) {
                MessageEnum.invalidKart.sendConvertedMessage(null);
                return;
            }

            if (args[1].equalsIgnoreCase("all")) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    RaceManager.setKartRaceData(other.getUniqueId(), kart);
                }
                if (args[2].equalsIgnoreCase("random"))
                    MessageEnum.cmdRideRandomAll.sendConvertedMessage(null);
                else
                    MessageEnum.cmdRideAll.sendConvertedMessage(null, kart);
            } else {
                if (!Util.isOnline(args[1])) {
                    MessageEnum.invalidPlayer.sendConvertedMessage(null);
                    return;
                }
                Player other = Bukkit.getPlayer(args[1]);
                RaceManager.setKartRaceData(other.getUniqueId(), kart);
                MessageEnum.cmdRideOther.sendConvertedMessage(null, new Object[] { other, kart });
            }
        } else {
            SystemMessageEnum.referenceRideOther.sendConvertedMessage(null);
        }
    }

    //ka leave {player}
    //ka leave all
    @Override
    void leave() {
        if (length == 2) {
            if (args[1].equalsIgnoreCase("all")) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    RaceManager.leaveRacingKart(other);
                    RaceManager.clearKartRaceData(other.getUniqueId());
                }
                MessageEnum.cmdLeaveAll.sendConvertedMessage(null);
            } else {
                if (!Util.isOnline(args[1])) {
                    MessageEnum.invalidPlayer.sendConvertedMessage(null);
                    return;
                }

                Player other = Bukkit.getPlayer(args[1]);
                RaceManager.leaveRacingKart(other);
                RaceManager.clearKartRaceData(other.getUniqueId());
                MessageEnum.cmdLeaveOther.sendConvertedMessage(null, other);
            }
        } else {
            SystemMessageEnum.referenceLeaveOther.sendConvertedMessage(null);
        }
    }

    @Override
    void ranking() {
        //ka ranking {circuit name}
        //ka ranking list
        if (this.length == 2) {
            if (args[1].equalsIgnoreCase("list")) {
                MessageEnum.cmdCircuitList.sendConvertedMessage(null);
            } else {
                if (CircuitConfig.getCircuitData(args[1]) == null) {
                    Circuit circuit = new Circuit();
                    circuit.setCircuitName(args[1]);
                    MessageEnum.invalidCircuit.sendConvertedMessage(null, circuit);
                    return;
                }

                CircuitConfig.sendRanking(null, args[1]);
            }
            //ka ranking {player name} 	{circuit name}
            //ka ranking all 			{circuit name}
        } else if (this.length == 3) {
            if (CircuitConfig.getCircuitData(args[2]) == null) {
                Circuit circuit = new Circuit();
                circuit.setCircuitName(args[2]);
                MessageEnum.invalidCircuit.sendConvertedMessage(null, circuit);
                return;
            }
            if (args[1].equalsIgnoreCase("all")) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    CircuitConfig.sendRanking(other.getUniqueId(), args[2]);
                }
                MessageEnum.cmdRankingAll.sendConvertedMessage(null, args[2]);
            } else {
                if (!Util.isOnline(args[1])) {
                    MessageEnum.invalidPlayer.sendConvertedMessage(null);
                    return;
                }
                Player other = Bukkit.getPlayer(args[1]);
                CircuitConfig.sendRanking(other.getUniqueId(), args[2]);
                MessageEnum.cmdRankingOther.sendConvertedMessage(null, new Object[] { other, args[2] });
            }
        } else {
            SystemMessageEnum.referenceRankingOther.sendConvertedMessage(null);
        }
    }

    @Override
    void reload() {
        for (Player other : Bukkit.getOnlinePlayers()) {
            RaceManager.clearEntryRaceData(other.getUniqueId());
        }
        RaceManager.endAllCircuit();

        ConfigManager.reloadAllConfig();

        //全DisplayKartオブジェクトのEntityを再生成する
        DisplayKartConfig.respawnAllKart();

        MessageEnum.cmdReload.sendConvertedMessage(null);
    }

    @Override
    void additem(ItemStack item, Permission permission) {
        if (item == null && permission == null) {
            SystemMessageEnum.referenceAddItemOther.sendConvertedMessage(null);
        } else if (length == 2) {
            //ka {item} all
            if (args[1].equalsIgnoreCase("all")) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    other.getInventory().addItem(item);
                    MessageEnum.cmdItem.sendConvertedMessage(other, item);
                }
                MessageEnum.cmdItemAll.sendConvertedMessage(null, item);
                //ka {item} {player}
            } else {
                if (!Util.isOnline(args[1])) {
                    MessageEnum.invalidPlayer.sendConvertedMessage(null);
                    return;
                }

                Player other = Bukkit.getPlayer(args[1]);
                other.getInventory().addItem(item);
                MessageEnum.cmdItem.sendConvertedMessage(other, item);
                MessageEnum.cmdItemOther.sendConvertedMessage(null, new Object[] { other, item });
            }
        } else if (length == 3) {
            if (!Util.isNumber(args[2])) {
                SystemMessageEnum.referenceAddItemOther.sendConvertedMessage(null);
                return;
            }
            item.setAmount(Integer.valueOf(args[2]));

            //ka {item} all 64
            if (args[1].equalsIgnoreCase("all")) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    other.getInventory().addItem(item);
                    MessageEnum.cmdItem.sendConvertedMessage(other, item);
                }
                MessageEnum.cmdItemAll.sendConvertedMessage(null, item);
                //ka {item} {player} 64
            } else {
                if (!Util.isOnline(args[1])) {
                    MessageEnum.invalidPlayer.sendConvertedMessage(null);
                    return;
                }

                Player other = Bukkit.getPlayer(args[1]);
                other.getInventory().addItem(item);
                MessageEnum.cmdItem.sendConvertedMessage(other, item);
                MessageEnum.cmdItemOther.sendConvertedMessage(null, new Object[] { other, item });
            }
        } else {
            SystemMessageEnum.referenceAddItemOther.sendConvertedMessage(null);
        }
    }

    @Override
    void debug() {
        // Do nothing
    }
}
