package com.github.erozabesu.yplkart.Enum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.Data.RaceData;
import com.github.erozabesu.yplkart.Data.Settings;

public enum EnumItem {
    CheckPoint(
            Permission.op_cmd_circuit,
            Material.NETHER_STAR,
            (byte) 0,
            0,
            1,
            64,
            "チェックポイントツール",
            ""),
    MarioHat(
            null,
            Material.REDSTONE_BLOCK,
            (byte) 0,
            0,
            1,
            64,
            "赤い帽子",
            "ひげのおじさんになれる赤い帽子。"),
    ItemBoxTool(
            Permission.op_cmd_itemboxtool,
            Material.INK_SACK,
            (byte) 0,
            0,
            1,
            64,
            "アイテムボックスツール",
            "アイテムボックスを設置します。"),
    ItemBoxToolTier2(
            Permission.op_cmd_itemboxtool,
            Material.INK_SACK,
            (byte) 0,
            0,
            1,
            64,
            "高級アイテムボックスツール",
            "1段階豪華なアイテムをゲットできるアイテムボックスを設置します。"),
    FakeItemBoxTool(
            Permission.op_cmd_itemboxtool,
            Material.INK_SACK,
            (byte) 0,
            0,
            1,
            64,
            "にせアイテムボックスツール",
            "にせアイテムボックスを設置します。当たっても復活します。"),
    Menu(
            null,
            Material.GOLD_INGOT,
            (byte) 0,
            0,
            1,
            64,
            "メニューを開きます",
            ""),

