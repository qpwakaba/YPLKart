package com.github.erozabesu.yplkart.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.erozabesu.yplkart.ConfigManager;
import com.github.erozabesu.yplkart.Permission;
import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.object.CircuitData;

/**
 * アイテム設定を格納するクラス
 * enum要素名がアイテム取得コマンドのキーになるため注意
 * ユーザ側で要素数を変更できない静的なコンフィグを扱うためenumで管理する
 * @author erozabesu
 */
public enum ItemEnum {
    CHECKPOINT_TOOL(
            "check_point_tool",
            Permission.OP_CMD_CIRCUIT,
            "チェックポイントツール",
            ""),
    MARIO_HAT(
            "mario_hat",
            null,
            "赤い帽子",
            "ひげのおじさんになれる赤い帽子。"),
    ITEMBOX_TOOL(
            "itembox_tool",
            Permission.OP_CMD_ITEMBOXTOOL,
            "アイテムボックスツール",
            "アイテムボックスを設置します。"),
    ITEMBOX_TOOL_TIER2(
            "itembox_tool",
            Permission.OP_CMD_ITEMBOXTOOL,
            "高級アイテムボックスツール",
            "1段階豪華なアイテムをゲットできるアイテムボックスを設置します。"),
    FAKE_ITEMBOX_TOOL(
            "itembox_tool",
            Permission.OP_CMD_ITEMBOXTOOL,
            "にせアイテムボックスツール",
            "にせアイテムボックスを設置します。当たっても復活します。"),
    MENU(
            "menu",
            null,
            "メニューを開きます",
            ""),

    STAR(
            "star",
            Permission.USE_STAR,
            "スーパースター",
            "一定時間無敵になり、ぶつかったプレイヤーがひどい目に遭います。さらにスピードも速くなります。"),
    MUSHROOM(
            "mushroom",
            Permission.USE_MUSHROOM,
            "ダッシュキノコ",
            "一定時間プレイヤーのスピードが上がります。"),
    POWERFULL_MUSHROOM(
            "powerfull_mushroom",
            Permission.USE_POWERFULLMUSHROOM,
            "パワフルダッシュキノコ",
            "一定時間プレイヤーのスピードが上がります。"),
    TURTLE(
            "turtle",
            Permission.USE_TURTLE,
            "ミドリこうら",
            "投げると直進し、当たったプレイヤーを爆破します。"),
    RED_TURTLE(
            "red_turtle",
            Permission.USE_REDTURTLE,
            "アカこうら",
            "投げると前方のプレイヤーを追いかけ、当たったプレイヤーを爆破します。"),
    THORNED_TURTLE(
            "thorned_turtle",
            Permission.USE_THORNEDTURTLE,
            "トゲゾーこうら",
            "投げると先頭のプレイヤーを追いかけ、当たったプレイヤーを爆破します。先頭のプレイヤーに追いつくまで、他のプレイヤーに当たっても進み続けます。"),
    BANANA(
            "banana",
            Permission.USE_BANANA,
            "バナナ",
            "コースに設置でき、接触したプレイヤーを減速させます。"),
    FAKE_ITEMBOX(
            "fake_itembox",
            Permission.USE_FAKEITEMBOX,
            "にせアイテムボックス",
            "アイテムボックスにそっくりですが、触れると減速します。"),
    THUNDER(
            "thunder",
            Permission.USE_THUNDER,
            "サンダー",
            "ライバルのプレイヤーが一定時間遅くなります。"),
    TERESA(
            "teresa",
            Permission.USE_TERESA,
            "テレサ",
            "一定時間透明になり、ライバルの攻撃を受けなくなります。さらにランダムでライバルからアイテムを奪います。"),
    GESSO(
            "gesso",
            Permission.USE_GESSO,
            "ゲッソー",
            "自分より上位のプレイヤーの画面に墨を吐いて画面を見難くします。"),
    KILLER(
            "killer",
            Permission.USE_KILLER,
            "キラー",
            "");

