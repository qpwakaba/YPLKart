package com.github.erozabesu.yplkart.cmd;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.data.Messages;
import com.github.erozabesu.yplkart.data.RaceData;
import com.github.erozabesu.yplkart.data.Settings;
import com.github.erozabesu.yplkart.enumdata.EnumCharacter;
import com.github.erozabesu.yplkart.enumdata.EnumKarts;
import com.github.erozabesu.yplkart.enumdata.Permission;
import com.github.erozabesu.yplkart.utils.Util;

public class CMDAbstractPlayer extends CMDAbstract {
    Player p;
    UUID id;
    String[] args;
    int length;

    public CMDAbstractPlayer(Player p, String[] args) {
        this.p = p;
        this.id = p.getUniqueId();
        this.args = args;
        this.length = args.length;
    }

    @Override
    void ka() {
        if (!Permission.hasCMDPermission(this.p, Permission.CMD_KA, false, false))
            return;

        Messages.reference.sendMessage(this.p);
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
        if (this.length == 2) {
            if (args[1].equalsIgnoreCase("accept")) {
                RaceManager.setMatchingCircuitData(this.id);
                return;
            } else if (args[1].equalsIgnoreCase("deny")) {
                RaceManager.clearMatchingCircuitData(this.id);
                return;
            }
        }

        if (!Permission.hasCMDPermission(this.p, Permission.OP_CMD_CIRCUIT, false, false))
            return;
        if (this.length == 2) {
            if (args[1].equalsIgnoreCase("list")) {
                RaceData.listCricuit(this.p);
                return;
            }
        } else if (this.length == 3) {
            if (args[1].equalsIgnoreCase("info")) {
                RaceData.sendCircuitInformation(this.p, args[2]);
                return;
            } else if (args[1].equalsIgnoreCase("create")) {
                RaceData.createCircuit(this.p, args[2]);
                return;
            } else if (args[1].equalsIgnoreCase("delete")) {
                RaceData.deleteCircuit(this.p, args[2]);
                return;
            } else if (args[1].equalsIgnoreCase("edit")) {
                RaceData.editCircuit(this.p, args[2]);
                return;
            } else if (args[1].equalsIgnoreCase("setposition")) {
                RaceData.setPosition(this.p, args[2]);
                return;
            }
        } else if (this.length == 4) {
            if (args[1].equalsIgnoreCase("rename")) {
                RaceData.renameCircuit(this.p, args[2], args[3]);
                return;
            } else if (args[1].equalsIgnoreCase("setminplayer")) {
                if (!Util.isNumber(args[3])) {
                    Messages.invalidNumber.sendMessage(this.p);
                    return;
                }
                RaceData.setMinPlayer(this.p, args[2], Integer.valueOf(args[3]));
                return;
            } else if (args[1].equalsIgnoreCase("setmaxplayer")) {
                if (!Util.isNumber(args[3])) {
                    Messages.invalidNumber.sendMessage(this.p);
                    return;
                }
                RaceData.setMaxPlayer(this.p, args[2], Integer.valueOf(args[3]));
                return;
            } else if (args[1].equalsIgnoreCase("setmatchingtime")) {
                if (!Util.isNumber(args[3])) {
                    Messages.invalidNumber.sendMessage(this.p);
                    return;
                }
                RaceData.setMatchingTime(this.p, args[2], Integer.valueOf(args[3]));
                return;
            } else if (args[1].equalsIgnoreCase("setmenutime")) {
                if (!Util.isNumber(args[3])) {
                    Messages.invalidNumber.sendMessage(this.p);
                    return;
                }
                RaceData.setMenuTime(this.p, args[2], Integer.valueOf(args[3]));
                return;
            } else if (args[1].equalsIgnoreCase("setlimittime")) {
                if (!Util.isNumber(args[3])) {
                    Messages.invalidNumber.sendMessage(this.p);
                    return;
                }
                RaceData.setLimitTime(this.p, args[2], Integer.valueOf(args[3]));
                return;
            } else if (args[1].equalsIgnoreCase("setlap")) {
                if (!Util.isNumber(args[3])) {
                    Messages.invalidNumber.sendMessage(this.p);
                    return;
                }
                RaceData.setNumberOfLaps(this.p, args[2], Integer.valueOf(args[3]));
                return;
            } else if (args[1].equalsIgnoreCase("broadcastgoal")) {
                if (!Util.isBoolean(args[3])) {
                    Messages.invalidBoolean.sendMessage(this.p);
                    return;
                }
                RaceData.setBroadcastGoalMessage(this.p, args[2], Boolean.valueOf(args[3]));
                return;
            }
        } else if (this.length == 9) {
            if (Bukkit.getWorld(args[3]) == null) {
                Messages.invalidWorld.sendMessage(this.p);
                return;
            }
            if (!Util.isNumber(args[4]) || !Util.isNumber(args[5]) || !Util.isNumber(args[6])
                    || !Util.isNumber(args[7]) || !Util.isNumber(args[8])) {
                Messages.invalidNumber.sendMessage(this.p);
                return;
            }
            //0:circuit 1:create 2:circuitname 3:worldname 4:x 5:y 6:z
            if (args[1].equalsIgnoreCase("create")) {
                RaceData.createCircuit(this.p, args[2], args[3], Double.valueOf(args[4]), Double.valueOf(args[5]),
                        Double.valueOf(args[6]), Float.valueOf(args[7]), Float.valueOf(args[8]));
                return;
                //0:circuit 1:setposition 2:circuitname 3:worldname 4:x 5:y 6:z
            } else if (args[1].equalsIgnoreCase("setposition")) {
                RaceData.setPosition(this.p, args[2], args[3], Double.valueOf(args[4]), Double.valueOf(args[5]),
                        Double.valueOf(args[6]), Float.valueOf(args[7]), Float.valueOf(args[8]));
                return;
            }
        }
        Messages.referenceCircuitIngame.sendMessage(this.p);
        Messages.referenceCircuitOutgame.sendMessage(this.p);
    }