    Star(
            Permission.use_star,
            Material.NETHER_STAR,
            (byte) 0,
            Settings.StarTier,
            1,
            Settings.StarMaxStackSize,
            "スーパースター",
            "一定時間無敵になり、ぶつかったプレイヤーがひどい目に遭います。さらにスピードも速くなります。"),
    StarMultiple(
            Permission.use_star,
            Material.NETHER_STAR,
            (byte) 0,
            Settings.StarMultipleTier,
            Settings.StarMultipleDropAmount,
            Settings.StarMaxStackSize,
            "スーパースター",
            "一定時間無敵になり、ぶつかったプレイヤーがひどい目に遭います。さらにスピードも速くなります。"),
    Mushroom(
            Permission.use_mushroom,
            Material.INK_SACK,
            (byte) 1,
            Settings.MushroomTier,
            1,
            Settings.MushroomMaxStackSize,
            "ダッシュキノコ",
            "一定時間プレイヤーのスピードが上がります。"),
    MushroomMultiple(
            Permission.use_mushroom,
            Material.INK_SACK,
            (byte) 1,
            Settings.MushroomMultipleTier,
            Settings.MushroomMultipleDropAmount,
            Settings.MushroomMaxStackSize,
            "ダッシュキノコ",
            "一定時間プレイヤーのスピードが上がります。"),
    PowerfullMushroom(
            Permission.use_powerfullmushroom,
            Material.INK_SACK,
            (byte) 2,
            Settings.PowerfullMushroomTier,
            1,
            Settings.PowerfullMushroomMaxStackSize,
            "パワフルダッシュキノコ",
            "一定時間プレイヤーのスピードが上がります。"),
    PowerfullMushroomMultiple(
            Permission.use_powerfullmushroom,
            Material.INK_SACK,
            (byte) 2,
            Settings.PowerfullMushroomMultipleTier,
            Settings.PowerfullMushroomMultipleDropAmount,
            Settings.PowerfullMushroomMaxStackSize,
            "パワフルダッシュキノコ",
            "一定時間プレイヤーのスピードが上がります。"),
    Turtle(
            Permission.use_turtle,
            Material.INK_SACK,
            (byte) 3,
            Settings.TurtleTier,
            1,
            Settings.TurtleMaxStackSize,
            "ミドリこうら",
            "投げると直進し、当たったプレイヤーを爆破します。"),
    TurtleMultiple(
            Permission.use_turtle,
            Material.INK_SACK,
            (byte) 3,
            Settings.TurtleMultipleTier,
            Settings.TurtleMultipleDropAmount,
            Settings.TurtleMaxStackSize,
            "ミドリこうら",
            "投げると直進し、当たったプレイヤーを爆破します。"),
    RedTurtle(
            Permission.use_redturtle,
            Material.INK_SACK,
            (byte) 4,
            Settings.RedTurtleTier,
            1,
            Settings.RedTurtleMaxStackSize,
            "アカこうら",
            "投げると前方のプレイヤーを追いかけ、当たったプレイヤーを爆破します。"),
    RedTurtleMultiple(
            Permission.use_redturtle,
            Material.INK_SACK,
            (byte) 4,
            Settings.RedTurtleMultipleTier,
            Settings.RedTurtleMultipleDropAmount,
            Settings.RedTurtleMaxStackSize,
            "アカこうら",
            "投げると前方のプレイヤーを追いかけ、当たったプレイヤーを爆破します。"),
    ThornedTurtle(
            Permission.use_thornedturtle,
            Material.INK_SACK,
            (byte) 5,
            Settings.ThornedTurtleTier,
            1,
            Settings.ThornedTurtleMaxStackSize,
            "トゲゾーこうら",
            "投げると先頭のプレイヤーを追いかけ、当たったプレイヤーを爆破します。先頭のプレイヤーに追いつくまで、他のプレイヤーに当たっても進み続けます。"),
    ThornedTurtleMultiple(
            Permission.use_thornedturtle,
            Material.INK_SACK,
            (byte) 5,
            Settings.ThornedTurtleMultipleTier,
            Settings.ThornedTurtleMultipleDropAmount,
            Settings.ThornedTurtleMaxStackSize,
            "トゲゾーこうら",
            "投げると先頭のプレイヤーを追いかけ、当たったプレイヤーを爆破します。先頭のプレイヤーに追いつくまで、他のプレイヤーに当たっても進み続けます。"),
    Banana(
            Permission.use_banana,
            Material.INK_SACK,
            (byte) 6,
            Settings.BananaTier,
            1,
            Settings.BananaMaxStackSize,
            "バナナ",
            "コースに設置でき、接触したプレイヤーを減速させます。"),
    BananaMultiple(
            Permission.use_banana,
            Material.INK_SACK,
            (byte) 6,
            Settings.BananaMultipleTier,
            Settings.BananaMultipleDropAmount,
            Settings.BananaMaxStackSize,
            "バナナ",
            "コースに設置でき、接触したプレイヤーを減速させます。"),
    FakeItembox(
            Permission.use_fakeitembox,
            Material.INK_SACK,
            (byte) 7,
            Settings.FakeItemBoxTier,
            1,
            Settings.FakeItemBoxMaxStackSize,
            "にせアイテムボックス",
            "アイテムボックスにそっくりですが、触れると減速します。"),
    FakeItemboxMultiple(
            Permission.use_fakeitembox,
            Material.INK_SACK,
            (byte) 7,
            Settings.FakeItemBoxMultipleTier,
            Settings.FakeItemBoxMultipleDropAmount,
            Settings.FakeItemBoxMaxStackSize,
            "にせアイテムボックス",
            "アイテムボックスにそっくりですが、触れると減速します。"),
    Thunder(
            Permission.use_thunder,
            Material.INK_SACK,
            (byte) 8,
            Settings.ThunderTier,
            1,
            Settings.ThunderMaxStackSize,
            "サンダー",
            "ライバルのプレイヤーが一定時間遅くなります。"),
    ThunderMultiple(
            Permission.use_thunder,
            Material.INK_SACK,
            (byte) 8,
            Settings.ThunderMultipleTier,
            Settings.ThunderMultipleDropAmount,
            Settings.ThunderMaxStackSize,
            "サンダー",
            "ライバルのプレイヤーが一定時間遅くなります。"),
    Teresa(
            Permission.use_teresa,
            Material.INK_SACK,
            (byte) 9,
            Settings.TeresaTier,
            1,
            Settings.TeresaMaxStackSize,
            "テレサ",
            "一定時間透明になり、ライバルの攻撃を受けなくなります。さらにランダムでライバルからアイテムを奪います。"),
    TeresaMultiple(
            Permission.use_teresa,
            Material.INK_SACK,
            (byte) 9,
            Settings.TeresaMultipleTier,
            Settings.TeresaMultipleDropAmount,
            Settings.TeresaMaxStackSize,
            "テレサ",
            "一定時間透明になり、ライバルの攻撃を受けなくなります。さらにランダムでライバルからアイテムを奪います。"),
    Gesso(
            Permission.use_gesso,
            Material.INK_SACK,
            (byte) 10,
            Settings.GessoTier,
            1,
            Settings.GessoMaxStackSize,
            "ゲッソー",
            "自分より上位のプレイヤーの画面に墨を吐いて画面を見難くします。"),
    GessoMultiple(
            Permission.use_gesso,
            Material.INK_SACK,
            (byte) 10,
            Settings.GessoMultipleTier,
            Settings.GessoMultipleDropAmount,
            Settings.GessoMaxStackSize,
            "ゲッソー",
            "自分より上位のプレイヤーの画面に墨を吐いて画面を見難くします。"),
    Killer(
            Permission.use_killer,
            Material.INK_SACK,
            (byte) 11,
            Settings.KillerTier,
            1,
            Settings.KillerMaxStackSize,
            "キラー",
            ""),
    KillerMultiple(
            Permission.use_killer,
            Material.INK_SACK,
            (byte) 11,
            Settings.KillerMultipleTier,
            Settings.KillerMultipleDropAmount,
            Settings.KillerMaxStackSize,
            "キラー",
            "");