    /** アイテムコマンドキー */
    private String commandKey;

    /** コンフィグキー */
    private String configKey;

    /** パーミッションノード */
    private Permission permission;

    /**
     * アイテム階級
     * -1 : ドロップしない、アイテムコマンドからの呼び出し不可
     * 0  : ドロップしない、アイテムコマンドからの呼び出し可
     * 1~4: ドロップする、アイテムコマンドからの呼び出し可
     */
    private int tier;

    /** アイテムマテリアル */
    private Material material;

    /** アイテムマテリアルデータ */
    private byte materialData;

    /**
     * 表示ブロックマテリアル
     * キラーのみ使用
     */
    private Material displayBlockMaterial;

    /**
     * 表示ブロックマテリアルデータ
     * キラーのみ使用
     */
    private byte displayBlockMaterialData;

    /** 最大スタック数 */
    private int maxstack;

    /** 歩行速度 */
    private float walkSpeed;

    /** 被弾ダメージ */
    private int hitDamage;

    /** 移動中の被弾ダメージ */
    private int movingDamage;

    /** ポーションエフェクトレベル */
    private int effectLevel;

    /** ポーションエフェクト秒数 */
    private int effectSecond;

    /** 複数ドロップ時の階級 */
    private int tierMultiple;

    /** 複数ドロップ時のドロップ数 */
    private int dropAmount;

    /** アイテム名 */
    private String displayName;

    /** アイテムロア */
    private String lore;

    //〓 main 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * コンストラクタ
     * enumの静的データを格納する
     * 動的データ（コンフィグ）の読み込みはstatic.reload()メソッドで明示的に行う
     * static.reload()はConfigManager.reload()から実行される
     * @param permission パーミッションノード
     * @param configKey コンフィグキー
     * @param displayName アイテム名
     * @param lore アイテムロア
     */
    private ItemEnum(String configKey, Permission permission, String displayName, String lore) {
        setCommandKey(name());
        setConfigKey(configKey);
        setPermission(permission);
        setDisplayName(displayName);
        setLore(lore);
    }

    /** 全要素のメンバ変数を再取得する */
    public static void reload(){
        for (ItemEnum enumItem : values()) {
            enumItem.loadLocalConfig();
        }
    }

    /** ローカルコンフィグファイルの設定データを格納する */
    public void loadLocalConfig() {
        ConfigManager config = ConfigManager.ITEM_ENUM;
        String nodePrefix = getConfigKey() + ".";

        setTier(config.getInteger(nodePrefix + "tier"));
        setMaterial(config.getMaterial(nodePrefix + "material"));
        setMaterialData(config.getByte(nodePrefix + "material_data"));
        setDisplayBlockMaterial(config.getMaterial(nodePrefix + "display_block_material"));
        setDisplayBlockMaterialData(config.getByte(nodePrefix + "display_block_material_data"));
        setMaxstack(config.getInteger(nodePrefix + "max_stack_size"));
        setWalkSpeed(config.getFloat(nodePrefix + "walk_speed"));
        setHitDamage(config.getInteger(nodePrefix + "hit_damage"));
        setMovingDamage(config.getInteger(nodePrefix + "moving_damage"));
        setEffectLevel(config.getInteger(nodePrefix + "effect_level"));
        setEffectSecond(config.getInteger(nodePrefix + "effect_second"));
        setTierMultiple(config.getInteger(nodePrefix + "multiple.tier"));
        setDropAmount(config.getInteger(nodePrefix + "multiple.drop_amount"));
    }

    //〓 getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @return commandKey アイテムコマンドキー */
    public String getCommandKey() {
        return commandKey;
    }

    /** @return configKey コンフィグキー */
    public String getConfigKey() {
        return configKey;
    }

    /** @return permission パーミッションノード */
    public Permission getPermission() {
        return permission;
    }

    /** @return tier アイテム階級 */
    public int getTier() {
        return tier;
    }

    /** @return material アイテムマテリアル */
    public Material getMaterial() {
        return material;
    }

