package com.github.erozabesu.yplkart.Task;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.erozabesu.yplkart.Utils.PacketUtil;

public class SendBlinkingTitleTask extends BukkitRunnable{
	int life = 0;
	int maxlife = 0;
	Player p;
	String text;
	ChatColor color;

	public SendBlinkingTitleTask(Player p, int maxlife, String text, ChatColor color){
		this.p = p;
		this.maxlife = maxlife*20;
		this.text = text;
		this.color = color;

		PacketUtil.sendTitle(p, text, 10, 0, 10, color, false);
	}

	@Override
	public void run(){
		life++;

		if(maxlife < life){
			this.cancel();
			return;
		}

		if(life % 20 == 0){
			PacketUtil.sendTitle(p, text, 10, 0, 10, color, false);
		}
	}
}