    //ka display {kart name}
    //ka display random
    //ka display {kart name} {worldname} {x} {y} {z} {yaw} {pitch}
    //ka display random {worldname} {x} {y} {z}  {yaw} {pitch}
    @Override
    void display() {
        if (length == 2) {
            if (!Permission.hasCMDPermission(this.p, Permission.OP_CMD_DISPLAY, false, false))
                return;
            EnumKarts kart = null;
            if (args[1].equalsIgnoreCase("random"))
                kart = EnumKarts.getRandomKart();
            else
                kart = EnumKarts.getKartfromString(args[1]);
            if (kart == null) {
                Messages.invalidKart.sendMessage(this.p);
                return;
            }

            RaceManager.createDisplayMinecart(this.p.getLocation(), kart, null);
            Messages.cmdDisplayCreate.sendMessage(this.p, kart);
        } else if (length == 8) {
            if (!Permission.hasCMDPermission(this.p, Permission.OP_CMD_DISPLAY, false, false))
                return;
            if (Bukkit.getWorld(args[2]) == null) {
                Messages.invalidWorld.sendMessage(this.p);
                return;
            }
            if (!Util.isNumber(args[3]) || !Util.isNumber(args[4]) || !Util.isNumber(args[5])
                    || !Util.isNumber(args[6]) || !Util.isNumber(args[7])) {
                Messages.invalidNumber.sendMessage(this.p);
                return;
            }
            EnumKarts kart = null;
            if (args[1].equalsIgnoreCase("random"))
                kart = EnumKarts.getRandomKart();
            else
                kart = EnumKarts.getKartfromString(args[1]);
            if (kart == null) {
                Messages.invalidKart.sendMessage(this.p);
                return;
            }

            RaceManager.createDisplayMinecart(
                    new Location(Bukkit.getWorld(args[2]), Double.valueOf(args[3]), Double.valueOf(args[4]), Double
                            .valueOf(args[5]), Float.valueOf(args[6]), Float.valueOf(args[7])), kart, null);
            Messages.cmdDisplayCreate.sendMessage(this.p, kart);
        } else {
            Messages.referenceDisplayIngame.sendMessage(this.p);
            Messages.referenceDisplayOutgame.sendMessage(this.p);
        }
    }