    /** @return materialData アイテムマテリアルデータ */
    public byte getMaterialData() {
        return materialData;
    }

    /** @return displayBlockMaterial 表示ブロックマテリアル */
    public Material getDisplayBlockMaterial() {
        return displayBlockMaterial;
    }

    /** @return displayBlockMaterialData 表示ブロックマテリアルデータ */
    public byte getDisplayBlockMaterialData() {
        return displayBlockMaterialData;
    }

    /** @return maxstack 最大スタック数 */
    public int getMaxstack() {
        return maxstack;
    }

    /** @return walkSpeed 歩行速度 */
    public float getWalkSpeed() {
        return walkSpeed;
    }

    /** @return hitDamage 被弾ダメージ */
    public int getHitDamage() {
        return hitDamage;
    }

    /** @return movingDamage 移動中の被弾ダメージ */
    public int getMovingDamage() {
        return movingDamage;
    }

    /** @return effectLevel ポーションエフェクトレベル */
    public int getEffectLevel() {
        return effectLevel;
    }

    /** @return effectSecond ポーションエフェクト秒数 */
    public int getEffectSecond() {
        return effectSecond;
    }

    /** @return tierMultiple 複数ドロップ時の階級 */
    public int getTierMultiple() {
        return tierMultiple;
    }

    /** @return dropAmount 複数ドロップ時のドロップ数 */
    public int getDropAmount() {
        return dropAmount;
    }

    /** @return displayName アイテム名 */
    public String getDisplayName() {
        return displayName;
    }

    /** @return lore アイテムロア */
    public String getLore() {
        return lore;
    }

    //〓 setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @param commandKey セットするアイテムコマンドキー */
    public void setCommandKey(String commandKey) {
        this.commandKey = commandKey;
    }

    /** @param configKey セットするコンフィグキー */
    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    /** @param permission セットするパーミッションノード */
    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    /** @param tier セットするアイテム階級 */
    public void setTier(int tier) {
        this.tier = tier;
    }

    /** @param material セットするアイテムマテリアル */
    public void setMaterial(Material material) {
        this.material = material;
    }

    /** @param materialData セットするアイテムマテリアルデータ */
    public void setMaterialData(byte materialData) {
        this.materialData = materialData;
    }

    /** @param displayBlockMaterial セットする表示ブロックマテリアル */
    public void setDisplayBlockMaterial(Material displayBlockMaterial) {
        this.displayBlockMaterial = displayBlockMaterial;
    }

    /** @param displayBlockMaterialData セットする表示ブロックマテリアルデータ */
    public void setDisplayBlockMaterialData(byte displayBlockMaterialData) {
        this.displayBlockMaterialData = displayBlockMaterialData;
    }

    /** @param maxstack セットする最大スタック数 */
    public void setMaxstack(int maxstack) {
        this.maxstack = maxstack;
    }

    /** @param walkSpeed セットする歩行速度 */
    public void setWalkSpeed(float walkSpeed) {
        this.walkSpeed = walkSpeed;
    }

    /** @param hitDamage セットする被弾ダメージ */
    public void setHitDamage(int hitDamage) {
        this.hitDamage = hitDamage;
    }

    /** @param movingDamage セットする移動中の被弾ダメージ */
    public void setMovingDamage(int movingDamage) {
        this.movingDamage = movingDamage;
    }

    /** @param effectLevel セットするポーションエフェクトレベル */
    public void setEffectLevel(int effectLevel) {
        this.effectLevel = effectLevel;
    }

    /** @param effectSecond セットするポーションエフェクト秒数 */
    public void setEffectSecond(int effectSecond) {
        this.effectSecond = effectSecond;
    }

    /** @param tierMultiple セットする複数ドロップ時の階級 */
    public void setTierMultiple(int tierMultiple) {
        this.tierMultiple = tierMultiple;
    }

    /** @param dropAmount セットする複数ドロップ時のドロップ数 */
    public void setDropAmount(int dropAmount) {
        this.dropAmount = dropAmount;
    }