    private Permission perm;
    private Material material;
    private byte data;
    private int tier;
    private int amount;
    private int maxstack;
    private String name;
    private String lore;
    private static List<String> ignorelist = Arrays.asList("CheckPoint", "MarioHat", "Menu", "Multiple");

    private EnumItem(Permission perm, Material material, byte data, int tier, int amount, int maxstack, String name,
            String lore) {
        this.perm = perm;
        this.material = material;
        this.data = data;
        this.tier = tier;
        this.amount = amount;
        this.maxstack = maxstack;
        this.name = name;
        this.lore = lore;
    }

    public void reload(int tier, int amount, int maxstack) {
        this.tier = tier;
        this.amount = amount;
        this.maxstack = maxstack;
    }

    public Permission getPermission() {
        return this.perm;
    }

    public Material getType() {
        return this.material;
    }

    public byte getData() {
        return this.data;
    }

    public Integer getTier() {
        return this.tier;
    }

    public int getAmount() {
        return this.amount;
    }

    public int getMaxstackSize() {
        return this.maxstack;
    }

    public static int getMaxstackSize(ItemStack i) {
        int maxstacksize = i.getMaxStackSize();
        for (EnumItem item : EnumItem.values()) {
            if (item.isSimilar(i))
                maxstacksize = item.getMaxstackSize();
        }
        return maxstacksize;
    }

    public String getName() {
        return this.name;
    }

    public String getLore() {
        return this.lore;
    }

