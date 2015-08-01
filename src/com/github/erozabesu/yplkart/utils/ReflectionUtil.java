package com.github.erozabesu.yplkart.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import com.github.erozabesu.yplkart.object.Kart;
import com.github.erozabesu.yplkart.object.KartType;

public class ReflectionUtil {

    //〓 Package Name 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static String bukkitVersion =
            Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    public static String nmsPackage = "net.minecraft.server." + getBukkitVersion();
    public static String craftPackage = "org.bukkit.craftbukkit." + getBukkitVersion();
    public static String yplkartPackage =
            "com.github.erozabesu.yplkart.override." + getBukkitVersion();

    //〓 Nms Class 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static Class<?> nmsWorld = getNMSClass("World");
    public static Class<?> nmsBlock = getNMSClass("Block");
    public static Class<?> nmsMaterial = getNMSClass("Material");
    public static Class<?> nmsEntity = getNMSClass("Entity");
    public static Class<?> nmsEntityHuman = getNMSClass("EntityHuman");
    public static Class<?> nmsEntityLiving = getNMSClass("EntityLiving");
    public static Class<?> nmsItemStack = getNMSClass("ItemStack");
    public static Class<?> nmsPacket = getNMSClass("Packet");
    public static Class<?> nmsWatchableObject = getNMSClass("DataWatcher$WatchableObject");
    public static Class<?> nmsPlayerConnection = getNMSClass("PlayerConnection");
    public static Class<?> nmsIChatBaseComponent = getNMSClass("IChatBaseComponent");
    public static Class<?> nmsChatSerializer = null;
    public static Class<?> nmsEnumTitleAction = null;
    public static Class<?> nmsEnumClientCommand = null;
    public static Class<?> nmsPacketPlayInSteerVehicle = getNMSClass("PacketPlayInSteerVehicle");
    public static Class<?> nmsPacketPlayInUseEntity = getNMSClass("PacketPlayInUseEntity");
    public static Class<?> nmsPacketPlayOutAttachEntity = getNMSClass("PacketPlayOutAttachEntity");
    public static Class<?> nmsPacketPlayOutEntityDestroy = getNMSClass("PacketPlayOutEntityDestroy");
    public static Class<?> nmsPacketPlayOutEntityEquipment = getNMSClass("PacketPlayOutEntityEquipment");
    public static Class<?> nmsPacketPlayOutEntityLook = null;
    public static Class<?> nmsPacketPlayOutEntityMetadata = getNMSClass("PacketPlayOutEntityMetadata");
    public static Class<?> nmsPacketPlayOutEntityTeleport = getNMSClass("PacketPlayOutEntityTeleport");
    public static Class<?> nmsPacketPlayOutNamedEntitySpawn = getNMSClass("PacketPlayOutNamedEntitySpawn");
    public static Class<?> nmsPacketPlayOutRelEntityMoveLook = null;
    public static Class<?> nmsPacketPlayOutSpawnEntity = getNMSClass("PacketPlayOutSpawnEntity");
    public static Class<?> nmsPacketPlayOutSpawnEntityLiving = getNMSClass("PacketPlayOutSpawnEntityLiving");
    public static Class<?> nmsPacketPlayOutTitle = getNMSClass("PacketPlayOutTitle");
    public static Class<?> nmsPacketPlayInClientCommand = getNMSClass("PacketPlayInClientCommand");

    static {
        if (getBukkitVersion().equalsIgnoreCase("v1_8_R1")) {
            nmsChatSerializer = getNMSClass("ChatSerializer");
            nmsEnumTitleAction = getNMSClass("EnumTitleAction");
            nmsEnumClientCommand = getNMSClass("EnumClientCommand");
            nmsPacketPlayOutEntityLook = getNMSClass("PacketPlayOutEntityLook");
            nmsPacketPlayOutRelEntityMoveLook = getNMSClass("PacketPlayOutRelEntityMoveLook");
        } else {
            nmsChatSerializer = getNMSClass("IChatBaseComponent$ChatSerializer");
            nmsEnumTitleAction = getNMSClass("PacketPlayOutTitle$EnumTitleAction");
            nmsEnumClientCommand = getNMSClass("PacketPlayInClientCommand$EnumClientCommand");
            nmsPacketPlayOutEntityLook = getNMSClass("PacketPlayOutEntity$PacketPlayOutEntityLook");
            nmsPacketPlayOutRelEntityMoveLook = getNMSClass("PacketPlayOutEntity$PacketPlayOutRelEntityMoveLook");
        }
    }

