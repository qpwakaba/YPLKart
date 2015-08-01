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
import com.github.erozabesu.yplkart.object.CircuitData;
import com.github.erozabesu.yplkart.object.Kart;
import com.github.erozabesu.yplkart.utils.Util;

public class CMDAbstractBlock extends CMDAbstract {
    String[] args;
    int length;

    public CMDAbstractBlock(String[] args) {
        this.args = args;
        this.length = args.length;
    }

    @Override
    void ka() {
        if (Bukkit.getPlayer(args[0]) != null) {
            SystemMessageEnum.reference.sendConvertedMessage(Bukkit.getPlayer(args[0]));
        }
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
            if (Bukkit.getWorld(args[3]) == null)
                return;
            if (!Util.isNumber(args[4]) || !Util.isNumber(args[5]) || !Util.isNumber(args[6])
                    || !Util.isNumber(args[7]) || !Util.isNumber(args[8]))
                return;
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
    }

    //ka display {kart name} {worldname} {x} {y} {z} {yaw} {pitch}
    //ka display random {worldname} {x} {y} {z}  {yaw} {pitch}
    @Override
    void display() {
        if (length == 8) {
            if (Bukkit.getWorld(args[2]) == null)
                return;
            if (!Util.isNumber(args[3]) || !Util.isNumber(args[4]) || !Util.isNumber(args[5])
                    || !Util.isNumber(args[6]) || !Util.isNumber(args[7]))
                return;
            Kart kart = null;
            if (args[1].equalsIgnoreCase("random"))
                kart = KartConfig.getRandomKart();
            else
                kart = KartConfig.getKart(args[1]);
            if (kart == null)
                return;

            RaceManager.createDisplayKart(
                    new Location(Bukkit.getWorld(args[2]), Double.valueOf(args[3]), Double.valueOf(args[4]), Double
                            .valueOf(args[5]), Float.valueOf(args[6]), Float.valueOf(args[7])), kart, null);
        } else {
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
            } else {
                if (!Util.isOnline(args[1]))
                    return;

                Player other = Bukkit.getPlayer(args[1]);
                RaceManager.showSelectMenu(other, true);
            }
        } else {
        }
    }

    //ka entry {player name} {circuit name}
    //ka entry all {circuit name}
    @Override
    void entry() {
        if (this.length == 3) {
            if (CircuitConfig.getCircuitData(args[2]) == null) {
                return;
            }
            if (args[1].equalsIgnoreCase("all")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    RaceManager.setEntryRaceData(p.getUniqueId(), args[2]);
                }
            } else {
                if (!Util.isOnline(args[1])) {
                    return;
                }
                RaceManager.setEntryRaceData(Bukkit.getPlayer(args[1]).getUniqueId(), args[2]);
            }
        } else {
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
            } else {
                if (!Util.isOnline(args[1]))
                    return;

                Player other = Bukkit.getPlayer(args[1]);
                RaceManager.clearEntryRaceData(other.getUniqueId());
            }
        } else {
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
                } else {
                    if (!Util.isOnline(args[1]))
                        return;
                    Character character = CharacterConfig.getRandomCharacter();
                    RaceManager.setCharacterRaceData(Bukkit.getPlayer(args[1]).getUniqueId(), character);
                }
            } else {
                Character character = CharacterConfig.getCharacter(args[2]);
                if (character == null)
                    return;
                if (args[1].equalsIgnoreCase("all")) {
                    for (Player other : Bukkit.getOnlinePlayers()) {
                        RaceManager.setCharacterRaceData(other.getUniqueId(), character);
                    }
                } else {
                    if (!Util.isOnline(args[1]))
                        return;
                    RaceManager.setCharacterRaceData(Bukkit.getPlayer(args[1]).getUniqueId(), character);
                }
            }
        } else {
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
            } else {
                if (!Util.isOnline(args[1]))
                    return;

                RaceManager.clearCharacterRaceData(Bukkit.getPlayer(args[1]).getUniqueId());
            }
        } else {
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
            if (kart == null)
                return;

            if (args[1].equalsIgnoreCase("all")) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    RaceManager.setKartRaceData(other.getUniqueId(), kart);
                }
            } else {
                if (!Util.isOnline(args[1]))
                    return;

                Player other = Bukkit.getPlayer(args[1]);
                RaceManager.setKartRaceData(other.getUniqueId(), kart);
            }
        } else {
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
            } else {
                if (!Util.isOnline(args[1]))
                    return;

                Player other = Bukkit.getPlayer(args[1]);
                RaceManager.leaveRacingKart(other);
                RaceManager.clearKartRaceData(other.getUniqueId());
            }
        } else {
        }
    }

    @Override
    void ranking() {
        //ka ranking {player name} 	{circuit name}
        //ka ranking all 			{circuit name}
        if (!Util.isOnline(args[1]))
            return;
        Player other = Bukkit.getPlayer(args[1]);

        if (this.length == 3) {
            if (CircuitConfig.getCircuitData(args[2]) == null) {
                MessageEnum.invalidCircuit.sendConvertedMessage(null, args[2]);
                return;
            }

            CircuitConfig.sendRanking(other.getUniqueId(), args[2]);
        } else {
            SystemMessageEnum.referenceRankingOther.sendConvertedMessage(null);
        }
    }

    @Override
    void reload() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            RaceManager.clearEntryRaceData(p.getUniqueId());
        }
        RaceManager.endAllCircuit();

        ConfigManager.reloadAllConfig();

        //全DisplayKartオブジェクトのEntityを再生成する
        DisplayKartConfig.respawnAllKart();

        MessageEnum.cmdReload.sendConvertedMessage(null);
    }

    @Override
    void additem(ItemStack item, Permission permission) {
        if (length == 2) {
            //ka {item} all
            if (args[1].equalsIgnoreCase("all")) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    other.getInventory().addItem(item);
                    MessageEnum.cmdItem.sendConvertedMessage(other, item);
                }
                //ka {item} @?
            } else {
                Player other = Bukkit.getPlayer(args[1]);
                other.getInventory().addItem(item);
                MessageEnum.cmdItem.sendConvertedMessage(other, item);
            }
        } else if (length == 3) {
            if (!Util.isNumber(args[2]))
                return;
            item.setAmount(Integer.valueOf(args[2]));

            //ka {item} all 64
            if (args[1].equalsIgnoreCase("all")) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    other.getInventory().addItem(item);
                    MessageEnum.cmdItem.sendConvertedMessage(other, item);
                }
                //ka {item} @? 64
            } else {
                Player other = Bukkit.getPlayer(args[1]);
                other.getInventory().addItem(item);
                MessageEnum.cmdItem.sendConvertedMessage(other, item);
            }
        } else {
        }
    }

    @Override
    void debug() {
        // Do nothing
    }
}