    //ka menu
    //ka menu {player}
    //ka menu all
    @Override
    void menu() {
        if (this.length == 1) {
            if (!Permission.hasCMDPermission(this.p, Permission.CMD_MENU, false, false))
                return;
            RaceManager.showCharacterSelectMenu(this.p);
        } else if (length == 2) {
            if (!Permission.hasCMDPermission(this.p, Permission.CMD_MENU, true, false))
                return;

            if (args[1].equalsIgnoreCase("all")) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    RaceManager.showCharacterSelectMenu(other);
                }
                Messages.cmdMenuAll.sendMessage(this.p);
            } else {
                if (!Util.isOnline(args[1])) {
                    Messages.invalidPlayer.sendMessage(this.p);
                    return;
                }

                Player other = Bukkit.getPlayer(args[1]);
                RaceManager.showCharacterSelectMenu(other);
                Messages.cmdMenuOther.sendMessage(this.p, other);
            }
        } else {
            Messages.referenceMenu.sendMessage(this.p);
            Messages.referenceMenuOther.sendMessage(this.p);
        }
    }

    //ka entry {circuit name}
    //ka entry {player name} {circuit name}
    //ka entry all {circuit name}
    @Override
    void entry() {
        //ka entry {circuit name}
        if (this.length == 2) {
            if (!Permission.hasCMDPermission(this.p, Permission.CMD_ENTRY, false, false))
                return;
            if (!RaceData.isCircuit(args[1])) {
                Messages.invalidCircuit.sendMessage(this.p, args[1]);
                return;
            }

            RaceManager.setEntryRaceData(this.id, args[1]);
            //ka entry {player name} {circuit name}
            //ka entry all {circuit name}
        } else if (this.length == 3) {
            if (!Permission.hasCMDPermission(this.p, Permission.CMD_ENTRY, true, false))
                return;
            if (!RaceData.isCircuit(args[2])) {
                Messages.invalidCircuit.sendMessage(this.p, args[1]);
                return;
            }
            if (args[1].equalsIgnoreCase("all")) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    RaceManager.setEntryRaceData(other.getUniqueId(), args[2]);
                }
                Messages.cmdEntryAll.sendMessage(this.p, args[2]);
            } else {
                if (!Util.isOnline(args[1])) {
                    Messages.invalidPlayer.sendMessage(this.p);
                    return;
                }
                Player other = Bukkit.getPlayer(args[1]);
                Messages.cmdEntryOther.sendMessage(this.p, new Object[] { other, args[2] });
                RaceManager.setEntryRaceData(other.getUniqueId(), args[2]);
            }
        } else {
            Messages.referenceEntry.sendMessage(this.p);
            Messages.referenceEntryOther.sendMessage(this.p);
        }
    }

    //ka exit
    //ka exit {player}
    //ka exit all
    @Override
    void exit() {
        if (this.length == 1) {
            if (!Permission.hasCMDPermission(this.p, Permission.CMD_EXIT, false, false))
                return;
            RaceManager.clearEntryRaceData(this.id);
        } else if (length == 2) {
            if (!Permission.hasCMDPermission(this.p, Permission.CMD_EXIT, true, false))
                return;

            if (args[1].equalsIgnoreCase("all")) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    RaceManager.clearEntryRaceData(other.getUniqueId());
                }
                Messages.cmdExitAll.sendMessage(this.p);
            } else {
                if (!Util.isOnline(args[1])) {
                    Messages.invalidPlayer.sendMessage(this.p);
                    return;
                }

                Player other = Bukkit.getPlayer(args[1]);
                RaceManager.clearEntryRaceData(other.getUniqueId());
                Messages.cmdExitOther.sendMessage(this.p, other);
            }
        } else {
            Messages.referenceExit.sendMessage(this.p);
            Messages.referenceExitOther.sendMessage(this.p);
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
        if (this.length == 2) {
            if (!Permission.hasCMDPermission(this.p, Permission.CMD_CHARACTER, false, false))
                return;
            if (args[1].equalsIgnoreCase("random")) {
                RaceManager.setCharacterRaceData(this.id, EnumCharacter.getRandomCharacter());
                //ka character {character name}
            } else {
                EnumCharacter character = EnumCharacter.getClassfromString(args[1]);
                if (character == null) {
                    Messages.invalidCharacter.sendMessage(this.p);
                    return;
                }
                RaceManager.setCharacterRaceData(this.id, character);
            }
            //ka character {player} {character name}
            //ka character all {character name}
            //ka character {player} random
            //ka character all random
        } else if (this.length == 3) {
            if (!Permission.hasCMDPermission(this.p, Permission.CMD_CHARACTER, true, false))
                return;
            if (args[2].equalsIgnoreCase("random")) {
                if (args[1].equalsIgnoreCase("all")) {
                    for (Player other : Bukkit.getOnlinePlayers()) {
                        RaceManager.setCharacterRaceData(other.getUniqueId(), EnumCharacter.getRandomCharacter());
                    }
                    Messages.cmdCharacterRandomAll.sendMessage(this.p);
                } else {
                    if (!Util.isOnline(args[1])) {
                        Messages.invalidPlayer.sendMessage(this.p);
                        return;
                    }
                    EnumCharacter character = EnumCharacter.getRandomCharacter();
                    Player other = Bukkit.getPlayer(args[1]);
                    RaceManager.setCharacterRaceData(other.getUniqueId(), character);
                    Messages.cmdCharacterOther.sendMessage(this.p, new Object[] { other, character });
                }
            } else {
                EnumCharacter character = EnumCharacter.getClassfromString(args[2]);
                if (character == null) {
                    Messages.invalidCharacter.sendMessage(this.p);
                    return;
                }
                if (args[1].equalsIgnoreCase("all")) {
                    for (Player other : Bukkit.getOnlinePlayers()) {
                        RaceManager.setCharacterRaceData(other.getUniqueId(), character);
                    }
                    Messages.cmdCharacterAll.sendMessage(this.p, character);
                } else {
                    if (!Util.isOnline(args[1])) {
                        Messages.invalidPlayer.sendMessage(this.p);
                        return;
                    }
                    Player other = Bukkit.getPlayer(args[1]);
                    RaceManager.setCharacterRaceData(other.getUniqueId(), character);
                    Messages.cmdCharacterOther.sendMessage(this.p, new Object[] { other, character });
                }
            }
        } else {
            Messages.referenceCharacter.sendMessage(this.p);
            Messages.referenceCharacterOther.sendMessage(this.p);
        }
    }

    //ka characterreset
    //ka characterreset {player}
    //ka characterreset all
    @Override
    void characterreset() {
        if (this.length == 1) {
            if (!Permission.hasCMDPermission(this.p, Permission.CMD_CHARACTERRESET, false, false))
                return;
            RaceManager.clearCharacterRaceData(this.id);
        } else if (length == 2) {
            if (!Permission.hasCMDPermission(this.p, Permission.CMD_CHARACTERRESET, true, false))
                return;

            if (args[1].equalsIgnoreCase("all")) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    RaceManager.clearCharacterRaceData(other.getUniqueId());
                }
                Messages.cmdCharacterResetAll.sendMessage(this.p);
            } else {
                if (!Util.isOnline(args[1])) {
                    Messages.invalidPlayer.sendMessage(this.p);
                    return;
                }

                Player other = Bukkit.getPlayer(args[1]);
                RaceManager.clearCharacterRaceData(other.getUniqueId());
                Messages.cmdCharacterResetOther.sendMessage(this.p, other);
            }
        } else {
            Messages.referenceCharacterReset.sendMessage(this.p);
            Messages.referenceCharacterResetOther.sendMessage(this.p);
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
        if (this.length == 2) {
            if (!Permission.hasCMDPermission(this.p, Permission.CMD_RIDE, false, false))
                return;
            if (args[1].equalsIgnoreCase("random")) {
                RaceManager.setKartRaceData(this.id, EnumKarts.getRandomKart());
            } else {
                EnumKarts kart = EnumKarts.getKartfromString(args[1]);
                if (kart == null) {
                    Messages.invalidKart.sendMessage(this.p);
                    return;
                }
                RaceManager.setKartRaceData(this.id, kart);
            }
            //ka ride all {kart name}
            //ka ride {player name} {kart name}
            //ka ride all random
            //ka ride {player name} random
        } else if (this.length == 3) {
            if (!Permission.hasCMDPermission(this.p, Permission.CMD_RIDE, true, false))
                return;
            EnumKarts kart = null;
            if (args[2].equalsIgnoreCase("random"))
                kart = EnumKarts.getRandomKart();
            else
                kart = EnumKarts.getKartfromString(args[2]);
            if (kart == null) {
                Messages.invalidKart.sendMessage(this.p);
                return;
            }

            if (args[1].equalsIgnoreCase("all")) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    RaceManager.setKartRaceData(other.getUniqueId(), kart);
                }
                if (args[2].equalsIgnoreCase("random"))
                    Messages.cmdRideRandomAll.sendMessage(this.p);
                else
                    Messages.cmdRideAll.sendMessage(this.p, kart);
            } else {
                if (!Util.isOnline(args[1])) {
                    Messages.invalidPlayer.sendMessage(this.p);
                    return;
                }
                Player other = Bukkit.getPlayer(args[1]);
                RaceManager.setKartRaceData(other.getUniqueId(), kart);
                Messages.cmdRideOther.sendMessage(this.p, new Object[] { other, kart });
            }
        } else {
            Messages.referenceRide.sendMessage(this.p);
            Messages.referenceRideOther.sendMessage(this.p);
        }
    }

    //ka leave
    //ka leave {player}
    //ka leave all
    @Override
    void leave() {
        //ka leave
        if (this.length == 1) {
            if (!Permission.hasCMDPermission(this.p, Permission.CMD_LEAVE, false, false))
                return;
            RaceManager.leaveRacingKart(this.p);
            RaceManager.clearKartRaceData(this.id);
            //ka leave {player}
            //ka leave all
        } else if (length == 2) {
            if (!Permission.hasCMDPermission(this.p, Permission.CMD_LEAVE, true, false))
                return;

            if (args[1].equalsIgnoreCase("all")) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    RaceManager.leaveRacingKart(other);
                    RaceManager.clearKartRaceData(this.id);
                }
                Messages.cmdLeaveAll.sendMessage(this.p);
            } else {
                if (!Util.isOnline(args[1])) {
                    Messages.invalidPlayer.sendMessage(this.p);
                    return;
                }

                Player other = Bukkit.getPlayer(args[1]);
                RaceManager.leaveRacingKart(other);
                RaceManager.clearKartRaceData(other.getUniqueId());
                Messages.cmdLeaveOther.sendMessage(this.p, other);
            }
        } else {
            Messages.referenceLeave.sendMessage(this.p);
            Messages.referenceLeaveOther.sendMessage(this.p);
        }
    }

    @Override
    void ranking() {
        //ka ranking {circuit name}
        //ka ranking list
        if (this.length == 2) {
            if (!Permission.hasCMDPermission(this.p, Permission.CMD_RANKING, false, false))
                return;
            if (args[1].equalsIgnoreCase("list")) {
                RaceData.listCricuit(this.p);
            } else {
                if (!RaceData.isCircuit(args[1])) {
                    Messages.invalidCircuit.sendMessage(this.p, args[1]);
                    return;
                }

                RaceData.sendRanking(this.id, args[1]);
            }
            //ka ranking {player name} 	{circuit name}
            //ka ranking all 			{circuit name}
        } else if (this.length == 3) {
            if (!Permission.hasCMDPermission(this.p, Permission.CMD_RANKING, true, false))
                return;
            if (!RaceData.isCircuit(args[2])) {
                Messages.invalidCircuit.sendMessage(this.p, args[2]);
                return;
            }
            if (args[1].equalsIgnoreCase("all")) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    RaceData.sendRanking(other.getUniqueId(), args[2]);
                }
                Messages.cmdRankingAll.sendMessage(this.p, args[2]);
            } else {
                if (!Util.isOnline(args[1])) {
                    Messages.invalidPlayer.sendMessage(this.p);
                    return;
                }
                Player other = Bukkit.getPlayer(args[1]);
                RaceData.sendRanking(other.getUniqueId(), args[2]);
                Messages.cmdRankingOther.sendMessage(this.p, new Object[] { other, args[2] });
            }
        } else {
            Messages.referenceRanking.sendMessage(this.p);
            Messages.referenceRankingOther.sendMessage(this.p);
        }
    }

    @Override
    void reload() {
        if (!Permission.hasCMDPermission(this.p, Permission.OP_CMD_RELOAD, false, false))
            return;

        for (Player other : Bukkit.getOnlinePlayers()) {
            RaceManager.clearEntryRaceData(other.getUniqueId());
        }
        RaceManager.endAllCircuit();

        Settings.reloadConfig();
        Messages.reloadConfig();
        Messages.cmdReload.sendMessage(this.p);
    }

    @Override
    void additem(ItemStack item, Permission permission) {
        //ka {item}
        if (item == null && permission == null) {
            Messages.referenceAddItem.sendMessage(null);
            Messages.referenceAddItemOther.sendMessage(null);
        } else if (length == 1) {
            if (!Permission.hasCMDPermission(this.p, permission, false, false))
                return;

            this.p.getInventory().addItem(item);
            Messages.cmdItem.sendMessage(this.p, item);
        } else if (length == 2) {
            //ka {item} 64
            if (Util.isNumber(args[1])) {
                if (!Permission.hasCMDPermission(this.p, permission, false, false))
                    return;

                item.setAmount(Integer.valueOf(args[1]));
                this.p.getInventory().addItem(item);
                Messages.cmdItem.sendMessage(this.p, item);
            } else {
                if (permission.isOpPerm()) {
                    if (!Permission.hasPermission(this.p, permission, false))
                        return;
                } else if (!Permission.hasCMDPermission(this.p, permission, true, false))
                    return;

                //ka {item} all
                if (args[1].equalsIgnoreCase("all")) {
                    for (Player other : Bukkit.getOnlinePlayers()) {
                        other.getInventory().addItem(item);
                        Messages.cmdItem.sendMessage(other, item);
                    }
                    Messages.cmdItemAll.sendMessage(this.p, item);
                    //ka {item} {player}
                } else {
                    if (!Util.isOnline(args[1])) {
                        Messages.invalidPlayer.sendMessage(this.p);
                        return;
                    }

                    Player other = Bukkit.getPlayer(args[1]);
                    other.getInventory().addItem(item);
                    Messages.cmdItem.sendMessage(other, item);
                    Messages.cmdItemOther.sendMessage(this.p, new Object[] { other, item });
                }
            }
        } else if (length == 3) {
            if (permission.isOpPerm()) {
                if (!Permission.hasPermission(this.p, permission, false))
                    return;
            } else if (!Permission.hasCMDPermission(this.p, permission, true, false))
                return;
            if (!Util.isNumber(args[2])) {
                Messages.referenceAddItemOther.sendMessage(this.p);
                return;
            }
            item.setAmount(Integer.valueOf(args[2]));

            //ka {item} all 64
            if (args[1].equalsIgnoreCase("all")) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    other.getInventory().addItem(item);
                    Messages.cmdItem.sendMessage(other, item);
                }
                Messages.cmdItemAll.sendMessage(this.p, item);
                //ka {item} {player} 64
            } else {
                if (!Util.isOnline(args[1])) {
                    Messages.invalidPlayer.sendMessage(this.p);
                    return;
                }

                Player other = Bukkit.getPlayer(args[1]);
                other.getInventory().addItem(item);
                Messages.cmdItem.sendMessage(other, item);
                Messages.cmdItemOther.sendMessage(this.p, new Object[] { other, item });
            }
        } else {
            Messages.referenceAddItem.sendMessage(this.p);
            Messages.referenceAddItemOther.sendMessage(this.p);
        }
    }
}