    //〓 Nms Constructor 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static Constructor<?> constructor_nmsPacketPlayOutAttachEntity;
    public static Constructor<?> constructor_nmsPacketPlayOutEntityDestroy;
    public static Constructor<?> constructor_nmsPacketPlayOutEntityEquipment;
    public static Constructor<?> constructor_nmsPacketPlayOutEntityLook;
    public static Constructor<?> constructor_nmsPacketPlayOutEntityTeleport;
    public static Constructor<?> constructor_nmsPacketPlayOutNamedEntitySpawn;
    public static Constructor<?> constructor_nmsPacketPlayOutRelEntityMoveLook;
    public static Constructor<?> constructor_nmsPacketPlayOutSpawnEntity;
    public static Constructor<?> constructor_nmsPacketPlayOutSpawnEntityLiving;
    public static Constructor<?> constructor_nmsPacketPlayOutTitle;
    public static Constructor<?> constructor_nmsPacketPlayOutTitle_Length;
    public static Constructor<?> constructor_nmsPacketPlayInClientCommand;

    static {
        try {
            constructor_nmsPacketPlayOutAttachEntity = nmsPacketPlayOutAttachEntity.getConstructor(int.class, nmsEntity, nmsEntity);
            constructor_nmsPacketPlayOutEntityDestroy = nmsPacketPlayOutEntityDestroy.getConstructor(int[].class);
            constructor_nmsPacketPlayOutEntityEquipment = nmsPacketPlayOutEntityEquipment.getConstructor(int.class, int.class, nmsItemStack);
            constructor_nmsPacketPlayOutEntityLook = nmsPacketPlayOutEntityLook.getConstructor(int.class, byte.class, byte.class, boolean.class);
            constructor_nmsPacketPlayOutEntityTeleport = nmsPacketPlayOutEntityTeleport.getConstructor(int.class, int.class, int.class, int.class, byte.class, byte.class, boolean.class);
            constructor_nmsPacketPlayOutRelEntityMoveLook = nmsPacketPlayOutRelEntityMoveLook.getConstructor(int.class, byte.class, byte.class, byte.class, byte.class, byte.class, boolean.class);
            constructor_nmsPacketPlayOutNamedEntitySpawn = nmsPacketPlayOutNamedEntitySpawn.getConstructor(nmsEntityHuman);
            constructor_nmsPacketPlayOutSpawnEntity = nmsPacketPlayOutSpawnEntity.getConstructor(nmsEntity, int.class, int.class);
            constructor_nmsPacketPlayOutSpawnEntityLiving = nmsPacketPlayOutSpawnEntityLiving.getConstructor(nmsEntityLiving);
            constructor_nmsPacketPlayOutTitle = nmsPacketPlayOutTitle.getConstructor(nmsEnumTitleAction, nmsIChatBaseComponent);
            constructor_nmsPacketPlayOutTitle_Length = nmsPacketPlayOutTitle.getConstructor(int.class, int.class, int.class);
            constructor_nmsPacketPlayInClientCommand = nmsPacketPlayInClientCommand.getConstructor(nmsEnumClientCommand);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    //〓 Nms Constructor List 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** EntityXxxxxクラス毎のコンストラクタ */
    public static HashMap<String, Constructor<?>> nmsEntity_Constructor =
            new HashMap<String, Constructor<?>>();

    //〓 Nms Method 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static Method nmsWorld_addEntity;
    public static Method nmsBlock_getMaterial;
    public static Method nmsMaterial_isSolid;
    public static Method nmsPlayerConnection_sendPacket;
    public static Method nmsEntity_getBukkitEntity;
    public static Method nmsEntity_setEntityID;
    public static Method nmsEntity_setLocation;
    public static Method nmsEntity_setCustomName;
    public static Method nmsEntity_setCustomNameVisible;
    public static Method nmsWatchableObject_getIndex;

    public static Method static_nmsChatSerializer_buildTitle;
    public static Method static_nmsBlock_getById;

    static {
        try {
            nmsWorld_addEntity = nmsWorld.getMethod("addEntity", nmsEntity);
            nmsBlock_getMaterial = nmsBlock.getMethod("getMaterial");
            nmsMaterial_isSolid = nmsMaterial.getMethod("isSolid");
            nmsPlayerConnection_sendPacket = nmsPlayerConnection.getMethod("sendPacket", nmsPacket);
            nmsEntity_getBukkitEntity = nmsEntity.getMethod("getBukkitEntity");
            nmsEntity_setEntityID = nmsEntity.getMethod("d", int.class);
            nmsEntity_setLocation = nmsEntity.getMethod("setLocation", double.class, double.class, double.class, float.class, float.class);
            nmsEntity_setCustomName = nmsEntity.getMethod("setCustomName", String.class);
            nmsEntity_setCustomNameVisible = nmsEntity.getMethod("setCustomNameVisible", boolean.class);
            nmsWatchableObject_getIndex = nmsWatchableObject.getMethod("a");

            static_nmsChatSerializer_buildTitle = nmsChatSerializer.getMethod("a", String.class);
            static_nmsBlock_getById = nmsBlock.getMethod("getById", int.class);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    //〓 Nms Object 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static Object enumTitleAction_PerformTitle = nmsEnumTitleAction.getEnumConstants()[0];
    public static Object enumTitleAction_PerformSubTitle = nmsEnumTitleAction.getEnumConstants()[1];
    public static Object enumClientCommand_PerformRespawn = nmsEnumClientCommand.getEnumConstants()[0];

    //〓 Nms Field 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static Field field_PacketPlayInSteerVehicle_isUnmount = getField(nmsPacketPlayInSteerVehicle, "d");
    public static Field field_PacketPlayOutEntityMetadata_EntityId = getField(nmsPacketPlayOutEntityMetadata, "a");
    public static Field field_PacketPlayOutEntityMetadata_WatchableObject = getField(nmsPacketPlayOutEntityMetadata, "b");
    public static Field field_PacketPlayOutNamedEntitySpawn_UUID = getField(nmsPacketPlayOutNamedEntitySpawn, "b");
    public static Field field_PacketPlayOutEntityEquipment_EntityId = getField(nmsPacketPlayOutEntityEquipment, "a");
    public static Field field_PacketPlayOutEntityEquipment_ItemSlot = getField(nmsPacketPlayOutEntityEquipment, "b");
    public static Field field_PacketPlayOutEntityEquipment_ItemStack = getField(nmsPacketPlayOutEntityEquipment, "c");
    public static Field field_PacketPlayOutEntityTeleport_EntityId = getField(nmsPacketPlayOutEntityTeleport, "a");
    public static Field field_PacketPlayOutEntityTeleport_LocationY = getField(nmsPacketPlayOutEntityTeleport, "c");
    public static Field field_PacketPlayOutEntityTeleport_LocationYaw = getField(nmsPacketPlayOutEntityTeleport, "e");
    public static Field field_PacketPlayOutSpawnEntity_EntityId = getField(nmsPacketPlayOutSpawnEntity, "a");
    public static Field field_PacketPlayOutSpawnEntity_LocationY = getField(nmsPacketPlayOutSpawnEntity, "c");
    public static Field field_PacketPlayOutSpawnEntity_LocationYaw = getField(nmsPacketPlayOutSpawnEntity, "i");

    //〓 Craft Class 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static Class<?> craftWorld = getCraftClass("CraftWorld");
    public static Class<?> craftItemStack = getCraftClass("inventory.CraftItemStack");

    //〓 Craft Method 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static Method craftWorld_getHandle;
    public static Method static_craftItemStack_asNMSCopy;

    static {
        try {
            craftWorld_getHandle = craftWorld.getMethod("getHandle");
            static_craftItemStack_asNMSCopy = craftItemStack.getMethod("asNMSCopy", ItemStack.class);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    //〓 Craft Method List 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** EntityXxxxxクラス毎のgetHandleメソッド */
    public static HashMap<String, Method> craftEntity_getHandle = new HashMap<String, Method>();

    //〓 Ypl Class 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static Class<?> yplCustomKart = getYPLKartClass("CustomArmorStand");

    //〓 Ypl Constructor 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static Constructor<?> constructor_yplCustomKart;

    static {
        try {
            constructor_yplCustomKart = yplCustomKart.getConstructor(nmsWorld, Kart.class, KartType.class, Location.class);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    //〓 getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static String getBukkitVersion() {
        return bukkitVersion;
    }

    public static String getNMSPackageName() {
        return nmsPackage;
    }

    public static String getCraftPackageName() {
        return craftPackage;
    }

    public static String getYPLKartPackageName() {
        return yplkartPackage;
    }

    public static Class<?> getNMSClass(String s) {
        try {
            return Class.forName(getNMSPackageName() + "." + s);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Class<?> getCraftClass(String s) {
        try {
            return Class.forName(getCraftPackageName() + "." + s);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Class<?> getYPLKartClass(String s) {
        try {
            return Class.forName(getYPLKartPackageName() + "." + s);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getCraftEntity(Entity entity) {
        try {
            Method getHandle = craftEntity_getHandle.get(entity.getClass().getSimpleName());
            if (getHandle == null) {
                getHandle = entity.getClass().getMethod("getHandle");
                craftEntity_getHandle.put(entity.getClass().getSimpleName(), getHandle);
            }

            return getHandle.invoke(entity);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Object getNewCraftEntityFromClass(World world, Class<?> nmsEntityClass) {
        try {
            Constructor<?> constructor = nmsEntity_Constructor.get(nmsEntityClass.getSimpleName());

            if (constructor == null) {
                constructor = nmsEntityClass.getConstructor(nmsWorld);
                nmsEntity_Constructor.put(nmsEntityClass.getSimpleName(), constructor);
            }

            return constructor.newInstance(getCraftWorld(world));
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Object getNewCraftEntityFromClassName(World world, String classname) {
        try {
            Constructor<?> constructor = nmsEntity_Constructor.get(classname);

            if (constructor == null) {
                constructor = getNMSClass(classname).getConstructor(nmsWorld);
                nmsEntity_Constructor.put(classname, constructor);
            }

            return constructor.newInstance(getCraftWorld(world));
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Object getCraftWorld(World w) {
        try {
            return craftWorld_getHandle.invoke(w);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getCraftItemStack(ItemStack item) {
        try {
            return static_craftItemStack_asNMSCopy.invoke(null, item);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    //〓 java reflection 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 引数instanceオブジェクトのクラスから引数fieldNameフィールドを取得し返す
     * private、publicを問わず、全てのスーパークラスを遡って取得する
     * @param instance フィールドを取得するオブジェクト
     * @param fieldName フィールド名
     * @return 取得したフィールド
     */
    public static Field getField(Object instance, String fieldName) {
        return getField(instance.getClass(), fieldName);
    }

    /**
     * 引数clazzクラスから引数fieldNameフィールドを取得し返す
     * private、publicを問わず、全てのスーパークラスを遡って取得する
     * @param clazz フィールドを取得するクラス
     * @param fieldName フィールド名
     * @return 取得したフィールド
     */
    public static Field getField(Class<?> clazz, String fieldName) {
        Field field = null;
        while (clazz != null) {
            try {
                field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                break;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }

        return field;
    }

    public static Object getFieldValue(Object instance, String fieldName){
        Field field = getField(instance, fieldName);
        if (field != null) {
            field.setAccessible(true);
            try {
                return field.get(instance);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static Object getFieldValue(Field field, Object instance){
        if (field != null) {
            field.setAccessible(true);
            try {
                return field.get(instance);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static void setFieldValue(Field field, Object instance, Object value) {
        try {
            field.set(instance, value);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
