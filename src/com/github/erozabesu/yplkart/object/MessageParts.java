package com.github.erozabesu.yplkart.object;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.erozabesu.yplkart.Permission;
import com.github.erozabesu.yplkart.enumdata.RaceType;
import com.github.erozabesu.yplkart.enumdata.TagType;

public class MessageParts {

    /** パーツテキスト */
    private String[] partsText;

    /** テキストメッセージのタグタイプ */
    private TagType tagType;

    public MessageParts(TagType tagType, String... partsText) {
        this.setMessage(partsText);
        this.setTagType(tagType);
    }

    //〓 Util 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static MessageParts getMessageParts(Player player) {
        return new MessageParts(TagType.PLAYER, player.getName());
    }

    public static MessageParts getMessageParts(Player... player) {
        String[] playerNameArray = new String[player.length];
        for (int i = 0; i < player.length; i++) {
            playerNameArray[i] = player[i].getName();
        }
        return new MessageParts(TagType.PLAYER_ARRAY, playerNameArray);
    }

    public static MessageParts getMessageParts(RaceType raceType) {
        return new MessageParts(TagType.RACE_TYPE, raceType.name());
    }

    public static MessageParts getMessageParts(Permission permission) {
        return new MessageParts(TagType.PERMISSION, permission.getPermissionNode());
    }

    public static MessageParts getMessageParts(Character character) {
        return new MessageParts(TagType.CHARACTER, character.getCharacterName());
    }

    public static MessageParts getMessageParts(Kart kart) {
        return new MessageParts(TagType.KART, kart.getKartName());
    }

    public static MessageParts getMessageParts(ItemStack itemStack) {
        return new MessageParts(TagType.ITEM, itemStack.hasItemMeta() ? itemStack.getItemMeta().getDisplayName() : "");
    }

    public static MessageParts getMessageParts(Number number) {
        return new MessageParts(TagType.NUMBER, String.valueOf(number));
    }

    public static MessageParts getMessageParts(Number... number) {
        String[] numberArray = new String[number.length];
        for (int i = 0; i < number.length; i++) {
            numberArray[i] = String.valueOf(number[i]);
        }
        return new MessageParts(TagType.NUMBER_ARRAY, numberArray);
    }

    public static MessageParts getMessageParts(Circuit circuit) {
        return new MessageParts(TagType.CIRCUIT, circuit.getCircuitName());
    }

    public static MessageParts getMessageParts(CircuitData circuitData) {
        return new MessageParts(TagType.CIRCUIT, circuitData.getCircuitName());
    }

    public static MessageParts getMessageParts(boolean flag) {
        return new MessageParts(TagType.FLAG, String.valueOf(flag));
    }

    public static MessageParts getMessageParts(String text) {
        return new MessageParts(TagType.TEXT, text);
    }

    public static MessageParts getMessageParts(String... text) {
        return new MessageParts(TagType.TEXT_ARRAY, text);
    }

    //〓 Getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @return パーツテキスト */
    public String[] getMessage() {
        return partsText;
    }

    /** @return パーツテキストのタグタイプ */
    public TagType getTagType() {
        return tagType;
    }

    //〓 Setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @param message パーツテキスト */
    public void setMessage(String[] partsText) {
        this.partsText = partsText;
    }

    /** @param tagType パーツテキストのタグタイプ */
    public void setTagType(TagType tagType) {
        this.tagType = tagType;
    }
}
