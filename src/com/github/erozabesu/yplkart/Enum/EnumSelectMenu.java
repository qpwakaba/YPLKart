package com.github.erozabesu.yplkart.Enum;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum EnumSelectMenu {
    CHARACTER_CANCEL(ChatColor.GOLD + "選択画面を閉じます", Material.DIAMOND, (byte) 0),
    CHARACTER_RANDOM(ChatColor.GOLD + "キャラクターをランダムに選択します", Material.IRON_INGOT, (byte) 0),
    CHARACTER_PREVIOUS(ChatColor.GOLD + "カート選択画面に移動します", Material.COAL, (byte) 0),
    CHARACTER_NEXT(ChatColor.GOLD + "カート選択画面に移動します", Material.COAL, (byte) 1),

    KART_CANCEL(ChatColor.GOLD + "選択画面を閉じます", Material.DIAMOND, (byte) 0),
    KART_RANDOM(ChatColor.GOLD + "カートをランダムに選択します", Material.IRON_INGOT, (byte) 0),
    KART_PREVIOUS(ChatColor.GOLD + "キャラクター選択画面に移動します", Material.COAL, (byte) 0),
    KART_NEXT(ChatColor.GOLD + "キャラクター選択画面に移動します", Material.COAL, (byte) 1);

    private String name;
    private Material material;
    private byte data;

    private EnumSelectMenu(String name, Material material, byte data) {
        this.name = name;
        this.material = material;
        this.data = data;
    }

    public String getName() {
        return this.name;
    }

    public Material getType() {
        return this.material;
    }

    public byte getData() {
        return this.data;
    }

    public ItemStack getMenuItem() {
        ItemStack item = new ItemStack(getType(), 1, (short) 0, getData());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(getName());

        item.setItemMeta(meta);

        return item;
    }

    public boolean equalsIgnoreCase(String text) {
        if (ChatColor.stripColor(getName()).equalsIgnoreCase(ChatColor.stripColor(text)))
            return true;
        return false;
    }
}