    public ItemStack getItem() {
        ItemStack i = new ItemStack(getType(), getAmount(), (short) 0, getData());
        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + getName());
        meta.setLore(Arrays.asList(getLore()));
        i.setItemMeta(meta);
        return i;
    }

    public Boolean isSimilar(ItemStack i) {
        if (i == null)
            return false;
        if (getName().equalsIgnoreCase(CheckPoint.getName())) {
            if (i.getType().equals(getType()))
                if (i.getData().getData() == getData())
                    if (i.getItemMeta().hasDisplayName() && i.getItemMeta().hasLore())
                        if (i.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + getName()))
                            if (RaceData.getCircuitSet().contains(i.getItemMeta().getLore().get(0)))
                                return true;
        } else {
            if (i.getType().equals(getType()))
                if (i.getData().getData() == getData())
                    if (i.getItemMeta().hasDisplayName() && i.getItemMeta().hasLore())
                        if (i.getItemMeta().getLore().get(0).equalsIgnoreCase(getLore())
                                && i.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + getName()))
                            return true;
        }
        return false;
    }

    // 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static String getItemList() {
        String list = null;
        for (EnumItem item : values()) {
            if (ignorelist.contains(item.name()))
                continue;
            if (list == null)
                list = item.name();
            else
                list += ", " + item.name();
        }
        return list;
    }

    public static List<String> getItemArrayList() {
        List<String> list = new ArrayList<String>();
        for (EnumItem item : values()) {
            if (ignorelist.contains(item.name()))
                continue;
            list.add(item.name());
        }
        return list;
    }

    public static EnumItem getEnumItem(String name) {
        for (EnumItem item : values()) {
            if (item.name().equalsIgnoreCase(name))
                return item;
            else if (item.name().equalsIgnoreCase(name.toLowerCase()))
                return item;
            else if (item.name().toLowerCase().equalsIgnoreCase(name))
                return item;
            else if (item.name().toLowerCase().equalsIgnoreCase(name.toLowerCase()))
                return item;
        }
        return null;
    }

    public static ArrayList<EnumItem> getItemfromTier(int grade) {
        ArrayList<EnumItem> item = new ArrayList<EnumItem>();
        for (EnumItem i : EnumItem.values()) {
            if (i.getTier() != 0)
                if (i.getTier() <= grade)
                    if (grade - 1 <= i.getTier())
                        item.add(i);
        }
        return item;
    }

    public static ItemStack getRandomItemfromTier(Player p, int grade) {
        ArrayList<EnumItem> itemlist = getItemfromTier(grade);
        Iterator<EnumItem> i = itemlist.iterator();
        EnumItem item;
        while (i.hasNext()) {
            item = i.next();
            if (!Permission.hasPermission(p, item.getPermission(), true)) {
                i.remove();
                itemlist.remove(item);
            }
        }

        if (itemlist.size() == 0)
            return null;

        return itemlist.get(new Random().nextInt(itemlist.size())).getItem();
    }

    public static ItemStack getCheckPointTool(EnumItem item, String circuitname) {
        ItemStack i = new ItemStack(item.getType(), item.getAmount(), (short) 0, item.getData());
        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + item.getName());
        meta.setLore(Arrays.asList(circuitname));
        i.setItemMeta(meta);
        return i;
    }

    // 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static Boolean isKeyItem(ItemStack i) {
        for (EnumItem item : values()) {
            if (item.isSimilar(i))
                return true;
        }
        return false;
    }

    // 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static void removeAllKeyItems(Player p) {
        PlayerInventory inv = p.getInventory();

        for (int i = 0; i < 36; i++) {
            if (isKeyItem(inv.getItem(i)))
                inv.setItem(i, null);
        }

        if (inv.getHelmet() != null)
            if (EnumItem.isKeyItem(inv.getHelmet()))
                inv.setHelmet(new ItemStack(Material.AIR));
        if (inv.getChestplate() != null)
            if (EnumItem.isKeyItem(inv.getChestplate()))
                inv.setChestplate(new ItemStack(Material.AIR));
        if (inv.getLeggings() != null)
            if (EnumItem.isKeyItem(inv.getLeggings()))
                inv.setLeggings(new ItemStack(Material.AIR));
        if (inv.getBoots() != null)
            if (EnumItem.isKeyItem(inv.getBoots()))
                inv.setBoots(new ItemStack(Material.AIR));
        p.updateInventory();
    }

    public static void removeUnuseslotKeyItems(Player p) {
        PlayerInventory inv = p.getInventory();
        int i = Settings.ItemSlot;
        i += RaceManager.getRace(p).getCharacter() == null ? 0 : RaceManager.getRace(p).getCharacter()
                .getItemAdjustMaxSlotSize();
        for (int j = i; j < 36; j++) {
            if (isKeyItem(inv.getItem(j)))
                inv.setItem(j, null);
        }
        p.updateInventory();
    }

    public static void addItem(Player p, ItemStack item) {
        Inventory inv = p.getInventory();
        inv.addItem(item);

        adjustInventoryfromMaxstacksize(p);
        removeUnuseslotKeyItems(p);
        p.updateInventory();
    }

    // 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    private static void adjustInventoryfromMaxstacksize(Player p) {
        Inventory inv = p.getInventory();
        ArrayList<ItemStack> over = new ArrayList<ItemStack>();

        ItemStack temp;
        ItemStack flow;
        int maxstacksize;

        for (int i = 0; i < 36; i++) {
            if (inv.getItem(i) == null)
                continue;

            temp = inv.getItem(i);
            maxstacksize = EnumItem.getMaxstackSize(temp);
            maxstacksize += RaceManager.getRace(p).getCharacter() == null ? 0 : RaceManager.getRace(p).getCharacter()
                    .getItemAdjustMaxStackSize();

            if (maxstacksize < temp.getAmount()) {
                flow = temp.clone();
                flow.setAmount(flow.getAmount() - maxstacksize);
                temp.setAmount(maxstacksize);
            } else
                continue;

            while (maxstacksize < flow.getAmount()) {
                int flowsize = flow.getAmount() - maxstacksize;
                flow.setAmount(maxstacksize);
                over.add(flow.clone());
                flow.setAmount(flowsize);
            }
            over.add(flow.clone());
        }

        if (over.isEmpty())
            return;

        int overindex = 0;
        for (int j = 0; j < 36; j++) {
            try {
                over.get(overindex);
            } catch (IndexOutOfBoundsException ex) {
                break;
            }

            if (inv.getItem(j) != null) {
                if (inv.getItem(j).isSimilar(over.get(overindex))) {
                    int tempmaxstacksize = EnumItem.getMaxstackSize(inv.getItem(j));
                    tempmaxstacksize += RaceManager.getRace(p).getCharacter() == null ? 0 : RaceManager.getRace(p)
                            .getCharacter().getItemAdjustMaxStackSize();
                    if (inv.getItem(j).getAmount() < tempmaxstacksize) {
                        int tempflow = (inv.getItem(j).getAmount() + over.get(overindex).getAmount())
                                - tempmaxstacksize;
                        if (tempflow < 0) {
                            inv.getItem(j).setAmount(tempmaxstacksize);
                            overindex++;
                        } else {
                            inv.getItem(j).setAmount(tempmaxstacksize);
                            over.get(overindex).setAmount(tempflow);
                        }
                        continue;
                    }
                }
            } else {
                inv.setItem(j, over.get(overindex));
                overindex++;
            }
        }
    }
}
