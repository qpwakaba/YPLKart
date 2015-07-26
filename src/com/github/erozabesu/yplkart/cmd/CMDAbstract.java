package com.github.erozabesu.yplkart.cmd;

import org.bukkit.inventory.ItemStack;

import com.github.erozabesu.yplkart.Permission;

abstract class CMDAbstract extends CMD {
    public CMDAbstract() {
        super();
    }

    abstract void ka();

    abstract void circuit();

    abstract void display();

    abstract void menu();

    abstract void entry();

    abstract void exit();

    abstract void character();

    abstract void characterreset();

    abstract void ride();

    abstract void leave();

    abstract void ranking();

    abstract void reload();

    abstract void debug();

    abstract void additem(ItemStack item, Permission permission);
}