    /**
     * @param displayName セットするアイテム名
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /** @param lore セットするアイテムロア */
    public void setLore(String lore) {
        this.lore = lore;
    }

    //〓 do 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @return 専用アイテムのItemStack */
    public ItemStack getItem() {
        ItemStack i = new ItemStack(getMaterial(), 1, (short) 0, getMaterialData());
        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + getDisplayName());
        meta.setLore(Arrays.asList(getLore()));
        i.setItemMeta(meta);
        return i;
    }

    /**
     * マテリアル、マテリアルデータ、アイテム名、アイテムロアを比較し一致しているかどうかを返す<br>
     * ChatColorは考慮しない<br>
     * チェックポイントツールの場合はロアが動的なため、ロアの比較を行わず
     * サーキット一覧にロアが含まれているかどうかで判別する
     * @param itemStack 比較するItemStack
     * @return 比較結果
     */
    public Boolean isSimilar(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }

        if (itemStack.getType().equals(getMaterial())) {
            if (itemStack.getData().getData() == getMaterialData()) {
                ItemMeta itemMeta = itemStack.getItemMeta();
                ItemMeta targetMeta = getItem().getItemMeta();
                if (itemMeta.hasDisplayName() && targetMeta.hasDisplayName()) {
                    String itemName = ChatColor.stripColor(itemMeta.getDisplayName());
                    String targetName = ChatColor.stripColor(getDisplayName());

                    if (itemMeta.hasLore() && targetMeta.hasLore()) {
                        String itemLore = ChatColor.stripColor(itemMeta.getLore().get(0));
                        String targetLore = ChatColor.stripColor(getLore());

                        //チェックポイントツールかどうか
                        if (itemName.equalsIgnoreCase(
                                ChatColor.stripColor(CHECKPOINT_TOOL.getDisplayName()))) {
                            if (CircuitConfig.getCircuitData(itemLore) != null) {
                                return true;
                            }
                        } else {
                            if (itemLore.equalsIgnoreCase(targetLore)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    //〓 static 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * @param itemStack 調べるItemStack
     * @return 引数itemStackが当プラグイン専用のアイテムかどうか
     */
    public static Boolean isKeyItem(ItemStack itemStack) {
        //アイテムの比較
        for (ItemEnum item : values()) {
            if (item.isSimilar(itemStack)) {
                return true;
            }
        }

        return false;
    }

    /**
     * アイテムコマンドから取得可能なアイテム一覧Stringを返す
     * Tierが-1以下のアイテムは含まれない
     * @return アイテム一覧String
     */
    public static String getItemListString() {
        String listString = null;
        for (ItemEnum itemEnum : values()) {
            if (-1 < itemEnum.getTier()) {
                if (listString == null) {
                    listString = itemEnum.getCommandKey().toLowerCase();
                } else {
                    listString += ", " + itemEnum.getCommandKey().toLowerCase();
                }
            }
        }
        return listString;
    }

    /**
     * commandKeyと一致するenum要素名のenumを返す
     * 大文字小文字は考慮しない
     * Tierが-1以下のアイテムは含まれない
     * @param commandKey enum要素名
     * @return ItemType
      */
    public static ItemEnum getItemFromCommandKey(String commandKey) {
        for (ItemEnum item : values()) {
            if (-1 < item.getTier()) {
                String itemName = item.name();
                if (itemName.equalsIgnoreCase(commandKey)) {
                    return item;
                }
            }
        }
        return null;
    }

    /**
     * 引数circuitnameのサーキットを編集できるチェックポイントツールを返す
     * @param player アイテムを付与するプレイヤー
     * @param circuitDataName チェックポイントを編集するサーキット名
     */
    public static void addCheckPointTool(Player player, String circuitDataName) {
        CircuitData circuitData = CircuitConfig.getCircuitData(circuitDataName);
        if (circuitData == null) {
            MessageEnum.invalidCircuit.sendConvertedMessage(player, circuitDataName);
            return;
        }

        ItemEnum itemType = ItemEnum.CHECKPOINT_TOOL;
        ItemStack itemStack = new ItemStack(itemType.getMaterial(), 1, (short) 0, itemType.getMaterialData());
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + itemType.getDisplayName());
        meta.setLore(Arrays.asList(circuitDataName));
        itemStack.setItemMeta(meta);

        player.getInventory().addItem(itemStack);
        player.updateInventory();

        MessageEnum.cmdCircuitEdit.sendConvertedMessage(player, circuitDataName);
    }

    /**
     * tier-1 ～ tierの階級のアイテムからランダム抽出した1アイテムをplayerに付与する
     * playerがアイテム使用パーミッションを所有していないアイテムは抽選に含まれない
     * @param player アイテムを付与するプレイヤー
     * @param tier アイテム階級
     */
    public static void addRandomItemFromTier(Player player, int tier) {
        UUID uuid = player.getUniqueId();
        HashMap<ItemStack, ItemEnum> itemList = getItemFromTier(tier);

        //パーミッションを所有していないアイテムを除外
        Iterator<ItemEnum> iterator = itemList.values().iterator();
        ItemEnum itemEnum;
        while (iterator.hasNext()) {
            itemEnum = iterator.next();

            if (!Permission.hasPermission(player, itemEnum.getPermission(), true)) {
                iterator.remove();
            }
        }

        //ランダム抽出
        if (itemList.size() != 0) {
            //HashMapをItemStackの配列に変換し、ランダムなインデックスから1要素を抽出
            ItemStack[] itemStackArray = itemList.keySet().toArray(new ItemStack[itemList.size()]);
            ItemStack itemStack = itemStackArray[new Random().nextInt(itemList.size())];

            //アイテムの配布
            addItem(player, itemStack);
            MessageEnum.raceInteractItemBox.sendConvertedMessage(player
                    , new Object[] { RaceManager.getCircuit(uuid), itemStack });
            player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1.0F, 2.0F);
        } else {
            //アイテム利用パーミッションがない等の理由から取得できるアイテムが無かった場合
            MessageEnum.raceInteractItemBoxFailed.sendConvertedMessage(player, RaceManager.getCircuit(uuid));
        }
    }

    /**
     * 引数playerに引数itemを付与する
     *
     * @param player アイテムを付与するプレイヤー
     * @param item 付与するアイテムのItemStack
     */
    public static void addItem(Player player, ItemStack item) {
        Inventory inv = player.getInventory();
        inv.addItem(item);

        adjustInventoryFromMaxstackSize(player);
        removeUnuseslotKeyItems(player);
        player.updateInventory();
    }

    /**
     * 引数playerのインベントリに含まれるキーアイテムを全て削除する
     * @param player インベントリを整理するプレイヤー
     */
    public static void removeAllKeyItems(Player player) {
        PlayerInventory inv = player.getInventory();

        for (int i = 0; i < 36; i++) {
            if (isKeyItem(inv.getItem(i))) {
                inv.setItem(i, null);
            }
        }

        if (inv.getHelmet() != null) {
            if (isKeyItem(inv.getHelmet())) {
                inv.setHelmet(new ItemStack(Material.AIR));
            }
        }

        if (inv.getChestplate() != null) {
            if (isKeyItem(inv.getChestplate())) {
                inv.setChestplate(new ItemStack(Material.AIR));
            }
        }

        if (inv.getLeggings() != null) {
            if (isKeyItem(inv.getLeggings())) {
                inv.setLeggings(new ItemStack(Material.AIR));
            }
        }

        if (inv.getBoots() != null) {
            if (isKeyItem(inv.getBoots())) {
                inv.setBoots(new ItemStack(Material.AIR));
            }
        }

        player.updateInventory();
    }

    //〓 private static 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * tier-1 ～ tierの階級のアイテムをHashMap<ItemStack, ItemEnum>で返す
     * ItemEnumにはMultipleDropの設定があり、1Enum=1ItemStackではなく、
     * MultipleTierの設定が1以上の場合は1Enum=2ItemStackの場合がある
     * このためItemEnumのListを返すだけではMultipleDrop設定が有効な場合に
     * 対応できないため、単数ItemStack:EnumItemが1対、複数ItemStack:EnumItemが1対
     * といったようにHashMapに値を格納する
     * ItemStackのListを返すことでも対応可能だが、ItemEnumからはアイテム使用パーミッション
     * が取得可能なため極力セットで扱いたい
     * @param tier アイテム階級
     * @return HashMap<ItemStack, ItemEnum>
     */
    private static HashMap<ItemStack, ItemEnum> getItemFromTier(int tier) {
        HashMap<ItemStack, ItemEnum> itemList = new HashMap<ItemStack, ItemEnum>();
        for (ItemEnum item : ItemEnum.values()) {
            //階級チェック
            if (0 < item.getTier()) {
                if (item.getTier() <= tier) {
                    if (tier - 1 <= item.getTier()) {
                        itemList.put(item.getItem(), item);
                    }
                }
            }

            //複数ドロップ時の階級チェック
            if (0 < item.getTierMultiple()) {
                if (item.getTierMultiple() <= tier) {
                    if (tier - 1 <= item.getTierMultiple()) {
                        ItemStack itemStack = item.getItem();
                        itemStack.setAmount(item.getDropAmount());
                        itemList.put(itemStack, item);
                    }
                }
            }
        }
        return itemList;
    }

    /**
     * 引数playerの選択キャラクターのアイテムスロット数に基づいてインベントリを整理する
     * 利用できるアイテムスロット以外のスロットに入っているキーアイテムを削除する
     * @param player インベントリを整理するプレイヤー
     */
    private static void removeUnuseslotKeyItems(Player player) {
        PlayerInventory inv = player.getInventory();
        int i = (Integer) ConfigEnum.ITEM_SLOT.getValue();
        i += RaceManager.getRace(player).getCharacter() == null ? 0 : RaceManager.getRace(player).getCharacter()
                .getAdjustMaxSlotSize();
        for (int j = i; j < 36; j++) {
            if (isKeyItem(inv.getItem(j)))
                inv.setItem(j, null);
        }
        player.updateInventory();
    }

    /**
     * インベントリ内のキーアイテムを、最大スタック数に基づいて整理する
     * 最大スタック数を超過しているアイテムの個数を他スロットへ分配する
     * @param player インベントリを整理するプレイヤー
     */
    private static void adjustInventoryFromMaxstackSize(Player player) {
        Inventory inv = player.getInventory();
        ArrayList<ItemStack> over = new ArrayList<ItemStack>();

        ItemStack temp;
        ItemStack flow;
        int maxstacksize;

        for (int i = 0; i < 36; i++) {
            if (inv.getItem(i) == null)
                continue;

            temp = inv.getItem(i);
            maxstacksize = getMaxstackSize(temp);
            maxstacksize += RaceManager.getRace(player).getCharacter() == null ? 0 : RaceManager.getRace(player).getCharacter()
                    .getAdjustMaxStackSize();

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
                    int tempmaxstacksize = getMaxstackSize(inv.getItem(j));
                    tempmaxstacksize += RaceManager.getRace(player).getCharacter() == null ? 0 : RaceManager.getRace(player)
                            .getCharacter().getAdjustMaxStackSize();
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

    /**
     * 引数itemStackの最大スタック数を返す
     * itemStackがキーアイテムの場合はキーアイテムの最大スタック数を返す
     * @param itemStack
     * @return
     */
    private static int getMaxstackSize(ItemStack itemStack) {
        int maxstacksize = itemStack.getMaxStackSize();
        for (ItemEnum item : values()) {
            if (item.isSimilar(itemStack))
                maxstacksize = item.getMaxstack();
        }
        return maxstacksize;
    }
}
