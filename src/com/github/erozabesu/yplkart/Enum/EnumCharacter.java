package com.github.erozabesu.yplkart.Enum;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.erozabesu.yplkart.Data.Message;
import com.github.erozabesu.yplkart.Data.Settings;
import com.github.erozabesu.yplkart.Utils.Util;

public enum EnumCharacter {
    Human("Human", "EntityHuman", Settings.HumanClassSetting, Material.SUGAR, Sound.BURP, 1.0F, 1.0F),
    Creeper("Creeper", "EntityCreeper", Settings.CreeperClassSetting, Material.MELON_SEEDS, Sound.CREEPER_HISS, 1.0F,
            1.0F),
    Zombie("Zombie", "EntityZombie", Settings.ZombieClassSetting, Material.PUMPKIN_SEEDS, Sound.ZOMBIE_DEATH, 1.0F,
            1.0F),
    Skeleton("Skeleton", "EntitySkeleton", Settings.SkeletonClassSetting, Material.BLAZE_ROD, Sound.SKELETON_DEATH,
            1.0F, 1.0F),
    Spider("Spider", "EntitySpider", Settings.SpiderClassSetting, Material.GOLD_NUGGET, Sound.SPIDER_IDLE, 1.0F, 1.0F),
    Enderman("Enderman", "EntityEnderman", Settings.EndermanClassSetting, Material.NETHER_STALK, Sound.ENDERMAN_DEATH,
            1.0F, 1.0F),
    Witch("Witch", "EntityWitch", Settings.WitchClassSetting, Material.NETHER_BRICK_ITEM, Sound.VILLAGER_DEATH, 1.0F,
            4.0F),
    Pig("Pig", "EntityPig", Settings.PigClassSetting, Material.QUARTZ, Sound.PIG_DEATH, 1.0F, 1.0F),
    Squid("Squid", "EntitySquid", Settings.SquidClassSetting, Material.PRISMARINE_SHARD, Sound.SLIME_WALK, 1.0F, 1.0F),
    Villager("Villager", "EntityVillager", Settings.VillagerClassSetting, Material.PRISMARINE_CRYSTALS,
            Sound.VILLAGER_DEATH, 1.0F, 1.0F);

    private double[] classsetting;
    private String name;
    private String craftclass;
    private Material material;
    private Sound sound;
    private float volume;
    private float pitch;

    public static String human = "human";
    public static String creeper = "creeper";
    public static String zombie = "zombie";
    public static String skeleton = "skeleton";
    public static String spider = "spider";
    public static String enderman = "enderman";
    public static String witch = "witch";
    public static String pig = "pig";
    public static String squid = "squid";
    public static String villager = "villager";
    public static String[] classlist = { human, creeper, zombie, skeleton, spider, enderman, witch, pig, squid,
            villager };

    private EnumCharacter(String name, String craftclass, double[] classsetting, Material material, Sound sound,
            float volume, float pitch) {
        this.classsetting = classsetting;
        this.name = name;
        this.craftclass = craftclass;
        this.material = material;
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public static String getCharacterList() {
        String characterlist = null;
        for (String charactername : EnumCharacter.classlist) {
            if (characterlist == null)
                characterlist = charactername;
            else
                characterlist += ", " + charactername;
        }
        return characterlist;
    }

    //Java6だとswitchにstringは使えない
    public static EnumCharacter getClassfromString(String value) {
        if (value.equalsIgnoreCase(human))
            return EnumCharacter.Human;
        if (value.equalsIgnoreCase(creeper))
            return EnumCharacter.Creeper;
        if (value.equalsIgnoreCase(zombie))
            return EnumCharacter.Zombie;
        if (value.equalsIgnoreCase(skeleton))
            return EnumCharacter.Skeleton;
        if (value.equalsIgnoreCase(spider))
            return EnumCharacter.Spider;
        if (value.equalsIgnoreCase(enderman))
            return EnumCharacter.Enderman;
        if (value.equalsIgnoreCase(witch))
            return EnumCharacter.Witch;
        if (value.equalsIgnoreCase(pig))
            return EnumCharacter.Pig;
        if (value.equalsIgnoreCase(squid))
            return EnumCharacter.Squid;
        if (value.equalsIgnoreCase(villager))
            return EnumCharacter.Villager;
        return null;
    }

    public static EnumCharacter getRandomCharacter() {
        int ram = EnumCharacter.values().length;
        ram = new Random().nextInt(ram);

        int count = 0;
        for (EnumCharacter character : EnumCharacter.values()) {
            if (count == ram)
                return character;
            count++;
        }
        return null;
    }

    public static void playCharacterVoice(Player p, EnumCharacter character) {
        p.playSound(p.getLocation(), character.getCharacterVoice(), character.getCharacterVoiceVolume(),
                character.getCharacterVoicePitch());
    }

    public Material getType() {
        return this.material;
    }

    public String getParameter() {
        return Message.tableCharacterParameter.getMessage(new String[] {
                String.valueOf(getMaxHealth()),
                String.valueOf(getWalkSpeed()),
                String.valueOf(getItemAdjustMaxSlotSize() + Settings.ItemSlot),
                Util.convertSignNumber(getItemAdjustMaxStackSize()),
                Util.convertSignNumber(getItemAdjustAttackDamage()),
                String.valueOf(getDeathPenaltyAntiReskillSecond()),
                String.valueOf(getDeathPenaltySecond()),
                String.valueOf(getDeathPenaltyWalkSpeed()),
                Util.convertSignNumber(getItemAdjustPositiveEffectLevel()),
                Util.convertSignNumber(getItemAdjustPositiveEffectSecond()),
                Util.convertSignNumberR(getItemAdjustNegativeEffectLevel()),
                Util.convertSignNumberR(getItemAdjustNegativeEffectSecond()) });
    }

    public ItemStack getMenuItem() {
        ItemStack item = new ItemStack(getType());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(getName());
        meta.setLore(Message.replaceLine(Message.replaceChatColor(getParameter())));

        item.setItemMeta(meta);

        return item;
    }

    public void reload(double[] classsetting) {
        this.classsetting = classsetting;
    }

    public String getName() {
        return this.name;
    }

    public String getCraftClassName() {
        return this.craftclass;
    }

    public Sound getCharacterVoice() {
        return this.sound;
    }

    public float getCharacterVoiceVolume() {
        return this.volume;
    }

    public float getCharacterVoicePitch() {
        return this.pitch;
    }

    public int getItemAdjustMaxSlotSize() {
        //アイテムスロットの上限は9
        if (9 < classsetting[0] + Settings.ItemSlot)
            return 9;
        return (int) classsetting[0];
    }

    public int getItemAdjustMaxStackSize() {
        return (int) classsetting[1];
    }

    public int getItemAdjustPositiveEffectSecond() {
        return (int) classsetting[2];
    }

    public int getItemAdjustPositiveEffectLevel() {
        return (int) classsetting[3];
    }

    public int getItemAdjustNegativeEffectSecond() {
        return (int) classsetting[4];
    }

    public int getItemAdjustNegativeEffectLevel() {
        return (int) classsetting[5];
    }

    public int getItemAdjustAttackDamage() {
        return (int) classsetting[6];
    }

    public double getMaxHealth() {
        return classsetting[7];
    }

    public float getWalkSpeed() {
        return (float) classsetting[8];
    }

    public int getDeathPenaltyAntiReskillSecond() {
        return (int) classsetting[9];
    }

    public float getDeathPenaltyWalkSpeed() {
        return (float) classsetting[10];
    }

    public int getDeathPenaltySecond() {
        return (int) classsetting[11];
    }
}
